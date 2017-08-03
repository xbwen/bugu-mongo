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
import com.bugull.mongo.entity.EnumMock.AppSize;
import java.util.ArrayList;
import java.util.List;
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
        e.setAppSize(EnumMock.AppSize.SMALL);
        AppSize[] arraySize = new AppSize[]{AppSize.SMALL, AppSize.MEDIUM};
        e.setArraySize(arraySize);
        List<AppSize> listSize = new ArrayList<AppSize>();
        listSize.add(AppSize.SMALL);
        listSize.add(AppSize.MEDIUM);
        listSize.add(AppSize.LARGE);
        e.setListSize(listSize);
        new EnumMockDao().save(e);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        EnumMockDao dao = new EnumMockDao();
        EnumMock e = dao.query().is("appSize", EnumMock.AppSize.SMALL).result();
        System.out.println("appSize: " + e.getAppSize());
        AppSize[] arraySize = e.getArraySize();
        for(AppSize size : arraySize){
            System.out.println("arraySize: " + size);
        }
        List<AppSize> listSize = e.getListSize();
        for(AppSize size : listSize){
            System.out.println("listSize: " + size);
        }
        disconnectDB();
    }
    
}
