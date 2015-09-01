/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.dao;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.entity.User;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class UserDao extends BuguDao<User> {
    
    public UserDao(){
        super(User.class);
    }

}
