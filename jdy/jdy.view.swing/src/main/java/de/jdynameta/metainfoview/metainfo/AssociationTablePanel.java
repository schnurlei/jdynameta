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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.metainfoview.metainfo.table.ClassInfoColumnModel;
import de.jdynameta.metainfoview.metainfo.table.ClassInfoTableModel;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.metainfoview.metainfo.table.RendererCreationStrategy;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.PersistentAssociationListModel;
import de.jdynameta.persistence.state.PersistentObjectList;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.SaveException;

/**
 * @author Rainer
 *
 */
public class AssociationTablePanel<TEditObj extends ApplicationObj>
	implements AttrInfoComponent
{
	private final JTable assocTbl;
	private final AssociationInfo displayedAssocInfo;
	private final ClassInfoTableModel<TEditObj> tableModel;
	private final ApplicationManager<TEditObj> appManager;
	private final PanelManager pnlMngr;
	private PersistentObjectList<TEditObj> assocList;	
	private final ArrayList<InputChangedListener> inputChangedListenerColl;
	
	private final Action editAction;
	private final Action newAction;
	private final Action deleteAction;

	private boolean isDirty;
	private boolean isEditable;
	private boolean isReadonly;
	
	private TEditObj masterobject;
	private ComponentCreationStrategy editPnlComponentStrat;
	private ComponentLayoutStrategy editPnlLayout;
	private ColumnVisibilityDef editPnlVisibility;
	private WorkflowCtxt workflowContext;

	private boolean persistentListenerIsUsed = true;
	
	/**
	 * 
	 */
	public AssociationTablePanel(AssociationInfo aAssocInfo
			, PanelManager aPanelManager
			, ApplicationManager<TEditObj> anAppMngr) 
	{
		super();
		assert(aAssocInfo != null);
		
		this.isEditable = true;
		this.isReadonly = false;
		this.appManager = anAppMngr;
		this.displayedAssocInfo = aAssocInfo;
		this.pnlMngr = aPanelManager;
		this.inputChangedListenerColl = new ArrayList<InputChangedListener>();
		
		this.editAction = createEditAction(aPanelManager.res());
		this.newAction = createNewAction(aPanelManager.res());
		this.deleteAction = createDeleteAction(aPanelManager.res());

		this.tableModel = new ClassInfoTableModel<TEditObj>(this.displayedAssocInfo.getDetailClass());	
		ClassInfoColumnModel colModel = new ClassInfoColumnModel(this.displayedAssocInfo.getDetailClass(), new AssociationColumnVisibility(), anAppMngr, aPanelManager, null);
		this.assocTbl = new JTable( tableModel,colModel);
		this.assocTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.assocTbl.getSelectionModel().addListSelectionListener(createTableSelectionListener());
		this.assocTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		Dimension headerSize = this.assocTbl.getTableHeader().getPreferredSize();
		headerSize.height += 10;
		this.assocTbl.addMouseListener(	new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent event) {
	            mouseClickInTable(event);
            }
    	});

		selectionChanged(); // initialize actions
		this.newAction.setEnabled(this.masterobject != null);
		
		addHeaderListener();
	}

	public WorkflowCtxt getWorkflowContext() 
	{
		return workflowContext;
	}
	
	/**
	 * 
	 * @param isMulteSelection
	 */
	public void setMultiSelection(boolean isMulteSelection)
	{
		if( isMulteSelection ) {
			this.assocTbl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			this.assocTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}
	
	
	public void setRendererCreationStrategy( RendererCreationStrategy aRenderStrat ) 
	{
		ClassInfoColumnModel colModel = new ClassInfoColumnModel(this.displayedAssocInfo.getDetailClass(),new AssociationColumnVisibility(),appManager, pnlMngr, aRenderStrat);
//		colModel.restoreColumnStateFromConfig(this, panelManager.getPropertyManager());
//		colModel.writeColumnChangesToConfig(this, panelManager.getPropertyManager());
		this.assocTbl.setColumnModel(colModel);
	}
	
	
	public void setWorkflowContext(WorkflowCtxt aWorkflowContext) 
	{
		this.workflowContext = aWorkflowContext;
	}
	
	public void setEditPanelCreateComponentStrat(ComponentCreationStrategy editAssocCreateComponentStrat) 
	{
		this.editPnlComponentStrat = editAssocCreateComponentStrat;
	}

	public void setEditPanelLayoutStrategy(ComponentLayoutStrategy editAssocLayout) 
	{
		this.editPnlLayout = editAssocLayout;
	}
	
	public void setEditPanelVisibility(ColumnVisibilityDef aEditPnlVisibility)
	{
		this.editPnlVisibility = aEditPnlVisibility;
	}
	
	private ListSelectionListener createTableSelectionListener()
	{
		return new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent ev) 
			{
				selectionChanged();
			}
		};
	}

    private void mouseClickInTable(MouseEvent event) {

    	// edit object on double click
        if ( event.getClickCount() > 1 && this.editAction.isEnabled()) {
        	this.editSelectedObject();
        }
    }
	
	private void selectionChanged()
	{
		this.editAction.setEnabled(  this.assocTbl.getSelectedRowCount() == 1);
		this.deleteAction.setEnabled( !this.isReadonly && isEditable &&  this.assocTbl.getSelectedRowCount() == 1);
	}

	/**
	 * set the attribute value of the Object in the Swing-Component 
	 * 
	 */
	public void readValueFromObject(TEditObj anObject)
	{
		this.masterobject = anObject;
		
		if( this.masterobject != null) {
		    assocList = (PersistentObjectList<TEditObj>) this.masterobject.getValue(this.displayedAssocInfo);
		} else {
		    assocList = new PersistentAssociationListModel<TEditObj>(displayedAssocInfo,new ArrayList<TEditObj>(),anObject, appManager);
		}
		
		this.newAction.setEnabled(this.masterobject !=null);
		this.tableModel.setListModel(assocList);
	}


	public void addToContainer(Container aContainer, Object constraints)
	{
		JPanel borderedPnl = new JPanel(new GridBagLayout());
		
		final GridBagConstraints 	constr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,1,1
				,1.0, 0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL
				, new Insets(0,0,0,0),0,0 );
		
		borderedPnl.add(createToolBar(), constr);
		constr.weighty=1.0; constr.fill=GridBagConstraints.BOTH;
		JScrollPane tblScrl = new JScrollPane(this.assocTbl);
		borderedPnl.add(tblScrl, constr);
		
		aContainer.add(borderedPnl, constraints);		
	}

	protected JToolBar createToolBar()
	{
		JToolBar editToolbar = new JToolBar();
		editToolbar.setFloatable(false);
		
		editToolbar.add(this.newAction);
		editToolbar.add(this.editAction);
		editToolbar.add(this.deleteAction);
		
		return editToolbar;
	}

	@SuppressWarnings("serial")
	private Action createEditAction(final JdyResourceLoader aResourceLoader)
	{

		return  new AbstractAction(aResourceLoader.getString("common.action.edit_selected"), aResourceLoader.getIcon(ApplicationIcons.EDIT_ICON))
		{
			public void actionPerformed(ActionEvent event) 
			{
				editSelectedObject();
			}
		};
	}
	
	protected void editSelectedObject()
	{
		TEditObj objToUpdate = tableModel.getObjectAt( assocTbl.getSelectedRow());

		try
		{
			ClassInfoEditPanel<TEditObj> editPnl = createUpdateEditPanel( displayedAssocInfo);
			editPnl.setBaseClassInfo( objToUpdate.getClassInfo());
			editPnl.setObjectToEdit(objToUpdate);
		
			editPnl.setName(displayedAssocInfo.getDetailClass().getInternalName());
			editPnl.setPanelReadOnly(this.isReadonly || !isEditable);
			if ( pnlMngr.displayDialog(editPnl, pnlMngr.res().getString("AssociationTablePanel.title.changeObject"), new Dimension(300,200), false, assocTbl) ) {
				
				editPnl.writeChangedValuesIntoObject();
				if( !appManager.isWritingDependentObject()) {
					appManager.saveObject(editPnl.getObjectToEdit(), null);
				} else {
					setIsDirty(true);
				}
				
			}
		} catch (JdyPersistentException ex)
		{
			pnlMngr.displayErrorDialog(assocTbl, ex.getLocalizedMessage(), ex);
		} catch (SaveException ex) {
			pnlMngr.displayErrorDialog(assocTbl, ex.getLocalizedMessage(), ex);
		} 		
	}
	
	protected void createNewAssociationObject()
	{
		try
		{
			if( !appManager.isWritingDependentObject() && this.masterobject.getPersistentState().isNew()) {

				pnlMngr.displayErrorDialog(assocTbl, pnlMngr.res().getString("AssociationTablePanel.msg.saveBeforeAddingSubobjectsAssociationTablePanel"), null);
			} else 
			{
				ClassInfoEditPanel< TEditObj> editPnl = createUpdateEditPanel(displayedAssocInfo);
				
				if ( pnlMngr.displayDialog(editPnl, pnlMngr.res().getString("AssociationTablePanel.title.newObject"), new Dimension(300,300), false, assocTbl) ) {
					
					editPnl.writeChangedValuesIntoObject();
					if( !appManager.isWritingDependentObject()) {
						appManager.saveObject(editPnl.getObjectToEdit(), null);
					} else {
						setIsDirty(true);
					}
					if( appManager.isWritingDependentObject() || !persistentListenerIsUsed ) {
						assocList.addObject(editPnl.getObjectToEdit());
					}
				}
			}
				
		} catch (JdyPersistentException ex)
		{
			pnlMngr.displayErrorDialog(assocTbl, ex.getLocalizedMessage(), ex);
		} catch (SaveException ex) {
			pnlMngr.displayErrorDialog(assocTbl, ex.getLocalizedMessage(), ex);
		}
	}
	

	
	@SuppressWarnings("serial")
	private Action createDeleteAction(final JdyResourceLoader aResourceLoader)
	{
		return  new AbstractAction( aResourceLoader.getString("common.action.delete"), aResourceLoader.getIcon(ApplicationIcons.DELETE_ICON))
		{
			public void actionPerformed(ActionEvent event) 
			{
				deleteSelectedObject(); 
			}

		};
	}
	
	private void deleteSelectedObject()
	{
		if ( JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(assocTbl, pnlMngr.res().getString("common.confirm.delete"), pnlMngr.res().getString("common.confirm.delete.title"), JOptionPane.YES_NO_OPTION))  
		{
			TEditObj objToDel = tableModel.getObjectAt( assocTbl.getSelectedRow());
			
			try
			{
				if( !appManager.isWritingDependentObject()) {
					appManager.deleteObject(objToDel, null);
					assocList.removeObject(objToDel);
				} else {
					if( objToDel.getPersistentState().isNew()) {
						assocList.removeObject(objToDel);
					} else {
						objToDel.getPersistentState().setMarkedAsDeleted(true);
						setIsDirty(true);
					}
				}
				
			} catch (JdyPersistentException ex)
			{
				pnlMngr.displayErrorDialog(assocTbl, ex.getLocalizedMessage(), ex);
			}
		}
	}
	

	@SuppressWarnings("serial")
	private Action createNewAction(final JdyResourceLoader aResourceLoader)
	{
		return  new AbstractAction(aResourceLoader.getString("common.action.new"), aResourceLoader.getIcon(ApplicationIcons.NEW_ICON))
		{
			public void actionPerformed(ActionEvent event) 
			{
				createNewAssociationObject(); 
			}
		};
	}
	
	private ClassInfo getConcreteSubclass(ClassInfo aSuperClass)
	{
		ClassInfo resultType = null;
		for (Iterator<ClassInfo> iterator =  aSuperClass.getAllSubclasses().iterator(); resultType == null && iterator.hasNext();)
		{
			ClassInfo curType = iterator.next();
			if( !curType.isAbstract()) {
				resultType = curType; 
			} else {
				resultType = getConcreteSubclass(curType);
			}
		}
		
		return resultType;
	}
	

	protected ClassInfoEditPanel<TEditObj>  createUpdateEditPanel(	 AssociationInfo anAssocInfo) throws JdyPersistentException 
	{
		SubclassEditPanel<TEditObj> editPnl = new SubclassAssociationObjectPanel<TEditObj>( appManager, anAssocInfo, masterobject
				, pnlMngr, workflowContext);
		if( this.editPnlComponentStrat != null) {
			editPnl.setCreateComponentStrategy(editPnlComponentStrat);
		}
		
		if( this.editPnlLayout != null) {
			editPnl.setLayouStrategy(editPnlLayout);
		}

		if( this.editPnlVisibility != null) {
			editPnl.setColumnVisibility(this.editPnlVisibility);
		}
		
		
		return editPnl;
	}
	

	private class AssociationColumnVisibility implements ColumnVisibilityDef
	{
		public boolean isAttributeVisible(AttributeInfo aAttrInfo)
		{
			return !displayedAssocInfo.getMasterClassReference().equals(aAttrInfo);
		}
		
		@Override
		public boolean isAssociationVisible(AssociationInfo aAttrInfo) 
		{
			return true;
		}
		
	}


	public void addLabelToContainer(String labelText, Container container, Object constraints)
	{
	}

	public boolean hasValidInput()
	{
		return true;
	}

	public boolean isDirty()
	{
		return isDirty;
	}

	public void readValueFromObject(Object anObject)
	{
		this.readValueFromObject((TEditObj) anObject);
		this.isDirty = false;
	}

	public void setEditable(boolean editable)
	{
		this.isEditable = editable;
		newAction.setEnabled(this.masterobject !=null && !this.isReadonly && editable);
		selectionChanged();
	}

	public boolean isReadonly()
	{
		return isReadonly;
	}
	
	public void setReadonly(boolean isReadonly)
	{
		this.isReadonly = isReadonly;
	}
	
	/**
	 * @param listener
	 */
	public void addInputChangedListener(InputChangedListener listener)
	{
		inputChangedListenerColl.add(listener);
	}

	public void removeInputChangedListener(InputChangedListener listener) {
		inputChangedListenerColl.remove(listener);
	}
	
	private void fireInputHasChanged()
	{
		if (inputChangedListenerColl!=null && inputChangedListenerColl.size()>0){
			for (InputChangedListener curListener : inputChangedListenerColl) {
				curListener.inputHasChanged();
			}
		}
	}

	public void writeValueIntoObject()
	{
		this.isDirty = false;
	}
	
	protected void setIsDirty(boolean aFlag)
	{
		isDirty = aFlag;
		fireInputHasChanged();	
	}
	
	public AssociationInfo getAssociationInfo()
	{
		return this.displayedAssocInfo;
	}
	
	public AttributeInfo getAttributeInfo()
	{
		return null;
	}
	
	private void addHeaderListener()
	{
		SortableHeaderRenderer<TEditObj> renderer = new SortableHeaderRenderer<TEditObj>(this.assocTbl.getTableHeader().getDefaultRenderer(), this.tableModel);		
		renderer.addSortListenerToHeader(this.assocTbl.getTableHeader());
		this.assocTbl.getTableHeader().setDefaultRenderer(renderer);

		 		
	}

	public void setNullable(boolean nullableFlag) {
		// Ignore
		
	}
	

	
}
