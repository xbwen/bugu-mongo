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
package com.bugull.mongo.codec;

import com.bugull.mongo.decoder.AbstractDecoder;
import com.mongodb.DBObject;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class MyDecoder extends AbstractDecoder {
    
    public MyDecoder(Field field, DBObject dbo){
        super(field);
        
        //Write your code here. Get value from DBObject
        //for example:
        value = dbo.get("my_obj");
    }

    @Override
    public void decode(Object obj) {
        //Write your code here. Set value to object
        
        System.out.println("value from db is: " + value);
        
        try{
            field.set(obj, new MyObj());
        }catch(Exception ex){
            
        }
        
    }
    
}
