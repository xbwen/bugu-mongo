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

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.utils.ThreadUtil;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Things used by framework internally.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguFramework {
    
    private final Map<String, BuguConnection> map = new ConcurrentHashMap<String, BuguConnection>();
    
    private ExecutorService executor;
    
    private int threadPoolSize;
    
    private BuguFramework(){
        if(threadPoolSize == 0){
            //default thread pool size: 2 * cpu + 1
            threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        }
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }
    
    private static class Holder {
        final static BuguFramework instance = new BuguFramework();
    } 
    
    public static BuguFramework getInstance(){
        return Holder.instance;
    }
    
    public BuguConnection createConnection(){
        return createConnection(Default.NAME);
    }
    
    public synchronized BuguConnection createConnection(String name){
        BuguConnection connection = map.get(name);
        if(connection == null){
            connection = new DefaultConnection();
            map.put(name, connection);
        }
        return connection;
    }

    public BuguConnection getConnection() {
        return map.get(Default.NAME);
    }
    
    public BuguConnection getConnection(String name) {
        return map.get(name);
    }

    public ExecutorService getExecutor() {
        return executor;
    }
    
    public void setThreadPoolSize(int threadPoolSize){
        this.threadPoolSize = threadPoolSize;
    }
    
    /**
     * destroy the framework, release all resource.
     */
    public void destroy(){
        //close the thread pool
        ThreadUtil.safeClose(executor);
        
        //close all the mongoDB connection
        Set<Entry<String, BuguConnection>> set = map.entrySet();
        for(Entry<String, BuguConnection> entry : set){
            BuguConnection conn = entry.getValue();
            conn.close();
        }
    }

}
