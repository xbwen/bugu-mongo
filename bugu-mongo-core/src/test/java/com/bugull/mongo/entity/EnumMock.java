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
import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.Entity;
import java.util.List;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@Entity
public class EnumMock extends SimpleEntity {
    
    @Embed
    private AppSize appSize;
    
    @EmbedList
    private AppSize[] arraySize;
    
    @EmbedList
    private List<AppSize> listSize;

    public AppSize getAppSize() {
        return appSize;
    }

    public void setAppSize(AppSize appSize) {
        this.appSize = appSize;
    }

    public AppSize[] getArraySize() {
        return arraySize;
    }

    public void setArraySize(AppSize[] arraySize) {
        this.arraySize = arraySize;
    }

    public List<AppSize> getListSize() {
        return listSize;
    }

    public void setListSize(List<AppSize> listSize) {
        this.listSize = listSize;
    }
    
    public enum AppSize {
        SMALL, MEDIUM, LARGE
    }
    
}