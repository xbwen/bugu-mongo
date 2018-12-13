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

package com.bugull.mongo.encoder;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.access.InternalDao;
import com.bugull.mongo.utils.ReferenceUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class RefListEncoder extends AbstractEncoder{
    
    private final RefList refList;
    private final boolean cascadeCU;
    
    public RefListEncoder(Object obj, Field field){
        super(obj, field);
        refList = field.getAnnotation(RefList.class);
        cascadeCU = refList.cascade().toUpperCase().indexOf(Default.CASCADE_CREATE)!=-1 || refList.cascade().toUpperCase().indexOf(Default.CASCADE_UPDATE)!=-1;
    }
    
    @Override
    public String getFieldName(){
        String fieldName = field.getName();
        String name = refList.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        return fieldName;
    }
    
    @Override
    public Object encode(){
        Object result = null;
        Class<?> type = field.getType();
        if(type.isArray()){
            result = encodeArray(type.getComponentType(), value);
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                result = encodeCollection((Class)types[0], value);
            }else if(len == 2){
                result = encodeMap();
            }
        }
        return result;
    }
    
    private Object encodeArray(Class type, Object val){
        Class<?> cls = FieldUtil.getRealType(type, field);
        InternalDao dao = DaoCache.getInstance().get(cls);
        int len = Array.getLength(val);
        List<Object> result = new ArrayList<>();
        for(int i=0; i<len; i++){
            BuguEntity entity = (BuguEntity)Array.get(val, i);
            if(entity != null){
                if(!withoutCascade && cascadeCU){
                    dao.saveWithoutCascade(entity, true);
                }
                result.add(ReferenceUtil.toDbReference(refList, entity.getClass(), entity.getId()));
            }
        }
        return result;
    }
    
    private Object encodeCollection(Class type, Object val){
        Collection<BuguEntity> collection = (Collection<BuguEntity>)val;
        List<Object> result = new ArrayList<>();
        Class<?> cls = FieldUtil.getRealType(type, field);
        InternalDao dao = DaoCache.getInstance().get(cls);
        for(BuguEntity entity : collection){
            if(entity != null){
                if(!withoutCascade && cascadeCU){
                    dao.saveWithoutCascade(entity, true);
                }
                result.add(ReferenceUtil.toDbReference(refList, entity.getClass(), entity.getId()));
            }
        }
        return result;
    }
    
    private Object encodeMap(){
        //for Map<K,V>, first to check the type of V
        ParameterizedType paramType = (ParameterizedType)field.getGenericType();
        Type[] types = paramType.getActualTypeArguments();
        boolean isArray = false;
        boolean isCollection = false;
        boolean isSingle = false;
        Class elementType = null;
        if(types[1] instanceof GenericArrayType){
            isArray = true;
            GenericArrayType g = (GenericArrayType)types[1];
            elementType = (Class)g.getGenericComponentType();
        }else if(types[1] instanceof ParameterizedType){
            isCollection = true;
            ParameterizedType p = (ParameterizedType)types[1];
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
        //encode value by different type of V
        Map result = new HashMap();
        InternalDao dao = null;
        if(isSingle){
            Class<?> cls = FieldUtil.getRealType((Class)types[1], field);
            dao = DaoCache.getInstance().get(cls);
        }
        Map map = (Map)value;
        for(Object key : map.keySet()){
            Object entryValue = map.get(key);
            if(isSingle){
                BuguEntity entity = (BuguEntity)entryValue;
                if(entity != null){
                    if(!withoutCascade && cascadeCU){
                        dao.saveWithoutCascade(entity, true);
                    }
                    result.put(key, ReferenceUtil.toDbReference(refList, entity.getClass(), entity.getId()));
                }else{
                    result.put(key, null);
                }
            }
            else if(isArray){
                Object arr = encodeArray(elementType, entryValue);
                result.put(key, arr);
            }
            else if(isCollection){
                Object arr = encodeCollection(elementType, entryValue);
                result.put(key, arr);
            }
        }
        return result;
    }
    
}
