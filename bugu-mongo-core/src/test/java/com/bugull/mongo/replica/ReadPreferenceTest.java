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
package com.bugull.mongo.replica;

import com.bugull.mongo.base.BaseTest;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.mongodb.ReadPreference;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ReadPreferenceTest extends ReplicaSetBaseTest{
    
    //@Test
    public void testSave(){
        connectDB();
        
        BlogDao dao = new BlogDao();
        
        Blog blog = new Blog();
        blog.setTitle("About iPhone");
        blog.setContent("Bla Bla Bla");
        
        dao.save(blog);
        
        //after save, if need immediately read, must read from primary
        dao.setReadPreference(ReadPreference.primary());
        Blog x = dao.findOne("title", "About iPhone");
        
        System.out.println("blog content: " + x.getContent());
        
        disconnectDB();
    }
    
    @Test
    public void testRead(){
        connectDB();
        
        BlogDao dao = new BlogDao();
        
        //read from secondary
        dao.setReadPreference(ReadPreference.secondaryPreferred());
        
        Blog y = dao.findOne("title", "About iPhone");
        
        System.out.println("blog content: " + y.getContent());
        
        disconnectDB();
    }
    
}
