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
import javax.swing.JPanel;

import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;

/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AttributeBlobPanel implements AttributeComponent 
{
	
	private JPanel attributePanel;
	private Object objectToEdit;
	private boolean isEditable;
	private boolean isNullable;
	private boolean isDirty;
	private BlobType blobType;
	private final ArrayList<InputChangedListener> inputChangedListenerColl;

	public AttributeBlobPanel(BlobType aType) 
	{
		super();
		
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		this.blobType = aType;
		this.objectToEdit = null;
		this.isEditable = true;
		this.isDirty = false;
		this.isNullable = true;
		
		attributePanel = new JPanel(); 
		updateComponentEditable();
	}

	public void addToContainer(Container aContainer, Object constraints) 
	{
		aContainer.add(attributePanel, constraints);	
	}

	public void writeValueIntoObject()
	{
		if( objectToEdit != null) {

			setBlobInObject(objectToEdit, new BlobByteArrayHolder(new byte[0]));
		}
		isDirty = false;
	}
	


	public void readValueFromObject(Object anObject)
	{
		objectToEdit = anObject;
		
		BlobByteArrayHolder newValue = null;
		if (anObject != null) {
			newValue = getBlobFromObject(anObject);
		}

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
		attributePanel.setEnabled(isEditable && objectToEdit != null);
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

	

	protected abstract BlobByteArrayHolder getBlobFromObject(Object anObject);
	protected abstract void setBlobInObject(Object anObject, BlobByteArrayHolder newValue);

}
