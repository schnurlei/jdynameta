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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.persistence.manager.PersistentOperation;

/**
 * @author rainer
 *
 */
public class DefaultPersistentOperation<TPersistentType> implements PersistentOperation<TPersistentType>
{
	private Operation operation;
	private ClassInfo classInfo;
	private TPersistentType objectToPersist;

	
	
	public DefaultPersistentOperation(Operation operation, ClassInfo classInfo, TPersistentType objectToPersist)
	{
		super();
		this.operation = operation;
		this.classInfo = classInfo;
		this.objectToPersist = objectToPersist;
	}
	
	public Operation getOperation()
	{
		return operation;
	}
	public void setOperation(Operation operation)
	{
		this.operation = operation;
	}
	public ClassInfo getClassInfo()
	{
		return classInfo;
	}
	public void setClassInfo(ClassInfo classInfo)
	{
		this.classInfo = classInfo;
	}
	public TPersistentType getObjectToPersist()
	{
		return objectToPersist;
	}
	public void setObjectToPersist(TPersistentType objectToPersist)
	{
		this.objectToPersist = objectToPersist;
	}

}
