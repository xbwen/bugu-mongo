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
package com.bugull.mongo.agg;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class Lookup {
    
    public final static String FROM = "from";
    public final static String LOCAL_FIELD = "localField";
    public final static String FOREIGN_FIELD = "foreignField";
    public final static String AS = "as";

    public String from;
    public String localField;
    public String foreignField;
    public String as;

    /**
     * constructor
     * @param from the collection in the same database to perform the join with.
     * @param localField the field from the documents input to the $lookup stage.
     * @param foreignField the field from the documents in the from collection.
     * @param as the name of the new array field to add to the input documents.
     */
    public Lookup(String from, String localField, String foreignField, String as){
        this.from = from;
        this.localField = localField;
        this.foreignField = foreignField;
        this.as = as;
    }
    
}
