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
 * AttributeInfoModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public interface AttributeInfoModel	extends de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface, de.jdynameta.base.metainfo.AttributeInfo

{


	/**
	 * Get the internalName
	 * @generated
	 * @return get the internalName
	 */
	public String getInternalName(); 
	/**
	 * set the internalName
	 * @generated
	 * @param internalName
	 */
	public void setInternalName( String aInternalName); 
	/**
	 * Get the externalName
	 * @generated
	 * @return get the externalName
	 */
	public String getExternalName(); 
	/**
	 * set the externalName
	 * @generated
	 * @param externalName
	 */
	public void setExternalName( String aExternalName); 
	/**
	 * Get the isKey
	 * @generated
	 * @return get the isKey
	 */
	public Boolean getIsKeyValue(); 
	/**
	 * set the isKey
	 * @generated
	 * @param isKey
	 */
	public void setIsKey( Boolean aIsKey); 
	/**
	 * Get the isNotNull
	 * @generated
	 * @return get the isNotNull
	 */
	public Boolean getIsNotNullValue(); 
	/**
	 * set the isNotNull
	 * @generated
	 * @param isNotNull
	 */
	public void setIsNotNull( Boolean aIsNotNull); 
	/**
	 * Get the isChangeable
	 * @generated
	 * @return get the isChangeable
	 */
	public Boolean getIsChangeableValue(); 
	/**
	 * set the isChangeable
	 * @generated
	 * @param isChangeable
	 */
	public void setIsChangeable( Boolean aIsChangeable); 
	/**
	 * Get the isGenerated
	 * @generated
	 * @return get the isGenerated
	 */
	public Boolean getIsGeneratedValue(); 
	/**
	 * set the isGenerated
	 * @generated
	 * @param isGenerated
	 */
	public void setIsGenerated( Boolean aIsGenerated); 
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

	public String getAttrGroup();
	
	public void setAttrGroup( String aName); 
	

}