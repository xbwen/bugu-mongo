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

import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CategoryTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        CategoryDao dao = new CategoryDao();
        
        Category c1 = new Category();
        c1.setName("Books");
        c1.setOrder(1);
        dao.save(c1);
        
        Category c2 = new Category();
        c2.setName("Food");
        c2.setOrder(2);
        dao.save(c2);
        
        Category c3 = new Category();
        c3.setName("Computers");
        c3.setOrder(11);
        dao.save(c3);
        
        Category c4 = new Category();
        c4.setName("Phones");
        c4.setOrder(22);
        dao.save(c4);
        
        disconnectDB();
    }
    
    //@Test
    public void testQuery(){
        connectDB();
        
        BuguCache.getInstance().init();
        
        CategoryDao dao = new CategoryDao();
        
        long begin_1 = System.currentTimeMillis();
        List list1 = dao.getCacheData();
        long end_1 = System.currentTimeMillis();
        System.out.println("use time: " + (end_1 - begin_1));
        System.out.println("list size: " + list1.size());
        
        long begin_2 = System.currentTimeMillis();
        List list2 = dao.getCacheData();
        long end_2 = System.currentTimeMillis();
        System.out.println("use time: " + (end_2 - begin_2));
        System.out.println("list size: " + list2.size());
        
        BuguCache.getInstance().destroy();
        
        disconnectDB();
    }
    
    //@Test
    public void testStandaloneChanged() throws Exception {
        connectDB();
        
        BuguCache cache = BuguCache.getInstance();
        cache.init();
        
        CategoryDao dao = new CategoryDao();
        
        long begin_1 = System.currentTimeMillis();
        List list1 = dao.getCacheData();
        long end_1 = System.currentTimeMillis();
        System.out.println("use time: " + (end_1 - begin_1));
        System.out.println("list size: " + list1.size());
        
        //insert a new record
        Category c5 = new Category();
        c5.setName("Toys");
        c5.setOrder(33);
        dao.save(c5);
        
        Thread.sleep(3000);
        
        long begin_2 = System.currentTimeMillis();
        List list2 = dao.getCacheData();
        long end_2 = System.currentTimeMillis();
        System.out.println("use time: " + (end_2 - begin_2));
        System.out.println("list size: " + list2.size());
        
        cache.destroy();
        
        disconnectDB();
    }
    
    @Test
    public void testClusterChanged() throws Exception {
        connectDB();
        
        BuguCache cache = BuguCache.getInstance();
        //set zkConnectString, to identify the application is in cluster.
        //otherwise, it's standalone.
        cache.setZkConnectString("127.0.0.1:2181");
        cache.init();
        
        CategoryDao dao = new CategoryDao();
        
        long begin_1 = System.currentTimeMillis();
        List list1 = dao.getCacheData();
        long end_1 = System.currentTimeMillis();
        System.out.println("use time: " + (end_1 - begin_1));
        System.out.println("list size: " + list1.size());
        
        //insert a new record
        Category c5 = new Category();
        c5.setName("Toys");
        c5.setOrder(33);
        dao.save(c5);
        
        Thread.sleep(3000);
        
        long begin_2 = System.currentTimeMillis();
        List list2 = dao.getCacheData();
        long end_2 = System.currentTimeMillis();
        System.out.println("use time: " + (end_2 - begin_2));
        System.out.println("list size: " + list2.size());
        
        cache.destroy();
        
        disconnectDB();
    }
    
}
