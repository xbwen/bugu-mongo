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
package com.bugull.mongo.transaction;

import com.bugull.mongo.BuguFramework;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.AccountDao;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.entity.Account;
import com.bugull.mongo.entity.Product;
import com.bugull.mongo.utils.TransactionUtil;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import java.math.BigDecimal;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class TransactionTest extends ReplicaSetBaseTest {
    
    @Test
    public void test(){
        connectDB();
        
        TransactionUtil.runTransactionWithRetry(this::doTxn);
        
        disconnectDB();
    }
    
    private void doTxn(){
        MongoClient client = BuguFramework.getInstance().getConnection().getMongoClient();
        ClientSession session = client.startSession();
        session.startTransaction();
        
        Account account = new Account();
        account.setName("Jessica");
        account.setMoney(BigDecimal.valueOf(9999.99));
        new AccountDao().insert(account);
        
        ProductDao dao = new ProductDao();
        Product product = new Product();
        product.setName("iPhone");
        product.setPrice(7777.0F);
        product.setDescription("iPhone is the best mobile phone!!!");
        dao.save(product);
        
        TransactionUtil.commitWithRetry(session);
        
    }
    
}
