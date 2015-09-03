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
package de.jdynameta.metainfoview.attribute;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.metainfoview.metainfo.SwingListModelMapping;

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AttributeCombobox<TDisplayedObject> implements AttributeComponent
{
	public static final Object NULL_OBJECT = "_NULL_";
	private final JComboBox objectCombo;
	private final JLabel comboLabel;
	private Object objectToEdit;
	private boolean isEditable;
	private boolean isNullable; 
	private boolean isDirty;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	

	/**
	 * 
	 */
	public AttributeCombobox(ObjectListModel<TDisplayedObject>  aObjectColl) throws ProxyResolveException 
	{
		this(aObjectColl, null , null);
	}


	public AttributeCombobox(ObjectListModel<TDisplayedObject> aObjectColl,
							ListCellRenderer aAttributeRenderer, 
							Comparator<TDisplayedObject> aAttributeComparator) throws ProxyResolveException 
	{
		super();

		objectToEdit = null;
		isEditable = true;
		isNullable = true;
		comboLabel = new JLabel();
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		
		URL url = AttributeCombobox.class.getResource("datum_rs.png");
		if(url != null) {
			ImageIcon image = new ImageIcon(url);
			comboLabel.setIcon(image);
		}
		comboLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		comboLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		comboLabel.setIconTextGap(1);
		
		
		
		if (aAttributeComparator != null) {
			
		} else {
			
		}
		

		objectCombo =  new JComboBox(new ComboboxObjectList<TDisplayedObject>(aObjectColl));
		comboLabel.setLabelFor(objectCombo);
		objectCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				isDirty = true;
				fireInputHasChanged();
			}
		});
		
		
		if (aAttributeRenderer != null) {
			objectCombo.setRenderer( aAttributeRenderer);
		}
		
		updateComponentEditable();
	}


	public void addSelectionChangeListener(ActionListener selectionChangeListener)
	{
		objectCombo.addActionListener(selectionChangeListener);
	}

	public void removeSelectionChangeListener(ActionListener selectionChangeListener)
	{
		objectCombo.removeActionListener(selectionChangeListener);
	}

	protected void setAttributeComparator(Comparator<TDisplayedObject>  aComparator)
	{
	}
	
	protected void setAttributeRenderer(ListCellRenderer aRenderer)
	{
		objectCombo.setRenderer( aRenderer);
	}

	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {
			setAttributeInObject( objectToEdit, (TDisplayedObject) objectCombo.getSelectedItem());
		}
		isDirty = false; 
	}


	public void readValueFromObject(Object anObject)
	{
		objectToEdit = anObject;
		if( anObject != null ) {
			TDisplayedObject aObject = getAttributeFromObject(anObject);
			objectCombo.setSelectedItem(aObject);
		} else {
			objectCombo.setSelectedItem(null);
		}
		
		isDirty = false; 
		updateComponentEditable();
	}


	public Object getSelectedObject()
	{
		return objectCombo.getSelectedItem();
	}

	public void setSelectedObject(Object objectToSelect)
	{
		objectCombo.setSelectedItem(objectToSelect);
	}


	public void addToContainer(Container aContainer, Object constraints)
	{
		aContainer.add(objectCombo, constraints);	
	}

	public void setEditable(boolean editable)
	{
		isEditable = editable;
		updateComponentEditable();
	}

	protected void updateComponentEditable()
	{
		objectCombo.setEnabled(isEditable && objectToEdit != null);
	}

	/* (non-Javadoc)
	 */
	public boolean hasValidInput()
	{
		return (isNullable || getSelectedObject() != null) ;
	}

	/* (non-Javadoc)
	 */
	public boolean isDirty()
	{
		return isDirty;
	}

	/**
	 * @param listener
	 */
	public void addInputChangedListener(InputChangedListener listener)
	{
		inputChangedListenerColl.add(listener);
	}

	public void removeInputChangedListener(InputChangedListener listener) {
		inputChangedListenerColl.remove(listener);
	}
	
	protected void fireInputHasChanged()
	{
		for (InputChangedListener curListener : inputChangedListenerColl) {
			curListener.inputHasChanged();
		}
	}

	protected abstract TDisplayedObject getAttributeFromObject(Object anObject);
	protected abstract void setAttributeInObject(Object anObject, TDisplayedObject selectedAttribute);

	/**
	 * @param b
	 */
	public void setNullable(boolean aNotNullFlag)
	{
		isNullable = aNotNullFlag;

		if( !isNullable) {
			((ComboboxObjectList) objectCombo.getModel()).setNullObject(null);
			comboLabel.setFont(comboLabel.getFont().deriveFont(Font.BOLD) );
		} else {
			((ComboboxObjectList) objectCombo.getModel()).setNullObject(NULL_OBJECT);
			comboLabel.setFont(comboLabel.getFont().deriveFont(Font.PLAIN) );
		}
		
	}

	public void setLabelText(String aLabelText)
	{
		comboLabel.setText(aLabelText +":");
	}

	public void addLabelToContainer(String aLabelText, Container aContainer, Object constraints )
	{
		setLabelText(aLabelText);
		aContainer.add(comboLabel, constraints);	
	}

	/**
	 * @return Returns the objectCombo.
	 */
	protected JComboBox getObjectCombo()
	{
		return this.objectCombo;
	}

	/**
	 * 
	 * @author rainer
	 *
	 * @param <TDisplayedObject>
	 */
	@SuppressWarnings("serial")
	public static class  ComboboxObjectList<TDisplayedObject> extends SwingListModelMapping<TDisplayedObject>
		implements ComboBoxModel
	{
		private TDisplayedObject selectedObject;
		private Object nullObject;

		public ComboboxObjectList(ObjectListModel<TDisplayedObject> aWrappedList) throws ProxyResolveException
		{
			super(aWrappedList);
		}

		public TDisplayedObject getSelectedItem()
		{
			return selectedObject;
		}

		public void setSelectedItem(Object anItem)
		{
			this.selectedObject = (TDisplayedObject) anItem;
		}

		public int getIndexOf(TDisplayedObject anObject) 
		{
			int index;
			
			if ( nullObject != null) {
				index = (anObject == nullObject) ? 0 :  super.getIndexOf(anObject)+1;
			} else {
				index = super.getIndexOf(anObject);
			}
			
			return index;
		}

		@Override
		public TDisplayedObject getElementAt(int index) 
		{
			TDisplayedObject result = null;
			
			if ( nullObject == null ) {
				result = super.getElementAt(index);
			} else {
				if(index == 0) {
					result = null;
				} else {
					result = super.getElementAt(index-1);
				}
			}
			
			return result; 
		}

		@Override
		public int getSize() 
		{
			if( nullObject == null) {
				return super.getSize();
			} else {
				return super.getSize()+1;
			}
		}
		
		public void setNullObject(Object nullObject) 
		{
			this.nullObject = nullObject;
			fireContentsChanged(this, 0, getSize()-1); 
			
		}
		
	}


}
