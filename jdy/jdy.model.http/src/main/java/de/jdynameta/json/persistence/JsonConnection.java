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
package de.jdynameta.json.persistence;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.json.client.JsonHttpObjectReader;
import de.jdynameta.json.client.JsonHttpObjectWriter;

@SuppressWarnings("serial")
public class JsonConnection<TTransformToValueObj, TCreatedFromValueObj> implements DbAccessConnection<TTransformToValueObj, TCreatedFromValueObj>
{
    private final JsonHttpObjectWriter writer;
    private final JsonHttpObjectReader reader;
    private ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> objectTransformator;

    public JsonConnection(String aHost, int aPort, String aBasePath, String aMetaModelNamespace, ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> aObjectTransformator) throws JdyPersistentException
    {
        assert (aObjectTransformator != null);
        this.writer = new JsonHttpObjectWriter(aHost, aPort, aBasePath, aMetaModelNamespace);
        this.reader = new JsonHttpObjectReader(aHost, aPort, aBasePath, aMetaModelNamespace);
        this.objectTransformator = aObjectTransformator;
    }

    @Override
    public void closeConnection() throws JdyPersistentException
    {

    }

    @Override
    public void deleteObjectInDb(TTransformToValueObj aObjToDelete, ClassInfo aInfo) throws JdyPersistentException
    {
        this.writer.deleteObjectInDb(objectTransformator.getValueObjectFor(aInfo, aObjToDelete), aInfo);
    }

    @Override
    public TCreatedFromValueObj insertObjectInDb(TTransformToValueObj aObjToInsert, ClassInfo aInfo)
            throws JdyPersistentException
    {

        ValueObject valueObj = objectTransformator.getValueObjectFor(aInfo, aObjToInsert);
        TypedValueObject readedObj = writer.insertObjectInDb(valueObj, aInfo);

        try
        {
            return objectTransformator.createObjectFor(readedObj);
        } catch (ObjectCreationException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public ObjectList<TCreatedFromValueObj> loadValuesFromDb(ClassInfoQuery aFilter) throws JdyPersistentException
    {
        return reader.loadValuesFromDb(aFilter, this.objectTransformator);
    }

    @Override
    public void updateObjectToDb(TTransformToValueObj aObjToUpdate, ClassInfo aInfo) throws JdyPersistentException
    {
        this.writer.updateObjectToDb(objectTransformator.getValueObjectFor(aInfo, aObjToUpdate), aInfo);

    }

    /**
     *
     * @param actionName
     * @param aObjToUpdate
     * @param aInfo
     * @throws JdyPersistentException
     */
    public void executeWorkflowAction(String actionName, TTransformToValueObj aObjToUpdate, ClassInfo aInfo) throws JdyPersistentException
    {
        this.writer.executeWorkflowAction(actionName, objectTransformator.getValueObjectFor(aInfo, aObjToUpdate), aInfo);

    }

    @Override
    public ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> getObjectTransformator()
    {
        return this.objectTransformator;
    }

    @Override
    public void setObjectTransformator(ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> aTransformator)
    {
        this.objectTransformator = aTransformator;
    }

    @Override
    public void commitTransaction() throws JdyPersistentException
    {
        throw new UnsupportedOperationException("Not Yet IMplemented");

    }

    @Override
    public void rollbackTransaction() throws JdyPersistentException
    {
        throw new UnsupportedOperationException("Not Yet IMplemented");
    }

    @Override
    public void startTransaction() throws JdyPersistentException
    {
        throw new UnsupportedOperationException("Not Yet IMplemented");
    }

}
