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
package de.jdynameta.websocket;

import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Class to start a testversion of the websocket servlet
 * @author rainer
 *
 */
public class TestStartWebsocketServlet
{

	private Server	jettyServer;

	public void startJettyServer(int port) throws Exception
	{
		URL url1 = TestStartWebsocketServlet.class.getResource("context");
		System.out.println("Web Path: " + (( url1 == null) ? "Empty Websocket Url" : url1.getPath()));
		jettyServer = createJettyServer(port, url1.getPath(),"/context");

	}

	
	
	public void stopJettyServer() throws Exception
	{
		System.out.println(">>> STOPPING EMBEDDED JETTY SERVER"); 
		// while (System.in.available() == 0) {
		//   Thread.sleep(5000);
		// }
		if( jettyServer != null ){
			jettyServer.stop();
			jettyServer.join();
		}
	}

	
	
	public void stopServer() throws Exception
	{
		jettyServer.stop();
	}

	public boolean isRunning()
	{
		return jettyServer.isRunning();
	}
	
	protected Server createJettyServer(int port, String war1, String context1) throws Exception
	{
	 

		Server server = new Server(port);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("./src/main/resources/");

		ServletHandler servletHandler = new ServletHandler();
		server.setHandler(servletHandler);
		servletHandler.addServletWithMapping(JDynametaWebsocketServlet.class, "/*");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler,
				servletHandler });
		server.setHandler(handlers);


		server.start();
		//server.join();
	
		return server;
	}
	
	public static void main(String[] args)
	{
		try {
			new TestStartWebsocketServlet().startJettyServer(9081);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
