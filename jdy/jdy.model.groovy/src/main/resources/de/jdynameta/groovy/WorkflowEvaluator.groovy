public class Tester implements de.jdynameta.groovy.ExecuteWorkflow 
{
	de.jdynameta.base.metainfo.ClassInfo classInfo;
	de.jdynameta.base.value.ValueObject valueModel;

	public void setValueModel(de.jdynameta.base.value.ValueObject objToHandle, de.jdynameta.base.metainfo.ClassInfo typeOfObj)
	{
		valueModel = objToHandle; // new GroovyValueObject(objToHandle, typeOfObj);
		classInfo = typeOfObj;
	}
	
	def propertyMissing(String aName) 
	{ 
		println 'Get property Value ' +  aName ;	 
	
		def attrInfo = classInfo.getAttributeInfoForExternalName(aName);
	
    	if ( attrInfo != null  )
    	{
      		Object value =  valueModel.getValue(attrInfo);
       		return value;
    	}
	    else
	    {
	      throw new MissingPropertyException(aName, this.class);
	    }
	}	
	
	def propertyMissing(String aName, value) 
	{ 
 		println 'Set property Value ' +  aName + ' - ' + value ;	 

		def attrInfo = classInfo.getAttributeInfoForExternalName(aName);
	
    	if ( attrInfo != null  )
    	{
      		valueModel.setValue(attrInfo, value);
    	}
	    else
	    {
	      throw new MissingPropertyException(aName, this.class);
	    }
	}
	
    def methodMissing(String methodName, args){
   
        if(args.length == 1 && methodName.startsWith('set')){
	 		def attrInfo = classInfo.getAttributeInfoForExternalName(methodName.substring(3,  methodName.length() ));
		   	if ( attrInfo != null  )
		    	{
		      		valueModel.setValue(attrInfo, args[0]);
		    	}
			    else
			    {
			      throw new MissingPropertyException(methodName.substring(3,  methodName.length() ), this.class);
			    }       
	     }
	}	
	
	public void evaluate(String expression)
	{
 		def shell = new GroovyShell()
	    def workflowClosure = (Closure) shell.evaluate("{->" + expression +"}")

		workflowClosure.delegate = this

   		workflowClosure()
		
		
	}
		
}
