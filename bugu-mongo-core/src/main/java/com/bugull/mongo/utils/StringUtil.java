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

package com.bugull.mongo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for String.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class StringUtil {
    
    /**
     * Check if a string is empty
     * @param s
     * @return 
     */
    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }
    
    /**
     * Get the file's extension name, such as doc, png, jpeg
     * @param filename
     * @return 
     */
    public static String getExtention(String filename){
        String ext = null;
        int index = filename.lastIndexOf(".");
        if(index > 0){
            ext = filename.substring(index + 1);
        }
        return ext;
    }
    
    /**
     * Encrypt string s with MD5.
     * @param s
     * @return 
     */
    public static String encodeMD5(String s){
        if(isEmpty(s)){
            return null;
        }
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("MD5");
        }catch (NoSuchAlgorithmException ex) {
            //ignore ex
            return null;
        }
        char[] hexDigits = { '0', '1', '2', '3', '4',
                             '5', '6', '7', '8', '9',
                             'A', 'B', 'C', 'D', 'E', 'F' };
        md.update(s.getBytes());
        byte[] datas = md.digest();
        int len = datas.length;
        char str[] = new char[len * 2];
        int k = 0;
        for (int i = 0; i < len; i++) {
            byte byte0 = datas[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
    
}
