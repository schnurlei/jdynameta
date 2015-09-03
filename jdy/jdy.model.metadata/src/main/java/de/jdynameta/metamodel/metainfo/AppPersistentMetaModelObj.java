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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface;
import de.jdynameta.persistence.state.ApplicationModel;

/**
 * Convenience class to define a PersistentValueObjectImpl which holds an ReflectionValueObject
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class AppPersistentMetaModelObj extends ApplicationModel<TypedReflectionObjectInterface> 
{

	public AppPersistentMetaModelObj(ClassInfo aClassInfo, TypedReflectionObjectInterface aWrappedValueObject, boolean isNew, PersistentObjectReader<AppPersistentMetaModelObj> persistentReader) 
	{
		super(aClassInfo, aWrappedValueObject, isNew, persistentReader);
	}

	public  AppPersistentMetaModelObj createModelFor( ClassInfo aClassInfo,TypedReflectionObjectInterface persistentObj, boolean isNew)
	{
		return new AppPersistentMetaModelObj(aClassInfo, persistentObj, isNew, (PersistentObjectReader<AppPersistentMetaModelObj>) getPersistentReader());
	}

}
