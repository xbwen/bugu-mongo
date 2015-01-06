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

package com.bugull.mongo;

import com.bugull.mongo.exception.AggregationException;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Convenient class for creating aggregating operation.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguAggregation<T> {
    
    private DBCollection coll;
    List<DBObject> pipeline;
    
    public BuguAggregation(DBCollection coll){
        this.coll = coll;
        pipeline = new ArrayList<DBObject>();
    }
    
    public BuguAggregation project(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.PROJECT, dbo));
        return this;
    }
    
    public BuguAggregation match(String key, Object value){
        DBObject dbo = new BasicDBObject(key, value);
        pipeline.add(new BasicDBObject(Operator.MATCH, dbo));
        return this;
    }
    
    public BuguAggregation match(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.MATCH, dbo));
        return this;
    }
    
    public BuguAggregation limit(int n){
        pipeline.add(new BasicDBObject(Operator.LIMIT, n));
        return this;
    }
    
    public BuguAggregation skip(int n){
        pipeline.add(new BasicDBObject(Operator.SKIP, n));
        return this;
    }
    
    public BuguAggregation unwind(String field){
        pipeline.add(new BasicDBObject(Operator.UNWIND, field));
        return this;
    }
    
    public BuguAggregation group(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.GROUP, dbo));
        return this;
    }
    
    public BuguAggregation sort(String orderBy){
        pipeline.add(new BasicDBObject(Operator.SORT, MapperUtil.getSort(orderBy)));
        return this;
    }
    
    public BuguAggregation sort(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public Iterable<DBObject> results() throws AggregationException {
        int size = pipeline.size();
        if(size <= 0){
            throw new AggregationException("Empty pipeline in aggregation!");
        }
        AggregationOutput output = null;
        if(size == 1){
            output = coll.aggregate(pipeline.get(0));
        }else{
            DBObject firstOp = pipeline.get(0);
            List<DBObject> subList = pipeline.subList(1, size);
            DBObject[] arr = subList.toArray(new DBObject[size-1]);
            output = coll.aggregate(firstOp, arr);
        }
        CommandResult cr = output.getCommandResult();
        if(! cr.ok()){
            throw new AggregationException(cr.getErrorMessage());
        }
        return output.results();
    }

}
