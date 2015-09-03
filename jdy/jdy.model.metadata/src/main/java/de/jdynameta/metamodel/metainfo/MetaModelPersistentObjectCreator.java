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
package de.jdynameta.metamodel.metainfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface;
import de.jdynameta.base.value.defaultimpl.TypedReflectionValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.metamodel.generation.MetaModelClassFileGenerator;
import de.jdynameta.metamodel.metainfo.model.impl.ObjectReferenceAttributeInfoModelImpl;
import de.jdynameta.persistence.impl.proxy.ReflectionObjectCreator;
import de.jdynameta.persistence.manager.impl.AssociationInfoListModel;

/**
 * Creates an GenericPersitentObjectCreator for the ClassInfo
 * 
 * Use an external Object creator to create referenced Objects
 * (maybe they are cached and have not be created)  
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class MetaModelPersistentObjectCreator extends ReflectionObjectCreator<ValueObject,TypedReflectionObjectInterface>
{
	private final MetaModelPersistenceObjectManager objectManager;
	
	public MetaModelPersistentObjectCreator(ObjectCreator aReferencedObjectCreator, ProxyResolver aProxyResolver
										, MetaModelPersistenceObjectManager aObjectManager)
	{
		super( aProxyResolver, new ModelNameCreator(), aObjectManager);
		this.objectManager = aObjectManager;
	}

		
	@Override
	protected TypedReflectionValueObject createNoProxyObjectFor( TypedValueObject aValueModel) throws ObjectCreationException
	{
		TypedReflectionValueObject newObject;
		try
		{
			final Class<? extends Object> metaClass = Class.forName(getNameCreator().getAbsolutClassNameFor(aValueModel.getClassInfo()));
			
			Constructor<? extends Object> classConstructor = metaClass.getConstructor(new Class[] {});
			newObject = (TypedReflectionValueObject)  classConstructor.newInstance(new Object[]{});
			
			setValuesInObject(metaClass, newObject, aValueModel.getClassInfo(), aValueModel);
		} catch (Exception excp)
		{
			throw new ObjectCreationException(excp);
		} 
		return newObject;
	}
	
	
	@Override
	protected ObjectList<TypedReflectionObjectInterface> createNonProxyObjectList(AssociationInfo aAssocInfo, ObjectList aListToSet, Object aParent) 
	{
		return new AssociationInfoListModel<TypedReflectionObjectInterface>(objectManager, aAssocInfo, aListToSet, aParent);
	}
	
//	protected ObjectList<ValueObject>  createProxyObjectList(AssociationInfo aAssocInfo, Object aParent) 
//	{
//		return new ProxyAssociationListModel<ValueObject>(objectManager, aAssocInfo, (ValueObject) aParent );
//	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.persistence.ObjectCreatorWithProxy#createProxyObjectFor(de.comafra.model.metainfo.ClassInfo, de.comafra.model.value.ValueObject)
	 */
	@Override
	protected TypedReflectionObjectInterface createProxyObjectFor( TypedValueObject aValueModel) throws ObjectCreationException
	{
		try
		{
			Method aMethod = ObjectReferenceAttributeInfoModelImpl.class.getMethod("getInternalName", (Class[]) null );
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		TypedReflectionObjectInterface newObject = super.createProxyObjectFor( aValueModel);

		return newObject;
	}
	
	public TypedValueObject getValueObjectFor(ClassInfo aClassinfo, ValueObject aObjectToTransform)
	{
		return new TypedWrappedValueObject( aObjectToTransform, aClassinfo) ;
	}
	
	
	public static class ModelNameCreator extends MetaModelClassFileGenerator.ModelNameCreator
	{
		@Override
		public String getReferenceClassNameFor(ClassInfo aInfo)
		{
			return  "de.jdynameta.metamodel.metainfo.model" +"." + aInfo.getInternalName();
			//.substring(0, aInfo.getInternalName().length() - "Model".length());
		}
		
		@Override
		public String getPackageNameFor(ClassInfo aInfo) 
		{
			return "de.jdynameta.metamodel.metainfo.model.impl";
		}
	}
				



}
