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

package com.bugull.mongo.base;

import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class BaseTest {
    
    protected void connectDB(){
        BuguConnection conn = BuguFramework.getInstance().createConnection();
        conn.setHost("127.0.0.1");
        conn.setPort(27017);
        conn.setUsername("test");
        conn.setPassword("test");
        conn.setDatabase("test");
        conn.connect();
    }
    
    protected void disconnectDB(){
        BuguFramework.getInstance().destroy();
    }

}
