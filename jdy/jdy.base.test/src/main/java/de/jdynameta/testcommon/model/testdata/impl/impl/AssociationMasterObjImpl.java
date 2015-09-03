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

import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;

/**
 * AssociationMasterObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class AssociationMasterObjImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
	private ObjectList simpleRefColl = new DefaultObjectList();
	private ObjectList simpleRef2Coll = new DefaultObjectList();
	private ObjectList keyRefColl = new DefaultObjectList();
	private ObjectList notNullRefColl = new DefaultObjectList();
	private ObjectList selfRefColl = new DefaultObjectList();
	private ReferenceInPrimaryKeyImpl deepRef;
	private Date bonusDate;
	private AssociationMasterObjImpl assocSelfRef;

	/**
	 *Constructor 
	 */
	public AssociationMasterObjImpl ()
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

	/**
	 * Get the assocSelfRef
	 * @generated
	 * @return get the assocSelfRef
	 */
	public AssociationMasterObjImpl getAssocSelfRef() 
	{
		return assocSelfRef;
	}

	/**
	 * set the assocSelfRef
	 * @generated
	 * @param assocSelfRef
	 */
	public void setAssocSelfRef( AssociationMasterObjImpl aAssocSelfRef) 
	{
		assocSelfRef = aAssocSelfRef;
	}

	/**
	 * Get all  AssocDetailSimpleObjImpl
	 *
	 * @return get the Collection ofAssocDetailSimpleObjImpl
	 */
	public de.jdynameta.base.objectlist.ObjectList getSimpleRefColl() 
	{
		return simpleRefColl;
	}

	/**
	 * Set aCollection of all AssocDetailSimpleObjImpl
	 *
	 * @param AssocDetailSimpleObjImpl
	 */
	public void setSimpleRefColl( ObjectList aAssocDetailSimpleObjImplColl) 
	{
		simpleRefColl =  aAssocDetailSimpleObjImplColl ;
	}

	/**
	 * Get all  AssocDetailSimpleObjImpl
	 *
	 * @return get the Collection ofAssocDetailSimpleObjImpl
	 */
	public de.jdynameta.base.objectlist.ObjectList getSimpleRef2Coll() 
	{
		return simpleRef2Coll;
	}

	/**
	 * Set aCollection of all AssocDetailSimpleObjImpl
	 *
	 * @param AssocDetailSimpleObjImpl
	 */
	public void setSimpleRef2Coll( ObjectList aAssocDetailSimpleObjImplColl) 
	{
		simpleRef2Coll =  aAssocDetailSimpleObjImplColl ;
	}

	/**
	 * Get all  AssocDetailKeyObjImpl
	 *
	 * @return get the Collection ofAssocDetailKeyObjImpl
	 */
	public de.jdynameta.base.objectlist.ObjectList getKeyRefColl() 
	{
		return keyRefColl;
	}

	/**
	 * Set aCollection of all AssocDetailKeyObjImpl
	 *
	 * @param AssocDetailKeyObjImpl
	 */
	public void setKeyRefColl( ObjectList aAssocDetailKeyObjImplColl) 
	{
		keyRefColl =  aAssocDetailKeyObjImplColl ;
	}

	/**
	 * Get all  AssocDetailNotNullObjImpl
	 *
	 * @return get the Collection ofAssocDetailNotNullObjImpl
	 */
	public de.jdynameta.base.objectlist.ObjectList getNotNullRefColl() 
	{
		return notNullRefColl;
	}

	/**
	 * Set aCollection of all AssocDetailNotNullObjImpl
	 *
	 * @param AssocDetailNotNullObjImpl
	 */
	public void setNotNullRefColl( ObjectList aAssocDetailNotNullObjImplColl) 
	{
		notNullRefColl =  aAssocDetailNotNullObjImplColl ;
	}

	/**
	 * Get all  AssociationMasterObjImpl
	 *
	 * @return get the Collection ofAssociationMasterObjImpl
	 */
	public de.jdynameta.base.objectlist.ObjectList getSelfRefColl() 
	{
		return selfRefColl;
	}

	/**
	 * Set aCollection of all AssociationMasterObjImpl
	 *
	 * @param AssociationMasterObjImpl
	 */
	public void setSelfRefColl( ObjectList aAssociationMasterObjImplColl) 
	{
		selfRefColl =  aAssociationMasterObjImplColl ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		AssociationMasterObjImpl typeObj = (AssociationMasterObjImpl) compareObj;
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