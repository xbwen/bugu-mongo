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
package com.bugull.mongo.replica;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.mongodb.ReadPreference;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class SecondaryBlogDao extends BuguDao<Blog> {
    
    public SecondaryBlogDao() {
        super(Blog.class);
        //set ReadPreference to secondaryPreferred
        super.setReadPreference(ReadPreference.secondaryPreferred());
    }

}
