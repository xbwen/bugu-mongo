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
 * A polygon area on map, consists of points sequence.
 * Note: for rings in polygon, the first point must be equal to the last point.
 * 
 * @author Frank Wen (xbwen@hotmail.com)
 */
public class Polygon extends GeoJSON implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double[][][] coordinates;
    
    public Polygon(){
        type = "Polygon";
    }

    public double[][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][] coordinates) {
        this.coordinates = coordinates;
    }
    
    public void setSingleRing(double[]... points){
        int size = points.length;
        coordinates = new double[1][size][2];
        for(int i=0; i<size; i++){
            coordinates[0][i] = points[i];
        }
    }
    
}
