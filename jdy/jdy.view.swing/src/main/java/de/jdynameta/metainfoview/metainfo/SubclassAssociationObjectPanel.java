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

import java.util.ArrayList;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;

/**
 * @author Rainer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("serial")
public class SubclassAssociationObjectPanel<TEditObj extends ApplicationObj> extends SubclassEditPanel<TEditObj> 
{
	private AssociationInfo assocInfo;
	private TEditObj masterobject;
	/**
	 * @param aPersistenceManager
	 * @param aBaseClassInfo
	 * @throws JdyPersistentException 
	 * @throws JdyPersistentException
	 */
	public SubclassAssociationObjectPanel(ApplicationManager<TEditObj> persistenceBridge
										, AssociationInfo anAssocInfo
										, TEditObj aMasterobject
										, PanelManager aPanelManager
										, WorkflowCtxt aWorkflowContext) throws JdyPersistentException  
	{
		super( persistenceBridge, aPanelManager, aWorkflowContext);
		
		this.assocInfo = anAssocInfo;
		this.masterobject = aMasterobject;
		setColumnVisibility(createDefaultColumnVisibility(null));
		super.setBaseClassInfo(anAssocInfo.getDetailClass());
	}

	protected ColumnVisibilityDef createDefaultColumnVisibility(final ColumnVisibilityDef parentVisibility)
	{
		return new ColumnVisibilityDef()
		{
			public boolean isAttributeVisible(AttributeInfo anAttrInfo)
			{
				boolean isBackReference = assocInfo.getMasterClassReference().equals(anAttrInfo);
				return !isBackReference && ( parentVisibility == null || parentVisibility.isAttributeVisible(anAttrInfo));
			}
			
			@Override
			public boolean isAssociationVisible(AssociationInfo anAssocInfo) 
			{
				return ( parentVisibility == null || parentVisibility.isAssociationVisible(anAssocInfo));
			}
		};
	}
	
	public void setColumnVisibility(ColumnVisibilityDef columnVisibility) throws JdyPersistentException 
	{
		super.setColumnVisibility( createDefaultColumnVisibility(columnVisibility) );
	}
	
	
	@Override
	public boolean canClose()
	{
		return super.isDirty();
	}
	
	@Override
	public void close() throws CloseVetoException
	{
		ArrayList<AttrInfoComponent> invalidList = this.getEditPnl().getInvalidComponents();
		if( invalidList.size() > 0) {
			String fieldList = this.getEditPnl().getInvalidFieldsString(invalidList);
			throw new CloseVetoException(getEditPnl().getPanelManager().getResourceLoader().getMessage("ValueModelEditPanel.error.invalidFields", fieldList)); 
		}
		
	}
	
	@Override
	protected TEditObj createNewObject(ApplicationManager<TEditObj> persistenceBridge,
			ClassInfo newClass) throws ObjectCreationException
	{
		
		if( !getAppManager().isWritingDependentObject() && this.masterobject.getPersistentState().isNew()) {
			throw new ObjectCreationException("Subobjects could not be added to new objects. Please save first");
		}
		TEditObj newObj = super.createNewObject(persistenceBridge, newClass);
		newObj.setValue(assocInfo.getMasterClassReference(), masterobject);
		return newObj;
	}

}
