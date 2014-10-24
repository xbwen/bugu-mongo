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

import com.bugull.mongo.lucene.holder.IndexSearcherHolder;
import com.bugull.mongo.lucene.BuguIndex;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class IndexReopenTask implements Runnable {
    
    private final static Logger logger = LogManager.getLogger(IndexReopenTask.class.getName());

    @Override
    public void run() {
        BuguIndex index = BuguIndex.getInstance();
        if(index.isReopening()){
            return;
        }
        index.setReopening(true);
        IndexSearcherHolder searcherCache = IndexSearcherHolder.getInstance();
        Map<String, IndexSearcher> map = searcherCache.getAll();
        for(Entry<String, IndexSearcher> entry : map.entrySet()){
            IndexSearcher searcher = entry.getValue();
            IndexReader reader = searcher.getIndexReader();
            IndexReader newReader = null;
            try{
                newReader = IndexReader.openIfChanged(reader, true);
            }catch(IOException ex){
                logger.error("Something is wrong when reopen the Lucene IndexReader", ex);
            }
            if(newReader!=null && newReader!=reader){
                try{
                    reader.decRef();
                }catch(IOException ex){
                    logger.error("Something is wrong when decrease the reference of the lucene IndexReader", ex);
                }
                IndexSearcher newSearcher = new IndexSearcher(newReader);
                searcherCache.put(entry.getKey(), newSearcher);
            }
        }
        index.setReopening(false);
    }
    
}
