package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppPrimitiveAttribute
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppPrimitiveAttribute extends de.jdynameta.metamodel.application.AppAttribute

{

	/**
	 *Constructor 
	 */
	public AppPrimitiveAttribute ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppPrimitiveAttribute"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppPrimitiveAttribute (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}


}