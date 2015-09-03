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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.filter.ObjectReferenceEqualExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultObjectReferenceEqualExpression;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.objectlist.FilteredObjectListModel;

/**
 * 
 * Proxy Association List. Delays the Query of the Association objects 
 * until it contents are accessed
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ProxyAssociationListModel<TListObject>
	implements ObjectListModel<TListObject>, Serializable
{
	private final ValueObject masterObject;
	private ObjectListModel<TListObject> allObjects;
	private final PersistentObjectReader<TListObject> objectManager;
	private final AssociationInfo assocInfo;
	private final List<ObjectListModelListener<TListObject>> listenerList;
	
	public ProxyAssociationListModel(PersistentObjectReader<TListObject> aObjectManager
							, AssociationInfo aAssocInfo
							, ValueObject aMasterObject)
	{
		this.listenerList = new ArrayList<ObjectListModelListener<TListObject>>();
		this.objectManager = aObjectManager;
		this.assocInfo = aAssocInfo; 
		this.masterObject = aMasterObject;
	}

	private void resolveProxy() throws ProxyResolveException 
	{
		
		if( this.allObjects == null) 
		{
			try
			{
				ObjectReferenceEqualExpression aResolveExpression = new DefaultObjectReferenceEqualExpression(assocInfo.getMasterClassReference(),masterObject);
				
				DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocInfo.getDetailClass());
				filter.setFilterExpression(aResolveExpression);
				
				this.allObjects = new PersistentQueryObjectListModel<TListObject>(this.objectManager, filter, true);
				
				for (Iterator<ObjectListModelListener<TListObject>> listenerIter = this.listenerList.iterator(); listenerIter.hasNext();)
				{
					ObjectListModelListener<TListObject> curListener = listenerIter.next();
					this.allObjects.addObjectListModelListener(curListener);
				}
			} catch (JdyPersistentException excp)
			{
				this.allObjects = new FilteredObjectListModel<TListObject>(null);
				throw new ProxyResolveException(excp);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see de.comafra.model.value.ObjectList#get(int)
	 */
	public TListObject get(int index) throws ProxyResolveException
	{
		resolveProxy(); 
		return this.allObjects.get(index);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.value.ObjectList#iterator()
	 */
	public Iterator<TListObject> iterator() throws ProxyResolveException
	{
		resolveProxy(); 
		return this.allObjects.iterator();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.value.ObjectList#size()
	 */
	public int size() throws ProxyResolveException
	{
		resolveProxy(); 
		return this.allObjects.size();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectList#indexOf(java.lang.Object)
	 */
	public int indexOf(TListObject anObject) throws ProxyResolveException
	{
		resolveProxy(); 
		return this.allObjects.indexOf(anObject);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#addObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void addObjectListModelListener(ObjectListModelListener<TListObject> aListener)
	{
		if( this.allObjects != null) {
			this.allObjects.addObjectListModelListener(aListener);
		} else {

			listenerList.add(aListener);
		}
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
	 */
	public void removeObjectListModelListener(ObjectListModelListener<TListObject> aListener)
	{
		if( this.allObjects != null) {
			this.allObjects.removeObjectListModelListener(aListener);
		} else {
			listenerList.remove(aListener);
		}
	}
	
}
