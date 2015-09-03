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

import de.jdynameta.application.WorkflowApplicationStep;
import de.jdynameta.application.WorkflowCtxt;
import de.jdynameta.application.WorkflowException;
import de.jdynameta.application.WorkflowManager;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

public class RepositoryWorkflowManager implements WorkflowManager<GenericValueObjectImpl>
{
	public static final String REFERENCE_PANEL_CTXT = "ReferencePanel";
	
	private final WorkflowApplicationStep<GenericValueObjectImpl> closeRepoStep;
	private final WorkflowApplicationStep<GenericValueObjectImpl> openRepoStep;


	public RepositoryWorkflowManager(final JdbcSchemaHandler aSchemmaHandler) throws JdyPersistentException
	{
		this.closeRepoStep = createCloseRepositoryStep(aSchemmaHandler);
		this.openRepoStep = createOpenRepositoryStep();
	}
	
	
	@Override
	public ObjectList<WorkflowApplicationStep<GenericValueObjectImpl>> getWorkflowStepsFor(ValueObject aObj, ClassInfo aClassInfo)
	{
		if( aClassInfo.equals(getRepo().getRepositoryModel())) {
			return new DefaultObjectList<WorkflowApplicationStep<GenericValueObjectImpl>>(closeRepoStep, openRepoStep);
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
	
	
	private WorkflowApplicationStep<GenericValueObjectImpl> createCloseRepositoryStep(final JdbcSchemaHandler  aSchemaHandler )
	{
		return new WorkflowApplicationStep<GenericValueObjectImpl>()
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
			
			public void executeOn(GenericValueObjectImpl anObjectToProcess) throws WorkflowException 
			{
				AppRepository repository = (AppRepository)anObjectToProcess;
				closeRepository(aSchemaHandler, repository);
			}

		};
	}

	
	private WorkflowApplicationStep<GenericValueObjectImpl> createOpenRepositoryStep()
	{
		return new WorkflowApplicationStep<GenericValueObjectImpl>()
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
			
			public void executeOn(GenericValueObjectImpl anObjectToProcess) throws WorkflowException 
			{
				AppRepository repository = (AppRepository)anObjectToProcess;
				openRepository(repository);
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
	
	public static void closeRepository(final JdbcSchemaHandler aSchemaHandler, AppRepository repository)
			throws WorkflowException
	{
		try
		{
			if( aSchemaHandler.existsSchema(repository.getApplicationName())) {
				aSchemaHandler.deleteSchema(repository.getApplicationName());
			}

			MetaRepositoryCreator repCreator = new MetaRepositoryCreator(null);
			ClassRepository metaRep = repCreator.createMetaRepository(repository);
			aSchemaHandler.createSchema(repository.getApplicationName(), metaRep);
			repository.setClosed(true);
		} catch (JdyPersistentException e1)
		{
			throw new WorkflowException(e1.getLocalizedMessage(), e1);
		}
	}

	public static void openRepository(AppRepository repository)
			throws WorkflowException
	{
		repository.setClosed(false);
		repository.setAppVersion(repository.getAppVersion()+1);
	}

}
