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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Very simple websocket servlet to notifies the connected client about changes
 * in other clients
 *
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class JDynametaWebsocketServlet extends WebSocketServlet implements WebSocketCreator
{

    private final NotificationDispatcher members = new NotificationDispatcher();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        getServletContext().getNamedDispatcher("default").forward(request, response);
    }

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.getPolicy().setIdleTimeout(30 * 60 * 1000);
        factory.register(JdyJettyWebSocket.class);
        factory.setCreator(this);
    }

    @Override
    public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp)
    {

        JdyJettyWebSocket newSocket = new JdyJettyWebSocket();
        members.addMember(newSocket);
        newSocket.setDispatcher(this.members);
        return newSocket;
    }
}
