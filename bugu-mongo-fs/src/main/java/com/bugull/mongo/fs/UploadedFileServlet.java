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
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A convenient Servlet for getting a file from GridFS via http.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class UploadedFileServlet extends HttpServlet {
    
    private final static Logger logger = LogManager.getLogger(UploadedFileServlet.class.getName());
    
    private final static String SLASH = "/";
    
    private String connection;
    private boolean contentMD5;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        connection = config.getInitParameter("connection");
        
        String md5 = config.getInitParameter("contentMD5");
        if(! StringUtil.isEmpty(md5)){
            contentMD5 = Boolean.valueOf(md5);
        }

    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        //ignore redundant slash
        uri = uri.replaceAll("//", SLASH);
        String servlet = request.getServletPath();
        //skip the servlet name
        int start = uri.indexOf(servlet);
        uri = uri.substring(start + servlet.length());
        //check if the url is valid
        if(uri.length() < 2){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);  //404
            return;
        }
        
        //get the file name
        int last = uri.lastIndexOf(SLASH);
        String filename = uri.substring(last+1);
        
        HttpFileGetter getter = new HttpFileGetter(request, response);
        getter.setConnection(connection);
        getter.setContentMD5(contentMD5);
        
        int first = uri.indexOf(SLASH);
        if(first != last){
            String sub = uri.substring(first+1, last);
            String[] arr = sub.split(SLASH);
            for(int i=0; i<arr.length; i+=2){
                if(arr[i].equals(BuguFS.BUCKET)){
                    getter.setBucket(arr[i+1]);
                }else{
                    getter.setAttribute(arr[i], arr[i+1]);
                }
            }
        }
        
        getter.response(filename);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
}
