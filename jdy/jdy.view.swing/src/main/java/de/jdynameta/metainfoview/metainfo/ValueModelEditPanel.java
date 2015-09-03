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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.attribute.AttributeComponent;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.PersistentStateModel;
import de.jdynameta.persistence.state.PersistentStateModel.Listener;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowManager;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.SaveException;

/**
 * Panel to edit the value model of a specific ClassInfo,
 * If the model has subclasses 
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ValueModelEditPanel<TEditObj extends ApplicationObj> extends ClassInfoEditPanel<TEditObj>
{
	private static final Logger logger = Logger.getLogger(ValueModelEditPanel.class.getName());
	
	
	private ClassInfo baseClassInfo;

	protected Collection<AttrInfoComponent> modelComponentColl;
	private final AttributeComponent.InputChangedListener attributeChangeListener;
	private final PanelManager panelManager;
	private TEditObj currentEditedObject;
	private final ApplicationManager<TEditObj> appManager;
	private final PersistentStateModel.Listener persistentStateListener;
	private ComponentLayoutStrategy layoutStrategy;
	private ComponentCreationStrategy createComponentStrategy;
	private boolean isPanelReadOnly;
	/** show all fields when columnVisibility is null */
	private ColumnVisibilityDef columnVisibility;
	private WorkflowCtxt workflowContext;
	
	public ValueModelEditPanel( PanelManager aPanelManger
			, ApplicationManager<TEditObj> aPersistenceBridge) throws JdyPersistentException 
	{
		this(null, aPanelManger,aPersistenceBridge);
	}
	
	public ValueModelEditPanel( ClassInfo aBaseClassInfo, PanelManager aPanelManger
			, ApplicationManager<TEditObj> aPersistenceBridge) throws JdyPersistentException 
	{
		this(aBaseClassInfo, aPanelManger,aPersistenceBridge, null);
	}
	/**
	 * @throws JdyPersistentException 
	 * 
	 */
	public ValueModelEditPanel( ClassInfo aBaseClassInfo, PanelManager aPanelManger
								, ApplicationManager<TEditObj> aPersistenceBridge, WorkflowCtxt aWorkflowContext) throws JdyPersistentException 
	{
		super();

		this.isPanelReadOnly = false;
		this.appManager = aPersistenceBridge;
		this.panelManager = aPanelManger;
		this.workflowContext = aWorkflowContext;
		this.persistentStateListener = createPersistentStateListener();
		this.setLayout(new BorderLayout());
		this.modelComponentColl = new ArrayList<AttrInfoComponent>(10);
		this.layoutStrategy = new ComponentLayoutStrategy(panelManager.res());
		this.createComponentStrategy = new ComponentCreationStrategyDefault<TEditObj>(aPanelManger, aPersistenceBridge);
		this.columnVisibility = null;
		
		this.attributeChangeListener = new AttributeComponent.InputChangedListener()
		{
			public void inputHasChanged()
			{
				fireIsDirtyChanged();	
				fireCanCloseChanged();
			}
		};
		
		this.baseClassInfo = aBaseClassInfo;
		rebuildComponets();
	}

	@Override
	public void makeVisible() 
	{
		super.makeVisible();
	}
	
	public TEditObj getObjectToEdit() 
	{
		return currentEditedObject;
	}
	
	public void setBaseClassInfo(ClassInfo aBaseClassInfo) throws JdyPersistentException 
	{
		this.baseClassInfo = aBaseClassInfo;
		rebuildComponets();
	}	
	
	public ClassInfo getDisplayedClassInfo()
	{
		return (ClassInfo) this.baseClassInfo;	
	}
	
	public PanelManager getPanelManager()
	{
		return panelManager;
	}
	
	public ComponentLayoutStrategy getLayoutStrategy()
	{
		return layoutStrategy;
	}
	
	public JdyResourceLoader getResourceLoader()
	{
		return panelManager.res();
	}
	
	public WorkflowCtxt getWorkflowContext() 
	{
		return workflowContext;
	}
	
	public void setWorkflowContext(WorkflowCtxt aWorkflowContext) throws JdyPersistentException 
	{
		this.workflowContext = aWorkflowContext;
		rebuildComponets();
		setObjectToEdit(currentEditedObject);
	}
	
	protected AttributeComponent.InputChangedListener getAttributeChangeListener()
	{
		return attributeChangeListener;
	}
	
	public ApplicationManager<TEditObj> getAppManager()
	{
		return appManager;
	}
	
	public void setLayoutAndComponentStrategy(ClassInfo aClassInfo, ComponentLayoutStrategy aLayouStrategy, 	ComponentCreationStrategy aCreateComponentStrategy) throws JdyPersistentException
	{
		this.baseClassInfo = aClassInfo;
		this.layoutStrategy = aLayouStrategy;
		this.createComponentStrategy = aCreateComponentStrategy;
		rebuildComponets();
		setObjectToEdit(currentEditedObject);
	}
	
	public void setLayouStrategy(ComponentLayoutStrategy aLayouStrategy) throws JdyPersistentException
	{
		this.layoutStrategy = aLayouStrategy;
		rebuildComponets();
		setObjectToEdit(currentEditedObject);
	}
	
	public void setCreateComponentStrategy(	ComponentCreationStrategy aCreateComponentStrategy) throws JdyPersistentException 
	{
		this.createComponentStrategy = aCreateComponentStrategy;
		rebuildComponets();
		setObjectToEdit(currentEditedObject);
	}
	
	
	/**
	 * remove all components from the panel a recreate them
	 * @throws JdyPersistentException
	 */
	protected void rebuildComponets() throws JdyPersistentException
	{
		this.removeAll();
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {

			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			curAttrComp.removeInputChangedListener(attributeChangeListener);
		}
		
		modelComponentColl.clear();

		JPanel editPanelHolder = new JPanel(new BorderLayout());
		this.setLayout(new BorderLayout());
		if( getDisplayedClassInfo() != null) {
			this.modelComponentColl = createAttributeComponents();
			editPanelHolder.add(this.layoutStrategy.createMainPanel(modelComponentColl, getDisplayedClassInfo()), BorderLayout.CENTER);
		}


		this.add(editPanelHolder, BorderLayout.CENTER);

		this.invalidate();
		this.revalidate();
	}	
	
	private void persistentStateChanged() 
	{
		// disable key fields, if object is not in state new
		for(Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {

			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			if( isPanelReadOnly) {
				curAttrComp.setEditable(false);
			} else {
				if( (curAttrComp.getAttributeInfo() != null)) {

					boolean isEditableWorkflow = true;
					WorkflowManager<TEditObj> workflow = (this.appManager != null) ? this.appManager.getWorkflowManager() : null;
					if( workflow  != null ) {
						isEditableWorkflow = workflow.isAttributeEditable(currentEditedObject, baseClassInfo, curAttrComp.getAttributeInfo(), workflowContext);
					}

					curAttrComp.setEditable(currentEditedObject != null 
							&& ( currentEditedObject.getPersistentState().isNew() || !curAttrComp.getAttributeInfo().isKey()) 
							&& !curAttrComp.getAttributeInfo().isGenerated() &&  isEditableWorkflow );
				} else {
					boolean isEditableWorkflow = true;
					WorkflowManager<TEditObj> workflow = this.appManager.getWorkflowManager();
					if( workflow  != null && curAttrComp.getAssociationInfo() != null) {
						isEditableWorkflow = workflow.isAssociationEditable(currentEditedObject, baseClassInfo, curAttrComp.getAssociationInfo(), workflowContext);
					}
					curAttrComp.setEditable(isEditableWorkflow);
				}
			}
		}
	}
	
	@Override
	public boolean isDirty() 
	{
		boolean isComponentDirty = false;
	
		for(Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); !isComponentDirty && attrCompIter.hasNext();) {

			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			isComponentDirty = curAttrComp.isDirty();
			
			if( curAttrComp.isDirty()) {
				if(curAttrComp.getAttributeInfo() != null) {
					logger.fine("Dirty: " + curAttrComp.getAttributeInfo().getInternalName());
				}
				if(curAttrComp.getAssociationInfo() != null) {
					logger.fine("Dirty: " + curAttrComp.getAssociationInfo().getNameResource());
				}
			}
		}
		return isComponentDirty && !isPanelReadOnly;
	}

	@Override
	public boolean canClose() 
	{
		boolean allFiledHasValidInput = true;
		
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator()
				; allFiledHasValidInput && attrCompIter.hasNext();) {
					
			AttrInfoComponent curAttrComp 	=  attrCompIter.next();
			allFiledHasValidInput = curAttrComp.hasValidInput();
		}
	
		return allFiledHasValidInput;	
	}

	/**
	 * Set the values of the object into the appropriate swing components
	 * @param aValueObject  
	 * @throws PersistenceException
	 */
	@Override
	public void setObjectToEdit(TEditObj aValueObject) 
	{
		if( this.currentEditedObject != null) {
			this.currentEditedObject.getPersistentState().removePersistenceListener(persistentStateListener);
		}
		
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
	
	private Listener createPersistentStateListener()
	{
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

	public ArrayList<AttrInfoComponent> getInvalidComponents()
	{
		// check whether the values are valid 
		ArrayList<AttrInfoComponent> invalidList = new ArrayList<AttrInfoComponent>();
		// write the text form the textAreas to the model
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {
			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			if( !curAttrComp.hasValidInput()) {
				invalidList.add(curAttrComp);
			}
		}

		return invalidList;
	}
	
	public String getInvalidFieldsString(ArrayList<AttrInfoComponent> invalidList)
	{
		String fieldListString = null;
		for (AttrInfoComponent attrInfoComponent : invalidList) {
			if ( attrInfoComponent.getAttributeInfo() != null ) {
				fieldListString = ( fieldListString == null ) ? "" : fieldListString + ", ";
				fieldListString += panelManager.res().getString(layoutStrategy.getPropertyGenerator().getPropertyNameFor(baseClassInfo, attrInfoComponent.getAttributeInfo()));
			}
		}
		return fieldListString;
	}
	
	/**
	 * Write the values of the Swing components into the fields of the given valueModel
	 * @param country country to write in 
	 * @throws SaveException 
	 */
	public void writeChangedValuesIntoObject() throws SaveException 
	{

		ArrayList<AttrInfoComponent> invalidList = getInvalidComponents();
		if( invalidList.size() > 0) {
			String fieldList = getInvalidFieldsString(invalidList);
			throw new SaveException(panelManager.res().getMessage("ValueModelEditPanel.error.invalidFields", fieldList)); 
		}
		
		// write the values to the model
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {
			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			curAttrComp.writeValueIntoObject();
		}
		fireIsDirtyChanged();	
	}


	
	protected ColumnVisibilityDef getColumnVisibility()
	{
		return columnVisibility;
	}
	
	public void setColumnVisibility(ColumnVisibilityDef columnVisibility) throws JdyPersistentException 
	{
		this.columnVisibility = columnVisibility;
		rebuildComponets();
		setObjectToEdit(currentEditedObject);
	}
	
	/**
	 * Create the Attribute Components edited by this application
	 * @return 
	 * @throws JdyPersistentException
	 */
	protected List<AttrInfoComponent> createAttributeComponents() throws JdyPersistentException
	{
		final ColumnVisibilityDef visibility = getColumnVisibility();
		
		final List<AttrInfoComponent> newModelComponentColl = this.createComponentStrategy.createAttributeComponents(visibility, this.getDisplayedClassInfo());
		newModelComponentColl.addAll(this.createComponentStrategy.createAssociationComponents(visibility, this.getDisplayedClassInfo(), workflowContext));
		
		for (AttrInfoComponent attrInfoComponent : newModelComponentColl)
		{
			attrInfoComponent.addInputChangedListener(attributeChangeListener);
		}
		
		return newModelComponentColl;
	}

	/* (non-Javadoc)
	 * @see de.comafra.view.panel.OkPanel#terminate()
	 */
	@Override
	public void closed() 
	{
	}

	@Override
	public void save() throws SaveException
	{
		writeChangedValuesIntoObject();
		
	}
	
	@Override
	public void discardChanges() 
	{
		//@TODO Test 
		// write the values to the model
		for (Iterator<AttrInfoComponent> attrCompIter = modelComponentColl.iterator(); attrCompIter.hasNext();) {
			AttrInfoComponent curAttrComp 	= attrCompIter.next();
			curAttrComp.readValueFromObject(currentEditedObject);
		}
		fireIsDirtyChanged();	
		
	}
	

	public boolean isPanelReadOnly() {
		return isPanelReadOnly;
	}

	public void setPanelReadOnly(boolean isPanelReadOnly) 
	{
		this.isPanelReadOnly = isPanelReadOnly;
		persistentStateChanged();
	}



}
