package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppBlobType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppBlobType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppBlobType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppBlobType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppBlobType (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the typeHintId
	 * @generated
	 * @return get the typeHintId
	 */
	public Long getTypeHintIdValue() 
	{
		return (Long) getValue("TypeHintId");
	}

	/**
	 * set the typeHintId
	 * @generated
	 * @param typeHintId
	 */
	public void setTypeHintId( Long aTypeHintId) 
	{
		 setValue( "TypeHintId",aTypeHintId);
	}

	public long getTypeHintId() 
	{
		return getTypeHintIdValue().intValue();
	}


}