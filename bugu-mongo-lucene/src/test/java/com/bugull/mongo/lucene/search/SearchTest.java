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

package com.bugull.mongo.lucene.search;

import com.bugull.mongo.lucene.BuguParser;
import com.bugull.mongo.lucene.BuguSearcher;
import com.bugull.mongo.lucene.base.BaseTest;
import com.bugull.mongo.lucene.dao.ProductDao;
import com.bugull.mongo.lucene.entity.Product;
import java.util.List;
import org.apache.lucene.search.Query;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class SearchTest extends BaseTest {
    
    @Test
    public void test(){
        
        connectDB();
        
        ProductDao produectDao = new ProductDao();
        Product p1 = new Product();
        p1.setName("iPhone 6");
        p1.setDescription("iPhone 6 is the first choice for your mobile phone, and bala bala bala...");
        p1.setPrice(5321.5F);
        produectDao.save(p1);
        
        Product p2 = new Product();
        p2.setName("iPhone 6 Plus");
        p2.setDescription("iPhone 6 Plus is the second choice for your mobile phone, and bala bala bala...");
        p2.setPrice(6321.5F);
        produectDao.save(p2);
        
        try {
            //wait some time, for index reopen
            Thread.sleep(40L * 1000L);
        } catch (InterruptedException ex) {
            
        }
        
        Query query = BuguParser.parse("name", "iPhone");
        BuguSearcher<Product> searcher = new BuguSearcher(Product.class);
        List<Product> list = searcher.setQuery(query).search();
        for(Product p : list){
            System.out.println(p.getName());
            System.out.println(p.getPrice());
        }
        searcher.close();
        
        disconnectDB();
        
    }

}
