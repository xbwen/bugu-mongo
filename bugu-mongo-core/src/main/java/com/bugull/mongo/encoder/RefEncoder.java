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
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.misc.InternalDao;
import com.bugull.mongo.utils.ReferenceUtil;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
@SuppressWarnings("unchecked")
public class RefEncoder extends AbstractEncoder{
    
    private Ref ref;
    
    public RefEncoder(Object obj, Field field){
        super(obj, field);
        ref = field.getAnnotation(Ref.class);
    }
    
    @Override
    public String getFieldName(){
        String fieldName = field.getName();
        String name = ref.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        return fieldName;
    }
    
    @Override
    public Object encode(){
        BuguEntity entity = (BuguEntity)value;
        if(ref.cascade().toUpperCase().indexOf(Default.CASCADE_CREATE)!=-1 || ref.cascade().toUpperCase().indexOf(Default.CASCADE_UPDATE)!=-1){
            Class<?> clazz = FieldUtil.getRealType(field);
            InternalDao dao = DaoCache.getInstance().get(clazz);
            dao.save(entity);
        }
        return ReferenceUtil.toDbReference(ref, entity.getClass(), entity.getId());
    }
    
}
