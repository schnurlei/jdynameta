package de.jdynameta.metamodel.filter;

import java.lang.Long;
import java.lang.Boolean;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppOperatorLess
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppOperatorLess extends de.jdynameta.metamodel.filter.AppPrimitiveOperator

{
	private static final long serialVersionUID = 1L;
	private java.lang.Boolean isAlsoEqual;

	/**
	 *Constructor 
	 */
	public AppOperatorLess ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppOperatorLess"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppOperatorLess (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the isAlsoEqual
	 * @generated
	 * @return get the isAlsoEqual
	 */
	public Boolean getIsAlsoEqual() 
	{
		return isAlsoEqual;
	}

	/**
	 * set the isAlsoEqual
	 * @generated
	 * @param isAlsoEqual
	 */
	public void setIsAlsoEqual( Boolean aIsAlsoEqual) 
	{
		isAlsoEqual = aIsAlsoEqual;
	}


}