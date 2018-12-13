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
package com.bugull.mongo.crud;

import com.bugull.mongo.BuguDao;
import com.bugull.mongo.base.ReplicaSetBaseTest;
import com.bugull.mongo.cache.DaoCache;
import com.bugull.mongo.counter.Article;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class RegexQueryTest extends ReplicaSetBaseTest {
    
    @Test
    public void testStartsWith(){
        connectDB();
        
        BuguDao<Article> dao = DaoCache.getInstance().get(Article.class);
        
        List<Article> list = dao.query().startsWith("title", "97e11903-").results();
        
        for(Article article : list){
            System.out.println(article.getTitle());
        }
        
        disconnectDB();
    }
    
}
