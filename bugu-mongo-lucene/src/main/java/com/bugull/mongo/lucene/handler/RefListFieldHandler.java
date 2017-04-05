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
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.lucene.annotations.IndexRefBy;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.utils.IdUtil;
import com.bugull.mongo.access.InternalDao;
import com.bugull.mongo.utils.Operator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.lucene.document.Document;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class RefListFieldHandler extends AbstractFieldHandler{
    
    public RefListFieldHandler(Object obj, Field field, String prefix){
        super(obj, field, prefix);
    }

    @Override
    public void handle(Document doc){
        Object value = FieldUtil.get(obj, field);
        if(value == null){
            return;
        }
        Class clazz = null;
        Class<?> type = field.getType();
        List<Object> idList = new ArrayList<Object>();
        if(type.isArray()){
            clazz = type.getComponentType();
            int len = Array.getLength(value);
            for(int i=0; i<len; i++){
                Object item = Array.get(value, i);
                if(item != null){
                    BuguEntity ent = (BuguEntity)item;
                    Object dbId = IdUtil.toDbId(ent.getClass(), ent.getId());
                    idList.add(dbId);
                }
            }
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            if(types.length == 1){
                clazz = (Class)types[0];
                Collection<BuguEntity> collection = (Collection<BuguEntity>)value;
                for(BuguEntity ent : collection){
                    if(ent != null){
                        Object dbId = IdUtil.toDbId(ent.getClass(), ent.getId());
                        idList.add(dbId);
                    }
                }
            }
            else if(types.length == 2){
                clazz = (Class)types[1];
                Map<Object, BuguEntity> map = (Map<Object, BuguEntity>)value;
                for(Entry<Object, BuguEntity> entry : map.entrySet()){
                    BuguEntity ent = entry.getValue();
                    if(ent != null){
                        Object dbId = IdUtil.toDbId(ent.getClass(), ent.getId());
                        idList.add(dbId);
                    }
                }
            }
        }
        clazz = FieldUtil.getRealType(clazz, field);
        InternalDao dao = DaoCache.getInstance().get(clazz);
        DBObject in = new BasicDBObject(Operator.IN, idList);
        DBObject query = new BasicDBObject(Operator.ID, in);
        List list = dao.findNotLazily(query);
        if(list!=null && list.size()>0){
            Field[] fields = FieldsCache.getInstance().get(clazz);
            for(Field f : fields){
                IndexRefBy irb = f.getAnnotation(IndexRefBy.class);
                if(irb != null){
                    FieldHandler handler = new RefByFieldHandler(obj.getClass(), list, f, prefix);
                    handler.handle(doc);
                }
            }
        }
    }
    
}
