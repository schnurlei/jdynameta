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

import javax.swing.JCheckBox;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterBooleanCheckbox implements FilterEditorComponent 
{
	
	private JCheckBox  attributeCheckBox;
	private InputChangedListener inputChangedListener;
	
	public FilterBooleanCheckbox() 
	{
		super();
		
	
		attributeCheckBox = new JCheckBox(); 
		attributeCheckBox.addActionListener(new ActionListener() 
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
		this.attributeCheckBox.setBackground(markerColor);
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeCheckBox, constraints);	
	}

	public Object getValue()
	{
		return new Boolean(attributeCheckBox.isSelected());
	}
	
	public void setValue(Object newValue)
	{
		if( newValue instanceof Boolean) {
			attributeCheckBox.setSelected( ((Boolean) newValue).booleanValue());
		}
	}
	

	/**
	 * @param listener
	 */
	public void setInputChangedListener(InputChangedListener listener)
	{
		inputChangedListener = listener;
	}



}
