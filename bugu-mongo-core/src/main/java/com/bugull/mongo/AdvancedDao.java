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

import com.bugull.mongo.exception.MapReduceException;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Aggregation and MapReduce.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class AdvancedDao<T> extends BuguDao<T>{
    
    public AdvancedDao(Class<T> clazz){
        super(clazz);
    }
    
    public double max(String key){
        return max(key, new BasicDBObject());
    }
    
    public double max(String key, BuguQuery query){
        return max(key, query.getCondition());
    }
    
    private double max(String key, DBObject query){
        if(!this.exists(query)){
            return 0;
        }
        StringBuilder map = new StringBuilder("function(){emit('");
        map.append(key);
        map.append("', {'value':this.");
        map.append(key);
        map.append("});}");
        String reduce = "function(key, values){var max=values[0].value; for(var i=1;i<values.length; i++){if(values[i].value>max){max=values[i].value;}} return {'value':max}}";
        Iterable<DBObject> results = null;
        try{
            results = mapReduce(map.toString(), reduce, query);
        }catch(MapReduceException ex){
            logger.error(ex.getMessage(), ex);
        }
        DBObject result = results.iterator().next();
        DBObject dbo = (DBObject)result.get("value");
        return Double.parseDouble(dbo.get("value").toString());
    }
    
    public double min(String key){
        return min(key, new BasicDBObject());
    }
    
    public double min(String key, BuguQuery query){
        return min(key, query.getCondition());
    }
    
    private double min(String key, DBObject query){
        if(!this.exists(query)){
            return 0;
        }
        StringBuilder map = new StringBuilder("function(){emit('");
        map.append(key);
        map.append("', {'value':this.");
        map.append(key);
        map.append("});}");
        String reduce = "function(key, values){var min=values[0].value; for(var i=1;i<values.length; i++){if(values[i].value<min){min=values[i].value;}} return {'value':min}}";
        Iterable<DBObject> results = null;
        try{
            results = mapReduce(map.toString(), reduce, query);
        }catch(MapReduceException ex){
            logger.error(ex.getMessage(), ex);
        }
        DBObject result = results.iterator().next();
        DBObject dbo = (DBObject)result.get("value");
        return Double.parseDouble(dbo.get("value").toString());
    }
    
    public double sum(String key){
        return sum(key, new BasicDBObject());
    }
    
    public double sum(String key, BuguQuery query){
        return sum(key, query.getCondition());
    }
    
    private double sum(String key, DBObject query){
        if(!this.exists(query)){
            return 0;
        }
        StringBuilder map = new StringBuilder("function(){emit('");
        map.append(key);
        map.append("', {'value':this.");
        map.append(key);
        map.append("});}");
        String reduce = "function(key, values){var sum=0; for(var i=0;i<values.length; i++){sum+=values[i].value;} return {'value':sum}}";
        Iterable<DBObject> results = null;
        try{
            results = mapReduce(map.toString(), reduce, query);
        }catch(MapReduceException ex){
            logger.error(ex.getMessage(), ex);
        }
        DBObject result = results.iterator().next();
        DBObject dbo = (DBObject)result.get("value");
        return Double.parseDouble(dbo.get("value").toString());
    }
    
    public double average(String key){
        return average(key, new BasicDBObject());
    }
    
    public double average(String key, BuguQuery query){
        return average(key, query.getCondition());
    }
    
    private double average(String key, DBObject query){
        long count = coll.count(query);
        if(count == 0){
            return 0;
        }
        double sum = this.sum(key, query);
        return sum / count;
    }
    
    public Iterable<DBObject> mapReduce(MapReduceCommand cmd) throws MapReduceException {
        MapReduceOutput output = coll.mapReduce(cmd);
        CommandResult cr = output.getCommandResult();
        if(! cr.ok()){
            throw new MapReduceException(cr.getErrorMessage());
        }
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce) throws MapReduceException {
        MapReduceOutput output = coll.mapReduce(map, reduce, null, OutputType.INLINE, null);
        CommandResult cr = output.getCommandResult();
        if(! cr.ok()){
            throw new MapReduceException(cr.getErrorMessage());
        }
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, BuguQuery query) throws MapReduceException {
        return mapReduce(map, reduce, query.getCondition());
    }
    
    private Iterable<DBObject> mapReduce(String map, String reduce, DBObject query) throws MapReduceException {
        MapReduceOutput output = coll.mapReduce(map, reduce, null, OutputType.INLINE, query);
        CommandResult cr = output.getCommandResult();
        if(! cr.ok()){
            throw new MapReduceException(cr.getErrorMessage());
        }
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, BuguQuery query) throws MapReduceException {
        return mapReduce(map, reduce, outputTarget, outputType, orderBy, query.getCondition());
    }
    
    private synchronized Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, DBObject query) throws MapReduceException {
        MapReduceOutput output = coll.mapReduce(map, reduce, outputTarget, outputType, query);
        CommandResult cr = output.getCommandResult();
        if(! cr.ok()){
            throw new MapReduceException(cr.getErrorMessage());
        }
        DBCollection c = output.getOutputCollection();
        DBCursor cursor = null;
        if(orderBy != null){
            cursor = c.find().sort(MapperUtil.getSort(orderBy));
        }else{
            cursor = c.find();
        }
        List<DBObject> list = new ArrayList<DBObject>();
        for(Iterator<DBObject> it = cursor.iterator(); it.hasNext(); ){
            list.add(it.next());
        }
        return list;
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, int pageNum, int pageSize, BuguQuery query) throws MapReduceException {
        return mapReduce(map, reduce, outputTarget, outputType, orderBy, pageNum, pageSize, query.getCondition());
    }
    
    private synchronized Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, int pageNum, int pageSize, DBObject query) throws MapReduceException {
        MapReduceOutput output = coll.mapReduce(map, reduce, outputTarget, outputType, query);
        CommandResult cr = output.getCommandResult();
        if(! cr.ok()){
            throw new MapReduceException(cr.getErrorMessage());
        }
        DBCollection c = output.getOutputCollection();
        DBCursor cursor = null;
        if(orderBy != null){
            cursor = c.find().sort(MapperUtil.getSort(orderBy)).skip((pageNum-1)*pageSize).limit(pageSize);
        }else{
            cursor = c.find().skip((pageNum-1)*pageSize).limit(pageSize);
        }
        List<DBObject> list = new ArrayList<DBObject>();
        for(Iterator<DBObject> it = cursor.iterator(); it.hasNext(); ){
            list.add(it.next());
        }
        return list;
    }
    
    /**
     * Create an aggregation.
     * @return a new BuguQuery object
     */
    public BuguAggregation<T> aggregate(){
        return new BuguAggregation<T>(coll);
    }
    
    /**
     * Check if any entity match the condition
     * @param query the condition
     * @return 
     */
    private boolean exists(DBObject query){
        return coll.findOne(query) != null;
    }
    
}
