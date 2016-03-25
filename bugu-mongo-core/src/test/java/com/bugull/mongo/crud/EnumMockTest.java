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
import com.bugull.mongo.dao.EnumMockDao;
import com.bugull.mongo.entity.EnumMock;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class EnumMockTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        EnumMock e = new EnumMock();
        e.setSize(100);
        e.setAppSize(EnumMock.AppSize.MEDIUM);
        new EnumMockDao().save(e);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        EnumMockDao dao = new EnumMockDao();
        EnumMock e = dao.query().is("appSize", EnumMock.AppSize.MEDIUM).result();
        System.out.println("size: " + e.getSize());
        System.out.println("app size: " + e.getAppSize());
        System.out.println(e.getAppSize()==EnumMock.AppSize.MEDIUM);
        
        disconnectDB();
    }
    
}
