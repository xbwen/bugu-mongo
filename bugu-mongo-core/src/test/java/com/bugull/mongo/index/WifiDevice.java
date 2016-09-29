/*
 * Copyright (c) www.bugull.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bugull.mongo.index;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;
import java.util.Date;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
@EnsureIndex("{macAddress:1, unique:true}")
public class WifiDevice extends SimpleEntity {
    
    private String macAddress;
    private Date lastLogin;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

}
