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
import com.bugull.mongo.dao.AccountDao;
import com.bugull.mongo.entity.Account;
import java.math.BigDecimal;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class AccountTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        Account account = new Account();
        account.setName("Jessica");
        account.setMoney(BigDecimal.valueOf(9999.99));

        new AccountDao().insert(account);
        
        disconnectDB();
    }
    
    //@Test
    public void testQuery(){
        connectDB();
        
        AccountDao dao = new AccountDao();
        Account account = dao.findOne();
        System.out.println("name: " + account.getName());
        BigDecimal bd = account.getMoney();
        System.out.println("money: " + bd.toString());
        
        disconnectDB();
    }
    
    @Test
    public void testUpdate(){
        connectDB();
        
        AccountDao dao = new AccountDao();
        BuguQuery<Account> query = dao.query().is("name", "Frank");
        dao.update().set("money", new BigDecimal("6666")).setUpsert(true).execute(query);
        
        disconnectDB();
    }
    
}
