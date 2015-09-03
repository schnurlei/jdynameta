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
package de.jdynameta.dbaccess.jdbc.reader;

/*
       
 */
import java.sql.Connection;
import java.sql.SQLException;

import de.jdynameta.base.creation.ObjectReader;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.JdbcDbAcessUtil;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;

/**
 *
 */
public abstract class JdbcObjectReadTest extends JdyObjectReadTest
{
    private ObjectWriter writer;
    private ObjectReader reader;
    private JdbcConnectionHolder baseConnection;

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdbcObjectReadTest(String name)
    {
        super(name);
    }

    /**
     * INitialize ClassInfo
     *
     * @throws Exception DOCUMENT ME!
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        this.baseConnection = createBaseConnection();
        this.writer = createObjectWriter(baseConnection);
        this.reader = createObjectReader(baseConnection);
        initializeTables(baseConnection.getConnection());

    }

    @Override
    protected void tearDown() throws Exception
    {
        this.baseConnection.close();
    }

    protected JdbcObjectReader createObjectReader(JdbcConnectionHolder aBaseConnection)
    {
        return new JdbcObjectReader(aBaseConnection, new JDyDefaultRepositoryTableMapping());
    }

    @Override
    protected ObjectWriter getObjectWriter()
    {
        return writer;
    }

    @Override
    protected ObjectReader getObjectReader()
    {
        return this.reader;
    }

    protected abstract JdbcConnectionHolder createBaseConnection() throws Exception;

    protected abstract SqlTableCreator createTableCreator(Connection aConnection) throws Exception;

    protected ObjectWriter createObjectWriter(JdbcConnectionHolder aConnection) throws JdyPersistentException
    {
        return new JdyJdbcObjectWriter(aConnection, new JDyDefaultRepositoryTableMapping());
    }

    /**
     * DOCUMENT ME!
     *
     * @param aBaseConnection
     * @throws InvalidClassInfoException
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void initializeTables(Connection aBaseConnection) throws Exception
    {
        SqlTableCreator tableCreator = createTableCreator(aBaseConnection);
        JdbcDbAcessUtil.initializeTables(baseConnection.getConnection(), tableCreator);
    }

    @Override
    protected void assertValueIsSet(ClassInfo aClassInfo, PrimitiveAttributeInfo aPrimitivAttr, ValueObject aValue)
    {
        try
        {
            if (!JdbcDbAcessUtil.isValueSet(baseConnection.getConnection(), aClassInfo, aPrimitivAttr, aValue))
            {
                fail("Value not set Table: " + aClassInfo.getExternalName() + " Column: " + aPrimitivAttr.getExternalName() + " Value: " + aValue);
            }
        } catch (SQLException | JdyPersistentException excp)
        {
            excp.printStackTrace();
            fail("Exception on getting value " + excp.getMessage());
        }
    }

}
