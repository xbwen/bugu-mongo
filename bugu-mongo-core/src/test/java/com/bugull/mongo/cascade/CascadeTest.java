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
package com.bugull.mongo.cascade;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguMapper;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CascadeTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        BuguDao<Node> dao = DaoCache.getInstance().get(Node.class);
        
        Node root = new Node();
        root.setName("root");
        
        Node toad = new Node();
        toad.setName("toad");
        toad.setFather(root);
        
        dao.save(toad);
        
        Node child1 = new Node();
        child1.setName("child1");
        child1.setFather(toad);
        
        Node child2 = new Node();
        child2.setName("child2");
        child2.setFather(toad);
        
        List<Node> list = new ArrayList<Node>();
        list.add(child1);
        list.add(child2);
        
        toad.setChildren(list);
        
        dao.save(toad);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        BuguDao<Node> dao = DaoCache.getInstance().get(Node.class);
        Node child1 = dao.findOne("name", "child1");
        Node father = child1.getFather();
        String json = BuguMapper.toJsonString(father);
        System.out.println(json);
        BuguMapper.fetchCascade(father, "father");
        System.out.println("father name:" + father.getName());
        Node grand = father.getFather();
        if(grand != null){
            System.out.println("grand name:" + grand.getName());
        }else{
            System.out.println("grand is null");
        }
        
        disconnectDB();
    }
    
}
