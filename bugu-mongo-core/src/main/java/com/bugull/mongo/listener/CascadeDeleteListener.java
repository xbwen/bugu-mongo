/*
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

package com.bugull.mongo.listener;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.misc.InternalDao;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.utils.Operator;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CascadeDeleteListener implements EntityListener {
    
    private final List<Field> refFields = new ArrayList<Field>();
    private final List<Field> refListFields = new ArrayList<Field>();

    public CascadeDeleteListener(Class<?> clazz) {
        Field[] fields = FieldsCache.getInstance().get(clazz);
        for(Field f : fields){
            Ref ref = f.getAnnotation(Ref.class);
            if(ref!=null && ref.cascade().toUpperCase().indexOf(Default.CASCADE_DELETE)!=-1){
                refFields.add(f);
                continue;
            }
            RefList refList = f.getAnnotation(RefList.class);
            if(refList!=null && refList.cascade().toUpperCase().indexOf(Default.CASCADE_DELETE)!=-1){
                refListFields.add(f);
                continue;
            }
        }
    }

    @Override
    public void entityDeleted(BuguEntity entity) {
        for(Field f : refFields){
            processRef(entity, f);
        }
        for(Field f : refListFields){
            processRefList(entity, f);
        }
    }
    
    private void processRef(BuguEntity entity, Field f){
        Object value = FieldUtil.get(entity, f);
        if(value != null){
            Class<?> type = f.getType();
            InternalDao dao = DaoCache.getInstance().get(type);
            dao.remove(value);
        }
    }
    
    private void processRefList(BuguEntity entity, Field f){
        Object value = FieldUtil.get(entity, f);
        if(value == null){
            return;
        }
        List<String> idList = null;
        Class<?> clazz = null;
        Class<?> type = f.getType();
        if(type.isArray()){
            clazz = type.getComponentType();
            idList = getArrayIds(value);
        }else{
            ParameterizedType paramType = (ParameterizedType)f.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                clazz = (Class)types[0];
                idList = getCollectionIds(value);
            }else if(len == 2){
                clazz = (Class)types[1];
                idList = getMapIds(value);
            }
        }
        if(clazz == null){
            return;
        }
        InternalDao dao = DaoCache.getInstance().get(clazz);
        BuguQuery query = dao.query().in(Operator.ID, idList);
        dao.remove(query);
    }
    
    private List<String> getArrayIds(Object value){
        int len = Array.getLength(value);
        List<String> idList = new ArrayList<String>();
        for(int i=0; i<len; i++){
            Object item = Array.get(value, i);
            if(item != null){
                BuguEntity ent = (BuguEntity)item;
                idList.add(ent.getId());
            }
        }
        return idList;
    }
    
    private List<String> getCollectionIds(Object value){
        Collection<BuguEntity> collection = (Collection<BuguEntity>)value;
        List<String> idList = new ArrayList<String>();
        for(BuguEntity ent : collection){
            if(ent != null){
                idList.add(ent.getId());
            }
        }
        return idList;
    }
    
    private List<String> getMapIds(Object value){
        Map<Object, BuguEntity> map = (Map<Object, BuguEntity>)value;
        Collection<BuguEntity> values = map.values();
        List<String> idList = new ArrayList<String>();
        for(BuguEntity ent : values){
            if(ent != null){
                idList.add(ent.getId());
            }
        }
        return idList;
    }
    
    @Override
    public void entityInserted(BuguEntity entity) {
        //do nothing
    }

    @Override
    public void entityUpdated(BuguEntity entity) {
        //do nothing
    }

}
