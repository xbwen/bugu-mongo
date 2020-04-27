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

package com.bugull.mongo.index;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.BaseTest;
import com.bugull.mongo.cache.DaoCache;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class UniqueIndexTest extends BaseTest {
    
    @Test
    public void test(){
        connectDB();
        
        BuguDao<WifiDevice> dao = DaoCache.getInstance().get(WifiDevice.class);
        
        //insert a new document
        WifiDevice device1 = new WifiDevice();
        device1.setMacAddress("AABBCCDDEEFF");
        device1.setLastLogin(new Date());
        dao.save(device1);
        System.out.println("save 1 OK");
        
        //insert another document, with the same mac address, to check the behavior of unique index
        WifiDevice device2 = new WifiDevice();
        device2.setMacAddress("AABBCCDDEEFF");
        device2.setLastLogin(new Date());
        try{
            dao.save(device2);
        }catch(Exception ex){
            System.out.println("save 2 NOT ok");
        }
        
        disconnectDB();
    }

}
