package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppBooleanType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppBooleanType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppBooleanType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppBooleanType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppBooleanType (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the temp
	 * @generated
	 * @return get the temp
	 */
	public Long getTempValue() 
	{
		return (Long) getValue("temp");
	}

	/**
	 * set the temp
	 * @generated
	 * @param temp
	 */
	public void setTemp( Long aTemp) 
	{
		 setValue( "temp",aTemp);
	}

	public long getTemp() 
	{
		return getTempValue().intValue();
	}


}