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

/**
 * Interface for Classes to Edit an Attribute (InstanceVariable) of an Object
 * in an Swing-Component
 *  
 * @author Rainer Schneider
 *
 */
public interface AttributeComponent 
{
	/**
	 * Write the changes into the Object, that was set in the last 
	 * call of {@link #readAttributeFromObject(Object)}
	 */
	public void writeValueIntoObject();

	/**
	 * set the attribute value of the Object in the Swing-Component 
	 * 
	 */
	public void readValueFromObject(Object anObject);

	/**
	 * Add the Edit Component to the given container with the given constraints
	 * @param aContainer
	 * @param constraints
	 */
	public void addToContainer(Container aContainer, Object constraints);

	/**
	 * Add the Edit Component to the given container with the given constraints
	 * @param aContainer
	 * @param constraints
	 */
	public void addLabelToContainer(String aLabelText, Container aContainer, Object constraints );
	
	/**
	 * Set the Component editable,
	 * editable means, that the value of the Component could be changed
	 * @param editable true - the value of the component could be changed
	 * 							when the internal state of the componet allows a change
	 * 							(for example a Object to edit have to be set) 
	 * 					false - the value of the Component could not be changed 
	 */
	public void setEditable(boolean editable);
	
	
	/**
	 * Test if the inserted value in the component has a valid Content
	 * @return
	 */
	public boolean hasValidInput();

	/** the value of the component has be changed since the last call of {@link AttributeComponent#readAttributeFromObject(Object)}
	 * or {@link #writeAttributeIntoObject()} or the creation of the component
	 */
	public boolean isDirty();

	/**
	 * Set a Listener, wich is notified, when the input in the Component has changed
	 * @param listener
	 */
	public void addInputChangedListener(InputChangedListener listener);

	public void removeInputChangedListener(InputChangedListener listener);


	public static interface InputChangedListener
	{
		public void inputHasChanged();	
	}

}
