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

package com.bugull.mongo.lucene.handler;

import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.lucene.annotations.IndexEmbed;
import com.bugull.mongo.lucene.annotations.IndexEmbedList;
import com.bugull.mongo.lucene.annotations.IndexProperty;
import com.bugull.mongo.lucene.annotations.IndexRef;
import com.bugull.mongo.lucene.annotations.IndexRefList;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class FieldHandlerFactory {
    
    private final static String DOT = ".";
    
    public static FieldHandler create(Object obj, Field f, String prefix){
        FieldHandler handler = null;
        if(f.getAnnotation(Id.class) != null){
            handler = new IdFieldHandler(obj, f, "");
        }
        else if(f.getAnnotation(IndexProperty.class) != null){
            handler = new PropertyFieldHandler(obj, f, prefix);
        }
        else if(f.getAnnotation(IndexEmbed.class) != null){
            handler = new EmbedFieldHandler(obj, f, f.getName() + DOT);
        }
        else if(f.getAnnotation(IndexEmbedList.class) != null){
            handler = new EmbedListFieldHandler(obj, f, f.getName() + DOT);
        }
        else if(f.getAnnotation(IndexRef.class) != null){
            handler = new RefFieldHandler(obj, f, f.getName() + DOT);
        }
        else if(f.getAnnotation(IndexRefList.class) != null){
            handler = new RefListFieldHandler(obj, f, f.getName() + DOT);
        }
        return handler;
    }
    
}
