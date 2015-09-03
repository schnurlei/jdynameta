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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeComponent;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.persistence.state.PersistentStateModel;
import de.jdynameta.persistence.state.PersistentValueObject;
import de.jdynameta.persistence.state.PersistentStateModel.Listener;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowManager;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.SaveException;

public abstract class SimpleValueModelEditPanel<TViewObj extends ValueObject, TEditObj extends PersistentValueObject> extends ClassInfoEditPanel<TEditObj> 
{
	private ClassInfo baseClassInfo;
	private Collection<AttrInfoComponent> modelComponentColl;
	private final AttributeComponent.InputChangedListener attributeChangeListener;
	private final PanelManager panelManager;
	private TEditObj currentEditedObject;
	private boolean isNew;
	private final WorkflowManager<TEditObj> workflowManager;
	private final PersistentStateModel.Listener persistentStateListener;
	private boolean isPanelReadOnly;
	private WorkflowCtxt workflowContext;

	
	public SimpleValueModelEditPanel(	PanelManager aPanelManger, WorkflowManager<TEditObj> aWorkflowManager, WorkflowCtxt aWorkflowContext)
			throws JdyPersistentException 
	{
		super();
		this.isPanelReadOnly = false;
		this.workflowManager = aWorkflowManager;
		this.panelManager = aPanelManger;
		this.workflowContext = aWorkflowContext;
		this.isNew = true;
		this.persistentStateListener = createPersistentStateListener();
		this.setLayout(new BorderLayout());
		this.modelComponentColl = new ArrayList<AttrInfoComponent>(10);
		
		this.attributeChangeListener = new AttributeComponent.InputChangedListener()
		{
			public void inputHasChanged()
			{
				fireIsDirtyChanged();	
				fireCanCloseChanged();
			}
		};
	}
	
	public WorkflowCtxt getWorkflowContext()
	{
		return workflowContext;
	}

	public void setWorkflowContext(WorkflowCtxt workflowContext) throws JdyPersistentException
	{
		this.workflowContext = workflowContext;
	}
	
	public PanelManager getPanelManager() 
	{
		return panelManager;
	}
	
	public Collection<AttrInfoComponent> getModelComponentColl() 
	{
		return modelComponentColl;
	}
	
	protected AttributeComponent.InputChangedListener getAttributeChangeListener() 
	{
		return attributeChangeListener;
	}
	
	protected TEditObj getCurrentEditedObject() 
	{
		return currentEditedObject;
	}
	
	protected Listener createPersistentStateListener() {
		return new Listener()
		{
			public void stateChanged()
			{
				persistentStateChanged();
				fireIsDirtyChanged();	
				fireCanCloseChanged();
			}
		};
	}
	
	
	/**
	 * Set the values of the object into the appropriate swing components
	 * @param aValueObject  
	 * @throws PersistenceException
	 */
	public void setObjectToEdit(TEditObj aValueObject, boolean aIsNewFlag) 
	{
		if( this.currentEditedObject != null) {
			this.currentEditedObject.getPersistentState().removePersistenceListener(persistentStateListener);
		}
		
		this.isNew = aIsNewFlag;
		this.currentEditedObject = aValueObject;
		
		for (Iterator<AttrInfoComponent> attrCompIter = this.modelComponentColl.iterator(); attrCompIter.hasNext();) {

			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			curAttrComp.readValueFromObject(aValueObject);
		}

		if( this.currentEditedObject != null) {
			this.currentEditedObject.getPersistentState().addPersistenceListener(this.persistentStateListener);
		}
		persistentStateChanged();
		fireIsDirtyChanged();	
		fireCanCloseChanged();
	}
	

	
	public void persistentStateChanged() 
	{
		 updateComponentVisibility();
	}
	
	
	protected void updateComponentVisibility()
	{
		// disable key fields, if object is not in state new
		for(Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {

			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			if( isPanelReadOnly) {
				curAttrComp.setEditable(false);
			} else {
				curAttrComp.setEditable(isComponentEditable(curAttrComp));
			}
		}
		
	}

	protected boolean isComponentEditable(AttrInfoComponent curAttrComp) 
	{
		if( (curAttrComp.getAttributeInfo() != null)) {

			return isAttributeEditable(curAttrComp.getAttributeInfo());
		} else {
			return isAssociationEditable(curAttrComp.getAssociationInfo());
		}
	}
	
	private boolean isAttributeEditable(AttributeInfo anInfo)
	{

		boolean isEditableWorkflow = true;
		WorkflowManager<TEditObj> workflow = this.workflowManager;
		if( workflow  != null ) {
			isEditableWorkflow = workflow.isAttributeEditable(currentEditedObject, baseClassInfo, anInfo, workflowContext);
		}

		return currentEditedObject != null 
				&& ( isNew || !anInfo.isKey()) 
				&& !anInfo.isGenerated() &&  isEditableWorkflow ;
		
	}
	
	private boolean isAssociationEditable(AssociationInfo anAssocInfo)
	{
		boolean isEditableWorkflow = true;
		WorkflowManager<TEditObj> workflow = this.workflowManager;
		if( workflow  != null && anAssocInfo != null) {
			isEditableWorkflow = workflow.isAssociationEditable(currentEditedObject, baseClassInfo, anAssocInfo, workflowContext);
		}
		return isEditableWorkflow;
		
	}
	
	@Override
	public void setPanelReadOnly(boolean isPanelReadOnly) 
	{
		this.isPanelReadOnly = isPanelReadOnly;
		persistentStateChanged();
	}
	
	@Override
	/**
	 * Write the values of the Swing components into the fields of the given valueModel
	 * @param country country to write in 
	 * @throws SaveException 
	 */
	public void writeChangedValuesIntoObject() throws SaveException 
	{
		DefaultPropertyNameCreator propertyGenerator = new DefaultPropertyNameCreator();
		// check whether the values are valid 
		ArrayList<AttrInfoComponent> invalidList = new ArrayList<AttrInfoComponent>();
		// write the text form the textAreas to the model
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {
			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			if( !curAttrComp.hasValidInput()) {
				invalidList.add(curAttrComp);
			}
		}

		if( invalidList.size() > 0) {
			String fieldList = null;
			for (AttrInfoComponent attrInfoComponent : invalidList) {
				if ( attrInfoComponent.getAttributeInfo() != null ) {
					fieldList = ( fieldList == null ) ? "" : fieldList + ", ";
					fieldList += panelManager.getResourceLoader().getString(getNameForComponent(propertyGenerator, attrInfoComponent));
				}
			}
			throw new SaveException(panelManager.getResourceLoader().getMessage("ValueModelEditPanel.error.invalidFields" , fieldList)); 
		}
		
		// write the values to the model
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {
			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			curAttrComp.writeValueIntoObject();
		}
		fireIsDirtyChanged();	
	}

	protected String getNameForComponent(	DefaultPropertyNameCreator propertyGenerator,AttrInfoComponent attrInfoComponent) 
	{
		return propertyGenerator.getPropertyNameFor(baseClassInfo, attrInfoComponent.getAttributeInfo());
	}

	@Override
	public void save() throws SaveException 
	{
		writeChangedValuesIntoObject();
	}
	
	@Override
	public void discardChanges() 
	{
		// write the values to the model
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {
			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			curAttrComp.readValueFromObject(currentEditedObject);
		}
		fireIsDirtyChanged();	
	}

	
	@Override
	public boolean isDirty() 
	{
		boolean isComponentDirty = false;
	
		for(Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); !isComponentDirty && attrCompIter.hasNext();) {

			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			isComponentDirty = curAttrComp.isDirty();
		}
		return isComponentDirty && !isPanelReadOnly;
	}
	
	@Override
	public boolean canClose()
	{
		return isDirty();
	}

}
