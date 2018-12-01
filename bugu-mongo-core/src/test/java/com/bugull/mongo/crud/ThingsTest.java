/*
 * Copyright 2018 .
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

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.entity.Things;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ThingsTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        BuguDao<Things> dao = DaoCache.getInstance().get(Things.class);
        
        Things things = new Things();
        things.setMacAddress("AABBCCDDEEFF");
        things.setType((byte)0x0A);
        dao.save(things);
        
        System.out.println("done.");
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        BuguDao<Things> dao = DaoCache.getInstance().get(Things.class);
        
        Things things = dao.findOne();
        System.out.println(things.getMacAddress());
        System.out.println(things.getType());
        
        disconnectDB();
    }
    
}
