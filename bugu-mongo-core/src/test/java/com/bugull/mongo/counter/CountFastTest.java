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
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import static com.mongodb.client.model.Aggregates.count;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CountFastTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        for(int i=0; i<1000000; i++){
            Article article = new Article();
            article.setTitle(UUID.randomUUID().toString());
            article.setScore(Math.random());
            dao.save(article);
        }
        
        disconnectDB();
        
        System.out.println("done!");
    }
    
    //@Test
    public void testInsertMore(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        for(int i=0; i<100; i++){
            Article article1 = new Article();
            article1.setTitle("5394bb45-a316-4f7b-8d5c-9648e412209c");
            article1.setScore(0.09567830121026977D);
            dao.save(article1);
            
            Article article2 = new Article();
            article2.setTitle("0c91f91c-0a41-42e2-b305-88489ce9aded");
            article2.setScore(0.749565940706794D);
            dao.save(article2);
            
            Article article3 = new Article();
            article3.setTitle("a70db806-0587-41f3-b3bd-505043bbd612");
            article3.setScore(0.29770944623277573D);
            dao.save(article3);
        }
        
        disconnectDB();
        
        System.out.println("done!");
    }
    
    @Test
    public void testDistinctLarge(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        //common count
        long begin = System.currentTimeMillis();
        List allTitle = dao.query().greaterThan("score", 0.1).distinctLarge("title");
        long end = System.currentTimeMillis();
        System.out.println("result: " + allTitle.size() + "  use time: " + (end - begin));
        
        disconnectDB();
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
    
    //@Test
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
