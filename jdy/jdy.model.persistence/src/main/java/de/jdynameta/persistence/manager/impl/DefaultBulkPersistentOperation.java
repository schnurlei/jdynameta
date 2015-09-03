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

import java.util.ArrayList;
import java.util.List;

import de.jdynameta.persistence.manager.BulkPersistentOperation;
import de.jdynameta.persistence.manager.PersistentOperation;

public class DefaultBulkPersistentOperation<TPersistentType> implements BulkPersistentOperation<TPersistentType>
{
	private List<PersistentOperation<TPersistentType>> persistObjectList;

	public DefaultBulkPersistentOperation() 
	{
		persistObjectList = new ArrayList<PersistentOperation<TPersistentType>>();
	}
	
	public void addObjectToPersist(PersistentOperation<TPersistentType> anObjectTopersist)
	{
		persistObjectList.add(anObjectTopersist);
	}
	
	@Override
	public List<PersistentOperation<TPersistentType>> getObjectsToPersist()
	{
		return persistObjectList;
	}
	
	

}
