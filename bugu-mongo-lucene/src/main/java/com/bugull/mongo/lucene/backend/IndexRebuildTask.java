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

package com.bugull.mongo.lucene.backend;

import com.bugull.mongo.lucene.utils.IndexFilterChecker;
import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.lucene.holder.IndexWriterHolder;
import com.bugull.mongo.lucene.BuguIndex;
import com.bugull.mongo.access.InternalDao;
import com.bugull.mongo.utils.MapperUtil;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IndexRebuildTask implements Runnable{
    
    private final static Logger logger = LogManager.getLogger(IndexRebuildTask.class.getName());
    
    private Class<?> clazz;
    private IndexWriter writer;
    private int batchSize;
    
    private String entityName;
    
    public IndexRebuildTask(Class<?> clazz, int batchSize){
        this.clazz = clazz;
        this.batchSize = batchSize;
        entityName = MapperUtil.getEntityName(clazz);
        IndexWriterHolder holder = IndexWriterHolder.getInstance();
        writer = holder.get(entityName);
    }

    @Override
    public void run() {
        BuguIndex index = BuguIndex.getInstance();
        if(index.isRebuilding(entityName)){
            logger.error("Another rebuilding task is running on: " + entityName);
            return;
        }
        index.setRebuilding(entityName, true);
        logger.info("Index rebuilding start on: " + entityName);
        try{
            writer.deleteAll();
        }catch(IOException ex){
            logger.error("Something is wrong when lucene IndexWriter doing deleteAll()", ex);
        }
        InternalDao dao = DaoCache.getInstance().get(clazz);
        long count = dao.count();
        int pages = (int) (count / batchSize);
        int remainder = (int) (count % batchSize);
        if(pages > 0){
            for(int i=1; i<=pages; i++){
                List list = dao.findNotLazily(i, batchSize);
                process(list);
            }
        }
        //process the remainder
        if(remainder > 0){
            List list = dao.findNotLazily(++pages, batchSize);
            process(list);
        }
        index.setRebuilding(entityName, false);
        logger.info("Index rebuilding finish on: " + entityName);
    }
    
    private void process(List list){
        for(Object o : list){
            BuguEntity obj = (BuguEntity)o;
            process(obj);
        }
    }
    
    private void process(BuguEntity obj){
        if(IndexFilterChecker.needIndex(obj)){
            Document doc = new Document();
            IndexCreator creator = new IndexCreator(obj, "");
            creator.create(doc);
            try {
                writer.addDocument(doc);
            } catch (CorruptIndexException ex) {
                logger.error("IndexWriter can not add a document to the lucene index", ex);
            } catch (IOException ex) {
                logger.error("IndexWriter can not add a document to the lucene index", ex);
            }
        }
    }
    
}
