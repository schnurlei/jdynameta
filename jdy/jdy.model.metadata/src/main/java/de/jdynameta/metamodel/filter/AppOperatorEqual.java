package de.jdynameta.metamodel.filter;

import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ClassNameCreator;

/**
 * AppOperatorEqual
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppOperatorEqual extends de.jdynameta.metamodel.filter.AppPrimitiveOperator

{
	private static final long serialVersionUID = 1L;
	private java.lang.Boolean isNotEqual;

	/**
	 *Constructor 
	 */
	public AppOperatorEqual ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppOperatorEqual"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppOperatorEqual (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the isNotEqual
	 * @generated
	 * @return get the isNotEqual
	 */
	public Boolean getIsNotEqual() 
	{
		return isNotEqual;
	}

	/**
	 * set the isNotEqual
	 * @generated
	 * @param isNotEqual
	 */
	public void setIsNotEqual( Boolean aIsNotEqual) 
	{
		isNotEqual = aIsNotEqual;
	}


}