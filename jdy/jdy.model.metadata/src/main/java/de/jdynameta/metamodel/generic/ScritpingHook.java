package de.jdynameta.metamodel.generic;

import de.jdynameta.application.WorkflowPersistenceHookAdaptor;
import de.jdynameta.persistence.state.ApplicationObj;

public class ScritpingHook extends WorkflowPersistenceHookAdaptor<ApplicationObj>
{
	private ScriptSource scriptSource;
	
	public ScritpingHook(ScriptSource aClassInfo) 
	{
		this.scriptSource = aClassInfo;
	}
	
	public ScriptSource getScriptSource()
	{
		return scriptSource;
	}
	
	public static interface ScriptSource
	{

		public String getBeforeSaveScript();
		
	}
}