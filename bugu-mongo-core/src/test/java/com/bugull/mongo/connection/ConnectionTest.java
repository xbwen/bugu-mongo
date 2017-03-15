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

package com.bugull.mongo.connection;

import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.mongodb.MongoClientOptions;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ConnectionTest extends ReplicaSetBaseTest {
    
    //@Test
    public void test(){
        connectDB();
        
        //do query here
        
        disconnectDB();
    }
    
    @Test
    public void testWithOptions(){
        //set connection pool size to 20. the default is 10.
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(20).build();
        
        connectDBWithOptions(options);
        
        //do query here
        
        disconnectDB();
    }

}
