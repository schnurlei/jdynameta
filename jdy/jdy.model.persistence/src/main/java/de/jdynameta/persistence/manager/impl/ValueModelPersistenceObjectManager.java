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

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.persistence.cache.CachedObjectTransformator;
import de.jdynameta.persistence.cache.UpdatableObjectCreator;
import de.jdynameta.persistence.impl.proxy.SimpleProxyResolver;

/**
 * Persistent manager that handles GenericValueObjects
 * Caches read Objects and read dependent objects and associations deferred by proxy 
 * @author Rainer
 *
 * @version 04.07.2002
 */
@SuppressWarnings("serial")
public class ValueModelPersistenceObjectManager extends BasicPersistenceObjectManagerImpl<ChangeableValueObject, GenericValueObjectImpl>
{
	/**
	 * 
	 */
	public ValueModelPersistenceObjectManager( DbAccessConnection<ChangeableValueObject, GenericValueObjectImpl> aDbConnection) 
	{
		super(aDbConnection);
	}

	@Override
	protected CachedObjectTransformator<ChangeableValueObject, GenericValueObjectImpl> createObjectCreator()
	{
		SimpleProxyResolver<ChangeableValueObject, GenericValueObjectImpl> proxyResolver = new SimpleProxyResolver<ChangeableValueObject, GenericValueObjectImpl>(this.getDbConnect());
		UpdatableObjectCreator<ChangeableValueObject ,GenericValueObjectImpl> transformator = new ValueModelObjectCreatorWithProxy( proxyResolver, this);

		CachedObjectTransformator<ChangeableValueObject, GenericValueObjectImpl> cachedObjCreator 
			= new CachedObjectTransformator<ChangeableValueObject, GenericValueObjectImpl>();
		
		cachedObjCreator.setNewObjectCreator(transformator);
		return cachedObjCreator;
	}
	
}

