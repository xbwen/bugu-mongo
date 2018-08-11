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
import com.bugull.mongo.dao.ProductDao;
import com.bugull.mongo.entity.Product;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ProductTest extends BaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        ProductDao dao = new ProductDao();
        for(int i=1; i<=10000; i++){
            Product product = new Product();
            product.setName("iPhone " + i);
            product.setPrice(i * 1.0F);
            product.setDescription("iPhone " + i + " is the best mobile phone!!!");
            dao.save(product);
        }
        
        disconnectDB();
    }
    
    @Test
    public void testFind(){
        connectDB();
        
        ProductDao dao = new ProductDao();
        List<Product> list = dao.query()
                .greaterThan("price", 5000).lessThanEquals("price", 6000)
                .notReturnFields("description")
                .sortAsc("price")
                .maxTimeMS(100)
                .results();
        for(Product p : list){
            System.out.println(p.getName() + "  : " + p.getDescription());
        }
        
        disconnectDB();
    }
    
}
