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

import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.DBQueryException;
import com.bugull.mongo.parallel.Parallelable;
import com.bugull.mongo.utils.IdUtil;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.ReferenceUtil;
import com.bugull.mongo.utils.SortUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Convenient class for creating queries.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class BuguQuery<T> implements Parallelable {
    
    protected final BuguDao<T> dao;
    
    protected DBObject slices;
    protected DBObject fields;
    protected boolean fieldsSpecified;  //default value is false
    
    protected DBObject condition = new BasicDBObject();
    
    protected String orderBy;
    protected int pageNumber;  //default value is zero
    protected int pageSize;  //default value is zero
    
    protected long maxTimeMS;
    
    protected boolean withoutCascade;
    
    public BuguQuery(BuguDao<T> dao){
        this.dao = dao;
    }
    
    private void appendEquals(String key, String op, Object value){
        Class<T> clazz = dao.getEntityClass();
        if(key.equals(Operator.ID)){
            Object dbId = IdUtil.toDbId(clazz, (String)value);
            append(Operator.ID, op, dbId);
        }
        else if(key.indexOf(".")!=-1){
            append(key, op, value);
        }
        else{
            Field f = FieldsCache.getInstance().getField(clazz, key);
            if(f.getAnnotation(Id.class) != null){
                Object dbId = IdUtil.toDbId(clazz, (String)value);
                append(Operator.ID, op, dbId);
            }
            else if(value instanceof BuguEntity){
                BuguEntity ent = (BuguEntity)value;
                Object refObj = ReferenceUtil.toDbReference(clazz, key, ent.getClass(), ent.getId());
                append(key, op, refObj);
            }
            else if(f.getType().isEnum()){
                if(value != null){
                    append(key, op, value.toString());
                }else{
                    append(key, op, null);
                }
            }
            else{
                append(key, op, value);
            }
        }
    }
    
    private void appendThan(String key, String op, Object value){
        Class<T> clazz = dao.getEntityClass();
        if(key.equals(Operator.ID)){
            Object dbId = IdUtil.toDbId(clazz, (String)value);
            append(Operator.ID, op, dbId);
        }
        else if(key.indexOf(".") != -1){
            append(key, op, value);
        }
        else{
            Field f = FieldsCache.getInstance().getField(clazz, key);
            if(f.getAnnotation(Id.class) != null){
                Object dbId = IdUtil.toDbId(clazz, (String)value);
                append(Operator.ID, op, dbId);
            }
            else{
                append(key, op, value);
            }
        }
    }
    
    private void appendIn(String key, String op, Object... values){
        if(key.equals(Operator.ID)){
            append(Operator.ID, op, toIds(values));
        }
        else if(key.indexOf(".") != -1){
            append(key, op, values);
        }
        else{
            Class<T> clazz = dao.getEntityClass();
            Field f = FieldsCache.getInstance().getField(clazz, key);
            if(f.getAnnotation(Id.class) != null){
                append(Operator.ID, op, toIds(values));
            }
            else if(values.length != 0 && values[0] instanceof BuguEntity){
                append(key, op, toReferenceList(key, values));
            }
            else if(f.getType().isEnum()){
                List<String> list = new ArrayList<String>();
                for(Object obj : values){
                    list.add(obj.toString());
                }
                append(key, op, list);
            }
            else{
                append(key, op, values);
            }
        }
    }
    
    /**
     * Be careful! this method is hard to understand, but it is correct.
     * @param key
     * @param op
     * @param value 
     */
    protected void append(String key, String op, Object value){
        if(op == null) {
            condition.put(key, value);
            return;
        }
        Object obj = condition.get(key);
        if(!(obj instanceof DBObject)) {
            DBObject dbo = new BasicDBObject(op, value);
            condition.put(key, dbo);
        } else {
            DBObject dbo = (DBObject)obj;
            dbo.put(op, value);
        }
    }
    
    private List<Object> toIds(Object... values){
        List<Object> idList = new ArrayList<Object>();
        Class<T> clazz = dao.getEntityClass();
        int len = values.length;
        for(int i=0; i<len; i++){
            if(values[i] != null){
                Object dbId = IdUtil.toDbId(clazz, (String)values[i]);
                idList.add(dbId);
            }
        }
        return idList;
    }
    
    private List<Object> toReferenceList(String key, Object... values){
        List<Object> refList = new ArrayList<Object>();
        Class<T> clazz = dao.getEntityClass();
        int len = values.length;
        for(int i=0; i<len; i++){
            if(values[i] != null){
                BuguEntity ent = (BuguEntity)values[i];
                Object refObj = ReferenceUtil.toDbReference(clazz, key, ent.getClass(), ent.getId());
                refList.add(refObj);
            }
        }
        return refList;
    }
    
    public BuguQuery<T> is(String key, Object value){
        appendEquals(key, null, value);
        return this;
    }
    
    public BuguQuery<T> notEquals(String key, Object value){
        appendEquals(key, Operator.NE, value);
        return this;
    }
    
    public BuguQuery<T> text(String value){
        return text(value, false);
    }
    
    public BuguQuery<T> text(String value, boolean caseSensitive){
        DBObject dbo = new BasicDBObject();
        dbo.put(Operator.SEARCH, value);
        dbo.put(Operator.CASE_SENSITIVE, caseSensitive);
        condition.put(Operator.TEXT, dbo);
        return this;
    }
    
    public BuguQuery<T> or(BuguQuery... querys){
        List list = (List)condition.get(Operator.OR);
        if(list == null){
            list = new ArrayList();
            condition.put(Operator.OR, list);
        }
        for(BuguQuery q : querys){
            list.add(q.getCondition());
        }
        return this;
    }
    
    public BuguQuery<T> and(BuguQuery... querys){
        List list = (List)condition.get(Operator.AND);
        if(list == null){
            list = new ArrayList();
            condition.put(Operator.AND, list);
        }
        for(BuguQuery q : querys){
            list.add(q.getCondition());
        }
        return this;
    }
    
    public BuguQuery<T> nor(BuguQuery... querys){
        List list = (List)condition.get(Operator.NOR);
        if(list == null){
            list = new ArrayList();
            condition.put(Operator.NOR, list);
        }
        for(BuguQuery q : querys){
            list.add(q.getCondition());
        }
        return this;
    }
    
    public BuguQuery<T> greaterThan(String key, Object value){
        appendThan(key, Operator.GT, value);
        return this;
    }
    
    public BuguQuery<T> greaterThanEquals(String key, Object value){
        appendThan(key, Operator.GTE, value);
        return this;
    }
    
    public BuguQuery<T> lessThan(String key, Object value){
        appendThan(key, Operator.LT, value);
        return this;
    }
    
    public BuguQuery<T> lessThanEquals(String key, Object value){
        appendThan(key, Operator.LTE, value);
        return this;
    }
    
    public BuguQuery<T> in(String key, List list){
        if(list == null || list.isEmpty()){
            throw new IllegalArgumentException("$in query with empty value is not allowed.");
        }
        return in(key, list.toArray());
    }
    
    public BuguQuery<T> in(String key, Object... values){
        if(values == null || values.length == 0){
            throw new IllegalArgumentException("$in query with empty value is not allowed.");
        }
        appendIn(key, Operator.IN, values);
        return this;
    }
    
    public BuguQuery<T> notIn(String key, List list){
        if(list == null || list.isEmpty()){
            throw new IllegalArgumentException("$nin query with empty value is not allowed.");
        }
        return notIn(key, list.toArray());
    }
    
    public BuguQuery<T> notIn(String key, Object... values){
        if(values == null || values.length == 0){
            throw new IllegalArgumentException("$nin query with empty value is not allowed.");
        }
        appendIn(key, Operator.NIN, values);
        return this;
    }
    
    public BuguQuery<T> all(String key, List list){
        if(list == null || list.isEmpty()){
            throw new IllegalArgumentException("$all query with empty value is not allowed.");
        }
        return all(key, list.toArray());
    }
    
    public BuguQuery<T> all(String key, Object... values){
        if(values == null || values.length == 0){
            throw new IllegalArgumentException("$all query with empty value is not allowed.");
        }
        append(key, Operator.ALL, values);
        return this;
    }
    
    /**
     * Note: the regex string must in Java style, not JavaScript style.
     * @param key
     * @param regexStr
     * @return 
     */
    public BuguQuery<T> regex(String key, String regexStr){
        append(key, null, Pattern.compile(regexStr));
        return this;
    }
    
    public BuguQuery<T> regexCaseInsensitive(String key, String regexStr){
        append(key, null, Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE));
        return this;
    }
    
    public BuguQuery<T> size(String key, int value){
        append(key, Operator.SIZE, value);
        return this;
    }
    
    public BuguQuery<T> mod(String key, int divisor, int remainder){
        append(key, Operator.MOD, new int[]{divisor, remainder});
        return this;
    }
    
    public BuguQuery<T> existsField(String key){
        append(key, Operator.EXISTS, Boolean.TRUE);
        return this;
    }
    
    public BuguQuery<T> notExistsField(String key){
        append(key, Operator.EXISTS, Boolean.FALSE);
        return this;
    }
    
    public BuguQuery<T> where(String whereStr){
        append(Operator.WHERE, null, whereStr);
        return this;
    }
    
    /**
     * @since mongoDB 3.6
     * @param jsonStr
     * @return 
     */
    public BuguQuery<T> expr(String jsonStr){
        DBObject expression = BasicDBObject.parse(jsonStr);
        return expr(expression);
    }
    
    /**
     * @since mongoDB 3.6
     * @param expression
     * @return 
     */
    public BuguQuery<T> expr(DBObject expression){
        append(Operator.EXPR, null, expression);
        return this;
    }
    
    public BuguQuery<T> slice(String key, long num){
        DBObject dbo = new BasicDBObject(Operator.SLICE, num);
        return addSlice(key, dbo);
    }
    
    public BuguQuery<T> slice(String key, long begin, long length){
        DBObject dbo = new BasicDBObject(Operator.SLICE, new Long[]{begin, length});
        return addSlice(key, dbo);
    }
    
    private BuguQuery<T> addSlice(String key, DBObject dbo){
        if(slices == null){
            slices = new BasicDBObject();
        }
        slices.put(key, dbo);
        dao.getKeyFields().put(key, dbo);
        if(fields == null){
            fields = new BasicDBObject();
        }
        fields.put(key, dbo);
        return this;
    }
    
    public BuguQuery<T> returnFields(String... fieldNames){
        return specifyFields(1, fieldNames);
    }
    
    public BuguQuery<T> notReturnFields(String... fieldNames){
        return specifyFields(0, fieldNames);
    }
    
    private BuguQuery<T> specifyFields(int value, String... fieldNames){
        if(fields == null){
            fields = new BasicDBObject();
        }
        for(String field : fieldNames){
            //do not replace the $slice, if has been set
            if(fields.get(field)==null){
                fields.put(field, value);
            }
        }
        fieldsSpecified = true;
        return this;
    }
    
    public BuguQuery<T> maxTimeMS(long maxTimeMS){
        this.maxTimeMS = maxTimeMS;
        return this;
    }
    
    /**
     * 
     * @param orderBy JSON string to sort. 
     * @return 
     */
    public BuguQuery<T> sort(String orderBy){
        this.orderBy = orderBy;
        return this;
    }
    
    /**
     * Order by a single key ascend
     * @param key
     * @return 
     */
    public BuguQuery<T> sortAsc(String key){
        this.orderBy = SortUtil.asc(key);
        return this;
    }
    
    /**
     * Order by a single key descend
     * @param key
     * @return 
     */
    public BuguQuery<T> sortDesc(String key){
        this.orderBy = SortUtil.desc(key);
        return this;
    }
    
    public BuguQuery<T> pageNumber(int pageNumber){
        this.pageNumber = pageNumber;
        return this;
    }
    
    public BuguQuery<T> pageSize(int pageSize){
        this.pageSize = pageSize;
        return this;
    }
    
    public T result(){
        if(orderBy!=null || pageNumber!=0 || pageSize!=0){
            throw new DBQueryException("You should use results() to get a list, when you use sorting or pagination");
        }
        DBCollection coll = dao.getCollection();
        DBObject dbo;
        if(fieldsSpecified){
            dbo = coll.findOne(condition, fields);
        }else if(slices != null){
            dbo = coll.findOne(condition, slices);
        }else{
            dbo = coll.findOne(condition);
        }
        return MapperUtil.fromDBObject(dao.getEntityClass(), dbo, withoutCascade);
    }
    
    @Override
    public List<T> results(){
        DBObject projection;
        if(fieldsSpecified){
            projection = fields;
        }else{
            projection = dao.getKeyFields();
        }
        
        DBCollectionFindOptions options = new DBCollectionFindOptions();
        options.projection(projection);
        if(maxTimeMS > 0){
            options.maxTime(maxTimeMS, TimeUnit.MILLISECONDS);
        }
        if(orderBy != null){
            options.sort(SortUtil.getSort(orderBy));
        }
        if(pageNumber>0 && pageSize>0){
            options.skip((pageNumber-1) * pageSize);
            options.limit(pageSize);
        }
        
        DBCollection coll = dao.getCollection();
        DBCursor cursor = coll.find(condition, options);
        return MapperUtil.toList(dao.getEntityClass(), cursor, withoutCascade);
    }
    
    /**
     * If collection is very large, count() will be slow, you should use countFast().
     * @return 
     */
    public long count(){
        return dao.getCollection().count(condition);
    }
    
    /**
     * If collection is very large, count() will be slow, you should use countFast().
     * @since mongoDB 3.4
     * @return 
     */
    public long countFast(){
        long counter = 0;
        Iterable<DBObject> results = dao.aggregate().match(condition).count("counter").results();
        Iterator<DBObject> it = results.iterator();
        if(it.hasNext()){
            DBObject dbo = it.next();
            String s = dbo.get("counter").toString();
            counter = Long.parseLong(s);
        }
        return counter;
    }
    
    public boolean exists(){
        DBObject dbo = dao.getCollection().findOne(condition);
        return dbo != null;
    }
    
    /**
     * distinct() on large collection will fail. you should use distinctLarge().
     * @param key
     * @return 
     */
    public List distinct(String key){
        return dao.getCollection().distinct(key, condition);
    }
    
    /**
     * distinct() on large collection will fail. you should use distinctLarge().
     * @param key
     * @return 
     */
    public List distinctLarge(String key){
        List list = new ArrayList();
        Iterable<DBObject> results = dao.aggregate().match(condition).group("{_id:'$" + key + "'}").results();
        for(DBObject dbo : results){
            list.add(dbo.get("_id"));
        }
        return list;
    }

    public DBObject getCondition() {
        return condition;
    }
    
    public void setCondition(DBObject condition) {
        this.condition = condition;
    }
    
    public DBObject getSort(){
        if(orderBy == null){
            return null;
        }
        return SortUtil.getSort(orderBy);
    }

    public void setWithoutCascade(boolean withoutCascade) {
        this.withoutCascade = withoutCascade;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("query condition:");
        sb.append(condition.toString());
        if(orderBy != null){
            sb.append(" sort:");
            sb.append(SortUtil.getSort(orderBy).toString());
        }
        if(pageNumber>0 && pageSize>0){
            sb.append(" skip:");
            sb.append((pageNumber-1) * pageSize);
            sb.append(" limit:");
            sb.append(pageSize);
        }
        return sb.toString();
    }
    
}
