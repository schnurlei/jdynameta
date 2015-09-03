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
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.objectlist.ObjectFilter;

/**
 * List Model that reads its objects by a Query
 * It listens to the Persistence Manager for the add, remove and update of Object
 * when the flag listenToChanges is set in the constructor
 * Translate a PersistentEvent into ObjectListModelEvent 
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class PersistentQueryObjectListModel<TReadedObject> 
	implements ObjectListModel<TReadedObject>, Serializable
{
	private PersistentObjectManager.PersistentListener<TReadedObject> persistentListener;
	private final List<TReadedObject> allObjects;
	protected final ArrayList<ObjectListModelListener<TReadedObject>> listenerList;
	private ObjectList<TReadedObject> wrappedObjectList;
	private final ObjectFilter<TReadedObject> filter;
	
	/**
	 * 
	 */
	public PersistentQueryObjectListModel(PersistentObjectReader<TReadedObject> aPersistenceManager
										, final ClassInfoQuery aQuery, boolean listenToChanges) throws JdyPersistentException, ProxyResolveException
	{
		super();

		this.listenerList = new ArrayList<ObjectListModelListener<TReadedObject>>();

		ObjectList<TReadedObject> valueList = aPersistenceManager.loadObjectsFromDb(aQuery);
//		ObjectList<TReadedObject> valueList = new DefaultObjectList<TReadedObject>();
		this.allObjects = new ArrayList<TReadedObject>(valueList.size());
		setWrappedObjectList(valueList);
		
		this.filter = new ObjectFilter<TReadedObject>()
		{
			public boolean accept(TReadedObject object) 
			{
				try {
					return aQuery.matchesObject(((ValueObject)object));
				} catch (JdyPersistentException e) {
					e.printStackTrace();
				}
				return false;
			}
		};

		if( listenToChanges) {
			
			this.persistentListener = new PersistentObjectManager.PersistentListener<TReadedObject>()
			{
				public void persistentStateChanged(PersistentObjectManager.PersistentEvent<TReadedObject>  aEvent) 
				{
					switch (aEvent.getState()) {
						case OBJECT_CREATED :
							if(filter.accept(aEvent.getChangedObject())){
								allObjects.add( aEvent.getChangedObject());
								fireIntervalAdded(PersistentQueryObjectListModel.this,allObjects.size(),allObjects.size());
							}
							break;
						case OBJECT_DELETED :
							int index = allObjects.indexOf(aEvent.getChangedObject());
							if (index >= 0) {
								allObjects.remove(aEvent.getChangedObject());
								fireIntervalRemoved(PersistentQueryObjectListModel.this,index,index);
							}
							break;
						case OBJECT_MODIFIED :
							int modIdx = allObjects.indexOf(aEvent.getChangedObject());
							if (modIdx >= 0) {
								fireIntervalUpdated(PersistentQueryObjectListModel.this,modIdx,modIdx);
							}
							break;
					} aEvent.getState();
				}
			};
			
			aPersistenceManager.addListener(aQuery.getResultInfo(), persistentListener);
		}
	}

	/**
	 * @return
	 */
	private void setWrappedObjectList(ObjectList<TReadedObject> aObjectList) throws ProxyResolveException
	{
		this.wrappedObjectList = aObjectList;
		
		refresh();
	}


	/**	
	 * Refreshes the collection.
	 */
	private void refresh() throws ProxyResolveException
	{
		this.allObjects.clear();
		this.fireIntervalRemoved(this, 0, this.allObjects.size()-1);
		
		if ( this.wrappedObjectList != null) 
		{
			for(Iterator<TReadedObject> iterator = this.wrappedObjectList.iterator(); iterator.hasNext();)
			{
				TReadedObject object = iterator.next();
				if( this.filter == null || this.filter.accept(object))
				{
					this.addToAllObjects(object);
				}
			}
		}

		this.fireIntervalAdded(this, 0, this.allObjects.size()-1);
	}

	/**
	 * Sychronize access to allObjects
	 * @param anObject
	 */
	private synchronized void addToAllObjects(TReadedObject anObject)
	{
		this.allObjects.add(anObject);
	}
	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#get(int)
	 */
	public TReadedObject get(int index)
	{
		return this.allObjects.get(index);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#iterator()
	 */
	public Iterator<TReadedObject> iterator()
	{
		return this.allObjects.iterator();
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#size()
	 */
	public int size()
	{
		return this.allObjects.size();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#indexOf(java.lang.Object)
	 */
	public int indexOf(Object anObject)
	{
		return this.allObjects.indexOf(anObject);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#addObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void addObjectListModelListener(ObjectListModelListener<TReadedObject> aListener)
	{
		listenerList.add( aListener);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void removeObjectListModelListener(ObjectListModelListener<TReadedObject> aListener)
	{
		listenerList.remove( aListener);
	}

	protected void fireIntervalAdded(ObjectListModel<TReadedObject> source, int index0, int index1)
	{
		ObjectListModelEvent<TReadedObject> event = new ObjectListModelEvent<TReadedObject>(source, index0, index1);
		for (ObjectListModelListener<TReadedObject> listener : listenerList)
		{
			listener.intervalAdded(event);
		}
	}

	protected void fireIntervalRemoved(ObjectListModel<TReadedObject> source, int index0, int index1)
	{
		ObjectListModelEvent<TReadedObject> event = new ObjectListModelEvent<TReadedObject>(source, index0, index1);
		for (ObjectListModelListener<TReadedObject> listener : listenerList)
		{
			listener.intervalRemoved(event);
		}
	}

	protected void fireIntervalUpdated(ObjectListModel<TReadedObject> source, int index0, int index1)
	{
		ObjectListModelEvent<TReadedObject> event = new ObjectListModelEvent<TReadedObject>(source, index0, index1);
		for (ObjectListModelListener<TReadedObject> listener : listenerList)
		{
			listener.intervalUpdated(event);
		}
	}
	
}
