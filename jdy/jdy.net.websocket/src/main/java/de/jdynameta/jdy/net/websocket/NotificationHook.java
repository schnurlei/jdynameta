package de.jdynameta.jdy.net.websocket;

import java.net.URI;

import de.jdynameta.application.WorkflowException;
import de.jdynameta.application.WorkflowPersistenceHookAdaptor;
import de.jdynameta.application.impl.AppPersistentGenericObj;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.MetaRepositoryCreator;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.jdy.net.websocket.JdyWebSocketNotification.PersistenceListener;

/**
 * Hook for the notification from an Websocket
 *
 * @author rainer
 *
 */
public class NotificationHook extends WorkflowPersistenceHookAdaptor<AppPersistentGenericObj>
{
    private JdyWebSocketNotification notification;

    public NotificationHook(AppRepository anAppRepository, URI aNotificationServerUrl)
    {
        MetaRepositoryCreator repCreator = new MetaRepositoryCreator(null);
        ClassRepository metaRep = repCreator.createMetaRepository(anAppRepository);
        this.notification = new JdyWebSocketNotification(aNotificationServerUrl, metaRep);

        boolean connectSuccessfull = false;
        // @todo replace with reconnection thread
        for (int i = 0; i < 3 && !connectSuccessfull; i++)
        {
            try
            {
                notification.connect();
                connectSuccessfull = true;
            } catch (JdyPersistentException ex)
            {
                // @todo 
//					log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    public void addPersistenceListener(PersistenceListener aListener)
    {
        notification.addPersistenceListener(aListener);
    }

    @Override
    public void afterSave(AppPersistentGenericObj aEditedObject, boolean wasNew)
    {
        try
        {
            this.notification.sendObjectNotification(aEditedObject, aEditedObject.getClassInfo(), (wasNew) ? Operation.INSERT : Operation.UPDATE);
        } catch (JdyPersistentException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void afterDelete(AppPersistentGenericObj aObjToDel) throws WorkflowException
    {
        try
        {
            this.notification.sendObjectNotification(aObjToDel, aObjToDel.getClassInfo(), Operation.DELETE);
        } catch (JdyPersistentException e)
        {
            throw new WorkflowException(e.getLocalizedMessage(), e);
        }
    }
}
