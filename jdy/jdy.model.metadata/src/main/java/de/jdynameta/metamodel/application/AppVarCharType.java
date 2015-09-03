package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;

/**
 * AppVarCharType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppVarCharType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppVarCharType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppVarCharType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppVarCharType (de.jdynameta.base.metainfo.ClassInfo infoForType)
	{
		super(infoForType);
	}

	/**
	 * Get the length
	 * @generated
	 * @return get the length
	 */
	public Long getLengthValue() 
	{
		return (Long) getValue("length");
	}

	/**
	 * set the length
	 * @generated
	 * @param length
	 */
	public void setLength( Long aLength) 
	{
		 setValue( "length",aLength);
	}

	public long getLength() 
	{
		return getLengthValue().intValue();
	}

	/**
	 * Get the isClob
	 * @generated
	 * @return get the isClob
	 */
	public Boolean getIsClobValue() 
	{
		return (Boolean) getValue("isClob");
	}

	/**
	 * set the isClob
	 * @generated
	 * @param isClob
	 */
	public void setIsClob( Boolean aIsClob) 
	{
		 setValue( "isClob",aIsClob);
	}

	public boolean isClob() 
	{
		return getIsClobValue().booleanValue();
	}

	/**
	 * Get the mimeType
	 * @generated
	 * @return get the mimeType
	 */
	public String getMimeType() 
	{
		return (String) getValue("mimeType");
	}

	/**
	 * set the mimeType
	 * @generated
	 * @param mimeType
	 */
	public void setMimeType( String aMimeType) 
	{
		 setValue( "mimeType",(aMimeType!= null) ? aMimeType.trim() : null);
	}


}