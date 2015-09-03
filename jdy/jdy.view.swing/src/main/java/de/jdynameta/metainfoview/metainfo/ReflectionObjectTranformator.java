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
package de.jdynameta.metainfoview.metainfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.persistence.impl.proxy.ReflectionObjectCreator;
import de.jdynameta.persistence.manager.PersistentObjectManager;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class ReflectionObjectTranformator extends ReflectionObjectCreator implements ObjectTransformator
{
	/**
	 * @param aMainObjectCreator
	 * @param aProxyResolver
	 */
	public ReflectionObjectTranformator( ProxyResolver aProxyResolver, PersistentObjectManager aObjectManager)
	{
		super( aProxyResolver, aObjectManager);
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.persistence.ObjectTransformator#getValueObjectFor(de.comafra.model.metainfo.ClassInfo, java.lang.Object)
	 */
	public TypedValueObject getValueObjectFor( ClassInfo aClassinfo, Object aObjectToTransform)
	{
		return (TypedValueObject) aObjectToTransform;
	}
	
	/**
	 * Create object by the reflection Model
	 */
	public Object createNewObjectFor(ClassInfo aClassInfo) throws ObjectCreationException
	{
		try
		{
			final Class metaClass = Class.forName(getNameCreator().getAbsolutClassNameFor(aClassInfo));
				
			Constructor classConstructor = metaClass.getConstructor((Class[]) null);
			return classConstructor.newInstance((Object[]) null);
		} catch (SecurityException e){
			throw new ObjectCreationException(e);
		} catch (IllegalArgumentException e){
			throw new ObjectCreationException(e);
		} catch (ClassNotFoundException e){
			throw new ObjectCreationException(e);
		} catch (NoSuchMethodException e){
			throw new ObjectCreationException(e);
		} catch (InstantiationException e){
			throw new ObjectCreationException(e);
		} catch (IllegalAccessException e){
			throw new ObjectCreationException(e);
		} catch (InvocationTargetException e){
			throw new ObjectCreationException(e);
		} 
	}
	
}
