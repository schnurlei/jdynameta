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
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.DefaultObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.DeselectVetoException;
import de.jdynameta.view.panel.SaveException;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class SubclassEditPanel<TEditObj extends ApplicationObj>  extends ClassInfoEditPanel<TEditObj>
{
	private final ValueModelEditPanel<TEditObj> editPnl;
	private final JComboBox subclassCmbx;
	
	
	/**
	 * 
	 */
	public SubclassEditPanel( ApplicationManager<TEditObj> anAppManager,  final PanelManager aPanelManger, WorkflowCtxt aWorkflowContext) 
		throws JdyPersistentException
	{
		super();
		this.editPnl = new ValueModelEditPanel<TEditObj>( null, aPanelManger, anAppManager, aWorkflowContext);
		this.subclassCmbx = new JComboBox();
		this.subclassCmbx.addActionListener(createSublcassComboListener(anAppManager, aPanelManger));
		this.subclassCmbx.setRenderer(createClassInfoTextRenderer());
		this.initializePanel();
		
		this.editPnl.setSavePanelListener(new SavePanelListener()
		{
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
	
	protected ValueModelEditPanel<TEditObj> getEditPnl()
	{
		return editPnl;
	}
	
	protected ApplicationManager<TEditObj> getAppManager()
	{
		return this.editPnl.getAppManager();
	}

	private ActionListener createSublcassComboListener( final ApplicationManager<TEditObj> aPersistenceBridge, final PanelManager aPanelManger)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent aEvent)
			{
				try
				{
					ClassInfo newClass = (ClassInfo)subclassCmbx.getSelectedItem();
					setDisplayedClassInfo(newClass);
				} catch (JdyPersistentException ex)
				{
					aPanelManger.displayErrorDialog(SubclassEditPanel.this, ex.getMessage(), ex);
				} catch (ObjectCreationException ex)
				{
					aPanelManger.displayErrorDialog(SubclassEditPanel.this, ex.getMessage(), ex);
				}
				
			}

		};
	}

	protected ListCellRenderer createClassInfoTextRenderer() 
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
					if( value != null && value instanceof ClassInfo) {
						String attrNameProperty = editPnl.getLayoutStrategy().getPropertyGenerator().getPropertyNameFor((ClassInfo) value);
						((JLabel)resultComp).setText(editPnl.getPanelManager().res().getString(attrNameProperty));															
					} else {
						((JLabel)resultComp).setText("");															
					}
				}
				return resultComp;																			
			}

		};
	}
	
	private void setDisplayedClassInfo( ClassInfo newClass)
			throws JdyPersistentException, ObjectCreationException
	{
		editPnl.setBaseClassInfo(newClass);
		editPnl.setObjectToEdit(createNewObject(this.editPnl.getAppManager(), newClass));
	}
	
	protected TEditObj createNewObject( ApplicationManager<TEditObj> anAppMngr, ClassInfo newClass)
			throws ObjectCreationException
	{
		return anAppMngr.createObject(newClass, null);
	}
	
	private void initializePanel()
	{	
		this.setLayout(new BorderLayout());
		this.add(this.subclassCmbx, BorderLayout.PAGE_START);
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
	
	public void setBaseClassInfo(ClassInfo aBaseClassInfo) throws JdyPersistentException 
	{
		if( aBaseClassInfo.hasSubClasses()) {
			this.subclassCmbx.setVisible(true);
			ChangeableObjectList<ClassInfo> sublcasses = new ChangeableObjectList<ClassInfo>();
			addConcreteSublcasses(aBaseClassInfo, sublcasses);
			this.subclassCmbx.setModel(new SwingComboboxModelMapping<ClassInfo>(new DefaultObjectListModel<ClassInfo>(sublcasses)));
			this.subclassCmbx.setSelectedIndex(0);
		} else {
			this.subclassCmbx.setVisible(false);
			try {
				this.setDisplayedClassInfo( aBaseClassInfo );
			} catch (ObjectCreationException ex)
			{
				throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
			}
		}
		
		if ( getTitle() == null || getTitle().length() == 0 ) {
			String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(aBaseClassInfo);
			this.setTitle(this.editPnl.getResourceLoader().getString(classNameProperty));
		}
	}

	private void addConcreteSublcasses(ClassInfo aBaseClassInfo, ChangeableObjectList<ClassInfo> result )
	{
		if( aBaseClassInfo.hasSubClasses()) 
		{
			for (Iterator<ClassInfo> iterator = aBaseClassInfo.getAllSubclasses().iterator(); iterator.hasNext();)
			{
				addConcreteSublcasses( iterator.next(),result );
			} 
		} else {
			if( !aBaseClassInfo.isAbstract() ) {
				result.addObject(aBaseClassInfo);
			}
		}
	}
	
	
	
	@Override
	public void discardChanges()
	{
		
	}
	
	private void enableActions()
	{
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
	public void save() throws SaveException
	{
		// TODO Auto-generated method stub
		
	}

	public TEditObj getObjectToEdit()
	{
		return this.editPnl.getObjectToEdit();
	}
	
	@Override
	public void setObjectToEdit(TEditObj aValueObject)
	{
		this.editPnl.setObjectToEdit(aValueObject);
		
	}

	public void writeChangedValuesIntoObject() throws SaveException
	{
		this.editPnl.writeChangedValuesIntoObject();
	}

	public ClassInfo getDisplayedClassInfo()
	{
		return editPnl.getDisplayedClassInfo();
	}	
}
