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

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class TransactionUtil {
    
    public static void runTransactionWithRetry(Runnable transactional){
        runTransactionWithRetry(transactional, 3);
    }
    
    public static void runTransactionWithRetry(Runnable transactional, int retryTimes){
        int count = 0;
        while (count < retryTimes) {
            count++;
            try {
                transactional.run();
                break;
            } catch (MongoException e) {
                if (e.hasErrorLabel(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL)) {
                    continue;
                } else {
                    throw e;
                }
            }
        }
    }
    
    /**
     * commit transaction, retry 3 times if fail.
     * @param session 
     */
    public static void commitWithRetry(ClientSession session){
        commitWithRetry(session, 3);
    }
    
    /**
     * commit transaction, retry n times.
     * @param session
     * @param retryTimes 
     */
    public static void commitWithRetry(ClientSession session, int retryTimes){
        int count = 0;
        while(count < retryTimes){
            count++;
            try {
                session.commitTransaction();
                break;
            } catch (MongoException e) {
                // can retry commit
                if (e.hasErrorLabel(MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL)) {
                    continue;
                } else {
                    throw e;
                }
            }
        }
    }
    
}
