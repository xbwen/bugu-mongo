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

import com.bugull.mongo.utils.ThreadUtil;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguFramework {
    
    private ExecutorService executor = null;
    
    private BuguFramework(){
        //default thread pool size: 2 * cpu + 1
        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }
    
    private static class Holder {
        final static BuguFramework instance = new BuguFramework();
    } 
    
    public static BuguFramework getInstance(){
        return Holder.instance;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
    
    public void destroy(){
        ThreadUtil.safeClose(executor);
    }

}
