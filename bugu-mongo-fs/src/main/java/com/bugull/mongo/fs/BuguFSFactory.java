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

import com.mongodb.gridfs.GridFS;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory to create BuguFS instance. 
 * 
 * For performance sake, it uses a map to cache all the BuguFS object.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguFSFactory {
    
    private final ConcurrentMap<String, SoftReference<BuguFS>> cache = new ConcurrentHashMap<String, SoftReference<BuguFS>>();
    
    private static class Holder {
        final static BuguFSFactory instance = new BuguFSFactory();
    } 
    
    public static BuguFSFactory getInstance(){
        return Holder.instance;
    }
    
    public BuguFS create(){
        return create(GridFS.DEFAULT_BUCKET, GridFS.DEFAULT_CHUNKSIZE);
    }
    
    public BuguFS create(String bucketName){
        return create(bucketName, GridFS.DEFAULT_CHUNKSIZE);
    }
    
    public BuguFS create(long chunkSize){
        return create(GridFS.DEFAULT_BUCKET, chunkSize);
    }
    
    public BuguFS create(String bucketName, long chunkSize){
        SoftReference<BuguFS> sr = cache.get(bucketName);
        if(sr != null){
            return sr.get();
        }
        BuguFS fs = new BuguFS(bucketName, chunkSize);
        sr = new SoftReference<BuguFS>(fs);
        SoftReference<BuguFS> temp = cache.putIfAbsent(bucketName, sr);
        if(temp != null){
            return temp.get();
        }else{
            return sr.get();
        }
    }
    
}
