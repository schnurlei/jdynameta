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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.QueryObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.metainfoview.attribute.model.ApplicationQueryObjectListModel;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.view.base.PanelManager;

/**
 * Table panel that reads its data from a {@link QueryObjectListModel}
 * @author rs
 *
 * @param <TEditObj>
 */
@SuppressWarnings("serial")
public class SwingxQueryTablePanel<TEditObj extends ApplicationObj> extends SwingxTablePanel<TEditObj> 
{
	
	public SwingxQueryTablePanel(ClassInfo aDisplayedClassInfo,
			PanelManager aPanelManger, ApplicationManager<TEditObj> aPersistenceBridge)
			throws JdyPersistentException 
	{
		this(aDisplayedClassInfo, aPanelManger, aPersistenceBridge, new  DefaultClassInfoQuery(aDisplayedClassInfo));
	}

	public SwingxQueryTablePanel(ClassInfo aDisplayedClassInfo,
			PanelManager aPanelManger, ApplicationManager<TEditObj> aPersistenceBridge,
			ClassInfoQuery query) throws JdyPersistentException 
	{
		super(aDisplayedClassInfo, aPanelManger, aPersistenceBridge, new ApplicationQueryObjectListModel<TEditObj>(aPersistenceBridge, query));
	}

	
	public SwingxQueryTablePanel(ClassInfo aDisplayedClassInfo,
			PanelManager aPanelManger, ClassInfoAttrSource aAttrSource,
			QueryObjectListModel<TEditObj> aQueryList) throws JdyPersistentException 
	{
		super(aDisplayedClassInfo, aPanelManger, aAttrSource, aQueryList);
	}

	public void setQuery(ClassInfoQuery query) throws JdyPersistentException
	{
		((QueryObjectListModel<TEditObj>)getObjectList()).setQuery(query);
	}
	
	public void refreshList() throws JdyPersistentException
	{
		
		((QueryObjectListModel<TEditObj>)getObjectList()).refresh();
	}
	
}
