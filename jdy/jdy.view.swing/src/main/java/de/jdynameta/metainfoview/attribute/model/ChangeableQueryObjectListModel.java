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
package de.jdynameta.metainfoview.attribute.model;

import java.util.Collections;

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * List Model that reads its objects by a Query
 * It listens to the Persistence Manager for the add, remove and update of Object
 * Translate a PersistentEvent into ObjectListModelEvent 
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ChangeableQueryObjectListModel<TViewObj extends ValueObject> extends PersListenerQueryObjectListModel<TViewObj>
{
	/**
	 * 
	 */
	public ChangeableQueryObjectListModel(PersistentObjectReader<TViewObj> aPersistenceReader
										, final ClassInfoQuery aQuery, boolean listenToChanges) throws JdyPersistentException
	{
		super( aPersistenceReader, aQuery, listenToChanges);
	}

	public void insertObject(TViewObj newObj)
	{
		//		assert(getFilter().accept(newObj));
		getAllObjects().add(newObj);
		fireIntervalAdded(ChangeableQueryObjectListModel.this,getAllObjects().size()-1,getAllObjects().size()-1);
	}

	public void removeObject(TViewObj oldObj)
	{
		int index = getAllObjects().indexOf(oldObj);
		if (index >= 0) {
			getAllObjects().remove(oldObj);
			fireIntervalRemoved(ChangeableQueryObjectListModel.this,index,index, Collections.singletonList(oldObj));
		}
	}

	public void updateObject(TViewObj objToUpdate) 
	{
		int index = this.getAllObjects().indexOf(objToUpdate);
		if (index >= 0) {
			this.fireIntervalUpdated(this, index, index);
		}
		
	};
	

	
}
