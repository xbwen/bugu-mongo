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

import com.bugull.mongo.BuguEntity;
import com.bugull.mongo.lucene.holder.IndexWriterHolder;
import com.bugull.mongo.utils.MapperUtil;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IndexInsertJob implements IndexJob {
    
    private final static Logger logger = LogManager.getLogger(IndexInsertJob.class.getName());
    
    private BuguEntity obj;
    
    public IndexInsertJob(BuguEntity obj){
        this.obj = obj;
    }

    @Override
    public void doJob() {
        Class<?> clazz = obj.getClass();
        String name = MapperUtil.getEntityName(clazz);
        IndexWriterHolder holder = IndexWriterHolder.getInstance();
        IndexWriter writer = holder.get(name);
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
