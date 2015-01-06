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
    
    private RefList refList;
    
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
            decodeArray(obj, type.getComponentType());
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                decodeCollection(obj, (Class)types[0]);
            }else if(len == 2){
                decodeMap(obj, (Class)types[1]);
            }
        }
    }
    
    private void decodeArray(Object obj, Class clazz){
        clazz = FieldUtil.getRealType(clazz, field);
        List list = (ArrayList)value;
        int size = list.size();
        if(size <= 0){
            return;
        }
        Object arr = Array.newInstance(clazz, size);
        if(refList.cascade().toUpperCase().indexOf(Default.CASCADE_READ)==-1){
            for(int i=0; i<size; i++){
                Object item = list.get(i);
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    BuguEntity refObj = (BuguEntity)ConstructorCache.getInstance().create(clazz);
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
            InternalDao dao = DaoCache.getInstance().get(clazz);
            BuguQuery query = dao.query().in(Operator.ID, idList);
            String sort = refList.sort();
            if(!sort.equals(Default.SORT)){
                query.sort(sort);
            }
            List<BuguEntity> entityList = query.results();
            //when query returns, the size maybe changed
            if(entityList.size() != size){
                size = entityList.size();
                arr = Array.newInstance(clazz, size);
            }
            for(int i=0; i<size; i++){
                Array.set(arr, i, entityList.get(i));
            }
        }
        FieldUtil.set(obj, field, arr);
    }
    
    private void decodeCollection(Object obj, Class clazz){
        clazz = FieldUtil.getRealType(clazz, field);
        Collection collection = (Collection)value;
        List<BuguEntity> result = new ArrayList<BuguEntity>();
        if(refList.cascade().toUpperCase().indexOf(Default.CASCADE_READ)==-1){
            for(Object item : collection){
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    BuguEntity refObj = (BuguEntity)ConstructorCache.getInstance().create(clazz);
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
            InternalDao dao = DaoCache.getInstance().get(clazz);
            BuguQuery query = dao.query().in(Operator.ID, idList);
            String sort = refList.sort();
            if(!sort.equals(Default.SORT)){
                query.sort(sort);
            }
            result = query.results();
        }
        Class type = field.getType();
        if(DataType.isListType(type)){
            FieldUtil.set(obj, field, result);
        }
        else if(DataType.isSetType(type)){
            FieldUtil.set(obj, field, new HashSet(result));
        }
        else if(DataType.isQueueType(type)){
            FieldUtil.set(obj, field, new LinkedList(result));
        }
    }
    
    private void decodeMap(Object obj, Class clazz){
        clazz = FieldUtil.getRealType(clazz, field);
        Map map = (Map)value;
        Map<Object, BuguEntity> result = new HashMap<Object, BuguEntity>();
        if(refList.cascade().toUpperCase().indexOf(Default.CASCADE_READ)==-1){
            for(Object key : map.keySet()){
                Object item = map.get(key);
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    BuguEntity refObj = (BuguEntity)ConstructorCache.getInstance().create(clazz);
                    refObj.setId(refId);
                    result.put(key, refObj);
                }else{
                    result.put(key, null);
                }
            }
        }else{
            InternalDao dao = DaoCache.getInstance().get(clazz);
            for(Object key : map.keySet()){
                Object item = map.get(key);
                if(item != null){
                    String refId = ReferenceUtil.fromDbReference(refList, item);
                    BuguEntity refObj = (BuguEntity)dao.findOne(refId);
                    result.put(key, refObj);
                }else{
                    result.put(key, null);
                }
            }
        }
        FieldUtil.set(obj, field, result);
    }
    
}
