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

import com.bugull.mongo.BuguAggregation;
import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.agg.Lookup;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.FieldException;
import com.bugull.mongo.parallel.Parallelable;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class JoinQuery<T> implements Parallelable {
    
    private final static Logger logger = LogManager.getLogger(JoinQuery.class.getName());
    
    protected final BuguDao<T> dao;
    
    protected DBObject fields;
    protected boolean fieldsSpecified;  //default value is false
    
    protected String[] returnFields;
    protected String[] notReturnFields;
    
    protected String orderBy;
    protected int pageNumber;  //default value is zero
    protected int pageSize;  //default value is zero
    
    protected Class<?> rightTable;  //the right table to join
    
    protected String leftKey;
    protected String rightKey;
    
    protected BuguQuery leftCondition;
    protected BuguQuery rightCondition;
    
    public JoinQuery(BuguDao<T> dao, Class<?> rightTable){
        this.dao = dao;
        this.rightTable = rightTable;
    }
    
    public JoinQuery<T> keys(String leftKey, String rightKey){
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        return this;
    }
    
    public JoinQuery<T> leftCondition(BuguQuery leftCondition){
        this.leftCondition = leftCondition;
        return this;
    }
    
    public JoinQuery<T> rightCondition(BuguQuery rightCondition){
        this.rightCondition = rightCondition;
        return this;
    }
    
    public JoinQuery<T> returnFields(String... returnFields){
        this.returnFields = returnFields;
        return this;
    }
    
    public JoinQuery<T> notReturnFields(String... notReturnFields){
        this.notReturnFields = notReturnFields;
        return this;
    }
    
    public JoinQuery<T> sort(String orderBy){
        this.orderBy = orderBy;
        return this;
    }
    
    public JoinQuery<T> pageNumber(int pageNumber){
        this.pageNumber = pageNumber;
        return this;
    }
    
    public JoinQuery<T> pageSize(int pageSize){
        this.pageSize = pageSize;
        return this;
    }
    
    @Override
    public List<T> results(){
        BuguAggregation<T> agg = dao.aggregate();
        
        //step 1:
        if(leftCondition != null){
            agg.match(leftCondition);
        }
        
        //To do:
        //before lookup, have to check: 
        //if rightKey is _id, and the leftKey is DBRef, need to project DBRef to ObjectId
        //use ref.$id to get ObjectId value
        Field leftField = null;
        try{
            leftField = FieldsCache.getInstance().getField(dao.getClass(), leftKey);
        }catch(FieldException ex){
            logger.error(ex.getMessage(), ex);
        }
        
        Ref ref = leftField.getAnnotation(Ref.class);
        if(ref != null){
            
        }else{
            RefList refList = leftField.getAnnotation(RefList.class);
            if(refList != null){
                
            }
        }
        
        Field rightField = null;
        try{
            rightField = FieldsCache.getInstance().getField(rightTable, rightKey);
        }catch(FieldException ex){
            logger.error(ex.getMessage(), ex);
        }
        if(rightField!=null && rightField.getAnnotation(Id.class)!=null){
            rightKey = Operator.ID;
        }
        
        //the as field
        Class<T> leftTable = dao.getEntityClass();
        String leftTableName = MapperUtil.getEntityName(leftTable);
        String rightTableName = MapperUtil.getEntityName(rightTable);
        String as = leftTableName + "_" + rightTableName + "_" + leftTableName.length() + "_" + rightTableName.length();  //make sure the as field does not exists

        agg.lookup(new Lookup(rightTableName, leftKey, rightKey, as));
        
        //match the real right condition after lookup
        if(rightCondition != null){
            DBObject cond = rightCondition.getCondition();
            DBObject realCondition = new BasicDBObject();
            Map map = cond.toMap();
            Set<Entry> set = map.entrySet();
            for(Entry entry : set){
                String key = (String)entry.getKey();
                realCondition.put(as + "." + key, entry.getValue());
            }
            agg.match(realCondition);
        }
        
        if(returnFields != null){
            agg.projectInclude(returnFields);
        }
        if(notReturnFields != null){
            agg.projectExclude(notReturnFields);
        }
        if(orderBy != null){
            agg.sort(orderBy);
        }
        if(pageNumber>0 && pageSize>0){
            agg.skip((pageNumber-1)*pageSize).limit(pageSize);
        }
        
        Iterable<DBObject> results = agg.results();
        return MapperUtil.toList(leftTable, results);
    }
    
}
