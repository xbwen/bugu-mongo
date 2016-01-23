/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.aggregation;

import com.bugull.mongo.BuguDao;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class CommentDao extends BuguDao<Comment> {
    
    public CommentDao(){
        super(Comment.class);
    }

}
