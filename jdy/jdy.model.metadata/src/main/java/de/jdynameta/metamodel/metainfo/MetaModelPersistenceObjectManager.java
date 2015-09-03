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

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface;
import de.jdynameta.persistence.cache.CachedObjectTransformator;
import de.jdynameta.persistence.impl.proxy.SimpleProxyResolver;
import de.jdynameta.persistence.limbus.KeyGenerator;
import de.jdynameta.persistence.limbus.ValueModelKeyGenerator;
import de.jdynameta.persistence.manager.impl.BasicPersistenceObjectManagerImpl;

/**
 * @author Rainer
 *
 * @version 04.07.2002
 */
@SuppressWarnings("serial")
public class MetaModelPersistenceObjectManager extends BasicPersistenceObjectManagerImpl<ValueObject, TypedReflectionObjectInterface>
{
	
	/**
	 * 
	 */
	public MetaModelPersistenceObjectManager( DbAccessConnection<ValueObject, TypedReflectionObjectInterface> aDbConnection) 
	{
		super(aDbConnection);
		
	}
	
	@Override
	protected CachedObjectTransformator<ValueObject, TypedReflectionObjectInterface> createObjectCreator()
	{
		SimpleProxyResolver<ValueObject, TypedReflectionObjectInterface> proxyResolver 
				= new SimpleProxyResolver<ValueObject, TypedReflectionObjectInterface>(this.getDbConnect());

		CachedObjectTransformator<ValueObject, TypedReflectionObjectInterface> cachedObjCreator 
			= new CachedObjectTransformator<ValueObject, TypedReflectionObjectInterface>();
		
		MetaModelPersistentObjectCreator transformator = new MetaModelPersistentObjectCreator(cachedObjCreator, proxyResolver, this);
		cachedObjCreator.setNewObjectCreator(transformator);
		return cachedObjCreator;
	}
	
}

