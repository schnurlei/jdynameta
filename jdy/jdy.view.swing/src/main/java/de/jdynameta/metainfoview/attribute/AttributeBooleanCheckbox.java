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
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AttributeBooleanCheckbox implements AttributeComponent 
{
	
	private JCheckBox  attributeCheckBox;
	private Object objectToEdit;
	private boolean isEditable;
	private boolean isNullable;
	private boolean isDirty;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	
	public AttributeBooleanCheckbox() 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		objectToEdit = null;
		isEditable = true;
		isDirty = false;
		isNullable = true;
		
	
		attributeCheckBox = new JCheckBox(); 

		attributeCheckBox.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				setIsDirty(true);
			}
		});

		updateComponentEditable();
	}

	protected JCheckBox getAttributeCheckBox() 
	{
		return attributeCheckBox;
	}
	
	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributeCheckBox, constraints);	
	}

	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {

			setBooleanInObject(objectToEdit, (attributeCheckBox.isSelected() ? Boolean.TRUE : Boolean.FALSE));
		}
		isDirty = false;
	}
	


	public void readValueFromObject(Object anObject)
	{
		objectToEdit = anObject;
		
		Boolean newValue = null;
		if (anObject != null) {
			newValue = getBooleanFromObject(anObject);
		}
		attributeCheckBox.setSelected((newValue != null) ? newValue.booleanValue() : false);

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
		attributeCheckBox.setEnabled(isEditable && objectToEdit != null);
	}

	/* (non-Javadoc)
	 */
	public boolean hasValidInput()
	{
		
		return true;
	}
	

	/* (non-Javadoc)
	 */
	public boolean isDirty()
	{
		return isDirty;
	}


	/**
	 * @param b
	 */
	public void setNullable(boolean aNotNullFlag)
	{
		isNullable = aNotNullFlag;	
	}

	private void setIsDirty(boolean b) 
	{
		isDirty = b;
		fireInputHasChanged();	
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
	
	protected abstract Boolean getBooleanFromObject(Object anObject);
	protected abstract void setBooleanInObject(Object anObject, Boolean newValue);

}
