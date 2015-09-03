package de.jdynameta.websocket;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationDispatcher
{

    private final List<JdyJettyWebSocket> members = new CopyOnWriteArrayList<>();

    public void addMember(JdyJettyWebSocket newMember)
    {
        this.members.add(newMember);
    }

    public void removeMember(JdyJettyWebSocket aMember)
    {
        this.members.remove(aMember);

    }

    public void dispatchMessage(String message)
    {

        ListIterator<JdyJettyWebSocket> iter = members.listIterator();
        while (iter.hasNext())
        {
            JdyJettyWebSocket member = iter.next();

            try
            {
                // Async write the message back.
                member.write(message);
            } catch (IOException e)
            {
            }
        }
    }
}
