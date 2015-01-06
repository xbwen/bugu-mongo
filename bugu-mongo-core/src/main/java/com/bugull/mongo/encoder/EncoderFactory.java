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

import com.bugull.mongo.annotations.Embed;
import com.bugull.mongo.annotations.EmbedList;
import com.bugull.mongo.annotations.Id;
import com.bugull.mongo.annotations.Ignore;
import com.bugull.mongo.annotations.Ref;
import com.bugull.mongo.annotations.RefList;
import java.lang.reflect.Field;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class EncoderFactory {
    
    public static Encoder create(Object obj, Field field){
        Encoder encoder = null;
        if(field.getAnnotation(Id.class) != null){
            encoder = new IdEncoder(obj, field);
        }
        else if(field.getAnnotation(Embed.class) != null){
            encoder = new EmbedEncoder(obj, field);
        }
        else if(field.getAnnotation(EmbedList.class) != null){
            encoder = new EmbedListEncoder(obj, field);
        }
        else if(field.getAnnotation(Ref.class) != null){
            encoder = new RefEncoder(obj, field);
        }
        else if(field.getAnnotation(RefList.class) != null){
            encoder = new RefListEncoder(obj, field);
        }
        else if(field.getAnnotation(Ignore.class) != null){
            encoder = null;
        }
        else{
            encoder = new PropertyEncoder(obj, field);  //no mapping annotation or @Property
        }
        return encoder;
    }
    
}
