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
package de.jdynameta.persistence.limbus;

import java.io.Serializable;

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;

/**
 * Implements an empty Cache which holds no Objects
 * @author rsc
 * @param <T_ValueObjectClass>
 *
 */
@SuppressWarnings("serial")
public class EmptyQueryCacheImpl<T_ValueObjectClass> implements QueryCache<T_ValueObjectClass>, Serializable
{
	
	private KeyGenerator<T_ValueObjectClass> keyGenerator;
	
	/**
	 * 
	 */
	public EmptyQueryCacheImpl() 
	{
		super();
		
	}


        @Override
        public boolean areAllObjectsInCache(ClassInfoQuery aFilter)
	{
		return false;
	}

        @Override
	public ObjectList<T_ValueObjectClass> getObjectsForFilter(ClassInfoQuery aFilter)
	{
		return new DefaultObjectList<>();
	}

        @Override
	public void insertObjectsForFilter(ClassInfoQuery aFilter, ObjectList<T_ValueObjectClass> aList)
	{
		
	}

        @Override
	public void addNewOject(T_ValueObjectClass aNewCreatedObject)
	{
		
	}

        @Override
	public void removeDeletedOject(T_ValueObjectClass aDeletedObjectKey)
	{
		
	}
	
        @Override
	public KeyGenerator<T_ValueObjectClass> getKeyGenerator()
	{
		return keyGenerator;
	}
	
        @Override
	public void setKeyGenerator( KeyGenerator<T_ValueObjectClass> aGenerator)
	{
		this.keyGenerator = aGenerator;
	}
	
}
