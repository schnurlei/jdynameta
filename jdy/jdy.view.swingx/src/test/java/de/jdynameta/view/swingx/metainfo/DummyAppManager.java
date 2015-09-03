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

import java.util.List;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.application.ApplicationManager;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowManager;
import de.jdynameta.testcommon.testdata.TestDataCreator;

@SuppressWarnings("serial")
public class DummyAppManager<TEditObj extends ApplicationObj> implements ApplicationManager<TEditObj> 
{
	private TestDataCreator<TEditObj> dataCreator;
	
	
	
	public DummyAppManager(TestDataCreator<TEditObj> aDataCreator) 
	{
		super();
		this.dataCreator = aDataCreator;
	}

	@Override
	public void addListener(ClassInfo aClassInfo,
			de.jdynameta.base.model.PersistentObjectReader.PersistentListener<TEditObj> aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(	ClassInfo aClassInfo,de.jdynameta.base.model.PersistentObjectReader.PersistentListener<TEditObj> aListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AttributeInfo> getDisplayAttributesFor(ClassInfo aClassInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(TEditObj editedObject, WorkflowCtxt aContext)
			throws JdyPersistentException 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void refreshObject(TEditObj editedObject)
			throws JdyPersistentException 
	{
		
	}

	@Override
	public TEditObj createObject(ClassInfo aClassInfo, WorkflowCtxt aContext) throws ObjectCreationException 
	{
		return null;
	}

	@Override
	public ObjectList<TEditObj> loadObjectsFromDb(ClassInfoQuery query)	throws JdyPersistentException 
	{
		ObjectListModel<TEditObj> allData =  dataCreator.createTestData(query.getResultInfo(), 200, query);
		return allData;
	}

	@Override
	public void deleteObject(TEditObj objToDel, WorkflowCtxt aContext)
			throws JdyPersistentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WorkflowManager<TEditObj> getWorkflowManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeConnection() throws JdyPersistentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWritingDependentObject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TEditObj cloneObject(TEditObj editedObject)
			throws ObjectCreationException {
		// TODO Auto-generated method stub
		return null;
	}
	
}