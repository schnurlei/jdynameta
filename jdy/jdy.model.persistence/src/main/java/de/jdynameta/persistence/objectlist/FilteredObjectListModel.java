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
package de.jdynameta.persistence.objectlist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.objectlist.ProxyResolveException;

/**
 * @author Rainer
 *
 */
public class FilteredObjectListModel<TListObject> 
	implements ObjectListModel<TListObject> 
{
	private final List<TListObject> allObjects;
	private final ArrayList<ObjectListModelListener> listenerList;
	private final ObjectFilter<TListObject> filter;
	
	/**
	 * 
	 */
	public FilteredObjectListModel(List<TListObject> aListContent) 
	{
		super();

		this.listenerList = new ArrayList<ObjectListModelListener>();
		
		this.allObjects = new ArrayList<TListObject>(aListContent != null ? aListContent.size() : 10);

		setListContent(aListContent);
		
		this.filter = new ObjectFilter<TListObject>()
		{
			public boolean accept(TListObject object) 
			{
				return true;
			}
		};

	}

	/** Retrieves the filter.
	 *
	 * @return	The filter currently applied to the model.
	 */
	public ObjectFilter getFilter()
	{
		return filter;
	}

	/**
	 * @return
	 */
	private void setListContent(List<TListObject> aListContent) throws ProxyResolveException
	{
		this.allObjects.clear();
		this.fireIntervalRemoved(this, 0, this.allObjects.size()-1);
		
		if ( aListContent != null) 
		{
			for(Iterator<TListObject> iterator = aListContent.iterator(); iterator.hasNext();)
			{
				TListObject curObject = iterator.next();
				if( this.filter == null || this.filter.accept(curObject))
				{
					this.allObjects.add(curObject);
				}
			}
		}

		this.fireIntervalAdded(this, 0, this.allObjects.size()-1);
				
	}


	/**
	 * Sychronize access to allObjects
	 * @param anObject
	 */
	protected synchronized void addToListContent(TListObject anObject)
	{
		this.allObjects.add(anObject);
		fireIntervalAdded(this, this.allObjects.size()-1, this.allObjects.size()-1);
	}
	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#get(int)
	 */
	public TListObject get(int index)
	{
		return this.allObjects.get(index);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#iterator()
	 */
	public Iterator<TListObject> iterator()
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
	public void addObjectListModelListener(ObjectListModelListener aListener)
	{
		listenerList.add(aListener);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void removeObjectListModelListener(ObjectListModelListener aListener)
	{
		listenerList.remove(aListener);
	}

	protected void fireIntervalAdded(ObjectListModel source, int index0, int index1)
	{
		ObjectListModelEvent event = new ObjectListModelEvent(source, index0, index1);
		for (ObjectListModelListener listener : listenerList)
		{
			listener.intervalAdded(event);
		}
	}

	protected void fireIntervalRemoved(ObjectListModel source, int index0, int index1)
	{
		ObjectListModelEvent event = new ObjectListModelEvent(source, index0, index1);
		for (ObjectListModelListener listener : listenerList)
		{
			listener.intervalRemoved(event);
		}
	}

}
