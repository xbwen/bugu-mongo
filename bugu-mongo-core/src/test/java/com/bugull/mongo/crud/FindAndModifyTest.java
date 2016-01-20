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

import com.bugull.mongo.BuguUpdater;
import com.bugull.mongo.base.BaseTest;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.User;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FindAndModifyTest extends BaseTest {
    
    @Test
    public void testFindAndModify(){
        connectDB();
        
        UserDao userDao = new UserDao();
        BuguUpdater updater1 = userDao.update().set("age", 35);
        User user1 = userDao.findAndModify("username", "frank", updater1);
        System.out.println("age before: " + user1.getAge());
        
        BuguUpdater updater2 = userDao.update().set("age", 36);
        User user2 = userDao.findAndModify("username", "frank", updater2, true);
        System.out.println("age after: " + user2.getAge());
        
        disconnectDB();
    }
    
    //@Test
    public void testFindAndRemove(){
        connectDB();
        
        UserDao userDao = new UserDao();
        User user1 = userDao.findAndRemove("username", "frank");
        System.out.println("user before remove: " + user1.getUsername());
        
        User user2 = userDao.findOne("username", "frank");
        if(user2==null){
            System.out.println("user not exists!");
        }
        
        disconnectDB();
    }

}
