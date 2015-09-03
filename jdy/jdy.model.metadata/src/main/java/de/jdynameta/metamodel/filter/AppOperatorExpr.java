package de.jdynameta.metamodel.filter;

import java.lang.Long;
import java.util.Date;
import java.lang.Double;
import java.lang.String;
import java.math.BigDecimal;
import java.lang.Boolean;
import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * AppOperatorExpr
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppOperatorExpr extends de.jdynameta.metamodel.filter.AppFilterExpr

{
	private static final long serialVersionUID = 1L;
	private java.lang.String attrName;
	private AppPrimitiveOperator operator;
	private java.lang.Boolean booleanVal;
	private java.math.BigDecimal decimalVal;
	private java.lang.Double floatVal;
	private java.lang.Long longVal;
	private java.lang.String textVal;
	private java.util.Date timestampVal;

	/**
	 *Constructor 
	 */
	public AppOperatorExpr ()
	{
		super(FilterRepository.getSingleton().getInfoForType("AppOperatorExpr"), NAME_CREATOR);
	}
	/**
	 *Constructor for subclasses
	 */
	public AppOperatorExpr (ClassInfo infoForType, ClassNameCreator aNameCreator)
	{
		super(infoForType, aNameCreator);
	}

	/**
	 * Get the attrName
	 * @generated
	 * @return get the attrName
	 */
	public String getAttrName() 
	{
		return attrName;
	}

	/**
	 * set the attrName
	 * @generated
	 * @param attrName
	 */
	public void setAttrName( String aAttrName) 
	{
		attrName = (aAttrName!= null) ? aAttrName.trim() : null;
	}

	/**
	 * Get the operator
	 * @generated
	 * @return get the operator
	 */
	public AppPrimitiveOperator getOperator() 
	{
		return operator;
	}

	/**
	 * set the operator
	 * @generated
	 * @param operator
	 */
	public void setOperator( AppPrimitiveOperator aOperator) 
	{
		operator = aOperator;
	}

	/**
	 * Get the booleanVal
	 * @generated
	 * @return get the booleanVal
	 */
	public Boolean getBooleanVal() 
	{
		return booleanVal;
	}

	/**
	 * set the booleanVal
	 * @generated
	 * @param booleanVal
	 */
	public void setBooleanVal( Boolean aBooleanVal) 
	{
		booleanVal = aBooleanVal;
	}

	/**
	 * Get the decimalVal
	 * @generated
	 * @return get the decimalVal
	 */
	public BigDecimal getDecimalVal() 
	{
		return decimalVal;
	}

	/**
	 * set the decimalVal
	 * @generated
	 * @param decimalVal
	 */
	public void setDecimalVal( BigDecimal aDecimalVal) 
	{
		decimalVal = aDecimalVal;
	}

	/**
	 * Get the floatVal
	 * @generated
	 * @return get the floatVal
	 */
	public Double getFloatVal() 
	{
		return floatVal;
	}

	/**
	 * set the floatVal
	 * @generated
	 * @param floatVal
	 */
	public void setFloatVal( Double aFloatVal) 
	{
		floatVal = aFloatVal;
	}

	/**
	 * Get the longVal
	 * @generated
	 * @return get the longVal
	 */
	public Long getLongVal() 
	{
		return longVal;
	}

	/**
	 * set the longVal
	 * @generated
	 * @param longVal
	 */
	public void setLongVal( Long aLongVal) 
	{
		longVal = aLongVal;
	}

	/**
	 * Get the textVal
	 * @generated
	 * @return get the textVal
	 */
	public String getTextVal() 
	{
		return textVal;
	}

	/**
	 * set the textVal
	 * @generated
	 * @param textVal
	 */
	public void setTextVal( String aTextVal) 
	{
		textVal = (aTextVal!= null) ? aTextVal.trim() : null;
	}

	/**
	 * Get the timestampVal
	 * @generated
	 * @return get the timestampVal
	 */
	public Date getTimestampVal() 
	{
		return timestampVal;
	}

	/**
	 * set the timestampVal
	 * @generated
	 * @param timestampVal
	 */
	public void setTimestampVal( Date aTimestampVal) 
	{
		timestampVal = aTimestampVal;
	}


}