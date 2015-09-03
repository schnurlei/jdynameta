package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppTimestampType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppTimestampType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppTimestampType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppTimestampType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppTimestampType (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the isDatePartUsed
	 * @generated
	 * @return get the isDatePartUsed
	 */
	public Boolean getIsDatePartUsedValue() 
	{
		return (Boolean) getValue("isDatePartUsed");
	}

	/**
	 * set the isDatePartUsed
	 * @generated
	 * @param isDatePartUsed
	 */
	public void setIsDatePartUsed( Boolean aIsDatePartUsed) 
	{
		 setValue( "isDatePartUsed",aIsDatePartUsed);
	}

	public boolean isDatePartUsed() 
	{
		return getIsDatePartUsedValue().booleanValue();
	}

	/**
	 * Get the isTimePartUsed
	 * @generated
	 * @return get the isTimePartUsed
	 */
	public Boolean getIsTimePartUsedValue() 
	{
		return (Boolean) getValue("isTimePartUsed");
	}

	/**
	 * set the isTimePartUsed
	 * @generated
	 * @param isTimePartUsed
	 */
	public void setIsTimePartUsed( Boolean aIsTimePartUsed) 
	{
		 setValue( "isTimePartUsed",aIsTimePartUsed);
	}

	public boolean isTimePartUsed() 
	{
		return getIsTimePartUsedValue().booleanValue();
	}


}