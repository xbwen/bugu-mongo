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

import com.bugull.mongo.geo.Point;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class GeoNearOptions {

    private final static String SPHERICAL = "spherical";
    private final static String LIMIT = "limit";
    private final static String NUM = "num";
    private final static String MAX_DISTANCE = "maxDistance";
    private final static String MIN_DISTANCE = "minDistance";
    private final static String QUERY = "query";
    private final static String NEAR = "near";
    private final static String DISTANCE_FIELD = "distanceField";
    private final static String INCLUDE_LOCS = "includeLocs";

    public boolean spherical;
    public int limit;
    public int num;
    public double maxDistance;
    public double minDistance;
    public String query;  //json string
    public Point near;
    public String distanceField;
    public String includeLocs;

    public DBObject toDBObject() {
        DBObject dbo = new BasicDBObject();
        dbo.put(SPHERICAL, spherical);
        if (limit != 0) {
            dbo.put(LIMIT, limit);
        }
        if (num != 0) {
            dbo.put(NUM, num);
        }
        if (maxDistance != 0) {
            dbo.put(MAX_DISTANCE, maxDistance);
        }
        if (minDistance != 0) {
            dbo.put(MIN_DISTANCE, minDistance);
        }
        if (query != null) {
            dbo.put(QUERY, (DBObject) JSON.parse(query));
        }
        if (near != null) {
            dbo.put(NEAR, MapperUtil.toDBObject(near, true));
        }
        if (distanceField != null) {
            dbo.put(DISTANCE_FIELD, distanceField);
        }
        if (includeLocs != null) {
            dbo.put(INCLUDE_LOCS, includeLocs);
        }
        return dbo;
    }

}
