/*
 * Copyright (c) www.bugull.com
 */

package com.bugull.mongo.split;

import com.bugull.mongo.base.BaseTest;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FriendTest extends BaseTest {
    
    //@Test
    public void testInsert(){
        connectDB();
        
        FriendDao dao = new FriendDao();
        
        Friend f1 = new Friend();
        f1.setName("Frank");
        f1.setProvince("Zhejiang");
        
        dao.setSplitSuffix(f1.getProvince());
        dao.save(f1);
        
        Friend f2 = new Friend();
        f2.setName("Tom");
        f2.setProvince("Shanghai");
        
        dao.setSplitSuffix(f2.getProvince());
        dao.save(f2);
        
        disconnectDB();
    }
    
    @Test
    public void testQuery(){
        connectDB();
        
        FriendDao dao = new FriendDao();
        dao.setSplitSuffix("Zhejiang");
        
        List<Friend> list = dao.findAll();
        for(Friend f : list){
            System.out.println("name: " + f.getName());
            System.out.println("province: " + f.getProvince());
        }
        
        disconnectDB();
    }

}
