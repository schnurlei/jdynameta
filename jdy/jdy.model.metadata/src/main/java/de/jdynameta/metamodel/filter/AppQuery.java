package de.jdynameta.metamodel.filter;

import java.lang.Long;
import java.lang.String;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppQuery
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppQuery extends de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject

{
	private static final long serialVersionUID = 1L;
	private java.lang.Long filterId;
	private java.lang.String repoName;
	private java.lang.String className;
	private AppFilterExpr expr;

	/**
	 *Constructor 
	 */
	public AppQuery ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppQuery"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppQuery (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the filterId
	 * @generated
	 * @return get the filterId
	 */
	public Long getFilterId() 
	{
		return filterId;
	}

	/**
	 * set the filterId
	 * @generated
	 * @param filterId
	 */
	public void setFilterId( Long aFilterId) 
	{
		filterId = aFilterId;
	}

	/**
	 * Get the repoName
	 * @generated
	 * @return get the repoName
	 */
	public String getRepoName() 
	{
		return repoName;
	}

	/**
	 * set the repoName
	 * @generated
	 * @param repoName
	 */
	public void setRepoName( String aRepoName) 
	{
		repoName = (aRepoName!= null) ? aRepoName.trim() : null;
	}

	/**
	 * Get the className
	 * @generated
	 * @return get the className
	 */
	public String getClassName() 
	{
		return className;
	}

	/**
	 * set the className
	 * @generated
	 * @param className
	 */
	public void setClassName( String aClassName) 
	{
		className = (aClassName!= null) ? aClassName.trim() : null;
	}

	/**
	 * Get the expr
	 * @generated
	 * @return get the expr
	 */
	public AppFilterExpr getExpr() 
	{
		return expr;
	}

	/**
	 * set the expr
	 * @generated
	 * @param expr
	 */
	public void setExpr( AppFilterExpr aExpr) 
	{
		expr = aExpr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppQuery typeObj = (AppQuery) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getFilterId() != null
					&& typeObj.getFilterId() != null
					&& this.getFilterId().equals( typeObj.getFilterId()) )
					)
					|| ( getFilterId() == null
					&& typeObj.getFilterId() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( filterId != null) ? filterId.hashCode() : super.hashCode())		;
	}

}