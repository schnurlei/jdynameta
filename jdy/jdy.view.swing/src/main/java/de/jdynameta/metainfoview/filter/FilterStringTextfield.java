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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import de.jdynameta.metainfoview.attribute.AttributeDocumentFilter;

/**
 *
 * @author Rainer Schneider
 *
 */
public class FilterStringTextfield implements FilterEditorComponent 
{
	private JTextField  attributeTextField;
	private InputChangedListener inputChangedListener;
	
	
	public FilterStringTextfield(  long aMaxLength) 
	{
		super();
		
		attributeTextField = new JTextField();
		AbstractDocument doc = (AbstractDocument)attributeTextField.getDocument();		
		doc.setDocumentFilter( new AttributeDocumentFilter(aMaxLength));

		attributeTextField.getDocument().addDocumentListener(new DocumentListener() 
		{
			public void changedUpdate(DocumentEvent e) 
			{
			}

			public void insertUpdate(DocumentEvent e) 
			{
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
			}

			public void removeUpdate(DocumentEvent e) {
				if( inputChangedListener != null) {
					inputChangedListener.inputHasChanged();	
				}	
			}
		});
	}

	public void markFieldAsChanged(Color markerColor) 
	{
		this.attributeTextField.setBackground(markerColor);
	}
	
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeTextField, constraints);	
	}

	public Object getValue()
	{
		return ( attributeTextField.getText() == null || attributeTextField.getText().trim().length() == 0) ? null : attributeTextField.getText();
	}
	
	public void setValue(Object newValue)
	{
		if( newValue instanceof String) {
			attributeTextField.setText( ((String) newValue));
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

