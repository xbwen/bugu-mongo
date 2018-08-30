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

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CompareBuilder implements Builder {
    
    private static final String EQ = "$eq";
    private static final String NE = "$ne";
    private static final String GT = "$gt";
    private static final String GTE = "$gte";
    private static final String LT = "$lt";
    private static final String LTE = "$lte";
    
    private final DBObject dbo;
    
    public CompareBuilder(){
        dbo = new BasicDBObject();
    }
    
    public CompareBuilder eq(String attr1, String attr2){
        dbo.put(EQ, new String[]{attr1, attr2});
        return this;
    }
    
    public CompareBuilder notEquals(String attr1, String attr2){
        dbo.put(NE, new String[]{attr1, attr2});
        return this;
    }
    
    public CompareBuilder greaterThan(String attr1, String attr2){
        dbo.put(GT, new String[]{attr1, attr2});
        return this;
    }
    
    public CompareBuilder greaterThanEquals(String attr1, String attr2){
        dbo.put(GTE, new String[]{attr1, attr2});
        return this;
    }
    
    public CompareBuilder lessThan(String attr1, String attr2){
        dbo.put(LT, new String[]{attr1, attr2});
        return this;
    }
    
    public CompareBuilder lessThanEquals(String attr1, String attr2){
        dbo.put(LTE, new String[]{attr1, attr2});
        return this;
    }

    @Override
    public DBObject build() {
        return dbo;
    }
    
}
