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
package com.bugull.mongo.join;

import com.bugull.mongo.BuguEntity;

/**
 * Result for JoinQuery. Composing by left entity and right entity.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class JoinResult<L, R> {
    
    private L leftEntity;
    private R[] rightEntity;

    public void setLeftEntity(L leftEntity) {
        this.leftEntity = leftEntity;
    }

    public void setRightEntity(R[] rightEntity) {
        this.rightEntity = rightEntity;
    }

    public L getLeftEntity() {
        return leftEntity;
    }

    public R[] getRightEntity() {
        return rightEntity;
    }
    
}
