/*
 * Copyright 2015 rainer.
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
package de.jdynameta.jdy.jaxrs;


import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.jdy.net.httpclient.JsonConnection;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.ApplicationRepositoryClassFileGenerator;
import de.jdynameta.metamodel.application.MetaRepositoryCreator;
import de.jdynameta.metamodel.filter.FilterRepository;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author rainer
 */
public class TestMetadataResource {
    
    private static Server jettyServer;
    
    @BeforeClass
    public static void startServer() throws Exception
    {
       ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
 
        jettyServer = new Server(8080);
        jettyServer.setHandler(context);
 
        ServletHolder jerseyServlet = context.addServlet(
             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
 
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           EntryPoint.class.getCanonicalName());
 
        jettyServer.start();
    }
    
    @AfterClass
    public static void stopServer() throws Exception {
        jettyServer.stop();
    }
    
    @Test
    public void connect() throws Exception {
        
        HttpClient httpclient = HttpClientBuilder.create().build();

//        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);
//        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 50000);

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost("localhost");
        builder.setPort(8080);
        builder.setPath("/entry-point/test");
        builder.setQuery("filter");

        URI getUri = builder.build();
        HttpGet httpget = new HttpGet(getUri);

//		get.setHeader("Content-Type", "application/json");
//		get.setHeader("Accept", "application/json");
        HttpResponse response = httpclient.execute(httpget);
        
        System.out.println(response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        long len = entity.getContentLength();
        String responseString = EntityUtils.toString(entity);
        System.out.println(responseString);
 
    }
    
    	@Test
	public void testFilterRepository() throws JdyPersistentException
	{
		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		DbAccessConnection<ValueObject, GenericValueObjectImpl> connection = new JsonConnection<>("localhost", 8080, "/entry-point/test", ApplicationRepository.META_REPO_NAME, transformator);
//		MetaRepositoryCreator creator = new MetaRepositoryCreator(connection);
//		creator.createAppRepository(FilterRepository.getSingleton());
		
		DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
		ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
		System.out.println(readObjects);
	}
}
