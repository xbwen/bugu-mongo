/*
 * Copyright (c) www.bugull.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bugull.mongo;

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.FieldException;
import com.bugull.mongo.utils.DataType;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.access.InternalDao;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The utility class for ODM(Object Document Mapping), mainly fetch lazy and cascade data.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public final class BuguMapper {
    
    private final static Logger logger = LogManager.getLogger(BuguMapper.class.getName());
    
    /**
     * Convert to JSON string.
     * @param obj
     * @return 
     */
    public static String toJsonString(Object obj){
        if(obj == null){
            return null;
        }
        BasicDBObject bdbo;
        if(obj instanceof DBObject){
            bdbo = (BasicDBObject)obj;
        }else{
            DBObject dbo = MapperUtil.toDBObject(obj, true);
            bdbo = (BasicDBObject)dbo;
        }
        return bdbo.toString();
    }
    
    /**
     * Fetch out the lazy @Property, @Embed, @EmbedList field of a list
     * @param list the list needs to operate on
     */
    public static void fetchLazy(List list){
        for(Object o : list){
            if(o != null){
                BuguEntity obj = (BuguEntity)o;
                BuguEntity newObj = (BuguEntity)DaoCache.getInstance().get(obj.getClass()).findOne(obj.getId());
                FieldUtil.copy(newObj, obj);
            }
        }
    }
    
    /**
     * Fetch out the lazy @Property, @Embed, @EmbedList field of an entity.
     * <p>The entity must be an element of a list.</p>
     * @param obj the entity needs to operate on
     */
    public static void fetchLazy(BuguEntity obj){
        BuguEntity newObj = (BuguEntity)DaoCache.getInstance().get(obj.getClass()).findOne(obj.getId());
        FieldUtil.copy(newObj, obj);
    }
    
    /**
     * Fetch out the cascade @Ref or @RefList entity.
     * @param obj the entity needs to operate on
     * @param names the fields' names
     */
    public static void fetchCascade(BuguEntity obj, String... names){
        if(obj != null){
            for(String name : names){
                String remainder = null;
                int index = name.indexOf(".");
                if(index > 0){
                    remainder = name.substring(index+1);
                    name = name.substring(0, index);
                }
                fetchOneLevel(obj, name);
                if(remainder != null){
                    fetchRemainder(obj, name, remainder);
                }
            }
        }
    }
    
    /**
     * Fetch out the cascade @Ref or @RefList entity.
     * @param list the list needs to operate on
     * @param names the fields' names
     */
    public static void fetchCascade(List list, String... names){
        for(Object o : list){
            if(o != null){
                BuguEntity obj = (BuguEntity)o;
                fetchCascade(obj, names);
            }
        }
    }
    
    private static void fetchOneLevel(BuguEntity obj, String fieldName){
        Field field = null;
        try{
            field = FieldsCache.getInstance().getField(obj.getClass(), fieldName);
        }catch(FieldException ex){
            logger.error(ex.getMessage(), ex);
        }
        if(field.getAnnotation(Ref.class) != null){
            fetchRef(obj, field);
        }else if(field.getAnnotation(RefList.class) != null){
            fetchRefList(obj, field);
        }
    }
    
    private static void fetchRemainder(BuguEntity obj, String fieldName, String remainder){
        Field field = null;
        try{
            field = FieldsCache.getInstance().getField(obj.getClass(), fieldName);
        }catch(FieldException ex){
            logger.error(ex.getMessage(), ex);
        }
        Object value = FieldUtil.get(obj, field);
        if(value == null){
            return;
        }
        if(field.getAnnotation(Ref.class) != null){
            BuguEntity entity = (BuguEntity)value;
            fetchCascade(entity, remainder);
        }else if(field.getAnnotation(RefList.class) != null){
            Class type = field.getType();
            if(DataType.isMapType(type)){
                //To-do:
                //this is not strictly correct. It won't works in some situation
                Map<Object, BuguEntity> map = (Map<Object, BuguEntity>)value;
                for(Entry<Object, BuguEntity> entry : map.entrySet()){
                    fetchCascade(entry.getValue(), remainder);
                }
            }
            else{
                Collection<BuguEntity> collection = (Collection<BuguEntity>)value;
                for(BuguEntity entity : collection){
                    fetchCascade(entity, remainder);
                }
            }
        }
    }
    
    private static void fetchRef(BuguEntity obj, Field field){
        Object val = FieldUtil.get(obj, field);
        if( val == null){
            return;
        }
        BuguEntity refObj = (BuguEntity)val;
        String id = refObj.getId();
        Class cls = FieldUtil.getRealType(field);
        InternalDao dao = DaoCache.getInstance().get(cls);
        Object value = dao.findOne(id);
        FieldUtil.set(obj, field, value);
    }
    
    private static void fetchRefList(BuguEntity obj, Field field){
        Object val = FieldUtil.get(obj, field);
        if(val == null){
            return;
        }
        Class<?> type = field.getType();
        if(type.isArray()){
            Object arr = fetchArrayValue(val, field, type.getComponentType());
            FieldUtil.set(obj, field, arr);
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                //for Collection
                List list = fetchCollectionValue(val, field, (Class)types[0]);
                if(DataType.isListType(type)){
                    FieldUtil.set(obj, field, list);
                }
                else if(DataType.isSetType(type)){
                    FieldUtil.set(obj, field, new HashSet(list));
                }
                else if(DataType.isQueueType(type)){
                    FieldUtil.set(obj, field, new LinkedList(list));
                }
            }else if(len == 2){
                //for Map
                Map map = fetchMapValue(val, field);
                FieldUtil.set(obj, field, map);
            }
        }
    }
    
    private static Object fetchArrayValue(Object val, Field field, Class elementClass) {
        int len = Array.getLength(val);
        elementClass = FieldUtil.getRealType(elementClass, field);
        Object arr = Array.newInstance(elementClass, len);
        List<String> idList = new ArrayList<String>();
        for(int i=0; i<len; i++){
            Object item = Array.get(val, i);
            if(item != null){
                BuguEntity ent = (BuguEntity)item;
                idList.add(ent.getId());
            }
        }
        RefList refList = field.getAnnotation(RefList.class);
        String sort = refList.sort();
        InternalDao dao = DaoCache.getInstance().get(elementClass);
        BuguQuery query = dao.query().in(Operator.ID, idList);
        List<BuguEntity> entityList;
        if(sort.equals(Default.SORT)){
            entityList = query.results();
        }else{
            entityList = query.sort(sort).results();
        }
        if(entityList.size() != len){
            len = entityList.size();
            arr = Array.newInstance(elementClass, len);
        }
        for(int i=0; i<len; i++){
            Array.set(arr, i, entityList.get(i));
        }
        return arr;
    }
    
    private static List fetchCollectionValue(Object val, Field field, Class elementClass){
        Collection<BuguEntity> collection = (Collection<BuguEntity>)val;
        List<String> idList = new ArrayList<String>();
        for(BuguEntity ent : collection){
            if(ent != null){
                idList.add(ent.getId());
            }
        }
        RefList refList = field.getAnnotation(RefList.class);
        String sort = refList.sort();
        elementClass = FieldUtil.getRealType(elementClass, field);
        InternalDao dao = DaoCache.getInstance().get(elementClass);
        BuguQuery query = dao.query().in(Operator.ID, idList);
        List result;
        if(sort.equals(Default.SORT)){
            result = query.results();
        }else{
            result = query.sort(sort).results();
        }
        return result;
    }
    
    private static Map fetchMapValue(Object val, Field field) {
        //for Map<K,V>, first to check the type of V
        ParameterizedType paramType = (ParameterizedType)field.getGenericType();
        Type[] types = paramType.getActualTypeArguments();
        boolean isArray = false;
        boolean isCollection = false;
        boolean isSingle = false;
        Class vType = null;
        Class elementType = null;
        if(types[1] instanceof GenericArrayType){
            isArray = true;
            GenericArrayType g = (GenericArrayType)types[1];
            elementType = (Class)g.getGenericComponentType();
        }else if(types[1] instanceof ParameterizedType){
            isCollection = true;
            ParameterizedType p = (ParameterizedType)types[1];
            vType = (Class)p.getRawType();
            elementType = (Class)p.getActualTypeArguments()[0];
        }else{
            //in JDK8, type[1] of array, is a class, not array
            Class<?> actualType = FieldUtil.getClassOfType(types[1]);
            if(actualType.isArray()){
                isArray = true;
                elementType = actualType.getComponentType();
            }else{
                isSingle = true;
            }
        }
        //get value by different type of V
        Map map = (Map)val;
        Map result = new HashMap();
        Class<?> cls  = null;
        InternalDao dao = null;
        if(isSingle){
            cls = FieldUtil.getRealType((Class)types[1], field);
            dao = DaoCache.getInstance().get(cls);
        }
        for(Object key : map.keySet()){
            Object entryValue = map.get(key);
            if(entryValue == null){
                result.put(key, null);
                continue;
            }
            if(isSingle){
                BuguEntity refObj = (BuguEntity)entryValue;
                String id = refObj.getId();
                Object value = dao.findOne(id);
                result.put(key, value);
            }else if(isArray){
                Object arr = fetchArrayValue(entryValue, field, elementType);
                result.put(key, arr);
            }else if(isCollection){
                List list = fetchCollectionValue(entryValue, field, elementType);
                if(DataType.isListType(vType)){
                    result.put(key, list);
                }
                else if(DataType.isSetType(vType)){
                    result.put(key, new HashSet(list));
                }
                else if(DataType.isQueueType(vType)){
                    result.put(key, new LinkedList(list));
                }
            }
        }
        return result;
    }
    
}
