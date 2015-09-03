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
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.DefaultObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.metainfo.PersistValueObjectHolder.ModelListener;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.DeselectVetoException;
import de.jdynameta.view.panel.SaveException;
import de.jdynameta.view.panel.SavePanel;
import de.jdynameta.view.util.ActionUtil;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ValueModeSavePanel<TEditObj extends ApplicationObj>  extends SavePanel
{
	private final ValueModelEditPanel<TEditObj> editPnl;
	private final JComboBox subclassCmbx;
	private final PersistentValueObjectHolderImpl<TEditObj> persValueHolder;
	private final JdyResourceLoader resourceLoader;
	private final Action saveAction;
	private final Action newAction;
	private final Action cloneAction;
	private final ApplicationManager<TEditObj> appMngr;
	
	
	public ValueModeSavePanel( ApplicationManager<TEditObj> aPersistenceBridge
			, ClassInfo aBaseClassInfo, PanelManager aPanelManger) 
	throws JdyPersistentException
	{
		this(aPersistenceBridge	,  aBaseClassInfo, aPanelManger, null);
	}
	
	/**
	 * 
	 */
	public ValueModeSavePanel( ApplicationManager<TEditObj> aPersistenceBridge
									, ClassInfo aBaseClassInfo, PanelManager aPanelManger, WorkflowCtxt aWorkflowContext) 
		throws JdyPersistentException
	{
		super();
		this.appMngr = aPersistenceBridge;
		this.persValueHolder = new PersistentValueObjectHolderImpl<TEditObj>();
		this.persValueHolder.addModelListener(createModelListener());
		this.editPnl = new ValueModelEditPanel<TEditObj>( null, aPanelManger, appMngr, aWorkflowContext);
		this.subclassCmbx = new JComboBox();
		this.resourceLoader = aPanelManger.res();
		this.initializePanel();
		this.saveAction = createSaveAction();
		this.newAction = createNewAction();
		this.cloneAction = createCloneAction();
		if( aBaseClassInfo != null) {
			setBaseClassInfo(aBaseClassInfo);
		}
		
		this.editPnl.setSavePanelListener(new SavePanelListener(){

			public void isDirtyChanged()
			{
				fireIsDirtyChanged();
				enableActions();
			}

			public void canCloseChanged()
			{
				fireCanCloseChanged();
			}
			
		});
		
		enableActions();
	}
	
	private void initializePanel()
	{	
		this.setLayout(new BorderLayout());
		this.add(this.editPnl, BorderLayout.CENTER);
	}
	
	
	public void setPanelReadOnly(boolean isPanelReadOnly) 
	{
		this.editPnl.setPanelReadOnly(isPanelReadOnly);
	}
	
	
	public void setWorkflowContext(WorkflowCtxt aWorkflowContext) throws JdyPersistentException 
	{
		this.editPnl.setWorkflowContext(aWorkflowContext);
	}
	
	private ModelListener createModelListener()
	{
		return new ModelListener()
		{
			public void editedObjectReplaced()
			{
				editPnl.setObjectToEdit(persValueHolder.getEditedObject());
				enableActions();
			}
		};
	}
	public void setLayoutAndComponentStrategy(ClassInfo aClassInfo, ComponentLayoutStrategy aLayouStrategy, 	ComponentCreationStrategy aCreateComponentStrategy) throws JdyPersistentException
	{
		this.editPnl.setLayoutAndComponentStrategy(aClassInfo, aLayouStrategy, aCreateComponentStrategy);
		if ( getTitle() == null || getTitle().length() == 0 ) {
			String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(aClassInfo);
			this.setTitle(resourceLoader.getString(classNameProperty));
		}
	}
	
	
	public void setLayouStrategy(ComponentLayoutStrategy aLayouStrategy) throws JdyPersistentException
	{
		this.editPnl.setLayouStrategy(aLayouStrategy);
	}
	
	public void setCreateComponentStrategy(	ComponentCreationStrategy aCreateComponentStrategy) throws JdyPersistentException 
	{
		this.editPnl.setCreateComponentStrategy( aCreateComponentStrategy );
	}
	
	public void setColumnVisibility(ColumnVisibilityDef columnVisibility) throws JdyPersistentException 
	{
		this.editPnl.setColumnVisibility( columnVisibility );
	}
	
	public ClassInfo getBaseClassInfo()
	{
		return (this.editPnl != null) ?  this.editPnl.getDisplayedClassInfo() : null;
	}
	
	public void setBaseClassInfo(ClassInfo aBaseClassInfo) throws JdyPersistentException 
	{
		if( aBaseClassInfo.hasSubClasses()) {
			this.subclassCmbx.setVisible(true);
			this.subclassCmbx.setModel(new SwingComboboxModelMapping<ClassInfo>(new DefaultObjectListModel<ClassInfo>(aBaseClassInfo.getAllSubclasses())));
		} else {
			this.subclassCmbx.setVisible(false);
		}
		
		this.editPnl.setBaseClassInfo( aBaseClassInfo );
		if ( getTitle() == null || getTitle().length() == 0 ) {
			String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(aBaseClassInfo);
			this.setTitle(resourceLoader.getString(classNameProperty));
		}
	}
	
	
	
	public PersistentValueObjectHolderImpl<TEditObj> getPersValueHolder()
	{
		return persValueHolder;
	}
	
	private void enableActions()
	{
		saveAction.setEnabled(!this.editPnl.isPanelReadOnly() &&  (persValueHolder.getEditedObject() != null && this.isDirty()));
		newAction.setEnabled(!this.editPnl.isPanelReadOnly() && !this.isDirty());
	}
	

	private Action createNewAction()
	{
		Action tmpNewAction = new AbstractAction(resourceLoader.getString("common.action.new"), resourceLoader.getIcon(ApplicationIcons.NEW_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					createNewObject();
				} catch (ObjectCreationException perExcp) {
					JOptionPane.showMessageDialog(ValueModeSavePanel.this, perExcp.getLocalizedMessage());
				}
			}
		};
		ActionUtil.addDescriptionsToAction(tmpNewAction, resourceLoader.getString("common.action.new.short")
				, resourceLoader.getString("common.action.new.long"));	
		
		return tmpNewAction;
	}

	private Action createSaveAction()
	{
		Action tmpSaveAction = new AbstractAction(resourceLoader.getString("common.action.save"), resourceLoader.getIcon(ApplicationIcons.SAVE_ICON)) 
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					saveObject();
				} catch (JdyPersistentException perExcp) {

					JOptionPane.showMessageDialog(ValueModeSavePanel.this, perExcp.getLocalizedMessage());
				} catch (SaveException ex) {
					JOptionPane.showMessageDialog(ValueModeSavePanel.this, ex.getLocalizedMessage());
				}	
			}
		};
		ActionUtil.addDescriptionsToAction(tmpSaveAction, resourceLoader.getString("common.action.save.short")
				, resourceLoader.getString("common.action.save.long"));	
		
		return tmpSaveAction;
	}

	private Action createCloneAction()
	{
		Action tmpCloneAction = new AbstractAction(resourceLoader.getString("common.action.clone"), resourceLoader.getIcon(ApplicationIcons.COPY_ICON))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					cloneObject();
				} catch (ObjectCreationException perExcp) {
					JOptionPane.showMessageDialog(ValueModeSavePanel.this, perExcp.getLocalizedMessage());
				}
			}
		};
		ActionUtil.addDescriptionsToAction(tmpCloneAction, resourceLoader.getString("common.action.clone.short")
				, resourceLoader.getString("common.action.clone.long"));	
		
		return tmpCloneAction;
	}
	

	protected void saveObject() throws JdyPersistentException, SaveException
	{
		editPnl.writeChangedValuesIntoObject();
		appMngr.saveObject(persValueHolder.getEditedObject(), null);
		persValueHolder.setEditedObject(persValueHolder.getEditedObject());
	}

	protected void createNewObject() throws ObjectCreationException
	{
		persValueHolder.setEditedObject(appMngr.createObject(editPnl.getDisplayedClassInfo(), null));
	}

	protected void cloneObject() throws ObjectCreationException
	{
		persValueHolder.setEditedObject(appMngr.cloneObject(persValueHolder.getEditedObject()));
	}
	
	
	public void refresh() throws JdyPersistentException
	{
		if( persValueHolder.getEditedObject() != null) {
			if( !persValueHolder.getEditedObject().getPersistentState().isNew() && !persValueHolder.getEditedObject().getPersistentState().isDirty()) {
				appMngr.refreshObject(persValueHolder.getEditedObject());
				persValueHolder.setEditedObject(persValueHolder.getEditedObject());
			}
		}
	}
	
	
	@Override
	public void closed() 
	{
		super.closed();
		persValueHolder.setEditedObject(null);
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.view.panel.SavePanel#save()
	 */
	@Override
	public void save() throws SaveException
	{
		try
		{
			saveObject();
		} catch (JdyPersistentException excp)
		{
			throw new SaveException(excp);
		}
		
	}

	@Override
	public void discardChanges() 
	{
		this.editPnl.discardChanges();
		
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.view.panel.SavePanel#isDirty()
	 */
	@Override
	public boolean isDirty() throws DeselectVetoException
	{
		return this.editPnl.isDirty();
	}

	@Override
	public boolean canClose() throws DeselectVetoException
	{
		return this.editPnl.canClose();
	}

	/**
	 * @param o1
	 * @return
	 */
	protected String getTextForClassInfo(ClassInfo aClassInfo) 
	{
		return aClassInfo.getInternalName();
	}

	protected ListCellRenderer createAttributeTextRenderer() 
	{
		return new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(
				JList list,	Object value, int index,
				boolean isSelected, boolean cellHasFocus) 
			{
				Component resultComp = super.getListCellRendererComponent( list, value, index
																			,isSelected, cellHasFocus);
				if( resultComp instanceof JLabel ) {
					if( value != null) {
						((JLabel)resultComp).setText(getTextForClassInfo((ClassInfo)value));															
					} else {
						((JLabel)resultComp).setText("");															
					}
				}
				return resultComp;																			
			}

		};
	}


	public Action getNewAction()
	{
		return this.newAction;
	}


	public Action getSaveAction()
	{
		return this.saveAction;
	}
	
	public Action getCloneAction()
	{
		return this.cloneAction;
	}
	
}
