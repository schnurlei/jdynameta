package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppAttribute
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppAttribute extends de.jdynameta.base.value.GenericValueObjectImpl

{

	/**
	 *Constructor 
	 */
	public AppAttribute ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppAttribute"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppAttribute (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
		return (String) getValue("InternalName");
	}

	/**
	 * set the internalName
	 * @generated
	 * @param internalName
	 */
	public void setInternalName( String aInternalName) 
	{
		 setValue( "InternalName",(aInternalName!= null) ? aInternalName.trim() : null);
	}

	/**
	 * Get the isKey
	 * @generated
	 * @return get the isKey
	 */
	public Boolean getIsKeyValue() 
	{
		return (Boolean) getValue("isKey");
	}

	/**
	 * set the isKey
	 * @generated
	 * @param isKey
	 */
	public void setIsKey( Boolean aIsKey) 
	{
		 setValue( "isKey",aIsKey);
	}

	public boolean isKey() 
	{
		return getIsKeyValue().booleanValue();
	}

	/**
	 * Get the isNotNull
	 * @generated
	 * @return get the isNotNull
	 */
	public Boolean getIsNotNullValue() 
	{
		return (Boolean) getValue("isNotNull");
	}

	/**
	 * set the isNotNull
	 * @generated
	 * @param isNotNull
	 */
	public void setIsNotNull( Boolean aIsNotNull) 
	{
		 setValue( "isNotNull",aIsNotNull);
	}

	public boolean isNotNull() 
	{
		return getIsNotNullValue().booleanValue();
	}

	/**
	 * Get the isGenerated
	 * @generated
	 * @return get the isGenerated
	 */
	public Boolean getIsGeneratedValue() 
	{
		return (Boolean) getValue("isGenerated");
	}

	/**
	 * set the isGenerated
	 * @generated
	 * @param isGenerated
	 */
	public void setIsGenerated( Boolean aIsGenerated) 
	{
		 setValue( "isGenerated",aIsGenerated);
	}

	public boolean isGenerated() 
	{
		return getIsGeneratedValue().booleanValue();
	}

	/**
	 * Get the attrGroup
	 * @generated
	 * @return get the attrGroup
	 */
	public String getAttrGroup() 
	{
		return (String) getValue("AttrGroup");
	}

	/**
	 * set the attrGroup
	 * @generated
	 * @param attrGroup
	 */
	public void setAttrGroup( String aAttrGroup) 
	{
		 setValue( "AttrGroup",(aAttrGroup!= null) ? aAttrGroup.trim() : null);
	}

	/**
	 * Get the pos
	 * @generated
	 * @return get the pos
	 */
	public Long getPosValue() 
	{
		return (Long) getValue("pos");
	}

	/**
	 * set the pos
	 * @generated
	 * @param pos
	 */
	public void setPos( Long aPos) 
	{
		 setValue( "pos",aPos);
	}

	public long getPos() 
	{
		return getPosValue().intValue();
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
		AppAttribute typeObj = (AppAttribute) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getInternalName() != null
					&& typeObj.getInternalName() != null
					&& this.getInternalName().equals( typeObj.getInternalName()) )

					&& (getMasterclass() != null
					&& typeObj.getMasterclass() != null
					&& typeObj.getMasterclass().equals( typeObj.getMasterclass()) )
					)
					|| ( getInternalName() == null
					&& typeObj.getInternalName() == null
					&& getMasterclass() == null
					&& typeObj.getMasterclass() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( getInternalName() != null) ? getInternalName().hashCode() : super.hashCode())
			^
				 (( getMasterclass() != null) ? getMasterclass().hashCode() : super.hashCode())		;
	}

}