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

import java.io.StringReader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

/**
 * Wrapped class on top of lucene highlighter for convinient use.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguHighlighter {
    
    private String keywords;
    private String[] fields;
    private Formatter formatter = new SimpleHTMLFormatter("<font color=\"#FF0000\">", "</font>");
    private int maxFragments = 3;
    
    public BuguHighlighter(){
        //default constructor
    }
    
    public BuguHighlighter(String field, String keywords){
        this.fields = new String[]{field};
        this.keywords = keywords;
    }
    
    public BuguHighlighter(String[] fields, String keywords){
        this.fields = fields;
        this.keywords = keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public void setMaxFragments(int maxFragments) {
        this.maxFragments = maxFragments;
    }
    
    public String getResult(String fieldName, String fieldValue) throws Exception{
        BuguIndex index = BuguIndex.getInstance();
        QueryParser parser = new QueryParser(index.getVersion(), fieldName, index.getAnalyzer());
        Query query = parser.parse(keywords);
        TokenStream tokens = index.getAnalyzer().tokenStream(fieldName, new StringReader(fieldValue));
        QueryScorer scorer = new QueryScorer(query, fieldName);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));
        return highlighter.getBestFragments(tokens, fieldValue, maxFragments, "...");
    }
    
}
