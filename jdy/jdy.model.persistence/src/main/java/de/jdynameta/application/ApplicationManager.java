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
package de.jdynameta.application;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.view.ClassInfoAttrSource;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 * Bridge between the application and the Persistence layer respectively the
 * workflow management
 *
 * @author Rainer Schneider
 * @param <TEditObj>
 */
public interface ApplicationManager<TEditObj extends ApplicationObj>
        extends PersistentObjectReader<TEditObj>, ClassInfoAttrSource
{

    /**
     * Insert or update depending on the persistent sate of the Object in the DB
     *
     * @param editedObject
     * @param aContext TODO
     * @throws JdyPersistentException
     */
    public void saveObject(TEditObj editedObject, WorkflowCtxt aContext) throws JdyPersistentException;

    /**
     * reload the object from the database
     *
     * @param editedObject
     * @throws JdyPersistentException
     */
    public void refreshObject(TEditObj editedObject) throws JdyPersistentException;

    /**
     * Create a new Object for the given ClassInfo
     *
     * @param aClassInfo
     * @param aContext TODO
     * @return
     * @throws ObjectCreationException
     */
    public TEditObj createObject(ClassInfo aClassInfo, WorkflowCtxt aContext) throws ObjectCreationException;

    @Override
    public ObjectList<TEditObj> loadObjectsFromDb(ClassInfoQuery query) throws JdyPersistentException;

    /**
     * Delete the Object in the DB
     *
     * @param objToDel
     * @param aContext TODO
     * @throws JdyPersistentException
     */
    public void deleteObject(TEditObj objToDel, WorkflowCtxt aContext) throws JdyPersistentException;

    /**
     * Get a Workflow manager which handles the Workflow steps and the
     * Customability of the Attributes
     *
     * @return
     */
    public WorkflowManager<TEditObj> getWorkflowManager();

    public void closeConnection() throws JdyPersistentException;

    /**
     *
     * @return true - when an Object is updated, delete or inserted all changed
     * dependent object are also made persistent
     */
    public boolean isWritingDependentObject();

    public TEditObj cloneObject(TEditObj editedObject)
            throws ObjectCreationException;

}
