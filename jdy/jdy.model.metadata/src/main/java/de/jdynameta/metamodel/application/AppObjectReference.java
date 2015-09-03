package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppObjectReference
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppObjectReference extends de.jdynameta.metamodel.application.AppAttribute

{

	/**
	 *Constructor 
	 */
	public AppObjectReference ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppObjectReference"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppObjectReference (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the referencedClass
	 * @generated
	 * @return get the referencedClass
	 */
	public de.jdynameta.metamodel.application.AppClassInfo getReferencedClass() 
	{
		return (de.jdynameta.metamodel.application.AppClassInfo) getValue("referencedClass");
	}

	/**
	 * set the referencedClass
	 * @generated
	 * @param referencedClass
	 */
	public void setReferencedClass( AppClassInfo aReferencedClass) 
	{
		 setValue( "referencedClass",aReferencedClass);
	}

	/**
	 * Get the isInAssociation
	 * @generated
	 * @return get the isInAssociation
	 */
	public Boolean getIsInAssociationValue() 
	{
		return (Boolean) getValue("isInAssociation");
	}

	/**
	 * set the isInAssociation
	 * @generated
	 * @param isInAssociation
	 */
	public void setIsInAssociation( Boolean aIsInAssociation) 
	{
		 setValue( "isInAssociation",aIsInAssociation);
	}

	public boolean isInAssociation() 
	{
		return getIsInAssociationValue().booleanValue();
	}

	/**
	 * Get the isDependent
	 * @generated
	 * @return get the isDependent
	 */
	public Boolean getIsDependentValue() 
	{
		return (Boolean) getValue("isDependent");
	}

	/**
	 * set the isDependent
	 * @generated
	 * @param isDependent
	 */
	public void setIsDependent( Boolean aIsDependent) 
	{
		 setValue( "isDependent",aIsDependent);
	}

	public boolean isDependent() 
	{
		return getIsDependentValue().booleanValue();
	}


}