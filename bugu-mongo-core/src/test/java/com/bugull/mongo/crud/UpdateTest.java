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

import com.bugull.mongo.base.BaseTest;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.Product;
import com.bugull.mongo.entity.User;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class UpdateTest extends BaseTest {
    
    @Test
    public void testUpdate(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        Product product = productDao.query().is("name", "iPhone 6").result();
        product.setPrice(5000F);
        productDao.save(product);
        
        UserDao userDao = new UserDao();
        User user = userDao.query().is("username", "frank").result();
        userDao.update().set("username", "franky").inc("age", 1).execute(user);
        
        OrderDao orderDao = new OrderDao();
        orderDao.update().inc("money", -500).execute();
        
        disconnectDB();
    }

}
