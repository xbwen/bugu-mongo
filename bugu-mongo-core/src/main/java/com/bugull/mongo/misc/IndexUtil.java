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

package com.bugull.mongo.misc;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class IndexUtil {
    
    public static List<DBIndex> getDBIndex(String indexString){
        List<DBIndex> list = new ArrayList<DBIndex>();
        indexString = indexString.replaceAll("\\}[^{^}]+\\{", "};{");
        indexString = indexString.replaceAll("[{}'']", "");
        String[] items = indexString.split(";");
        for(String item : items){
            DBObject indexKeys = new BasicDBObject();
            DBObject indexOptions = new BasicDBObject("background", true);
            String[] arr = item.split(",");
            for(String s : arr){
                String[] kv = s.split(":");
                String k = kv[0].trim();
                String v = kv[1].trim();
                //note: the following check order can't be changed!
                if(v.equalsIgnoreCase("2dsphere") || v.equalsIgnoreCase("text")){
                    indexKeys.put(k, v);
                }
                else if(k.equalsIgnoreCase("expireAfterSeconds")){
                    indexOptions.put(k, Integer.parseInt(v));
                }
                else if(v.equals("1") || v.equals("-1")){
                    indexKeys.put(k, Integer.parseInt(v));
                }
                else if(v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")){
                    indexOptions.put(k, Boolean.parseBoolean(v));
                }
                else if(k.equalsIgnoreCase("name")){
                    indexOptions.put(k, v);
                }
            }
            DBIndex dbi = new DBIndex();
            dbi.indexKeys = indexKeys;
            dbi.indexOptions = indexOptions;
            list.add(dbi);
        }
        return list;
    }
    
}
