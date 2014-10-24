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
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.listener.EntityListener;
import com.bugull.mongo.lucene.annotations.IndexRefBy;
import com.bugull.mongo.lucene.utils.IndexFilterChecker;
import com.bugull.mongo.lucene.backend.IndexInsertJob;
import com.bugull.mongo.lucene.backend.IndexJob;
import com.bugull.mongo.lucene.backend.IndexDeleteJob;
import com.bugull.mongo.lucene.backend.IndexUpdateJob;
import com.bugull.mongo.lucene.utils.IndexChecker;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Change the relative lucene index when an entity is changed.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class LuceneEntityListener implements EntityListener{
    
    private Class<?> clazz;
    private boolean needListener;
    private boolean onlyIdRefBy;
    private RefEntityListener refListener;
    
    public LuceneEntityListener(Class<?> clazz){
        needListener = IndexChecker.needListener(clazz);
        if(needListener){
            this.clazz = clazz;
            Set<Class<?>> refBySet = new HashSet<Class<?>>();
            boolean byId = false;
            boolean byOther = false;
            Field[] fields = FieldsCache.getInstance().get(clazz);
            for(Field f : fields){
                IndexRefBy irb = f.getAnnotation(IndexRefBy.class);
                if(irb != null){
                    Class<?>[] cls = irb.value();
                    refBySet.addAll(Arrays.asList(cls));
                    if(f.getAnnotation(Id.class) != null){
                        byId = true;
                    }else{
                        byOther = true;
                    }
                }
            }
            if(refBySet.size() > 0){
                refListener = new RefEntityListener(refBySet);
                if(byId && !byOther){
                    onlyIdRefBy = true;
                }
            }
        }
    }
    
    @Override
    public void entityInserted(BuguEntity ent){
        if(needListener){
            if(IndexFilterChecker.needIndex(ent)){
                IndexJob job = new IndexInsertJob(ent);
                job.doJob();
            }
        }
    }
    
    @Override
    public void entityUpdated(BuguEntity ent){
        if(needListener){
            if(IndexFilterChecker.needIndex(ent)){
                IndexJob job = new IndexUpdateJob(ent);
                job.doJob();
            }
            else{
                processDelete(ent.getId());
            }
            //for @IndexRefBy
            if(refListener != null && !onlyIdRefBy){
                processRefBy(ent.getId());
            }
        }
    }
    
    @Override
    public void entityDeleted(BuguEntity ent){
        if(needListener){
            processDelete(ent.getId());
            //for @IndexRefBy
            if(refListener != null){
                processRefBy(ent.getId());
            }
        }
    }
    
    private void processDelete(String id){
        IndexJob job = new IndexDeleteJob(clazz, id);
        job.doJob();
    }
    
    private void processRefBy(String id){
        refListener.entityChanged(clazz, id);
    }

    public RefEntityListener getRefListener() {
        return refListener;
    }
    
}
