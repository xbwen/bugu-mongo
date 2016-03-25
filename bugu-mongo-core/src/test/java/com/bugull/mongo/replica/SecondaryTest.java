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

package com.bugull.mongo.replica;

import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.entity.Product;
import com.mongodb.ReadPreference;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class SecondaryTest extends ReplicaSetBaseTest {
    
    @Test
    public void testSecondary(){
        connectDB();
        
        ProductDao productDao = new ProductDao();
        
        productDao.setReadPreference(ReadPreference.secondaryPreferred());
        
        productDao.remove("name", "LG");
        
        Product p = new Product();
        p.setName("LG");
        p.setDescription("LG Android Phone");
        p.setPrice(3000F);
        productDao.save(p);
        
        //check if the product data can read from secondary
        Product fromSecondary = productDao.findOne("name", "LG");
        if(fromSecondary != null){
            System.out.println("read from secondary");
        }else{
            System.out.println("can not read from secondary");
        }
        
        disconnectDB();
        
    }

}
