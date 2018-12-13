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

import com.mongodb.DBCollection;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class AbstractDao {
    
    protected boolean split;
    
    private DBCollection coll;
    
    private static final ThreadLocal<DBCollection> local = new ThreadLocal<>();
    
    protected void setCollection(DBCollection coll) {
        if(split){
            local.set(coll);
        }else{
            this.coll = coll;
        }
    }

    public DBCollection getCollection() {
        if(split){
            return local.get();
        }else{
            return coll;
        }
    }
    
}
