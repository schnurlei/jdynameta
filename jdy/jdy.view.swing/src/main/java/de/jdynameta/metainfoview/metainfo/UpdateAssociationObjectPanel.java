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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.PersistentValueObject;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.view.base.PanelManager;

/**
 * @author Rainer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("serial")
public class UpdateAssociationObjectPanel<TEditObj extends ApplicationObj> extends ValueModelEditPanel<TEditObj> 
{
	private TEditObj updateObject;
	private AssociationInfo assocInfo;
	/**
	 * @param aPersistenceManager
	 * @param aBaseClassInfo
	 * @throws JdyPersistentException 
	 * @throws JdyPersistentException
	 */
	public UpdateAssociationObjectPanel(ApplicationManager<TEditObj> persistenceBridge
										, AssociationInfo anAssocInfo
										, TEditObj aUpdateObj
										, PanelManager aPanelManager
										, WorkflowCtxt aWorkflowContext) throws JdyPersistentException  
	{
		super( null, aPanelManager, persistenceBridge, aWorkflowContext);
		assert(aUpdateObj != null);
		
		this.updateObject = aUpdateObj;
		this.assocInfo = anAssocInfo;
		super.setBaseClassInfo(anAssocInfo.getDetailClass());
		setObjectToEdit(this.updateObject);
	}

	@Override
	protected ColumnVisibilityDef getColumnVisibility()
	{
		return new ColumnVisibilityDef()
		{
			public boolean isAttributeVisible(AttributeInfo aAttrInfo)
			{
				return !assocInfo.getMasterClassReference().equals(aAttrInfo);
			}
			
			@Override
			public boolean isAssociationVisible(AssociationInfo aAttrInfo) 
			{
				return true;
			}
		};
	}
	
	@Override
	public boolean canClose()
	{
		return super.isDirty();
	}
	
	/**
	 * @return
	 */
	public final PersistentValueObject getUpdateObject() 
	{
		return this.updateObject;
	}

}
