package de.jdynameta.metamodel.application;

import de.jdynameta.base.objectlist.ObjectList;

/**
 * AppRepository
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppRepository extends de.jdynameta.base.value.GenericValueObjectImpl

{

	/**
	 *Constructor 
	 */
	public AppRepository ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppRepository"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppRepository (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
	 * Get the name
	 * @generated
	 * @return get the name
	 */
	public Long getAppVersion() 
	{
		return (Long) getValue("appVersion");
	}

	public void setAppVersion( Long aValue) 
	{
		 setValue( "appVersion",aValue);
	}

	/**
	 * Get the name
	 * @generated
	 * @return get the name
	 */
	public Boolean getClosed() 
	{
		return (Boolean) getValue("closed");
	}

	
	/**
	 * set the name
	 * @generated
	 * @param name
	 */
	public void setClosed( Boolean aValue) 
	{
		 setValue( "closed", aValue);
	}


	
	/**
	 * Get the applicationName
	 * @generated
	 * @return get the applicationName
	 */
	public String getApplicationName() 
	{
		return (String) getValue("applicationName");
	}

	/**
	 * set the applicationName
	 * @generated
	 * @param applicationName
	 */
	public void setApplicationName( String aApplicationName) 
	{
		 setValue( "applicationName",(aApplicationName!= null) ? aApplicationName.trim() : null);
	}

	/**
	 * Get all  AppClassInfo
	 *
	 * @return get the Collection ofAppClassInfo
	 */
	public de.jdynameta.base.objectlist.ObjectList<AppClassInfo> getClassesColl() 
	{
		return (ObjectList<AppClassInfo>) getValue(this.getClassInfo().getAssoc("Classes"));
	}

	/**
	 * Set aCollection of all AppClassInfo
	 *
	 * @param AppClassInfo
	 */
	public void setClassesColl( ObjectList aAppClassInfoColl) 
	{
		setValue(this.getClassInfo().getAssoc("Classes"), aAppClassInfoColl );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppRepository typeObj = (AppRepository) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getApplicationName() != null
					&& typeObj.getApplicationName() != null
					&& this.getApplicationName().equals( typeObj.getApplicationName()) )
					)
					|| ( getApplicationName() == null
					&& typeObj.getApplicationName() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( getApplicationName() != null) ? getApplicationName().hashCode() : super.hashCode())		;
	}

}