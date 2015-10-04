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

package com.bugull.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The connection to MongoDB.
 * 
 * <p>Singleton Pattern is used here. An application should use only one BuguConnection.</p>
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguConnection {
    
    private String host;
    private int port;
    private List<ServerAddress> serverList;
    private List<MongoCredential> credentialList;
    private ReadPreference readPreference;
    private MongoClientOptions options;
    private String database;
    private String username;
    private String password;
    private MongoClient mc;
    
    public void connect(String host, int port, String database){
        this.host = host;
        this.port = port;
        this.database = database;
        connect();
    }
    
    public void connect(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
    }
    
    public void connect(List<ServerAddress> serverList, List<MongoCredential> credentialList, String database){
        this.serverList = serverList;
        this.credentialList = credentialList;
        this.database = database;
        connect();
    }
    
    public void connect(){
        if(username != null && password != null && database != null){
            credentialList = new ArrayList<MongoCredential>();
            MongoCredential cred = MongoCredential.createCredential(username, database, password.toCharArray());
            credentialList.add(cred);
        }
        if(host != null && port != 0){
            ServerAddress sa = new ServerAddress(host, port);
            if(options != null){
                mc = new MongoClient(sa, credentialList, options);
            }else{
                mc = new MongoClient(sa, credentialList);
            }
        }
        else if(serverList != null && credentialList != null){
            if(options != null){
                mc = new MongoClient(serverList, credentialList, options);
            }else{
                mc = new MongoClient(serverList, credentialList);
            }
            if(readPreference != null){
                mc.setReadPreference(readPreference);
            }
        }
    }
    
    public void close(){
        if(mc != null){
            mc.close();
            mc = null;
        }
    }
    
    public BuguConnection setHost(String host){
        this.host = host;
        return this;
    }
    
    public BuguConnection setPort(int port){
        this.port = port;
        return this;
    }
    
    public BuguConnection setDatabase(String database){
        this.database = database;
        return this;
    }
    
    public BuguConnection setUsername(String username){
        this.username = username;
        return this;
    }
    
    public BuguConnection setPassword(String password){
        this.password = password;
        return this;
    }

    public BuguConnection setOptions(MongoClientOptions options) {
        this.options = options;
        return this;
    }

    public BuguConnection setServerList(List<ServerAddress> serverList) {
        this.serverList = serverList;
        return this;
    }

    public BuguConnection setCredentialList(List<MongoCredential> credentialList) {
        this.credentialList = credentialList;
        return this;
    }

    public BuguConnection setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }

    public DB getDB() {
        return mc.getDB(database);
    }
    
}
