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

import java.sql.Date;

/**
 * KeyRefDeepObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class KeyRefDeepObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private ReferenceInPrimaryKeyImpl deepRef;
	private java.sql.Date bonusDate;

	/**
	 *Constructor 
	 */
	public KeyRefDeepObjImpl ()
	{
		super(new de.jdynameta.base.value.DefaultClassNameCreator());
	}

	/**
	 * Get the deepRef
	 * @generated
	 * @return get the deepRef
	 */
	public ReferenceInPrimaryKeyImpl getDeepRef() 
	{
		return deepRef;
	}

	/**
	 * set the deepRef
	 * @generated
	 * @param deepRef
	 */
	public void setDeepRef( ReferenceInPrimaryKeyImpl aDeepRef) 
	{
		deepRef = aDeepRef;
	}

	/**
	 * Get the bonusDate
	 * @generated
	 * @return get the bonusDate
	 */
	public Date getBonusDate() 
	{
		return bonusDate;
	}

	/**
	 * set the bonusDate
	 * @generated
	 * @param bonusDate
	 */
	public void setBonusDate( Date aBonusDate) 
	{
		bonusDate = aBonusDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		KeyRefDeepObjImpl typeObj = (KeyRefDeepObjImpl) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getDeepRef() != null
					&& typeObj.getDeepRef() != null
					&& typeObj.getDeepRef().equals( typeObj.getDeepRef()) )
					)
					|| ( getDeepRef() == null
					&& typeObj.getDeepRef() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( deepRef != null) ? deepRef.hashCode() : super.hashCode())		;
	}

}