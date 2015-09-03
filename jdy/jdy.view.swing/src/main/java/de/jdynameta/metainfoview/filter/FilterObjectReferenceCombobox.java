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
package de.jdynameta.metainfoview.filter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Comparator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeCombobox.ComboboxObjectList;
import de.jdynameta.metainfoview.attribute.model.ModelObjectReferenceCombobox;
import de.jdynameta.metainfoview.attribute.model.PersListenerQueryObjectListModel;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterObjectReferenceCombobox<TEditObj extends ApplicationObj> implements FilterEditorComponent 
{
	private JComboBox  objectCombo;
	private InputChangedListener inputChangedListener;
	private final ObjectReferenceAttributeInfo referenceInfo;
	private final ApplicationManager<TEditObj> appManager;
	
	
	public FilterObjectReferenceCombobox( ObjectReferenceAttributeInfo aInfo
			, ApplicationManager<TEditObj> anAppMngr) throws JdyPersistentException 
	{
		super();
		
		this.appManager = anAppMngr;
		this.referenceInfo = aInfo;
		
		PersListenerQueryObjectListModel<TEditObj> objList = new PersListenerQueryObjectListModel<TEditObj>(anAppMngr, new  DefaultClassInfoQuery(aInfo.getReferencedClass()))  ;
		objList.refresh();
		this.objectCombo =  new JComboBox(new ComboboxObjectList<TEditObj>(objList));

		objectCombo.setRenderer( createAttributeTextRenderer());
		
		
		objectCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
			}
		});
	}
	
	protected Comparator<TEditObj> createAttributeTextComparator() 
	{
		final Collator textCollator = Collator.getInstance();
		textCollator.setStrength(Collator.TERTIARY);

		return new Comparator<TEditObj>()
		{
			public int compare(TEditObj o1, TEditObj o2)
			{
				return  textCollator.compare( getTextForAttribute(o1), getTextForAttribute(o2));
			}
		};
	}	
	
	/* (non-Javadoc)
	 * @see de.comafra.view.attribute.AbstractAttributeCombobox#getTextForAttribute(java.lang.Object)
	 */
	protected String getTextForAttribute(Object aValue)
	{
		return ModelObjectReferenceCombobox.getTextForAttribute((ValueObject)aValue, appManager, referenceInfo.getReferencedClass());
	}
	
	@SuppressWarnings("serial")
	protected ListCellRenderer createAttributeTextRenderer() 
	{
		return new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(
				JList list,	Object value, int index,
				boolean isSelected, boolean cellHasFocus) 
			{
				Component resultComp = super.getListCellRendererComponent( list, value, index
																			,isSelected, cellHasFocus);
				if( resultComp instanceof JLabel ) {
					if( value != null) {
						((JLabel)resultComp).setText(getTextForAttribute(value));															
					} else {
						((JLabel)resultComp).setText("_  ");															
					}
				}
				return resultComp;																			
			}

		};
	}
	
	
	public void markFieldAsChanged(Color markerColor) 
	{
		this.objectCombo.setBackground(markerColor);
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(objectCombo, constraints);	
	}

	public Object getValue()
	{
		return ( objectCombo.getSelectedItem() == null ) ? null : objectCombo.getSelectedItem();
	}
	
	public void setValue(Object newValue)
	{
		if( newValue == null || newValue instanceof Long) {
			objectCombo.setSelectedItem( ((String) newValue));
		}
	}
	

	public boolean hasValidInput()
	{
		return true;
	}
	

	/**
	 * @param listener
	 */
	public void setInputChangedListener(InputChangedListener listener)
	{
		inputChangedListener = listener;
	}

}

