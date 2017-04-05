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

package com.bugull.mongo.access;

import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.IdUtil;
import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.IdType;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.IdException;
import com.bugull.mongo.utils.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.lang.reflect.Field;
import java.util.List;

/**
 * The dao used in bugu-mongo framework itself. Do not use this in your application.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class InternalDao<T> extends BuguDao<T> {
    
    public InternalDao(Class<T> clazz){
        super(clazz);
    }

    /**
     * Get the non-lazy fields.
     * @return 
     */
    public DBObject getKeys() {
        return keys;
    }
    
    /**
     * Get one entity without the lazy fields.
     * @param id
     * @param withoutCascade
     * @return 
     */
    public T findOneLazily(String id, boolean withoutCascade){
        DBObject dbo = new BasicDBObject();
        dbo.put(Operator.ID, IdUtil.toDbId(clazz, id));
        DBObject result = getCollection().findOne(dbo, keys);
        return MapperUtil.fromDBObject(clazz, result, withoutCascade);
    }
    
    public List<T> findNotLazily(DBObject query){
        DBCursor cursor = getCollection().find(query);
        return MapperUtil.toList(clazz, cursor);
    }
    
    public List<T> findNotLazily(int pageNum, int pageSize){
        DBCursor cursor = getCollection().find().skip((pageNum-1)*pageSize).limit(pageSize);
        return MapperUtil.toList(clazz, cursor);
    }
    
    public WriteResult saveWithoutCascade(T t, boolean withoutCascade){
        WriteResult wr;
        BuguEntity ent = (BuguEntity)t;
        if(StringUtil.isEmpty(ent.getId())){
            wr = doInsertWithoutCascade(t, withoutCascade);
        }
        else{
            Field idField = null;
            try{
                idField = FieldsCache.getInstance().getIdField(clazz);
            }catch(IdException ex){
                logger.error(ex.getMessage(), ex);
            }
            Id idAnnotation = idField.getAnnotation(Id.class);
            if(idAnnotation.type()==IdType.USER_DEFINE){
                if(this.exists(Operator.ID, ent.getId())){
                    wr = doSaveWithoutCascade(ent, withoutCascade);
                }else{
                    wr = doInsertWithoutCascade(t, withoutCascade);
                }
            }
            else{
                wr = doSaveWithoutCascade(ent, withoutCascade);
            }
        }
        return wr;
    }
    
    private WriteResult doSaveWithoutCascade(BuguEntity ent, boolean withoutCascade){
        if(hasCustomListener){
            notifyUpdated(ent);
        }
        return getCollection().save(MapperUtil.toDBObject(ent, withoutCascade));
    }
    
    private WriteResult doInsertWithoutCascade(T t, boolean withoutCascade){
        DBObject dbo = MapperUtil.toDBObject(t, withoutCascade);
        WriteResult wr = getCollection().insert(dbo);
        String id = dbo.get(Operator.ID).toString();
        BuguEntity ent = (BuguEntity)t;
        ent.setId(id);
        if(hasCustomListener){
            notifyInserted(ent);
        }
        return wr;
    }
    
    /**
     * Get the max id value, for auto increased id type.
     * @return 
     */
    public synchronized long getMaxId(){
        double d = this.max(Operator.ID);
        return (long)d;
    }

}
