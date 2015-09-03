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
package de.jdynameta.metainfoview.metainfo;


import javax.swing.AbstractListModel;

import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.objectlist.ProxyResolveException;


/**
 * 
 * @author Rainer Schneider
 * @version 04/2002
 */
@SuppressWarnings("serial")
public class SwingListModelMapping<TListType> extends AbstractListModel 
{
	/** the list model which holds the object */
	private ObjectListModel<TListType> listModel; 
	private ObjectListModelListener<TListType> listModelListener;

	/** Creates an SwingListmodel for the specified ListModel
	 */
	public SwingListModelMapping(ObjectListModel<TListType> aObjModel) throws ProxyResolveException 
	{
		
		setListModel(aObjModel);
	}
	
	/**
	 * Set a new List Model
	 */
	public void setListModel(ObjectListModel<TListType> aListModel) throws ProxyResolveException 
	{
		
		
		if ( listModel != null && listModelListener != null) {
			listModel.removeObjectListModelListener(listModelListener);
		}

		if( aListModel != null) {
			// add this object as listerner to the listModel and do the necessary updates		
			listModelListener = new ObjectListModelListener<TListType>() {
				
				public void contentsChanged(ObjectListModelEvent<TListType> aEvent)
				{
					fireContentsChanged(this, aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}

				public void intervalUpdated(ObjectListModelEvent<TListType> aEvent)
				{
					fireContentsChanged(this, aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}
				
				public void intervalAdded(ObjectListModelEvent<TListType> aEvent)
				{
					fireIntervalAdded(this, aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}

				public void intervalRemoved(ObjectListModelEvent<TListType> aEvent)
				{
					removedElement(aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}
				
			};

			aListModel.addObjectListModelListener(listModelListener); 		
			listModel = aListModel;
			fireIntervalAdded(this, 0, listModel.size()-1); 

		} else {
			ObjectListModel<TListType> oldModel = listModel;
			listModelListener = null;
			listModel = aListModel;
			if ( oldModel != null) {
				fireIntervalRemoved(this,0, oldModel.size());
			}
		}
	}

	/**
	 * @see SwingListModelMapping#removedElement(Object, int)
	 * fire Selection after remove because the List didn't do it
	 */
	protected void removedElement( int aBeginIndex, int aEndIndex) 
	{
		fireIntervalRemoved(this, aBeginIndex, aEndIndex);	
	}


	public int getIndexOf(TListType anObject) 
	{
		try
		{
			return (listModel == null) ? -1 : listModel.indexOf(anObject);
		} catch (ProxyResolveException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public TListType getElementAt(int index) 
	{
		try
		{
			return (listModel == null || index >= listModel.size()    ) ? null : listModel.get(index);
		} catch (ProxyResolveException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSize() 
	{
		try
		{
			return (listModel == null) ? 0 : listModel.size();
		} catch (ProxyResolveException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

}
