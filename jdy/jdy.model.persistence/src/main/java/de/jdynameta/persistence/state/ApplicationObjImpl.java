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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.persistence.impl.persistentobject.PersistentStateModelImpl;

/**
 * Convenience class to define a PersistentValueObject which wrappes an TypedValueObject
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class ApplicationObjImpl extends HashedValueObject
	implements ApplicationObj, PersistentValueObject
{

	private final PersistentStateModelImpl persistentState;
	private final ClassInfo classInfo;

	public ApplicationObjImpl(ClassInfo aClassInfo, boolean isNew) 
	{
		super();
		this.classInfo = aClassInfo;
		this.persistentState = new PersistentStateModelImpl();
		this.persistentState.setState(isNew, false);
	}

	
	public Object getValue(String anExternlName) 
	{
		return getValue( this.classInfo.getAttributeInfoForExternalName(anExternlName));
		
	}

	public void setValue( String anExternlName, Object aValue)
	{
		setValue( this.classInfo.getAttributeInfoForExternalName(anExternlName), aValue);
	}
	
	public void setState(boolean aIsNewFlag, boolean aIsDirtyFlag)
	{
		this.persistentState.setState(aIsNewFlag, aIsDirtyFlag);
	}
	
	public void setMarkedAsDeleted(boolean isMarkedAsDeleted)
	{
		this.persistentState.setMarkedAsDeleted(isMarkedAsDeleted);
	}
	
	
	public EditablePersistentStateModel getPersistentState()
	{
		return persistentState;
	}


	@Override
	public void setValue(AttributeInfo aInfo, Object aValue)
	{
		super.setValue(aInfo, aValue);		
		this.persistentState.setIsDirty(true);
	}

	
	public ClassInfo getClassInfo()
	{
		return this.classInfo;
	}

	
	
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(getClass().getSimpleName() + "->" +classInfo.getInternalName() +"\n ");
		for( AttributeInfo curInfo: getClassInfo().getAttributeInfoIterator()) {
		
			Object objToSet = this.getValue(curInfo);
			appendAttribute(result, curInfo, objToSet, super.hasValueFor(curInfo));
			result.append(" \n ");
		}
		for( AssociationInfo curInfo: getClassInfo().getAssociationInfoIterator()) {
			
			result.append("#").append(curInfo.getNameResource()).append(" - ");
			if (this.getValue(curInfo) == null ) {
				result.append("<null>");
			} else {
				result.append(this.getValue(curInfo).size());
			}
			result.append("\n");
		}
		
		
		

		return result.toString();
	}
	
	/**
	 * append attribute to {@link StringBuffer}
	 * @param aBuffer
	 * @param aInfo
	 * @param objToSet
	 * @param isChanged
	 */
	private void appendAttribute( StringBuffer aBuffer, AttributeInfo aInfo, Object objToSet, boolean isChanged)
	{
		if( aInfo instanceof PrimitiveAttributeInfo) {
			aBuffer.append(aInfo.getInternalName());
			if(  isChanged) {
				aBuffer.append("*");
			}
			aBuffer.append("-").append(objToSet);
		} else if ( aInfo instanceof ObjectReferenceAttributeInfo) {

			aBuffer.append(aInfo.getInternalName());
			if(  isChanged) {
				aBuffer.append("*");
			}

			aBuffer.append("[");
			if( objToSet == null) {
				aBuffer.append("null");
			} else if( objToSet instanceof ValueObject ) {
				ValueObject refObj = (ValueObject)objToSet;
				aBuffer.append(refObj.getClass().getSimpleName()+"->");
				ObjectReferenceAttributeInfo refInfo = (ObjectReferenceAttributeInfo) aInfo;
				for (AttributeInfo curRefInfo: refInfo.getReferencedClass().getAttributeInfoIterator())
				{
					try
					{
						appendAttribute(aBuffer, curRefInfo, refObj.getValue(curRefInfo), false);
					} catch (Throwable ex)
					{
						aBuffer.append("#error#");
					}
				}
			} else {
				aBuffer.append("#error#");
			}
			aBuffer.append("]");
		
		}
		
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
