package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Long;

/**
 * AppLongDomainModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppLongDomainModel extends de.jdynameta.base.value.GenericValueObjectImpl

{

	/**
	 *Constructor 
	 */
	public AppLongDomainModel ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppLongDomainModel"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppLongDomainModel (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
	public Long getDbValueValue() 
	{
		return (Long) getValue("dbValue");
	}

	/**
	 * set the dbValue
	 * @generated
	 * @param dbValue
	 */
	public void setDbValue( Long aDbValue) 
	{
		 setValue( "dbValue",aDbValue);
	}

	public long getDbValue() 
	{
		return getDbValueValue().intValue();
	}

	/**
	 * Get the type
	 * @generated
	 * @return get the type
	 */
	public de.jdynameta.metamodel.application.AppLongType getType() 
	{
		return (de.jdynameta.metamodel.application.AppLongType) getValue("Type");
	}

	/**
	 * set the type
	 * @generated
	 * @param type
	 */
	public void setType( AppLongType aType) 
	{
		 setValue( "Type",aType);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppLongDomainModel typeObj = (AppLongDomainModel) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getDbValueValue() != null
					&& typeObj.getDbValueValue() != null
					&& this.getDbValueValue().equals( typeObj.getDbValueValue()) )

					&& (getType() != null
					&& typeObj.getType() != null
					&& typeObj.getType().equals( typeObj.getType()) )
					)
					|| ( getDbValueValue() == null
					&& typeObj.getDbValueValue() == null
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
		return 				 (( getDbValueValue() != null) ? getDbValueValue().hashCode() : super.hashCode())
			^
				 (( getType() != null) ? getType().hashCode() : super.hashCode())		;
	}

}