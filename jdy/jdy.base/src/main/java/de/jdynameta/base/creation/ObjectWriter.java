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
package de.jdynameta.base.creation;

import java.io.Serializable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;

/**
 * @author Rainer
 *
 */
public interface ObjectWriter extends Serializable
  {

    public abstract void deleteObjectInDb(ValueObject objToDelete, ClassInfo aInfo)
            throws JdyPersistentException;

    public abstract TypedValueObject insertObjectInDb(ValueObject aObjToInsert, ClassInfo aInfo)
            throws JdyPersistentException;

    public abstract void updateObjectToDb(ValueObject objToModify, ClassInfo aInfo)
            throws JdyPersistentException;
  }
