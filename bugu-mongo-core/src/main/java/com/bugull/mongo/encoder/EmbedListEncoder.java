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
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
            result = encodeArray();
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                result = encodeCollection();
            }else if(len == 2){
                result = encodeMap();
            }
        }
        return result;
    }
    
    private Object encodeArray(){
        int len = Array.getLength(value);
        List<DBObject> result = new ArrayList<DBObject>();
        for(int i=0; i<len; i++){
            Object o = Array.get(value, i);
            if(o != null){
                result.add(MapperUtil.toDBObject(o));
            }
        }
        return result;
    }
    
    private Object encodeCollection(){
        List<DBObject> result = new ArrayList<DBObject>();
        Collection collection = (Collection)value;
        for(Object o : collection){
            if(o != null){
                result.add(MapperUtil.toDBObject(o));
            }
        }
        return result;
    }
    
    private Object encodeMap(){
        Map map = (Map)value;
        Map result = new HashMap();
        for(Object key : map.keySet()){
            Object o = map.get(key);
            if(o != null){
                result.put(key, MapperUtil.toDBObject(o));
            }else{
                result.put(key, null);
            }
        }
        return result;
    }
    
}
