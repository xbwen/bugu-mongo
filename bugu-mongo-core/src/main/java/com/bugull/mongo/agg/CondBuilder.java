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

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CondBuilder extends AbstractBuilder {
    
    public CondBuilder ifCondition(DBObject ifObj){
        dbo.put(IF, ifObj);
        return this;
    }
    
    public CondBuilder ifCondition(String json){
        DBObject ifObj = (DBObject)JSON.parse(json);
        dbo.put(IF, ifObj);
        return this;
    }
    
    public CondBuilder thenValue(Object value){
        dbo.put(THEN, value);
        return this;
    }
    
    public CondBuilder elseValue(Object value){
        dbo.put(ELSE, value);
        return this;
    }
    
    public CondBuilder(){
        this.expression = COND;
    }
    
}
