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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ReflectTest {
    
    private class Foo
	{
		private Map<String, String[]> map = new HashMap<String, String[]>();
        
        private Map<String, List<Integer>> map2 = new HashMap<String, List<Integer>>();
        
        private Map<String, Integer> map3 = new HashMap<String, Integer>();
	}
    
    @Test
    public void test() throws Exception{
        Field field = Foo.class.getDeclaredField("map3");
		ParameterizedType type = (ParameterizedType) field.getGenericType();
		Type f1  = type.getActualTypeArguments()[1];
		if (f1 instanceof GenericArrayType){
			System.out.println("GenericArrayType");
		}
        else if(f1 instanceof ParameterizedType){
			ParameterizedType p = (ParameterizedType)f1;
            Type raw = p.getRawType();
            System.out.println("raw:" + raw.toString());
		}
        else{
            System.err.println("primitive");
        }
    }
    

}
