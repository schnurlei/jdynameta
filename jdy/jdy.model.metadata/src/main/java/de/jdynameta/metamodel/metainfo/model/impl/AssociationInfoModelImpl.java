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
import de.jdynameta.metamodel.metainfo.model.ClassInfoModel;
import de.jdynameta.metamodel.metainfo.model.ObjectReferenceAttributeInfoModel;

/**
 * AssociationInfoModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public  class AssociationInfoModelImpl extends de.jdynameta.base.value.defaultimpl.TypedReflectionValueObject
	implements de.jdynameta.metamodel.metainfo.model.AssociationInfoModel

{
	private java.lang.String nameResource;
	private ClassInfoModel masterclass;
	private ObjectReferenceAttributeInfoModel masterClassReference;

	/**
	 *Constructor 
	 */
	public AssociationInfoModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getAssociationInfo());
	}

	/**
	 * Get the nameResource
	 * @generated
	 * @return get the nameResource
	 */
	public String getNameResource() 
	{
		return nameResource;
	}

	/**
	 * set the nameResource
	 * @generated
	 * @param nameResource
	 */
	public void setNameResource( String aNameResource) 
	{
		nameResource = (aNameResource!= null) ? aNameResource.trim() : null;
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

	public ClassInfo getDetailClass()
	{
		return (masterClassReference == null) ? null : masterClassReference.getMasterclass();
	}
	
	/**
	 * Get the masterClassReference
	 * @generated
	 * @return get the masterClassReference
	 */
	public de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo getMasterClassReference() 
	{
		return masterClassReference;
	}

	/**
	 * set the masterClassReference
	 * @generated
	 * @param masterClassReference
	 */
	public void setMasterClassReference( ObjectReferenceAttributeInfoModel aMasterClassReference) 
	{
		masterClassReference = aMasterClassReference;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AssociationInfoModelImpl typeObj = (AssociationInfoModelImpl) compareObj;
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
		return 				 (( nameResource != null) ? nameResource.hashCode() : super.hashCode())		;
	}

}