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

package com.bugull.mongo.lucene.handler;

import com.bugull.mongo.lucene.annotations.IndexRefBy;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class RefByFieldHandler extends ByFieldHandler{
    
    private Class<?> refBy;
    
    public RefByFieldHandler(Class<?> refBy, Object obj, Field field, String prefix){
        super(obj, field, prefix);
        this.refBy = refBy;
    }

    @Override
    public void handle(Document doc){
        IndexRefBy irb = field.getAnnotation(IndexRefBy.class);
        Class<?>[] cls = irb.value();
        int len = cls.length;
        for(int i=0; i<len; i++){
            if(cls[i].equals(refBy)){
                boolean analyze = false;
                boolean[] as = irb.analyze();
                if(as.length > 0){
                    analyze = as[i];
                }
                boolean store = false;
                boolean[] ss = irb.store();
                if(ss.length > 0){
                    store = ss[i];
                }
                float boost = 1.0f;
                float[] bs = irb.boost();
                if(bs.length > 0){
                    boost = bs[i];
                }
                if(obj instanceof List){
                    processList((List)obj, doc, analyze, store, boost);
                }else{
                    process(doc, analyze, store, boost);
                }
                break;
            }
        }
    }
    
}
