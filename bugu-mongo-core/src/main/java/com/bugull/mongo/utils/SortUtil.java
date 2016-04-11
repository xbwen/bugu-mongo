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

package com.bugull.mongo.utils;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class SortUtil {
    
    public static String aesc(String key){
        return new StringBuilder().append("{").append(key).append(":1").append("}").toString();
    }
    
    public static String desc(String key){
        return new StringBuilder().append("{").append(key).append(":-1").append("}").toString();
    }

    /**
     * convert order string to DBObject.
     * @param jsonString
     * @return
     */
    public static DBObject getSort(String jsonString) {
        jsonString = jsonString.trim();
        if (!jsonString.startsWith("{")) {
            jsonString = "{" + jsonString;
        }
        if (!jsonString.endsWith("}")) {
            jsonString = jsonString + "}";
        }
        return (DBObject) JSON.parse(jsonString);
    }

}
