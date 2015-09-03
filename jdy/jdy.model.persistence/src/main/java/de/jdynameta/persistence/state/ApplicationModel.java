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
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.persistence.impl.persistentobject.PersistentStateModelImpl;

/**
 * Convenience class to define a PersistentValueObject which wrappes an TypedValueObject
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class ApplicationModel<TWrappedObject extends TypedValueObject> extends HashedValueObject
	implements ApplicationObj, PersistentValueObject
{

	private final PersistentStateModelImpl persistentState;
	private final ClassInfo classInfo;
	private TWrappedObject wrappedValueObject;
	private PersistentObjectReader<? extends ApplicationModel<TWrappedObject>> persistentReader;

	public ApplicationModel(ClassInfo aClassInfo, TWrappedObject aWrappedValueObject, boolean isNew, PersistentObjectReader<? extends ApplicationModel<TWrappedObject>> aPersistentReader) 
	{
		super();
		this.classInfo = aClassInfo;
		this.persistentState = new PersistentStateModelImpl();
		this.setWrappedValueObject(aWrappedValueObject);
		
		this.persistentState.setState(isNew, false);
		this.persistentReader = aPersistentReader;
	}

	protected PersistentObjectReader<? extends ApplicationModel<TWrappedObject>> getPersistentReader()
	{
		return this.persistentReader;
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
	public Object getValue(AttributeInfo aInfo)
	{
		if( super.hasValueFor(aInfo) || wrappedValueObject == null) {
			return super.getValue( aInfo);
		} else {
			if( aInfo instanceof ObjectReferenceAttributeInfo  ) {
				TWrappedObject wrappedObj = (TWrappedObject) wrappedValueObject.getValue(aInfo);
				if( wrappedObj != null) {
					super.setValue(aInfo, createModelFor( wrappedObj.getClassInfo(), wrappedObj,  false) );
					return super.getValue( aInfo);
				} else {
					return super.getValue( aInfo);
				}
			} else {
				return wrappedValueObject.getValue(aInfo);
			}
		}
		
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
	public PersistentObjectList<? extends ApplicationModel<TWrappedObject>> getValue(AssociationInfo aAssocInfo)
	{
		PersistentAssociationListModel<ApplicationModel<TWrappedObject>> result;
		if( super.hasValueFor(aAssocInfo) ) {
			result = (PersistentAssociationListModel<ApplicationModel<TWrappedObject>>) super.getValue( aAssocInfo);
		} else {
			
			List<ApplicationModel<TWrappedObject>> wrappedList = new ArrayList<ApplicationModel<TWrappedObject>>();
			if( getWrappedValueObject() != null && getWrappedValueObject().getValue(aAssocInfo) != null) {
				ObjectList<TWrappedObject> listToWrap =  (ObjectList<TWrappedObject>) getWrappedValueObject().getValue(aAssocInfo);
				for (TWrappedObject wrapObj : listToWrap)
				{
					wrappedList.add(createModelFor(wrapObj.getClassInfo(),wrapObj,false));
				}
			}
			result = new PersistentAssociationListModel<ApplicationModel<TWrappedObject>>(aAssocInfo, wrappedList,this, (PersistentObjectReader<ApplicationModel<TWrappedObject>>) this.persistentReader);
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
	public TWrappedObject getWrappedValueObject()
	{
		return this.wrappedValueObject;
	}

	public void setWrappedValueObject(TWrappedObject aWrappedValueObject)
	{
		this.wrappedValueObject = aWrappedValueObject;
		this.clearCachedData();
//		try {
//			classInfo.handleAttributes(new ConverterAttrHandler(this), aWrappedValueObject);
//		} catch (JdyPersistentException ex) {
//			ex.printStackTrace();
//		}
	}
	
	@Override
	public int hashCode()
	{
		return wrappedValueObject.hashCode();
	}
	
	@Override
	public boolean equals(Object aObj)
	{
		return (aObj != null && aObj instanceof ApplicationModel) &&  wrappedValueObject.equals(((ApplicationModel<? extends ApplicationModel<TWrappedObject>>)aObj).getWrappedValueObject());
	}
	
	public void clearCachedData()
	{

		super.clearAllValues();
		
		getPersistentState().setState(false, false);
		
//		super.clearAssociationValues();
	}
		
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(getClass().getSimpleName() + "#" +classInfo.getInternalName() +"@ ");
		for( AttributeInfo curInfo : getClassInfo().getAttributeInfoIterator()) {
		
			Object objToSet = this.getValue(curInfo);
			appendAttribute(result, curInfo, objToSet, super.hasValueFor(curInfo));
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
			aBuffer.append("-").append(objToSet).append(" | ");
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
				aBuffer.append(refObj.getClass().getSimpleName()+"@");
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
	
	/** 
	 * Replaces GenericValueObjectImpl with ApplicationModel for all references 
	 * @author rs
	 *
	 */
	public class ConverterAttrHandler implements AttributeHandler
	{
		private ApplicationModel newObj;
		
		public ConverterAttrHandler(ApplicationModel aNewObj)
		{
			this.newObj = aNewObj;
		}

		@Override
		public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
		{
			if( objToHandle == null) {
				newObj.setValue(aInfo, null);
			} else {
				newObj.setValue(aInfo, createModelFor( ((TWrappedObject)objToHandle).getClassInfo(), (TWrappedObject)objToHandle,  false) );
			}
		}

		@Override
		public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,	Object objToHandle) throws JdyPersistentException
		{
		}
		
	}	
	
	public  ApplicationModel<TWrappedObject> createModelFor( ClassInfo aClassInfo,TWrappedObject persistentObj, boolean isNew)
	{
		return new ApplicationModel<TWrappedObject>(aClassInfo, persistentObj, isNew, persistentReader);
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
	
	
//	public void writeDataIntoWrappedObject()
//	{
//		for( Iterator<AttributeInfo> attrIter = getClassInfo().getAttributeInfoIterator(); attrIter.hasNext();) {
//			
//			AttributeInfo curInfo = attrIter.next();
//			
////			if( !curInfo.isGenerated() ) {
//				Object objToSet = this.getValue(curInfo);
//				if( objToSet instanceof ReflectionValueObjectWrapper) {
//					getWrappedValueObject().setValue(curInfo, ((ReflectionValueObjectWrapper)objToSet).getWrappedObject());
//				} else if (  objToSet instanceof PersistentValueObjectImpl ) {
//					ReflectionValueObjectWrapper reflObj = (ReflectionValueObjectWrapper) ((PersistentValueObjectImpl)objToSet).getWrappedValueObject();
//					getWrappedValueObject().setValue(curInfo, (reflObj != null) ? reflObj.getWrappedObject() : null );
//				} else {
//					getWrappedValueObject().setValue(curInfo, this.getValue(curInfo));
//				}
////			}
//		}
//
//		for( Iterator<AssociationInfo> attrIter = getClassInfo().getAssociationInfoIterator(); attrIter.hasNext();) {
//			
//			AssociationInfo curInfo = attrIter.next();
//			PersistentObjectList<PersistentValueObject> objList =  this.getValue(curInfo);
//			for (PersistentValueObject persValueObj : objList)
//			{
//				if(persValueObj.getPersistentState().isNew()) {
//					Object convObj = convertValueObjToPersistentObj( (PersReflValueObj ) persValueObj, curInfo.getDetailClass() );
//					
//					Collection<Object> aColl = this.getWrappedValueObject().getWrappedCollFor(curInfo);
//					if( aColl == null) {
//						aColl = new HashSet<Object>();
//						this.getWrappedValueObject().setWrappedCollFor(curInfo, aColl); 
//					}
//					this.getWrappedValueObject().getWrappedCollFor(curInfo).add(convObj);								
//				} else {
//					Object convObj = convertValueObjToPersistentObj( (PersReflValueObj ) persValueObj, curInfo.getDetailClass() );
//				}
//			}
//			
//			for (PersistentValueObject persValueObj : objList.getDeletedObjects())
//			{
//				boolean removed = this.getWrappedValueObject().getWrappedCollFor(curInfo).remove( ((ReflectionValueObjectWrapper)((PersistentValueObjectImpl)persValueObj).getWrappedValueObject()).getWrappedObject());
//				System.out.println("removed" + removed);
//			}
//		}
//
//		return ((ReflectionValueObjectWrapper) persistentObj.getWrappedValueObject()).getWrappedObject();
//	}


}
