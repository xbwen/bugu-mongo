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
import com.bugull.mongo.entity.Address;
import com.bugull.mongo.entity.Contact;
import com.bugull.mongo.entity.Order;
import com.bugull.mongo.entity.Product;
import com.bugull.mongo.entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class InsertTest extends BaseTest {
    
    @Test
    public void testInsert(){
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
        
        UserDao userDao = new UserDao();
        User user = new User();
        user.setUsername("frank");
        user.setAge(30);
        user.setValid(true);
        user.setRegisterTime(new Date());
        Contact contact = new Contact();
        contact.setEmail("xiaobinwen@gmail.com");
        contact.setPhone("13600010002");
        user.setContact(contact);
        List<Address> addressList = new ArrayList<Address>();
        Address addr1 = new Address();
        addr1.setProvince("Zhejiang");
        addr1.setCity("Ningbo");
        addr1.setDetailAddress("Jiangnan Road");
        addressList.add(addr1);
        Address addr2 = new Address();
        addr2.setProvince("Hainan");
        addr2.setCity("Haikou");
        addr2.setDetailAddress("Binhai Road");
        addressList.add(addr2);
        user.setAddressList(addressList);
        userDao.save(user);
        
        OrderDao orderDao = new OrderDao();
        Order order = new Order();
        List<Product> productList = new ArrayList<Product>();
        productList.add(p1);
        productList.add(p2);
        order.setProductList(productList);
        order.setUser(user);
        order.setMoney(9999.9);
        orderDao.save(order);
        
        disconnectDB();
    }

}
