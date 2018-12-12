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

package com.bugull.mongo.utils;

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.Property;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.BuguException;
import com.bugull.mongo.exception.FieldException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Utility class for operating object's fields.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class FieldUtil {
    
    //Note: there is an blank space at the end.
    private static final String TYPE_NAME_PREFIX = "class ";
    
    public static Object get(Object obj, Field f){
        Object value = null;
        try {
            value = f.get(obj);
        } catch (Exception ex) {
            throw new FieldException(ex.getMessage());
        }
        return value;
    }
    
    public static void set(Object obj, Field f, Object value){
        try{
            f.set(obj, value);
        }catch(Exception ex){
            throw new FieldException(ex.getMessage());
        }
    }
    
    /**
     * Copy an object's properties to another object. 
     * <p>Note: The source and target object can't be null.</p>
     * @param src
     * @param target 
     */
    public static void copy(Object src, Object target){
        if(src==null || target==null){
            return;
        }
        Field[] fields = FieldsCache.getInstance().get(src.getClass());
        for(Field f : fields){
            Object value = get(src, f);
            set(target, f, value);
        }
    }
    
    public static Class<?> getRealType(Field field){
        Class<?> clazz = field.getType();
        return getRealType(clazz, field);
    }
    
    public static Class<?> getRealType(Class<?> clazz, Field field){
        Class<?> cls = clazz;
        if(clazz.isInterface()){
            Ref ref = field.getAnnotation(Ref.class);
            if(ref!=null && ref.impl()!=Default.class){
                cls = ref.impl();
            }
            else{
                RefList refList = field.getAnnotation(RefList.class);
                if(refList!=null && refList.impl()!=Default.class){
                    cls = refList.impl();
                }
            }
        }
        return cls;
    }
    
    public static Class<?> getClassOfType(Type type) {
        if(type == null) {
            return null;
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        Class<?> cls = null;
        try{
            cls = Class.forName(className);
        }catch(ClassNotFoundException ex){
            throw new BuguException(ex.getMessage());
        }
        return cls;
    }
    
    public static DBObject getLazyFields(Class<?> clazz){
        DBObject lazyKeys = new BasicDBObject();
        Field[] fields = FieldsCache.getInstance().get(clazz);
        for(Field field : fields){
            String fieldName = field.getName();
            Property property = field.getAnnotation(Property.class);
            if(property!=null && property.lazy()){
                String name = property.name();
                if(!name.equals(Default.NAME)){
                    fieldName = name;
                }
                lazyKeys.put(fieldName, 0);
                continue;
            }
            Embed embed = field.getAnnotation(Embed.class);
            if(embed!=null && embed.lazy()){
                String name = embed.name();
                if(!name.equals(Default.NAME)){
                    fieldName = name;
                }
                lazyKeys.put(fieldName, 0);
                continue;
            }
            EmbedList embedList = field.getAnnotation(EmbedList.class);
            if(embedList!=null && embedList.lazy()){
                String name = embedList.name();
                if(!name.equals(Default.NAME)){
                    fieldName = name;
                }
                lazyKeys.put(fieldName, 0);
                continue;
            }
        }
        return lazyKeys;
    }
    
    public static boolean hasCascadeDelete(Class<?> clazz){
        boolean result = false;
        Field[] fields = FieldsCache.getInstance().get(clazz);
        for(Field f : fields){
            Ref ref = f.getAnnotation(Ref.class);
            if(ref!=null && ref.cascade().toUpperCase().indexOf(Default.CASCADE_DELETE)!=-1){
                result = true;
                break;
            }
            RefList refList = f.getAnnotation(RefList.class);
            if(refList!=null && refList.cascade().toUpperCase().indexOf(Default.CASCADE_DELETE)!=-1){
                result = true;
                break;
            }
        }
        return result;
    }
    
}
