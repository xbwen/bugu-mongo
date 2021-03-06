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
package com.bugull.mongo.fs;

import com.bugull.mongo.utils.StringUtil;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class FileTypeUtil {
    
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
     * check the file is png image
     * @param filename
     * @return 
     */
    public static boolean isPng(String filename){
        boolean result = false;
        String ext = getExtention(filename);
        if(!StringUtil.isEmpty(ext) && ext.equalsIgnoreCase("png")){
            result = true;
        }
        return result;
    }
    
    /**
     * If it's an image file, cache it in browser
     * @param ext
     * @return 
     */
    public static boolean needCache(String ext){
        if(StringUtil.isEmpty(ext)){
            return false;
        }
        ext = ext.toLowerCase();
        boolean need = false;
        String[] arr = {"jpg", "jpeg", "png", "gif", "bmp"};
        for(String s : arr){
            if(s.equals(ext)){
                need = true;
                break;
            }
        }
        return need;
    }
    
    /**
     * check content type that can use in browser
     * @param ext
     * @return 
     */
    public static String getContentType(String ext){
        //default content type is application/octet-stream
        if(StringUtil.isEmpty(ext)){
            return "application/octet-stream";
        }
        ext = ext.toLowerCase();
        String type = "application/octet-stream";
        switch (ext) {
            case "jpg":
                type = "image/jpeg";
                break;
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                type = "image/" + ext;
                break;
            case "mp3":
                type = "audio/mpeg";
                break;
            case "mp4":
                type = "video/mp4";
                break;
            default:
                break;
        }
        return type;
    }
    
}
