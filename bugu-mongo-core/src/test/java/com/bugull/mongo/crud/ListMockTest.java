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

import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.ListMockDao;
import com.bugull.mongo.entity.ListMock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ListMockTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        ListMockDao dao = new ListMockDao();
        
        ListMock mock = new ListMock();
        
        Set<Integer> set = new HashSet<Integer>();
        set.add(100);
        set.add(200);
        
        List<Boolean[]> list = new ArrayList<Boolean[]>();
        list.add(new Boolean[]{true, true});
        list.add(new Boolean[]{false, false});
        
        List<List<String>> listlist = new ArrayList<List<String>>();
        List<String> subList1 = new ArrayList<String>();
        subList1.add("1_1");
        subList1.add("1_2");
        List<String> subList2 = new ArrayList<String>();
        subList2.add("2_1");
        subList2.add("2_2");
        listlist.add(subList1);
        listlist.add(subList2);
        
        Collection<String> collection = new ArrayList<String>();
        collection.add("a");
        collection.add("b");
        collection.add("c");
        
        mock.setSet(set);
        mock.setList(list);
        mock.setListlist(listlist);
        mock.setCollection(collection);
        
        dao.save(mock);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        ListMockDao dao = new ListMockDao();
        
        ListMock mock = dao.findOne();
        
        Set<Integer> set = mock.getSet();
        System.out.println("set:");
        for(Integer i : set){
            System.out.println(i);
        }
        
        List<Boolean[]> list = mock.getList();
        for(Boolean[] bb : list){
            System.out.println("boolean array:");
            for(Boolean b : bb){
                System.out.println(b);
            }
        }
        
        List<List<String>> listlist = mock.getListlist();
        for(List<String> subList : listlist){
            System.out.println("sub list:");
            for(String s : subList){
                System.out.println(s);
            }
        }
        
        Collection<String> collection = mock.getCollection();
        System.out.println("collection:");
        if(collection != null){
            for(String s : collection){
                System.out.println(s);
            }
        }
        
        disconnectDB();
    }
    
}
