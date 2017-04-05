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

package com.bugull.mongo.lucene;

import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.cache.FieldsCache;
import com.bugull.mongo.lucene.holder.IndexSearcherHolder;
import com.bugull.mongo.exception.FieldException;
import com.bugull.mongo.utils.FieldUtil;
import com.bugull.mongo.access.InternalDao;
import com.bugull.mongo.utils.MapperUtil;
import com.bugull.mongo.utils.StringUtil;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguSearcher<T> {
    
    private final static Logger logger = LogManager.getLogger(BuguSearcher.class.getName());
    
    private Class<T> clazz;
    private IndexSearcher searcher;
    private IndexReader reader;
    private InternalDao<T> dao;
    
    private Query query;
    private Sort sort;
    private Filter filter;
    private int pageNumber = 1;
    private int pageSize = 20;
    private int maxPage = 100;
    private int resultCount;
    private BuguHighlighter highlighter;
    
    public BuguSearcher(Class<T> clazz){
        this.clazz = clazz;
        String name = MapperUtil.getEntityName(clazz);
        searcher = IndexSearcherHolder.getInstance().get(name);
        reader = searcher.getIndexReader();
        reader.incRef();
    }
    
    public BuguSearcher<T> setQuery(Query query){
        this.query = query;
        return this;
    }
    
    public BuguSearcher<T> setSort(Sort sort){
        this.sort = sort;
        return this;
    }
    
    public BuguSearcher<T> setFilter(Filter filter){
        this.filter = filter;
        return this;
    }
    
    public BuguSearcher<T> setMaxPage(int maxPage){
        this.maxPage = maxPage;
        return this;
    }
    
    public BuguSearcher<T> setPageNumber(int pageNumber){
        this.pageNumber = pageNumber;
        return this;
    }
    
    public BuguSearcher<T> setPageSize(int pageSize){
        this.pageSize = pageSize;
        return this;
    }
    
    public BuguSearcher<T> setHighlighter(BuguHighlighter highlighter) {
        this.highlighter = highlighter;
        return this;
    }
    
    public int getResultCount(){
        return resultCount;
    }
    
    public List<T> search(Query query){
        this.query = query;
        return search();
    }
    
    public List<T> search(Query query, Sort sort){
        this.query = query;
        this.sort = sort;
        return search();
    }
    
    public List<T> search(Query query, Filter filter){
        this.query = query;
        this.filter = filter;
        return search();
    }
    
    public List<T> search(Query query, Filter filter, Sort sort){
        this.query = query;
        this.filter = filter;
        this.sort = sort;
        return search();
    }
    
    public List<T> search(){
        TopDocs topDocs = null;
        try{
            if(sort == null){
                topDocs = searcher.search(query, filter, maxPage*pageSize);
            }else{
                topDocs = searcher.search(query, filter, maxPage*pageSize, sort);
            }
        }catch(IOException ex){
            logger.error("Something is wrong when doing lucene search", ex);
        }
        if(topDocs == null){
            return Collections.emptyList();
        }
        resultCount = topDocs.totalHits;
        ScoreDoc[] docs = topDocs.scoreDocs;
        List<T> list = new ArrayList<T>();
        dao = DaoCache.getInstance().get(clazz);
        int begin = (pageNumber - 1) * pageSize;
        int end = begin + pageSize;
        if(end > resultCount){
            end = resultCount;
        }
        //fix lazy or not
        boolean lazy = needLazy();
        //get from db
        for(int i=begin; i<end; i++){
            Document doc = null;
            try{
                doc = searcher.doc(docs[i].doc);
            }catch(IOException ex){
                logger.error("Lucene IndexSearcher can not get the document", ex);
            }
            if(doc != null){
                String id = doc.get(FieldsCache.getInstance().getIdFieldName(clazz));
                T t = lazy ? dao.findOneLazily(id, false) : dao.findOne(id);
                if(t != null){
                    list.add(t);
                }
            }
        }
        //process highlighter
        if(highlighter != null){
            for(Object obj : list){
                highlightObject(obj);
            }
        }
        return list;
    }
    
    private boolean needLazy(){
        if(highlighter == null){
            return true;
        }
        boolean lazy = true;
        String[] fields = highlighter.getFields();
        Set<String> keys = dao.getKeys().keySet();
        for(String f : fields){
            if(! keys.contains(f)){
                lazy = false;
                break;
            }
        }
        return lazy;
    }
    
    private void highlightObject(Object obj){
        String[] fields = highlighter.getFields();
        for(String fieldName : fields){
            if(! fieldName.contains(".")){
                Field field = null;
                try{
                    field = FieldsCache.getInstance().getField(clazz, fieldName);
                }catch(FieldException ex){
                    logger.error(ex.getMessage(), ex);
                }
                Object fieldValue = FieldUtil.get(obj, field);
                if(fieldValue != null){
                    String result = null;
                    try{
                        result = highlighter.getResult(fieldName, fieldValue.toString());
                    }catch(Exception ex){
                        logger.error("Something is wrong when getting the highlighter result", ex);
                    }
                    if(!StringUtil.isEmpty(result)){
                        FieldUtil.set(obj, field, result);
                    }
                }
            }
        }
    }
    
    public void close(){
        try{
            reader.decRef();
        }catch(IOException ex){
            logger.error("Something is wrong when decrease the reference of IndexReader", ex);
        }
    }
    
    public IndexSearcher getSearcher(){
        return searcher;
    }
    
}
