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
package de.jdynameta.jdy.net.websocket;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;

/**
 * 
 * @author rs
 *
 */
public class TestWebsocketNotification extends TestCase
{
	private static final int PORT = 9081;
	private TestStartWebsocketServlet	testServlet;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		testServlet = new TestStartWebsocketServlet();
		testServlet.startJettyServer(PORT);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		testServlet.stopServer();
	}
	
	 /**
     * Test insert of simple object 
     * @throws Exception test failed
     */
    public void testInsertCompany() throws Exception 
    {
    	assertTrue(testServlet.isRunning());
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger(JdyWebSocketNotification.class.getName()).addHandler(handler);
		Logger.getLogger(JdyWebSocketNotification.class.getName()).setLevel(Level.ALL);
    	
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		

		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCompanyId(new Integer(100));		
		testCompany.setCity("Roggenburg");		
		testCompany.setCompanyName("Successfull living Comp.");		
		testCompany.setStreet("I did it my Way");		
		testCompany.setZip("D-89297");		
		
		//String serverName = "ws://localhost:"+PORT+"/";
		String destUri = "ws://127.0.0.1:" + PORT + "/echo";
		JdyWebSocketNotification notification1 = new JdyWebSocketNotification(new URI(destUri), repository);
		boolean connected = false;
		for( int i = 0; i < 20 && !connected; i++) {
			try {
				notification1.connect();
				connected = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JdyWebSocketNotification notification2 = new JdyWebSocketNotification(new URI(destUri), repository);
		connected = false;
		for( int i = 0; i < 20 && !connected; i++) {
			try {
				notification2.connect();
				connected = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		final List<ObjectList<PersistentOperation<TypedValueObject>>> listHolder = new ArrayList<ObjectList<PersistentOperation<TypedValueObject>>>();
		notification2.addPersistenceListener(new JdyWebSocketNotification.PersistenceListener() {
			
			@Override
			public void persistenceChanged(	ObjectList<PersistentOperation<TypedValueObject>> changedObjects) 
			{
				listHolder.add(changedObjects);
			}
		});

		notification1.sendObjectNotification(testCompany, repository.getCompanyClassInfo(), Operation.INSERT);
		
		// wait for notification
		for(int i =0 ; i < 10 && listHolder.size() == 0; i++) {
			Thread.sleep(2000);
		}
		
		assertEquals(1, listHolder.size());
    }
	
}
