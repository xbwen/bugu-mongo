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
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.FieldsCache;
import java.lang.reflect.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for operating object's fields.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class FieldUtil {
    
    private final static Logger logger = LogManager.getLogger(FieldUtil.class.getName());
    
    public static Object get(Object obj, Field f){
        Object value = null;
        try {
            value = f.get(obj);
        } catch (IllegalArgumentException ex) {
            logger.error("Can not get field's value", ex);
        } catch (IllegalAccessException ex) {
            logger.error("Can not get field's value", ex);
        }
        return value;
    }
    
    public static void set(Object obj, Field f, Object value){
        try{
            f.set(obj, value);
        }catch(IllegalArgumentException ex){
            logger.error("Can not set field's value", ex);
        }catch(IllegalAccessException ex){
            logger.error("Can not set field's value", ex);
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
    
}
