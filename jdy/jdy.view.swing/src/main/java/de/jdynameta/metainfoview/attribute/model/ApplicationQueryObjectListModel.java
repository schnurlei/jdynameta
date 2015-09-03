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
package de.jdynameta.metainfoview.attribute.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.objectlist.QueryObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.objectlist.ObjectFilter;

/**
 * List Model that reads its objects by a Query
 * It listens to the Persistence Manager for the add, remove and update of Object
 * Translate a PersistentEvent into ObjectListModelEvent 
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ApplicationQueryObjectListModel<TViewObj extends ValueObject> 
	implements QueryObjectListModel<TViewObj>
{
	private final List<TViewObj> allObjects;
	protected final ArrayList<ObjectListModelListener<TViewObj>> listenerList;
	private ObjectList<TViewObj> wrappedObjectList;
	private final ObjectFilter<TViewObj> filter;
	private ClassInfoQuery query;
	private final PersistentObjectReader<TViewObj> persistenceReader;
	
	/**
	 * 
	 */
	public ApplicationQueryObjectListModel(PersistentObjectReader<TViewObj> aPersistenceReader
										, final ClassInfoQuery aQuery) throws JdyPersistentException
	{
		super();

		this.persistenceReader = aPersistenceReader;
		this.query = aQuery;
		this.listenerList = new ArrayList<ObjectListModelListener<TViewObj>>();
		this.allObjects = new ArrayList<TViewObj>();
		
		this.filter = new ObjectFilter<TViewObj>()
		{
			public boolean accept(TViewObj object) 
			{
				try {
					return (query != null ) ? query.matchesObject(((ValueObject)object)): false;
				} catch (JdyPersistentException e) {
					e.printStackTrace();
				}
				return false;
			}
		};
	}

	public List<TViewObj> getAllObjects() 
	{
		return allObjects;
	}
	
	protected ObjectFilter<TViewObj> getFilter() 
	{
		return filter;
	}
	
	public void setQuery(ClassInfoQuery query) throws JdyPersistentException 
	{
		this.query = query;
		refresh();
	}
	
	/**	
	 * Refreshes the collection.
	 * @throws JdyPersistentException 
	 */
	public void refresh() throws JdyPersistentException
	{
		if( this.allObjects.size() > 0) {
			this.allObjects.clear();
		}
		if ( query != null) {
			ObjectList<TViewObj> valueList = this.persistenceReader.loadObjectsFromDb(query);
			this.wrappedObjectList = valueList;
		} else {
			this.wrappedObjectList = new DefaultObjectList<TViewObj>();
		}

		
 		if ( this.wrappedObjectList != null) 
		{
			for(Iterator<TViewObj> iterator = this.wrappedObjectList.iterator(); iterator.hasNext();)
			{
				TViewObj object = iterator.next();
//				if( this.filter == null || this.filter.accept(object))
//				{
					this.addToAllObjects(object);
//				}
			}
		}

 		if( this.allObjects.size() > 0 ) {
 			this.fireContentsChanged(this, 0, this.allObjects.size()-1);
 		} else {
 			this.fireContentsChanged(this, -1, -1);
 		}
	}

	/**
	 * Sychronize access to allObjects
	 * @param anObject
	 */
	private synchronized void addToAllObjects(TViewObj anObject)
	{
		this.allObjects.add(anObject);	
	}
	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#get(int)
	 */
	public TViewObj get(int index)
	{
		return this.allObjects.get(index);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#iterator()
	 */
	public Iterator<TViewObj> iterator()
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
	public int indexOf(TViewObj anObject)
	{
		return this.allObjects.indexOf(anObject);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#addObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void addObjectListModelListener(ObjectListModelListener<TViewObj> aListener)
	{
		listenerList.add( aListener);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void removeObjectListModelListener(ObjectListModelListener<TViewObj> aListener)
	{
		listenerList.remove( aListener);
	}

	protected void fireIntervalAdded(ObjectListModel<TViewObj> source, int index0, int index1)
	{
		ObjectListModelEvent<TViewObj> event = null;
		for (ObjectListModelListener<TViewObj> curListener : listenerList) {
			
			if (event == null) {
				event = new ObjectListModelEvent<TViewObj>(source, index0, index1);
			}
			curListener.intervalAdded(event);
		}		
	}

	protected void fireIntervalRemoved(ObjectListModel<TViewObj> source, int index0, int index1, List<TViewObj> allChangedObj)
	{
		ObjectListModelEvent<TViewObj> event = null;
		for (ObjectListModelListener<TViewObj> curListener : listenerList) {
			
			if (event == null) {
				event = new ObjectListModelEvent<TViewObj>(source, index0, index1, allChangedObj);
			}
			curListener.intervalRemoved(event);
		}		
	}

	protected void fireIntervalUpdated(ObjectListModel<TViewObj> source, int index0, int index1)
	{
		ObjectListModelEvent<TViewObj> event = null;
		for (ObjectListModelListener<TViewObj> curListener : listenerList) {
			
			if (event == null) {
				event = new ObjectListModelEvent<TViewObj>(source, index0, index1);
			}
			curListener.intervalUpdated(event);
		}		
	}

	protected void fireContentsChanged(ObjectListModel<TViewObj> source, int index0, int index1)
	{
		ObjectListModelEvent<TViewObj> event = null;
		for (ObjectListModelListener<TViewObj> curListener : listenerList) {
			
			if (event == null) {
				event = new ObjectListModelEvent<TViewObj>(source, index0, index1);
			}
			curListener.contentsChanged(event);
		}		
	}
	
}
