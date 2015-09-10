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

import com.bugull.mongo.utils.StreamUtil;
import com.bugull.mongo.utils.StringUtil;
import com.mongodb.gridfs.GridFS;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Convenient class for uploading a file to GridFS. It uses the BuguFS class internally.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class Uploader {
    
    private final static Logger logger = LogManager.getLogger(Uploader.class.getName());
    
    protected InputStream input;
    protected String originalName;
    protected boolean rename;
    
    protected String bucket = GridFS.DEFAULT_BUCKET;
    protected long chunkSize = GridFS.DEFAULT_CHUNKSIZE;
    protected String filename;
    protected Map<String, Object> attributes;
    
    public Uploader(File file, String originalName){
        try{
            this.input = new FileInputStream(file);
        }catch(FileNotFoundException ex){
            logger.error("Can not create the FileInputStream", ex);
        }
        this.originalName = originalName;
    }
    
    public Uploader(File file, String originalName, boolean rename){
        this(file, originalName);
        this.rename = rename;
    }
    
    public Uploader(InputStream input, String originalName){
        this.input = input;
        this.originalName = originalName;
    }
    
    public Uploader(InputStream input, String originalName, boolean rename){
        this(input, originalName);
        this.rename = rename;
    }
    
    public Uploader(byte[] data, String originalName){
        this.input = new ByteArrayInputStream(data);
        this.originalName = originalName;
    }
    
    public Uploader(byte[] data, String originalName, boolean rename){
        this(data, originalName);
        this.rename = rename;
    }
    
    public void setBucket(String bucket){
        this.bucket = bucket;
    }
    
    public void setChunkSize(long chunkSize){
        this.chunkSize = chunkSize;
    }
    
    public void setAttribute(String key, Object value){
        if(attributes == null){
            attributes = new HashMap<String, Object>();
        }
        attributes.put(key, value);
    }
    
    public void save(){
        processFilename();
        saveInputStream();
    }

    public String getFilename() {
        return filename;
    }
    
    protected void processFilename(){
        if(rename){
            long time = System.nanoTime();
            //the nano time's last 3 bits is 000, add it with a random number
            time += new Random().nextInt(1000);
            String ext = StringUtil.getExtention(originalName);
            if(!StringUtil.isEmpty(ext)){
                filename = String.valueOf(time) + "." + ext;
            }else{
                filename = String.valueOf(time);
            }
        }else{
            filename = originalName;
        }
    }
    
    protected void saveInputStream(){
        BuguFS fs = BuguFSFactory.getInstance().create(bucket, chunkSize);
        fs.save(input, filename, attributes);
        StreamUtil.safeClose(input);
    }
    
}
