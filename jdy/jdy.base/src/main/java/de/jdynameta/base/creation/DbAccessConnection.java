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
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * Defines a Connection to a database to read and write objects
 * TTransformToValueObj type of the objects which are written to the db
 * TCreatedFromValueObj type of the objects which are returned from the db
 *
 * Before the objects could be written they must be converted to ValueObjects by
 * a ObjectTransformator
 *
 * @author Rainer
 *
 */
public interface DbAccessConnection<TTransformToValueObj, TCreatedFromValueObj> extends Serializable
  {

    /**
     * Delete the Object in the DB defined by the specified ClassInfo
     *
     * @author	Rainer Schneider
     * @return
     * @throws de.jdynameta.base.value.JdyPersistentException
     */
    public void deleteObjectInDb(TTransformToValueObj objToDelete, ClassInfo aInfo)
            throws JdyPersistentException;

    /**
     * Read the objects defined by the query from the db
     *
     * @return JdbcObjectReader
     * @throws de.jdynameta.base.value.JdyPersistentException
     */
    public ObjectList<TCreatedFromValueObj> loadValuesFromDb(ClassInfoQuery filter)
            throws JdyPersistentException;

    public TCreatedFromValueObj insertObjectInDb(TTransformToValueObj aObjToInsert, ClassInfo aInfo)
            throws JdyPersistentException;

    public void updateObjectToDb(TTransformToValueObj aObjToUpdate, ClassInfo aInfo)
            throws JdyPersistentException;

    public ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> getObjectTransformator();

    public void setObjectTransformator(ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> aTransformator);

    public void closeConnection() throws JdyPersistentException;

    public void commitTransaction() throws JdyPersistentException;

    public void rollbackTransaction() throws JdyPersistentException;

    public void startTransaction() throws JdyPersistentException;

  }
