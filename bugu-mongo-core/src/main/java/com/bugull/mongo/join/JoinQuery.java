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
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.exception.FieldException;
import com.bugull.mongo.parallel.Parallelable;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.SortUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Join query on two collection. It's based on aggregation, but easy to use.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class JoinQuery<L, R> implements Parallelable {
    
    private final static Logger logger = LogManager.getLogger(JoinQuery.class.getName());
    
    protected final BuguDao<L> dao;
    
    protected DBObject fields;
    protected boolean fieldsSpecified;  //default value is false
    
    protected String[] leftReturnFields;
    protected String[] rightReturnFields;
    protected String[] leftNotReturnFields;
    protected String[] rightNotReturnFields;
    
    protected String leftOrderBy;
    protected String rightOrderBy;
    protected int pageNumber;  //default value is zero
    protected int pageSize;  //default value is zero
    
    protected Class<R> rightColl;  //the right collection to join
    
    protected String leftKey;
    protected String rightKey;
    
    protected BuguQuery leftMatch;
    protected BuguQuery rightMatch;
    
    public JoinQuery(BuguDao<L> dao, Class<R> rightColl){
        this.dao = dao;
        this.rightColl = rightColl;
    }
    
    public JoinQuery<L, R> keys(String leftKey, String rightKey){
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        return this;
    }
    
    public JoinQuery<L, R> leftMatch(BuguQuery leftMatch){
        this.leftMatch = leftMatch;
        return this;
    }
    
    public JoinQuery<L, R> rightMatch(BuguQuery rightMatch){
        this.rightMatch = rightMatch;
        return this;
    }
    
//    public JoinQuery<L, R> crossEquals(String leftField, String rightField){
//        
//    }
//    
//    public JoinQuery<L, R> crossNotEquals(String leftField, String rightField){
//        
//    }
    
    public JoinQuery<L, R> returnFields(String[] leftReturnFields, String[] rightReturnFields){
        this.leftReturnFields = leftReturnFields;
        this.rightReturnFields = rightReturnFields;
        return this;
    }
    
    public JoinQuery<L, R> notReturnFields(String[] leftNotReturnFields, String[] rightNotReturnFields){
        this.leftNotReturnFields = leftNotReturnFields;
        this.rightNotReturnFields = rightNotReturnFields;
        return this;
    }
    
    public JoinQuery<L, R> sort(String leftOrderBy, String rightOrderBy){
        this.leftOrderBy = leftOrderBy;
        this.rightOrderBy = rightOrderBy;
        return this;
    }
    
    public JoinQuery<L, R> pageNumber(int pageNumber){
        this.pageNumber = pageNumber;
        return this;
    }
    
    public JoinQuery<L, R> pageSize(int pageSize){
        this.pageSize = pageSize;
        return this;
    }
    
    @Override
    public List<JoinResult<L, R>> results(){
        BuguAggregation<L> agg = dao.aggregate();
        
        //match the left
        if(leftMatch != null){
            agg.match(leftMatch);
        }
        
        //check left key
        Field leftField = null;
        try{
            leftField = FieldsCache.getInstance().getField(dao.getEntityClass(), leftKey);
        }catch(FieldException ex){
            logger.error(ex.getMessage(), ex);
        }
        if(leftField != null){
            Id leftId = leftField.getAnnotation(Id.class);
            if(leftId != null){
                leftKey = Operator.ID;
            }
        }
        
        //check right key
        Field rightField = null;
        try{
            rightField = FieldsCache.getInstance().getField(rightColl, rightKey);
        }catch(FieldException ex){
            logger.error(ex.getMessage(), ex);
        }
        if(rightField != null){
            Id rightId = rightField.getAnnotation(Id.class);
            if(rightId != null){
                rightKey = Operator.ID;
            }
        }
        
        //the as field
        Class<L> leftColl = dao.getEntityClass();
        String leftCollName = MapperUtil.getEntityName(leftColl);
        String rightCollName = MapperUtil.getEntityName(rightColl);
        String as = leftCollName + "_" + leftCollName.length() + "_" + rightCollName + "_" + rightCollName.length();  //make sure the as field does not exists

        //lookup
        agg.lookup(new Lookup(rightCollName, leftKey, rightKey, as));
        
        //unwind
        agg.unwind(as);

        //match the real right condition after lookup
        if(rightMatch != null){
            DBObject cond = rightMatch.getCondition();
            DBObject realRightMatch = new BasicDBObject();
            Map map = cond.toMap();
            Set<Entry> set = map.entrySet();
            for(Entry entry : set){
                String key = entry.getKey().toString();
                realRightMatch.put(as + "." + key, entry.getValue());
            }
            agg.match(realRightMatch);
        }
        
        //sort
        if(rightOrderBy == null){
            if(leftOrderBy != null){
                agg.sort(leftOrderBy);
            }
        }
        else{
            DBObject realSort = new BasicDBObject();
            if(leftOrderBy != null){
                DBObject leftSort = SortUtil.getSort(leftOrderBy);
                Map map = leftSort.toMap();
                Set<Entry> set = map.entrySet();
                for(Entry entry : set){
                    realSort.put(entry.getKey().toString(), entry.getValue());
                }
            }
            DBObject rightSort = SortUtil.getSort(rightOrderBy);
            Map map = rightSort.toMap();
            Set<Entry> set = map.entrySet();
            for(Entry entry : set){
                String key = entry.getKey().toString();
                realSort.put(as + "." + key, entry.getValue());
            }
            agg.sort(realSort);
        }
        
        //project retrun fields
        if(rightReturnFields == null){
            if(leftReturnFields != null){
                agg.projectInclude(leftReturnFields);
            }
        }
        else{
            int len = rightReturnFields.length;
            for(int i=0; i<len; i++){
                rightReturnFields[i] = as + "." + rightReturnFields[i];
            }
            if(leftReturnFields == null){
                agg.projectInclude(rightReturnFields);
            }else{
                String[] returnFields = new String[leftReturnFields.length + rightReturnFields.length];
                System.arraycopy(leftReturnFields, 0, returnFields, 0, leftReturnFields.length);
                System.arraycopy(rightReturnFields, 0, returnFields, leftReturnFields.length, rightReturnFields.length);
                agg.projectInclude(returnFields);
            }
        }
        
        //project not return fields
        if(leftNotReturnFields != null){
            agg.projectExclude(leftNotReturnFields);
        }
        if(rightNotReturnFields != null){
            int len = rightNotReturnFields.length;
            for(int i=0; i<len; i++){
                rightNotReturnFields[i] = as + "." + rightNotReturnFields[i];
            }
            agg.projectExclude(rightNotReturnFields);
        }
        
        //group it
        //do group and push here
        List<String> columns = FieldsCache.getInstance().getAllColumnsName(leftColl);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("_id:");
        sb.append("{"); //start of _id
        for(String col : columns){
            sb.append(col).append(":").append("'$").append(col).append("',");
        }
        sb.deleteCharAt(sb.length()-1); //delete the last comma(,)
        sb.append("}");  //end of _id
        sb.append(",");
        sb.append(as).append(":").append("{$push:'$").append(as).append("'}");
        sb.append("}");
        
        agg.group(sb.toString());
        
        //skip and limit
        if(pageNumber>0 && pageSize>0){
            agg.skip((pageNumber-1)*pageSize).limit(pageSize);
        }
        
        //return JoinResult
        List<JoinResult<L, R>> list = new ArrayList<JoinResult<L, R>>();
        Iterable<DBObject> it = agg.results();
        for(DBObject dbo : it){
            System.out.println(dbo.toString());
            JoinResult<L, R> result = new JoinResult<L, R>();
            DBObject _id = (DBObject)dbo.get("_id");
            L leftEntity = MapperUtil.fromDBObject(leftColl, _id);
            result.setLeftEntity(leftEntity);
            Object asArr = dbo.get(as);
            if(asArr != null){
                Object arr = decodeArray(asArr);
                result.setRightEntity((R[])arr);
            }
            list.add(result);
        }
        return list;

    }
    
    private Object decodeArray(Object val){
        List list = (ArrayList)val;
        int size = list.size();
        Object arr = Array.newInstance(rightColl, size);
        for(int i=0; i<size; i++){
            Object item = list.get(i);
            if(item != null){
                DBObject o = (DBObject)item;
                Array.set(arr, i, MapperUtil.fromDBObject(rightColl, o));
            }else{
                Array.set(arr, i, null);
            }
        }
        return arr;
    }
    
}
