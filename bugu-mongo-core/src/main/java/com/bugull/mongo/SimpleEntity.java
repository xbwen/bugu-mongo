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

import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.utils.StringUtil;
import java.io.Serializable;
import org.bson.types.ObjectId;

/**
 * A simple implementation of BuguEntity. 
 * <p>
 * SimpleEntity is convenient for use: <br/>
 * 1. It contains the id field, and getId(), setId() method.<br/>
 * 2. It contains getTimestamp() method, telling when the document is inserted into mongoDB.<br/>
 * 3. It implements equals(), hashCode(), and toString() method. <br/>
 * 4. It is serializable. <br/>
 * </p>
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class SimpleEntity implements BuguEntity, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    protected String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Get the time when the document is created, in milliseconds.
     * @return 
     */
    public long getTimestamp() {
        if(StringUtil.isEmpty(id)){
            return -1;
        }
        ObjectId oid = new ObjectId(id);
        return oid.getTimestamp() * 1000L;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SimpleEntity)) {
            return false;
        }
        SimpleEntity o = (SimpleEntity)other;
        String oid = o.getId();
        if( StringUtil.isEmpty(id) || StringUtil.isEmpty(oid) ){
            return false;
        }
        return id.equalsIgnoreCase(oid);
    }
    
    @Override
    public int hashCode() {
        if(StringUtil.isEmpty(id)){
            return -1;
        }
        return new ObjectId(id).hashCode();
    }
    
    @Override
    public String toString(){
        return id;
    }

}
