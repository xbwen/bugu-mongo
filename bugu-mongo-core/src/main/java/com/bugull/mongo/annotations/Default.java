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

package com.bugull.mongo.annotations;

/**
 * Default value for some annotations' property.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class Default {
    
    public final static String NAME = "";
    
    public final static String SORT = "";
    
    public final static long CAP_SIZE = -1;
    public final static long CAP_MAX = -1;
    
    public final static String CASCADE = "";
    
    public final static String CASCADE_CREATE = "C";
    public final static String CASCADE_READ = "R";
    public final static String CASCADE_UPDATE = "U";
    public final static String CASCADE_DELETE = "D";
    
}