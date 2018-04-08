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
package com.bugull.mongo.array;

import com.bugull.mongo.base.ReplicaSetBaseTest;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ReportTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        ReportDao dao = new ReportDao();
        
        Report report1 = new Report();
        report1.setTitle("title 1");
        report1.setTags(new String[]{"a", "b"});
        dao.save(report1);
        
        Report report2 = new Report();
        report2.setTitle("title 2");
        report2.setTags(new String[]{"a", "b", "c"});
        dao.save(report2);
        
        Report report3 = new Report();
        report3.setTitle("title 3");
        report3.setTags(new String[]{"a", "b", "c", "d"});
        dao.save(report3);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        ReportDao dao = new ReportDao();
        
        List<Report> list = dao.query().all("tags", new String[]{"c", "b", "a"}).size("tags", 3).results();
        for(Report report : list){
            System.out.println(report.getTitle());
        }
        
        
        disconnectDB();
    }
    
}
