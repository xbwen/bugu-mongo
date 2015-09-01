/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.entity;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Property;

/**
 * 商城产品
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
public class Product implements BuguEntity {
    
    @Id
    private String id;
    private String name;
    @Property(lazy = true)
    private String description;
    private Float price;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

}
