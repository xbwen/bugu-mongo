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

import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.utils.StreamUtil;
import com.bugull.mongo.utils.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Get a file from GridFS by HTTP.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class HttpFileGetter {
    
    private final static Logger logger = LogManager.getLogger(HttpFileGetter.class.getName());
    
    private final static long ONE_YEAR_SECONDS = 365L * 24L * 60L * 60L;
    private final static long ONE_YEAR_MILLISECONDS = ONE_YEAR_SECONDS * 1000L;
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    private String connection;
    private String bucket;
    private boolean contentMD5;
    
    private DBObject query = new BasicDBObject(ImageUploader.DIMENSION, null);
    
    public HttpFileGetter(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
    }
    
    public void response(String filename) throws ServletException, IOException {
        query.put(BuguFS.FILENAME, filename);
        
        if(StringUtil.isEmpty(connection)){
            connection = Default.NAME;
        }
        
        if(StringUtil.isEmpty(bucket)){
            bucket = GridFS.DEFAULT_BUCKET;
        }
        
        BuguFS fs = BuguFSFactory.getInstance().create(connection, bucket);
        GridFSDBFile f = fs.findOne(query);
        if(f == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);  //404
            return;
        }
        
        InputStream is = f.getInputStream();
        OutputStream os = response.getOutputStream();
        try{
            int fileLength = (int)f.getLength();
            String ext = FileTypeUtil.getExtention(filename);
            response.setContentType(FileTypeUtil.getContentType(ext));
            String range = request.getHeader("Range");
            //normal http request, no "range" in header.
            if(StringUtil.isEmpty(range)){
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentLength(fileLength);
                if(contentMD5){
                    response.setHeader("Content-MD5", f.getMD5());
                }
                if(FileTypeUtil.needCache(ext)){
                    String modifiedSince = request.getHeader("If-Modified-Since");
                    DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                    df.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date uploadDate = f.getUploadDate();
                    String lastModified = df.format(uploadDate);
                    if(modifiedSince != null){
                        Date modifiedDate = null;
                        Date sinceDate = null;
                        try{
                            modifiedDate = df.parse(lastModified);
                            sinceDate = df.parse(modifiedSince);
                        }catch(ParseException ex){
                            logger.error("Can not parse the Date", ex);
                        }
                        if(modifiedDate!=null && sinceDate!=null && modifiedDate.compareTo(sinceDate) <= 0){
                            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);    //Not Modified
                            return;
                        }
                    }
                    response.setHeader("Cache-Control", "max-age=" + ONE_YEAR_SECONDS);
                    response.setHeader("Last-Modified", lastModified);
                    response.setDateHeader("Expires", uploadDate.getTime() + ONE_YEAR_MILLISECONDS);
                }else{
                    response.setHeader("Pragma","no-cache");
                    response.setHeader("Cache-Control","no-cache");
                    response.setDateHeader("Expires", 0);
                }
                f.writeTo(os);
            }
            //has "range" in header
            else{
                range = range.substring("bytes=".length());
                if(StringUtil.isEmpty(range)){
                    return;
                }
                int begin = 0;
                int end = fileLength - 1;
                boolean onlyLast = range.startsWith("-");
                String[] rangeArray = range.split("-");
                if(rangeArray.length == 1){
                    if(onlyLast){
                        begin = fileLength - Integer.parseInt(rangeArray[0]);
                    }else{
                        begin = Integer.parseInt(rangeArray[0]);
                    }
                }else if(rangeArray.length == 2){
                    begin = Integer.parseInt(rangeArray[0]);
                    end = Integer.parseInt(rangeArray[1]);
                }
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                int contentLength = end - begin + 1;
                response.setContentLength(contentLength);
                response.setHeader("Content-Range", "bytes " + begin + "-" + end + "/" + fileLength);
                is.skip(begin);
                int read = -1;
                int bufferSize = (int)f.getChunkSize();
                byte[] buffer = new byte[bufferSize];
                int remain = contentLength;
                int readSize = Math.min(bufferSize, remain);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while( (read = is.read(buffer, 0, readSize)) != -1 ){
                    baos.write(buffer, 0, read);
                    remain -= read;
                    if(remain <= 0){
                        break;
                    }
                    readSize = Math.min(bufferSize, remain);
                }
                byte[] bytes = baos.toByteArray();
                if(contentMD5){
                    String md5 = StringUtil.encodeMD5(bytes);
                    if(! StringUtil.isEmpty(md5)){
                        response.setHeader("Content-MD5", md5.toLowerCase());
                    }
                }
                os.write(bytes);
                os.flush();
            }
        }finally{
            StreamUtil.safeClose(is);
            StreamUtil.safeClose(os);
        }
    }
    
    public void setAttribute(String attribute, Object value){
        query.put(attribute, value);
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setContentMD5(boolean contentMD5) {
        this.contentMD5 = contentMD5;
    }
    
}
