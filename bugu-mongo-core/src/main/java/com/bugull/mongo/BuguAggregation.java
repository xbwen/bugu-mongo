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

import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.SortUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.List;

/**
 * Convenient class for creating aggregating operation.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguAggregation<T> {
    
    private DBCollection coll;
    private final List<DBObject> pipeline = new ArrayList<DBObject>();
    
    public BuguAggregation(DBCollection coll){
        this.coll = coll;
    }
    
    public BuguAggregation lookup(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.LOOKUP, dbo));
        return this;
    }
    
    public BuguAggregation lookup(String jsonString){
        DBObject dbo = (DBObject)JSON.parse(jsonString);
        return lookup(dbo);
    }
    
    public BuguAggregation lookup(Lookup lookup){
        DBObject dbo = new BasicDBObject();
        dbo.put(Lookup.FROM, lookup.from);
        dbo.put(Lookup.LOCAL_FIELD, lookup.localField);
        dbo.put(Lookup.FOREIGN_FIELD, lookup.foreignField);
        dbo.put(Lookup.AS, lookup.as);
        return lookup(dbo);
    }
    
    public BuguAggregation project(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.PROJECT, dbo));
        return this;
    }
    
    public BuguAggregation project(String jsonString){
        DBObject dbo = (DBObject)JSON.parse(jsonString);
        return project(dbo);
    }
    
    public BuguAggregation projectInclude(String... fields){
        DBObject dbo = new BasicDBObject();
        for(String field : fields){
            dbo.put(field, 1);
        }
        return project(dbo);
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
    
    public BuguAggregation match(BuguQuery query){
        return match(query.getCondition());
    }
    
    public BuguAggregation match(String jsonString){
        DBObject dbo = (DBObject)JSON.parse(jsonString);
        return match(dbo);
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
        if(! field.startsWith("$")){
            field = "$" + field;
        }
        pipeline.add(new BasicDBObject(Operator.UNWIND, field));
        return this;
    }
    
    public BuguAggregation group(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.GROUP, dbo));
        return this;
    }
    
    public BuguAggregation group(String jsonString){
        DBObject dbo = (DBObject)JSON.parse(jsonString);
        return group(dbo);
    }
    
    public BuguAggregation sort(String jsonString){
        DBObject dbo = SortUtil.getSort(jsonString);
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public BuguAggregation sort(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public BuguAggregation out(String target){
        pipeline.add(new BasicDBObject(Operator.OUT, target));
        return this;
    }
    
    public Iterable<DBObject> results(){
        AggregationOutput output = coll.aggregate(pipeline);
        return output.results();
    }
    
    
    public final static class Lookup{
        
        final static String FROM = "from";
        final static String LOCAL_FIELD = "localField";
        final static String FOREIGN_FIELD = "foreignField";
        final static String AS = "as";
        
        String from;
        String localField;
        String foreignField;
        String as;
        
        public Lookup(String from, String localField, String foreignField, String as){
            this.from = from;
            this.localField = localField;
            this.foreignField = foreignField;
            this.as = as;
        }
        
    }

}
