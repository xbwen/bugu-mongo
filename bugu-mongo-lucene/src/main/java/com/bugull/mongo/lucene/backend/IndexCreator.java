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

package com.bugull.mongo.lucene.backend;

import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.lucene.annotations.BoostSwitch;
import com.bugull.mongo.lucene.handler.FieldHandler;
import com.bugull.mongo.lucene.handler.FieldHandlerFactory;
import com.bugull.mongo.utils.FieldUtil;
import java.lang.reflect.Field;
import org.apache.lucene.document.Document;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IndexCreator{
    
    protected Object obj;
    protected String prefix;
    
    public IndexCreator(Object obj, String prefix){
        this.obj = obj;
        this.prefix = prefix;
    }
    
    public void create(Document doc){
        Field[] fields = FieldsCache.getInstance().get(obj.getClass());
        for(Field f : fields){
            BoostSwitch bs = f.getAnnotation(BoostSwitch.class);
            if(bs != null){
                ValueComparer comparer = new ValueComparer(obj);
                boolean fit = comparer.isFit(f, bs.compare(), bs.value());
                if(fit){
                    doc.setBoost(bs.fit());
                }else{
                    doc.setBoost(bs.unfit());
                }
            }
            Object objValue = FieldUtil.get(obj, f);
            if(objValue != null){
                FieldHandler handler = FieldHandlerFactory.create(obj, f, prefix);
                if(handler != null){
                    handler.handle(doc);
                }
            }
        }
    }
    
}
