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
package de.jdynameta.metamodel.application;

import java.util.Collections;

import de.jdynameta.application.WorkflowApplicationStep;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowException;
import de.jdynameta.application.WorkflowManager;
import de.jdynameta.application.impl.AppPersistentGenericObj;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

public class ApplicationWorkflowManager implements WorkflowManager<AppPersistentGenericObj>
{
	public static final String REFERENCE_PANEL_CTXT = "ReferencePanel";
	
	private final WorkflowApplicationStep<AppPersistentGenericObj> createCloseRepStep;
	private final WorkflowApplicationStep<AppPersistentGenericObj> createOpenRepStep;


	public ApplicationWorkflowManager(final JdbcSchemaHandler aSchemmaHandler) throws JdyPersistentException
	{
		this.createCloseRepStep = createCloseRepositoryStep(aSchemmaHandler);
		this.createOpenRepStep = createOpenRepositoryStep();
		
	}
	
	
	@Override
	public ObjectList<WorkflowApplicationStep<AppPersistentGenericObj>> getWorkflowStepsFor(ValueObject aObj, ClassInfo aClassInfo)
	{
		if( aClassInfo.equals(getRepo().getRepositoryModel())) {
			
			ChangeableObjectList<WorkflowApplicationStep<AppPersistentGenericObj>>actColl 
			 	= new ChangeableObjectList<WorkflowApplicationStep<AppPersistentGenericObj>>();
			 actColl.addObject(createCloseRepStep);
			 actColl.addObject(createOpenRepStep);
			return actColl;
			
		} else {
			return null;
		}
	}
	
	@Override
	public boolean isAssociationEditable(ValueObject aObj, ClassInfo aClassInfo, AssociationInfo aAttributeInfo,
			WorkflowCtxt aContext)
	{
		return true;
	}	
	
	@Override
	public boolean isAttributeEditable(ValueObject aObj, ClassInfo aClassInfo, AttributeInfo aAttributeInfo,
			WorkflowCtxt aContext)
	{
		boolean editable = true;
		
		if( aContext != null && aContext.getName().equals(REFERENCE_PANEL_CTXT) && aClassInfo == getRepo().getObjectReferenceInfo()) {
			editable = !aAttributeInfo.getInternalName().equals("referencedClass");
		}
			
		
		return editable;
	}


	private ApplicationRepository getRepo()
	{
		return ApplicationRepository.getSingleton();
	}
	
	
	private WorkflowApplicationStep<AppPersistentGenericObj> createCloseRepositoryStep(final JdbcSchemaHandler  aSchemaHandler )
	{
		return new WorkflowApplicationStep<AppPersistentGenericObj>()
		{
			@Override
			public String getNameResource()
			{
				return "workflow.closeRepository";
			}
			
			@Override
			public boolean isPossible(ValueObject aAnObjectToProcess)
			{
				return true;
			}			
			
			public void executeOn(AppPersistentGenericObj anObjectToProcess) throws WorkflowException 
			{

				AppRepository repository = (AppRepository) anObjectToProcess.getWrappedValueObject();
				RepositoryWorkflowManager.closeRepository(aSchemaHandler, repository);
			}

		};
	}

	private WorkflowApplicationStep<AppPersistentGenericObj> createOpenRepositoryStep()
	{
		return new WorkflowApplicationStep<AppPersistentGenericObj>()
		{
			@Override
			public String getNameResource()
			{
				return "workflow.openRepository";
			}
			
			@Override
			public boolean isPossible(ValueObject aAnObjectToProcess)
			{
				return true;
			}			
			
			public void executeOn(AppPersistentGenericObj anObjectToProcess) throws WorkflowException 
			{

				AppRepository repository = (AppRepository) anObjectToProcess.getWrappedValueObject();
				RepositoryWorkflowManager.openRepository(repository);
			}

		};
	}
	
	
	@Override
	public boolean isAttributeVisible(AttributeInfo anAttrInfo,
			ClassInfo aClassInfo, WorkflowCtxt context) 
	{
		if( anAttrInfo instanceof ObjectReferenceAttributeInfo) {
				
			return false;
		} else {
			return true;
		}
	}


	@Override
	public boolean isAssociationVisible(AssociationInfo aAttrInfo,
			ClassInfo aClassInfo, WorkflowCtxt context) 
	{
		return true;
	}
	

	
}
