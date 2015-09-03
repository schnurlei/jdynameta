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
 * SimpleKeyWithSubObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class SimpleKeyWithSubObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Long simpleIntKey;
	private java.lang.String simpleKeyData1;
	private Date simpleKeyData2;

	/**
	 *Constructor 
	 */
	public SimpleKeyWithSubObjImpl ()
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
	 * Get the simpleKeyData2
	 * @generated
	 * @return get the simpleKeyData2
	 */
	public Date getSimpleKeyData2() 
	{
		return simpleKeyData2;
	}

	/**
	 * set the simpleKeyData2
	 * @generated
	 * @param simpleKeyData2
	 */
	public void setSimpleKeyData2( Date aSimpleKeyData2) 
	{
		simpleKeyData2 = aSimpleKeyData2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		SimpleKeyWithSubObjImpl typeObj = (SimpleKeyWithSubObjImpl) compareObj;
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