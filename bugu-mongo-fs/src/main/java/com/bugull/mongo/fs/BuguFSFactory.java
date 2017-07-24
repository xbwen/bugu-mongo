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

import com.bugull.mongo.annotations.Default;
import com.mongodb.gridfs.GridFS;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory to create BuguFS instance. 
 * 
 * Create a GridFS/BuguFS object is slow and expensive. For performance sake, we use a map to cache all the BuguFS object.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguFSFactory {
    
    private final ConcurrentMap<String, SoftReference<BuguFS>> cache = new ConcurrentHashMap<String, SoftReference<BuguFS>>();
    
    private BuguFSFactory(){
        
    }
    
    private static class Holder {
        final static BuguFSFactory instance = new BuguFSFactory();
    } 
    
    public static BuguFSFactory getInstance(){
        return Holder.instance;
    }
    
    public BuguFS createWithConnection(String connectionName){
        return create(connectionName, GridFS.DEFAULT_BUCKET, GridFS.DEFAULT_CHUNKSIZE);
    }
    
    public BuguFS create(){
        return create(GridFS.DEFAULT_BUCKET, GridFS.DEFAULT_CHUNKSIZE);
    }
    
    public BuguFS create(String bucket){
        return create(bucket, GridFS.DEFAULT_CHUNKSIZE);
    }
    
    public BuguFS create(int chunkSize){
        return create(GridFS.DEFAULT_BUCKET, chunkSize);
    }
    
    public BuguFS create(String bucket, int chunkSize){
        return create(Default.NAME, bucket, chunkSize);
    }
    
    public BuguFS create(String connectionName, String bucket, int chunkSize){
        BuguFS fs;
        boolean recycled = false;
        String key = connectionName + ":" + bucket;
        SoftReference<BuguFS> sr = cache.get(key);
        if(sr != null){
            fs = sr.get();
            if(fs == null){
                recycled = true;
            }else{
                return fs;
            }
        }
        //if not exists
        fs = new BuguFS(connectionName, bucket, chunkSize);
        sr = new SoftReference<BuguFS>(fs);
        if(recycled){
            cache.putIfAbsent(key, sr);
            return fs;
        }else{
            SoftReference<BuguFS> temp = cache.putIfAbsent(key, sr);
            if(temp != null){
                return temp.get();
            }else{
                return sr.get();
            }
        }
    }
    
}
