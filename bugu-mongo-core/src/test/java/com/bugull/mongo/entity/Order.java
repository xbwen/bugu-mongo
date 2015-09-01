/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import java.util.List;

/**
 * 用户订单
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
public class Order extends SimpleEntity {
    
    private double money;
    @Ref
    private User user;
    @RefList
    private List<Product> productList;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

}
