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
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class PointTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        MyPointDao dao = new MyPointDao();
        
        MyPoint my1 = new MyPoint();
        my1.setDeviceName("Machine_A");
        my1.setLocation(new Point(-73.856077, 40.848447));
        
        MyPoint my2 = new MyPoint();
        my2.setDeviceName("Machine_B");
        my2.setLocation(new Point(-73.856078, 40.848448));
        
        MyPoint my3 = new MyPoint();
        my3.setDeviceName("Machine_C");
        my3.setLocation(new Point(-73.856079, 40.848449));
        
        MyPoint my4 = new MyPoint();
        my4.setDeviceName("Machine_D");
        my4.setLocation(new Point(-70, 40));
        
        dao.save(my1);
        dao.save(my2);
        dao.save(my3);
        dao.save(my4);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        MyPointDao dao = new MyPointDao();
        
        List<MyPoint> list1 = dao.geoQuery().nearSphere("location", new Point(-73.856076, 40.848446)).results();
        for(MyPoint my : list1){
            System.out.println("device name: " + my.getDeviceName());
            System.out.println("longtitude: " + my.getLocation().getLongitude());
            System.out.println("latitude: " + my.getLocation().getLatitude());
        }
        
        System.out.println();
        
        List<MyPoint> list2 = dao.geoQuery().nearSphere("location", new Point(-73.856080, 40.848450), 1000).results();
        for(MyPoint my : list2){
            System.out.println("device name: " + my.getDeviceName());
            System.out.println("longtitude: " + my.getLocation().getLongitude());
            System.out.println("latitude: " + my.getLocation().getLatitude());
        }
        
        disconnectDB();
    }
    
}
