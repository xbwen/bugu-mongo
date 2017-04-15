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

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.MapperUtil;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;

/**
 * DAO that can cache data.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CacheableDao <T> extends BuguDao<T> {

    private BuguQuery<T> cacheQuery;
    
    public CacheableDao(Class<T> clazz){
        super(clazz);
        super.addEntityListener(new DataChangeListener(this));
        BuguCache cache = BuguCache.getInstance();
        CuratorFramework zkClient = cache.getZkClient();
        if(zkClient != null){
            String path = BuguCache.ZK_PREFIX + MapperUtil.getEntityName(clazz);
            //create node if not exists
            try {
                if(zkClient.checkExists().forPath(path) == null){
                    zkClient.create().withMode(CreateMode.PERSISTENT).forPath(path);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            //create and start NodeCache
            NodeCache nodeCache = new NodeCache(zkClient, path);
            try {
                nodeCache.start();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
            //add NodeCache listener
            nodeCache.getListenable().addListener(new NodeCacheListener(){
                @Override
                public void nodeChanged() throws Exception {
                    reloadCacheData();
                }
            });
        }
    }

    protected void setCacheQuery(BuguQuery<T> cacheQuery) {
        this.cacheQuery = cacheQuery;
    }
    
    /**
     * Get data from cache. If not exists, will query from database.
     * @return 
     */
    public List<T> getCacheData(){
        BuguCache cache = BuguCache.getInstance();
        String key = MapperUtil.getEntityName(clazz);
        List<T> value = (List<T>)cache.getValue(key);
        if(value == null){
            if(cacheQuery == null){
                value = this.findAll();
            }else{
                value = cacheQuery.results();
            }
            cache.setValue(key, value);
        }
        return value;
    }
    
    /**
     * used in DataChangeListener, call this to refresh cache data.
     */
    public void dataChanged(){
        BuguCache cache = BuguCache.getInstance();
        CuratorFramework zkClient = cache.getZkClient();
        //process single JVM
        if(zkClient == null){
            reloadCacheData();
        }
        //process cluster JVM, by ZooKeeper/curator
        else{
            //set new value to ZK node, in order to trigger nodeChanged event
            String path = BuguCache.ZK_PREFIX + MapperUtil.getEntityName(clazz);
            String data = String.valueOf(System.currentTimeMillis());
            try {
                zkClient.setData().inBackground().forPath(path, data.getBytes());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
    
    private void reloadCacheData(){
        BuguCache cache = BuguCache.getInstance();
        String key = MapperUtil.getEntityName(clazz);
        List<T> value = null;
        if(cacheQuery == null){
            value = this.findAll();
        }else{
            value = cacheQuery.results();
        }
        cache.setValue(key, value);
    }
    
}
