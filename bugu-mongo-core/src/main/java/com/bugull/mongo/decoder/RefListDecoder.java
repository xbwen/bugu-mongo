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

package com.bugull.mongo.decoder;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.ConstructorCache;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.utils.DataType;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.misc.InternalDao;
import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.ReferenceUtil;
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

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class RefListDecoder extends AbstractDecoder{
    
    private final RefList refList;
    
    public RefListDecoder(Field field, DBObject dbo){
        super(field);
        refList = field.getAnnotation(RefList.class);
        String fieldName = field.getName();
        String name = refList.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        value = dbo.get(fieldName);
    }
    
    @Override
    public void decode(Object obj){
        Class<?> type = field.getType();
        if(type.isArray()){
            Object arr = decodeArray(value, type.getComponentType());
            FieldUtil.set(obj, field, arr);
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                List list = decodeCollection(value, (Class)types[0]);
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
                Object map = decodeMap();
                FieldUtil.set(obj, field, map);
            }
        }
    }
    
    private Object decodeArray(Object val, Class elementClass){
        elementClass = FieldUtil.getRealType(elementClass, field);
        List list = (ArrayList)val;
        int size = list.size();
        if(size <= 0){
            return null;
        }
        Object arr = Array.newInstance(elementClass, size);
        if(refList.cascade().toUpperCase().indexOf(Default.CASCADE_READ)==-1){
            for(int i=0; i<size; i++){
                Object item = list.get(i);
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    BuguEntity refObj = (BuguEntity)ConstructorCache.getInstance().create(elementClass);
                    refObj.setId(refId);
                    Array.set(arr, i, refObj);
                }else{
                    Array.set(arr, i, null);
                }
            }
        }
        else{
            List<String> idList = new ArrayList<String>();
            for(int i=0; i<size; i++){
                Object item = list.get(i);
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    idList.add(refId);
                }
            }
            InternalDao dao = DaoCache.getInstance().get(elementClass);
            BuguQuery query = dao.query().in(Operator.ID, idList);
            String sort = refList.sort();
            if(!sort.equals(Default.SORT)){
                query.sort(sort);
            }
            List<BuguEntity> entityList = query.results();
            //when query returns, the size maybe changed
            if(entityList.size() != size){
                size = entityList.size();
                arr = Array.newInstance(elementClass, size);
            }
            for(int i=0; i<size; i++){
                Array.set(arr, i, entityList.get(i));
            }
        }
        return arr;
    }
    
    private List decodeCollection(Object val, Class elementClass){
        elementClass = FieldUtil.getRealType(elementClass, field);
        Collection collection = (Collection)val;
        List<BuguEntity> result = new ArrayList<BuguEntity>();
        if(refList.cascade().toUpperCase().indexOf(Default.CASCADE_READ)==-1){
            for(Object item : collection){
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    BuguEntity refObj = (BuguEntity)ConstructorCache.getInstance().create(elementClass);
                    refObj.setId(refId);
                    result.add(refObj);
                }
            }
        }else{
            List<String> idList = new ArrayList<String>();
            for(Object item : collection){
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    idList.add(refId);
                }
            }
            InternalDao dao = DaoCache.getInstance().get(elementClass);
            BuguQuery query = dao.query().in(Operator.ID, idList);
            String sort = refList.sort();
            if(!sort.equals(Default.SORT)){
                query.sort(sort);
            }
            result = query.results();
        }
        return result;
    }
    
    private Map decodeMap(){
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
        //decode value by different type of V
        Map map = (Map)value;
        Map result = new HashMap();
        boolean cascadeRead = false;
        Class<?> cls  = null;
        InternalDao dao = null;
        if(isSingle){
            cls  = FieldUtil.getRealType((Class)types[1], field);
            cascadeRead = (refList.cascade().toUpperCase().indexOf(Default.CASCADE_READ) != -1);
            if(cascadeRead){
                dao = DaoCache.getInstance().get(cls);
            }
        }
        for(Object key : map.keySet()){
            Object entryValue = map.get(key);
            if(entryValue == null){
                result.put(key, null);
                continue;
            }
            if(isSingle){
                String refId = ReferenceUtil.fromDbReference(refList, entryValue);
                BuguEntity refObj = null;
                if(cascadeRead){
                    refObj = (BuguEntity)dao.findOne(refId);
                }else{
                    refObj = (BuguEntity)ConstructorCache.getInstance().create(cls);
                    refObj.setId(refId);
                }
                result.put(key, refObj);
            }else if(isArray){
                Object arr = decodeArray(entryValue, elementType);
                result.put(key, arr);
            }else if(isCollection){
                List list = decodeCollection(entryValue, elementType);
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
