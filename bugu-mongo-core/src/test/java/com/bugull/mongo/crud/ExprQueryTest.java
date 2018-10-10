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
import com.bugull.mongo.agg.ExpressionBuilder;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.CustomerDao;
import com.bugull.mongo.entity.Customer;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ExprQueryTest extends ReplicaSetBaseTest {
    
    private final static Logger logger = LogManager.getLogger(ExprQueryTest.class.getName());
    
    @Test
    public void test(){
        connectDB();
        
        CustomerDao dao = new CustomerDao();
        
        BuguQuery<Customer> query = dao.query().expr(ExpressionBuilder.compare().greaterThan("$star", "$age").build());
        
        logger.error("the query is: {}", query);
        
        List<Customer> list = query.results();
        
        for(Customer customer : list){
            System.out.println("username: " + customer.getUsername());
            System.out.println("age: " + customer.getAge());
            System.out.println("star: " + customer.getStar());
        }
        
        disconnectDB();
    }
    
}
