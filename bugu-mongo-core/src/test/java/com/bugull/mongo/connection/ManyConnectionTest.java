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
package com.bugull.mongo.connection;

import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ManyConnectionTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        connectDB2();
        
        FooDao dao = new FooDao();
        Foo f = new Foo();
        f.setName("f_1");
        dao.save(f);
        
        Foo2Dao dao2 = new Foo2Dao();
        Foo2 f2 = new Foo2();
        f2.setName("f_2");
        dao2.save(f2);
        
        disconnectAll();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        connectDB2();
        
        FooDao dao = new FooDao();
        Foo f = dao.findOne("name", "f_1");
        System.out.println("id: " + f.getId().toString());
        System.out.println("name: " + f.getName());
        
        System.out.println("");
        
        Foo2Dao dao2 = new Foo2Dao();
        Foo2 f2 = dao2.findOne("name", "f_2");
        System.out.println("id: " + f2.getId().toString());
        System.out.println("name: " + f2.getName());
        
        disconnectAll();
    }
    
    private void connectDB(){
        List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        serverList.add(new ServerAddress("192.168.0.200", 27017));
        serverList.add(new ServerAddress("192.168.0.200", 27018));
        serverList.add(new ServerAddress("192.168.0.200", 27019));
        
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        MongoCredential credentialA = MongoCredential.createCredential("test", "test", "test".toCharArray());
        MongoCredential credentialB = MongoCredential.createCredential("test", "test", "test".toCharArray());
        MongoCredential credentialC = MongoCredential.createCredential("test", "test", "test".toCharArray());
        credentialList.add(credentialA);
        credentialList.add(credentialB);
        credentialList.add(credentialC);
        //create default db connection
        BuguConnection conn = BuguFramework.getInstance().createConnection();
        conn.setServerList(serverList).setCredentialList(credentialList).setDatabase("test").connect();
    }
    
    
    private void connectDB2(){
        List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        serverList.add(new ServerAddress("192.168.0.200", 27017));
        serverList.add(new ServerAddress("192.168.0.200", 27018));
        serverList.add(new ServerAddress("192.168.0.200", 27019));
        
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        MongoCredential credentialA = MongoCredential.createCredential("test2", "test2", "test2".toCharArray());
        MongoCredential credentialB = MongoCredential.createCredential("test2", "test2", "test2".toCharArray());
        MongoCredential credentialC = MongoCredential.createCredential("test2", "test2", "test2".toCharArray());
        credentialList.add(credentialA);
        credentialList.add(credentialB);
        credentialList.add(credentialC);
        //create another db connection, with name 'test2'
        BuguConnection conn = BuguFramework.getInstance().createConnection("test2");
        conn.setServerList(serverList).setCredentialList(credentialList).setDatabase("test2").connect();
    }
    
    private void disconnectAll(){
        BuguFramework.getInstance().destroy();
    }
    
}
