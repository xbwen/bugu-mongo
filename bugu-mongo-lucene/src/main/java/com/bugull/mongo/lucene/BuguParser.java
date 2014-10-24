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

import java.sql.Timestamp;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * The utility class for creating lucene query.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public final class BuguParser {
    
    private final static Logger logger = LogManager.getLogger(BuguParser.class.getName());
    
    /**
     * Create a term query. 
     * <p>Note: in fact, it does not parse. It just create a term query.</p>
     * @param field
     * @param value
     * @return 
     */
    public static Query parseTerm(String field, String value){
        Term t = new Term(field, value);
        return new TermQuery(t);
    }
    
    public static Query parse(String field, String value){
        return parse(field, value, Operator.OR);
    }
    
    public static Query parse(String field, String value, Operator op){
        QueryParser parser = new QueryParser(BuguIndex.getInstance().getVersion(), field, BuguIndex.getInstance().getAnalyzer());
        parser.setDefaultOperator(op);
        return parse(parser, value);
    }
    
    public static Query parse(String[] fields, String value){
        return parse(fields, value, Operator.OR);
    }
    
    public static Query parse(String[] fields, String value, Operator op){
        QueryParser parser = new MultiFieldQueryParser(BuguIndex.getInstance().getVersion(), fields, BuguIndex.getInstance().getAnalyzer());
        parser.setDefaultOperator(op);
        return parse(parser, value);
    }
    
    public static Query parse(String[] fields, Occur[] occurs, String value){
        Query query = null;
        try{
            query = MultiFieldQueryParser.parse(BuguIndex.getInstance().getVersion(), value, fields, occurs, BuguIndex.getInstance().getAnalyzer());
        }catch(ParseException ex){
            logger.error("MultiFieldQueryParser can not parse the value " + value , ex);
        }
        return query;
    }
    
    private static Query parse(QueryParser parser, String value){
        Query query = null;
        try{
            query = parser.parse(value);
        }catch(ParseException ex){
            logger.error("Can not parse the value " + value , ex);
        }
        return query;
    }
    
    public static Query parse(String field, int value){
        return NumericRangeQuery.newIntRange(field, value, value, true, true);
    }
    
    public static Query parse(String field, int minValue, int maxValue){
        return NumericRangeQuery.newIntRange(field, minValue, maxValue, true, true);
    }
    
    public static Query parse(String field, long value){
        return NumericRangeQuery.newLongRange(field, value, value, true, true);
    }
    
    public static Query parse(String field, long minValue, long maxValue){
        return NumericRangeQuery.newLongRange(field, minValue, maxValue, true, true);
    }
    
    public static Query parse(String field, float value){
        return NumericRangeQuery.newFloatRange(field, value, value, true, true);
    }
    
    public static Query parse(String field, float minValue, float maxValue){
        return NumericRangeQuery.newFloatRange(field, minValue, maxValue, true, true);
    }
    
    public static Query parse(String field, double value){
        return NumericRangeQuery.newDoubleRange(field, value, value, true, true);
    }
    
    public static Query parse(String field, double minValue, double maxValue){
        return NumericRangeQuery.newDoubleRange(field, minValue, maxValue, true, true);
    }
    
    /**
     * Date value is converted to long value, same as it's field type in index file. 
     * @param field
     * @param begin
     * @param end
     * @return 
     */
    public static Query parse(String field, Date begin, Date end){
        long beginTime = begin.getTime();
        long endTime = end.getTime();
        return parse(field, beginTime, endTime);
    }
    
    /**
     * Timestamp value is converted to long value, same as it's field type in index file. 
     * @param field
     * @param begin
     * @param end
     * @return 
     */
    public static Query parse(String field, Timestamp begin, Timestamp end){
        long beginTime = begin.getTime();
        long endTime = end.getTime();
        return parse(field, beginTime, endTime);
    }
    
    /**
     * Boolean value is converted to string value, same as it's field type in index file. 
     * @param field
     * @param value
     * @return 
     */
    public static Query parse(String field, boolean value){
        if(value){
            return parse(field, "true");
        }else{
            return parse(field, "false");
        }
    }
    
    /**
     * Char value is converted to string value, same as it's field type in index file. 
     * @param field
     * @param value
     * @return 
     */
    public static Query parse(String field, char value){
        return parse(field, String.valueOf(value));
    }
    
}
