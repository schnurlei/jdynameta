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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;

/**
 * Used by the {@link de.jdynameta.dbaccess.sql.connection.JdbcConnection} to
 * transform ApplicationObjects into ValueObjects
 *
 * @author Rainer
 * @param <TTransformToValueObj>
 * @param <TCreatedFromValueObj>
 *
 */
public interface ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> extends ObjectCreator<TCreatedFromValueObj>
  {

    /**
     * @see ObjectCreator#createObjectFor(ClassInfo , ValueObject ) the opposite
     * Method
     * @param aClassinfo
     * @param aObjectToTransform
     * @return
     */
    public TypedValueObject getValueObjectFor(ClassInfo aClassinfo, TTransformToValueObj aObjectToTransform);

  }
