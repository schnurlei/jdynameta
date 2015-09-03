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
package de.jdynameta.persistence.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.model.PersistentObjectReader.PersistentListener;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.persistence.manager.PersistentObjectManager;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class PersistentAssociationListModel< TListObj extends PersistentValueObject> implements PersistentObjectList<TListObj>,Serializable
{
	private final List<TListObj> allObjects;
	protected final ArrayList<ObjectListModelListener<TListObj>> listenerList;
	private final List<TListObj> deletedObjects;
	private PersistentObjectReader.PersistentListener<TListObj> persistentListener;
	
	/**
	 * 
	 * @param aAssocInfo
	 * @param aWrappedList
	 * @param aParent
	 * @param aPersistentReader if not null this list listens for PersistentEvent events of the PersistentObjectReader
	 */
	public PersistentAssociationListModel( final AssociationInfo aAssocInfo
											, List<TListObj> aWrappedList
										, final TListObj aParent, PersistentObjectReader<TListObj> aPersistentReader ) 
	{
		super();

		this.listenerList = new ArrayList<ObjectListModelListener<TListObj>>();
		this.allObjects = aWrappedList;
		this.deletedObjects = new ArrayList<TListObj>();

		if( aPersistentReader != null && aParent != null) {
			this.persistentListener =  createPersistentListener(aAssocInfo, aParent);
			aPersistentReader.addListener(aAssocInfo.getDetailClass(), persistentListener);
		} else {
			this.persistentListener = null;
		}
	}

	private PersistentListener<TListObj> createPersistentListener(final AssociationInfo aAssocInfo, final TListObj aParent)
	{
		return  new PersistentObjectReader.PersistentListener<TListObj>()
		{
			public void persistentStateChanged(PersistentObjectManager.PersistentEvent<TListObj> aEvent) 
			{
				switch (aEvent.getState()) {
					case OBJECT_CREATED:
						
						Object changedParent = aEvent.getChangedObject().getValue(aAssocInfo.getMasterClassReference());
						if( aParent.equals(changedParent) ){
							allObjects.add(aEvent.getChangedObject());
							fireIntervalAdded(PersistentAssociationListModel.this,allObjects.size()-1,allObjects.size()-1);
						}
						break;
					case OBJECT_DELETED :
						int index = allObjects.indexOf(aEvent.getChangedObject());
						if (index >= 0) {
							allObjects.remove(aEvent.getChangedObject());
							fireIntervalRemoved(PersistentAssociationListModel.this,index,index, Collections.singletonList(aEvent.getChangedObject()));
						}
						break;
					case OBJECT_MODIFIED :
						int modIdx = allObjects.indexOf(aEvent.getChangedObject());
						if (modIdx >= 0) {
							fireIntervalUpdated(PersistentAssociationListModel.this,modIdx,modIdx);
						}
						break;
				} aEvent.getState();
			}
		};
	}
	

	public void addObject(TListObj newObj)
	{
		this.allObjects.add(newObj);
		this.fireIntervalAdded(this, this.allObjects.size()-1, this.allObjects.size()-1);
	}

	public void removeObject(TListObj oldObj)
	{
		int index = this.allObjects.indexOf(oldObj);
		this.allObjects.remove(oldObj);
		this.deletedObjects.add(oldObj);
		this.fireIntervalRemoved(this, index, index, Collections.singletonList(oldObj));
	}

	public void updateObject(TListObj objToUpdate) 
	{
		int index = this.allObjects.indexOf(objToUpdate);
		this.fireIntervalUpdated(this, index, index);
	};
	
	
	public List<TListObj> getDeletedObjects()
	{
		return this.deletedObjects;
	}	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#get(int)
	 */
	public TListObj get(int index)
	{
		return this.allObjects.get(index);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#iterator()
	 */
	public Iterator<TListObj> iterator()
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
	public int indexOf(PersistentValueObject anObject)
	{
		return this.allObjects.indexOf(anObject);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#addObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void addObjectListModelListener(ObjectListModelListener<TListObj> aListener)
	{
		listenerList.add( aListener);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void removeObjectListModelListener(ObjectListModelListener<TListObj>  aListener)
	{
		listenerList.remove(aListener);
	}

	protected void fireIntervalAdded(ObjectListModel<TListObj>  source, int index0, int index1)
	{
		ObjectListModelEvent<TListObj>  event = new ObjectListModelEvent<TListObj> (source, index0, index1);
		for (ObjectListModelListener<TListObj> listObj : listenerList)
		{
			listObj.intervalAdded(event);
		}
	}

	protected void fireIntervalRemoved(ObjectListModel<TListObj>  source, int index0, int index1, List<TListObj> aList)
	{
		ObjectListModelEvent<TListObj>  event = new ObjectListModelEvent<TListObj> (source, index0, index1, aList);
		for (ObjectListModelListener<TListObj> listObj : listenerList)
		{
			listObj.intervalRemoved(event);
		}
	}

	protected void fireIntervalUpdated(ObjectListModel<TListObj>  source, int index0, int index1)
	{
		ObjectListModelEvent<TListObj>  event = new ObjectListModelEvent<TListObj> (source, index0, index1);
		for (ObjectListModelListener<TListObj> listObj : listenerList)
		{
			listObj.intervalUpdated(event);
		}
	}
	
	protected void fireContentsChanged(ObjectListModel<TListObj>  source, int index0, int index1)
	{
		ObjectListModelEvent<TListObj>  event = new ObjectListModelEvent<TListObj> (source, index0, index1);
		for (ObjectListModelListener<TListObj> listObj : listenerList)
		{
			listObj.contentsChanged(event);
		}
	}
	
}
