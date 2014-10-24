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

package com.bugull.mongo.decoder;

import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.utils.Operator;
import com.mongodb.DBObject;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IdDecoder extends AbstractDecoder{
    
    public IdDecoder(Field field, DBObject dbo){
        super(field);
        value = dbo.get(Operator.ID);
    }
    
    @Override
    public void decode(Object obj){
        FieldUtil.set(obj, field, value.toString());
    }
    
}
