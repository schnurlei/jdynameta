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

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AbstractAttributeComponent implements AttributeComponent 
{
	private Object objectToEdit;
	private boolean isEditable;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	private boolean isDirty;
	private boolean isNotNull;

	/**
	 * 
	 */
	public AbstractAttributeComponent() 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		objectToEdit = null;
		isEditable = true;
		isNotNull = false;
		isDirty = false;
	}

	protected final boolean isEditable()
	{
		return this.isEditable;
	}

	public void setEditable(boolean editable)
	{
		isEditable = editable;
		updateComponentEditable();
	}

	protected abstract void updateComponentEditable();


	/* (non-Javadoc)
	 */
	public boolean isDirty()
	{
		return isDirty;
	}
	
	/**
	 * set the is dirty flag with not notifying the inputchangelistener
	 * @param aFlag
	 */
	protected void setIsDirtyNoEvent(boolean aFlag) {
		
	}

	protected final boolean isNotNull()
	{
		return this.isNotNull;
	}


	
	/* (non-Javadoc)
	 */
	protected void setIsDirty(boolean aFlag)
	{
		isDirty = aFlag;
		fireInputHasChanged();	
	}


	public void setNotNull(boolean aNotNullFlag)
	{
		isNotNull = aNotNullFlag;
		
	}

	public void addLabelToContainer(String aLabelText, Container aContainer, Object constraints )
	{
		JLabel newLabel = new JLabel(aLabelText +":");
		
		if( isNotNull) {
			newLabel.setFont(newLabel.getFont().deriveFont(Font.BOLD) );
		}
		
		aContainer.add(newLabel, constraints);	
	}


	protected final Object getObjectToEdit()
	{
		return this.objectToEdit;
	}



	protected final void setObjectToEdit(Object aObjectToEdit)
	{
		this.objectToEdit = aObjectToEdit;
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
	
}
