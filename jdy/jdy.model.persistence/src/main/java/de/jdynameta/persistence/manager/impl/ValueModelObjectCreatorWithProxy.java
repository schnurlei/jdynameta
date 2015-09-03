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
package de.jdynameta.persistence.manager.impl;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.persistence.cache.UpdatableObjectCreator;
import de.jdynameta.persistence.impl.proxy.ObjectCreatorWithProxy;
import de.jdynameta.persistence.manager.PersistentObjectManager;

/**
 * Creates an GenericPersitentObjectCreator for the ClassInfo
 * 
 * Use an external Object creator to create referenced Objects
 * (maybe they are cached and have not be created)  
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ValueModelObjectCreatorWithProxy extends ObjectCreatorWithProxy<GenericValueObjectImpl>
	implements UpdatableObjectCreator<ChangeableValueObject ,GenericValueObjectImpl>
{
	private final ProxyResolver proxyResolver;
	private final PersistentObjectManager<ChangeableValueObject ,GenericValueObjectImpl> objectManager;
	
	public ValueModelObjectCreatorWithProxy(ProxyResolver aProxyResolver
										, PersistentObjectManager<ChangeableValueObject ,GenericValueObjectImpl> aObjectManager)
	{
		this.proxyResolver = aProxyResolver;
		this.objectManager = aObjectManager;
	}

		
	@Override
	protected GenericValueObjectImpl createNoProxyObjectFor( TypedValueObject aValueModel) throws ObjectCreationException
	{
		GenericValueObjectImpl newObject = createValueObject( proxyResolver, aValueModel.getClassInfo(), false);
		
		setValuesInObject( newObject, aValueModel);
					
		return newObject;
	}
	
	
	protected GenericValueObjectImpl createValueObject(ProxyResolver aProxyResolver,  ClassInfo aClassInfo, boolean aIsNewFlag) throws ObjectCreationException 
	{
		return new GenericValueObjectImpl( aProxyResolver , aClassInfo, aIsNewFlag);
	}
	
	@Override
	public GenericValueObjectImpl createNewObjectFor(ClassInfo aClassInfo)	throws ObjectCreationException 
	{
		return createValueObject( proxyResolver, aClassInfo, true);
	}
	
	private boolean setValuesInObject( GenericValueObjectImpl newObject, TypedValueObject aValueModel)
	{
		boolean allAttributesSet = true;			
		for( AttributeInfo curInfo : aValueModel.getClassInfo().getAttributeInfoIterator()) {
				
			newObject.setValue(curInfo, aValueModel.getValue(curInfo));
		}

		for( AssociationInfo curAssocInfo :aValueModel.getClassInfo().getAssociationInfoIterator()) {
				
			if( aValueModel.hasValueFor(curAssocInfo)) {
				
//@todo				AssociationInfoListModel<GenericValueObjectImpl> associationInfoListModel 
//					= new AssociationInfoListModel<GenericValueObjectImpl>(objectManager, curAssocInfo, newObject.getValue(curAssocInfo), newObject);
//				newObject.setValue(curAssocInfo, associationInfoListModel);
			} else {
				newObject.setValue(curAssocInfo, new ProxyAssociationListModel<GenericValueObjectImpl>(objectManager, curAssocInfo,newObject ));
			}
		}
		
		return allAttributesSet;
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.persistence.ObjectCreatorWithProxy#createProxyObjectFor(de.comafra.model.metainfo.ClassInfo, de.comafra.model.value.ValueObject)
	 */
	@Override
	protected GenericValueObjectImpl createProxyObjectFor( TypedValueObject aValueModel) throws ObjectCreationException
	{
		GenericValueObjectImpl newObject = createValueObject(proxyResolver, aValueModel.getClassInfo(), false);

			
		for( AttributeInfo curInfo: aValueModel.getClassInfo().getAttributeInfoIterator()) {
				
			if( curInfo.isKey()) {
				newObject.setValue(curInfo, aValueModel.getValue(curInfo));
			}
		}
		
		return newObject;
	}
	


	/* (non-Javadoc)
	 * @see de.comafra.model.creator.UpdatableObjectCreator#updateValuesInObject(de.comafra.model.metainfo.ClassInfo, de.comafra.model.value.ValueObject)
	 */
	public void updateValuesInObject( TypedValueObject aValueModel, GenericValueObjectImpl aObjectToUpdate) throws ObjectCreationException
	{
		setValuesInObject(aObjectToUpdate, aValueModel);
	}


	@Override
	public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,	ChangeableValueObject aObjectToTransform) 
	{
		return new TypedWrappedValueObject( aObjectToTransform, aClassinfo) ;
	}	


}
