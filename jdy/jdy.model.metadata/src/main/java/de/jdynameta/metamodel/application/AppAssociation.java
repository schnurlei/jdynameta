package de.jdynameta.metamodel.application;

/**
 * AppAssociation
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppAssociation extends de.jdynameta.base.value.GenericValueObjectImpl

{

	/**
	 *Constructor 
	 */
	public AppAssociation ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppAssociation"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppAssociation (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
	 * Get the nameResource
	 * @generated
	 * @return get the nameResource
	 */
	public String getNameResource() 
	{
		return (String) getValue("nameResource");
	}

	/**
	 * set the nameResource
	 * @generated
	 * @param nameResource
	 */
	public void setNameResource( String aNameResource) 
	{
		 setValue( "nameResource",(aNameResource!= null) ? aNameResource.trim() : null);
	}

	/**
	 * Get the masterClassReference
	 * @generated
	 * @return get the masterClassReference
	 */
	public de.jdynameta.metamodel.application.AppObjectReference getMasterClassReference() 
	{
		return (de.jdynameta.metamodel.application.AppObjectReference) getValue("masterClassReference");
	}

	/**
	 * set the masterClassReference
	 * @generated
	 * @param masterClassReference
	 */
	public void setMasterClassReference( AppObjectReference aMasterClassReference) 
	{
		 setValue( "masterClassReference",aMasterClassReference);
	}

	/**
	 * Get the masterclass
	 * @generated
	 * @return get the masterclass
	 */
	public de.jdynameta.metamodel.application.AppClassInfo getMasterclass() 
	{
		return (de.jdynameta.metamodel.application.AppClassInfo) getValue("Masterclass");
	}

	/**
	 * set the masterclass
	 * @generated
	 * @param masterclass
	 */
	public void setMasterclass( AppClassInfo aMasterclass) 
	{
		 setValue( "Masterclass",aMasterclass);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AppAssociation typeObj = (AppAssociation) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getNameResource() != null
					&& typeObj.getNameResource() != null
					&& this.getNameResource().equals( typeObj.getNameResource()) )
					)
					|| ( getNameResource() == null
					&& typeObj.getNameResource() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( getNameResource() != null) ? getNameResource().hashCode() : super.hashCode())		;
	}

}