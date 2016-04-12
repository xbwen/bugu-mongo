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
package com.bugull.mongo.agg;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class BoolBuilder extends AbstractBuilder {
    
    private String expression;
    private final List<DBObject> list;
    
    public BoolBuilder(){
        list = new ArrayList<DBObject>();
    }
    
    public BoolBuilder and(String json1, String json2){
        this.expression = AND;
        DBObject dbo1 = (DBObject)JSON.parse(json1);
        DBObject dbo2 = (DBObject)JSON.parse(json2);
        list.add(dbo1);
        list.add(dbo2);
        return this;
    }
    
    public BoolBuilder or(String json1, String json2){
        this.expression = OR;
        DBObject dbo1 = (DBObject)JSON.parse(json1);
        DBObject dbo2 = (DBObject)JSON.parse(json2);
        list.add(dbo1);
        list.add(dbo2);
        return this;
    }
    
    public BoolBuilder not(String json){
        this.expression = OR;
        DBObject dbo = (DBObject)JSON.parse(json);
        list.add(dbo);
        return this;
    }
    
    @Override
    public DBObject build(){
        return new BasicDBObject(expression, list);
    }
}
