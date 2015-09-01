/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.dao;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.entity.Product;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ProductDao extends BuguDao<Product> {
    
    public ProductDao(){
        super(Product.class);
    }

}
