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

import java.util.List;
import java.util.Vector;

import de.jdynameta.persistence.state.PersistentValueObject;

public class PersistentValueObjectHolderImpl<TEditObj extends PersistentValueObject> 
	implements PersistValueObjectHolder<TEditObj>
{
	private TEditObj editedObject;
	private final List<PersistValueObjectHolder.ModelListener> listenerColl;

	public PersistentValueObjectHolderImpl( )
	{
		this.listenerColl = new Vector<ModelListener>();
	}

	public void addModelListener(ModelListener listenertoAdd)
	{
		listenerColl.add(listenertoAdd);
	}
	public void removeModelListener(ModelListener listenertoRemove)
	{
		listenerColl.remove(listenertoRemove);
	}
	
	protected void fireEditedObjectReplaced()
	{
		for (ModelListener curListener : listenerColl)
		{
			curListener.editedObjectReplaced();
		}
	}
	
	public void setEditedObject(TEditObj aValueObject)
	{
		this.editedObject =  aValueObject;
		fireEditedObjectReplaced();
	}
	

	public TEditObj getEditedObject()
	{
		return editedObject;
	}
		
}
