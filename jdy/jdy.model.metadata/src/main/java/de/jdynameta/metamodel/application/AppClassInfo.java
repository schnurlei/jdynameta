package de.jdynameta.metamodel.application;

import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.metamodel.generic.ScritpingHook.ScriptSource;

/**
 * AppClassInfo
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppClassInfo extends de.jdynameta.base.value.GenericValueObjectImpl
	implements ScriptSource
{

	/**
	 *Constructor 
	 */
	public AppClassInfo ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppClassInfo"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppClassInfo (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the name
	 * @generated
	 * @return get the name
	 */
	public String getName() 
	{
		return (String) getValue("Name");
	}

	/**
	 * set the name
	 * @generated
	 * @param name
	 */
	public void setName( String aName) 
	{
		 setValue( "Name",(aName!= null) ? aName.trim() : null);
	}

	/**
	 * Get the internalName
	 * @generated
	 * @return get the internalName
	 */
	public String getInternalName() 
	{
		return (String) getValue("Internal");
	}

	/**
	 * set the internalName
	 * @generated
	 * @param internalName
	 */
	public void setInternalName( String aInternalName) 
	{
		 setValue( "Internal",(aInternalName!= null) ? aInternalName.trim() : null);
	}

	/**
	 * Get the isAbstract
	 * @generated
	 * @return get the isAbstract
	 */
	public Boolean getIsAbstractValue() 
	{
		return (Boolean) getValue("isAbstract");
	}

	/**
	 * set the isAbstract
	 * @generated
	 * @param isAbstract
	 */
	public void setIsAbstract( Boolean aIsAbstract) 
	{
		 setValue( "isAbstract",aIsAbstract);
	}

	public boolean isAbstract() 
	{
		return getIsAbstractValue().booleanValue();
	}

	/**
	 * Get the nameSpace
	 * @generated
	 * @return get the nameSpace
	 */
	public String getNameSpace() 
	{
		return (String) getValue("NameSpace");
	}

	/**
	 * set the nameSpace
	 * @generated
	 * @param nameSpace
	 */
	public void setNameSpace( String aNameSpace) 
	{
		 setValue( "NameSpace",(aNameSpace!= null) ? aNameSpace.trim() : null);
	}

	/**
	 * Get the beforeSaveScript
	 * @generated
	 * @return get the beforeSaveScript
	 */
	public String getBeforeSaveScript() 
	{
		return (String) getValue("beforeSaveScript");
	}

	/**
	 * set the beforeSaveScript
	 * @generated
	 * @param beforeSaveScript
	 */
	public void setBeforeSaveScript( String aBeforeSaveScript) 
	{
		 setValue( "beforeSaveScript",(aBeforeSaveScript!= null) ? aBeforeSaveScript.trim() : null);
	}

	/**
	 * Get the superclass
	 * @generated
	 * @return get the superclass
	 */
	public de.jdynameta.metamodel.application.AppClassInfo getSuperclass() 
	{
		return (de.jdynameta.metamodel.application.AppClassInfo) getValue("Superclass");
	}

	/**
	 * set the superclass
	 * @generated
	 * @param superclass
	 */
	public void setSuperclass( AppClassInfo aSuperclass) 
	{
		 setValue( "Superclass",aSuperclass);
	}

	/**
	 * Get the repository
	 * @generated
	 * @return get the repository
	 */
	public de.jdynameta.metamodel.application.AppRepository getRepository() 
	{
		return (de.jdynameta.metamodel.application.AppRepository) getValue("Repository");
	}

	/**
	 * set the repository
	 * @generated
	 * @param repository
	 */
	public void setRepository( AppRepository aRepository) 
	{
		 setValue( "Repository",aRepository);
	}

	/**
	 * Get all  AppAttribute
	 *
	 * @return get the Collection ofAppAttribute
	 */
	public de.jdynameta.base.objectlist.ObjectList getAttributesColl() 
	{
		return  getValue(this.getClassInfo().getAssoc("Attributes"));
	}

	/**
	 * Set aCollection of all AppAttribute
	 *
	 * @param AppAttribute
	 */
	public void setAttributesColl( ObjectList aAppAttributeColl) 
	{
		setValue(this.getClassInfo().getAssoc("Attributes"), aAppAttributeColl );
	}

	/**
	 * Get all  AppAssociation
	 *
	 * @return get the Collection ofAppAssociation
	 */
	public de.jdynameta.base.objectlist.ObjectList getAssociationsColl() 
	{
		return  getValue(this.getClassInfo().getAssoc("Associations"));
	}

	/**
	 * Set aCollection of all AppAssociation
	 *
	 * @param AppAssociation
	 */
	public void setAssociationsColl( ObjectList aAppAssociationColl) 
	{
		setValue(this.getClassInfo().getAssoc("Associations"), aAppAssociationColl );
	}

	/**
	 * Get all  AppClassInfo
	 *
	 * @return get the Collection ofAppClassInfo
	 */
	public de.jdynameta.base.objectlist.ObjectList getSubclassesColl() 
	{
		return  getValue(this.getClassInfo().getAssoc("Subclasses"));
	}

	/**
	 * Set aCollection of all AppClassInfo
	 *
	 * @param AppClassInfo
	 */
	public void setSubclassesColl( ObjectList aAppClassInfoColl) 
	{
		setValue(this.getClassInfo().getAssoc("Subclasses"), aAppClassInfoColl );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		if( compareObj instanceof AppClassInfo) {
			
			AppClassInfo typeObj = (AppClassInfo) compareObj;
			return typeObj != null 
					&& ( 
						( 
						(getInternalName() != null
						&& typeObj.getInternalName() != null
						&& this.getInternalName().equals( typeObj.getInternalName()) )

						&& (getRepository() != null
						&& typeObj.getRepository() != null
						&& typeObj.getRepository().equals( typeObj.getRepository()) )
						)
						|| ( getInternalName() == null
						&& typeObj.getInternalName() == null
						&& getRepository() == null
						&& typeObj.getRepository() == null
						&& this == typeObj )
					);
		} else {
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( getInternalName() != null) ? getInternalName().hashCode() : super.hashCode())
			^
				 (( getRepository() != null) ? getRepository().hashCode() : super.hashCode())		;
	}

}