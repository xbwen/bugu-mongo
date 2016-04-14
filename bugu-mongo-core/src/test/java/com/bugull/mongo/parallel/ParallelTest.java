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

package com.bugull.mongo.parallel;

import com.bugull.mongo.BuguAggregation;
import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.utils.SortUtil;
import com.mongodb.DBObject;
import java.util.List;
import java.util.Random;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ParallelTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        BuguDao<LargeData> dao = DaoCache.getInstance().get(LargeData.class);
        //insert lots of data
        for(int i=0; i<1000000; i++){
            LargeData data = new LargeData();
            data.setNum(i);
            data.setRandomValue(new Random().nextDouble());
            dao.save(data);
        }
        
        System.out.println("count: " + dao.count());
        
        disconnectDB();
    }
    
    //@Test
    public void testNormalQuery(){
        connectDB();
        
        BuguDao<LargeData> dao = new BuguDao(LargeData.class);
        long begin = System.currentTimeMillis();
        Iterable<DBObject> list1 = dao.aggregate().group("{_id:null, maxValue:{$max:'$randomValue'}}").results();
        long step1 = System.currentTimeMillis();
        System.out.println("step1: " + (step1 - begin));
        Iterable<DBObject> list2 = dao.aggregate().group("{_id:null, minValue:{$min:'$randomValue'}}").results();
        long step2 = System.currentTimeMillis();
        System.out.println("step2: " + (step2 - step1));
        Iterable<DBObject> list3 = dao.aggregate().group("{_id:null, avgValue:{$avg:'$randomValue'}}").results();
        long step3 = System.currentTimeMillis();
        System.out.println("step3: " + (step3 - step2));
        List<LargeData> list4 = dao.query().greaterThan("randomValue", 0.8).sort(SortUtil.aesc("randomValue")).pageNumber(1).pageSize(10).results();
        long end = System.currentTimeMillis();
        System.out.println("step4: " + (end - step3));
        System.out.println("total time:" + (end - begin));
        
        disconnectDB();
    }
    
    @Test
    public void testParallelQuery(){
        connectDB();
        
        BuguDao<LargeData> dao = new BuguDao(LargeData.class);
        long begin = System.currentTimeMillis();
        BuguAggregation<LargeData> agg1 = dao.aggregate().group("{_id:null, maxValue:{$max:'$randomValue'}}");
        BuguAggregation<LargeData> agg2 = dao.aggregate().group("{_id:null, minValue:{$min:'$randomValue'}}");
        BuguAggregation<LargeData> agg3 = dao.aggregate().group("{_id:null, avgValue:{$avg:'$randomValue'}}");
        BuguQuery<LargeData> q4 = dao.query().greaterThan("randomValue", 0.8).sort(SortUtil.aesc("randomValue")).pageNumber(1).pageSize(10);
        List<Iterable> list = dao.parallelQuery(agg1, agg2, agg3, q4);
        long end = System.currentTimeMillis();
        System.out.println("use time:" + (end - begin));
        
        //How to get the result data? Just like this:
        
        Iterable<DBObject> it1 = (Iterable<DBObject>)list.get(0);
        DBObject dbo1 = it1.iterator().next();
        System.out.println("maxValue: " + dbo1.get("maxValue"));
        
        Iterable<DBObject> it2 = (Iterable<DBObject>)list.get(1);
        DBObject dbo2 = it2.iterator().next();
        System.out.println("minValue: " + dbo2.get("minValue"));
        
        Iterable<DBObject> it3 = (Iterable<DBObject>)list.get(2);
        DBObject dbo3 = it3.iterator().next();
        System.out.println("avgValue: " + dbo3.get("avgValue"));
        
        List<LargeData> it4 = (List<LargeData>)list.get(3);
        for(LargeData data : it4){
            System.out.println("randomValue: " + data.getRandomValue());
        }
        disconnectDB();
    }
    
}
