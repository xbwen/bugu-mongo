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

package com.bugull.mongo.decoder;

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.cache.ConstructorCache;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.misc.InternalDao;
import com.bugull.mongo.utils.ReferenceUtil;
import com.mongodb.DBObject;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class RefDecoder extends AbstractDecoder{
    
    private Ref ref;
    
    public RefDecoder(Field field, DBObject dbo){
        super(field);
        ref = field.getAnnotation(Ref.class);
        String fieldName = field.getName();
        String name = ref.name();
        if(!name.equals(Default.NAME)){
            fieldName = name;
        }
        value = dbo.get(fieldName);
    }
    
    @Override
    public void decode(Object obj){
        String refId = ReferenceUtil.fromDbReference(ref, value);
        Class<?> clazz = FieldUtil.getRealType(field);
        BuguEntity refObj = null;
        if(ref.cascade().toUpperCase().indexOf(Default.CASCADE_READ)==-1){
            refObj = (BuguEntity)ConstructorCache.getInstance().create(clazz);
            refObj.setId(refId);
        }else{
            InternalDao dao = DaoCache.getInstance().get(clazz);
            refObj = (BuguEntity)dao.findOne(refId);
        }
        FieldUtil.set(obj, field, refObj);
    }
    
}
