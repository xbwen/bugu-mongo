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

package com.bugull.mongo.fs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

/**
 * Count the number of threads that accessing file in GridFS.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class AccessCount {
    
    private final ConcurrentMap<String, Semaphore> map = new ConcurrentHashMap<String, Semaphore>();
    
    private static class Holder {
        final static AccessCount instance = new AccessCount();
    } 
    
    public static AccessCount getInstance(){
        return Holder.instance;
    }
    
    public Semaphore getSemaphore(String resourceName, int maxAccess){
        Semaphore semaphore = map.get(resourceName);
        if(semaphore != null){
            return semaphore;
        }
        semaphore = new Semaphore(maxAccess);
        Semaphore temp = map.putIfAbsent(resourceName, semaphore);
        if(temp != null){
            return temp;
        }else{
            return semaphore;
        }
    }
    
    /**
     * Get the available count allowed to access.
     * @param resourceName
     * @return 
     */
    public int getAvailablePermits(String resourceName){
        int count = Integer.MAX_VALUE;
        Semaphore semaphore = map.get(resourceName);
        if(semaphore != null){
            count = semaphore.availablePermits();
        }
        return count;
    }
    
    /**
     * Get the waiting queue length. This is an estimate value.
     * @param resourceName
     * @return 
     */
    public int getQueueLength(String resourceName){
        int count = 0;
        Semaphore semaphore = map.get(resourceName);
        if(semaphore != null){
            count = semaphore.getQueueLength();
        }
        return count;
    }

}
