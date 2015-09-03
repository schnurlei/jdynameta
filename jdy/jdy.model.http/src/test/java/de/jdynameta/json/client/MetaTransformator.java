package de.jdynameta.json.client;

import de.jdynameta.base.cache.MultibleClassInfoIdentityCache;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.objectlist.AssocObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedWrappedValueObject;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.ApplicationRepositoryClassFileGenerator;

public class MetaTransformator implements ObjectTransformator<ValueObject, GenericValueObjectImpl>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClassNameCreator nameCreator = new ApplicationRepositoryClassFileGenerator.ModelNameCreator();

	public MetaTransformator(ClassNameCreator aNameCreator)
	{
		this.nameCreator = aNameCreator;
	}
	
	@Override
	public GenericValueObjectImpl createObjectFor(	TypedValueObject aTypedValueObject)	throws ObjectCreationException 
	{
		MultibleClassInfoIdentityCache<GenericValueObjectImpl> objCache = new MultibleClassInfoIdentityCache<GenericValueObjectImpl>();
	
		GenericValueObjectImpl newObj =  ApplicationRepository.createValueObject(null, aTypedValueObject.getClassInfo(), false, nameCreator);
		objCache.insertObject(aTypedValueObject, newObj, aTypedValueObject.getClassInfo());
		setValuesInObject(newObj, aTypedValueObject, objCache);
		return newObj;
	}

	public GenericValueObjectImpl createObjectFor(	TypedValueObject aTypedValueObject
			, MultibleClassInfoIdentityCache<GenericValueObjectImpl> objCache)	throws ObjectCreationException 
	{
		GenericValueObjectImpl newObj = objCache.getCacheForClassInfo(aTypedValueObject.getClassInfo()).getObject(aTypedValueObject);
		if( newObj == null)  {
			newObj =  ApplicationRepository.createValueObject(null, aTypedValueObject.getClassInfo(), false, nameCreator);
			objCache.insertObject(aTypedValueObject, newObj, aTypedValueObject.getClassInfo());
			setValuesInObject(newObj, aTypedValueObject, objCache);
		}

		return newObj;
	}
	
	@Override
	public GenericValueObjectImpl createNewObjectFor(ClassInfo aClassinfo)	throws ObjectCreationException 
	{
		return ApplicationRepository.createValueObject(null, aClassinfo, false, nameCreator);
	}

	@Override
	public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,	ValueObject aObjectToTransform) 
	{
		return new TypedHashedWrappedValueObject<ValueObject>(aClassinfo, aObjectToTransform);
	}
	
	private boolean setValuesInObject( GenericValueObjectImpl newObject, TypedValueObject aValueModel
			,MultibleClassInfoIdentityCache<GenericValueObjectImpl> objCache) throws ObjectCreationException
	{
		boolean allAttributesSet = true;			
		for( AttributeInfo curInfo : aValueModel.getClassInfo().getAttributeInfoIterator()) {
				
			if( curInfo instanceof ObjectReferenceAttributeInfo) {

				Object refValue = aValueModel.getValue(curInfo);
				TypedValueObject convertedRef = null;
				if( refValue != null ) {
					convertedRef = createObjectFor((TypedValueObject) refValue, objCache);
				} 

				newObject.setValue(curInfo, convertedRef);
			} else if(  curInfo instanceof PrimitiveAttributeInfo ) {
				newObject.setValue(curInfo, aValueModel.getValue(curInfo));
			} else {
				throw new InvalidClassInfoException("Unknown Attribute Type");
			}
				
		}

		for( AssociationInfo curAssocInfo : aValueModel.getClassInfo().getAssociationInfoIterator()) {
				
			if( aValueModel.hasValueFor(curAssocInfo)) {
				
				AssocObjectList<GenericValueObjectImpl> objList = new AssocObjectList<GenericValueObjectImpl>(curAssocInfo, newObject);
				ObjectList<? extends ValueObject> dependentObjs = aValueModel.getValue(curAssocInfo);
				for (ValueObject curDependetObj : dependentObjs)
				{
					objList.addObject(createObjectFor((TypedValueObject) curDependetObj, objCache));
				}
				newObject.setValue(curAssocInfo, objList);
			} else {
				newObject.setValue(curAssocInfo, null);
			}
		}
		
		return allAttributesSet;
	}		
}