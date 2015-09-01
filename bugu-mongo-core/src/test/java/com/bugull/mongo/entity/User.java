/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import java.util.Date;
import java.util.List;

/**
 * 商城用户
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
@EnsureIndex(value = "{username:1}")
public class User extends SimpleEntity {
    
    private String username;
    private int age;
    private boolean valid;
    private Date registerTime;
    @Embed
    private Contact contact;
    @EmbedList
    private List<Address> addressList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }
    
}
