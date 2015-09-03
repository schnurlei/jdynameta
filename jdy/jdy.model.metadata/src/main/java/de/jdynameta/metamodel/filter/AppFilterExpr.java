package de.jdynameta.metamodel.filter;

import java.lang.Long;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppFilterExpr
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppFilterExpr extends de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject

{
	private static final long serialVersionUID = 1L;
	private java.lang.Long exprId;
	private AppAndExpr appAndExpr;
	private AppOrExpr appOrExpr;

	/**
	 *Constructor 
	 */
	public AppFilterExpr ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppFilterExpr"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppFilterExpr (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the exprId
	 * @generated
	 * @return get the exprId
	 */
	public Long getExprId() 
	{
		return exprId;
	}

	/**
	 * set the exprId
	 * @generated
	 * @param exprId
	 */
	public void setExprId( Long aExprId) 
	{
		exprId = aExprId;
	}

	/**
	 * Get the appAndExpr
	 * @generated
	 * @return get the appAndExpr
	 */
	public AppAndExpr getAppAndExpr() 
	{
		return appAndExpr;
	}

	/**
	 * set the appAndExpr
	 * @generated
	 * @param appAndExpr
	 */
	public void setAppAndExpr( AppAndExpr aAppAndExpr) 
	{
		appAndExpr = aAppAndExpr;
	}

	/**
	 * Get the appOrExpr
	 * @generated
	 * @return get the appOrExpr
	 */
	public AppOrExpr getAppOrExpr() 
	{
		return appOrExpr;
	}

	/**
	 * set the appOrExpr
	 * @generated
	 * @param appOrExpr
	 */
	public void setAppOrExpr( AppOrExpr aAppOrExpr) 
	{
		appOrExpr = aAppOrExpr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppFilterExpr typeObj = (AppFilterExpr) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getExprId() != null
					&& typeObj.getExprId() != null
					&& this.getExprId().equals( typeObj.getExprId()) )
					)
					|| ( getExprId() == null
					&& typeObj.getExprId() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( exprId != null) ? exprId.hashCode() : super.hashCode())		;
	}

}