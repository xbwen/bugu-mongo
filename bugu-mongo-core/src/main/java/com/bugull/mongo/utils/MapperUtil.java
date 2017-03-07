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
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.cache.ConstructorCache;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.decoder.Decoder;
import com.bugull.mongo.decoder.DecoderFactory;
import com.bugull.mongo.encoder.Encoder;
import com.bugull.mongo.encoder.EncoderFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for internal useage.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class MapperUtil {
    
    /**
     * Convert a DBObject to entity object
     * @param <T>
     * @param clazz
     * @param dbo
     * @return 
     */
    public static <T> T fromDBObject(Class<T> clazz, DBObject dbo){
        return fromDBObject(clazz, dbo, false);
    }
    
    public static <T> T fromDBObject(Class<T> clazz, DBObject dbo, boolean withoutCascade){
        if(dbo == null){
            return null;
        }
        T obj = ConstructorCache.getInstance().create(clazz);
        Field[] fields = FieldsCache.getInstance().get(clazz);
        for(Field field : fields){
            Decoder decoder = DecoderFactory.create(field, dbo);
            if(decoder!=null && !decoder.isNullField()){
                decoder.setWithoutCascade(withoutCascade);
                decoder.decode(obj);
            }
        }
        return obj;
    }
    
    
    /**
     * Convert an entity object to DBObject
     * @param obj
     * @return 
     */
    public static DBObject toDBObject(Object obj){
        return toDBObject(obj, false);
    }
    
    public static DBObject toDBObject(Object obj, boolean withoutCascade){
        if(obj == null){
            return null;
        }
        Class<?> clazz = obj.getClass();
        Field[] fields = FieldsCache.getInstance().get(clazz);
        DBObject dbo = new BasicDBObject();
        for(Field field : fields){
            Encoder encoder = EncoderFactory.create(obj, field);
            if(encoder!=null && !encoder.isNullField()){
                encoder.setWithoutCascade(withoutCascade);
                dbo.put(encoder.getFieldName(), encoder.encode());
            }
        }
        return dbo;
    }
    
    public static <T> List<T> toList(Class<T> clazz, DBCursor cursor){
        return toList(clazz, cursor, false);
    }
    
    public static <T> List<T> toList(Class<T> clazz, DBCursor cursor, boolean withoutCascade){
        List<T> list = new ArrayList<T>();
        while(cursor.hasNext()){
            DBObject dbo = cursor.next();
            list.add(fromDBObject(clazz, dbo, withoutCascade));
        }
        cursor.close();
        return list;
    }
    
    public static <T> List<T> toList(Class<T> clazz, Iterable<DBObject> it){
        List<T> list = new ArrayList<T>();
        for(DBObject dbo : it){
            list.add(fromDBObject(clazz, dbo));
        }
        return list;
    }
    
    /**
     * Get the name property of @Entity annotation. 
     * If the name property is not set, then return the class' name, in lower case type.
     * @param clazz
     * @return 
     */
    public static String getEntityName(Class<?> clazz){
        Entity entity = clazz.getAnnotation(Entity.class);
        String name = entity.name();
        if(name.equals(Default.NAME)){
            name = clazz.getSimpleName().toLowerCase();
        }
        return name;
    }
    
}
