/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.crud;

import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.dao.OrderDao;
import com.bugull.mongo.dao.UserDao;
import com.bugull.mongo.entity.User;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class UpdateTest extends BaseTest {
    
    @Test
    public void testUpdate(){
        connectDB();
        
        UserDao userDao = new UserDao();
        User user = userDao.query().is("username", "frank").result();
        user.setAge(33);
        userDao.save(user);
        
        OrderDao orderDao = new OrderDao();
        BuguQuery query = orderDao.query().is("user", user);
        orderDao.update().inc("money", -100).apply(query);
        
        disconnectDB();
    }

}
