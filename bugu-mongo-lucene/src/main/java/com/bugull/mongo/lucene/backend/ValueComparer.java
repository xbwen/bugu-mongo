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

package com.bugull.mongo.lucene.backend;

import com.bugull.mongo.lucene.annotations.Compare;
import com.bugull.mongo.utils.DataType;
import com.bugull.mongo.utils.FieldUtil;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ValueComparer {
    
    private Object obj;
    
    public ValueComparer(Object obj){
        this.obj = obj;
    }
    
    public boolean isFit(Field f, Compare compare, String value){
        boolean fit = false;
        switch(compare){
            case IS_EQUALS:
                fit = isEquals(f, value);
                break;
            case NOT_EQUALS:
                fit = notEquals(f, value);
                break;
            case GREATER_THAN:
                fit = greaterThan(f, value);
                break;
            case GREATER_THAN_EQUALS:
                fit = greaterThanEquals(f, value);
                break;
            case LESS_THAN:
                fit = lessThan(f, value);
                break;
            case LESS_THAN_EQUALS:
                fit = lessThanEquals(f, value);
                break;
            case IS_NULL:
                fit = isNull(FieldUtil.get(obj, f));
                break;
            case NOT_NULL:
                fit = notNull(FieldUtil.get(obj, f));
                break;
            default:
                break;
        }
        return fit;
    }
    
    private boolean isEquals(Field f, String value) {
        Object objValue = FieldUtil.get(obj, f);
        if(objValue == null){
            return false;
        }
        String objStr = objValue.toString();
        Class type = f.getType();
        if(DataType.isString(type)){
            return value.equals(objStr);
        }
        else if(DataType.isBoolean(type) || DataType.isBooleanObject(type)){
            return  Boolean.parseBoolean(objStr) == Boolean.parseBoolean(value);
        }
        else if(DataType.isChar(type) || DataType.isCharObject(type)){
            return objStr.charAt(0) == value.charAt(0);
        }
        else if(DataType.isInteger(type) || DataType.isIntegerObject(type)){
            return Integer.parseInt(objStr) == Integer.parseInt(value);
        }
        else if(DataType.isLong(type) || DataType.isLongObject(type)){
            return Long.parseLong(objStr) == Long.parseLong(value);
        }
        else if(DataType.isShort(type) || DataType.isShortObject(type)){
            return Short.parseShort(objStr) == Short.parseShort(value);
        }
        else if(DataType.isFloat(type) || DataType.isFloatObject(type)){
            return Float.parseFloat(objStr) == Float.parseFloat(value);
        }
        else if(DataType.isDouble(type) || DataType.isDoubleObject(type)){
            return Double.parseDouble(objStr) == Double.parseDouble(value);
        }
        else{
            return false;
        }
    }
    
    private boolean notEquals(Field f, String value) {
        Object objValue = FieldUtil.get(obj, f);
        if(objValue == null){
            return false;
        }else{
            return !isEquals(f, value);
        }
    }
    
    private boolean greaterThan(Field f, String value){
        Object objValue = FieldUtil.get(obj, f);
        if(objValue == null){
            return false;
        }
        String objStr = objValue.toString();
        Class type = f.getType();
        if(DataType.isInteger(type) || DataType.isIntegerObject(type)){
            return Integer.parseInt(objStr) > Integer.parseInt(value);
        }
        else if(DataType.isLong(type) || DataType.isLongObject(type)){
            return Long.parseLong(objStr) > Long.parseLong(value);
        }
        else if(DataType.isShort(type) || DataType.isShortObject(type)){
            return Short.parseShort(objStr) > Short.parseShort(value);
        }
        else if(DataType.isFloat(type) || DataType.isFloatObject(type)){
            return Float.parseFloat(objStr) > Float.parseFloat(value);
        }
        else if(DataType.isDouble(type) || DataType.isDoubleObject(type)){
            return Double.parseDouble(objStr) > Double.parseDouble(value);
        }
        else{
            return false;
        }
    }
    
    private boolean greaterThanEquals(Field f, String value){
        Object objValue = FieldUtil.get(obj, f);
        if(objValue == null){
            return false;
        }
        String objStr = objValue.toString();
        Class type = f.getType();
        if(DataType.isInteger(type) || DataType.isIntegerObject(type)){
            return Integer.parseInt(objStr) >= Integer.parseInt(value);
        }
        else if(DataType.isLong(type) || DataType.isLongObject(type)){
            return Long.parseLong(objStr) >= Long.parseLong(value);
        }
        else if(DataType.isShort(type) || DataType.isShortObject(type)){
            return Short.parseShort(objStr) >= Short.parseShort(value);
        }
        else if(DataType.isFloat(type) || DataType.isFloatObject(type)){
            return Float.parseFloat(objStr) >= Float.parseFloat(value);
        }
        else if(DataType.isDouble(type) || DataType.isDoubleObject(type)){
            return Double.parseDouble(objStr) >= Double.parseDouble(value);
        }
        else{
            return false;
        }
    }
    
    private boolean lessThan(Field f, String value){
        Object objValue = FieldUtil.get(obj, f);
        if(objValue == null){
            return false;
        }else{
            return !greaterThanEquals(f, value);
        }
    }
    
    private boolean lessThanEquals(Field f, String value){
        Object objValue = FieldUtil.get(obj, f);
        if(objValue == null){
            return false;
        }else{
            return !greaterThan(f, value);
        }
    }
    
    private boolean isNull(Object objValue){
        return objValue==null ? true : false;
    }
    
    private boolean notNull(Object objValue){
        return !isNull(objValue);
    }
    
}
