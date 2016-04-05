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
package com.bugull.mongo.geo;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.EnsureIndex;
import com.bugull.mongo.annotations.Entity;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
@EnsureIndex(value = "{location:2dsphere}")
public class MyPoint extends SimpleEntity {
    
    private String deviceName;
    
    @Embed
    private Point location;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
    
}
