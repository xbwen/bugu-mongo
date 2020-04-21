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
public class LineString extends GeoJSON implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double[][] coordinates;
    
    public LineString(){
        type = "LineString";
    }
    
    public LineString(double[] start, double[] end){
        type = "LineString";
        coordinates = new double[2][];
        coordinates[0] = start;
        coordinates[1] = end;
    }
    
    public LineString(Point start, Point end){
        type = "LineString";
        coordinates = new double[2][];
        coordinates[0] = start.getCoordinates();
        coordinates[1] = end.getCoordinates();
    }
    
    public double[][] getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(Point start, Point end){
        coordinates = new double[2][];
        coordinates[0] = start.getCoordinates();
        coordinates[1] = end.getCoordinates();
    }
    
    public void setCoordinates(double[] start, double[] end){
        coordinates = new double[2][];
        coordinates[0] = start;
        coordinates[1] = end;
    }
    
}
