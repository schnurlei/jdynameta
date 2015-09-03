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
package de.jdynameta.metainfoview.metainfo;

import de.jdynameta.persistence.state.PersistentValueObject;

/**
 * Holds an PersistentValueObject and notifies when it has changed
 * @author Rainer Schneider
 *
 */
public interface PersistValueObjectHolder<TEditObj extends PersistentValueObject> 
{

	public TEditObj getEditedObject();
	public void addModelListener(ModelListener aListenertoAdd); 
	public void removeModelListener(ModelListener aListenertoRemove); 
	
	
	public static interface ModelListener
	{
		/** called when the edited Object eas replaced by another persistent object */
		public void editedObjectReplaced();
	}
	
}