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

package com.bugull.mongo.lucene.listener;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.lucene.annotations.IndexRef;
import com.bugull.mongo.lucene.annotations.IndexRefList;
import com.bugull.mongo.lucene.backend.IndexJob;
import com.bugull.mongo.lucene.backend.IndexUpdateJob;
import com.bugull.mongo.access.InternalDao;
import com.bugull.mongo.utils.ReferenceUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class RefEntityListener {
    
    private Set<Class<?>> refBySet;
    
    public RefEntityListener(Set<Class<?>> refBySet){
        this.refBySet = refBySet;
    }
    
    public void entityChanged(Class<?> refClass, String id){
        for(Class<?> cls : refBySet){
            Field[] fields = FieldsCache.getInstance().get(cls);
            for(Field f : fields){
                processField(refClass, id, cls, f);
            }
        }
    }
    
    private void processField(Class<?> refClass, String id, Class<?> cls, Field f){
        boolean match = false;
        String fieldName = f.getName();
        Ref ref = f.getAnnotation(Ref.class);
        if(ref!=null && f.getType().equals(refClass) && f.getAnnotation(IndexRef.class)!=null){
            match = true;
            String name = ref.name();
            if(!name.equals(Default.NAME)){
                fieldName = name;
            }
        }
        else{
            RefList refList = f.getAnnotation(RefList.class);
            if(refList!=null && f.getAnnotation(IndexRefList.class)!=null){
                Class<?> c;
                Class<?> type = f.getType();
                if(type.isArray()){
                    c = type.getComponentType();  //for Array
                }else{
                    ParameterizedType paramType = (ParameterizedType)f.getGenericType();
                    Type[] types = paramType.getActualTypeArguments();
                    if(types.length == 1){
                        c = (Class)types[0];  //for List, Set
                    }else{
                        c = (Class)types[1];  //for Map
                    }
                }
                if(c!=null && c.equals(refClass)){
                    match = true;
                    String name = refList.name();
                    if(!name.equals(Default.NAME)){
                        fieldName = name;
                    }
                }
            }
        }
        if(match){
            InternalDao dao = DaoCache.getInstance().get(cls);
            Object refObj = ReferenceUtil.toDbReference(cls, fieldName, refClass, id);
            DBObject query = new BasicDBObject(fieldName, refObj);
            List<BuguEntity> list = dao.findNotLazily(query);
            for(BuguEntity o : list){
                IndexJob job = new IndexUpdateJob(o);
                job.doJob();
            }
        }
    }
    
}
