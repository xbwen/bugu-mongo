/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.entity;

/**
 * 收货地址
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class Address {
    
    private String province;
    private String city;
    private String detailAddress;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

}
