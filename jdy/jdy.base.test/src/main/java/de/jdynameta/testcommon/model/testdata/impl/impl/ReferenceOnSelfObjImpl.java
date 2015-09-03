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
 * ReferenceOnSelfObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class ReferenceOnSelfObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Long simpleIntKey;
	private java.lang.String simpleTextKey;
	private Date simpleData;
	private ReferenceOnSelfObjImpl selfRef;

	/**
	 *Constructor 
	 */
	public ReferenceOnSelfObjImpl ()
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
	 * Get the simpleTextKey
	 * @generated
	 * @return get the simpleTextKey
	 */
	public String getSimpleTextKey() 
	{
		return simpleTextKey;
	}

	/**
	 * set the simpleTextKey
	 * @generated
	 * @param simpleTextKey
	 */
	public void setSimpleTextKey( String aSimpleTextKey) 
	{
		simpleTextKey = (aSimpleTextKey!= null) ? aSimpleTextKey.trim() : null;
	}

	/**
	 * Get the simpleData
	 * @generated
	 * @return get the simpleData
	 */
	public Date getSimpleData() 
	{
		return simpleData;
	}

	/**
	 * set the simpleData
	 * @generated
	 * @param simpleData
	 */
	public void setSimpleData( Date aSimpleData) 
	{
		simpleData = aSimpleData;
	}

	/**
	 * Get the selfRef
	 * @generated
	 * @return get the selfRef
	 */
	public ReferenceOnSelfObjImpl getSelfRef() 
	{
		return selfRef;
	}

	/**
	 * set the selfRef
	 * @generated
	 * @param selfRef
	 */
	public void setSelfRef( ReferenceOnSelfObjImpl aSelfRef) 
	{
		selfRef = aSelfRef;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		ReferenceOnSelfObjImpl typeObj = (ReferenceOnSelfObjImpl) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getSimpleIntKey() != null
					&& typeObj.getSimpleIntKey() != null
					&& this.getSimpleIntKey().equals( typeObj.getSimpleIntKey()) )

					&& (getSimpleTextKey() != null
					&& typeObj.getSimpleTextKey() != null
					&& this.getSimpleTextKey().equals( typeObj.getSimpleTextKey()) )
					)
					|| ( getSimpleIntKey() == null
					&& typeObj.getSimpleIntKey() == null
					&& getSimpleTextKey() == null
					&& typeObj.getSimpleTextKey() == null
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
				 (( simpleTextKey != null) ? simpleTextKey.hashCode() : super.hashCode())		;
	}

}