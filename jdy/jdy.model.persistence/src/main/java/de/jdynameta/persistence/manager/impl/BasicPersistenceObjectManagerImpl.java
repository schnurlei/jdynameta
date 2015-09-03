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
package de.jdynameta.persistence.manager.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

/**
 * @author Rainer
 *
 * @version 04.07.2002
 */
@SuppressWarnings("serial")
public abstract class BasicPersistenceObjectManagerImpl<TObjToWrite, TReadedObj> implements PersistentObjectManager<TObjToWrite, TReadedObj>, Serializable
{
	private final DbAccessConnection<TObjToWrite, TReadedObj> dbConnect;
	private final PersistentNotification<TReadedObj> notification;
	
	/**
	 * 
	 */
	public BasicPersistenceObjectManagerImpl( DbAccessConnection<TObjToWrite, TReadedObj> aDbConnection) 
	{
		super();
		this.dbConnect = aDbConnection;
		this.dbConnect.setObjectTransformator(createObjectCreator());
		this.notification = new PersistentNotification<TReadedObj>();
			
	}
	
	protected abstract ObjectTransformator<TObjToWrite, TReadedObj> createObjectCreator();
	
	protected TReadedObj getReadedObject(TObjToWrite aObjToWrite, ClassInfo aInfo) throws ObjectCreationException
	{
		TypedValueObject valueObj = dbConnect.getObjectTransformator().getValueObjectFor(aInfo, aObjToWrite); 
		TReadedObj readedObj = dbConnect.getObjectTransformator().createObjectFor( valueObj);
		return readedObj;
	}
	
	public TReadedObj createNewObject( ClassInfo aInfo) throws ObjectCreationException
	{
		return this.dbConnect.getObjectTransformator().createNewObjectFor(aInfo);
	}

	public TReadedObj insertObject(TObjToWrite objToInsert, ClassInfo aInfo) throws JdyPersistentException
	{
		TReadedObj newValueObj = dbConnect.insertObjectInDb(objToInsert, aInfo);
		notification.firePersistentStateChanged(aInfo, newValueObj,PersistentObjectReader.ModifierType.OBJECT_CREATED);	
		return newValueObj;
	}
	 
	public void updateObject(TObjToWrite objToUpdate, ClassInfo aInfo) throws JdyPersistentException
	{
		dbConnect.updateObjectToDb(objToUpdate, aInfo);
		try
		{
			TReadedObj updateValueObj = getReadedObject(objToUpdate, aInfo);
			notification.firePersistentStateChanged(aInfo, updateValueObj,PersistentObjectReader.ModifierType.OBJECT_MODIFIED);
		} catch (ObjectCreationException excp)
		{
			throw new JdyPersistentException(excp);
		}		
	}
	
	public void refreshObject(TObjToWrite objToRefresh, ClassInfo aInfo) throws JdyPersistentException 
	{
		ValueObject valueObj = dbConnect.getObjectTransformator().getValueObjectFor(aInfo, objToRefresh); 
		loadObjectsFromDb(FilterUtil.createSearchEqualObjectFilter(aInfo, valueObj));
	}	
	
	public void deleteObject(TObjToWrite objToDelete, ClassInfo aInfo) throws JdyPersistentException
	{
		dbConnect.deleteObjectInDb( objToDelete, aInfo);

		try
		{
			TReadedObj readedObj = getReadedObject(objToDelete, aInfo);
			notification.firePersistentStateChanged(aInfo, readedObj,PersistentObjectReader.ModifierType.OBJECT_DELETED);
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
		List<PersistentEvent<TReadedObj>> events = new ArrayList<PersistentEvent<TReadedObj>>();
		Map<TReadedObj, ClassInfo> deletedObjects = new HashMap<TReadedObj, ClassInfo>();
		Map<TReadedObj, ClassInfo> insertedObj2ClassInfo = new HashMap<TReadedObj, ClassInfo>();
		Map<TObjToWrite, TReadedObj> insertedObj2CreatedObj = new HashMap<TObjToWrite, TReadedObj>();

		try
		{
			for ( PersistentOperation<TObjToWrite> objToPersist : bulkOperation.getObjectsToPersist())
			{
				if( PersistentOperation.Operation.DELETE.equals(objToPersist.getOperation()) ) {

					dbConnect.deleteObjectInDb(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
					TReadedObj readedObj = getReadedObject(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
					deletedObjects.put(readedObj, objToPersist.getClassInfo());
					addEvent(events, objToPersist, readedObj,PersistentObjectReader.ModifierType.OBJECT_DELETED);

				} else if( PersistentOperation.Operation.INSERT.equals(objToPersist.getOperation()) ) {

					resolveCreatedObjReferences(objToPersist.getClassInfo(),objToPersist.getObjectToPersist(), insertedObj2CreatedObj);
					TReadedObj newValueObj = dbConnect.insertObjectInDb(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
					insertedObj2CreatedObj.put(objToPersist.getObjectToPersist(), newValueObj);
					
					addEvent(events, objToPersist, newValueObj,PersistentObjectReader.ModifierType.OBJECT_CREATED);
					insertedObj2ClassInfo.put(newValueObj, objToPersist.getClassInfo());
					resultObjects.add(new DefaultPersistentOperationResult<TObjToWrite, TReadedObj>(newValueObj,objToPersist));
				} else if( PersistentOperation.Operation.UPDATE.equals(objToPersist.getOperation()) ) {

					resolveCreatedObjReferences(objToPersist.getClassInfo(),objToPersist.getObjectToPersist(), insertedObj2CreatedObj);
					dbConnect.updateObjectToDb(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
					TReadedObj updateValueObj = getReadedObject(objToPersist.getObjectToPersist(), objToPersist.getClassInfo());
					addEvent(events, objToPersist, updateValueObj,PersistentObjectReader.ModifierType.OBJECT_MODIFIED);
				} 
			}
		} catch (JdyPersistentException ex)
		{
			rollbackTransaction();
			throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
		} catch (ObjectCreationException ex) {
			rollbackTransaction();
			throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
		}  catch (Throwable ex) {
			rollbackTransaction();
			throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
		}
		commitTransaction();
		
		// fire events
		for (PersistentEvent<TReadedObj> persistentEvent : events) {
			notification.firePersistentEvent(persistentEvent);
		}
		
		return resultObjects;
	}

	private void addEvent(List<PersistentEvent<TReadedObj>> events, PersistentOperation<TObjToWrite> objToPersist, TReadedObj readedObj, de.jdynameta.base.model.PersistentObjectReader.ModifierType aModifierType)
	{
		events.add( new PersistentEvent<TReadedObj>(this, objToPersist.getClassInfo(),objToPersist.getClassInfo(), readedObj,aModifierType));

	}	
	
	private void resolveCreatedObjReferences(ClassInfo classInfo, TObjToWrite objectToPersist,	Map<TObjToWrite, TReadedObj> insertedObj2CreatedObj)
	{
		if( objectToPersist instanceof ChangeableValueObject) {

			ChangeableValueObject editObj = (ChangeableValueObject) objectToPersist;
			for (AttributeInfo attrInfo :classInfo.getAttributeInfoIterator()) 
			{
				if( attrInfo instanceof ObjectReferenceAttributeInfo) 
				{
					Object refObj =  editObj.getValue(attrInfo);
					TReadedObj createdobj = insertedObj2CreatedObj.get(refObj);
					if( createdobj != null) {
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
	
	
	 /**
	  * @author	Rainer Schneider
	  * @param
	  * @TODO
	  */
	public  ObjectList<TReadedObj> loadObjectsFromDb( ClassInfoQuery aFilter) throws JdyPersistentException 
	{
		
		ObjectList<TReadedObj> readedObjColl;
		
		readedObjColl = dbConnect.loadValuesFromDb(aFilter); 
		
		ChangeableObjectList<TReadedObj> result = new ChangeableObjectList<TReadedObj>();
		
		for (Iterator<TReadedObj> objIter = readedObjColl.iterator(); objIter.hasNext();)
		{
			result.addObject(objIter.next());
		}
		return result;
	}
	

	public void addListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener)
	{
		notification.addListener(aClassInfo, aListener);
	}

	public void removeListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener)
	{
		notification.addListener(aClassInfo, aListener);
	}
	


	protected final DbAccessConnection<TObjToWrite, TReadedObj> getDbConnect()
	{
		return this.dbConnect;
	}

	public void closeConnection() throws JdyPersistentException
	{
		this.dbConnect.closeConnection();
	}
	
	/**
	 * 
	 * @author rainer
	 *
	 * @param <TReadedObj>
	 */
	public static class PersistentNotification<TReadedObj>
	{
		private final HashMap<ClassInfo,ArrayList<PersistentListener<TReadedObj>>> classInfo2ListenerListMap;
		
		public PersistentNotification()
		{
			this.classInfo2ListenerListMap = new HashMap<ClassInfo,ArrayList<PersistentListener<TReadedObj>>>(100);
		}
		
		public void addListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener)
		{
			ArrayList<PersistentListener<TReadedObj>> listenerList =  this.classInfo2ListenerListMap.get(aClassInfo);
			if(listenerList == null) {
				listenerList = new ArrayList<PersistentListener<TReadedObj>>();
				this.classInfo2ListenerListMap.put(aClassInfo, listenerList);
			}
			
			listenerList.add(aListener);
		}

		public void removeListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener)
		{
			ArrayList<PersistentListener<TReadedObj>> listenerList =  this.classInfo2ListenerListMap.get(aClassInfo);
			if(listenerList != null) {
				listenerList.remove(aListener);
			}
		}	
		
		private void firePersistentStateChanged(ClassInfo aClassInfo, TReadedObj aChangedObject, PersistentObjectReader.ModifierType aState)
		{
			PersistentEvent<TReadedObj> newEvent = new PersistentEvent<TReadedObj>(this, aClassInfo, aClassInfo, aChangedObject,aState);
			firePersistentEvent(newEvent);
		}
		
		private void firePersistentEvent(PersistentEvent<TReadedObj> newEvent) 
		{
			ArrayList<PersistentListener<TReadedObj>> listenerList =  this.classInfo2ListenerListMap.get(newEvent.getChangedClass());
			if(listenerList != null) {

				for (Iterator<PersistentListener<TReadedObj>> listenerIter = listenerList.iterator(); listenerIter.hasNext();) {

					PersistentListener<TReadedObj> curListener = listenerIter.next();
					curListener.persistentStateChanged(newEvent);				
				}
			}

			ArrayList<PersistentListener<TReadedObj>> commonListenerList =  this.classInfo2ListenerListMap.get(null);
			if(commonListenerList != null) {

				for (Iterator<PersistentListener<TReadedObj>> listenerIter = commonListenerList.iterator(); listenerIter.hasNext();) {

					PersistentListener<TReadedObj> curListener = listenerIter.next();
					curListener.persistentStateChanged(newEvent);				
				}
			}
		}
		
	}
	
}

