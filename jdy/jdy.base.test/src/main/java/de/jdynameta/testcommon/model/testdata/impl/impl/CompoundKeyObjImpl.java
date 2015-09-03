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
 * CompoundKeyObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class CompoundKeyObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private java.lang.Long compoundIntKey;
	private java.lang.String compoundTxtKey;
	private Date compoundKeyData;

	/**
	 *Constructor 
	 */
	public CompoundKeyObjImpl ()
	{
		super(new de.jdynameta.base.value.DefaultClassNameCreator());
	}

	/**
	 * Get the compoundIntKey
	 * @generated
	 * @return get the compoundIntKey
	 */
	public Long getCompoundIntKey() 
	{
		return compoundIntKey;
	}

	/**
	 * set the compoundIntKey
	 * @generated
	 * @param compoundIntKey
	 */
	public void setCompoundIntKey( Long aCompoundIntKey) 
	{
		compoundIntKey = aCompoundIntKey;
	}

	/**
	 * Get the compoundTxtKey
	 * @generated
	 * @return get the compoundTxtKey
	 */
	public String getCompoundTxtKey() 
	{
		return compoundTxtKey;
	}

	/**
	 * set the compoundTxtKey
	 * @generated
	 * @param compoundTxtKey
	 */
	public void setCompoundTxtKey( String aCompoundTxtKey) 
	{
		compoundTxtKey = (aCompoundTxtKey!= null) ? aCompoundTxtKey.trim() : null;
	}

	/**
	 * Get the compoundKeyData
	 * @generated
	 * @return get the compoundKeyData
	 */
	public Date getCompoundKeyData() 
	{
		return compoundKeyData;
	}

	/**
	 * set the compoundKeyData
	 * @generated
	 * @param compoundKeyData
	 */
	public void setCompoundKeyData( Date aCompoundKeyData) 
	{
		compoundKeyData = aCompoundKeyData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		CompoundKeyObjImpl typeObj = (CompoundKeyObjImpl) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getCompoundIntKey() != null
					&& typeObj.getCompoundIntKey() != null
					&& this.getCompoundIntKey().equals( typeObj.getCompoundIntKey()) )

					&& (getCompoundTxtKey() != null
					&& typeObj.getCompoundTxtKey() != null
					&& this.getCompoundTxtKey().equals( typeObj.getCompoundTxtKey()) )
					)
					|| ( getCompoundIntKey() == null
					&& typeObj.getCompoundIntKey() == null
					&& getCompoundTxtKey() == null
					&& typeObj.getCompoundTxtKey() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( compoundIntKey != null) ? compoundIntKey.hashCode() : super.hashCode())
				^
				 (( compoundTxtKey != null) ? compoundTxtKey.hashCode() : super.hashCode())		;
	}

}