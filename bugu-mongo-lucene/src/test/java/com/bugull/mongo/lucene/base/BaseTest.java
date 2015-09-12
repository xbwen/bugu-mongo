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

package com.bugull.mongo.lucene.base;

import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.lucene.BuguIndex;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class BaseTest {
    
    protected void connectDB(){
        BuguConnection conn = BuguConnection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPort(27017);
        conn.setUsername("test");
        conn.setPassword("test");
        conn.setDatabase("test");
        conn.connect();
        
        BuguIndex index = BuguIndex.getInstance();

        index.setDirectoryPath("/Users/frankwen/Temp/lucene_index/");
        Version version = index.getVersion();
        index.setAnalyzer(new StandardAnalyzer(version));
        index.setIndexReopenPeriod(30L * 1000L);

        index.open();
    }
    
    protected void disconnectDB(){
        BuguIndex.getInstance().close();
        BuguConnection.getInstance().close();
    }

}
