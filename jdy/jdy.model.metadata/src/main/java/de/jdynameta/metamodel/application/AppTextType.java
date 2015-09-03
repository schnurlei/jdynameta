package de.jdynameta.metamodel.application;

import java.lang.String;
import java.lang.Boolean;
import java.lang.Long;
import de.jdynameta.metamodel.application.AppStringDomainModel;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;

/**
 * AppTextType
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AppTextType extends de.jdynameta.metamodel.application.AppPrimitiveAttribute

{

	/**
	 *Constructor 
	 */
	public AppTextType ()
	{
		super(ApplicationRepository.getSingleton().getClassForName("AppTextType"));
	}
	/**
	 *Constructor for subclasses
	 */
	public AppTextType (de.jdynameta.base.metainfo.ClassInfo infoForType)
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
	 * Get the typeHint
	 * @generated
	 * @return get the typeHint
	 */
	public String getTypeHint() 
	{
		return (String) getValue("typeHint");
	}

	/**
	 * set the typeHint
	 * @generated
	 * @param typeHint
	 */
	public void setTypeHint( String aTypeHint) 
	{
		 setValue( "typeHint",(aTypeHint!= null) ? aTypeHint.trim() : null);
	}

	/**
	 * Get all  AppStringDomainModel
	 *
	 * @return get the Collection ofAppStringDomainModel
	 */
	public de.jdynameta.base.objectlist.ObjectList getDomainValuesColl() 
	{
		return  getValue(this.getClassInfo().getAssoc("DomainValues"));
	}

	/**
	 * Set aCollection of all AppStringDomainModel
	 *
	 * @param AppStringDomainModel
	 */
	public void setDomainValuesColl( ObjectList aAppStringDomainModelColl) 
	{
		setValue(this.getClassInfo().getAssoc("DomainValues"), aAppStringDomainModelColl );
	}


}