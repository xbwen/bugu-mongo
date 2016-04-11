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
package com.bugull.mongo.bitwise;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.Operator;

/**
 * Convenient class for creating bitwise queries.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BitwiseQuery<T> extends BuguQuery<T> {
    
    public BitwiseQuery(BuguDao<T> dao){
        super(dao);
    }
    
    public BitwiseQuery bitsAllSet(String key, int mask){
        append(key, Operator.BITS_ALL_SET, mask);
        return this;
    }
    
    public BitwiseQuery bitsAllSet(String key, int[] position){
        append(key, Operator.BITS_ALL_SET, position);
        return this;
    }
    
    public BitwiseQuery bitsAnySet(String key, int mask){
        append(key, Operator.BITS_ANY_SET, mask);
        return this;
    }
    
    public BitwiseQuery bitsAnySet(String key, int[] position){
        append(key, Operator.BITS_ANY_SET, position);
        return this;
    }
    
    public BitwiseQuery bitsAllClear(String key, int mask){
        append(key, Operator.BITS_ALL_CLEAR, mask);
        return this;
    }
    
    public BitwiseQuery bitsAllClear(String key, int[] position){
        append(key, Operator.BITS_ALL_CLEAR, position);
        return this;
    }
    
    public BitwiseQuery bitsAnyClear(String key, int mask){
        append(key, Operator.BITS_ANY_CLEAR, mask);
        return this;
    }
    
    public BitwiseQuery bitsAnyClear(String key, int[] position){
        append(key, Operator.BITS_ANY_CLEAR, position);
        return this;
    }
    
}
