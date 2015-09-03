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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.metainfoview.attribute.AttributeCombobox.ComboboxObjectList;
import de.jdynameta.metainfoview.attribute.model.ModelComboboxField;
import de.jdynameta.metainfoview.attribute.model.ModelComboboxField.DomainValueComboElem;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterDomainValueCombobox implements FilterEditorComponent 
{
	private JComboBox  attributeCombo;
	private InputChangedListener inputChangedListener;
	
	
	public FilterDomainValueCombobox(ObjectListModel<DomainValueComboElem>  aObjectColl) 
	{
		super();
		
		this.attributeCombo =  new JComboBox(new ComboboxObjectList<ModelComboboxField.DomainValueComboElem>(aObjectColl));

		attributeCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
			}
		});
	}
	
	public void markFieldAsChanged(Color markerColor) 
	{
		this.attributeCombo.setBackground(markerColor);
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeCombo, constraints);	
	}

	public Object getValue()
	{
		return ( attributeCombo.getSelectedItem() == null ) ? null : ((DomainValueComboElem)attributeCombo.getSelectedItem()).getAttributeValue();
	}
	
	public void setValue(Object newValue)
	{
		if( newValue == null || newValue instanceof Long) {
			attributeCombo.setSelectedItem( ((String) newValue));
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

