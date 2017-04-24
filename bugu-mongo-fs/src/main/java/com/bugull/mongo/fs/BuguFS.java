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

package com.bugull.mongo.fs;

import com.bugull.mongo.BuguFramework;
import com.bugull.mongo.annotations.Default;
import com.bugull.mongo.utils.Operator;
import com.bugull.mongo.utils.SortUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

/**
 * Basic class for operating the GridFS.
 * 
 * <p>BuguFS uses the BuguConnection class internally, so you don't need to care about the connetion and collections of GridFS.</p>
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class BuguFS {
    
    private final static Logger logger = LogManager.getLogger(BuguFS.class.getName());
    
    private final GridFS fs;
    private final DBCollection files;
    private final String bucket;
    private final int chunkSize;
    
    public final static String BUCKET = "bucket";
    public final static String FILENAME = "filename";
    
    public BuguFS(String bucket, int chunkSize){
        this(Default.NAME, bucket, chunkSize);
    }
    
    public BuguFS(String connectionName, String bucket, int chunkSize){
        this.bucket = bucket;
        this.chunkSize = chunkSize;
        DB db = BuguFramework.getInstance().getConnection(connectionName).getDB();
        fs = new GridFS(db, bucket);
        files = db.getCollection(bucket + ".files");
        //ensure the DBCursor can be cast to GridFSDBFile
        files.setObjectClass(GridFSDBFile.class);
    }
    
    public GridFS getGridFS(){
        return fs;
    }
    
    public String save(File file){
        return save(file, file.getName(), null);
    }
    
    public String save(File file, String filename){
        return save(file, filename, null);
    }
    
    public String save(File file, String filename, Map<String, Object> attributes){
        GridFSInputFile f = null;
        try{
            f = fs.createFile(file);
        }catch(IOException ex){
            logger.error("Can not create GridFSInputFile", ex);
        }
        if(f != null){
            f.setChunkSize(chunkSize);
            f.setFilename(filename);
            setAttributes(f, attributes);
            f.save();
        }
        return f.getId().toString();
    }
    
    public String save(InputStream is, String filename){
        return save(is, filename, null);
    }
    
    public String save(InputStream is, String filename, Map<String, Object> attributes){
        GridFSInputFile f = fs.createFile(is);
        f.setChunkSize(chunkSize);
        f.setFilename(filename);
        setAttributes(f, attributes);
        f.save();
        return f.getId().toString();
    }
    
    public String save(byte[] data, String filename){
        return save(data, filename, null);
    }
    
    public String save(byte[] data, String filename, Map<String, Object> attributes){
        GridFSInputFile f = fs.createFile(data);
        f.setChunkSize(chunkSize);
        f.setFilename(filename);
        setAttributes(f, attributes);
        f.save();
        return f.getId().toString();
    }
    
    private void setAttributes(GridFSInputFile f, Map<String, Object> attributes){
        if(attributes != null){
            for(Entry<String, Object> entry : attributes.entrySet()){
                f.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public boolean exists(String filename){
        GridFSDBFile f = fs.findOne(filename);
        return f != null;
    }
    
    public boolean existsId(String id){
        GridFSDBFile f = fs.findOne(new ObjectId(id));
        return f != null;
    }
    
    public GridFSDBFile findOne(String filename){
        return fs.findOne(filename);
    }
    
    public GridFSDBFile findOneById(String id){
        return fs.findOne(new ObjectId(id));
    }
    
    public GridFSDBFile findOne(DBObject query){
        return fs.findOne(query);
    }
    
    public List<GridFSDBFile> find(DBObject query){
        return fs.find(query);
    }
    
    public List<GridFSDBFile> find(DBObject query, String orderBy){
        DBObject sort = SortUtil.getSort(orderBy);
        return fs.find(query, sort);
    }
    
    public List<GridFSDBFile> find(DBObject query, int pageNum, int pageSize){
        DBCursor cursor = files.find(query).skip((pageNum-1)*pageSize).limit(pageSize);
        return toFileList(cursor);
    }
    
    public List<GridFSDBFile> find(DBObject query, String orderBy, int pageNum, int pageSize){
        DBObject sort = SortUtil.getSort(orderBy);
        DBCursor cursor = files.find(query).sort(sort).skip((pageNum-1)*pageSize).limit(pageSize);
        return toFileList(cursor);
    }
    
    public void rename(String oldName, String newName){
        DBObject query = new BasicDBObject(FILENAME, oldName);
        DBObject dbo = files.findOne(query);
        dbo.put(FILENAME, newName);
        files.save(dbo);
    }
    
    public void renameById(String id, String newName){
        DBObject query = new BasicDBObject(Operator.ID, new ObjectId(id));
        DBObject dbo = files.findOne(query);
        dbo.put(FILENAME, newName);
        files.save(dbo);
    }
    
    public void rename(GridFSDBFile file, String newName){
        ObjectId id = (ObjectId)file.getId();
        DBObject query = new BasicDBObject(Operator.ID, id);
        DBObject dbo = files.findOne(query);
        dbo.put(FILENAME, newName);
        files.save(dbo);
    }
    
    public void remove(String filename){
        fs.remove(filename);
    }
    
    public void removeById(String id){
        fs.remove(new ObjectId(id));
    }
    
    public void remove(DBObject query){
        fs.remove(query);
    }
    
    private List<GridFSDBFile> toFileList(DBCursor cursor){
        List<GridFSDBFile> list = new ArrayList<GridFSDBFile>();
        while(cursor.hasNext()){
            DBObject dbo = cursor.next();
            list.add((GridFSDBFile)dbo);
        }
        cursor.close();
        return list;
    }

    public String getBucket() {
        return bucket;
    }

    public int getChunkSize() {
        return chunkSize;
    }
    
}
