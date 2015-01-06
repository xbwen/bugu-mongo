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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.lucene.document.Document;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class EmbedListFieldHandler extends AbstractFieldHandler{
     
    public EmbedListFieldHandler(Object obj, Field field, String prefix){
        super(obj, field, prefix);
    }

    @Override
    public void handle(Document doc) {
        Object value = FieldUtil.get(obj, field);
        if(value == null){
            return;
        }
        List list = null;
        Class clazz = null;
        Class<?> type = field.getType();
        if(type.isArray()){
            clazz = type.getComponentType();
            int len = Array.getLength(value);
            list = new ArrayList();
            for(int i=0; i<len; i++){
                list.add(Array.get(value, i));
            }
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            if(types.length == 1){
                clazz = (Class)types[0];
                Collection collection = (Collection)value;
                list = new ArrayList(collection);
            }
            else if(types.length == 2){
                clazz = (Class)types[1];
                Map map = (Map)value;
                list = new ArrayList(map.values());
            }
        }
        if(list!=null && list.size()>0){
            Field[] fields = FieldsCache.getInstance().get(clazz);
            for(Field f : fields){
                IndexEmbedBy ieb = f.getAnnotation(IndexEmbedBy.class);
                if(ieb != null){
                    FieldHandler handler = new EmbedByFieldHandler(obj.getClass(), list, f, prefix);
                    handler.handle(doc);
                }
            }
        }
    }
    
}
