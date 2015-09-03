package de.jdynameta.metamodel.application;

import java.lang.String;

/**
 * AppStringDomainModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppStringDomainModel extends de.jdynameta.base.value.GenericValueObjectImpl

{

	/**
	 *Constructor 
	 */
	public AppStringDomainModel ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppStringDomainModel"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppStringDomainModel (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
	public String getDbValue() 
	{
		return (String) getValue("dbValue");
	}

	/**
	 * set the dbValue
	 * @generated
	 * @param dbValue
	 */
	public void setDbValue( String aDbValue) 
	{
		 setValue( "dbValue",(aDbValue!= null) ? aDbValue.trim() : null);
	}

	/**
	 * Get the type
	 * @generated
	 * @return get the type
	 */
	public de.jdynameta.metamodel.application.AppTextType getType() 
	{
		return (de.jdynameta.metamodel.application.AppTextType) getValue("Type");
	}

	/**
	 * set the type
	 * @generated
	 * @param type
	 */
	public void setType( AppTextType aType) 
	{
		 setValue( "Type",aType);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppStringDomainModel typeObj = (AppStringDomainModel) compareObj;
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