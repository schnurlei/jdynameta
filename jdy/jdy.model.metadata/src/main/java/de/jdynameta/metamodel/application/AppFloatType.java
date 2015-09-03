package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppFloatType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppFloatType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppFloatType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppFloatType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppFloatType (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the scale
	 * @generated
	 * @return get the scale
	 */
	public Long getScaleValue() 
	{
		return (Long) getValue("Scale");
	}

	/**
	 * set the scale
	 * @generated
	 * @param scale
	 */
	public void setScale( Long aScale) 
	{
		 setValue( "Scale",aScale);
	}

	public long getScale() 
	{
		return getScaleValue().intValue();
	}

	/**
	 * Get the maxValue
	 * @generated
	 * @return get the maxValue
	 */
	public Long getMaxValueValue() 
	{
		return (Long) getValue("MaxValue");
	}

	/**
	 * set the maxValue
	 * @generated
	 * @param maxValue
	 */
	public void setMaxValue( Long aMaxValue) 
	{
		 setValue( "MaxValue",aMaxValue);
	}

	public long getMaxValue() 
	{
		return getMaxValueValue().intValue();
	}


}