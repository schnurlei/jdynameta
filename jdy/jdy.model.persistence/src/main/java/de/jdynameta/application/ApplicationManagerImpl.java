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
package de.jdynameta.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.FilterUtil;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.persistence.manager.PersistentOperationResult;
import de.jdynameta.persistence.manager.impl.DefaultBulkPersistentOperation;
import de.jdynameta.persistence.manager.impl.DefaultPersistentOperation;
import de.jdynameta.persistence.state.ApplicationObj;

@SuppressWarnings("serial")
public abstract class ApplicationManagerImpl<TObjToPersist, TObjFromPersist, TEditedApplicObj extends ApplicationObj>
        implements ApplicationManager< TEditedApplicObj>
{
    private final PersistentObjectManager<TObjToPersist, TObjFromPersist> persistenceManager;
    private final ListenerHandler<TEditedApplicObj> listenerHandler;
    private final PersistToApplicationConverter<TObjToPersist, TObjFromPersist, TEditedApplicObj> converter;
    private ClassInfoAttrSource attrSource;

    public ApplicationManagerImpl(PersistentObjectManager<TObjToPersist, TObjFromPersist> aPersistenceManager)
    {
        this.persistenceManager = aPersistenceManager;
        this.listenerHandler = new ListenerHandler<>();
        this.converter = createConverter();
        aPersistenceManager.addListener(null, this::persistentObjectChanged);
    }

    public ClassInfoAttrSource getAttrSource()
    {
        return attrSource;
    }

    public void setAttrSource(ClassInfoAttrSource aAttrSource)
    {
        attrSource = aAttrSource;
    }

    /**
     *
     * @param aClassInfo
     * @return
     */
    @Override
    public List<AttributeInfo> getDisplayAttributesFor(ClassInfo aClassInfo)
    {
        return (attrSource == null) ? null : attrSource.getDisplayAttributesFor(aClassInfo);
    }

    /**
     *
     * @return
     */
    protected abstract PersistToApplicationConverter<TObjToPersist, TObjFromPersist, TEditedApplicObj> createConverter();

    public PersistentObjectManager<TObjToPersist, TObjFromPersist> getPersistenceManager()
    {
        return persistenceManager;
    }

    @Override
    public void closeConnection() throws JdyPersistentException
    {
        getPersistenceManager().closeConnection();

    }

    @Override
    public TEditedApplicObj cloneObject(TEditedApplicObj editedObject) throws ObjectCreationException
    {
        TEditedApplicObj clonedObj = createObject(editedObject.getClassInfo(), null);

        for (AttributeInfo curAttrInfo : editedObject.getClassInfo().getAttributeInfoIterator())
        {

            if (!curAttrInfo.isKey())
            {
                clonedObj.setValue(curAttrInfo, editedObject.getValue(curAttrInfo));
            }
        }

        return clonedObj;
    }

    /* (non-Javadoc)
     * @see de.comafra.view.metainfo.Application2PersistenceBridge#insertObject(TEditedApplicObj, de.comafra.model.metainfo.ClassInfo)
     */
    @Override
    public void saveObject(TEditedApplicObj editedObject, WorkflowCtxt aContext) throws JdyPersistentException
    {
        getApplicationHook().beforeSave(editedObject);

        boolean wasNew = editedObject.getPersistentState().isNew();

        if (editedObject.getPersistentState().isNew())
        {

            TObjFromPersist newObject = insertInPersistentManager(editedObject, editedObject.getClassInfo());
            this.converter.setObjFromPersistInAppObj(newObject, editedObject);
        } else
        {

            updateInPersistenceManager(editedObject, editedObject.getClassInfo());
        }

        getApplicationHook().afterSave(editedObject, wasNew);
    }

    protected TObjFromPersist insertInPersistentManager(TEditedApplicObj editedObject, ClassInfo aClassInfo)
            throws JdyPersistentException
    {
        DefaultBulkPersistentOperation<TObjToPersist> bulkOperation = new DefaultBulkPersistentOperation<>();

        DefaultPersistentOperation<TObjToPersist> insertOp = new DefaultPersistentOperation<>(Operation.INSERT, aClassInfo, this.converter.convertAppEditObjToObjToPersist(editedObject, aClassInfo));
        bulkOperation.addObjectToPersist(insertOp);

        addDependentObjects(editedObject, aClassInfo, bulkOperation);

        List<PersistentOperationResult<TObjToPersist, TObjFromPersist>> resultList = persistenceManager.persistObjects(bulkOperation);

        TObjFromPersist result = null;
        for (PersistentOperationResult<TObjToPersist, TObjFromPersist> persistentOperationResult : resultList)
        {
            if (persistentOperationResult.getOperation() == insertOp)
            {
                result = persistentOperationResult.getPersistentObject();
            }
        }

        return result;
    }

    protected void addDependentObjects(TEditedApplicObj editedObject, ClassInfo aClassInfo, DefaultBulkPersistentOperation<TObjToPersist> bulkOperation)
    {

        for (AssociationInfo curAssoc : aClassInfo.getAssociationInfoIterator())
        {
            ObjectList<? extends ChangeableValueObject> assocList = editedObject.getValue(curAssoc);

            for (ChangeableValueObject changeableValueObject : assocList)
            {
                TEditedApplicObj assocObj = (TEditedApplicObj) changeableValueObject;
                if (assocObj.getPersistentState().isMarkedAsDeleted())
                {
                    DefaultPersistentOperation<TObjToPersist> deleteOp = new DefaultPersistentOperation<>(Operation.DELETE, curAssoc.getDetailClass(), this.converter.convertAppEditObjToObjToPersist(assocObj, curAssoc.getDetailClass()));
                    bulkOperation.addObjectToPersist(deleteOp);
                } else if (assocObj.getPersistentState().isNew())
                {
                    DefaultPersistentOperation<TObjToPersist> insertOp = new DefaultPersistentOperation<>(Operation.INSERT, curAssoc.getDetailClass(), this.converter.convertAppEditObjToObjToPersist(assocObj, curAssoc.getDetailClass()));
                    bulkOperation.addObjectToPersist(insertOp);
                } else if (assocObj.getPersistentState().isDirty())
                {
                    DefaultPersistentOperation<TObjToPersist> updateOp = new DefaultPersistentOperation<>(Operation.UPDATE, curAssoc.getDetailClass(), this.converter.convertAppEditObjToObjToPersist(assocObj, curAssoc.getDetailClass()));
                    bulkOperation.addObjectToPersist(updateOp);
                }
            }
        }
    }

    private void updateInPersistenceManager(TEditedApplicObj editedObject, ClassInfo aClassInfo) throws JdyPersistentException
    {
        DefaultBulkPersistentOperation<TObjToPersist> bulkOperation = new DefaultBulkPersistentOperation<>();

        DefaultPersistentOperation<TObjToPersist> insertOp = new DefaultPersistentOperation<>(Operation.UPDATE, aClassInfo, this.converter.convertAppEditObjToObjToPersist(editedObject, aClassInfo));
        bulkOperation.addObjectToPersist(insertOp);
        addDependentObjects(editedObject, aClassInfo, bulkOperation);
        persistenceManager.persistObjects(bulkOperation);
    }

    @Override
    public void refreshObject(TEditedApplicObj editedObject) throws JdyPersistentException
    {
        persistenceManager.refreshObject(this.converter.convertAppEditObjToObjToPersist(editedObject, editedObject.getClassInfo()), editedObject.getClassInfo());
    }

    @Override
    public void deleteObject(TEditedApplicObj objToDel, WorkflowCtxt aContext) throws JdyPersistentException
    {
        getApplicationHook().beforeDelete(objToDel);
        persistenceManager.deleteObject(this.converter.convertAppEditObjToObjToPersist(objToDel, objToDel.getClassInfo()), objToDel.getClassInfo());
        getApplicationHook().afterDelete(objToDel);
    }

    ;
	
	/* (non-Javadoc)
	 * @see de.comafra.view.metainfo.Application2PersistenceBridge#createObject(de.comafra.model.metainfo.ClassInfo)
	 */
	@Override
    public TEditedApplicObj createObject(ClassInfo aClassInfo, WorkflowCtxt aContext) throws ObjectCreationException
    {
        TObjFromPersist dbObject = this.persistenceManager.createNewObject(aClassInfo);
        TEditedApplicObj createdObject = this.converter.createNewAppEditObj(dbObject, aClassInfo, true, this);

        getApplicationHook().afterCreate(createdObject);

        return createdObject;
    }

    @Override
    public ObjectList<TEditedApplicObj> loadObjectsFromDb(ClassInfoQuery query) throws JdyPersistentException
    {
        ObjectList<TObjFromPersist> dbColl = this.persistenceManager.loadObjectsFromDb(query);
        ArrayList<TEditedApplicObj> viewColl = new ArrayList<>();

        for (TObjFromPersist curObj : dbColl)
        {
            viewColl.add(this.converter.createNewAppEditObj(curObj, this.converter.getClassForPersObj(curObj), false, this));
        }
        return new DefaultObjectList<>(viewColl);
    }

    @Override
    public void addListener(ClassInfo aClassInfo, PersistentListener<TEditedApplicObj> aListener)
    {
        this.listenerHandler.addListener(aClassInfo, aListener);
    }

    @Override
    public void removeListener(ClassInfo aClassInfo, PersistentListener<TEditedApplicObj> aListener)
    {
        this.listenerHandler.removeListener(aClassInfo, aListener);
    }

    protected void persistentObjectChanged(PersistentEvent<TObjFromPersist> aEvent)
    {
        TEditedApplicObj appObj = this.converter.createNewAppEditObj(aEvent.getChangedObject(), aEvent.getChangedClass(), false, this);
        fireAppObjectChanged(aEvent.getState(), aEvent.getChangedClass(), appObj);
    }

    protected void fireAppObjectChanged(PersistentObjectReader.ModifierType aState, ClassInfo aChangedClass, TEditedApplicObj appObj)
    {
        this.listenerHandler.fireAppObjectChanged(aState, aChangedClass, appObj);
    }

    /**
     * check whether an object with the same key already exists
     *
     * @param editedObject
     * @param aClassInfo
     * @return
     * @throws JdyPersistentException
     */
    public boolean existsObjectAlready(final TEditedApplicObj editedObject, ClassInfo aClassInfo)
            throws JdyPersistentException
    {
        ClassInfoQuery equalQuery = FilterUtil.createSearchEqualObjectFilter(aClassInfo, editedObject);
        return this.getPersistenceManager().loadObjectsFromDb(equalQuery).size() > 0;
    }

    protected abstract ApplicationHook<ApplicationObj> getApplicationHook();

    /**
     *
     * @author rainer
     *
     * @param <TListenerObj>
     */
    public static class ListenerHandler<TListenerObj>
    {
        private final HashMap<ClassInfo, ArrayList<PersistentListener<TListenerObj>>> classInfo2ListenerListMap;

        public ListenerHandler()
        {
            this.classInfo2ListenerListMap = new HashMap<>(100);
        }

        public void addListener(ClassInfo aClassInfo, PersistentListener<TListenerObj> aListener)
        {
            ArrayList<PersistentListener<TListenerObj>> listenerList = this.classInfo2ListenerListMap.get(aClassInfo);
            if (listenerList == null)
            {
                listenerList = new ArrayList<>();
                this.classInfo2ListenerListMap.put(aClassInfo, listenerList);
            }

            listenerList.add(aListener);
        }

        public void removeListener(ClassInfo aClassInfo, PersistentListener<TListenerObj> aListener)
        {
            ArrayList<PersistentListener<TListenerObj>> listenerList = this.classInfo2ListenerListMap.get(aClassInfo);
            if (listenerList != null)
            {
                listenerList.remove(aListener);
            }
        }

        private void firePersistentStateChanged(ClassInfo aNotifiedClass, ClassInfo aChangedClass, TListenerObj aChangedObject, PersistentObjectReader.ModifierType aState)
        {
            ArrayList<PersistentListener<TListenerObj>> listenerList = this.classInfo2ListenerListMap.get(aNotifiedClass);
            if (listenerList != null)
            {
                PersistentEvent<TListenerObj> newEvent = new PersistentEvent<>(this, aNotifiedClass, aChangedClass, aChangedObject, aState);

                listenerList.stream().forEach((curListener) ->
                {
                    curListener.persistentStateChanged(newEvent);
                });
            }

            ArrayList<PersistentListener<TListenerObj>> commonListenerList = this.classInfo2ListenerListMap.get(null);
            if (commonListenerList != null)
            {
                PersistentEvent<TListenerObj> newEvent = new PersistentEvent<>(this, aNotifiedClass, aChangedClass, aChangedObject, aState);

                commonListenerList.stream().forEach((curListener) ->
                {
                    curListener.persistentStateChanged(newEvent);
                });
            }
        }

        protected void fireAppObjectChanged(PersistentObjectReader.ModifierType aState, ClassInfo aChangedClass, TListenerObj appObj)
        {
            // send also a notification for all superclasses
            ClassInfo notifyClassInfo = aChangedClass;
            while (notifyClassInfo != null)
            {

                firePersistentStateChanged(notifyClassInfo, aChangedClass, appObj, aState);
                notifyClassInfo = notifyClassInfo.getSuperclass();
            }
        }

    }

    /**
     *
     * @author rainer
     *
     * @param <TObjToPersist>
     * @param <TObjFromPersist>
     * @param <TEditedApplicObj>
     */
    public static interface PersistToApplicationConverter<TObjToPersist, TObjFromPersist, TEditedApplicObj extends ApplicationObj>
    {
        public TEditedApplicObj createNewAppEditObj(TObjFromPersist persistentObj, ClassInfo classInfo, boolean isNew, ApplicationManager<TEditedApplicObj> anAppManager);

        public void setObjFromPersistInAppObj(TObjFromPersist persistentObj, TEditedApplicObj anAppObj);

        public TObjToPersist convertAppEditObjToObjToPersist(TEditedApplicObj aPersistentObj, ClassInfo aClassInfo);

        public ClassInfo getClassForPersObj(TObjFromPersist aPersObj);
    }

    /**
     *
     * @author rainer
     *
     * @param <TEditObj>
     */
    public static class WorkflowApplicationHook<TEditObj extends ApplicationObj> implements ApplicationHook<TEditObj>
    {
        private final Map<ClassInfo, WorkflowPersistenceHook<TEditObj>> classInfo2PersHookMap;

        public WorkflowApplicationHook()
        {
            this.classInfo2PersHookMap = new HashMap<>();
        }

        public void setPersistenceHookForClass(ClassInfo aClassInfo, WorkflowPersistenceHook<TEditObj> aPersHook)
        {
            this.classInfo2PersHookMap.put(aClassInfo, aPersHook);
        }

        public WorkflowPersistenceHook<TEditObj> getWorkflowPersistenceHookFor(ClassInfo classInfo)
        {
            return classInfo2PersHookMap.get(classInfo);
        }

        @Override
        public void beforeSave(TEditObj aEditedObject)
                throws JdyPersistentException
        {
            WorkflowPersistenceHook<TEditObj> persHook = getWorkflowPersistenceHookFor(aEditedObject.getClassInfo());
            if (persHook != null)
            {
                try
                {
                    persHook.beforeSave(aEditedObject);
                } catch (WorkflowException ex)
                {

                    throw new JdyPersistentException(ex);
                }
            }

        }

        @Override
        public void afterSave(TEditObj aEditedObject,
                boolean aWasNew) throws JdyPersistentException
        {
            WorkflowPersistenceHook<TEditObj> persHook = getWorkflowPersistenceHookFor(aEditedObject.getClassInfo());
            if (persHook != null)
            {
                persHook.afterSave(aEditedObject, aWasNew);
            }

        }

        @Override
        public void afterCreate(TEditObj aCreatedObject)
        {
            WorkflowPersistenceHook<TEditObj> persHook = getWorkflowPersistenceHookFor(aCreatedObject.getClassInfo());
            if (persHook != null)
            {
                persHook.afterCreate(aCreatedObject);
            }
        }

        @Override
        public void beforeDelete(TEditObj aObjToDelete)
                throws JdyPersistentException
        {
            WorkflowPersistenceHook<TEditObj> persHook = getWorkflowPersistenceHookFor(aObjToDelete.getClassInfo());
            if (persHook != null)
            {
                try
                {
                    persHook.beforeDelete(aObjToDelete);
                } catch (WorkflowException ex)
                {

                    throw new JdyPersistentException(ex);
                }
            }
        }

        @Override
        public void afterDelete(TEditObj aDeletedObj)
                throws JdyPersistentException
        {
            WorkflowPersistenceHook<TEditObj> persHook = getWorkflowPersistenceHookFor(aDeletedObj.getClassInfo());
            if (persHook != null)
            {
                try
                {
                    persHook.afterDelete(aDeletedObj);
                } catch (WorkflowException ex)
                {

                    throw new JdyPersistentException(ex);
                }
            }

        }
    ;

}

}
