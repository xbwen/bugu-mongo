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

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.counter.Article;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.Product;
import com.bugull.mongo.entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class UpdateTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testUpdate(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        Product product = productDao.query().is("name", "iPhone 6").result();
        product.setPrice(5000F);
        productDao.save(product);
        
        UserDao userDao = new UserDao();
        User user = userDao.query().is("username", "frank").result();
        System.out.println("user:" + user.getUsername());
        userDao.update().set("username", "frank...").inc("age", 1).execute(user);
        
        OrderDao orderDao = new OrderDao();
        orderDao.update().inc("money", -500).execute();
        
        disconnectDB();
    }
    
    //@Test
    public void testUpdateListValue(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        Product product1 = productDao.findOne("56fe36705cad6e1e24dd1cda");
        Product product2 = productDao.findOne("56fe36705cad6e1e24dd1cdb");
        List<Product> list = new ArrayList<Product>();
        list.add(product1);
        list.add(product2);
        
        OrderDao orderDao = new OrderDao();
        orderDao.update().set("productList", list).execute("56f499725cad6e05e437fa17");
        
        disconnectDB();
    }
    
    //@Test
    public void testSetManyFields(){
        connectDB();
        
        UserDao userDao = new UserDao();
        User user = userDao.query().is("username", "frank").result();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", "Franky");
        map.put("age", 30);
        map.put("valid", false);
        userDao.update().set(map).execute(user);
        
        disconnectDB();
    }
    
    //@Test
    public void testUnsetManyFields(){
        connectDB();
        
        UserDao userDao = new UserDao();
        User user = userDao.query().is("username", "Franky").result();
        userDao.update().unset("registerTime", "contact").execute(user);
        
        disconnectDB();
    }
    
    //@Test
    public void testInc(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        dao.update().inc("score", 0.2).execute("5bffd2a98a7e29103a595392");
        
        disconnectDB();
    }
    
    @Test
    public void testDec(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        dao.update().dec("score", 2).execute("5bffd2a98a7e29103a595392");
        
        disconnectDB();
    }
    
    //@Test
    public void testMul(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        dao.update().mul("score", 2).execute("5bffd2a98a7e29103a595392");
        
        disconnectDB();
    }

}
