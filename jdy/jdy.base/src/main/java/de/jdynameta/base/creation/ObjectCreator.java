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
/*
 * Created on 26.05.2003
 *
 */
package de.jdynameta.base.creation;

import java.io.Serializable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;

/**
 * Used by the {@link de.jdynameta.dbaccess.base.ObjectReader} to transform the readed ValueObjects
 * into ApplicationObjects
 * @author rsc
 * @param <TCreatedObjFromValueObj>
 *
 */
public interface ObjectCreator<TCreatedObjFromValueObj> extends Serializable
{
	public TCreatedObjFromValueObj createObjectFor(TypedValueObject aTypedValueObject) throws ObjectCreationException;

	public TCreatedObjFromValueObj createNewObjectFor(ClassInfo aClassinfo) throws ObjectCreationException;
	
}
