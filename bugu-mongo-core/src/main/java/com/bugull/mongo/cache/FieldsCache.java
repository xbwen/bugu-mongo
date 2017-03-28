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

package com.bugull.mongo.cache;

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Ignore;
import com.bugull.mongo.annotations.Property;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.exception.FieldException;
import com.bugull.mongo.exception.IdException;
import com.bugull.mongo.utils.Operator;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cache(Map) holds entity classes' fields, for performance purporse.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FieldsCache {
    
    private final static Logger logger = LogManager.getLogger(FieldsCache.class.getName());
    
    private final ConcurrentMap<String, SoftReference<Field[]>> cache = new ConcurrentHashMap<String, SoftReference<Field[]>>();
    
    private FieldsCache(){
        
    }
    
    private static class Holder {
        final static FieldsCache instance = new FieldsCache();
    } 
    
    public static FieldsCache getInstance(){
        return Holder.instance;
    }
    
    /**
     * Get all declared and inherited field
     * @param clazz
     * @return 
     */
    private Field[] getAllFields(Class<?> clazz){
        List<Field> allFields = new ArrayList<Field>();
        allFields.addAll(filterFields(clazz.getDeclaredFields()));
        Class parent = clazz.getSuperclass();
        while((parent != null) && (parent != Object.class)){
            allFields.addAll(filterFields(parent.getDeclaredFields()));
            parent = parent.getSuperclass();
        }
        return allFields.toArray(new Field[allFields.size()]);
    }
    
    /**
     * Filter the static filed, and set all the filed to alccessibe=true
     * @param fields
     * @return 
     */
    private List<Field> filterFields(Field[] fields){
        List<Field> result = new ArrayList<Field>();
        for(Field field : fields){
            if (!Modifier.isStatic(field.getModifiers())){
                field.setAccessible(true);
                result.add(field);
            }
        }
        return result;
    }
    
    public Field[] get(Class<?> clazz){
        String name = clazz.getName();
        Field[] fields;
        boolean recycled = false;
        SoftReference<Field[]> sr = cache.get(name);
        if(sr != null){
            fields = sr.get();
            if(fields == null){
                recycled = true;
            }else{
                return fields;
            }
        }
        //if not exists
        fields = getAllFields(clazz);
        sr = new SoftReference<Field[]>(fields);
        if(recycled){
            cache.put(name, sr);
            return fields;
        }else{
            SoftReference<Field[]> temp = cache.putIfAbsent(name, sr);
            if(temp != null){
                return temp.get();
            }else{
                return sr.get();
            }
        }
    }
    
    /**
     * Get the field with @Id on it.
     * @param clazz
     * @return 
     */
    public Field getIdField(Class<?> clazz) throws IdException {
        Field result = null;
        Field[] fields = get(clazz);
        for(Field f : fields){
            if(f.getAnnotation(Id.class) != null){
                result = f;
                break;
            }
        }
        if(result == null){
            throw new IdException(clazz.getName() + " does not contain @Id field.");
        }
        return result;
    }
    
    /**
     * Get the field's name with @Id on it. In natural case, it returns "id".
     * @param clazz
     * @return 
     */
    public String getIdFieldName(Class<?> clazz){
        String name = null;
        Field f = null;
        try{
            f = this.getIdField(clazz);
        }catch(IdException ex){
            logger.error(ex.getMessage(), ex);
        }
        if(f != null){
            name = f.getName();
        }
        return name;
    }
    
    /**
     * Get the field by field name.
     * @param clazz
     * @param fieldName
     * @return
     * @throws FieldException 
     */
    public Field getField(Class<?> clazz, String fieldName) throws FieldException {
        //first, search by java object attribute name
        Field field = null;
        Field[] fields = get(clazz);
        for(Field f : fields){
            if(f.getName().equals(fieldName)){
                field = f;
                break;
            }
        }
        //second, if not found, search by mongodb's column name
        if(field == null){
            for(Field f : fields){
                Property property = f.getAnnotation(Property.class);
                if(property != null && property.name().equals(fieldName)){
                    field = f;
                    break;
                }
                Embed embed = f.getAnnotation(Embed.class);
                if(embed != null && embed.name().equals(fieldName)){
                    field = f;
                    break;
                }
                EmbedList el = f.getAnnotation(EmbedList.class);
                if(el != null && el.name().equals(fieldName)){
                    field = f;
                    break;
                }
                Ref ref = f.getAnnotation(Ref.class);
                if(ref != null && ref.name().equals(fieldName)){
                    field = f;
                    break;
                }
                RefList rl = f.getAnnotation((RefList.class));
                if(rl != null && rl.name().equals(fieldName)){
                    field = f;
                    break;
                }
            }
        }
        if(field == null){
            throw new FieldException("Field '" + fieldName + "' does not exists!");
        }
        return field;
    }
    
    /**
     * check if the field is annotated by @Embed
     * @param clazz
     * @param fieldName
     * @return 
     */
    public boolean isEmbedField(Class<?> clazz, String fieldName){
        boolean result = false;
        Field[] fields = get(clazz);
        for(Field f : fields){
            if(f.getName().equals(fieldName) && f.getAnnotation(Embed.class)!=null){
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * check if the field is annotated by @EmbedList
     * @param clazz
     * @param fieldName
     * @return 
     */
    public boolean isEmbedListField(Class<?> clazz, String fieldName){
        boolean result = false;
        Field[] fields = get(clazz);
        for(Field f : fields){
            if(f.getName().equals(fieldName) && f.getAnnotation(EmbedList.class)!=null){
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * check if the field is annotated by @RefList
     * @param clazz
     * @param fieldName
     * @return 
     */
    public boolean isRefListField(Class<?> clazz, String fieldName){
        boolean result = false;
        Field[] fields = get(clazz);
        for(Field f : fields){
            if(f.getName().equals(fieldName) && f.getAnnotation(RefList.class)!=null){
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * get all the columns' name
     * @param clazz
     * @return 
     */
    public List<String> getAllColumnsName(Class<?> clazz){
        List<String> list = new ArrayList<String>();
        Field[] fields = get(clazz);
        for(Field f : fields){
            String columnName = f.getName();
            Id id = f.getAnnotation(Id.class);
            if(id != null){
                columnName = Operator.ID;
                list.add(columnName);
                continue;
            }
            Property property = f.getAnnotation(Property.class);
            if(property != null){
                String name = property.name();
                if(!name.equals(Default.NAME)){
                    columnName = name;
                }
                list.add(columnName);
                continue;
            }
            Embed embed = f.getAnnotation(Embed.class);
            if(embed != null){
                String name = embed.name();
                if(!name.equals(Default.NAME)){
                    columnName = name;
                }
                list.add(columnName);
                continue;
            }
            EmbedList embedList = f.getAnnotation(EmbedList.class);
            if(embedList != null){
                String name = embedList.name();
                if(!name.equals(Default.NAME)){
                    columnName = name;
                }
                list.add(columnName);
                continue;
            }
            Ref ref = f.getAnnotation(Ref.class);
            if(ref != null){
                String name = ref.name();
                if(!name.equals(Default.NAME)){
                    columnName = name;
                }
                list.add(columnName);
                continue;
            }
            RefList refList = f.getAnnotation(RefList.class);
            if(refList != null){
                String name = refList.name();
                if(!name.equals(Default.NAME)){
                    columnName = name;
                }
                list.add(columnName);
                continue;
            }
            Ignore ignore = f.getAnnotation(Ignore.class);
            if(ignore != null){
                continue;
            }
            //if no annotations, run to here
            //this must put at the end
            list.add(columnName);
        }
        return list; 
    }
    
}
