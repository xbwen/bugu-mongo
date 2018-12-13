/*
 * Copyright 2017 .
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

import com.bugull.mongo.exception.BuguException;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.List;

/**
 * Basic implement of BuguConnection, create by BuguFramework.
 * 
 * Application code conn't new BasicConnection(). Please create connection by this way:
 * 
 * <pre>
 * BuguConnection conn = BuguFramework.getInstance().createConnection();
 * conn.connect("192.168.0.100", 27017, "mydb", "username", "password");
 * </pre>
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
class BasicConnection implements BuguConnection {
    
    private String host;
    private int port = 27017;
    private List<ServerAddress> serverList;
    @Deprecated
    private List<MongoCredential> credentialList;
    private MongoCredential credential;
    private MongoClientOptions options;
    private String database;
    private String username;
    private String password;
    private MongoClient mongoClient;
    private DB db;
    
    /**
     * connect to a single mongodb server without auth.
     * @param host
     * @param port
     * @param database 
     */
    @Override
    public void connect(String host, int port, String database){
        this.host = host;
        this.port = port;
        this.database = database;
        
        connect();
    }
    
    /**
     * connect to a single mongodb server.
     * @param host
     * @param port
     * @param database
     * @param username
     * @param password 
     */
    @Override
    public void connect(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        
        connect();
    }
    
    @Override
    @Deprecated
    public void connect(List<ServerAddress> serverList, List<MongoCredential> credentialList, String database){
        this.serverList = serverList;
        this.credentialList = credentialList;
        this.database = database;
        
        connect();
    }
    
    /**
     * connect to mongodb with specified parameters.
     */
    @Override
    public void connect() {
        if(host != null && serverList != null){
            throw new BuguException("Error when connect to database server! You should set database host or server list, but not both!");
        }
        if(username != null && password != null && database != null){
            this.credential = MongoCredential.createCredential(username, database, password.toCharArray());
        }
        if(options == null){
            options = MongoClientOptions.builder().build();
        }
        if(host != null){
            ServerAddress sa = new ServerAddress(host, port);
            if(credentialList != null){
                mongoClient = new MongoClient(sa, credentialList, options);
            }
            else if(credential != null){
                mongoClient = new MongoClient(sa, credential, options);
            }
            else{
                mongoClient = new MongoClient(sa, options);
            }
        }
        else if(serverList != null){
            if(credentialList != null){
                mongoClient = new MongoClient(serverList, credentialList, options);
            }
            else if(credential != null){
                mongoClient = new MongoClient(serverList, credential, options);
            }
            else{
                mongoClient = new MongoClient(serverList, options);
            }
        }
        else{
            throw new BuguException("Error when connect to database server! You should set database host or server list, at least one!");
        }
        //get the database
        db = mongoClient.getDB(database);
    }
    
    @Override
    public void close(){
        if(mongoClient != null){
            mongoClient.close();
            mongoClient = null;
        }
    }
    
    @Override
    public BuguConnection setHost(String host){
        this.host = host;
        return this;
    }
    
    @Override
    public BuguConnection setPort(int port){
        this.port = port;
        return this;
    }
    
    @Override
    public BuguConnection setDatabase(String database){
        this.database = database;
        return this;
    }
    
    @Override
    public BuguConnection setUsername(String username){
        this.username = username;
        return this;
    }
    
    @Override
    public BuguConnection setPassword(String password){
        this.password = password;
        return this;
    }

    @Override
    public BuguConnection setOptions(MongoClientOptions options) {
        this.options = options;
        return this;
    }

    @Override
    public BuguConnection setServerList(List<ServerAddress> serverList) {
        this.serverList = serverList;
        return this;
    }

    @Override
    @Deprecated
    public BuguConnection setCredentialList(List<MongoCredential> credentialList) {
        this.credentialList = credentialList;
        return this;
    }

    @Override
    public DB getDB() {
        return db;
    }

    @Override
    public MongoClient getMongoClient() {
        return mongoClient;
    }
    
}
