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

import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.lucene.annotations.IndexEmbedBy;
import com.bugull.mongo.utils.FieldUtil;
import java.lang.reflect.Field;
import org.apache.lucene.document.Document;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class EmbedFieldHandler extends AbstractFieldHandler{
    
    public EmbedFieldHandler(Object obj, Field field, String prefix){
        super(obj, field, prefix);
    }

    @Override
    public void handle(Document doc){
        Object embedObj = FieldUtil.get(obj, field);
        if(embedObj == null){
            return;
        }
        Class<?> clazz = field.getType();
        Field[] fields = FieldsCache.getInstance().get(clazz);
        for(Field f : fields){
            IndexEmbedBy ieb = f.getAnnotation(IndexEmbedBy.class);
            if(ieb != null){
                FieldHandler handler = new EmbedByFieldHandler(obj.getClass(), embedObj, f, prefix);
                handler.handle(doc);
            }
        }
    }
    
}
