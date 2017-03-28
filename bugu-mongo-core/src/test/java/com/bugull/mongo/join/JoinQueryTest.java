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

import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.Customer;
import com.bugull.mongo.entity.User;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class JoinQueryTest extends ReplicaSetBaseTest {
    
    @Test
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
                System.out.println("customer name: " + c.getUsername());
                System.out.println("customer age: " + c.getAge());
            }
        }
        
        disconnectDB();
    }
    
}
