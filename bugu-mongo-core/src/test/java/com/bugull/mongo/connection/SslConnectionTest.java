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

package com.bugull.mongo.connection;

import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;
import com.mongodb.MongoClientOptions;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class SslConnectionTest {
    
    @Test
    public void test() throws Exception {
        BuguConnection conn = BuguFramework.getInstance().createConnection();
        conn.setHost("192.168.1.179");
        conn.setPort(27017);
        conn.setUsername("test");
        conn.setPassword("test");
        conn.setDatabase("test");
        
        SSLContext sslContext = getSSLContext();
        MongoClientOptions options = MongoClientOptions.builder()
                .sslEnabled(true)
                .sslContext(sslContext)
                .sslInvalidHostNameAllowed(true)
                .build();
        conn.setOptions(options);
        
        conn.connect();
        
        System.out.println("SSL Connect success!");
        
        FooDao dao = new FooDao();
        Foo f = new Foo();
        f.setName("f_1");
        dao.save(f);
        
        System.out.println("Save success!");
        
        System.out.println("Foo Id: " + f.getId());

        BuguFramework.getInstance().destroy();
    }
    
    private SSLContext getSSLContext() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        String keystoreFilePath = "/Users/frankwen/Temp/cert/mongo.keystore";
        String keystorePassword = "hadlinks";

        //trust keystore
        KeyStore trustKeystore = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream(keystoreFilePath);
        trustKeystore.load(fis, keystorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("sunx509");
        tmf.init(trustKeystore);

        //init context
        context.init(null, tmf.getTrustManagers(), null);
        
        return context;
    }
    
}
