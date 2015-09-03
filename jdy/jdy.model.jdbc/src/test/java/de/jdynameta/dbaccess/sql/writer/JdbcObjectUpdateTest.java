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
package de.jdynameta.dbaccess.sql.writer;

/*
       
 */
import java.sql.Connection;
import java.sql.SQLException;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.JdbcDbAcessUtil;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;

/**
 *
 */
public abstract class JdbcObjectUpdateTest extends JdyObjectUpdateTest
{
    private ObjectWriter writer;
    private JdbcConnectionHolder baseConnection;

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdbcObjectUpdateTest(String name)
    {
        super(name);
    }

    /**
     * Initialize ClassInfo
     *
     * @throws Exception DOCUMENT ME!
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        this.baseConnection = createBaseConnection();
        this.baseConnection.getConnection().setAutoCommit(false);
        this.writer = createObjectWriter(baseConnection);
        initializeTables(baseConnection.getConnection());

    }

    @Override
    protected void tearDown() throws Exception
    {
        this.baseConnection.close();
    }

    @Override
    protected ObjectWriter getObjectWriter()
    {

        return writer;
    }

    protected abstract ObjectWriter createObjectWriter(JdbcConnectionHolder aConnection)
            throws JdyPersistentException;

    protected abstract JdbcConnectionHolder createBaseConnection() throws Exception;

    protected abstract SqlTableCreator createTableCreator(Connection aConnection) throws Exception;

    /**
     * @param aBaseConnection
     * @throws java.lang.Exception
     */
    public void initializeTables(Connection aBaseConnection) throws Exception
    {
        SqlTableCreator tableCreator = createTableCreator(aBaseConnection);
        initializeTables(baseConnection.getConnection(), tableCreator);
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

    /**
     * @param aBaseConnection
     * @param aTableCreator
     * @throws java.lang.Exception
     */
    public static void initializeTables(Connection aBaseConnection, SqlTableCreator aTableCreator) throws Exception
    {
        for (ClassInfo curInfo : new ComplexTestDataMetaInfoRepository().getAllClassInfosIter())
        {
            try
            {
                aTableCreator.deleteTableForClassInfo(curInfo);
            } catch (JdyPersistentException excp)
            {
// 				excp.printStackTrace();
            }
            aTableCreator.buildTableForClassInfo(curInfo);
        }
    }

}
