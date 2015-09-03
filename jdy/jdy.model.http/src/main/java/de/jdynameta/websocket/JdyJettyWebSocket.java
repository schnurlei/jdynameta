package de.jdynameta.websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;

/**
 * Example EchoSocket using Adapter.
 */
public class JdyJettyWebSocket extends WebSocketAdapter
{

    private NotificationDispatcher dispatcher;

    @Override
    public void onWebSocketText(String message)
    {
        if (isConnected())
        {
            dispatcher.dispatchMessage(message);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2)
    {
        System.out.println("onWebSocketBinary");

    }

    @OnWebSocketClose
    @Override
    public void onWebSocketClose(int arg0, String arg1)
    {

        this.dispatcher.removeMember(this);
    }

    @OnWebSocketConnect
    @Override
    public void onWebSocketConnect(Session aConnection)
    {
        super.onWebSocketConnect(aConnection);
        System.out.println("onWebSocketConnect");

    }

    @Override
    public void onWebSocketError(Throwable arg0)
    {
        System.out.println("onWebSocketError");

    }

    public void setDispatcher(NotificationDispatcher members)
    {

        this.dispatcher = members;

    }

    public void write(String message) throws IOException
    {
        if (isConnected())
        {
            System.out.printf("Echoing back message [%s]%n", message);
            getRemote().sendString(message);
        }
    }

}
