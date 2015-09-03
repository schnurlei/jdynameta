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
package de.jdynameta.dbaccess.jdbc.connection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.creation.ObjectReader;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.JdyRepositoryTableMapping;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.dbaccess.jdbc.reader.JdbcObjectReader;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;

/**
 *
 * JdbcConnection to database. Handels transactions and use JdyJdbcObjectWriter
 * and JdbcObjectReader to read Object from db
 *
 * @author <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 * Copyright 2006 Rainer Schneider, 89297 Schiessen, Roggenburg
 * @param <TObjToWrite>
 * @param <TReadedObj>
 */
@SuppressWarnings("serial")
public class JdbcConnection<TObjToWrite, TReadedObj> implements DbAccessConnection<TObjToWrite, TReadedObj>, Serializable
{
    private DataSource dataSource = null;

    private transient JdbcConnectionHolder holder;

    private JdyJdbcObjectWriter writer;
    private JdbcObjectReader reader;
    private ObjectTransformator<TObjToWrite, TReadedObj> objectTransformator;
    private boolean inTransaction;

    public JdbcConnection(DataSource aDataSource)
    {

        this(aDataSource, null, new JDyDefaultRepositoryTableMapping());
    }

    public JdbcConnection(DataSource aDataSource, ObjectTransformator<TObjToWrite, TReadedObj> aObjCreator, JdyRepositoryTableMapping tableMapping)
    {

        this.dataSource = aDataSource;
        this.objectTransformator = aObjCreator;
        this.holder = new JdbcConnectionHolder();
        this.writer = new JdyJdbcObjectWriter(holder, tableMapping);
        this.reader = new JdbcObjectReader(holder, tableMapping);
    }

    @Override
    public ObjectTransformator<TObjToWrite, TReadedObj> getObjectTransformator()
    {
        return this.objectTransformator;
    }

    @Override
    public void setObjectTransformator(ObjectTransformator<TObjToWrite, TReadedObj> aTransformator)
    {
        this.objectTransformator = aTransformator;
    }

    private synchronized void connectToDB() throws JdyPersistentException
    {
        try
        {
            Connection dbConnection = dataSource.getConnection();
            dbConnection.setAutoCommit(false);
            holder.setConnection(dbConnection);

        } catch (SQLException catchedExc)
        {

            throw UtilSql.getDefault().buildDbException(catchedExc);
        }
    }

    /**
     * Return the Object wich reads and writes Objects to the DB
     *
     * @author	Rainer Schneider
     * @return
     */
    protected ObjectWriter getObjectWriter()
    {
        return writer;
    }

    /**
     * Returns the reader.
     *
     * @return JdbcObjectReader
     */
    protected ObjectReader getReader()
    {
        return reader;
    }

    @Override
    public void closeConnection() throws JdyPersistentException
    {
        commitAndClose();

    }

    @Override
    public ObjectList<TReadedObj> loadValuesFromDb(ClassInfoQuery aFilter)
            throws JdyPersistentException
    {
        if (!inTransaction)
        {
            connectToDB();
        }
        try
        {
            ObjectList<TReadedObj> result = reader.loadValuesFromDb(aFilter, this.objectTransformator);
            if (!inTransaction)
            {
                commitAndClose();
            }

            return result;
        } catch (Throwable ex)
        {

            if (!inTransaction)
            {
                rollbackHolderConnection();
            }
            throw new JdyPersistentException(ex);
        }
    }

    @Override
    public synchronized void deleteObjectInDb(TObjToWrite objToDelete, ClassInfo aInfo) throws JdyPersistentException
    {
        if (!inTransaction)
        {
            connectToDB();
        }
        try
        {
            this.writer.deleteObjectInDb(objectTransformator.getValueObjectFor(aInfo, objToDelete), aInfo);
            if (!inTransaction)
            {
                commitAndClose();
            }
        } catch (Throwable ex)
        {

            if (!inTransaction)
            {
                rollbackHolderConnection();
            }
            throw new JdyPersistentException(ex);
        }
    }

    @Override
    public synchronized TReadedObj insertObjectInDb(TObjToWrite aObjToInsert, ClassInfo aInfo)
            throws JdyPersistentException
    {
        if (!inTransaction)
        {
            connectToDB();
        }
        try
        {
            TypedValueObject newObject = this.writer.insertObjectInDb(objectTransformator.getValueObjectFor(aInfo, aObjToInsert), aInfo);
            TReadedObj insertedObj = objectTransformator.createObjectFor(newObject);
            if (!inTransaction)
            {
                commitAndClose();
            }

            return insertedObj;
        } catch (ObjectCreationException | JdyPersistentException ex)
        {
            if (!inTransaction)
            {
                rollbackHolderConnection();
            }
            throw new JdyPersistentException(ex);
        }

    }

    @Override
    public synchronized void commitTransaction() throws JdyPersistentException
    {
        if (!inTransaction)
        {
            throw new JdyPersistentException("Transaction not open");
        }

        commitAndClose();
    }

    private void commitAndClose() throws JdyPersistentException
    {
        try
        {
            this.holder.commit();
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } finally
        {
            inTransaction = false;
            try
            {
                this.holder.close();
            } catch (SQLException ex)
            {
                throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
            }
        }
    }

    @Override
    public synchronized void rollbackTransaction() throws JdyPersistentException
    {
        if (!inTransaction)
        {
            throw new JdyPersistentException("Transaction not open");
        }

        rollbackHolderConnection();
    }

    private void rollbackHolderConnection() throws JdyPersistentException
    {
        try
        {
            this.holder.rollback();
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } finally
        {
            inTransaction = false;
            try
            {
                this.holder.close();
            } catch (SQLException ex)
            {
                throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
            }
        }
    }

    @Override
    public synchronized void startTransaction() throws JdyPersistentException
    {
        if (inTransaction)
        {
            throw new JdyPersistentException("Transaction already open");
        }
        connectToDB();
        inTransaction = true;
    }

    @Override
    public synchronized void updateObjectToDb(TObjToWrite aObjToUpdate, ClassInfo aInfo)
            throws JdyPersistentException
    {
        if (!inTransaction)
        {
            connectToDB();
        }
        try
        {
            this.writer.updateObjectToDb(objectTransformator.getValueObjectFor(aInfo, aObjToUpdate), aInfo);
            if (!inTransaction)
            {
                this.commitAndClose();
            }
        } catch (Throwable ex)
        {

            if (!inTransaction)
            {
                rollbackHolderConnection();
            }
            throw new JdyPersistentException(ex);
        }

    }

}
