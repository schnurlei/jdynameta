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
package de.jdynameta.persistence.manager;

import java.util.List;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;

/**
 * Manager to read and write objects from an DbConnection. It is responsible are
 * - Caching Objects - care for Object Identity - Transform db-Objects into
 * Application Objects The TReadedObj objects are objects read to the
 * application from the Persistence Manager. They could be different to the
 * TObjToWrite written from the application to the Persistence Manager
 *
 * @author Rainer
 * @param <TObjToWrite>
 * @param <TReadedObj>
 *
 */
public interface PersistentObjectManager<TObjToWrite, TReadedObj>
        extends PersistentObjectReader<TReadedObj>
{
    /**
     * Create new Application Object for the given Class Info
     *
     * @param aInfo
     * @return
     * @throws ObjectCreationException
     */
    public TReadedObj createNewObject(ClassInfo aInfo) throws ObjectCreationException;

    /**
     * make new Object Persistent
     *
     * @param objToSave
     * @param aInfo
     * @return 
     * @throws JdyPersistentException
     */
    public TReadedObj insertObject(TObjToWrite objToSave, ClassInfo aInfo) throws JdyPersistentException;

    /**
     * make the changes in an Application Object persistent
     *
     * @param objToSave
     * @param aInfo
     * @throws JdyPersistentException
     */
    public void updateObject(TObjToWrite objToSave, ClassInfo aInfo) throws JdyPersistentException;

    public void refreshObject(TObjToWrite objToRefresh, ClassInfo info) throws JdyPersistentException;

    /**
     * Delete the given Object from the Persistent storage
     *
     * @param aObjToDel
     * @param aClassInfo
     * @throws de.jdynameta.base.value.JdyPersistentException
     */
    public abstract void deleteObject(TObjToWrite aObjToDel, ClassInfo aClassInfo) throws JdyPersistentException;

    public List<PersistentOperationResult<TObjToWrite, TReadedObj>> persistObjects(BulkPersistentOperation<TObjToWrite> bulkOperation) throws JdyPersistentException;

    public void closeConnection() throws JdyPersistentException;

}
