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

package com.bugull.mongo.cache;

import com.bugull.mongo.access.InternalDao;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache(Map) holds dao instance.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class DaoCache {
    
    private final ConcurrentMap<String, SoftReference<InternalDao<?>>> cache = new ConcurrentHashMap<String, SoftReference<InternalDao<?>>>();
    
    private DaoCache(){
        
    }
    
    private static class Holder {
        final static DaoCache instance = new DaoCache();
    } 
    
    public static DaoCache getInstance(){
        return Holder.instance;
    }
    
    public <T> InternalDao<T> get(Class<T> clazz){
        String name = clazz.getName();
        InternalDao<T> dao;
        boolean recycled = false;
        SoftReference<InternalDao<?>> sr = cache.get(name);
        if(sr != null){
            dao = (InternalDao<T>)sr.get();
            if(dao == null){
                recycled = true;
            }else{
                return dao;
            }
        }
        //if not exists
        dao = new InternalDao<T>(clazz);
        sr = new SoftReference<InternalDao<?>>(dao);
        if(recycled){
            cache.put(name, sr);
            return dao;
        }else{
            SoftReference<InternalDao<?>> temp = cache.putIfAbsent(name, sr);
            if(temp != null){
                return (InternalDao<T>)temp.get();
            }else{
                return (InternalDao<T>)sr.get();
            }
        }
    }
    
}
