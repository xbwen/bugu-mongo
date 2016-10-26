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

import com.bugull.mongo.BuguMapper;
import com.bugull.mongo.entity.MyVO;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ManyListTest {
    
    @Test
    public void test(){
        MyVO father = new MyVO();
        father.setName("name");
        father.setValue(Double.MAX_VALUE);
        List<MyVO> list = new ArrayList<>();
        MyVO son = new MyVO();
        son.setName("son");
        son.setValue(Double.MIN_VALUE);
        list.add(son);
        father.setChildren(list);
        
        DBObject dbo = MapperUtil.toDBObject(father);
        String json = BuguMapper.toJsonString(dbo);
        System.out.println("json:" + json);
    }
    
}
