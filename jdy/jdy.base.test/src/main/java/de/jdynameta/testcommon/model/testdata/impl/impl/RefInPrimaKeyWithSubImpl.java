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
 * RefInPrimaKeyWithSubImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class RefInPrimaKeyWithSubImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Integer simpleIntKey;
	private SimpleKeyObjImpl simpleRefKey;
	private CompoundKeyObjImpl compoundRefKey;
	private java.lang.String referenceData;

	/**
	 *Constructor 
	 */
	public RefInPrimaKeyWithSubImpl ()
	{
		super(new de.jdynameta.base.value.DefaultClassNameCreator());
	}

	/**
	 * Get the simpleIntKey
	 * @generated
	 * @return get the simpleIntKey
	 */
	public Integer getSimpleIntKey() 
	{
		return simpleIntKey;
	}

	/**
	 * set the simpleIntKey
	 * @generated
	 * @param simpleIntKey
	 */
	public void setSimpleIntKey( Integer aSimpleIntKey) 
	{
		simpleIntKey = aSimpleIntKey;
	}

	/**
	 * Get the simpleRefKey
	 * @generated
	 * @return get the simpleRefKey
	 */
	public SimpleKeyObjImpl getSimpleRefKey() 
	{
		return simpleRefKey;
	}

	/**
	 * set the simpleRefKey
	 * @generated
	 * @param simpleRefKey
	 */
	public void setSimpleRefKey( SimpleKeyObjImpl aSimpleRefKey) 
	{
		simpleRefKey = aSimpleRefKey;
	}

	/**
	 * Get the compoundRefKey
	 * @generated
	 * @return get the compoundRefKey
	 */
	public CompoundKeyObjImpl getCompoundRefKey() 
	{
		return compoundRefKey;
	}

	/**
	 * set the compoundRefKey
	 * @generated
	 * @param compoundRefKey
	 */
	public void setCompoundRefKey( CompoundKeyObjImpl aCompoundRefKey) 
	{
		compoundRefKey = aCompoundRefKey;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		RefInPrimaKeyWithSubImpl typeObj = (RefInPrimaKeyWithSubImpl) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getSimpleIntKey() != null
					&& typeObj.getSimpleIntKey() != null
					&& this.getSimpleIntKey().equals( typeObj.getSimpleIntKey()) )

					&& (getSimpleRefKey() != null
					&& typeObj.getSimpleRefKey() != null
					&& typeObj.getSimpleRefKey().equals( typeObj.getSimpleRefKey()) )

					&& (getCompoundRefKey() != null
					&& typeObj.getCompoundRefKey() != null
					&& typeObj.getCompoundRefKey().equals( typeObj.getCompoundRefKey()) )
					)
					|| ( getSimpleIntKey() == null
					&& typeObj.getSimpleIntKey() == null
					&& getSimpleRefKey() == null
					&& typeObj.getSimpleRefKey() == null
					&& getCompoundRefKey() == null
					&& typeObj.getCompoundRefKey() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( simpleIntKey != null) ? simpleIntKey.hashCode() : super.hashCode())
			^
				 (( simpleRefKey != null) ? simpleRefKey.hashCode() : super.hashCode())
			^
				 (( compoundRefKey != null) ? compoundRefKey.hashCode() : super.hashCode())		;
	}

}