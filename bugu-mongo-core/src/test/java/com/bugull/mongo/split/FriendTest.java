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

package com.bugull.mongo.split;

import com.bugull.mongo.base.BaseTest;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FriendTest extends BaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        FriendDao dao = new FriendDao();
        
        Friend f1 = new Friend();
        f1.setName("Frank");
        f1.setProvince("Zhejiang");
        
        dao.setSplitSuffix(f1.getProvince());
        dao.save(f1);
        
        Friend f2 = new Friend();
        f2.setName("Tom");
        f2.setProvince("Shanghai");
        
        dao.setSplitSuffix(f2.getProvince());
        dao.save(f2);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        FriendDao dao = new FriendDao();
        dao.setSplitSuffix("Zhejiang");
        
        List<Friend> list = dao.findAll();
        for(Friend f : list){
            System.out.println("name: " + f.getName());
            System.out.println("province: " + f.getProvince());
        }
        
        disconnectDB();
    }

}
