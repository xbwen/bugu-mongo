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

package com.bugull.mongo.lucene;

import com.bugull.mongo.BuguFramework;
import com.bugull.mongo.lucene.backend.IndexRebuildTask;

/**
 * Rebuild the lucene index for all entities of a collection.
 * 
 * The rebuild task is executed asynchronized, by the BuguFramework thread pool.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IndexRebuilder {
    
    private Class<?> clazz;
    private int batchSize = 100;  //the default batch size is 100
    
    public IndexRebuilder(Class<?> clazz){
        this.clazz = clazz;
    }
    
    public IndexRebuilder(Class<?> clazz, int batchSize){
        this.clazz = clazz;
        this.batchSize = batchSize;
    }
    
    public void rebuild(){
        IndexRebuildTask task = new IndexRebuildTask(clazz, batchSize);
        BuguFramework.getInstance().getExecutor().execute(task);
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }
    
}
