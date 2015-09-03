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

import java.lang.Boolean;
import java.lang.String;

/**
 * ObjectReferenceAttributeInfoModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public interface ObjectReferenceAttributeInfoModel	extends de.jdynameta.metamodel.metainfo.model.AttributeInfoModel, de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo

{


	/**
	 * Get the referencedClass
	 * @generated
	 * @return get the referencedClass
	 */
	public de.jdynameta.base.metainfo.ClassInfo getReferencedClass(); 
	/**
	 * set the referencedClass
	 * @generated
	 * @param referencedClass
	 */
	public void setReferencedClass( ClassInfoModel aReferencedClass); 
	/**
	 * Get the isDependent
	 * @generated
	 * @return get the isDependent
	 */
	public Boolean getIsDependentValue(); 
	/**
	 * set the isDependent
	 * @generated
	 * @param isDependent
	 */
	public void setIsDependent( Boolean aIsDependent); 
	/**
	 * Get the isMultipleAssociation
	 * @generated
	 * @return get the isMultipleAssociation
	 */
	public Boolean getIsMultipleAssociationValue(); 
	/**
	 * set the isMultipleAssociation
	 * @generated
	 * @param isMultipleAssociation
	 */
	public void setIsMultipleAssociation( Boolean aIsMultipleAssociation); 

}