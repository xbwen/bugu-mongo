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

import com.bugull.mongo.agg.GeoNearOptions;
import com.bugull.mongo.agg.Lookup;
import com.bugull.mongo.parallel.Parallelable;
import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.SortUtil;
import com.bugull.mongo.utils.StringUtil;
import com.mongodb.AggregationOptions;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Convenient class for creating aggregating operation.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguAggregation<T> implements Parallelable {
    
    private final static Logger logger = LogManager.getLogger(BuguAggregation.class.getName());
    
    private final DBCollection coll;
    private final List<DBObject> pipeline = new ArrayList<DBObject>();
    
    private AggregationOptions options;
    
    public BuguAggregation(DBCollection coll){
        this.coll = coll;
    }
    
    public BuguAggregation setOptions(AggregationOptions options){
        this.options = options;
        return this;
    }
    
    /**
     * Since MongoDB 3.4
     * @param dbo
     * @return 
     */
    public BuguAggregation addFields(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.ADD_FIELDS, dbo));
        return this;
    }
    
    public BuguAggregation addFields(String jsonString){
        DBObject dbo = BasicDBObject.parse(jsonString);
        return addFields(dbo);
    }
    
    public BuguAggregation project(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.PROJECT, dbo));
        return this;
    }
    
    public BuguAggregation project(String jsonString){
        DBObject dbo = BasicDBObject.parse(jsonString);
        return project(dbo);
    }
    
    public BuguAggregation project(String key, Object val){
        return project(new BasicDBObject(key, val));
    }
    
    public BuguAggregation projectInclude(String... fields){
        DBObject dbo = new BasicDBObject();
        for(String field : fields){
            dbo.put(field, 1);
        }
        return project(dbo);
    }
    
    /**
     * Since MongoDB 3.4
     * @param fields
     * @return 
     */
    public BuguAggregation projectExclude(String... fields){
        DBObject dbo = new BasicDBObject();
        for(String field : fields){
            dbo.put(field, 0);
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
        DBObject dbo = BasicDBObject.parse(jsonString);
        return match(dbo);
    }
    
    public BuguAggregation lookup(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.LOOKUP, dbo));
        return this;
    }
    
    public BuguAggregation lookup(String jsonString){
        DBObject dbo = BasicDBObject.parse(jsonString);
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
    
    public BuguAggregation unwindPreserveEmpty(String field){
        if(! field.startsWith("$")){
            field = "$" + field;
        }
        DBObject dbo = new BasicDBObject();
        dbo.put("path", field);
        dbo.put("preserveNullAndEmptyArrays", true);
        pipeline.add(new BasicDBObject(Operator.UNWIND, dbo));
        return this;
    }
    
    public BuguAggregation geoNear(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.GEO_NEAR, dbo));
        return this;
    }
    
    public BuguAggregation geoNear(String jsonString){
        DBObject dbo = BasicDBObject.parse(jsonString);
        return geoNear(dbo);
    }
    
    public BuguAggregation geoNear(GeoNearOptions options){
        DBObject dbo = options.toDBObject();
        return geoNear(dbo);
    }
    
    public BuguAggregation group(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.GROUP, dbo));
        return this;
    }
    
    public BuguAggregation group(String jsonString){
        DBObject dbo = BasicDBObject.parse(jsonString);
        return group(dbo);
    }
    
    public BuguAggregation replaceRoot(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.REPLACE_ROOT, dbo));
        return this;
    }
    
    public BuguAggregation replaceRoot(String jsonString){
        DBObject dbo = BasicDBObject.parse(jsonString);
        return replaceRoot(dbo);
    }
    
    public BuguAggregation replaceRoot(String key, Object value){
        if(StringUtil.isEmpty(key) || !key.equals("newRoot")){
            logger.error("the key must be newRoot when use $replaceRoot!");
        }
        return replaceRoot(new BasicDBObject(key, value));
    }
    
    public BuguAggregation sort(DBObject dbo){
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public BuguAggregation sort(String jsonString){
        DBObject dbo = SortUtil.getSort(jsonString);
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public BuguAggregation sortAsc(String key){
        String ascString = SortUtil.asc(key);
        DBObject dbo = SortUtil.getSort(ascString);
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public BuguAggregation sortDesc(String key){
        String descString = SortUtil.desc(key);
        DBObject dbo = SortUtil.getSort(descString);
        pipeline.add(new BasicDBObject(Operator.SORT, dbo));
        return this;
    }
    
    public BuguAggregation out(String target){
        pipeline.add(new BasicDBObject(Operator.OUT, target));
        return this;
    }
    
    /**
     * Since MongoDB 3.4
     * @param resultName
     * @return 
     */
    public BuguAggregation count(String resultName){
        pipeline.add(new BasicDBObject(Operator.COUNT, resultName));
        return this;
    }
    
    /**
     * Since MongoDB 3.4
     * @param field
     * @return 
     */
    public BuguAggregation sortByCount(String field){
        if(! field.startsWith("$")){
            field = "$" + field;
        }
        pipeline.add(new BasicDBObject(Operator.SORT_BY_COUNT, field));
        return this;
    }
    
    @Override
    public Iterable<DBObject> results(){
        if(options == null){
            AggregationOutput output = coll.aggregate(pipeline);
            return output.results();
        }else{
            final Iterator<DBObject> it = coll.aggregate(pipeline, options);
            return new Iterable<DBObject>() {
                @Override
                public Iterator<DBObject> iterator() {
                    return it;
                }
            };
        }
    }

}
