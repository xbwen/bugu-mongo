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
package com.bugull.mongo.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.Entity;
import com.bugull.mongo.annotations.Ref;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
public class GroupContact extends SimpleEntity {
    @EmbedList
    private Map<String, Contact> mapContacts;
    @EmbedList
    private Map<String, List<Contact>> mapListContacts;
    @Ref
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, Contact> getMapContacts() {
        return mapContacts;
    }

    public void setMapContacts(Map<String, Contact> mapContacts) {
        this.mapContacts = mapContacts;
    }

    public Map<String, List<Contact>> getMapListContacts() {
        return mapListContacts;
    }

    public void setMapListContacts(Map<String, List<Contact>> mapListContacts) {
        this.mapListContacts = mapListContacts;
    }
    
}
