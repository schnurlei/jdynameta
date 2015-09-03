package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;
import de.jdynameta.metamodel.application.AppLongDomainModel;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;

/**
 * AppLongType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppLongType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppLongType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppLongType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppLongType (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the minValue
	 * @generated
	 * @return get the minValue
	 */
	public Long getMinValueValue() 
	{
		return (Long) getValue("MinValue");
	}

	/**
	 * set the minValue
	 * @generated
	 * @param minValue
	 */
	public void setMinValue( Long aMinValue) 
	{
		 setValue( "MinValue",aMinValue);
	}

	public long getMinValue() 
	{
		return getMinValueValue().intValue();
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

	/**
	 * Get all  AppLongDomainModel
	 *
	 * @return get the Collection ofAppLongDomainModel
	 */
	public de.jdynameta.base.objectlist.ObjectList getDomainValuesColl() 
	{
		return  getValue(this.getClassInfo().getAssoc("DomainValues"));
	}

	/**
	 * Set aCollection of all AppLongDomainModel
	 *
	 * @param AppLongDomainModel
	 */
	public void setDomainValuesColl( ObjectList aAppLongDomainModelColl) 
	{
		setValue(this.getClassInfo().getAssoc("DomainValues"), aAppLongDomainModelColl );
	}


}