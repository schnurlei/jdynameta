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
package de.jdynameta.view.swingx.metainfo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTitledPanel;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.ApplicationIcons;
import de.jdynameta.metainfoview.attribute.model.ChangeableQueryObjectListModel;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.CompositeManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.util.ActionUtil;

@SuppressWarnings("serial")
public class SwingxFilterTablePanel<TEditObj extends ApplicationObj> extends CompositeManagedPanel
{
	private SwingxTablePanel<TEditObj> objectTable;
	private SwingxFilterPanel<TEditObj> filterPnl;
	private ChangeableQueryObjectListModel<TEditObj> objectTableList;
	private final PanelManager pnlMngr;
	private final Action refreshAction;
	
	public SwingxFilterTablePanel(
			final ClassInfo displayedClassInfo,
			ApplicationManager<TEditObj> anAppMngr, PanelManager aPanelManager, boolean listenToChanges)
			throws JdyPersistentException 
	{
		super();
	
		this.pnlMngr = aPanelManager;
		this.objectTableList = new ChangeableQueryObjectListModel<TEditObj>(anAppMngr, new  DefaultClassInfoQuery(displayedClassInfo), listenToChanges);
		this.objectTableList.refresh();
		this.objectTable = new SwingxTablePanel<TEditObj>(displayedClassInfo, aPanelManager, anAppMngr, objectTableList);
		this.refreshAction = createRefreshObjectsAction(pnlMngr.res());
		this.objectTable.setMultiSelection(true);
		addSubPanel(objectTable);
		this.filterPnl = new SwingxFilterPanel<TEditObj>(anAppMngr, displayedClassInfo, aPanelManager.res(), null );
		setTitle("");
		initUi(displayedClassInfo, aPanelManager.res());

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
					pnlMngr.displayErrorDialog(SwingxFilterTablePanel.this, ex.getLocalizedMessage(), ex);
				}
			}
		};
		
		ActionUtil.addDescriptionsToAction(tmpRefreshAction, aResourceLoader.getString("common.action.refresh.short")
												, aResourceLoader.getString("common.action.refresh.long"));		
		return tmpRefreshAction;
	}
	
	public Action getRefreshAction() 
	{
		return refreshAction;
	}
	
	public void refreshList() throws JdyPersistentException
	{
		DefaultClassInfoQuery query = new  DefaultClassInfoQuery(this.objectTable.getDisplayedClassInfo());
		
		ObjectFilterExpression expr = filterPnl.getFilterExpr();
		if( expr != null) {
			query.setFilterExpression(expr);
		}
		objectTableList.setQuery(query);
		filterPnl.resetMarker();
	}
	
	
	@Override
	public String getName()
	{
		return "Filter_Table_" + ((this.objectTable == null) ? "" : this.objectTable.getDisplayedClassInfo().getInternalName()); 
	}
	
	private void initUi(ClassInfo displayedClassInfo, JdyResourceLoader resourceLoader)
	{
		this.setLayout(new BorderLayout(0,5));
		
		JXTaskPane filterCollpsePane = new JXTaskPane();
		filterCollpsePane.setCollapsed(true);
		filterCollpsePane.add(this.filterPnl);
		filterCollpsePane.setTitle(pnlMngr.res().getString("ValueModelFilterTablePanel.Filter"));
//		JXTitledPanel titledPnl = new JXTitledPanel(pnlMngr.res().getString("ValueModelFilterTablePanel.Filter"),filterCollpsePane);
//		//titledPnl.setBorder(BorderFactory.createEmptyBorder());
//		titledPnl.setTitlePainter(new GlossPainter());
				
//		titledPnl.setRightDecoration(createFilterOpenCloseComponent(filterCollpsePane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION)));
		this.add(filterCollpsePane, BorderLayout.PAGE_START);

		JXTitledPanel tableTtlPnl = new JXTitledPanel(pnlMngr.res().getString("ValueModelFilterTablePanel.Objects"),objectTable);
		tableTtlPnl.setBorder(BorderFactory.createEmptyBorder());
		this.add(objectTable, BorderLayout.CENTER);
	}

	private JCheckBox createFilterOpenCloseComponent(Action action)
	{
		JCheckBox newChbx = new JCheckBox(action);
		newChbx.setOpaque(false);
		newChbx.setFocusable(false);
		newChbx.setText("Show");
		return newChbx;
	}
	

	public Action getEditObjectAction()
	{
		return this.objectTable.getEditObjectAction();
	}

	public List<TEditObj> getAllSelectedObject()
	{
		return this.objectTable.getAllSelectedObject();
	}
	
	@Override
	public void setTitle(String newTitle)
	{
		super.setTitle(newTitle);
	}
	
	public void insertObject(TEditObj aObject) 
	{
		objectTableList.insertObject(aObject);
		int indx = objectTable.getObjectIndex(aObject);
		objectTable.selectRow(indx);
	}
	
	public void removeObject(TEditObj aObject) 
	{
		objectTableList.removeObject(aObject);
	}

	public void updateObject(TEditObj aObject) 
	{
		objectTableList.updateObject(aObject);			
	}
	
	public void setEditObjectHandler(SwingxTablePanel.EditObjectHandler<TEditObj> aEditObjectHandler)
	{
		this.objectTable.setEditObjectHandler(aEditObjectHandler);
	}

	public int getSelectedObjectCount()
	{
		return this.objectTable.getSelectedObjectCount();
	}

	public TEditObj getSelectedObject()
	{
		return this.objectTable.getSelectedObject();
	}
	
	public TEditObj getObjectAt(int aIndex)
	{
		return this.objectTable.getObjectAt(aIndex);
	}

	public int getObjectCount()
	{
		return this.objectTable.getObjectCount();
	}

	public void showToolbar(boolean isVisible)
	{
		this.objectTable.showToolbar(isVisible);
	}

	public void addActionToToolbar(Action anActionToAdd)
	{
		this.objectTable.addActionToToolbar(anActionToAdd);
	}
	
	public void addSeparatorToToolbar()
	{
		this.objectTable.addSeparatorToToolbar();
	}
	
}
