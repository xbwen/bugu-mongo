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

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
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
public class EmbedListEncoder extends AbstractEncoder{
    
    public EmbedListEncoder(Object obj, Field field){
        super(obj, field);
    }

    @Override
    public String getFieldName() {
        String fieldName = field.getName();
        EmbedList embedList = field.getAnnotation(EmbedList.class);
        String name = embedList.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        return fieldName;
    }

    @Override
    public Object encode() {
        Object result = null;
        Class<?> type = field.getType();
        if(type.isArray()){
            Class<?> comType = type.getComponentType();
            if(comType.isEnum()){
                result = encodeEnumArray(value);
            }else{
                result = encodeArray(value);
            }
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                Class<?> realCls = (Class)types[0];
                if(realCls.isEnum()){
                    result = encodeEnumCollection(value);
                }else{
                    result = encodeCollection(value);
                }
            }else if(len == 2){
                result = encodeMap();
            }
        }
        return result;
    }
    
    private Object encodeEnumArray(Object arr){
        int len = Array.getLength(arr);
        List<String> result = new ArrayList<String>();
        for(int i=0; i<len; i++){
            Object o = Array.get(arr, i);
            if(o != null){
                result.add(o.toString());
            }
        }
        return result;
    }
    
    private Object encodeArray(Object arr){
        int len = Array.getLength(arr);
        List<DBObject> result = new ArrayList<DBObject>();
        for(int i=0; i<len; i++){
            Object o = Array.get(arr, i);
            if(o != null){
                result.add(MapperUtil.toDBObject(o));
            }
        }
        return result;
    }
    
    private Object encodeEnumCollection(Object coll){
        List<String> result = new ArrayList<String>();
        Collection collection = (Collection)coll;
        for(Object o : collection){
            if(o != null){
                result.add(o.toString());
            }
        }
        return result;
    }
    
    private Object encodeCollection(Object coll){
        List<DBObject> result = new ArrayList<DBObject>();
        Collection collection = (Collection)coll;
        for(Object o : collection){
            if(o != null){
                result.add(MapperUtil.toDBObject(o));
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
        if(types[1] instanceof GenericArrayType){
            isArray = true;
        }else if(types[1] instanceof ParameterizedType){
            isCollection = true;
        }else{
            //in JDK8, type[1] of array, is a class, not array
            Class<?> actualType = FieldUtil.getClassOfType(types[1]);
            if(actualType.isArray()){
                isArray = true;
            }else{
                isSingle = true;
            }
        }
        //encode value by different type of V
        Map map = (Map)value;
        Map result = new HashMap();
        for(Object key : map.keySet()){
            Object entryValue = map.get(key);
            if(entryValue == null){
                result.put(key, null);
                continue;
            }
            if(isSingle){
                result.put(key, MapperUtil.toDBObject(entryValue));
            }else if(isArray){
                result.put(key, encodeArray(entryValue));
            }else if(isCollection){
                result.put(key, encodeCollection(entryValue));
            }
        }
        return result;
    }
    
}
