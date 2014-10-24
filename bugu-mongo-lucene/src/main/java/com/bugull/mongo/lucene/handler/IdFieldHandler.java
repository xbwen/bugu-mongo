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

import com.bugull.mongo.BuguEntity;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IdFieldHandler extends AbstractFieldHandler{
    
    public IdFieldHandler(Object obj, java.lang.reflect.Field field, String prefix){
        super(obj, field, prefix);
    }

    @Override
    public void handle(Document doc) {
        BuguEntity entity = (BuguEntity)obj;
        String fieldName = field.getName();
        doc.add(new Field(fieldName, entity.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    }
    
}
