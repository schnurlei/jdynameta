package de.jdynameta.metamodel.filter;

import java.lang.Long;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppPrimitiveOperator
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppPrimitiveOperator extends de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject

{
	private static final long serialVersionUID = 1L;
	private java.lang.Long operatorId;

	/**
	 *Constructor 
	 */
	public AppPrimitiveOperator ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppPrimitiveOperator"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppPrimitiveOperator (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the operatorId
	 * @generated
	 * @return get the operatorId
	 */
	public Long getOperatorId() 
	{
		return operatorId;
	}

	/**
	 * set the operatorId
	 * @generated
	 * @param operatorId
	 */
	public void setOperatorId( Long aOperatorId) 
	{
		operatorId = aOperatorId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppPrimitiveOperator typeObj = (AppPrimitiveOperator) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getOperatorId() != null
					&& typeObj.getOperatorId() != null
					&& this.getOperatorId().equals( typeObj.getOperatorId()) )
					)
					|| ( getOperatorId() == null
					&& typeObj.getOperatorId() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( operatorId != null) ? operatorId.hashCode() : super.hashCode())		;
	}

}