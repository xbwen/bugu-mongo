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

package com.bugull.mongo.lucene.handler;

import com.bugull.mongo.utils.DataType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class AbstractFieldHandler implements FieldHandler{
    
    protected final static String JOIN = ";";
    
    protected Object obj;
    protected Field field;
    protected String prefix;
    
    protected AbstractFieldHandler(Object obj, Field field, String prefix){
        this.obj = obj;
        this.field = field;
        this.prefix = prefix;
    }
    
    protected String getArrayString(Object value, Class type){
        StringBuilder sb = new StringBuilder();
        if(DataType.isDate(type)){
            Date[] arr = (Date[])value;
            for(Date e : arr){
                sb.append(e.getTime()).append(JOIN);
            }
        }
        else if(DataType.isTimestamp(type)){
            Timestamp[] arr = (Timestamp[])value;
            for(Timestamp e : arr){
                sb.append(e.getTime()).append(JOIN);
            }
        }
        else{
            int len = Array.getLength(value);
            for(int i=0; i<len; i++){
                sb.append(Array.get(value, i).toString()).append(JOIN);
            }
        }
        return sb.toString();
    }
    
}
