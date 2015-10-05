/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.split;

import com.bugull.mongo.BuguDao;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FriendDao extends BuguDao<Friend> {
    
    public FriendDao(){
        super(Friend.class);
    }

}
