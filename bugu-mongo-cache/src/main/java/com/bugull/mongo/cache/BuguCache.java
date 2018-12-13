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

import com.bugull.mongo.utils.StringUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

/**
 * Singleton object, which to configure cache behavior and hold cache data. 
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguCache {
    
    public final static String ZK_PREFIX = "/bugu:cache:";
    
    private String zkConnectString;
    private CuratorFramework zkClient;
    
    private final Map<String, List> data = new ConcurrentHashMap<>();
    
    private BuguCache(){
        
    }
    
    private static class Holder {
        final static BuguCache instance = new BuguCache();
    } 
    
    public static BuguCache getInstance(){
        return Holder.instance;
    }
    
    public void init(){
        if(StringUtil.isEmpty(zkConnectString)){
            return;
        }
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zkConnectString, retryPolicy);
        zkClient.start();
        try {
            zkClient.blockUntilConnected();
        } catch (InterruptedException ex) {
            throw new BuguCacheException(ex.getMessage());
        }
    }
    
    public void destroy(){
        if(StringUtil.isEmpty(zkConnectString)){
            return;
        }
        if(zkClient != null){
            CloseableUtils.closeQuietly(zkClient);
        }
    }

    public List getValue(String key) {
        return data.get(key);
    }

    public void setValue(String key, List value) {
        data.put(key, value);
    }
    
    public void setZkConnectString(String zkConnectString) {
        this.zkConnectString = zkConnectString;
    }

    public CuratorFramework getZkClient() {
        return zkClient;
    }
    
}
