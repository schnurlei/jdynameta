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

import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.PersistentValueObject;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.base.PanelManager;

/**
 * @author Rainer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("serial")
public class CreateObjectReferenceObjectPanel<TEditObj extends ApplicationObj> extends ValueModelEditPanel<TEditObj> 
{
	private PersistentValueObject newObject;
	private final ObjectReferenceAttributeInfo referencedInfo;

	/**
	 * @param aPersistenceManager
	 * @param aBaseClassInfo
	 * @throws JdyPersistentException
	 * @throws ObjectCreationException 
	 */
	public CreateObjectReferenceObjectPanel(ApplicationManager<TEditObj> aAppMngr
											, ObjectReferenceAttributeInfo aReferenceInfo
											, PanelManager aPanelManager) throws JdyPersistentException, ObjectCreationException 
	{
		super( aReferenceInfo.getReferencedClass(), aPanelManager, aAppMngr);

		this.referencedInfo = aReferenceInfo;
		this.changeDisplayClassInfo();
	}

	protected void changeDisplayClassInfo() throws JdyPersistentException, ObjectCreationException
	{
		
		if(this.referencedInfo != null) {
			
			TEditObj newObject = getAppManager().createObject(referencedInfo.getReferencedClass(), null);
			setObjectToEdit(newObject);
		}
	}	
	/**
	 * @return
	 */
	public PersistentValueObject getNewObject() 
	{
		return newObject;
	}

}
