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

package com.bugull.mongo.encoder;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.RefList;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.misc.InternalDao;
import com.bugull.mongo.utils.ReferenceUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class RefListEncoder extends AbstractEncoder{
    
    private RefList refList;
    private boolean cascadeCU;
    
    public RefListEncoder(Object obj, Field field){
        super(obj, field);
        refList = field.getAnnotation(RefList.class);
        cascadeCU = refList.cascade().toUpperCase().indexOf(Default.CASCADE_CREATE)!=-1 || refList.cascade().toUpperCase().indexOf(Default.CASCADE_UPDATE)!=-1;
    }
    
    @Override
    public String getFieldName(){
        String fieldName = field.getName();
        String name = refList.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        return fieldName;
    }
    
    @Override
    public Object encode(){
        Object result = null;
        Class<?> type = field.getType();
        if(type.isArray()){
            result = encodeArray(type.getComponentType());
        }else{
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            int len = types.length;
            if(len == 1){
                result = encodeCollection((Class)types[0]);
            }else if(len == 2){
                result = encodeMap((Class)types[1]);
            }
        }
        return result;
    }
    
    private Object encodeArray(Class<?> clazz){
        clazz = FieldUtil.getRealType(clazz, field);
        InternalDao dao = DaoCache.getInstance().get(clazz);
        int len = Array.getLength(value);
        List<Object> result = new ArrayList<Object>();
        for(int i=0; i<len; i++){
            BuguEntity entity = (BuguEntity)Array.get(value, i);
            if(entity != null){
                if(cascadeCU){
                    dao.save(entity);
                }
                result.add(ReferenceUtil.toDbReference(refList, entity.getClass(), entity.getId()));
            }
        }
        return result;
    }
    
    private Object encodeCollection(Class type){
        Collection<BuguEntity> collection = (Collection<BuguEntity>)value;
        List<Object> result = new ArrayList<Object>();
        Class<?> cls = FieldUtil.getRealType(type, field);
        InternalDao dao = DaoCache.getInstance().get(cls);
        for(BuguEntity entity : collection){
            if(entity != null){
                if(cascadeCU){
                    dao.save(entity);
                }
                result.add(ReferenceUtil.toDbReference(refList, entity.getClass(), entity.getId()));
            }
        }
        return result;
    }
    
    private Object encodeMap(Class type){
        Map<Object, BuguEntity> map = (Map<Object, BuguEntity>)value;
        Map<Object, Object> result = new HashMap<Object, Object>();
        Class<?> cls = FieldUtil.getRealType(type, field);
        InternalDao dao = DaoCache.getInstance().get(cls);
        for(Entry<Object, BuguEntity> entry : map.entrySet()){
            BuguEntity entity = entry.getValue();
            if(entity != null){
                if(cascadeCU){
                    dao.save(entity);
                }
                result.put(entry.getKey(), ReferenceUtil.toDbReference(refList, entity.getClass(), entity.getId()));
            }else{
                result.put(entry.getKey(), null);
            }
        }
        return result;
    }
    
}
