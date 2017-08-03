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

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.utils.DataType;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
public class EmbedListDecoder extends AbstractDecoder{
    
    public EmbedListDecoder(Field field, DBObject dbo){
        super(field);
        String fieldName = field.getName();
        EmbedList embedList = field.getAnnotation(EmbedList.class);
        String name = embedList.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        value = dbo.get(fieldName);
    }

    @Override
    public void decode(Object obj) {
        Class<?> type = field.getType();
        if(type.isArray()){
            Object arr = null;
            Class<?> comType = type.getComponentType();
            if(comType.isEnum()){
                arr = decodeEnumArray(value, comType);
            }else{
                arr = decodeArray(value, comType);
            }
            
            FieldUtil.set(obj, field, arr);
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                Class<?> comType = (Class)types[0];
                List list = null;
                if(comType.isEnum()){
                    list = decodeEnumCollection(value, comType);
                }else{
                    list = decodeCollection(value, comType);
                }
                if(DataType.isListType(type) || DataType.isCollectionType(type)){
                    FieldUtil.set(obj, field, list);
                }
                else if(DataType.isSetType(type)){
                    FieldUtil.set(obj, field, new HashSet(list));
                }
                else if(DataType.isQueueType(type)){
                    FieldUtil.set(obj, field, new LinkedList(list));
                }
            }else{
                Map map = decodeMap();
                FieldUtil.set(obj, field, map);
            }
        }
    }
    
    private Object decodeEnumArray(Object val, Class elementClass){
        List list = (ArrayList)val;
        int size = list.size();
        Object arr = Array.newInstance(elementClass, size);
        for(int i=0; i<size; i++){
            Object item = list.get(i);
            if(item != null){
                Array.set(arr, i, Enum.valueOf((Class<Enum>)elementClass, (String)item));
            }else{
                Array.set(arr, i, null);
            }
        }
        return arr;
    }
    
    private Object decodeArray(Object val, Class elementClass){
        List list = (ArrayList)val;
        int size = list.size();
        Object arr = Array.newInstance(elementClass, size);
        for(int i=0; i<size; i++){
            Object item = list.get(i);
            if(item != null){
                DBObject o = (DBObject)item;
                Array.set(arr, i, MapperUtil.fromDBObject(elementClass, o));
            }else{
                Array.set(arr, i, null);
            }
        }
        return arr;
    }
    
    private List decodeEnumCollection(Object val, Class elementClass){
        List list = (ArrayList)val;
        List result = new ArrayList();
        for(Object item : list){
            if(item != null){
                result.add(Enum.valueOf((Class<Enum>)elementClass, (String)item));
            }
        }
        return result;
    }
    
    private List decodeCollection(Object val, Class elementClass){
        List list = (ArrayList)val;
        List result = new ArrayList();
        for(Object item : list){
            if(item != null){
                Object embedObj = MapperUtil.fromDBObject(elementClass, (DBObject)item);
                result.add(embedObj);
            }
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
        //in JDK6, type[1] of array, is instanceof GenericArrayType
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
        for(Object key : map.keySet()){
            Object entryValue = map.get(key);
            if(entryValue == null){
                result.put(key, null);
                continue;
            }
            if(isSingle){
                Object embedObj = MapperUtil.fromDBObject((Class)types[1], (DBObject)entryValue);
                result.put(key, embedObj);
            }else if(isArray){
                Object arr = decodeArray(entryValue, elementType);
                result.put(key, arr);
            }else if(isCollection){
                List list = decodeCollection(entryValue, elementType);
                if(DataType.isListType(vType) || DataType.isCollectionType(vType)){
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
