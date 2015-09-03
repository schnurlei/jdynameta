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
package de.jdynameta.metamodel.metainfo.model;

import de.jdynameta.base.metainfo.AssociationInfo;


/**
 * AssociationInfoModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public interface AssociationInfoModel	extends de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface,  AssociationInfo

{
	/**
	 * Get the nameResource
	 * @generated
	 * @return get the nameResource
	 */
	public String getNameResource(); 
	/**
	 * set the nameResource
	 * @generated
	 * @param nameResource
	 */
	public void setNameResource( String aNameResource); 
	/**
	 * Get the masterclass
	 * @generated
	 * @return get the masterclass
	 */
	public de.jdynameta.base.metainfo.ClassInfo getMasterclass(); 
	/**
	 * set the masterclass
	 * @generated
	 * @param masterclass
	 */
	public void setMasterclass( ClassInfoModel aMasterclass); 
	/**
	 * Get the masterClassReference
	 * @generated
	 * @return get the masterClassReference
	 */
	public de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo getMasterClassReference(); 
	/**
	 * set the masterClassReference
	 * @generated
	 * @param masterClassReference
	 */
	public void setMasterClassReference( ObjectReferenceAttributeInfoModel aMasterClassReference); 

}