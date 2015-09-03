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


import java.beans.PropertyChangeListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.SwingPropertyChangeSupport;

import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;


/**
 * Extends the superclass by a selection model.
 * Listener for a selection change can be added 
 * and the selected Object(s) could be retrieved.
 *  
 * <p>Use with JList:
 * 	<pre>
 * 	CollectionModel collectionToShow
 * 	SwingSelectedListModelMapping selectedTermModel = new SwingSelectedListModelMapping(collectionToShow);				
 *	JList list = new JList(selectedTermModel);
 *	list.setSelectionModel(selectedTermModel.getSelectionModel());
 *	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 *	</pre>
 * 
  * @author Rainer Schneider
 * @version 04/2002
 */
public class SwingSelectedListModelMapping	extends SwingListModelMapping
	implements SwingSelectedModel
{
	private ListSelectionModel selectionModel;
	private ListSelectionListener selectionListener;
	
    private SwingPropertyChangeSupport selectionChangeSupport = null;
	

	/**
	 * @see SwingListModelMapping#SwingListModelMapping(ListModel)
	 */
	public SwingSelectedListModelMapping(ObjectListModel aListModel) throws ProxyResolveException
	{
		super(aListModel);

		setSelectionModel(new DefaultListSelectionModel());
		selectionChangeSupport = new SwingPropertyChangeSupport(this);

	}


	public void addSelectionChangeListener(PropertyChangeListener selectionChangeListener)
	{
		selectionChangeSupport.addPropertyChangeListener(SELECTION_CHANGE_PROPERTY, selectionChangeListener);
	}


	public void removeSelectionChangeListener(PropertyChangeListener selectionChangeListener)
	{
		selectionChangeSupport.removePropertyChangeListener(SELECTION_CHANGE_PROPERTY, selectionChangeListener);	
	}

	/**
	 * Called when the Selelction changes
	 */
	protected void fireSelectionChange( Object oldValue, Object newValue)
	{
		if (selectionChangeSupport != null) {
			selectionChangeSupport.firePropertyChange(SELECTION_CHANGE_PROPERTY, oldValue, newValue);
		}
	}


	/**
	 * Get the Selected Object in the listModel
	 */
	public Object getSelectedObject() {
		
		int i = selectionModel.getAnchorSelectionIndex();	

		// the element where the anchor selection is on
		// could be not selected so we then return the min selection index
		if( !selectionModel.isSelectedIndex(i)) {
			i = selectionModel.getMinSelectionIndex();	
		}
		
        return (i == -1) || (i >= getSize()) ? null : getElementAt(i);
	}

	/**
	 * Extend the selected Object by the given one.
	 */
	public void extendSelectionBy(Object anObject)
	{
		int index = getIndexOf(anObject);
		if ( index >= 0) {
			selectionModel.addSelectionInterval(index, index);		
		}
	}

   /**
     * Clears the selection - after calling this method.
     */
    public void clearSelection()
    {
    	if ( getSelectionModel() != null) {
	        getSelectionModel().clearSelection();
    	}
    }

   /**
     * Returns an array of the values for the selected cells.
     * The returned values are sorted in increasing index order.
     * 
     * @return the selected values
     */
    public Object[] getAllSelectedObjects()
    {
		Object[] resultArray;

        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            resultArray =  new Object[0];
        } else {

		    Object[] tmpObjectArray = new Object[1+ (iMax - iMin)];
		    int n = 0;
		    for(int i = iMin; i <= iMax; i++) {
		        if (selectionModel.isSelectedIndex(i)) {
		            tmpObjectArray[n++] = getElementAt(i);
		        }
		    }
		    resultArray = new Object[n];
		    System.arraycopy(tmpObjectArray, 0, resultArray, 0, n);
        }
        
        return resultArray;
    }

	/**
	 * Get the current ListSelectionModel
	 */
	public ListSelectionModel getSelectionModel() {
	
		return selectionModel;	
	}
	
	/**
	 * Set the used ListSelectionModel
	 */
	public void setSelectionModel ( ListSelectionModel newSelectionModel) {
		
		if ( selectionModel != null && selectionListener != null) {
			selectionModel.removeListSelectionListener(selectionListener);
		}	
		
		selectionModel = newSelectionModel;
		selectionListener = createListSelectionListener();
		selectionModel.addListSelectionListener(selectionListener);		
	}
	
	/**
	 * Create a Listener for the ListSelectionModel which calls 
	 * #fireSelectionChange when the value of the selection changes.
	 */
	protected ListSelectionListener createListSelectionListener() 
	{
		return new ListSelectionListener() 
		{
			public void valueChanged(ListSelectionEvent aEvent) 
			{
				fireSelectionChange(null,null);
			}
		};	
	}

	/**
	 * @see SwingListModelMapping#removedElement(Object, int)
	 * fire Selection after remove because the List didn't do it
	 */
	@Override
	protected void removedElement( int aBeginIndex, int aEndIndex) 
	{
		super.removedElement(aBeginIndex, aEndIndex);
		// fire Selection after remove because the List didn't do it
		selectionModel.clearSelection();
		fireSelectionChange(null,null);
	}

}
