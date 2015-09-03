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
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

/**
 * PrimitiveAttributeInfoModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public abstract class PrimitiveAttributeInfoModelImpl extends de.jdynameta.metamodel.metainfo.model.impl.AttributeInfoModelImpl
	implements de.jdynameta.metamodel.metainfo.model.PrimitiveAttributeInfoModel

{
	private java.lang.Long id;
	private java.lang.String name;

	/**
	 *Constructor 
	 */
	public PrimitiveAttributeInfoModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getPrimitiveAttributeInfo());
	}

	
	public PrimitiveAttributeInfoModelImpl(ClassInfo typeInfo)
	{
		super(typeInfo);
	}


	/**
	 * Get the id
	 * @generated
	 * @return get the id
	 */
	public Long getIdValue() 
	{
		return id;
	}

	/**
	 * set the id
	 * @generated
	 * @param id
	 */
	public void setId( Long aId) 
	{
		id = aId;
	}

	public long getId() 
	{
		return (id == null) ? 0 : id.longValue();
	}

	/**
	 * Get the name
	 * @generated
	 * @return get the name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * set the name
	 * @generated
	 * @param name
	 */
	public void setName( String aName) 
	{
		name = (aName!= null) ? aName.trim() : null;
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.AttributeInfo#handleAttribute(de.comafra.model.metainfo.AttributeHandler, java.lang.Object)
	 */
	public void handleAttribute(AttributeHandler aHandler, Object aValue)
			throws JdyPersistentException
	{
		aHandler.handlePrimitiveAttribute(this, aValue);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.PrimitiveAttributeInfo#getJavaTyp()
	 */
	public Class getJavaTyp() 
	{
		return getType().getJavaType();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.PrimitiveAttributeInfo#getJavaTypName()
	 */
	public String getJavaTypName()
	{
		String result = "";
		
		if ( getType().getJavaType().isPrimitive()) {
			
			result += getType().getJavaType().getName();	
		} else {
			
			result = getType().getJavaType().getName();
			result = result.substring(result.lastIndexOf('.')+1);
		}
		
		return result;
	}	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.PrimitiveAttributeInfo#compareObjects(java.lang.Object, java.lang.Object)
	 */
	public int compareObjects(Object aValue1, Object aValue2)
	{
		int result = 0;
		if(aValue1 instanceof Comparable) {
			result = ((Comparable) aValue1).compareTo(aValue2);
		} else {
			result = aValue1.toString().compareTo(aValue2.toString());
		}
		
		return result;
	}

}