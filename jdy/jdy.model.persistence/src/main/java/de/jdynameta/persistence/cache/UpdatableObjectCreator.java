/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.persistence.cache;

import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;

/**
 * @author Rainer
 *
 * @param <TTransformToValueObj>
 * @param <TCreatedUpdateableObjFromValueObj>
 */
public interface UpdatableObjectCreator<TTransformToValueObj, TCreatedUpdateableObjFromValueObj> extends ObjectTransformator<TTransformToValueObj, TCreatedUpdateableObjFromValueObj>
{
    public void updateValuesInObject(TypedValueObject aValueObject, TCreatedUpdateableObjFromValueObj objectToUpdate) throws ObjectCreationException;
}
