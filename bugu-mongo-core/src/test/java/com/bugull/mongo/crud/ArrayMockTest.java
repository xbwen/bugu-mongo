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
package com.bugull.mongo.crud;

import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.dao.ArrayMockDao;
import com.bugull.mongo.entity.ArrayMock;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class ArrayMockTest extends ReplicaSetBaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        ArrayMockDao dao = new ArrayMockDao();
        
        ArrayMock mock = new ArrayMock();
        
        byte[] binary = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
        
        int[] one = new int[]{1, 2, 3, 4, 5};
        
        double[][] two = new double[2][2];
        two[0][0] = 1.111;
        two[0][1] = 1.222;
        two[1][0] = 2.111;
        two[1][1] = 2.222;
        
        String[][][] three = new String[2][2][2];
        
        three[0][0][0] = "000";
        three[0][0][1] = "001";
        three[0][1][0] = "010";
        three[0][1][1] = "011";
        three[1][0][0] = "100";
        three[1][0][1] = "101";
        three[1][1][0] = "110";
        three[1][1][1] = "111";
        
        mock.setBinary(binary);
        mock.setOne(one);
        mock.setTwo(two);
        mock.setThree(three);
        
        dao.save(mock);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        ArrayMockDao dao = new ArrayMockDao();
        
        ArrayMock mock = dao.findOne();
        
        byte[] binary = mock.getBinary();
        for(byte b : binary){
            System.out.println("binary:" + b);
        }
        
        int[] one = mock.getOne();
        for(int o : one){
            System.out.println("one:" + o);
        }
        
        double[][] two = mock.getTwo();
        for(int i=0; i<two.length; i++){
            for(int j=0; j<two[i].length; j++){
                System.out.println("two:" + two[i][j]);
            }
        }
        
        String[][][] three = mock.getThree();
        
        for(int i=0; i<three.length; i++){
            for(int j=0; j<three[i].length; j++){
                for(int k=0; k<three[i][j].length; k++){
                    System.out.println("three:" + three[i][j][k]);
                }
            }
        }
        
        disconnectDB();
    }
    
}
