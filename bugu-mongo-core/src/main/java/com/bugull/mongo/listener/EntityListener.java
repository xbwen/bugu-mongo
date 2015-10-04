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
package com.bugull.mongo.listener;

import com.bugull.mongo.BuguEntity;

/**
 * When <code>BuguEntity</code> has been inserted, updated, or deleted, the event will deliver to <code>EntityListener</code>
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public interface EntityListener {
    
    /**
     * Notified that an entity has been inserted.
     * @param entity the inserted object
     */
    public void entityInserted(BuguEntity entity);
    
    /**
     * Notified that an entity has been updated.
     * @param entity the updated object
     */
    public void entityUpdated(BuguEntity entity);
    
    /**
     * Notified that an entity has been deleted.
     * @param entity the deleted object
     */
    public void entityDeleted(BuguEntity entity);
    
}
