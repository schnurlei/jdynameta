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

import java.util.Date;


/**
 * ReferenceTwoOnSameObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class ReferenceTwoOnSameObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Long simpleIntKey;
	private java.lang.String simpleData1;
	private Date simpleData2;
	private CompoundKeyObjImpl compoundRef1;
	private CompoundKeyObjImpl compoundRef2;

	/**
	 *Constructor 
	 */
	public ReferenceTwoOnSameObjImpl ()
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
	 * Get the simpleData1
	 * @generated
	 * @return get the simpleData1
	 */
	public String getSimpleData1() 
	{
		return simpleData1;
	}

	/**
	 * set the simpleData1
	 * @generated
	 * @param simpleData1
	 */
	public void setSimpleData1( String aSimpleData1) 
	{
		simpleData1 = (aSimpleData1!= null) ? aSimpleData1.trim() : null;
	}

	/**
	 * Get the simpleData2
	 * @generated
	 * @return get the simpleData2
	 */
	public Date getSimpleData2() 
	{
		return simpleData2;
	}

	/**
	 * set the simpleData2
	 * @generated
	 * @param simpleData2
	 */
	public void setSimpleData2( Date aSimpleData2) 
	{
		simpleData2 = aSimpleData2;
	}

	/**
	 * Get the compoundRef1
	 * @generated
	 * @return get the compoundRef1
	 */
	public CompoundKeyObjImpl getCompoundRef1() 
	{
		return compoundRef1;
	}

	/**
	 * set the compoundRef1
	 * @generated
	 * @param compoundRef1
	 */
	public void setCompoundRef1( CompoundKeyObjImpl aCompoundRef1) 
	{
		compoundRef1 = aCompoundRef1;
	}

	/**
	 * Get the compoundRef2
	 * @generated
	 * @return get the compoundRef2
	 */
	public CompoundKeyObjImpl getCompoundRef2() 
	{
		return compoundRef2;
	}

	/**
	 * set the compoundRef2
	 * @generated
	 * @param compoundRef2
	 */
	public void setCompoundRef2( CompoundKeyObjImpl aCompoundRef2) 
	{
		compoundRef2 = aCompoundRef2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		ReferenceTwoOnSameObjImpl typeObj = (ReferenceTwoOnSameObjImpl) compareObj;
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