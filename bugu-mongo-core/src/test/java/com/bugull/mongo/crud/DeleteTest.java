/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.crud;

import com.bugull.mongo.dao.UserDao;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class DeleteTest extends BaseTest {
    
    @Test
    public void testDelete(){
        connectDB();
        
        UserDao userDao = new UserDao();
        userDao.remove("valide", false);
        
        disconnectDB();
    }

}
