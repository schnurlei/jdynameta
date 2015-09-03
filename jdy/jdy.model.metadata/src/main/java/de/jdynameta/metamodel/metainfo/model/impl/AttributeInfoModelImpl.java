/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.metamodel.metainfo.model.impl;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;
import de.jdynameta.metamodel.metainfo.model.AttributeInfoModel;
import de.jdynameta.metamodel.metainfo.model.ClassInfoModel;

/**
 * AttributeInfoModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public abstract class AttributeInfoModelImpl extends de.jdynameta.base.value.defaultimpl.TypedReflectionValueObject
	implements de.jdynameta.metamodel.metainfo.model.AttributeInfoModel

{
	private java.lang.String internalName;
	private java.lang.String externalName;
	private java.lang.Boolean isKey;
	private java.lang.Boolean isNotNull;
	private java.lang.Boolean isChangeable;
	private java.lang.Boolean isGenerated;
	private ClassInfoModel masterclass;
	private String	attrGroup;

	/**
	 *Constructor 
	 */
	public AttributeInfoModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getAttributeInfoModelInfo());
	}

	

	protected AttributeInfoModelImpl(ClassInfo typeInfo)
	{
		super(typeInfo);
	}


	/**
	 * Get the internalName
	 * @generated
	 * @return get the internalName
	 */
	public String getInternalName() 
	{
		return internalName;
	}

	/**
	 * set the internalName
	 * @generated
	 * @param internalName
	 */
	public void setInternalName( String aInternalName) 
	{
		internalName = (aInternalName!= null) ? aInternalName.trim() : null;
	}

	/**
	 * Get the externalName
	 * @generated
	 * @return get the externalName
	 */
	public String getExternalName() 
	{
		return externalName;
	}

	/**
	 * set the externalName
	 * @generated
	 * @param externalName
	 */
	public void setExternalName( String aExternalName) 
	{
		externalName = (aExternalName!= null) ? aExternalName.trim() : null;
	}

	/**
	 * Get the isKey
	 * @generated
	 * @return get the isKey
	 */
	public Boolean getIsKeyValue() 
	{
		return isKey;
	}

	/**
	 * set the isKey
	 * @generated
	 * @param isKey
	 */
	public void setIsKey( Boolean aIsKey) 
	{
		isKey = aIsKey;
	}

	public boolean isKey() 
	{
		return isKey.booleanValue();
	}

	/**
	 * Get the isNotNull
	 * @generated
	 * @return get the isNotNull
	 */
	public Boolean getIsNotNullValue() 
	{
		return isNotNull;
	}

	/**
	 * set the isNotNull
	 * @generated
	 * @param isNotNull
	 */
	public void setIsNotNull( Boolean aIsNotNull) 
	{
		isNotNull = aIsNotNull;
	}

	public boolean isNotNull() 
	{
		return isNotNull.booleanValue();
	}

	/**
	 * Get the isChangeable
	 * @generated
	 * @return get the isChangeable
	 */
	public Boolean getIsChangeableValue() 
	{
		return isChangeable;
	}

	/**
	 * set the isChangeable
	 * @generated
	 * @param isChangeable
	 */
	public void setIsChangeable( Boolean aIsChangeable) 
	{
		isChangeable = aIsChangeable;
	}

	public boolean isChangeable() 
	{
		return isChangeable.booleanValue();
	}

	/**
	 * Get the isGenerated
	 * @generated
	 * @return get the isGenerated
	 */
	public Boolean getIsGeneratedValue() 
	{
		return isGenerated;
	}

	/**
	 * set the isGenerated
	 * @generated
	 * @param isGenerated
	 */
	public void setIsGenerated( Boolean aIsGenerated) 
	{
		isGenerated = aIsGenerated;
	}

	public boolean isGenerated() 
	{
		return isGenerated.booleanValue();
	}

	public String getAttrGroup()
	{
		return attrGroup;
	}
	
	public void setAttrGroup( String aGroup)
	{
		this.attrGroup = aGroup;
	}
	
	
	/**
	 * Get the masterclass
	 * @generated
	 * @return get the masterclass
	 */
	public de.jdynameta.base.metainfo.ClassInfo getMasterclass() 
	{
		return masterclass;
	}

	/**
	 * set the masterclass
	 * @generated
	 * @param masterclass
	 */
	public void setMasterclass( ClassInfoModel aMasterclass) 
	{
		masterclass = aMasterclass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
  		AttributeInfoModel typeObj = (AttributeInfoModel) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getInternalName() != null
					&& typeObj.getInternalName() != null
					&& this.getInternalName().equals( typeObj.getInternalName()) )

					&& (getMasterclass() != null
					&& typeObj.getMasterclass() != null
					&& this.getMasterclass().equals( typeObj.getMasterclass()) )
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
		return 				 (( internalName != null) ? internalName.hashCode() : super.hashCode())
			^
				 (( masterclass != null) ? masterclass.hashCode() : super.hashCode())		;
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " : " + internalName + " @ " + masterclass;
	}
	

}