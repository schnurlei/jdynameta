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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.objectlist.QueryObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.attribute.model.ApplicationQueryObjectListModel;
import de.jdynameta.metainfoview.metainfo.table.ClassInfoColumnModel;
import de.jdynameta.metainfoview.metainfo.table.ClassInfoTableModel;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.metainfoview.metainfo.table.RendererCreationStrategy;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ValueModelTablePanel<TEditObj extends ApplicationObj> extends ManagedPanel
{
	private final ClassInfo displayedClassInfo;
	private final ClassInfoTableModel<TEditObj> tableModel;
	private final PanelManager panelManager;
	private final QueryObjectListModel<TEditObj> queryList;
	private final JTable objectTbl; 
	private EditObjectHandler<TEditObj> editHandler;
	private final ApplicationManager<TEditObj> appMngr;
	private final Action editAction;
	private final Action refreshAction;
	private JToolBar toolBar; 
	
	/**
	 * 
	 */
	public ValueModelTablePanel(ClassInfo aDisplayedClassInfo
					, PanelManager aPanelManger
					,ApplicationManager<TEditObj> anAppMngr
								) throws JdyPersistentException
								
	{
		this(aDisplayedClassInfo, aPanelManger, anAppMngr, new  DefaultClassInfoQuery(aDisplayedClassInfo));
	}
	
	/**
	 * 
	 */
	public ValueModelTablePanel(ClassInfo aDisplayedClassInfo
					, PanelManager aPanelManger
					,ApplicationManager<TEditObj> anAppMngr
					, ClassInfoQuery query	
								) throws JdyPersistentException
								
	{
		this(aDisplayedClassInfo, aPanelManger, anAppMngr, new ApplicationQueryObjectListModel<TEditObj>(anAppMngr, query));
	}
	
	/**
	 * 
	 */
	public ValueModelTablePanel(ClassInfo aDisplayedClassInfo
					, PanelManager aPanelManger
					,ApplicationManager<TEditObj> anAppMngr
					, QueryObjectListModel<TEditObj> aQueryList
					) throws JdyPersistentException
	{
		super();
	
		this.panelManager = aPanelManger;
		this.displayedClassInfo = aDisplayedClassInfo;
		this.appMngr = anAppMngr;
		this.setName(aDisplayedClassInfo.getInternalName());

		this.tableModel = new ClassInfoTableModel<TEditObj>(this.displayedClassInfo);	
		this.queryList = aQueryList;
		this.tableModel.setListModel(aQueryList);
		
		ClassInfoColumnModel colModel = new ClassInfoColumnModel(this.displayedClassInfo,anAppMngr, aPanelManger);
		colModel.restoreColumnStateFromConfig(this, aPanelManger.getPropertyManager());
		colModel.writeColumnChangesToConfig(this, aPanelManger.getPropertyManager());
		
		this.objectTbl = new JTable( tableModel,colModel);
		this.objectTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.objectTbl.setRowHeight(20);
		this.objectTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.objectTbl.addMouseListener(	new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent event) {
	            mouseClickInTable(event);
            }
    	});

		this.editAction = createEditObjectAction(aPanelManger.res());
		this.refreshAction = createRefreshObjectsAction(aPanelManger.res());

		this.objectTbl.getSelectionModel().addListSelectionListener(createSelectionListener());
		this.toolBar = createToolBar();
		
		initUi();	
		enableActions();		
		addHeaderListener();
	}

	
	public ClassInfo getDisplayedClassInfo() 
	{
		return displayedClassInfo;
	}
	
	/**
	 * 
	 * @param isMulteSelection
	 */
	public void setMultiSelection(boolean isMulteSelection)
	{
		if( isMulteSelection ) {
			this.objectTbl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			this.objectTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}
	
	public void setVisibilityDef(ColumnVisibilityDef visibilityDef) 
	{
		RendererCreationStrategy renderStrat = null;
		if( this.objectTbl.getColumnModel() instanceof ClassInfoColumnModel) {
			renderStrat = ((ClassInfoColumnModel) this.objectTbl.getColumnModel()).getRendererCreateStrategy();
		}
		ClassInfoColumnModel colModel = new ClassInfoColumnModel(this.displayedClassInfo,visibilityDef,appMngr, panelManager, renderStrat);
		colModel.restoreColumnStateFromConfig(this, panelManager.getPropertyManager());
		colModel.writeColumnChangesToConfig(this, panelManager.getPropertyManager());
		this.objectTbl.setColumnModel(colModel);
	}
	
	public void setRendererCreationStrategy( RendererCreationStrategy aRenderStrat ) 
	{
		ColumnVisibilityDef visibilityDef = null;
		if( this.objectTbl.getColumnModel() instanceof ClassInfoColumnModel) {
			visibilityDef = ((ClassInfoColumnModel) this.objectTbl.getColumnModel()).getVisibilityDef();
		}
		ClassInfoColumnModel colModel = new ClassInfoColumnModel(this.displayedClassInfo,visibilityDef,appMngr, panelManager, aRenderStrat);
		colModel.restoreColumnStateFromConfig(this, panelManager.getPropertyManager());
		colModel.writeColumnChangesToConfig(this, panelManager.getPropertyManager());
		this.objectTbl.setColumnModel(colModel);
	}
	
	
	public void setQuery(ClassInfoQuery query) throws JdyPersistentException
	{
		this.queryList.setQuery(query);
	}
	
	private void addHeaderListener()
	{
		SortableHeaderRenderer<TEditObj> renderer = new SortableHeaderRenderer<TEditObj>(this.objectTbl.getTableHeader().getDefaultRenderer(), this.tableModel);		
		renderer.addSortListenerToHeader(this.objectTbl.getTableHeader());
		this.objectTbl.getTableHeader().setDefaultRenderer(renderer);
	}
	
	/**
	 * Enable/Disable actions depending on the selection state
	 *
	 */
	private void enableActions() 
	{
		if( editHandler != null) {
			editAction.setEnabled( (getSelectedObjectCount() ==  1 && editHandler.isObjectEditable(getSelectedObject())));
		} else {
			editAction.setEnabled(getSelectedObjectCount() ==  1);
		}
	}
	
    private void mouseClickInTable(MouseEvent event) {

    	// edit object on double click
        if ( event.getClickCount() > 1 ) {
        	this.editSelectedObject();
        }
    }

	private ListSelectionListener createSelectionListener()
	{
		return new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				if( !event.getValueIsAdjusting() ) {
					
					enableActions();
					if( editHandler != null) {
						editHandler.selectionChanged();
					} 
				}
			}
		};
	}
	
	protected boolean isSelectedObjectEditable(TEditObj aSelectedObj)
	{
		return aSelectedObj != null;
	}
	
	private Action createEditObjectAction( JdyResourceLoader aResourceLoader)
	{
		AbstractAction tmpEditAction = new AbstractAction(aResourceLoader.getString("common.action.edit_selected"), aResourceLoader.getIcon(ApplicationIcons.EDIT_ICON))
		{
			public void actionPerformed(ActionEvent aE)
			{
				editSelectedObject();
			}
		};
		
		ActionUtil.addDescriptionsToAction(tmpEditAction, aResourceLoader.getString("common.action.edit_selected.short")
												, aResourceLoader.getString("common.action.edit_selected.long"));		
		return tmpEditAction;
	}
	
	public void setEditObjectActionName( String aName, Icon icon,  String aShortDesc, String aLongDesc)
	{
		ActionUtil.setActionName(this.editAction, aName, icon,  aShortDesc, aLongDesc);		

	}

	private Action createRefreshObjectsAction( JdyResourceLoader aResourceLoader)
	{
		AbstractAction tmpRefreshAction = new AbstractAction(aResourceLoader.getString("common.action.refresh"), aResourceLoader.getIcon(ApplicationIcons.REFRESH_ICON))
		{
			public void actionPerformed(ActionEvent aE)
			{
				try {
					refreshList();
				} catch (JdyPersistentException ex) {
					panelManager.displayErrorDialog(ValueModelTablePanel.this, ex.getLocalizedMessage(), ex);
				}
			}
		};
		
		ActionUtil.addDescriptionsToAction(tmpRefreshAction, aResourceLoader.getString("common.action.refresh.short")
												, aResourceLoader.getString("common.action.refresh.long"));		
		return tmpRefreshAction;
	}
	
	
	public void refreshList() throws JdyPersistentException
	{
		queryList.refresh();
	}
	
	/**
	 * Get first selected Object 
	 * @return
	 * @throws ProxyResolveException
	 */
	public TEditObj getSelectedObject() throws ProxyResolveException
	{
		return (this.objectTbl.getSelectedRow() >=0) ? this.tableModel.getObjectAt(this.objectTbl.getSelectedRow()) : null; 
	}
	
	/**
	 * Get all Selected Object. Set Table to multi selection mode to get more than one Object
	 * @return
	 * @throws ProxyResolveException
	 */
	public List<TEditObj> getAllSelectedObject() throws ProxyResolveException
	{
		ArrayList<TEditObj> selObjectColl = new ArrayList<TEditObj>();
		int[] selectedIdx = this.objectTbl.getSelectedRows();
		
		for (int i : selectedIdx) {
			selObjectColl.add(this.tableModel.getObjectAt(i));
		}
		return selObjectColl; 
	}
	
	public TEditObj getObjectAt(int aIndex)
	{
		return this.tableModel.getObjectAt(aIndex);
	}
	
	public int getObjectCount()
	{
		return this.tableModel.getRowCount();
	}
	
	
	public int getSelectedObjectCount()
	{
		return this.objectTbl.getSelectedRowCount();
	}
	
	public int getObjectIndex(TEditObj objToSearch)
	{
		int result = -1;
		for( int i = this.tableModel.getRowCount()-1; i >=0; i--) {

			if ( this.tableModel.getObjectAt(i).equals(objToSearch) ) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	public void setEditObjectHandler(EditObjectHandler<TEditObj> aEditObjectHandler)
	{
		this.editHandler = aEditObjectHandler;
		enableActions();
	}

	public Action getEditObjectAction()
	{
		return this.editAction;
	}
	
	public Action getRefreshAction() 
	{
		return refreshAction;
	}
	
	private void editSelectedObject()
	{
		if( editHandler != null) { 
			TEditObj modelFromDb =  getSelectedObject();
			if( modelFromDb != null) {
				editHandler.editObject(modelFromDb);
			}
		}
	}
	
	private void initUi()
	{
		this.setLayout(new BorderLayout());
	
		this.add(this.toolBar, BorderLayout.PAGE_START);
		this.add(new JScrollPane(this.objectTbl), BorderLayout.CENTER);
	}

	
	private JToolBar createToolBar()
	{
		JToolBar editToolbar = new JToolBar();
		editToolbar.setFloatable(false);
		editToolbar.setVisible(true);
		return editToolbar;
	}
	
	public void showToolbar(boolean isVisible)
	{
		this.toolBar.setVisible(isVisible);
	}

	public void addActionToToolbar(Action anActionToAdd)
	{
		this.toolBar.add(anActionToAdd);
	}
	
	public void addSeparatorToToolbar()
	{
		this.toolBar.addSeparator();
	}
	
	
	/**
	 * Set the title of the panel 
	 * @subclass use this Method to set the title of the Panel
	 */
	@Override
	public void setTitle(String aNewTitle) 
	{
		super.setTitle(aNewTitle);
	}

	public void selectRow(int rowNo) 
	{
		if (0 <= rowNo && rowNo < objectTbl.getRowCount()) {
			this.objectTbl.setRowSelectionInterval(rowNo, rowNo);

			final Rectangle selectedRect = objectTbl.getCellRect(rowNo, 0, true);
			if( selectedRect != null) {
				objectTbl.scrollRectToVisible(selectedRect);
			}

		} 
	}
	
	
	public static interface EditObjectHandler<TEditObj extends ValueObject>
	{
		public void editObject(TEditObj anObject);
		public void selectionChanged();
		public boolean isObjectEditable(TEditObj anObject);
	}
}
