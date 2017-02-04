/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.json.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;


public class StartRestServer 
{
	public static final String DB_USER = "sa"; 
	public static final String DB_PASSWORD = "sa"; 
	
	private void startDatabase()
	{
		File databasePath = new File(System.getProperty("user.home"), ".jDyAppeditor");
		System.out.println("+++++++++++Home Path " + databasePath.getAbsolutePath());
		databasePath = new File("." + File.separator + ".jDyAppeditor");
		System.out.println("+++++++++++Context Path " + databasePath.getAbsolutePath());
		startDatabase(new File(databasePath,"TestDbRsc"));
	}

	
	public void startDatabase(File databasePath)
	{
		try {
			startDatabaseServer(databasePath, "JDynaMeta");
		} catch (Exception e1) {
			System.out.println(e1.getLocalizedMessage());
		}
	}
	
	public static void startDatabaseServer(final File aDbFile, String aSchemaName) throws IOException, AclFormatException 
	{
		HsqlProperties p = new HsqlProperties();
	    p.setProperty("server.database.0","file:" +aDbFile.getAbsolutePath()+";user="+DB_USER+";password="+DB_PASSWORD);
		p.setProperty("server.dbname.0",aSchemaName);
		// set up the rest of properties
		org.hsqldb.server.Server server = new org.hsqldb.server.Server();
		server.setProperties(p);
		server.setLogWriter(new PrintWriter(System.out)); // can use custom writer
		server.setErrWriter(new PrintWriter(System.out)); // can use custom writer
		server.start();

	
	}
		
	
	public Server startServer(Servlet aServlet, int port) throws Exception
	{

            URL url = StartRestServer.class.getResource("jetty-env.xml");
            final XmlConfiguration xconfig = new XmlConfiguration(url);

            Server server = new Server(port);
		 
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.addServlet(new ServletHolder(aServlet),"/*");
            context.setContextPath("/");

            WebAppContext appcontext = new WebAppContext();

            appcontext.setAttribute("deleteSchema", Boolean.TRUE);
    //        appcontext.setDescriptor("config/web.xml");      
            appcontext.setResourceBase(".");
            xconfig.configure(appcontext);
            context.setContextPath("/");
            appcontext.setParentLoaderPriority(true);
            appcontext.addServlet(new ServletHolder(aServlet),"/*");
            server.setHandler(appcontext);


            server.start();
            return server;	
		
		
//		Server server = new Server(port);
//        Resource fileserver_xml = Resource.newSystemResource("fileserver.xml");
//        XmlConfiguration configuration = new XmlConfiguration(fileserver_xml.getInputStream());
//        Server server = (Server)configuration.configure();
//	
//        envConfiguration envConfiguration = new EnvConfiguration();
//        envConfiguration.setJettyEnvXml(url);
//
//        bb.setConfigurations(new Configuration[]{ new WebInfConfiguration(), envConfiguration, new WebXmlConfiguration() });
		 
//        
//        Servletcon
//        
//        WebAppContext context = new WebAppContext();
//        xconfig.configure(context);
//        context.setContextPath("/");
//        context.addServlet(new ServletHolder(aServlet),"/*");
//        server.setHandler(context);
//        //context.setDescriptor(webapp+"/WEB-INF/web.xml");
// 
  
//        server.start();
//        return server;	
	}
	
}
