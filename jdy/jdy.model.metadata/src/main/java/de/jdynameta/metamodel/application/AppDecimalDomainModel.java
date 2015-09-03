package de.jdynameta.metamodel.application;

import java.lang.String;
import java.math.BigDecimal;

/**
 * AppDecimalDomainModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppDecimalDomainModel extends de.jdynameta.base.value.GenericValueObjectImpl

{

	/**
	 *Constructor 
	 */
	public AppDecimalDomainModel ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppDecimalDomainModel"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppDecimalDomainModel (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the representation
	 * @generated
	 * @return get the representation
	 */
	public String getRepresentation() 
	{
		return (String) getValue("representation");
	}

	/**
	 * set the representation
	 * @generated
	 * @param representation
	 */
	public void setRepresentation( String aRepresentation) 
	{
		 setValue( "representation",(aRepresentation!= null) ? aRepresentation.trim() : null);
	}

	/**
	 * Get the dbValue
	 * @generated
	 * @return get the dbValue
	 */
	public BigDecimal getDbValue() 
	{
		return (BigDecimal) getValue("dbValue");
	}

	/**
	 * set the dbValue
	 * @generated
	 * @param dbValue
	 */
	public void setDbValue( BigDecimal aDbValue) 
	{
		 setValue( "dbValue",aDbValue);
	}

	/**
	 * Get the type
	 * @generated
	 * @return get the type
	 */
	public de.jdynameta.metamodel.application.AppDecimalType getType() 
	{
		return (de.jdynameta.metamodel.application.AppDecimalType) getValue("Type");
	}

	/**
	 * set the type
	 * @generated
	 * @param type
	 */
	public void setType( AppDecimalType aType) 
	{
		 setValue( "Type",aType);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppDecimalDomainModel typeObj = (AppDecimalDomainModel) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getDbValue() != null
					&& typeObj.getDbValue() != null
					&& this.getDbValue().equals( typeObj.getDbValue()) )

					&& (getType() != null
					&& typeObj.getType() != null
					&& typeObj.getType().equals( typeObj.getType()) )
					)
					|| ( getDbValue() == null
					&& typeObj.getDbValue() == null
					&& getType() == null
					&& typeObj.getType() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( getDbValue() != null) ? getDbValue().hashCode() : super.hashCode())
			^
				 (( getType() != null) ? getType().hashCode() : super.hashCode())		;
	}

}