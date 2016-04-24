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
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.aggregation.BookDao;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class DateQueryTest extends ReplicaSetBaseTest {
    
    @Test
    public void test() throws Exception {
        connectDB();
        
        BookDao dao = new BookDao();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = sdf.parse("2016-04-04 00:00:00");
        
        BuguQuery query = dao.query().lessThan("publishDate", new Date()).greaterThan("publishDate", begin);
        
        System.out.println("json: " + BuguMapper.toJsonString(query.getCondition()));
        
        long count = query.count();
        
        System.out.println("count: " + count);
        
        disconnectDB();
    }
    
}
