package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;
import java.math.BigDecimal;
import de.jdynameta.metamodel.application.AppDecimalDomainModel;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;

/**
 * AppDecimalType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppDecimalType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppDecimalType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppDecimalType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppDecimalType (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
	 * Get the minValue
	 * @generated
	 * @return get the minValue
	 */
	public BigDecimal getMinValue() 
	{
		return (BigDecimal) getValue("MinValue");
	}

	/**
	 * set the minValue
	 * @generated
	 * @param minValue
	 */
	public void setMinValue( BigDecimal aMinValue) 
	{
		 setValue( "MinValue",aMinValue);
	}

	/**
	 * Get the maxValue
	 * @generated
	 * @return get the maxValue
	 */
	public BigDecimal getMaxValue() 
	{
		return (BigDecimal) getValue("MaxValue");
	}

	/**
	 * set the maxValue
	 * @generated
	 * @param maxValue
	 */
	public void setMaxValue( BigDecimal aMaxValue) 
	{
		 setValue( "MaxValue",aMaxValue);
	}

	/**
	 * Get all  AppDecimalDomainModel
	 *
	 * @return get the Collection ofAppDecimalDomainModel
	 */
	public de.jdynameta.base.objectlist.ObjectList getDomainValuesColl() 
	{
		return  getValue(this.getClassInfo().getAssoc("DomainValues"));
	}

	/**
	 * Set aCollection of all AppDecimalDomainModel
	 *
	 * @param AppDecimalDomainModel
	 */
	public void setDomainValuesColl( ObjectList aAppDecimalDomainModelColl) 
	{
		setValue(this.getClassInfo().getAssoc("DomainValues"), aAppDecimalDomainModelColl );
	}


}