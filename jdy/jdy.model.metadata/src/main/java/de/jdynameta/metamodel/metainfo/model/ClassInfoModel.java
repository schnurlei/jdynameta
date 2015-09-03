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


/**
 * ClassInfoModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public interface ClassInfoModel	extends de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface, de.jdynameta.base.metainfo.ClassInfo

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
	 * Get the shortName
	 * @generated
	 * @return get the shortName
	 */
	public String getShortName(); 
	/**
	 * set the shortName
	 * @generated
	 * @param shortName
	 */
	public void setShortName( String aShortName); 
	/**
	 * Get the isAbstract
	 * @generated
	 * @return get the isAbstract
	 */
	public Boolean getIsAbstractValue(); 
	/**
	 * set the isAbstract
	 * @generated
	 * @param isAbstract
	 */
	public void setIsAbstract( Boolean aIsAbstract); 
	/**
	 * Get the superclass
	 * @generated
	 * @return get the superclass
	 */
	public de.jdynameta.base.metainfo.ClassInfo getSuperclass(); 
	/**
	 * set the superclass
	 * @generated
	 * @param superclass
	 */
	public void setSuperclass( ClassInfoModel aSuperclass); 
	/**
	 * Get all  AttributesColl
	 * @generated
	 * @return get the Collection ofAttributesColl
	 */
	public de.jdynameta.base.objectlist.ObjectList getAttributesColl(); 
	/**
	 * Set aCollection of all AttributesColl
	 *@generated
	 * @param AttributesColl
	 */
	public void setAttributesColl( de.jdynameta.base.objectlist.ObjectList aAttributesCollColl); 
	/**
	 * Get all  AssociationsColl
	 * @generated
	 * @return get the Collection ofAssociationsColl
	 */
	public de.jdynameta.base.objectlist.ObjectList getAssociationsColl(); 
	/**
	 * Set aCollection of all AssociationsColl
	 *@generated
	 * @param AssociationsColl
	 */
	public void setAssociationsColl( de.jdynameta.base.objectlist.ObjectList aAssociationsCollColl); 
	/**
	 * Get all  SubclassesColl
	 * @generated
	 * @return get the Collection ofSubclassesColl
	 */
	public de.jdynameta.base.objectlist.ObjectList getSubclassesColl(); 
	/**
	 * Set aCollection of all SubclassesColl
	 *@generated
	 * @param SubclassesColl
	 */
	public void setSubclassesColl( de.jdynameta.base.objectlist.ObjectList aSubclassesCollColl); 

}