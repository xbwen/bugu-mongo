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

package com.bugull.mongo.aggregation;

import com.bugull.mongo.BuguAggregation;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.agg.ExpressionBuilder;
import com.bugull.mongo.agg.Lookup;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class AggregationTest extends ReplicaSetBaseTest {
    
    /**
     * insert records, for aggregate operation.
     */
    //@Test
    public void testInsert(){
        connectDB();
        
        BookDao bookDao = new BookDao();
        CommentDao commentDao = new CommentDao();
        CoolCommentDao ccDao = new CoolCommentDao();
        
        Book b1 = new Book();
        b1.setTitle("About Java");
        b1.setAuthor("Frank");
        b1.setTags(new String[]{"Java", "Programming"});
        b1.setPrice(5F);
        b1.setPublishDate(new Date());
        bookDao.save(b1);
        
        Comment c1 = new Comment();
        c1.setTitle("About Java");
        c1.setStar(5);
        commentDao.save(c1);
        
        Comment c2 = new Comment();
        c2.setTitle("About Java");
        c2.setStar(4);
        commentDao.save(c2);
        
        Comment c3 = new Comment();
        c3.setTitle("About Java");
        c3.setStar(3);
        commentDao.save(c3);
        
        CoolComment cc1 = new CoolComment();
        cc1.setBook(b1);
        cc1.setStar(5);
        ccDao.save(cc1);
        
        CoolComment cc2 = new CoolComment();
        cc2.setBook(b1);
        cc2.setStar(4);
        ccDao.save(cc2);
        
        CoolComment cc3 = new CoolComment();
        cc3.setBook(b1);
        cc3.setStar(3);
        ccDao.save(cc3);
        
        Book b2 = new Book();
        b2.setTitle("About C++");
        b2.setAuthor("Frank");
        b2.setTags(new String[]{"C++", "Programming"});
        b2.setPrice(9F);
        b2.setPublishDate(new Date());
        bookDao.save(b2);
        
        Book b3 = new Book();
        b3.setTitle("About Android");
        b3.setAuthor("Tom");
        b3.setTags(new String[]{"Android", "Java", "C++", "Programming"});
        b3.setPrice(15F);
        b3.setPublishDate(new Date());
        bookDao.save(b3);
        
        Comment c4 = new Comment();
        c4.setTitle("About Android");
        c4.setStar(4);
        commentDao.save(c4);
        
        Comment c5 = new Comment();
        c5.setTitle("About Android");
        c5.setStar(3);
        commentDao.save(c5);
        
        CoolComment cc4 = new CoolComment();
        cc4.setBook(b3);
        cc4.setStar(4);
        ccDao.save(cc4);
        
        CoolComment cc5 = new CoolComment();
        cc5.setBook(b3);
        cc5.setStar(3);
        ccDao.save(cc5);
        
        Book b4 = new Book();
        b4.setTitle("About iPhone");
        b4.setAuthor("Jessica");
        b4.setTags(new String[]{"iPhone", "Objective-C", "Programming"});
        b4.setPrice(20F);
        b4.setPublishDate(new Date());
        bookDao.save(b4);
        
        Book b5 = new Book();
        b5.setTitle("About Network");
        b5.setAuthor("Tom");
        b5.setTags(new String[]{"Network"});
        b5.setPrice(25F);
        b5.setPublishDate(new Date());
        bookDao.save(b5);
        
        Comment c6 = new Comment();
        c6.setTitle("About Network");
        c6.setStar(5);
        commentDao.save(c6);
        
        CoolComment cc6 = new CoolComment();
        cc6.setBook(b5);
        cc6.setStar(5);
        ccDao.save(cc6);
        
        disconnectDB();
    }
    
    /**
     * test the basic aggregate operation.
     */
    @Test
    public void testBasic(){
        connectDB();
        
        BookDao dao = new BookDao();
        BuguQuery query = dao.query().regex("title", "About");
        
        double maxValue = dao.max("price", query);
        System.out.println("max:" + maxValue);
        
        double minValue = dao.min("price", query);
        System.out.println("min:" + minValue);
        
        double sumValue = dao.sum("price", query);
        System.out.println("sum:" + sumValue);
        
        double avgValue = dao.average("price", query);
        System.out.println("average: " + avgValue);
        
        disconnectDB();
    }
    
    /**
     * group by author, order by books count
     */
    //@Test
    public void testSum(){
        connectDB();
        
        BookDao dao = new BookDao();
        
        BuguAggregation agg = dao.aggregate();
        agg.group("{_id:'$author', count:{$sum:1}}");
        agg.sort("{count:-1}");
        Iterable<DBObject> it = agg.results();
        for(DBObject dbo : it){
            System.out.println(dbo.get("_id"));
            System.out.println(dbo.get("count"));
        }
        disconnectDB();
    }
    
    /**
     * sum the total price of books contain tag 'Programming'
     */
    //@Test
    public void testUnwind(){
        connectDB();
        
        BookDao dao = new BookDao();
        
        BuguAggregation agg = dao.aggregate();
        agg.unwind("$tags");
        agg.match("tags", "Programming");
        agg.group("{_id:null, total:{$sum:'$price'}}");
        Iterable<DBObject> it = agg.results();
        for(DBObject dbo : it){
            System.out.println(dbo.get("total"));
        }
        
        disconnectDB();
    }
    
    /**
     * calculate average star of eache author.
     */
    //@Test
    public void testLookup(){
        connectDB();
        
        BookDao dao = new BookDao();
        
        BuguAggregation agg = dao.aggregate();
        agg.lookup(new Lookup("comment", "title", "title", "book_comment"));
        agg.unwind("$book_comment");
        agg.group("{_id:'$author', averageStar:{$avg:'$book_comment.star'}}");
        agg.sort("{averageStar:-1}");
        Iterable<DBObject> it = agg.results();
        for(DBObject dbo : it){
            System.out.println(dbo.get("_id"));
            System.out.println(dbo.get("averageStar"));
        }
        
        disconnectDB();
    }
    
    /**
     * calculate average star of eache author.
     */
    //@Test
    public void testLookupById(){
        connectDB();
        
        BookDao dao = new BookDao();
        
        BuguAggregation agg = dao.aggregate();
        agg.lookup(new Lookup("coolcomment", "_id", "book", "book_comment"));
        agg.unwind("$book_comment");
        agg.group("{_id:'$author', averageStar:{$avg:'$book_comment.star'}}");
        agg.sort("{averageStar:-1}");
        Iterable<DBObject> it = agg.results();
        for(DBObject dbo : it){
            System.out.println(dbo.get("_id"));
            System.out.println(dbo.get("averageStar"));
        }
        
        disconnectDB();
    }
    
    /**
     * mark the book as cheap or expensive.
     */
    //@Test
    public void testCond(){
        connectDB();
        
        BookDao dao = new BookDao();
        
        BuguAggregation agg = dao.aggregate();
        DBObject cond = ExpressionBuilder.cond().ifCondition("{'$lt':['$price', 10]}").thenValue("cheap").elseValue("expensive").build();
        DBObject p = new BasicDBObject();
        p.put("title", 1);
        p.put("price", cond);
        Iterable<DBObject> it = agg.project(p).results();
        for(DBObject dbo : it){
            System.out.print(dbo.get("title"));
            System.out.print(" : ");
            System.out.println(dbo.get("price"));
        }

        disconnectDB();
    }
    
    /**
     * get the count of books by price. divide into several groups: 
     * 1-10
     * 10-20
     * 20-30
     * ...
     */
    //@Test
    public void testComplexCond(){
        connectDB();
        
        BookDao dao = new BookDao();
        
        BuguAggregation agg = dao.aggregate();
        
        DBObject cond1 = ExpressionBuilder.cond().ifCondition("{'$lte':['$price', 10]}").thenValue(10).elseValue("$price").build();
        
        DBObject bool2 = ExpressionBuilder.bool().and("{'$gt':['$price', 10]}", "{'$lte':['$price', 20]}").build();
        DBObject cond2 = ExpressionBuilder.cond().ifCondition(bool2).thenValue(20).elseValue("$price").build();
        
        DBObject bool3 = ExpressionBuilder.bool().and("{'$gt':['$price', 20]}", "{'$lte':['$price', 30]}").build();
        DBObject cond3 = ExpressionBuilder.cond().ifCondition(bool3).thenValue(30).elseValue("$price").build();
        
        agg.project("price", cond1);
        agg.project("price", cond2);
        agg.project("price", cond3);
        
        agg.group("{_id:'$price', count:{$sum:1}}");
        
        Iterable<DBObject> it = agg.results();
        for(DBObject dbo : it){
            System.out.print(dbo.get("_id"));
            System.out.print(" : ");
            System.out.println(dbo.get("count"));
        }
        
        disconnectDB();
    }

}
