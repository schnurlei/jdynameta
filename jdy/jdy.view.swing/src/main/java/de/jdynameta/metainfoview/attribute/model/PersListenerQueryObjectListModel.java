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
 * 
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class PersListenerQueryObjectListModel<TViewObj extends ValueObject> extends ApplicationQueryObjectListModel<TViewObj>
{
	private PersistentObjectReader.PersistentListener<TViewObj> persistentListener;

	/**
	 * 
	 */
	public PersListenerQueryObjectListModel(PersistentObjectReader<TViewObj> aPersistenceReader 
										, final ClassInfoQuery aQuery) throws JdyPersistentException
	{
		this(aPersistenceReader, aQuery, true) ;
	}
	
	
	/**
	 * 
	 */
	protected PersListenerQueryObjectListModel(PersistentObjectReader<TViewObj> aPersistenceReader 
										, final ClassInfoQuery aQuery, boolean islistenToChanges) throws JdyPersistentException
	{
		super( aPersistenceReader, aQuery);

		this.persistentListener = new PersistentObjectReader.PersistentListener<TViewObj>()
		{
			public void persistentStateChanged(PersistentObjectReader.PersistentEvent<TViewObj>  aEvent) 
			{
				switch (aEvent.getState()) {
					case OBJECT_CREATED :
						if(getFilter().accept(aEvent.getChangedObject())){
							getAllObjects().add( aEvent.getChangedObject());
							fireIntervalAdded(PersListenerQueryObjectListModel.this,getAllObjects().size()-1,getAllObjects().size()-1);
						}
						break;
					case OBJECT_DELETED :
						int index = getAllObjects().indexOf(aEvent.getChangedObject());
						if (index >= 0) {
							getAllObjects().remove(aEvent.getChangedObject());
							fireIntervalRemoved(PersListenerQueryObjectListModel.this,index,index, Collections.singletonList(aEvent.getChangedObject()));
						}
						break;
					case OBJECT_MODIFIED :
						int modIdx = getAllObjects().indexOf(aEvent.getChangedObject());
						if (modIdx >= 0) {
							fireIntervalUpdated(PersListenerQueryObjectListModel.this,modIdx,modIdx);
						}
						break;
				} aEvent.getState();
			}
		};
		
		if( islistenToChanges) {
			aPersistenceReader.addListener(aQuery.getResultInfo(), persistentListener);		
		}
	}


	
}
