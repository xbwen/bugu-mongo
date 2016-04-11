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
import com.bugull.mongo.dao.GroupProductDao;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.GroupProduct;
import com.bugull.mongo.entity.Order;
import com.bugull.mongo.entity.Product;
import com.bugull.mongo.entity.User;
import com.bugull.mongo.utils.SortUtil;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CascadeFetchTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testCascadeFetch(){
        connectDB();
        
        UserDao userDao = new UserDao();
        User user = userDao.query().is("username", "frank").result();
        
        OrderDao orderDao = new OrderDao();
        List<Order> orderList = orderDao.query().is("user", user).sort(SortUtil.desc("money")).results();
        BuguMapper.fetchCascade(orderList, "productList");
        for(Order order : orderList){
            System.out.println("total money: " + order.getMoney());
            List<Product> productList = order.getProductList();
            for(Product product : productList){
                System.out.println("product name: " + product.getName());
            }
        }
        
        disconnectDB();
    }
    
    @Test
    public void testComplexRefList(){
        connectDB();
        
        GroupProductDao gpDao = new GroupProductDao();
        List<GroupProduct> all = gpDao.findAll();
        BuguMapper.fetchCascade(all, "map", "mapList");
        for(GroupProduct gp : all){
            Map<String, Product> map = gp.getMap();
            for(Map.Entry<String, Product> entry : map.entrySet()){
                System.out.println("key: " + entry.getKey());
                Product p = entry.getValue();
                System.out.println("product name: " + p.getName());
            }
            System.out.println();
            Map<String, List<Product>> mapList = gp.getMapList();
            for(Map.Entry<String, List<Product>> entry : mapList.entrySet()){
                System.out.println("key: " + entry.getKey());
                List<Product> list = entry.getValue();
                for(Product p : list){
                    System.out.println("product name: " + p.getName());
                }
            }
        }
        
        disconnectDB();
    }
    
}
