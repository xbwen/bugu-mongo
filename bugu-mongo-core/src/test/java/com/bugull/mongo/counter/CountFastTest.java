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
package com.bugull.mongo.counter;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.BaseTest;
import com.bugull.mongo.cache.DaoCache;
import java.util.UUID;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CountFastTest extends BaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        for(int i=0; i<5000; i++){
            Article article = new Article();
            article.setTitle(UUID.randomUUID().toString());
            article.setScore(Math.random());
            dao.save(article);
        }
        
        disconnectDB();
        
        System.out.println("done!");
    }
    
    //@Test
    public void testCountFast(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        //common count
        long begin = System.currentTimeMillis();
        long count = dao.count();
        long end = System.currentTimeMillis();
        System.out.println("result: " + count + "  use time: " + (end - begin));
        
        //fast count
        long begin2 = System.currentTimeMillis();
        long count2 = dao.countFast();
        long end2 = System.currentTimeMillis();
        System.out.println("result: " + count2 + "  use time: " + (end2 - begin2));
        
        disconnectDB();
    }
    
    //@Test
    public void testKVCountFast(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        //common count
        long begin = System.currentTimeMillis();
        long count = dao.count("score", 0.2);
        long end = System.currentTimeMillis();
        System.out.println("result: " + count + "  use time: " + (end - begin));
        
        //fast count
        long begin2 = System.currentTimeMillis();
        long count2 = dao.countFast("score", 0.2);
        long end2 = System.currentTimeMillis();
        System.out.println("result: " + count2 + "  use time: " + (end2 - begin2));
        
        disconnectDB();
    }
    
    @Test
    public void testQueryCountFast(){
        
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        //common count
        long begin = System.currentTimeMillis();
        long count = dao.query().greaterThan("score", 0.1).count();
        long end = System.currentTimeMillis();
        System.out.println("result: " + count + "  use time: " + (end - begin));
        
        //fast count
        long begin2 = System.currentTimeMillis();
        long count2 = dao.query().greaterThan("score", 0.1).countFast();
        long end2 = System.currentTimeMillis();
        System.out.println("result: " + count2 + "  use time: " + (end2 - begin2));
        
        disconnectDB();
        
    }
    
}
