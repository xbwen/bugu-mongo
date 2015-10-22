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
package com.bugull.mongo;

import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.utils.IdUtil;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.ReferenceUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Convenient class for execute atomic update.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguUpdater<T> {
    
    private BuguDao<T> dao;
    
    private final DBObject modifier = new BasicDBObject();
    
    public BuguUpdater(BuguDao<T> dao){
        this.dao = dao;
    }
    
    private Object checkSingleValue(String key, Object value){
        Class<T> clazz = dao.getEntityClass();
        Object result = value;
        if(value instanceof BuguEntity){
            BuguEntity be = (BuguEntity)value;
            result = ReferenceUtil.toDbReference(clazz, key, be.getClass(), be.getId());
        }else if(!(value instanceof DBObject) && 
                FieldsCache.getInstance().isEmbedField(clazz, key)){
            result = MapperUtil.toDBObject(value);
        }
        return result;
    }
    
    private Object checkArrayValue(String key, Object value){
        Class<T> clazz = dao.getEntityClass();
        Object result = value;
        if(value instanceof BuguEntity){
            BuguEntity be = (BuguEntity)value;
            result = ReferenceUtil.toDbReference(clazz, key, be.getClass(), be.getId());
        }else if(!(value instanceof DBObject) && 
                FieldsCache.getInstance().isEmbedListField(clazz, key)){
            result = MapperUtil.toDBObject(value);
        }
        return result;
    }
    
    private void append(String op, String key, Object value){
        Object obj = modifier.get(op);
        DBObject dbo;
        if(!(obj instanceof DBObject)) {
            dbo = new BasicDBObject(key, value);
            modifier.put(op, dbo);
        } else {
            dbo = (DBObject)modifier.get(op);
            dbo.put(key, value);
        }
    }
    
    /**
     * execute the update operation on a single entity.
     * @param t
     * @return 
     */
    public WriteResult execute(T t){
        BuguEntity ent = (BuguEntity)t;
        return execute(ent.getId());
    }
    
    /**
     * execute the update operation on a single entity.
     * @param id
     * @return 
     */
    public WriteResult execute(String id){
        Class<T> clazz = dao.getEntityClass();
        DBObject condition = new BasicDBObject(Operator.ID, IdUtil.toDbId(clazz, id));
        WriteResult wr = dao.getCollection().update(condition, modifier, false, false, dao.getWriteConcern()); //update one
        if(!dao.getListenerList().isEmpty()){
            BuguEntity entity = (BuguEntity)dao.findOne(id);
            dao.notifyUpdated(entity);
        }
        return wr;
    }
    
    /**
     * execute the update operation on multi entity.
     * @param query
     * @return 
     */
    public WriteResult execute(BuguQuery query){
        return execute(query.getCondition());
    }
    
    /**
     * execute the update operation on all entity.
     * @return 
     */
    public WriteResult execute(){
        return execute(new BasicDBObject());
    }
    
    private WriteResult execute(DBObject condition){
        List ids = null;
        if(!dao.getListenerList().isEmpty()){
            ids = dao.getCollection().distinct(Operator.ID, condition);
        }
        WriteResult wr = dao.getCollection().update(condition, modifier, false, true, dao.getWriteConcern());  //update multi
        if(ids != null){
            DBObject in = new BasicDBObject(Operator.IN, ids);
            DBCursor cursor = dao.getCollection().find(new BasicDBObject(Operator.ID, in));
            List<T> list = MapperUtil.toList(dao.getEntityClass(), cursor);
            for(T t : list){
                dao.notifyUpdated((BuguEntity)t);
            }
        }
        return wr;
    }
    
    /**
     * Update entity's attribute.
     * Notice: EmbedList and RefList fields is not supported yet.
     * @param key the field's name
     * @param value the field's new value
     * @return 
     */
    public BuguUpdater<T> set(String key, Object value){
        value = checkSingleValue(key, value);
        append(Operator.SET, key, value);
        return this;
    }
    
    /**
     * Update entities with new key/value pairs.
     * Notice: the Map values must can be converted to DBObject.
     * @param map
     * @return 
     */
    public BuguUpdater<T> set(Map<String, Object> map){
        Set<Entry<String, Object>> set = map.entrySet();
        for(Entry<String, Object> entry : set){
            set(entry.getKey(), entry.getValue());
        }
        return this;
    }
    
    /**
     * Remove one filed(column) of an entity.
     * @param key the field name
     * @return 
     */
    public BuguUpdater<T> unset(String key){
        append(Operator.UNSET, key, 1);
        return this;
    }
    
     /**
     * Remove many fileds(column) of an entity.
     * @param keys the fields' name
     * @return 
     */
    public BuguUpdater<T> unset(String... keys){
        for(String key : keys){
            unset(key);
        }
        return this;
    }
    
    /**
     * Increase a numeric field
     * @param key the field's name
     * @param value the numeric value to be added. It can be positive or negative integer, long, float, double.
     * @return 
     */
    public BuguUpdater<T> inc(String key, Object value){
        append(Operator.INC, key, value);
        return this;
    }
    
    /**
     * Multiply the value of a field by a number. 
     * @param key the field's name
     * @param value the numeric value to multiply
     * @return 
     */
    public BuguUpdater<T> mul(String key, Object value){
        append(Operator.MUL, key, value);
        return this;
    }
    
    /**
     * Adds a value to an array unless the value is already present, 
     * in which case does nothing to that array.
     * @param key
     * @param value
     * @return 
     */
    public BuguUpdater<T> addToSet(String key, Object value){
        append(Operator.ADD_TO_SET, key, value);
        return this;
    }
    
    /**
     * Add an element to entity's array/list/set field.
     * @param key the field's name
     * @param value the element to add
     * @return 
     */
    public BuguUpdater<T> push(String key, Object value){
        value = checkArrayValue(key, value);
        append(Operator.PUSH, key, value);
        return this;
    }
    
    /**
     * Add each element in a list to the specified field.
     * @param key the field's name
     * @param valueList the list contains each element
     * @return 
     */
    public BuguUpdater<T> pushEach(String key, List valueList){
        int len = valueList.size();
        Object[] values = new Object[len];
        for(int i=0; i<len; i++){
            values[i] = checkArrayValue(key, valueList.get(i));
        }
        DBObject each = new BasicDBObject(Operator.EACH, values);
        append(Operator.PUSH, key, each);
        return this;
    }
    
    /**
     * Remove an element from entity's array/list/set field.
     * @param key the field's name
     * @param value the element to remove
     * @return 
     */
    public BuguUpdater<T> pull(String key, Object value){
        value = checkArrayValue(key, value);
        append(Operator.PULL, key, value);
        return this;
    }
    
    /**
     * Removes all instances of the specified values from an existing array.
     * @param key
     * @param valueArray
     * @return 
     */
    public BuguUpdater<T> pullAll(String key, Object... valueArray){
        int len = valueArray.length;
        Object[] values = new Object[len];
        for(int i=0; i<len; i++){
            values[i] = checkArrayValue(key, valueArray[i]);
        }
        append(Operator.PULL_ALL, key, values);
        return this;
    }
    
    /**
     * Remove the first element from the array/list/set field
     * @param key the field's name
     * @return 
     */
    public BuguUpdater<T> popFirst(String key){
        append(Operator.POP, key, -1);
        return this;
    }
    
    /**
     * Remove the last element from the array/list/set field
     * @param key the field's name
     * @return 
     */
    public BuguUpdater<T> popLast(String key){
        append(Operator.POP, key, 1);
        return this;
    } 
    
    /**
     * Update the value of the field to a specified value if the specified value is less than the current value of the field.
     * If the field does not exists, this operation sets the field to the specified value. 
     * @param key the field's name
     * @param value the specified value
     * @return 
     */
    public BuguUpdater<T> min(String key, Object value){
        append(Operator.MIN, key, value);
        return this;
    }
    
    /**
     * updates the value of the field to a specified value if the specified value is greater than the current value of the field.
     * If the field does not exists, this operation sets the field to the specified value. 
     * @param key the field's name
     * @param value the specified value
     * @return 
     */
    public BuguUpdater<T> max(String key, Object value){
        append(Operator.MAX, key, value);
        return this;
    }
    
    /**
     * Performs a bitwise update of a field
     * @param key the field's name
     * @param value the bitwise value
     * @param bitwise the enum type of bitwise operation: AND,OR,XOR
     * @return 
     */
    public BuguUpdater<T> bitwise(String key, int value, Bitwise bitwise){
        DBObject logic = new BasicDBObject(checkBitwise(bitwise), value);
        append(Operator.BIT, key, logic);
        return this;
    }
    
    private String checkBitwise(Bitwise bitwise){
        String result = null;
        switch(bitwise){
            case AND:
                result = "and";
                break;
            case OR:
                result = "or";
                break;
            case XOR:
                result = "xor";
                break;
            default:
                break;
        }
        return result;
    }
    
    public enum Bitwise { AND, OR, XOR }
    
}
