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

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;
import de.jdynameta.metamodel.metainfo.model.ClassInfoModel;

/**
 * ObjectReferenceAttributeInfoModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public  class ObjectReferenceAttributeInfoModelImpl extends de.jdynameta.metamodel.metainfo.model.impl.AttributeInfoModelImpl
	implements de.jdynameta.metamodel.metainfo.model.ObjectReferenceAttributeInfoModel

{
	private ClassInfoModel referencedClass;
	private java.lang.Boolean isDependent;
	private java.lang.Boolean isMultipleAssociation;

	/**
	 *Constructor 
	 */
	public ObjectReferenceAttributeInfoModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getObjectReferenceInfo());
	}

	/**
	 * Get the referencedClass
	 * @generated
	 * @return get the referencedClass
	 */
	public de.jdynameta.base.metainfo.ClassInfo getReferencedClass() 
	{
		return referencedClass;
	}

	/**
	 * set the referencedClass
	 * @generated
	 * @param referencedClass
	 */
	public void setReferencedClass( ClassInfoModel aReferencedClass) 
	{
		referencedClass = aReferencedClass;
	}

	/**
	 * Get the isDependent
	 * @generated
	 * @return get the isDependent
	 */
	public Boolean getIsDependentValue() 
	{
		return isDependent;
	}

	/**
	 * set the isDependent
	 * @generated
	 * @param isDependent
	 */
	public void setIsDependent( Boolean aIsDependent) 
	{
		isDependent = aIsDependent;
	}

	public boolean isDependent() 
	{
		return isDependent.booleanValue();
	}

	/**
	 * Get the isMultipleAssociation
	 * @generated
	 * @return get the isMultipleAssociation
	 */
	public Boolean getIsMultipleAssociationValue() 
	{
		return isMultipleAssociation;
	}

	/**
	 * set the isMultipleAssociation
	 * @generated
	 * @param isMultipleAssociation
	 */
	public void setIsMultipleAssociation( Boolean aIsMultipleAssociation) 
	{
		isMultipleAssociation = aIsMultipleAssociation;
	}

	public boolean isInAssociation() 
	{
		return isMultipleAssociation.booleanValue();
	}

	
	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.AttributeInfo#handleAttribute(de.comafra.model.metainfo.AttributeHandler, java.lang.Object)
	 */
	public void handleAttribute(AttributeHandler aHandler, Object aValue)
		throws JdyPersistentException
	{
		aHandler.handleObjectReference(this, (ValueObject) aValue);
	}

}