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

import com.bugull.mongo.exception.DBConnectionException;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The connection to MongoDB.
 * 
 * <p>Singleton Pattern is used here. An application should use only one BuguConnection.</p>
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguConnection {
    
    private final static Logger logger = LogManager.getLogger(BuguConnection.class.getName());
    
    private String host;
    private int port;
    private List<ServerAddress> replicaSet;
    private ReadPreference readPreference;
    private MongoClientOptions options;
    private String database;
    private String username;
    private String password;
    private MongoClient mc;
    private DB db;
    
    private static class Holder {
        final static BuguConnection instance = new BuguConnection();
    } 
    
    public static BuguConnection getInstance(){
        return Holder.instance;
    }
    
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
    
    public void connect(List<ServerAddress> replicaSet, String database, String username, String password){
        this.replicaSet = replicaSet;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
    }
    
    public void connect(){
        try {
            doConnect();
        } catch (UnknownHostException ex) {
            logger.error("Can not connect to host " + host, ex);
        } catch (DBConnectionException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if(username != null && password != null){
            try{
                doAuth();
            }catch(DBConnectionException ex){
                logger.error(ex.getMessage(), ex);
            } 
        }
    }
    
    public void close(){
        BuguFramework.getInstance().destroy();
        if(mc != null){
            mc.close();
        }
    }

    private void doConnect() throws UnknownHostException, DBConnectionException {
        if(host != null && port != 0){
            ServerAddress sa = new ServerAddress(host, port);
            if(options != null){
                mc = new MongoClient(sa, options);
            }else{
                mc = new MongoClient(sa);
            }
        }
        else if(replicaSet != null){
            if(options != null){
                mc = new MongoClient(replicaSet, options);
            }else{
                mc = new MongoClient(replicaSet);
            }
            if(readPreference != null){
                mc.setReadPreference(readPreference);
            }
        }
        if(mc != null){
            db = mc.getDB(database);
        }else{
            throw new DBConnectionException("Can not get database instance! Please ensure connected to mongoDB correctly.");
        }
    }
    
    private void doAuth() throws DBConnectionException {
        boolean auth = db.authenticate(username, password.toCharArray());
        if(auth){
            logger.info("Connected to mongodb successfully!");
        }else{
            db = null;
            throw new DBConnectionException("Can not connect to mongoDB. Failed to authenticate!");
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

    public BuguConnection setReplicaSet(List<ServerAddress> replicaSet) {
        this.replicaSet = replicaSet;
        return this;
    }

    public BuguConnection setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }

    public DB getDB() throws DBConnectionException {
        if(db == null){
            throw new DBConnectionException("Can not get database instance! Please ensure connected to mongoDB correctly.");
        }else{
            return db;
        }
    }
    
}
