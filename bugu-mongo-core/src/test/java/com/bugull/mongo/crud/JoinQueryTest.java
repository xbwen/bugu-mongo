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
import com.bugull.mongo.join.JoinQuery;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.Order;
import com.bugull.mongo.entity.User;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class JoinQueryTest extends ReplicaSetBaseTest {
    
    @Test
    public void test(){
        connectDB();
        
//        OrderDao orderDao = new OrderDao();
//        UserDao userDao = new UserDao();
//        BuguQuery<Order> leftQuery = orderDao.query().lessThan("money", 10000);
//        BuguQuery<User> rightQuery = userDao.query().lessThan("age", 40);
//        JoinQuery<Order> jq = orderDao.joinQuery(User.class)
//                                    .leftField("user").rightField("_id")
//                                    .leftCondition(leftQuery).rightCondition(rightQuery);
//        List<Order> list = jq.results();
//        
//        for(Order order : list){
//            System.out.println("order id: " + order.getId());
//            System.out.println("order money: " + order.getMoney());
//        }
        
        disconnectDB();
    }
    
}
