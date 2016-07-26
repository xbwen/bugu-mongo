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

package com.bugull.mongo.base;

import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ReplicaSetBaseTest {
    
    protected void connectDB(){
        try{
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
            BuguConnection conn = BuguFramework.getInstance().createConnection();
            conn.setServerList(serverList).setCredentialList(credentialList).setDatabase("test").connect();
        }catch(UnknownHostException ex){
            ex.printStackTrace();
        }
    }
    
    protected void connectDBWithOptions(MongoClientOptions options){
        try{
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
            BuguConnection conn = BuguFramework.getInstance().createConnection();
            conn.setOptions(options);
            conn.setServerList(serverList);
            conn.setCredentialList(credentialList);
            conn.setDatabase("test");
            conn.connect();
        }catch(UnknownHostException ex){
            ex.printStackTrace();
        }
    }
    
    protected void disconnectDB(){
        BuguFramework.getInstance().destroy();
    }

}
