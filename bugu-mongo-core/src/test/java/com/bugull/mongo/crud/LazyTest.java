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

import com.bugull.mongo.BuguMapper;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.entity.Product;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class LazyTest extends ReplicaSetBaseTest {
    
    @Test
    public void test(){
        connectDB();
        
        ProductDao pDao = new ProductDao();
        List<Product> list = pDao.findAll();
        //the description field is lazy, it will not contained in the result list.
        for(Product p : list){
            System.out.println("description: " + p.getDescription());
        }
        
        //you can fetch out lazy property.
        BuguMapper.fetchLazy(list);
        for(Product p : list){
            System.out.println("description: " + p.getDescription());
        }
        
        //lazy property will not play a part if only one record is return.
        Product p1 = pDao.findOne();
        System.out.println("description: " + p1.getDescription());
        
        //if you query with method notReturnFields(), the field will not contained even if only one record is return.
        Product p2 = pDao.query().is("id", "55f3bae5a00803537bd092c6").notReturnFields("description").result();
        System.out.println("description: " + p2.getDescription());
        
        disconnectDB();
    }

}
