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
import com.mongodb.ServerAddress;
import java.util.List;

/**
 * The connection to MongoDB.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public interface BuguConnection {
    
    public void connect(String host, int port, String database);
    
    public void connect(String host, int port, String database, String username, String password);
    
    @Deprecated
    public void connect(List<ServerAddress> serverList, List<MongoCredential> credentialList, String database);
    
    public void connect();
    
    public void close();
    
    public BuguConnection setHost(String host);
    
    public BuguConnection setPort(int port);
    
    public BuguConnection setDatabase(String database);
    
    public BuguConnection setUsername(String username);
    
    public BuguConnection setPassword(String password);
    
    public BuguConnection setOptions(MongoClientOptions options);
    
    public BuguConnection setServerList(List<ServerAddress> serverList);
    
    @Deprecated
    public BuguConnection setCredentialList(List<MongoCredential> credentialList);
    
    public DB getDB();
    
    public MongoClient getMongoClient();
    
}
