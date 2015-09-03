package de.jdynameta.metamodel.filter;

import java.lang.String;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppAndExpr
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppAndExpr extends de.jdynameta.metamodel.filter.AppFilterExpr

{
	private static final long serialVersionUID = 1L;
	private ObjectList andSubExprColl = new DefaultObjectList();
	private java.lang.String exprName;

	/**
	 *Constructor 
	 */
	public AppAndExpr ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppAndExpr"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppAndExpr (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the exprName
	 * @generated
	 * @return get the exprName
	 */
	public String getExprName() 
	{
		return exprName;
	}

	/**
	 * set the exprName
	 * @generated
	 * @param exprName
	 */
	public void setExprName( String aExprName) 
	{
		exprName = (aExprName!= null) ? aExprName.trim() : null;
	}

	/**
	 * Get all  AppFilterExpr
	 *
	 * @return get the Collection ofAppFilterExpr
	 */
	public de.jdynameta.base.objectlist.ObjectList getAndSubExprColl() 
	{
		return andSubExprColl;
	}

	/**
	 * Set aCollection of all AppFilterExpr
	 *
	 * @param AppFilterExpr
	 */
	public void setAndSubExprColl( ObjectList aAppFilterExprColl) 
	{
		andSubExprColl =  aAppFilterExprColl ;
	}


}