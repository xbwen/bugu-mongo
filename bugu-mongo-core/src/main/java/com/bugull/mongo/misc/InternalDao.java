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

package com.bugull.mongo.misc;

import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.IdUtil;
import com.bugull.mongo.AdvancedDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.List;

/**
 * The dao used in bugu-mongo framework itself. Do not use this in your application.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class InternalDao<T> extends AdvancedDao<T> {
    
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
     * @return 
     */
    public T findOneLazily(String id){
        DBObject dbo = new BasicDBObject();
        dbo.put(Operator.ID, IdUtil.toDbId(clazz, id));
        DBObject result = coll.findOne(dbo, keys);
        return MapperUtil.fromDBObject(clazz, result);
    }
    
    public List<T> findNotLazily(DBObject query){
        DBCursor cursor = coll.find(query);
        return MapperUtil.toList(clazz, cursor);
    }
    
    public List<T> findNotLazily(int pageNum, int pageSize){
        DBCursor cursor = coll.find().skip((pageNum-1)*pageSize).limit(pageSize);
        return MapperUtil.toList(clazz, cursor);
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
