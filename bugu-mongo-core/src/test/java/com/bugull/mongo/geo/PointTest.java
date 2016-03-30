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

package com.bugull.mongo.geo;

import com.bugull.mongo.base.ReplicaSetBaseTest;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class PointTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        MyPoint obj = new MyPoint();
        obj.setDeviceName("Machine A");
        Point p = new Point();
        p.setLongitude(1.111);
        p.setLatitude(2.222);
        obj.setLocation(p);
        
        new MyPointDao().save(obj);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        MyPointDao dao = new MyPointDao();
        MyPoint obj = dao.findOne("deviceName", "Machine A");
        Point p = obj.getLocation();
        System.out.println("type: " + p.getType());
        System.out.println("longtitude: " + p.getLongitude());
        System.out.println("latitude: " + p.getLatitude());
        
        disconnectDB();
    }
    
}
