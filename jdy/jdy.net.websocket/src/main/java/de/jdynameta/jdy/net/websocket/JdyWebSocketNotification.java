/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.jdy.net.websocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.transform.TransformerConfigurationException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.jdy.model.json.JsonFileWriter;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Very simple implementation of a Websocket Client which get notifications
 * about the state change from persistent objects
 *
 * @author rs
 *
 */
public class JdyWebSocketNotification
{
    public static final Logger log = Logger.getLogger(JdyWebSocketNotification.class.getName());
    private static int socketCounter = 0;
    private final URI serverUri;
    private JettyWebSocketEventHandler webSocketClient;
    private final ArrayList<PersistenceListener> allListener;
    private final ClassRepository repository;
    private final int socketNumber;

    /**
     *
     * @param aUrl
     * @param aRepository repository to resolve class names from server
     */
    public JdyWebSocketNotification(URI aUrl, ClassRepository aRepository)
    {
        this.serverUri = aUrl;
        this.repository = aRepository;
        this.allListener = new ArrayList<>();
        this.socketNumber = socketCounter++;
    }

    public void addPersistenceListener(PersistenceListener aListener)
    {
        this.allListener.add(aListener);
    }

    public void removePersistenceListener(PersistenceListener aListener)
    {
        this.allListener.remove(aListener);
    }

    public void connect() throws JdyPersistentException
    {
        WebSocketClient client = new WebSocketClient();
        webSocketClient = createWebSocketListener();
        try
        {
            client.start();
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            Future<Session> session = client.connect(webSocketClient, serverUri, request);
            System.out.printf("Connecting to : %s%n", serverUri);
//            webSocketClient.awaitClose(50, TimeUnit.SECONDS);
        } catch (Throwable t)
        {
            t.printStackTrace();
        } finally
        {
//            try {
//                client.stop();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    public void disconnect() throws IOException
    {
        webSocketClient.session.close(StatusCode.NORMAL, "I'm done");

    }

    public void sendObjectNotification(ValueObject aObjToNotifyAbout, ClassInfo aInfo, Operation aPersistentType) throws JdyPersistentException
    {
        if (webSocketClient == null)
        {
            throw new JdyPersistentException("Not connected ");
        }

        JsonFileWriter fileWriter = new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        DefaultObjectList<TypedValueObject> singleElementList = new DefaultObjectList<>(new TypedWrappedValueObject(aObjToNotifyAbout, aInfo));

        try
        {
            fileWriter.writeObjectList(printWriter, aInfo, singleElementList, aPersistentType);
        } catch (TransformerConfigurationException e)
        {
            throw new JdyPersistentException(e.getLocalizedMessage(), e);
        }
        String content = writer.toString();
        log.log(Level.FINE, "Websocket  {0} send   notification {1}", new Object[]{socketNumber, content});
        try
        {
            webSocketClient.session.getRemote().sendString(content);
        } catch (IOException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }
    }

    private JettyWebSocketEventHandler createWebSocketListener()
    {
        return new JettyWebSocketEventHandler();

    }

    @WebSocket(maxMessageSize = 64 * 1024)
    public class JettyWebSocketEventHandler
    {

        private final CountDownLatch closeLatch;

        @SuppressWarnings("unused")
        private Session session;

        public JettyWebSocketEventHandler()
        {
            this.closeLatch = new CountDownLatch(1);
        }

        public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
        {
            return this.closeLatch.await(duration, unit);
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason)
        {
            log.log(Level.FINE, "Connection closed: {0} - {1}", new Object[]{statusCode, reason});
            this.session = null;
            this.closeLatch.countDown();
        }

        @OnWebSocketConnect
        public void onConnect(Session session)
        {
            log.log(Level.FINE, "Got connect: {0}", session);
            this.session = session;
        }

        @OnWebSocketMessage
        public void onMessage(String msg)
        {
            log.log(Level.FINE, "Websocket  {0} receive message: {1}", new Object[]{session, msg});

            JDyWebsocketReader fileReader = new JDyWebsocketReader()
            {
                @Override
                protected ClassInfo getConcreteClass(String aNamespaceName, String aClassInternalName)
                {
                    ClassInfo result = null;
                    for (ClassInfo classInfo : repository.getAllClassInfosIter())
                    {
                        if (classInfo.getInternalName().equals(aClassInternalName))
                        {
                            result = classInfo;
                        }

                    }
                    return result;
                }
            };
            try
            {
                ObjectList<PersistentOperation<TypedValueObject>> changedObjects = fileReader.readObjectList(new StringReader(msg));

                for (PersistenceListener listener : allListener)
                {
                    listener.persistenceChanged(changedObjects);
                }

            } catch (JdyPersistentException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }

        }
    }

    public static interface PersistenceListener
    {
        public void persistenceChanged(ObjectList<PersistentOperation<TypedValueObject>> changedObjects);
    }

}
