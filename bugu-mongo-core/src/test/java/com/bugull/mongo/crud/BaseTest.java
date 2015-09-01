/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.crud;

import com.bugull.mongo.BuguConnection;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class BaseTest {
    
    protected void connectDB(){
        BuguConnection conn = BuguConnection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPort(27017);
        conn.setUsername("test");
        conn.setPassword("test");
        conn.setDatabase("test");
        conn.connect();
    }
    
    protected void disconnectDB(){
        BuguConnection.getInstance().close();
    }

}
