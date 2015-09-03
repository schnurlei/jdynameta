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
 * AssocDetailSimpleObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AssocDetailSimpleObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Long simpleIntKey;
	private java.lang.String simpleKeyData1;
	private AssociationMasterObjImpl assocSimpleRef;
	private AssociationMasterObjImpl assocSimpleRef2;

	/**
	 *Constructor 
	 */
	public AssocDetailSimpleObjImpl ()
	{
		super(new de.jdynameta.base.value.DefaultClassNameCreator());
	}

	/**
	 * Get the simpleIntKey
	 * @generated
	 * @return get the simpleIntKey
	 */
	public Long getSimpleIntKey() 
	{
		return simpleIntKey;
	}

	/**
	 * set the simpleIntKey
	 * @generated
	 * @param simpleIntKey
	 */
	public void setSimpleIntKey( Long aSimpleIntKey) 
	{
		simpleIntKey = aSimpleIntKey;
	}

	/**
	 * Get the simpleKeyData1
	 * @generated
	 * @return get the simpleKeyData1
	 */
	public String getSimpleKeyData1() 
	{
		return simpleKeyData1;
	}

	/**
	 * set the simpleKeyData1
	 * @generated
	 * @param simpleKeyData1
	 */
	public void setSimpleKeyData1( String aSimpleKeyData1) 
	{
		simpleKeyData1 = (aSimpleKeyData1!= null) ? aSimpleKeyData1.trim() : null;
	}

	/**
	 * Get the assocSimpleRef
	 * @generated
	 * @return get the assocSimpleRef
	 */
	public AssociationMasterObjImpl getAssocSimpleRef() 
	{
		return assocSimpleRef;
	}

	/**
	 * set the assocSimpleRef
	 * @generated
	 * @param assocSimpleRef
	 */
	public void setAssocSimpleRef( AssociationMasterObjImpl aAssocSimpleRef) 
	{
		assocSimpleRef = aAssocSimpleRef;
	}

	/**
	 * Get the assocSimpleRef2
	 * @generated
	 * @return get the assocSimpleRef2
	 */
	public AssociationMasterObjImpl getAssocSimpleRef2() 
	{
		return assocSimpleRef2;
	}

	/**
	 * set the assocSimpleRef2
	 * @generated
	 * @param assocSimpleRef2
	 */
	public void setAssocSimpleRef2( AssociationMasterObjImpl aAssocSimpleRef2) 
	{
		assocSimpleRef2 = aAssocSimpleRef2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AssocDetailSimpleObjImpl typeObj = (AssocDetailSimpleObjImpl) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getSimpleIntKey() != null
					&& typeObj.getSimpleIntKey() != null
					&& this.getSimpleIntKey().equals( typeObj.getSimpleIntKey()) )
					)
					|| ( getSimpleIntKey() == null
					&& typeObj.getSimpleIntKey() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( simpleIntKey != null) ? simpleIntKey.hashCode() : super.hashCode())		;
	}

}