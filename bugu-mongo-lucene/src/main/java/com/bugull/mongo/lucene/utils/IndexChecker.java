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

package com.bugull.mongo.lucene.utils;

import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.lucene.annotations.*;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class IndexChecker {
    
    /**
     * Check if the clazz need a lucene listener.
     * <p>If it has @Indexed annotation, or, some of it's fields has @IndexRefBy annotation,
     * then it need a lucene listener.</p>
     * 
     * @param clazz
     * @return 
     */
    public static boolean needListener(Class<?> clazz){
        boolean result = false;
        if(clazz.getAnnotation(Indexed.class) != null){
            result = true;
        }
        else{
            Field[] fields = FieldsCache.getInstance().get(clazz);
            for(Field f : fields){
                IndexRefBy irb = f.getAnnotation(IndexRefBy.class);
                if(irb != null){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

}
