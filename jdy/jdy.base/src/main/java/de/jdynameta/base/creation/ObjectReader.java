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

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 */
public interface ObjectReader extends Serializable
  {

    /**
     * Builds a Select statement from the filter and read the values from the DB
     *
     * @author	Rainer Schneider
     */
    public abstract <TCreatedObjFromValueObj> ObjectList<TCreatedObjFromValueObj> loadValuesFromDb(ClassInfoQuery filter,
            ObjectCreator<TCreatedObjFromValueObj> aObjCreator) throws JdyPersistentException;
  }
