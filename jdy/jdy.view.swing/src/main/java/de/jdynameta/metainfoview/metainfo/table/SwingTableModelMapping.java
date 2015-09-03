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
package de.jdynameta.metainfoview.metainfo.table;


import javax.swing.table.AbstractTableModel;

import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;


/**	** @author Rainer Schneider
	* @version 04/2002
	*/
@SuppressWarnings("serial")
public abstract class SwingTableModelMapping<TListObject> extends AbstractTableModel
{

	/** the list model which holds the object */
	private ObjectListModel<TListObject> listModel; 
	private ObjectListModelListener<TListObject> listModelListener;


	/** Creates an SwingListmodel for the specified ListModel
	 */
	public SwingTableModelMapping() {
		super();
	}
	
	/** Creates an SwingListmodel for the specified ListModel
	 */
	public SwingTableModelMapping(ObjectListModel<TListObject> aListModel) {
		
		setListModel(aListModel);
	}


	/**	* Retrieves the current list model.
	*/
	public ObjectList<TListObject> getListModel()
	{
		return listModel;
	}	


	/**
	 * Set a new List Model
	 */
	public void setListModel(ObjectListModel<TListObject> aListModel) {
		
		
		if ( listModel != null && listModelListener != null) {
			listModel.removeObjectListModelListener(listModelListener);
		}

		if( aListModel != null) {
			// add this object as listener to the listModel and do the necessary updates		
			listModelListener = new ObjectListModelListener<TListObject>() {

				public void contentsChanged(ObjectListModelEvent<TListObject> aEvent)
				{
					fireTableStructureChanged();
				}
				
				public void intervalUpdated(ObjectListModelEvent<TListObject> aEvent)
				{
					fireTableRowsUpdated(aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}
				
				public void intervalAdded(ObjectListModelEvent<TListObject> aEvent)
				{
					fireTableRowsInserted( aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}
				
				public void intervalRemoved(ObjectListModelEvent<TListObject> aEvent)
				{
					fireTableRowsDeleted(aEvent.getLowerIndex(), aEvent.getUpperIndex());
				}

			};
			aListModel.addObjectListModelListener(listModelListener); 		
			listModel = aListModel;
			modelStructureChanged(); 

		} else {
			ObjectListModel<TListObject> oldModel = listModel;
			listModelListener = null;
			listModel = aListModel;
			if ( oldModel != null) {
				fireTableDataChanged();
			}
		}
	}

	/**
	 * Called after the structur of the _listModel ahs been changed
	 */
	protected void modelStructureChanged() {

		if ( listModel != null) {		
			fireTableDataChanged();
		}
	}


	protected void elementChanged(int beginIndex, int endIndex) 
	{
		fireTableRowsUpdated(beginIndex, endIndex);
	}
	


	/** 
	 * Called after an element has been removed from the _listModel
	 * fires the element remove event
	 * @param anObject the removed Object
	 * @param removeIndex postion where the object was removed
	 */
	protected void removedElement(int beginIndex, int endIndex) {
		
		fireTableRowsDeleted( beginIndex, endIndex );
	}

	public int getRowCount() 
	{
		return (listModel == null) ? 0 : listModel.size();
	}
		
	public TListObject getRowAt(int rowIndex) {

		return listModel.get(rowIndex);	
	}

	public Object getValueAt(int rowIndex, int columnIndex)	{
			
		Object rowObject  = listModel.get(rowIndex);	
			
		return getColumnValue(rowObject, columnIndex);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		Object rowObject  = listModel.get(rowIndex);	
		setColumnValue(rowObject,aValue,  columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex,int columnIndex) 
	{
		
		return false;	
	}	
		

	public int getIndexOf(TListObject anObject) 
	{
		return (listModel == null) ? -1 : listModel.indexOf(anObject);
	}

	public abstract int getColumnCount(); 
		
	public abstract Object getColumnValue(Object rowObject, int columnIndex); 

	public void setColumnValue(Object rowObject, Object aValue, int columnIndex)
	{
	}


}
