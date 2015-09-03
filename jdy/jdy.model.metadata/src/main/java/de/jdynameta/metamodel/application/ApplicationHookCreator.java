package de.jdynameta.metamodel.application;

import de.jdynameta.application.ApplicationManagerImpl.WorkflowApplicationHook;
import de.jdynameta.application.WorkflowException;
import de.jdynameta.application.WorkflowPersistenceHook;
import de.jdynameta.application.WorkflowPersistenceHookAdaptor;
import de.jdynameta.persistence.state.ApplicationObj;

public class ApplicationHookCreator
{
	public WorkflowApplicationHook<ApplicationObj> createAppHook()
	{
		ApplicationRepository repository = ApplicationRepository.getSingleton();
		
		WorkflowApplicationHook<ApplicationObj> appHook = new WorkflowApplicationHook<ApplicationObj>();
		
		WorkflowPersistenceHook<ApplicationObj> attributeHook = createAttributeInfoHook();
		appHook.setPersistenceHookForClass(repository.getClassInfoModelInfo(), createClassInfoHook());
		appHook.setPersistenceHookForClass(repository.getRepositoryModel(), createRepositorHook());
		appHook.setPersistenceHookForClass(repository.getAssociationInfo(), createAssociationHook());
		appHook.setPersistenceHookForClass(repository.getBlobTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getBooleanTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getCurrencyTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getFloatTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getLongTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getObjectReferenceInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getTextTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getTimeStampTypeModelInfo(), attributeHook);
		appHook.setPersistenceHookForClass(repository.getVarCharTypeModelInfo(), attributeHook);
		
		return appHook;
	}

	
	private WorkflowPersistenceHook<ApplicationObj> createAttributeInfoHook()
	{

		return new WorkflowPersistenceHookAdaptor<ApplicationObj>()
		{
			@Override
			public void beforeSave(ApplicationObj aEditedObject) throws WorkflowException
			{
				super.beforeSave(aEditedObject);
				aEditedObject.setValue("InternalName", replaceInvalidCharsInName((String)aEditedObject.getValue("Name")));
			}

		};
	}
	
	
	private WorkflowPersistenceHook<ApplicationObj> createClassInfoHook()
	{

		return new WorkflowPersistenceHookAdaptor<ApplicationObj>()
		{
			@Override
			public void beforeSave(ApplicationObj aEditedObject) throws WorkflowException
			{
				super.beforeSave(aEditedObject);
				aEditedObject.setValue("Internal", replaceInvalidCharsInName((String)aEditedObject.getValue("Name")));
			}

		};
	}

	private WorkflowPersistenceHook<ApplicationObj> createAssociationHook()
	{

		return new WorkflowPersistenceHookAdaptor<ApplicationObj>()
		{
			@Override
			public void beforeSave(ApplicationObj aEditedObject) throws WorkflowException
			{
				super.beforeSave(aEditedObject);
				aEditedObject.setValue("nameResource", replaceInvalidCharsInName((String)aEditedObject.getValue("Name")));
			}

		};
	}

	private WorkflowPersistenceHook<ApplicationObj> createRepositorHook()
	{

		return new WorkflowPersistenceHookAdaptor<ApplicationObj>()
		{
			@Override
			public void beforeSave(ApplicationObj aEditedObject) throws WorkflowException
			{
				super.beforeSave(aEditedObject);
				aEditedObject.setValue("applicationName", replaceInvalidCharsInName((String)aEditedObject.getValue("Name")));
				if(aEditedObject.getPersistentState().isNew()) {
					aEditedObject.setValue("appVersion", 1L);
					aEditedObject.setValue("closed", false);
				}
			}

		};
	}
	
	
	/**
	 * internal name may be used as java identifier or sql identifier therefore als non java chars are replaced by '_'
	 * @param aValue
	 * @return
	 */
	private Object replaceInvalidCharsInName(String aValue)
	{
		if( aValue != null && aValue.length() >0) {
			StringBuffer resultName = new StringBuffer(aValue.length());
			
			// append a '_' when first char is no java start char 
			if(!Character.isJavaIdentifierStart(aValue.charAt(0)) ) {
				resultName.append('_');
			}
			
			for( int i = 0; i < aValue.length(); i++) {
				
				Character curChar = aValue.charAt(i);
				if ( Character.isJavaIdentifierPart(curChar) && curChar != '$' ){
					resultName.append(curChar);
				} else {
					resultName.append('_');
				}
				
			}
			
			return resultName.toString();
		} else {
			return aValue;
		}
	}


}
