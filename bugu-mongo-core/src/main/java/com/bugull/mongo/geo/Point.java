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

import java.io.Serializable;

/**
 *
 * @author Frank Wen (xbwen@hotmail.com)
 */
public class Point extends GeoJSON implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double[] coordinates;  //element 0 is longtitude, element 1 is latitude
    
    public Point(){
        type = "Point";
    }
    
    public Point(double longtitude, double latitude){
        type = "Point";
        coordinates = new double[2];
        coordinates[0] = longtitude;
        coordinates[1] = latitude;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
    
    public double getLongitude(){
        return coordinates==null ? 0 : coordinates[0];
    }
    
    public void setLongitude(double longitude){
        if(coordinates == null){
            coordinates = new double[2];
        }
        coordinates[0] = longitude;
    }
    
    public double getLatitude(){
        return coordinates==null ? 0 : coordinates[1];
    }
    
    public void setLatitude(double latitude){
        if(coordinates == null){
            coordinates = new double[2];
        }
        coordinates[1] = latitude;
    }
    
}
