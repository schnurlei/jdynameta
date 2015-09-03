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
package de.jdynameta.persistence.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.persistence.impl.persistentobject.PersistentStateModelImpl;

@SuppressWarnings("serial")
public class PersistentValueObjectImpl< TWrapObj extends ValueObject> extends HashedValueObject
	implements PersistentValueObject,TypedValueObject, ChangeableTypedValueObject
{
	private final PersistentStateModelImpl persistentState;
	private final ClassInfo classInfo;
	private TWrapObj wrappedValueObject;

	/**
	 * @param aObjectManager
	 * @param aClassInfo
	 * @param aWrappedValueObject
	 */
	public PersistentValueObjectImpl( ClassInfo aClassInfo, TWrapObj aWrappedValueObject, boolean isNew)
	{
		super();
		this.classInfo = aClassInfo;
		this.wrappedValueObject = aWrappedValueObject;
		this.persistentState = new PersistentStateModelImpl();
		this.persistentState.setState(isNew, false);
	}
	
	public void setState(boolean aIsNewFlag, boolean aIsDirtyFlag)
	{
		this.persistentState.setState(aIsNewFlag, aIsDirtyFlag);
	}
	
	public EditablePersistentStateModel getPersistentState()
	{
		return persistentState;
	}

	@Override
	public Object getValue(AttributeInfo aInfo)
	{
		return (super.hasValueFor(aInfo)) 
				? 	super.getValue( aInfo)
				: wrappedValueObject.getValue(aInfo);
	}

	@Override
	public void setValue(AttributeInfo aInfo, Object aValue)
	{
		super.setValue(aInfo, aValue);		
		this.persistentState.setIsDirty(true);
	}

	@Override
	public boolean hasValueFor(AttributeInfo aInfo)
	{
		return super.hasValueFor(aInfo) || wrappedValueObject.hasValueFor(aInfo);
	}

	@Override
	public PersistentObjectList<PersistentValueObjectImpl> getValue(AssociationInfo aAssocInfo)
	{
		PersistentObjectList<PersistentValueObjectImpl> result;
		if( super.hasValueFor(aAssocInfo) ) {
			result = (PersistentObjectList<PersistentValueObjectImpl>) super.getValue( aAssocInfo);
		} else {
			
			List<PersistentValueObjectImpl> wrappedList = new ArrayList<PersistentValueObjectImpl>();
			if( wrappedValueObject.getValue(aAssocInfo) != null) {
				
				ObjectList<? extends ValueObject> list = wrappedValueObject.getValue(aAssocInfo);
				
				for (Iterator<? extends ValueObject> iterator = list.iterator(); iterator.hasNext();)
				{
					ValueObject wrapObj = (ValueObject) iterator.next();
					ClassInfo type = aAssocInfo.getDetailClass();
					if( wrapObj instanceof TypedValueObject) {
						type = ((TypedValueObject) wrapObj).getClassInfo();
					}
					wrappedList.add(new PersistentValueObjectImpl(type,wrapObj,false));
				}
			}
			result = new PersistentAssociationListModel<PersistentValueObjectImpl>(aAssocInfo, wrappedList,this, null);
			super.setValue(aAssocInfo, result);
		}
		return result;
	}	
	

	@Override
	public boolean hasValueFor(AssociationInfo aInfo)
	{
		return super.hasValueFor(aInfo) ||wrappedValueObject.hasValueFor(aInfo);
	}

	public ClassInfo getClassInfo()
	{
		return this.classInfo;
	}

	/**
	 * @return Returns the wrappedValueObject.
	 */
	public TWrapObj getWrappedValueObject()
	{
		return this.wrappedValueObject;
	}

	protected void setWrappedValueObject(TWrapObj aWrappedValueObject)
	{
		this.wrappedValueObject = aWrappedValueObject;
	}
	
	@Override
	public int hashCode()
	{
		return wrappedValueObject.hashCode();
	}
	
	@Override
	public boolean equals(Object aObj)
	{
		return (aObj != null && aObj instanceof PersistentValueObjectImpl) &&  wrappedValueObject.equals(((PersistentValueObjectImpl<TWrapObj>)aObj).getWrappedValueObject());
	}
	
	public void clearCachedData()
	{
		super.clearAllValues();
	}


	@Override
	public Object getValue(String anExternlName)
	{
		assert( this.classInfo.getAttributeInfoForExternalName(anExternlName) != null);
		return  (this.classInfo == null) ? null : getValue( this.classInfo.getAttributeInfoForExternalName(anExternlName));
	}
	
	@Override
	public void setValue(String anExternlName, Object aValue)
	{
		assert( this.classInfo.getAttributeInfoForExternalName(anExternlName) != null);
		setValue( this.classInfo.getAttributeInfoForExternalName(anExternlName), aValue);
	}

	@Override
	public ObjectList<? extends ChangeableTypedValueObject> getValues(String anAssocName)
	{
		assert( this.classInfo.getAssoc(anAssocName) != null);
		return getValue( this.classInfo.getAssoc(anAssocName));
	}

	@Override
	public void setValues(String anAssocName, ObjectList<? extends ChangeableTypedValueObject> aValue)
	{
		assert( this.classInfo.getAssoc(anAssocName) != null);
		setValue( this.classInfo.getAssoc(anAssocName), aValue);
	}
}
