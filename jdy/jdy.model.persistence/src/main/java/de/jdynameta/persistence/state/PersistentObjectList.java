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
package de.jdynameta.persistence.state;

import java.util.List;

import de.jdynameta.base.objectlist.EditableObjectList;
import de.jdynameta.base.objectlist.ObjectListModel;

/**
 * Object List of Persistent Objects which could be changed  
 * @author Rainer Schneider
 *
 * @param <TListType>
 */
public interface PersistentObjectList<TListType extends PersistentValueObject> extends ObjectListModel<TListType>, EditableObjectList<TListType>
{
	public void addObject(TListType newObj);
	public void removeObject(TListType objToDelete);
	public List<TListType> getDeletedObjects();
	public void updateObject(TListType objToUpdate);
	
}
