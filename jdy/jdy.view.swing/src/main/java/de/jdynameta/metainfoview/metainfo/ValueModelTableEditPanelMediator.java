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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.metainfo.ValueModelTablePanel.EditObjectHandler;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.PersistentValueObject;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowApplicationStep;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowException;
import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.action.DefaultActionDescriptionWrapper;
import de.jdynameta.view.base.PanelDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.PanelMediator;
import de.jdynameta.view.panel.SaveException;
import de.jdynameta.view.util.ActionUtil;

/**
 * Mediator between  a {@link ValueModelFilterTablePanel} and a {@link ValueModeSavePanel} 
 * for the same classinfo
 * @author * <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 * 
 */
public class ValueModelTableEditPanelMediator<TEditObj extends ApplicationObj>
		extends PanelMediator
{
	private ValueModelFilterTablePanel<TEditObj> objectTable;
	private ValueModeSavePanel<TEditObj> objectEditPnl;
	private final PanelManager panelManager;
	private final ApplicationManager<TEditObj> applicationMngr;
	private final ClassInfo classInfo;
	private final WorkflowCtxt workflowContext;
	private Action actDelete;
	private Action actNew;
	private List<WorkflowAction<TEditObj>> workflowActColl; 
	private ComponentLayoutStrategy layoutStrategy;
	private ComponentCreationStrategy createComponentStrategy;
	private ArrayList<ActionDecorator> tablePanelActions; 
	
	private final boolean persistentListenerIsUsed = false;

	/**
	 * @throws JdyPersistentException
	 * @throws ProxyResolveException
	 * 
	 */
	public ValueModelTableEditPanelMediator(ClassInfo aClassInfo,
			ApplicationManager<TEditObj> anApplicationMngr, PanelManager aPanelManager,
			WorkflowCtxt aWorkflowContext) throws ProxyResolveException, JdyPersistentException
	{
		super();
		this.panelManager = aPanelManager;
		this.applicationMngr = anApplicationMngr;
		this.classInfo = aClassInfo;
		this.workflowContext = aWorkflowContext;
		this.workflowActColl = new ArrayList<WorkflowAction<TEditObj>>();
		enableActions();

	}
	
	public PanelManager getPanelManager()
	{
		return panelManager;
	}
	
	public ApplicationManager<TEditObj> getApplicationMngr()
	{
		return applicationMngr;
	}

	public void setLayoutStrategy(ComponentLayoutStrategy aLayoutStrategy) throws JdyPersistentException
	{
		this.layoutStrategy = aLayoutStrategy;
		if( this.objectEditPnl != null) {
			this.objectEditPnl.setLayouStrategy(this.layoutStrategy);
		}
	}
	
	public void setCreateComponentStrategy(	ComponentCreationStrategy aCreateComponentStrategy) throws JdyPersistentException 
	{
		this.createComponentStrategy = aCreateComponentStrategy;
		if( this.objectEditPnl != null) {
			this.objectEditPnl.setCreateComponentStrategy(this.createComponentStrategy);
		}
	}
	
	
	protected ValueModelFilterTablePanel<TEditObj> getObjectTableLazy() throws JdyPersistentException
	{
		if( this.objectTable == null ) {

			this.objectTable = createObjectTable();
			this.actDelete = createDeleteAction();
			this.actNew = createNewObjectAction();
			this.objectTable.setEditObjectHandler( createEditSelectedObjectHandler());
			this.objectTable.showToolbar(true);
			this.objectTable.addActionToToolbar(this.objectTable.getRefreshAction());
			this.objectTable.addSeparatorToToolbar();
			this.objectTable.addActionToToolbar(this.actNew);
			this.objectTable.addActionToToolbar(this.objectTable.getEditObjectAction());
			this.objectTable.addActionToToolbar(this.actDelete);
			enableActions();
		}
		return objectTable;
	}

	protected ValueModelFilterTablePanel<TEditObj> getObjectTable() 
	{
		return objectTable;
	}
	
	protected void enableActions()
	{
		if( objectTable != null) {
			int selObjectCount = objectTable.getSelectedObjectCount();

			if (actDelete != null) {
				actDelete.setEnabled(selObjectCount > 0);
			}
		}

		for (WorkflowAction<TEditObj> wrkflAct : workflowActColl) {
			wrkflAct.setWorkflowObject(objectEditPnl.getPersValueHolder().getEditedObject());
		}

	}
	
	protected Action getActDelete() 
	{
		return actDelete;
	}
	
	protected Action getActNew() 
	{
		return actNew;
	}
	
	protected List<WorkflowAction<TEditObj>> getWorkflowActColl() 
	{
		return workflowActColl;
	}

	protected ValueModelFilterTablePanel<TEditObj> createObjectTable() throws JdyPersistentException
	{
		return new ValueModelFilterTablePanel<TEditObj>(this.classInfo, this.applicationMngr, this.panelManager, persistentListenerIsUsed);
		
	}

	
	public void showTablePanel(ManagedPanel aParent) throws JdyPersistentException
	{
		String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(classInfo);
		getObjectTableLazy().setTitle(getPanelManager().res().getMessage("ValueModelTableEditPanelMediator.list", getPanelManager().res().getString(classNameProperty)));
		ArrayList<ActionDecorator> tablePanelActionList = getTablePanelActions();
		PanelDecorator panelDecorator = new PanelDecorator(null, objectTable, tablePanelActionList);
		panelDecorator.setCloseActionDecorator(new DefaultActionDescriptionWrapper(createWrappedCloseAction(), true, false,true,new String[] { "common.menu.File" }));
		panelManager.displayPanel(panelDecorator, aParent);
	}

	@SuppressWarnings("serial")
	protected Action createWrappedCloseAction()
	{
		return new AbstractAction(getPanelManager().res().getString("common.action.close"), null)
		{
			@Override
			public void actionPerformed(ActionEvent aE)
			{
				System.out.println("close");
			}
		};
	}
	 
	public ArrayList<ActionDecorator> getTablePanelActions() throws JdyPersistentException
	{
		if( tablePanelActions == null) {
			tablePanelActions = createObjectTableActions();
		}
		
		return tablePanelActions;
	}
	
	protected ArrayList<ActionDecorator> createObjectTableActions() throws JdyPersistentException
	{
		ArrayList<ActionDecorator> tablePanelActionList = new ArrayList<ActionDecorator>();
		tablePanelActionList.add(new DefaultActionDescriptionWrapper(getObjectTableLazy().getRefreshAction(), true, false, false,
				new String[] { "common.menu.File" }));
		tablePanelActionList.add( DefaultActionDescriptionWrapper.separator(true, false, false,new String[] { "common.menu.File" }));
		
		tablePanelActionList.add(new DefaultActionDescriptionWrapper(this.actNew, true, false, false,
				new String[] { "common.menu.File" }));
		tablePanelActionList.add(new DefaultActionDescriptionWrapper(getObjectTableLazy().getEditObjectAction(), true, false,
				false, new String[] { "common.menu.File" }));
		tablePanelActionList.add(new DefaultActionDescriptionWrapper(this.actDelete, true, false, false,
				new String[] { "common.menu.File" }));
		
		tablePanelActionList.add( DefaultActionDescriptionWrapper.separator(true, false, false,new String[] { "common.menu.File" }));
		return tablePanelActionList;
	}

	
	private EditObjectHandler<TEditObj> createEditSelectedObjectHandler()
	{
		return new ValueModelTablePanel.EditObjectHandler<TEditObj>()
		{

			public void editObject(TEditObj anObject)
			{
				try
				{
					setObjectInEditPanel(anObject);
				} catch (JdyPersistentException ex)
				{
					panelManager.displayErrorDialog(null, ex.getLocalizedMessage(), ex);
				}
			}

			public void selectionChanged()
			{
				enableActions();
			}

			public boolean isObjectEditable(TEditObj anObject) 
			{
				return ValueModelTableEditPanelMediator.this.isObjectEditable(anObject);
			}
		};
	}
	
	protected boolean isObjectEditable(TEditObj anObject) 
	{
		return true;
	}
	
	protected void setObjectInEditPanel(TEditObj anObject) throws JdyPersistentException
	{
		applicationMngr.refreshObject(anObject);
		showEditObjectPanel(anObject);
		
	}
	
	@SuppressWarnings("serial")
	private Action createDeleteAction()
	{
		AbstractAction deleteAction = new AbstractAction(panelManager.res().getString(
				"common.action.delete"), panelManager.res().getIcon(ApplicationIcons.DELETE_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				List<TEditObj> allObjects = objectTable.getAllSelectedObject();

				if (JOptionPane.showConfirmDialog(objectTable, panelManager.res().getString(
						"common.confirm.delete"), panelManager.res().getString(
						"common.confirm.delete.title"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
				{

					for (TEditObj curDelObject : allObjects)
					{
						try
						{
							if (curDelObject != null )
							{
								applicationMngr.deleteObject(curDelObject, null);
								if( !persistentListenerIsUsed) {
									getObjectTableLazy().removeObject(curDelObject);
								}
							}
						} catch (JdyPersistentException ex)
						{
							String message = ex.getLocalizedMessage();
							if( ex.getMessage().toLowerCase().contains("constraint")) {
								message = panelManager.res().getString("common.msg.deleteConstraintViolation");
							} 
							
							panelManager.displayErrorDialog(objectTable, message, ex);
						}
					}
				}
			}
		};

		ActionUtil
				.addDescriptionsToAction(deleteAction, panelManager.res().getString(
						"common.action.delete.short"), panelManager.res().getString(
						"common.action.delete.short"));

		return deleteAction;
	}

	@SuppressWarnings("serial")
	private Action createNewObjectAction()
	{
		AbstractAction newAction = new AbstractAction(panelManager.res().getString("common.action.new"),
				panelManager.res().getIcon(ApplicationIcons.NEW_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					createNewObject();
				} catch (JdyPersistentException ex)
				{
					panelManager.displayErrorDialog(null, ex.getLocalizedMessage(), ex);
				} catch (ObjectCreationException ex)
				{
					panelManager.displayErrorDialog(null, ex.getLocalizedMessage(), ex);
				}
			}
		};

		ActionUtil.addDescriptionsToAction(newAction, panelManager.res().getString(
				"common.action.new.short"), panelManager.res().getString("common.action.new.short"));

		return newAction;
	}

	/**
	 * Create new object add set it in the objectEditPnl
	 * @throws JdyPersistentException
	 * @throws ObjectCreationException
	 */
	protected void createNewObject() throws JdyPersistentException, ObjectCreationException
	{
		TEditObj newObject = applicationMngr.createObject(classInfo, null);
		if (newObject != null)
		{
			showEditObjectPanel(newObject);
		}
	}

	private void showEditObjectPanel(TEditObj objectObj) throws JdyPersistentException
	{
		ValueModeSavePanel< TEditObj> objectEditPnl = getObjectEditPnlLazy();

		if (objectEditPnl.isDirty())
		{
			if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(objectEditPnl, "Object changed. Cancel editing?",
					"Warning", JOptionPane.OK_CANCEL_OPTION))
			{
				openEditPanel(objectObj, objectEditPnl);
			}
		} else
		{
			openEditPanel(objectObj, objectEditPnl);
		}
	}

	private void openEditPanel(TEditObj objectObj, ValueModeSavePanel<TEditObj> objectEditPnl)
	{
		objectEditPnl.getPersValueHolder().setEditedObject(objectObj);
		ArrayList<ActionDecorator> tablePanelActionList = createEditPanelActions(objectObj,objectEditPnl);

		PanelDecorator panelDecorator = new PanelDecorator(null, objectEditPnl, tablePanelActionList);
		panelDecorator.setCloseActionDecorator(new DefaultActionDescriptionWrapper(null, true, false,true,new String[] { "common.menu.File" }));
		
		panelManager.displayPanel( panelDecorator, objectTable);
		enableActions();
	}

	protected ArrayList<ActionDecorator> createEditPanelActions(TEditObj objectObj, ValueModeSavePanel<TEditObj> objectEditPnl)
	{
		ArrayList<ActionDecorator> newPanelActions = new ArrayList<ActionDecorator>();
		newPanelActions.add(new DefaultActionDescriptionWrapper(objectEditPnl.getNewAction(), true, true,
				false, new String[] { "common.menu.File" }));
		newPanelActions.add(new DefaultActionDescriptionWrapper(objectEditPnl.getSaveAction(), true, true,
				false, new String[] { "common.menu.File" }));
		
		for (WorkflowAction<TEditObj> wrflwAct : this.workflowActColl)
		{
			newPanelActions.add(new DefaultActionDescriptionWrapper(wrflwAct, true, false,
					true, new String[] { "common.menu.Workflow" }));
			
		}
		newPanelActions.add( DefaultActionDescriptionWrapper.separator(true, false, false,new String[] { "common.menu.File" }));
		
		
		return newPanelActions;
	}
	
	protected List<WorkflowAction<TEditObj>> createActionForWorkflowSteps(TEditObj objectObj)
	{
		workflowActColl.clear();
		
		if( this.applicationMngr.getWorkflowManager() != null ) {
			
			ObjectList<WorkflowApplicationStep<TEditObj>> workflowSteps = this.applicationMngr.getWorkflowManager().getWorkflowStepsFor(objectObj, classInfo);
			
			if( workflowSteps !=  null ) {
				for (WorkflowApplicationStep<TEditObj> workflowAppStep : workflowSteps)
				{
					workflowActColl.add(createActionFor(workflowAppStep, objectObj));
				}
			}
		}
		
		return workflowActColl;
	}
	
	
	private WorkflowAction<TEditObj> createActionFor(final WorkflowApplicationStep<TEditObj> aWorkflowAppStep, TEditObj objectObj)
	{
		WorkflowAction<TEditObj> workflowAct = new WorkflowAction<TEditObj>(aWorkflowAppStep,panelManager);
		workflowAct.setWorkflowObject(objectObj);
		return workflowAct;
	}

	protected ValueModeSavePanel<TEditObj> getObjectEditPnlLazy() throws JdyPersistentException
	{
		// lazy initialization
		if (this.objectEditPnl == null)
		{
			createActionForWorkflowSteps(null);
			
			this.objectEditPnl = createObjectEditPanel();
		}
		return this.objectEditPnl;
	}

	protected ValueModeSavePanel< TEditObj> getObjectEditPnl()
	{
		return objectEditPnl;
	}
	
	@SuppressWarnings("serial")
	protected ValueModeSavePanel<TEditObj> createObjectEditPanel() throws JdyPersistentException
	{
		ValueModeSavePanel< TEditObj> newObjectEditPnl = new ValueModeSavePanel<TEditObj>(applicationMngr, null, panelManager,
				workflowContext)
		{
			@Override
			protected void saveObject() throws JdyPersistentException, SaveException
			{
				boolean isNew = getPersValueHolder().getEditedObject().getPersistentState().isNew();
				super.saveObject();
				if (isNew)
				{
					// add created object to tabll when it is not done by the persistent listener
					if ( getObjectTable() != null && !persistentListenerIsUsed) {
						getObjectTable().insertObject((getPersValueHolder().getEditedObject()));
					}
					enableActions();
				} else
				{
					if ( getObjectTable() != null && !persistentListenerIsUsed) {
						getObjectTable().updateObject(getPersValueHolder().getEditedObject());
					}
					enableActions();
				}
			};
			
			@Override
			public String getName()
			{
				return "Edit panel " + ((getBaseClassInfo() != null) ? getBaseClassInfo().getInternalName() : "");
			}
		};
		
		ComponentLayoutStrategy layoutStrategy = this.layoutStrategy;
		ComponentCreationStrategy creationStrategy = this.createComponentStrategy;
		if( this.layoutStrategy == null) {
			layoutStrategy = ( classInfo.attributeInfoSize() < 10 ) ? new ComponentLayoutStrategy(panelManager.res())
									: new MultiColumnLayoutStrategy(panelManager.res());
		}

		if( this.createComponentStrategy == null) {
			creationStrategy = new ComponentCreationStrategyDefault<TEditObj>(panelManager, applicationMngr);
		}
		newObjectEditPnl.setLayoutAndComponentStrategy(this.classInfo, layoutStrategy, creationStrategy);
		return newObjectEditPnl;
	}

	
	@SuppressWarnings("serial")
	public static class WorkflowAction<TWorkflowObj extends PersistentValueObject> extends AbstractAction
	{
		private final PanelManager pnlMangr;
		private WorkflowApplicationStep<TWorkflowObj> workflowAppStep;
		private TWorkflowObj workflowObj;
		
		public WorkflowAction(final WorkflowApplicationStep<TWorkflowObj> aWorkflowAppStep, PanelManager aPnlMangr)
		{
			super( aPnlMangr.res().getString(aWorkflowAppStep.getNameResource()) );
			this.pnlMangr = aPnlMangr;
			this.workflowAppStep = aWorkflowAppStep;
		}


		@Override
		public void actionPerformed(ActionEvent aE)
		{
			try
			{
				this.workflowAppStep.executeOn(workflowObj);
			} catch (WorkflowException ex)
			{
				pnlMangr.displayErrorDialog(null, ex.getLocalizedMessage(), ex);
			}
		}
		
		public void setWorkflowObject( TWorkflowObj aWorkflowObj)
		{
			this.workflowObj = aWorkflowObj;
			this.setEnabled(workflowAppStep.isPossible(aWorkflowObj));
			
		}
	}

	
}
