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
package de.jdynameta.base.view.appinfo;

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.view.ClassInfoAttrSource;

/**
 * Bridge between the application and the Persistence layer respectively the
 * workflow management
 *
 * @author Rainer Schneider
 * @param <TEditObj>
 */
public interface ViewManager<TEditObj extends ViewObject>
        extends PersistentObjectReader<TEditObj>, ClassInfoAttrSource
{

    @Override
    public ObjectList<TEditObj> loadObjectsFromDb(ClassInfoQuery query) throws JdyPersistentException;

    public void closeConnection() throws JdyPersistentException;

    /**
     *
     * @return true - when an Object is updated, delete or inserted all changed
     * dependent object are also made persistent
     */
    public boolean isWritingDependentObject();

}
