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
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AttributeStringTextArea implements AttributeComponent 
{
	private JTextArea attributeTextField;
	private Object objectToEdit;
	private boolean isEditable;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	private boolean isDirty;
	private boolean isNullable;

	/**
	 * 
	 */
	public AttributeStringTextArea(long maxLength) 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		objectToEdit = null;
		isEditable = true;
		isNullable = true;
		attributeTextField = new JTextArea();
		attributeTextField.setRows(4);
		AbstractDocument doc = (AbstractDocument)attributeTextField.getDocument();
		doc.setDocumentFilter( new AttributeDocumentFilter(maxLength));

		isDirty = false;
		attributeTextField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				setIsDirty(true); 
			}
			public void insertUpdate(DocumentEvent e)
			{
				setIsDirty(true); 
			}
			public void removeUpdate(DocumentEvent e)
			{
				setIsDirty(true); 
			}
		});

		updateComponentEditable();
	}

	protected JTextArea getAttributeTextField() 
	{
		return attributeTextField;
	}

	/* (non-Javadoc)
	 * @see com.siemens.cdscms.client.ui.attribute.AttributeComponent#addToContainer(java.awt.Container, java.lang.Object)
	 */
	public void addToContainer(Container aContainer, Object constraints) 
	{
		JScrollPane areaScrollPane = new JScrollPane(attributeTextField);
		
		aContainer.add(areaScrollPane, constraints);	
		
	}

	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {
			setStringInObject(objectToEdit,attributeTextField.getText());
		}
		isDirty = false;
	}


	public void readValueFromObject(Object anObject)
	{
		objectToEdit = anObject;
		String aString = null;
		if (anObject != null) {
			aString = getStringFromObject(anObject);
		}
		attributeTextField.setText(aString);
		isDirty = false;
		updateComponentEditable();
	}

	public void setEditable(boolean editable)
	{
		isEditable = editable;
		updateComponentEditable();
	}

	protected void updateComponentEditable()
	{
		
		attributeTextField.setEditable(isEditable && objectToEdit != null);
		attributeTextField.setEnabled(true);
		if( attributeTextField.isEditable()) {
			attributeTextField.setBackground(UIManager.getColor("TextField.background"));
		} else {
			attributeTextField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
		}
	}

	/* (non-Javadoc)
	 */
	public boolean hasValidInput()
	{
		return isNullable || (attributeTextField.getText() != null && attributeTextField.getText().length() > 0);
	}

	/* (non-Javadoc)
	 */
	public boolean isDirty()
	{
		return isDirty;
	}

	/* (non-Javadoc)
	 */
	private void setIsDirty(boolean aFlag)
	{
		isDirty = aFlag;
		fireInputHasChanged();
	}

	/**
	 * @param listener
	 */
	public void addInputChangedListener(InputChangedListener listener)
	{
		inputChangedListenerColl.add(listener);
	}

	public void removeInputChangedListener(InputChangedListener listener) 
	{
		inputChangedListenerColl.remove(listener);
	}
	
	private void fireInputHasChanged()
	{
		for (InputChangedListener curListener : inputChangedListenerColl) {
			curListener.inputHasChanged();
		}
	}

	public void setNullable(boolean aNullableFlag)
	{
		isNullable = aNullableFlag;
		
	}

	public void addLabelToContainer(String aLabelText, Container aContainer, Object constraints )
	{
		JLabel newLabel = new JLabel(aLabelText +":");
		
		if( !isNullable) {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.BOLD) );
		} else {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.PLAIN) );
		}
		
		aContainer.add(newLabel, constraints);	
	}
	
	protected abstract String getStringFromObject(Object anObject);
	protected abstract void setStringInObject(Object anObject, String insertedDate);

}
