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
package com.bugull.mongo.join;

import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.CustomerDao;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.Customer;
import com.bugull.mongo.entity.Order;
import com.bugull.mongo.entity.User;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class JoinQueryTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testBasicJoin(){
        connectDB();
        
        UserDao dao = new UserDao();
        
        List<JoinResult<User, Customer>> list = dao.joinQuery(Customer.class).keys("username", "username").results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            System.out.println("user age: " + user.getAge());
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
                System.out.println("  customer age: " + c.getAge());
            }
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testWithMatch(){
        connectDB();
        
        UserDao userDao = new UserDao();
        CustomerDao customerDao = new CustomerDao();
        
        BuguQuery<User> leftMatch = userDao.query().greaterThan("age", 30);
        BuguQuery<Customer> rightMatch = customerDao.query().greaterThanEquals("star", 5);
        List<JoinResult<User, Customer>> list = userDao.joinQuery(Customer.class).keys("username", "username")
                .match(leftMatch, rightMatch)
                .results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            System.out.println("user age: " + user.getAge());
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
                System.out.println("  customer age: " + c.getAge());
                System.out.println("  customer star: " + c.getStar());
                System.out.println("");
            }
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testWithReturnFields(){
        connectDB();
        
        UserDao userDao = new UserDao();
        CustomerDao customerDao = new CustomerDao();
        
        BuguQuery<User> leftMatch = userDao.query().greaterThan("age", 30);
        BuguQuery<Customer> rightMatch = customerDao.query().greaterThanEquals("star", 5);
        List<JoinResult<User, Customer>> list = userDao.joinQuery(Customer.class).keys("username", "username")
                .match(leftMatch, rightMatch)
                .returnFields(new String[]{"username", "age"}, new String[]{"age", "star"})
                .results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            System.out.println("user age: " + user.getAge());
            if(user.getContact()!=null){
                System.out.println("user contact: " + user.getContact().toString());
            }else{
                System.out.println("user contact: null");
            }
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
                System.out.println("  customer age: " + c.getAge());
                System.out.println("  customer star: " + c.getStar());
                System.out.println("");
            }
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testWithNotReturnFields(){
        connectDB();
        
        UserDao userDao = new UserDao();
        CustomerDao customerDao = new CustomerDao();
        
        BuguQuery<User> leftMatch = userDao.query().greaterThan("age", 30);
        BuguQuery<Customer> rightMatch = customerDao.query().greaterThanEquals("star", 5);
        List<JoinResult<User, Customer>> list = userDao.joinQuery(Customer.class).keys("username", "username")
                .match(leftMatch, rightMatch)
                .notReturnFields(new String[]{"contact"}, new String[]{"username"})
                .results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            System.out.println("user age: " + user.getAge());
            if(user.getContact()!=null){
                System.out.println("user contact: " + user.getContact().toString());
            }else{
                System.out.println("user contact: null");
            }
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
                System.out.println("  customer age: " + c.getAge());
                System.out.println("  customer star: " + c.getStar());
                System.out.println("");
            }
        }
        
        disconnectDB();
    }
    
    @Test
    public void testReturnLeftFieldsOnly(){
        connectDB();
        
        UserDao userDao = new UserDao();
        CustomerDao customerDao = new CustomerDao();
        
        BuguQuery<User> leftMatch = userDao.query().greaterThan("age", 30);
        BuguQuery<Customer> rightMatch = customerDao.query().greaterThanEquals("star", 5);
        List<JoinResult<User, Customer>> list = userDao.joinQuery(Customer.class).keys("username", "username")
                .match(leftMatch, rightMatch)
                .returnLeftFieldsOnly()
                .results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            System.out.println("user age: " + user.getAge());
            if(user.getContact()!=null){
                System.out.println("user contact: " + user.getContact().toString());
            }else{
                System.out.println("user contact: null");
            }
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
                System.out.println("  customer age: " + c.getAge());
                System.out.println("  customer star: " + c.getStar());
                System.out.println("");
            }
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testWithSort(){
        connectDB();
        
        UserDao dao = new UserDao();
        
        List<JoinResult<User, Customer>> list = dao.joinQuery(Customer.class).keys("username", "username")
                .sort("{age : 1}", "{age: 1}").results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            System.out.println("user age: " + user.getAge());
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
                System.out.println("  customer age: " + c.getAge());
            }
            System.out.println("");
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testWithPage(){
        connectDB();
        
        UserDao dao = new UserDao();
        
        List<JoinResult<User, Customer>> list = dao.joinQuery(Customer.class).keys("username", "username")
                .pageSize(10).pageNumber(1).results();
        for(JoinResult<User, Customer> result : list){
            User user = result.getLeftEntity();
            System.out.println("user name: " + user.getUsername());
            Customer[] customers = result.getRightEntity();
            System.out.println("customer len: " + customers.length);
            for(Customer c : customers){
                System.out.println("  customer name: " + c.getUsername());
            }
            System.out.println("");
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testLeftRef(){
        connectDB();
        
        OrderDao dao = new OrderDao();
        
        List<JoinResult<Order, User>> list = dao.joinQuery(User.class).keys("user", "id").results();
        for(JoinResult<Order, User> result : list){
            Order order = result.getLeftEntity();
            System.out.println("order: " + order.getMoney());
            User[] users = result.getRightEntity();
            for(User u : users){
                System.out.println("  user name: " + u.getUsername());
            }
            System.out.println("");
        }
        
        disconnectDB();
    }
    
    //@Test
    public void testLeftId(){
        connectDB();
        
        UserDao dao = new UserDao();
        
        List<JoinResult<User, Order>> list = dao.joinQuery(Order.class).keys("id", "user").results();
        for(JoinResult<User, Order> result : list){
            User user = result.getLeftEntity();
            System.out.println("user: " + user.getUsername());
            Order[] orders = result.getRightEntity();
            for(Order o : orders){
                System.out.println("  order: " + o.getMoney());
            }
            System.out.println("");
        }
        
        disconnectDB();
    }
    
}
