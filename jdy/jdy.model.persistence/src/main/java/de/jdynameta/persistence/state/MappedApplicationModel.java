package de.jdynameta.persistence.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.persistence.impl.persistentobject.PersistentStateModelImpl;

/**
 * Model wich maps the data from the persistent reader to the application model
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class MappedApplicationModel extends HashedValueObject
	implements ApplicationObj, ChangeableTypedValueObject
{

	private final PersistentStateModelImpl persistentState;
	private final ClassInfo classInfo;
	private ChangeableTypedValueObject wrappedValueObject;
	private PersistentObjectReader<MappedApplicationModel> persistentReader;

	public MappedApplicationModel(ClassInfo aClassInfo, boolean isNew, PersistentObjectReader<MappedApplicationModel> aPersistentReader) 
	{
		super();
		this.classInfo = aClassInfo;
		this.persistentState = new PersistentStateModelImpl();
		
		this.persistentState.setState(isNew, false);
		this.persistentReader = aPersistentReader;
	}

	protected PersistentObjectReader<? extends MappedApplicationModel> getPersistentReader()
	{
		return this.persistentReader;
	}
	
	public Object getValue(String anExternlName) 
	{
		return getValue( this.classInfo.getAttributeInfoForExternalName(anExternlName));
		
	}

	public PersistentObjectList<? extends MappedApplicationModel> getValues(String anAssocName) 
	{
		return getValue( this.classInfo.getAssoc(anAssocName));
		
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
		AttributeInfo mappedAttribute = getMappedAttribute(aInfo);
		
		if( super.hasValueFor(aInfo)) {
			return super.getValue( aInfo);
		} else {
			if( aInfo instanceof ObjectReferenceAttributeInfo  ) {
				ChangeableTypedValueObject wrappedObj = (ChangeableTypedValueObject) wrappedValueObject.getValue(mappedAttribute);
				if( wrappedObj != null) {
					super.setValue(aInfo, createModelFor( ((ObjectReferenceAttributeInfo)aInfo).getReferencedClass() , wrappedObj,  false) );
					return super.getValue( aInfo);
				} else {
					return super.getValue( aInfo);
				}
			} else {
				return wrappedValueObject.getValue(mappedAttribute);
			}
		}
		
	}

	protected AttributeInfo getMappedAttribute(AttributeInfo aInfo)
	{
		return wrappedValueObject.getClassInfo().getAttributeInfoForExternalName(aInfo.getExternalName());
	}

	protected AssociationInfo getMappeAssociation(AssociationInfo anAssocInfo)
	{
		return wrappedValueObject.getClassInfo().getAssoc(anAssocInfo.getNameResource());
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
	public PersistentObjectList<? extends MappedApplicationModel> getValue(AssociationInfo anAssocInfo)
	{
		AssociationInfo mappedAttribute = getMappeAssociation(anAssocInfo);

		PersistentAssociationListModel<MappedApplicationModel> result;
		if( super.hasValueFor(anAssocInfo) ) {
			result = (PersistentAssociationListModel<MappedApplicationModel>) super.getValue( anAssocInfo);
		} else {
			
			List<MappedApplicationModel> wrappedList = new ArrayList<MappedApplicationModel>();
			if( getWrappedValueObject().getValue(mappedAttribute) != null) {
				ObjectList<ChangeableTypedValueObject> listToWrap =  (ObjectList<ChangeableTypedValueObject>) getWrappedValueObject().getValue(mappedAttribute);
				for (ChangeableTypedValueObject wrapObj : listToWrap)
				{
					wrappedList.add(createModelFor(wrapObj.getClassInfo(),wrapObj,false));
				}
			}
			result = new PersistentAssociationListModel<MappedApplicationModel>(anAssocInfo, wrappedList,this, (PersistentObjectReader<MappedApplicationModel>) this.persistentReader);
	
			super.setValue(anAssocInfo, result);
		}
		return result;
	}	

	@Override
	public void setValues(String anAssocName, ObjectList<? extends ChangeableTypedValueObject> aValue)
	{
		assert( this.classInfo.getAssoc(anAssocName) != null);
		setValue( this.classInfo.getAssoc(anAssocName), aValue);
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
	public ChangeableTypedValueObject getWrappedValueObject()
	{
		return this.wrappedValueObject;
	}

	public void setWrappedValueObject(ChangeableTypedValueObject aWrappedValueObject)
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
		return (aObj != null && aObj instanceof ApplicationModel) &&  wrappedValueObject.equals(((ApplicationModel<? extends ApplicationModel<ChangeableTypedValueObject>>)aObj).getWrappedValueObject());
	}
	
	public void clearCachedData()
	{

		super.clearAllValues();
		
		getPersistentState().setState(getPersistentState().isNew(), false);
		
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
				for (AttributeInfo curRefInfo : refInfo.getReferencedClass().getAttributeInfoIterator())
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
	
	public MappedApplicationModel createModelFor( ClassInfo aClassInfo,ChangeableTypedValueObject persistentObj, boolean isNew)
	{
		MappedApplicationModel createdModel =  new MappedApplicationModel(aClassInfo, isNew, persistentReader);
		
		createdModel.setWrappedValueObject(persistentObj);
		
		return createdModel;
	}

	
	
	/** 
	 * Replaces GenericValueObjectImpl with ApplicationModel for all references 
	 * @author rs
	 *
	 */
//	public class ConverterAttrHandler implements AttributeHandler
//	{
//		private ApplicationModel newObj;
//		
//		public ConverterAttrHandler(ApplicationModel aNewObj)
//		{
//			this.newObj = aNewObj;
//		}
//
//		@Override
//		public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
//		{
//			if( objToHandle == null) {
//				newObj.setValue(aInfo, null);
//			} else {
//				newObj.setValue(aInfo, createModelFor( ((GenericValueObjectImpl)objToHandle).getClassInfo(), (GenericValueObjectImpl)objToHandle,  false) );
//			}
//		}
//
//		@Override
//		public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,	Object objToHandle) throws JdyPersistentException
//		{
//		}
//		
//	}	
	
	
	
//	public void writeDataIntoWrappedObject()
//	{
//		for( Iterator<AttributeInfo> attrIter = getClassInfo().getAttributeInfoIterator(); attrIter.hasNext();) {
//			
//			AttributeInfo curInfo = attrIter.next();
//			
////			if( !curInfo.isGenerated() ) {
//				Object objToSet = this.getValue(curInfo);
//				if( objToSet instanceof ReflectionValueObjectWrapper) {
//					getWrappedValueObject().setValue(curInfo, ((ReflectionValueObjectWrapper)objToSet).geGenericValueObjectImpl());
//				} else if (  objToSet instanceof PersistentValueObjectImpl ) {
//					ReflectionValueObjectWrapper reflObj = (ReflectionValueObjectWrapper) ((PersistentValueObjectImpl)objToSet).getWrappedValueObject();
//					getWrappedValueObject().setValue(curInfo, (reflObj != null) ? reflObj.geGenericValueObjectImpl() : null );
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
//				boolean removed = this.getWrappedValueObject().getWrappedCollFor(curInfo).remove( ((ReflectionValueObjectWrapper)((PersistentValueObjectImpl)persValueObj).getWrappedValueObject()).geGenericValueObjectImpl());
//				System.out.println("removed" + removed);
//			}
//		}
//
//		return ((ReflectionValueObjectWrapper) persistentObj.getWrappedValueObject()).geGenericValueObjectImpl();
//	}


	

}
