public class GroovyValueObject extends de.jdynameta.base.value.defaultimpl.WrappedChangeableValueObject
{
		de.jdynameta.base.metainfo.ClassInfo classInfo;
	
		public GroovyValueObject(de.jdynameta.base.value.ValueObject aWrappedObject, de.jdynameta.base.metainfo.ClassInfo typeOfObj)
		{
			super(aWrappedObject)
			classInfo = typeOfObj;
		}
		
		def propertyMissing(String name) 
		{ 
	  		println 'Property Value ' +  name;	 
		
			def attrInfo;
			attrInfo = classInfo.getAttributeInfoForExternalName(name);
	
	    	if ( attrInfo != null  )
	    	{
	      		Object value =  getValue(attrInfo);
	       		return value;
	    	}
		    else
		    {
		      throw new MissingPropertyException(name, this.class);
		    }
		}
		
		def propertyMissing(String name, value) 
		{ 
	  		println 'Set property Value ' +  name + ' - ' + value ;	 

			def attrInfo;
			attrInfo = classInfo.getAttributeInfoForExternalName(name);
	
	    	if ( attrInfo != null  )
	    	{
	      		setValue(attrInfo, value);
	    	}
		    else
		    {
		      throw new MissingPropertyException(name, this.class);
		    }
		}
		
		
		def methodMissing(String name, args)
	  	 {
	  		println 'Method Value ' +  name;	 
			attrInfo = classInfo.getAttributeInfoForExternalName(name);
	
	    	if ( attrInfo != null  )
	    	{
	      		Object value =  getValue(attrInfo);
	       		return value;
	    	}
		    else
		    {
		      throw new MissingPropertyException(name, this.class);
		    }
		 }
	
}
