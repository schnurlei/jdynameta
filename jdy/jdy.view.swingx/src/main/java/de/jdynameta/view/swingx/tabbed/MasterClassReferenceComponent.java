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
package de.jdynameta.view.swingx.tabbed;

import java.awt.Container;

import org.jdesktop.swingx.JXTaskPane;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.attribute.AbstractAttributeComponent;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.metainfoview.metainfo.ComponentCreationStrategy;
import de.jdynameta.metainfoview.metainfo.ValueModelEditPanel;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.SaveException;

public class MasterClassReferenceComponent<TEditObj extends ApplicationObj> extends AbstractAttributeComponent implements AttrInfoComponent
{
	private ValueModelEditPanel<TEditObj>	objectReferencePnl;
	private ObjectReferenceAttributeInfo					masterClassRefAttr;
	private AttributeInfo									objRefRefAttr;
	private ApplicationManager<TEditObj>					appManager;
	private final PanelManager pnlMngr;

	public MasterClassReferenceComponent(ObjectReferenceAttributeInfo anObjRef, ApplicationManager<TEditObj> aPersistenceBridge, PanelManager aPanelManager, ComponentCreationStrategy aCreateComponentStrategy, ColumnVisibilityDef aColumnVisibility) throws JdyPersistentException
	{
		this.pnlMngr = aPanelManager;
		this.appManager = aPersistenceBridge;
		this.masterClassRefAttr = anObjRef;

//		this.masterClassRefAttr = (ObjectReferenceAttributeInfo) ApplicationRepository.getSingleton().getAssociationInfo().getAttributeInfoForExternalName("masterClassReference");
//		this.objRefRefAttr = ApplicationRepository.getSingleton().getObjectReferenceInfo().getAttributeInfoForExternalName("referencedClass");
		
		this.objectReferencePnl = new ValueModelEditPanel<TEditObj>(anObjRef.getReferencedClass(), aPanelManager, appManager, null);
		this.objectReferencePnl.setCreateComponentStrategy(aCreateComponentStrategy);
		this.objectReferencePnl.setColumnVisibility(aColumnVisibility);
	}

	@Override
	protected void updateComponentEditable()
	{

	}

	@Override
	public void addToContainer(Container aContainer, Object constraints)
	{
		JXTaskPane filterCollpsePane = new JXTaskPane();
		filterCollpsePane.setCollapsed(true);
		filterCollpsePane.add(objectReferencePnl);
		String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(masterClassRefAttr.getReferencedClass());
		
		filterCollpsePane.setTitle(pnlMngr.res().getString(classNameProperty));

		aContainer.add(filterCollpsePane, constraints);
	}

	@Override
	public boolean hasValidInput()
	{
		return true;
	}

	@Override
	public void readValueFromObject(Object anObject)
	{
		if( anObject != null) {
			ApplicationObj masterObj = (ApplicationObj) anObject;
			
			TEditObj masterClassRef = (TEditObj) masterObj.getValue(this.masterClassRefAttr);;
			try {
				if( masterClassRef == null) {
					// create edit obj an set it in master obj 
					masterClassRef = this.appManager.createObject(this.masterClassRefAttr.getReferencedClass(), null);
					masterObj.setValue(this.objRefRefAttr, masterClassRef);
				}
				objectReferencePnl.setObjectToEdit(masterClassRef);

			} catch (ObjectCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void writeValueIntoObject()
	{
		try {
			objectReferencePnl.writeChangedValuesIntoObject();
//			TEditObj objectModel = (TEditObj) objectReferencePnl.getObjectToEdit();
//			this.appManager.saveObject(objectModel, null);

		} catch (SaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		// try
		// {
		// AppPersistentGenericObj masterClassRef =
		// this.appManager.createObject(ApplicationRepository.getSingleton().getObjectReferenceInfo());
		// masterClassRef.setValue(this.objRefRefAttr, selectedAttribute);
		// ((ChangeableValueObject) anObject).setValue(masterClassRefAttr,
		// masterClassRef);
		// } catch (ObjectCreationException e)
		// {
		// e.printStackTrace();
		// }
	}

	@Override
	public AssociationInfo getAssociationInfo()
	{
		return null;
	}

	@Override
	public AttributeInfo getAttributeInfo()
	{
		return masterClassRefAttr;
	}

	@Override
	public void setNullable(boolean aNullableFlag)
	{

	}

}