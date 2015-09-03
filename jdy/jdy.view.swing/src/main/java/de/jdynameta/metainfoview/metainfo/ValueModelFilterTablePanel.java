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
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.attribute.model.ChangeableQueryObjectListModel;
import de.jdynameta.metainfoview.filter.FilterPanel;
import de.jdynameta.metainfoview.metainfo.ValueModelTablePanel.EditObjectHandler;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.CompositeManagedPanel;
import de.jdynameta.view.base.PanelManager;

@SuppressWarnings("serial")
public class ValueModelFilterTablePanel<TEditObj extends ApplicationObj> extends CompositeManagedPanel
{
	private ValueModelTablePanel<TEditObj> objectTable;
	private FilterPanel<TEditObj> filterPnl;
	private ChangeableQueryObjectListModel<TEditObj> objectTableList;
	private final PanelManager pnlMngr;
	
	public ValueModelFilterTablePanel(final ClassInfo displayedClassInfo,
			ApplicationManager<TEditObj> anAppMngr, PanelManager aPanelManager
			, boolean listenToChanges)
			throws JdyPersistentException 
	{
		super();
	
		this.pnlMngr = aPanelManager;
		this.objectTableList = new ChangeableQueryObjectListModel<TEditObj>(anAppMngr, new  DefaultClassInfoQuery(displayedClassInfo), listenToChanges);
		this.objectTableList.refresh();
		this.objectTable = new ValueModelTablePanel<TEditObj>(displayedClassInfo, aPanelManager, anAppMngr, objectTableList)
		{
			@Override
			public void refreshList() throws JdyPersistentException 
			{
				DefaultClassInfoQuery query = new  DefaultClassInfoQuery(displayedClassInfo);
				
				ObjectFilterExpression expr = filterPnl.getFilterExpr();
				if( expr != null) {
					query.setFilterExpression(expr);
				}
				objectTableList.setQuery(query);
				filterPnl.resetMarker();
			}
		};
		this.objectTable.setMultiSelection(true);
		addSubPanel(objectTable);
		this.filterPnl = new FilterPanel<TEditObj>(anAppMngr, displayedClassInfo, aPanelManager.res(), null );
		setTitle("");
		initUi(displayedClassInfo, aPanelManager.res());

	}	
	
	@Override
	public String getName()
	{
		return "Filter_Table_" + ((this.objectTable == null) ? "" : this.objectTable.getDisplayedClassInfo().getInternalName()); 
	}
	
	private void initUi(ClassInfo displayedClassInfo, JdyResourceLoader resourceLoader)
	{
		this.setLayout(new BorderLayout());
		this.filterPnl.setBorder(BorderFactory.createTitledBorder(pnlMngr.res().getString("ValueModelFilterTablePanel.Filter")));
		this.add(this.filterPnl, BorderLayout.PAGE_START);
		this.objectTable.setBorder(BorderFactory.createTitledBorder(pnlMngr.res().getString("ValueModelFilterTablePanel.Objects")));
		this.add(this.objectTable, BorderLayout.CENTER);
	}

	public Action getRefreshAction()
	{
		return this.objectTable.getRefreshAction();
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
	
	public void setEditObjectHandler(EditObjectHandler<TEditObj> aEditObjectHandler)
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
