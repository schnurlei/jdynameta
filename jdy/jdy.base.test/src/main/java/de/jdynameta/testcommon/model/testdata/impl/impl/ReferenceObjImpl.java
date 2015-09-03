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
package de.jdynameta.testcommon.model.testdata.impl.impl;


/**
 * ReferenceObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class ReferenceObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Long referenceKey;
	private java.lang.String referenceData;
	private SimpleKeyObjImpl simpleRef;
	private CompoundKeyObjImpl compoundRef;

	/**
	 *Constructor 
	 */
	public ReferenceObjImpl ()
	{
		super(new de.jdynameta.base.value.DefaultClassNameCreator());
	}

	/**
	 * Get the referenceKey
	 * @generated
	 * @return get the referenceKey
	 */
	public Long getReferenceKey() 
	{
		return referenceKey;
	}

	/**
	 * set the referenceKey
	 * @generated
	 * @param referenceKey
	 */
	public void setReferenceKey( Long aReferenceKey) 
	{
		referenceKey = aReferenceKey;
	}

	/**
	 * Get the referenceData
	 * @generated
	 * @return get the referenceData
	 */
	public String getReferenceData() 
	{
		return referenceData;
	}

	/**
	 * set the referenceData
	 * @generated
	 * @param referenceData
	 */
	public void setReferenceData( String aReferenceData) 
	{
		referenceData = (aReferenceData!= null) ? aReferenceData.trim() : null;
	}

	/**
	 * Get the simpleRef
	 * @generated
	 * @return get the simpleRef
	 */
	public SimpleKeyObjImpl getSimpleRef() 
	{
		return simpleRef;
	}

	/**
	 * set the simpleRef
	 * @generated
	 * @param simpleRef
	 */
	public void setSimpleRef( SimpleKeyObjImpl aSimpleRef) 
	{
		simpleRef = aSimpleRef;
	}

	/**
	 * Get the compoundRef
	 * @generated
	 * @return get the compoundRef
	 */
	public CompoundKeyObjImpl getCompoundRef() 
	{
		return compoundRef;
	}

	/**
	 * set the compoundRef
	 * @generated
	 * @param compoundRef
	 */
	public void setCompoundRef( CompoundKeyObjImpl aCompoundRef) 
	{
		compoundRef = aCompoundRef;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		ReferenceObjImpl typeObj = (ReferenceObjImpl) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getReferenceKey() != null
					&& typeObj.getReferenceKey() != null
					&& this.getReferenceKey().equals( typeObj.getReferenceKey()) )
					)
					|| ( getReferenceKey() == null
					&& typeObj.getReferenceKey() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( referenceKey != null) ? referenceKey.hashCode() : super.hashCode())		;
	}

}