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
import com.bugull.mongo.annotations.Entity;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
public class ListMock extends SimpleEntity {
    
    private Set<Integer> set;
    
    private List<Boolean[]> list;
    
    private List<List<String>> listlist;

    private Collection<String> collection;

    public Set<Integer> getSet() {
        return set;
    }

    public void setSet(Set<Integer> set) {
        this.set = set;
    }

    public List<Boolean[]> getList() {
        return list;
    }

    public void setList(List<Boolean[]> list) {
        this.list = list;
    }

    public List<List<String>> getListlist() {
        return listlist;
    }

    public void setListlist(List<List<String>> listlist) {
        this.listlist = listlist;
    }

    public Collection<String> getCollection() {
        return collection;
    }

    public void setCollection(Collection<String> collection) {
        this.collection = collection;
    }
    
}
