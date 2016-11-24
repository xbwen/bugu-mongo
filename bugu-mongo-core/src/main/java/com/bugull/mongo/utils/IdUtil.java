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

import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.IdException;
import java.lang.reflect.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class IdUtil {
    
    private final static Logger logger = LogManager.getLogger(IdUtil.class.getName());
    
    /**
     * Convert the id string to object, which matching the id data in mongoDB.
     * @param clazz
     * @param idStr
     * @return 
     */
    public static Object toDbId(Class<?> clazz, String idStr){
        if(StringUtil.isEmpty(idStr)){
            return null;
        }
        Object result = null;
        Field idField = null;
        try{
            idField = FieldsCache.getInstance().getIdField(clazz);
        }catch(IdException ex){
            logger.error(ex.getMessage(), ex);
        }
        Id idAnnotation = idField.getAnnotation(Id.class);
        
        //the idStr maybe illegal value, have to catch exception here
        try{
            switch(idAnnotation.type()){
                case AUTO_GENERATE:
                    result = new ObjectId(idStr);
                    break;
                case AUTO_INCREASE:
                    result = Long.parseLong(idStr);
                    break;
                case USER_DEFINE:
                    result = idStr;
                    break;
            }
        }catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }

}
