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

import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.GroupContactDao;
import com.bugull.mongo.dao.GroupProductDao;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.Address;
import com.bugull.mongo.entity.Contact;
import com.bugull.mongo.entity.GroupContact;
import com.bugull.mongo.entity.GroupProduct;
import com.bugull.mongo.entity.Order;
import com.bugull.mongo.entity.Product;
import com.bugull.mongo.entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class InsertTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        Product p1 = new Product();
        p1.setName("iPhone 6");
        p1.setDescription("iPhone 6 is the first choice for your mobile phone, and bala bala bala...");
        p1.setPrice(5321.5F);
        productDao.save(p1);
        
        Product p2 = new Product();
        p2.setName("iPhone 6 Plus");
        p2.setDescription("iPhone 6 Plus is the second choice for your mobile phone, and bala bala bala...");
        p2.setPrice(6321.5F);
        productDao.save(p2);
        
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
        Map<String, List<Integer>> permissions = new HashMap<String, List<Integer>>();
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        permissions.put("product", list1);
        List<Integer> list2 = new ArrayList<Integer>();
        list2.add(1);
        list2.add(3);
        permissions.put("order", list2);
        user.setPermissions(permissions);
        float[] scores = new float[]{80F, 95.5F, 100F};
        user.setScores(scores);
        userDao.save(user);        
        
        OrderDao orderDao = new OrderDao();
        Order order = new Order();
        List<Product> productList = new ArrayList<Product>();
        productList.add(p1);
        productList.add(p2);
        order.setProductList(productList);
        order.setUser(user);
        order.setMoney(9999.9);
        order.setNote("AA");
        orderDao.save(order);
        
        disconnectDB();
    }
    
    @Test
    public void testComplexEmbedList(){
        connectDB();
        
        UserDao userDao = new UserDao();
        User user = userDao.findOne("username", "frank");
        
        Contact c1 = new Contact();
        c1.setEmail("xbwen@hotmail.com");
        c1.setPhone("18900010002");
        Contact c2 = new Contact();
        c2.setEmail("xiaobinwen@gmail.com");
        c2.setPhone("13600010002");
        List<Contact> list = new ArrayList<Contact>();
        list.add(c1);
        list.add(c2);
        Map<String, List<Contact>> mapListContacts = new HashMap<String, List<Contact>>();
        mapListContacts.put("frank", list);
        
        Map<String, Contact> mapContacts = new HashMap<String, Contact>();
        mapContacts.put("c1", c1);
        mapContacts.put("c2", c2);
        
        GroupContactDao gcDao = new GroupContactDao();
        GroupContact gc = new GroupContact();
        gc.setUser(user);
        gc.setMapContacts(mapContacts);
        gc.setMapListContacts(mapListContacts);
        gcDao.save(gc);
        
        disconnectDB();
    }
    
    //@Test
    public void testComplextRefList(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        Product p1 = productDao.findOne("name", "iPhone 6");
        Product p2 = productDao.findOne("name", "iPhone 6 Plus");
        List<Product> list = new ArrayList<Product>();
        list.add(p1);
        list.add(p2);
        Map<String, List<Product>> mapList = new HashMap<String, List<Product>>();
        mapList.put("iPhone", list);
        Map<String, Product> map = new HashMap<String, Product>();
        map.put("6", p1);
        map.put("6P", p2);
        GroupProduct gp = new GroupProduct();
        gp.setMap(map);
        gp.setMapList(mapList);
        gp.setTotalPrice(10000);
        GroupProductDao gpDao = new GroupProductDao();
        gpDao.save(gp);
        
        disconnectDB();
    }

}
