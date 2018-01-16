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
import java.util.concurrent.Semaphore;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Number of threads accessing file in GridFS by this servlet is restricted.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class AccessRestrictedServlet extends UploadedFileServlet {
    
    private final static Logger logger = LogManager.getLogger(AccessRestrictedServlet.class.getName());
    
    private static final String DEFAULT_RESOURCE_NAME = "bugu";
    private static final String DEFAULT_MAX_ACCESS = "10";
    
    private String resourceName;
    private int maxAccess;
    private String redirectTo;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        resourceName = config.getInitParameter("resourceName");
        if(StringUtil.isEmpty(resourceName)){
            resourceName = DEFAULT_RESOURCE_NAME;
        }
        String strMaxAccess = config.getInitParameter("maxAccess");
        if(StringUtil.isEmpty(strMaxAccess)){
            strMaxAccess = DEFAULT_MAX_ACCESS;
        }
        maxAccess = Integer.parseInt(strMaxAccess);
        redirectTo = config.getInitParameter("redirectTo");
        
        AccessCount.getInstance().addSemaphore(resourceName, maxAccess);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Semaphore semaphore = AccessCount.getInstance().getSemaphore(resourceName, maxAccess);
        if(!StringUtil.isEmpty(redirectTo) && semaphore.availablePermits()<=0){
            response.sendRedirect(redirectTo);
        }
        else{
            try{
                semaphore.acquire();
                processRequest(request, response);
            }catch(Exception ex){
                logger.error(ex.getMessage(), ex);
            }finally{
                semaphore.release();
            }
        }
    }

}
