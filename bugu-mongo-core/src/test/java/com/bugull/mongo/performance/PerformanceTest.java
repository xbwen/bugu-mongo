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
package com.bugull.mongo.performance;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.entity.Order;
import com.bugull.mongo.entity.User;
import java.util.Date;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class PerformanceTest extends ReplicaSetBaseTest{
    
    //@Test
    public void testForProfile() {
        connectDB();
        
        BuguDao<Order> dao = DaoCache.getInstance().get(Order.class);
        for(int i=0; i<60; i++){
            List<Order> list = dao.findAll();
            try{
                Thread.sleep(1000);
            }catch(Exception ex){
                
            }
        }
        
        disconnectDB();
    }
    
    /**
     * update operation use currentDate() and set() with new Date have no difference.
     */
    @Test
    public void testCurrentDate(){
        connectDB();
        
        BuguDao<User> dao = DaoCache.getInstance().get(User.class);
        
        long begin1 = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            dao.update().currentDate("registerTime").execute("5d7216f9661c3c6c1f83b943");
        }
        long end1 = System.currentTimeMillis();
        System.out.println("use: " + (end1 - begin1));
        
        long begin2 = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            dao.update().set("registerTime", new Date()).execute("5d7216f9661c3c6c1f83b943");
        }
        long end2 = System.currentTimeMillis();
        System.out.println("use: " + (end2 - begin2));
        
        disconnectDB();
    }
    
}
