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
package com.bugull.mongo.crud;

import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.entity.Product;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class NullValueTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        Product p1 = new Product();
        p1.setName("MI 3");
        p1.setPrice(1999F);
        productDao.save(p1);
        
        Product p2 = new Product();
        p2.setName("MI 5");
        p2.setDescription("MI 5");
        p2.setPrice(2399F);
        productDao.save(p2);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        ProductDao dao = new ProductDao();
        
//        List<Product> list = dao.query().is("description", null).results();
//        for(Product p : list){
//            System.out.println("name: " + p.getName());
//            System.out.println("description: " + p.getDescription());
//        }
        
        BuguQuery<Product> q1 = dao.query().notEquals("description", null);
        BuguQuery<Product> q2 = dao.query().notEquals("description", "MI 5");
        
        List<Product> list = dao.query().and(q1, q2).results();
        for(Product p : list){
            System.out.println("name: " + p.getName());
            System.out.println("description: " + p.getDescription());
        }
        
        disconnectDB();
    }
    
}
