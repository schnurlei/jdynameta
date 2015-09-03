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

import javax.swing.ComboBoxModel;
import javax.swing.ListSelectionModel;

import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;

/**
 * Maps a ObjectListModel to a Comboboxmodel,
 * so that a CollectionModel can be used in a ComboBox
 * @author rs
 * @version 24.05.2002
 */
@SuppressWarnings("serial")
public class SwingComboboxModelMapping<TListType> extends SwingSelectedListModelMapping 
	implements ComboBoxModel 
{

	private Object editorObject = null;
	
	/** object that defines a null selection */
	private Object _nullObject; 

	/**
	 * Constructor for SwingComboboxModelMapping.
	 * @param aListModel
	 */
	public SwingComboboxModelMapping(ObjectListModel<TListType> aListModel) throws ProxyResolveException {
		super(aListModel);
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	/** Set the selected item **/
	public void setSelectedItem(Object anItem) {

        if ( (editorObject != null && !editorObject.equals( anItem )) 
        		|| editorObject == null && anItem != null )  {
            editorObject = anItem;
            fireContentsChanged(this, -1, -1);
            
            if ( getIndexOf(anItem) >= 0) {
				this.extendSelectionBy(anItem);
            }
        }

	}

	/** Return the selected item **/
	public Object getSelectedItem() 
	{
		return (editorObject == _nullObject) ? null : editorObject;
	}
	

	@Override
	public Object getSelectedObject()
	{
		return (super.getSelectedObject() == _nullObject) ? null : super.getSelectedObject();
	}

	/**
	 * @see SwingSelectedListModelMapping#clearSelection()
	 */
	@Override
	public void clearSelection() {
		super.clearSelection();
		fireContentsChanged(this, -1, -1);
	}

	@Override
	public int getIndexOf(Object anObject)
	{
		int index;
		
		if ( _nullObject != null) {
			index = (anObject == _nullObject) ? 0 :  super.getIndexOf(anObject)+1;
		} else {
			index = super.getIndexOf(anObject);
		}
		
		return index;
	}

	@Override
	public Object getElementAt(int index) 
	{
		Object result = null;
		
		if (_nullObject == null ) {
			result = super.getElementAt(index);
		} else {
			if(index == 0) {
				result = _nullObject;
			} else {
				result = super.getElementAt(index-1);
			}
		}
		
		return result; 
	}

	@Override
	public int getSize() 
	{
		return super.getSize() + ((_nullObject == null ) ? 0 : 1);
	}

	/**
	 * @return
	 */
	public Object getNullObject()
	{
		return _nullObject;
	}

	/**
	 * @param object
	 */
	public void setNullObject(Object object)
	{
		_nullObject = object;
	}

}
