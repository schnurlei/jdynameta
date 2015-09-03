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
package de.jdynameta.persistence.limbus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.FilterUtil;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.manager.BulkPersistentOperation;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.manager.PersistentOperationResult;
import de.jdynameta.persistence.manager.impl.DefaultPersistentOperationResult;

/**
 * @author Rainer
 *
 * @version 04.07.2002
 * @param <TObjToWrite>
 * @param <TReadedObj>
 */
@SuppressWarnings("serial")
public abstract class LimbusBasicPersistenceObjectManagerImpl<TObjToWrite, TReadedObj> implements PersistentObjectManager<TObjToWrite, TReadedObj>, Serializable
{
    private final Map<ClassInfo, QueryCache<TReadedObj>> classInfo2QueryCache;
    private final DbAccessConnection<TObjToWrite, TReadedObj> dbConnect;
    private final HashMap<ClassInfo, ArrayList<PersistentListener<TReadedObj>>> classInfo2ListenerListMap;

    /**
     *
     * @param aDbConnection
     */
    public LimbusBasicPersistenceObjectManagerImpl(DbAccessConnection<TObjToWrite, TReadedObj> aDbConnection)
    {
        super();
        this.dbConnect = aDbConnection;
        this.classInfo2QueryCache = new HashMap<>();
        this.classInfo2ListenerListMap = new HashMap<>(100);

        this.dbConnect.setObjectTransformator(createObjectCreator());

    }

    protected abstract ObjectTransformator<TObjToWrite, TReadedObj> createObjectCreator();

    protected abstract KeyGenerator<TReadedObj> createKeyGenerator();

    protected TReadedObj getReadedObject(TObjToWrite aObjToWrite, ClassInfo aInfo) throws ObjectCreationException
    {
        TypedValueObject valueObj = dbConnect.getObjectTransformator().getValueObjectFor(aInfo, aObjToWrite);
        TReadedObj readedObj = dbConnect.getObjectTransformator().createObjectFor(valueObj);
        return readedObj;
    }

    @Override
    public TReadedObj createNewObject(ClassInfo aInfo) throws ObjectCreationException
    {
        return this.dbConnect.getObjectTransformator().createNewObjectFor(aInfo);
    }

    @Override
    public TReadedObj insertObject(TObjToWrite objToInsert, ClassInfo aInfo) throws JdyPersistentException
    {
        QueryCache<TReadedObj> tmpCache = getQueryCacheForInfo(aInfo);

        TReadedObj newValueObj = dbConnect.insertObjectInDb(objToInsert, aInfo);
        tmpCache.addNewOject(newValueObj);
        firePersistentStateChanged(aInfo, newValueObj, PersistentObjectReader.ModifierType.OBJECT_CREATED);
        return newValueObj;
    }

    @Override
    public void updateObject(TObjToWrite objToUpdate, ClassInfo aInfo) throws JdyPersistentException
    {
        dbConnect.updateObjectToDb(objToUpdate, aInfo);
        try
        {
            TReadedObj updateValueObj = getReadedObject(objToUpdate, aInfo);
            firePersistentStateChanged(aInfo, updateValueObj, PersistentObjectReader.ModifierType.OBJECT_MODIFIED);
        } catch (ObjectCreationException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public void refreshObject(TObjToWrite objToRefresh, ClassInfo aInfo) throws JdyPersistentException
    {
        ValueObject valueObj = dbConnect.getObjectTransformator().getValueObjectFor(aInfo, objToRefresh);
        loadObjectsFromDb(FilterUtil.createSearchEqualObjectFilter(aInfo, valueObj));
    }

    @Override
    public void deleteObject(TObjToWrite objToDelete, ClassInfo aInfo) throws JdyPersistentException
    {
        QueryCache<TReadedObj> tmpQueryCache = getQueryCacheForInfo(aInfo);
        dbConnect.deleteObjectInDb(objToDelete, aInfo);

        try
        {
            TReadedObj readedObj = getReadedObject(objToDelete, aInfo);
            tmpQueryCache.removeDeletedOject(readedObj);
            firePersistentStateChanged(aInfo, readedObj, PersistentObjectReader.ModifierType.OBJECT_DELETED);
        } catch (ObjectCreationException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public List<PersistentOperationResult<TObjToWrite, TReadedObj>> persistObjects(BulkPersistentOperation<TObjToWrite> bulkOperation) throws JdyPersistentException
    {
        startTransaction();
        List<PersistentOperationResult<TObjToWrite, TReadedObj>> resultObjects = new ArrayList<PersistentOperationResult<TObjToWrite, TReadedObj>>();
        List<PersistentEvent<TReadedObj>> events = new ArrayList<>();
        Map<TReadedObj, ClassInfo> deletedObjects = new HashMap<>();
        Map<TReadedObj, ClassInfo> insertedObj2ClassInfo = new HashMap<>();
        Map<TObjToWrite, TReadedObj> insertedObj2CreatedObj = new HashMap<>();

        try
        {
            for (PersistentOperation<TObjToWrite> objToPersist : bulkOperation.getObjectsToPersist())
            {
                if (PersistentOperation.Operation.DELETE.equals(objToPersist.getOperation()))
                {

                    dbConnect.deleteObjectInDb(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
                    TReadedObj readedObj = getReadedObject(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
                    deletedObjects.put(readedObj, objToPersist.getClassInfo());
                    addEvent(events, objToPersist, readedObj, PersistentObjectReader.ModifierType.OBJECT_DELETED);

                } else if (PersistentOperation.Operation.INSERT.equals(objToPersist.getOperation()))
                {

                    resolveCreatedObjReferences(objToPersist.getClassInfo(), objToPersist.getObjectToPersist(), insertedObj2CreatedObj);
                    TReadedObj newValueObj = dbConnect.insertObjectInDb(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
                    insertedObj2CreatedObj.put(objToPersist.getObjectToPersist(), newValueObj);

                    addEvent(events, objToPersist, newValueObj, PersistentObjectReader.ModifierType.OBJECT_CREATED);
                    insertedObj2ClassInfo.put(newValueObj, objToPersist.getClassInfo());
                    resultObjects.add(new DefaultPersistentOperationResult<>(newValueObj, objToPersist));
                } else if (PersistentOperation.Operation.UPDATE.equals(objToPersist.getOperation()))
                {

                    resolveCreatedObjReferences(objToPersist.getClassInfo(), objToPersist.getObjectToPersist(), insertedObj2CreatedObj);
                    dbConnect.updateObjectToDb(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
                    TReadedObj updateValueObj = getReadedObject(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
                    addEvent(events, objToPersist, updateValueObj, PersistentObjectReader.ModifierType.OBJECT_MODIFIED);
                }
            }
        } catch (JdyPersistentException ex)
        {
            rollbackTransaction();
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } catch (ObjectCreationException ex)
        {
            rollbackTransaction();
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } catch (Throwable ex)
        {
            rollbackTransaction();
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }
        commitTransaction();

        // update caches
        insertedObj2ClassInfo.entrySet().stream().forEach((insertedObj) ->
        {
            QueryCache<TReadedObj> tmpCache = getQueryCacheForInfo(insertedObj.getValue());
            tmpCache.addNewOject(insertedObj.getKey());
        });
        deletedObjects.entrySet().stream().forEach((deletedObj) ->
        {
            QueryCache<TReadedObj> tmpCache = getQueryCacheForInfo(deletedObj.getValue());
            tmpCache.removeDeletedOject(deletedObj.getKey());
        });

        // fire events
        events.stream().forEach((persistentEvent) ->
        {
            firePersistentEvent(persistentEvent);
        });

        return resultObjects;
    }

    private void addEvent(List<PersistentEvent<TReadedObj>> events, PersistentOperation<TObjToWrite> objToPersist, TReadedObj readedObj, de.jdynameta.base.model.PersistentObjectReader.ModifierType aModifierType)
    {
        events.add(new PersistentEvent<>(this, objToPersist.getClassInfo(), objToPersist.getClassInfo(), readedObj, aModifierType));

    }

    private void resolveCreatedObjReferences(ClassInfo classInfo, TObjToWrite objectToPersist, Map<TObjToWrite, TReadedObj> insertedObj2CreatedObj)
    {
        if (objectToPersist instanceof ChangeableValueObject)
        {

            ChangeableValueObject editObj = (ChangeableValueObject) objectToPersist;
            for (AttributeInfo attrInfo : classInfo.getAttributeInfoIterator())
            {
                if (attrInfo instanceof ObjectReferenceAttributeInfo)
                {
                    Object refObj = editObj.getValue(attrInfo);
                    TReadedObj createdobj = insertedObj2CreatedObj.get(refObj);
                    if (createdobj != null)
                    {
                        editObj.setValue(attrInfo, createdobj);
                    }
                }
            }
        }
    }

    protected void commitTransaction() throws JdyPersistentException
    {
        getDbConnect().commitTransaction();

    }

    protected void rollbackTransaction() throws JdyPersistentException
    {
        getDbConnect().rollbackTransaction();
    }

    protected void startTransaction() throws JdyPersistentException
    {
        getDbConnect().startTransaction();
    }

    @Override
    public ObjectList<TReadedObj> loadObjectsFromDb(ClassInfoQuery aFilter) throws JdyPersistentException
    {
        QueryCache<TReadedObj> tmpQueryCache = getQueryCacheForInfo(aFilter.getResultInfo());

        ObjectList<TReadedObj> readedObjColl;

        if (tmpQueryCache.areAllObjectsInCache(aFilter))
        {

            readedObjColl = tmpQueryCache.getObjectsForFilter(aFilter);
        } else
        {
            readedObjColl = dbConnect.loadValuesFromDb(aFilter);
            tmpQueryCache.insertObjectsForFilter(aFilter, readedObjColl);
        }

        ChangeableObjectList<TReadedObj> result = new ChangeableObjectList<>();

        for (Iterator<TReadedObj> objIter = readedObjColl.iterator(); objIter.hasNext();)
        {
            result.addObject(objIter.next());
        }
        return result;
    }

    protected QueryCache<TReadedObj> getQueryCacheForInfo(ClassInfo aInfo)
    {
        QueryCache<TReadedObj> result;

        if (classInfo2QueryCache.containsKey(aInfo))
        {
            result = classInfo2QueryCache.get(aInfo);
        } else
        {
            result = createQueryCacheForClassInfo(aInfo);
            classInfo2QueryCache.put(aInfo, result);
        }

        return result;
    }

    protected QueryCache< TReadedObj> createQueryCacheForClassInfo(final ClassInfo aInfo)
    {
        QueryCache<TReadedObj> newCache = new EmptyQueryCacheImpl<>();
        newCache.setKeyGenerator(createKeyGenerator());
        return newCache;
    }

    @Override
    public void addListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener)
    {
        ArrayList<PersistentListener<TReadedObj>> listenerList = this.classInfo2ListenerListMap.get(aClassInfo);
        if (listenerList == null)
        {
            listenerList = new ArrayList<>();
            this.classInfo2ListenerListMap.put(aClassInfo, listenerList);
        }

        listenerList.add(aListener);
    }

    @Override
    public void removeListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener)
    {
        ArrayList<PersistentListener<TReadedObj>> listenerList = this.classInfo2ListenerListMap.get(aClassInfo);
        if (listenerList != null)
        {
            listenerList.remove(aListener);
        }
    }

    private void firePersistentStateChanged(ClassInfo aClassInfo, TReadedObj aChangedObject, PersistentObjectReader.ModifierType aState)
    {
        PersistentEvent<TReadedObj> newEvent = new PersistentEvent<>(this, aClassInfo, aClassInfo, aChangedObject, aState);
        firePersistentEvent(newEvent);
    }

    private void firePersistentEvent(PersistentEvent<TReadedObj> newEvent)
    {
        ArrayList<PersistentListener<TReadedObj>> listenerList = this.classInfo2ListenerListMap.get(newEvent.getChangedClass());
        if (listenerList != null)
        {

            listenerList.stream().forEach((curListener) ->
            {
                curListener.persistentStateChanged(newEvent);
            });
        }

        ArrayList<PersistentListener<TReadedObj>> commonListenerList = this.classInfo2ListenerListMap.get(null);
        if (commonListenerList != null)
        {

            commonListenerList.stream().forEach((curListener) ->
            {
                curListener.persistentStateChanged(newEvent);
            });
        }
    }

    protected final DbAccessConnection<TObjToWrite, TReadedObj> getDbConnect()
    {
        return this.dbConnect;
    }

    public void closeConnection() throws JdyPersistentException
    {
        this.dbConnect.closeConnection();
    }

}
