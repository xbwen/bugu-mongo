/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.dao;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.entity.Order;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class OrderDao extends BuguDao<Order> {
    
    public OrderDao(){
        super(Order.class);
    }

}
