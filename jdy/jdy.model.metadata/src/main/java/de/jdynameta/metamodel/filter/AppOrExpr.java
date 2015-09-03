package de.jdynameta.metamodel.filter;

import java.lang.Long;
import java.lang.String;
import de.jdynameta.metamodel.filter.AppFilterExpr;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppOrExpr
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppOrExpr extends de.jdynameta.metamodel.filter.AppFilterExpr

{
	private static final long serialVersionUID = 1L;
	private ObjectList orSubExprColl = new DefaultObjectList();
	private java.lang.String exprName;

	/**
	 *Constructor 
	 */
	public AppOrExpr ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppOrExpr"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppOrExpr (ClassInfo infoForType, ClassNameCreator aNameCreator)
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
	public de.jdynameta.base.objectlist.ObjectList getOrSubExprColl() 
	{
		return orSubExprColl;
	}

	/**
	 * Set aCollection of all AppFilterExpr
	 *
	 * @param AppFilterExpr
	 */
	public void setOrSubExprColl( ObjectList aAppFilterExprColl) 
	{
		orSubExprColl =  aAppFilterExprColl ;
	}


}