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
 * Note: for a polygon, the first point must be equal to the last point.
 * 
 * @author Frank Wen (xbwen@hotmail.com)
 */
public class Polygon extends GeoJSON implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double[][][] coordinates;
    
    public Polygon(){
        type = "Polygon";
    }
    
    /**
     * Create a rectangle. p1 must be same as p5.
     * @param p1
     * @param p2
     * @param p3
     * @param p4 
     * @param p5
     */
    public Polygon(double[] p1, double[] p2, double[] p3, double[] p4, double[] p5){
        type = "Polygon";
        coordinates = new double[1][5][2];
        coordinates[0][0] = p1;
        coordinates[0][1] = p2;
        coordinates[0][2] = p3;
        coordinates[0][3] = p4;
        coordinates[0][4] = p5;
    }

    public double[][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][] coordinates) {
        this.coordinates = coordinates;
    }
    
    public void setPointSequence(double[]... points){
        int size = points.length;
        coordinates = new double[1][size][2];
        for(int i=0; i<size; i++){
            coordinates[0][i] = points[i];
        }
    }
    
}
