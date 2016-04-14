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

import com.bugull.mongo.utils.SortUtil;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Not used anymore. Please just use BuguDao.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Deprecated
public class AdvancedDao<T> extends BuguDao<T>{
    
    public AdvancedDao(Class<T> clazz){
        super(clazz);
    }
    
    public Iterable<DBObject> mapReduce(MapReduceCommand cmd) {
        MapReduceOutput output = coll.mapReduce(cmd);
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce) {
        MapReduceOutput output = coll.mapReduce(map, reduce, null, MapReduceCommand.OutputType.INLINE, null);
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, BuguQuery query) {
        return mapReduce(map, reduce, query.getCondition());
    }
    
    private Iterable<DBObject> mapReduce(String map, String reduce, DBObject query) {
        MapReduceOutput output = coll.mapReduce(map, reduce, null, MapReduceCommand.OutputType.INLINE, query);
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, BuguQuery query) {
        return mapReduce(map, reduce, outputTarget, outputType, orderBy, query.getCondition());
    }
    
    private synchronized Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, DBObject query) {
        MapReduceOutput output = coll.mapReduce(map, reduce, outputTarget, outputType, query);
        DBCollection c = output.getOutputCollection();
        DBCursor cursor;
        if(orderBy != null){
            cursor = c.find().sort(SortUtil.getSort(orderBy));
        }else{
            cursor = c.find();
        }
        List<DBObject> list = new ArrayList<DBObject>();
        for(Iterator<DBObject> it = cursor.iterator(); it.hasNext(); ){
            list.add(it.next());
        }
        return list;
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, int pageNum, int pageSize, BuguQuery query) {
        return mapReduce(map, reduce, outputTarget, outputType, orderBy, pageNum, pageSize, query.getCondition());
    }
    
    private synchronized Iterable<DBObject> mapReduce(String map, String reduce, String outputTarget, MapReduceCommand.OutputType outputType, String orderBy, int pageNum, int pageSize, DBObject query) {
        MapReduceOutput output = coll.mapReduce(map, reduce, outputTarget, outputType, query);
        DBCollection c = output.getOutputCollection();
        DBCursor cursor;
        if(orderBy != null){
            cursor = c.find().sort(SortUtil.getSort(orderBy)).skip((pageNum-1)*pageSize).limit(pageSize);
        }else{
            cursor = c.find().skip((pageNum-1)*pageSize).limit(pageSize);
        }
        List<DBObject> list = new ArrayList<DBObject>();
        for(Iterator<DBObject> it = cursor.iterator(); it.hasNext(); ){
            list.add(it.next());
        }
        return list;
    }
    
}
