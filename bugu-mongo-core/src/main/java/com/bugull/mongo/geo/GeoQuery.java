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
package com.bugull.mongo.geo;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.Operator;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Convenient class for creating geometry queries.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class GeoQuery<T> extends BuguQuery<T> {
    
    public GeoQuery(BuguDao<T> dao){
        super(dao);
    }
    
    public GeoQuery<T> nearSphere(String key, Point point){
        DBObject geometry = new BasicDBObject();
        geometry.put(Operator.GEOMETRY, MapperUtil.toDBObject(point, true));
        append(key, Operator.NEAR_SPHERE, geometry);
        return this;
    }

    /**
     * 
     * @param key
     * @param point
     * @param maxDistance maximum meters
     * @return 
     */
    public GeoQuery<T> nearSphere(String key, Point point, double maxDistance){
        DBObject geometry = new BasicDBObject();
        geometry.put(Operator.GEOMETRY, MapperUtil.toDBObject(point, true));
        geometry.put(Operator.MAX_DISTANCE, maxDistance);
        append(key, Operator.NEAR_SPHERE, geometry);
        return this;
    }
    
    /**
     * 
     * @param key
     * @param point
     * @param maxDistance maximum meters
     * @param minDistance minimum meters
     * @return 
     */
    public GeoQuery<T> nearSphere(String key, Point point, double maxDistance, double minDistance){
        DBObject geometry = new BasicDBObject();
        geometry.put(Operator.GEOMETRY, MapperUtil.toDBObject(point, true));
        geometry.put(Operator.MAX_DISTANCE, maxDistance);
        geometry.put(Operator.MIN_DISTANCE, minDistance);
        append(key, Operator.NEAR_SPHERE, geometry);
        return this;
    }
    
    public GeoQuery<T> geoWithin(String key, GeoJSON geoJson){
        DBObject geometry = new BasicDBObject();
        geometry.put(Operator.GEOMETRY, MapperUtil.toDBObject(geoJson, true));
        append(key, Operator.GEO_WITHIN, geometry);
        return this;
    }
    
    public GeoQuery<T> geoIntersects(String key, GeoJSON geoJson){
        DBObject geometry = new BasicDBObject();
        geometry.put(Operator.GEOMETRY, MapperUtil.toDBObject(geoJson, true));
        append(key, Operator.GEO_INTERSECTS, geometry);
        return this;
    }
    
}
