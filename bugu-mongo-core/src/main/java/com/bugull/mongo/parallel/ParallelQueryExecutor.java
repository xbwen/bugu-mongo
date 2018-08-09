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
package com.bugull.mongo.parallel;

import com.bugull.mongo.utils.ThreadUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ParallelQueryExecutor {
    
    private final static Logger logger = LogManager.getLogger(ParallelQueryExecutor.class.getName());
    
    /**
     * Execute BuguQuery or BuguAggregation in parallel.
     * @param querys
     * @return
     */
    public List<Iterable> execute(Parallelable... querys) {
        List<ParallelTask> taskList = new ArrayList<ParallelTask>();
        for(Parallelable query : querys){
            taskList.add(new ParallelTask(query));
        }
        int len = querys.length;
        if(len <= 1){
            logger.warn("You should NOT use parallel query when only one query!");
        }
        int max = Runtime.getRuntime().availableProcessors() * 2 + 1;
        if(len > max){
            len = max;
        }
        ExecutorService es = Executors.newFixedThreadPool(len);
        List<Iterable> result = new ArrayList<Iterable>();
        try{
            List<Future<Iterable>> futureList = es.invokeAll(taskList);
            for(Future<Iterable> future : futureList){
                result.add(future.get());
            }
        }catch(InterruptedException ie){
            logger.error(ie.getMessage(), ie);
        }catch(ExecutionException ee){
            logger.error(ee.getMessage(), ee);
        }finally{
            ThreadUtil.safeClose(es);
        }
        return result;
    }
    
}
