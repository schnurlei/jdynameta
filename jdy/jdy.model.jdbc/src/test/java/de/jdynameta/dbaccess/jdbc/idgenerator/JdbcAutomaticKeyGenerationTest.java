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
package de.jdynameta.dbaccess.jdbc.idgenerator;

import java.sql.Connection;

import junit.framework.TestCase;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.impl.JdyAbstractAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyPrimitiveAttributeModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;

public abstract class JdbcAutomaticKeyGenerationTest extends TestCase
{
    private ObjectWriter writer;
    private JdbcConnectionHolder baseConnection;
    private JdyClassInfoModel testClassInfo;
    private AttributeInfo keyInfo;
    private JdyPrimitiveAttributeModel textAttr;
    private JdyAbstractAttributeModel notNullAttr;

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdbcAutomaticKeyGenerationTest(String name)
    {
        super(name);
    }

    public void testGenerateKey() throws JdyPersistentException
    {
        HashedValueObject testObj = new HashedValueObject();
        testObj.setValue(textAttr, "Test1");
        testObj.setValue(notNullAttr, "notNull1");
        ValueObject valueObj = writer.insertObjectInDb(testObj, testClassInfo);
        Object value = valueObj.getValue(keyInfo);

        assertTrue(value != null && value instanceof Number);

        HashedValueObject testObj2 = new HashedValueObject();
        testObj2.setValue(textAttr, "Test2");
        testObj2.setValue(notNullAttr, "notNull2");
        ValueObject valueObj2 = writer.insertObjectInDb(testObj2, testClassInfo);
        Object value2 = valueObj2.getValue(keyInfo);

        assertTrue(value2 != null && value2 instanceof Number);
        assertTrue(((Number) value2).longValue() > ((Number) value).longValue());

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

    /**
     * @param aBaseConnection
     */
    public void initializeTables(Connection aBaseConnection) throws Exception
    {
        SqlTableCreator tableCreator = createTableCreator(aBaseConnection);
        testClassInfo = new JdyClassInfoModel("TestClass");
        keyInfo = testClassInfo.addLongAttr("genrateKeyAttrId", 0, 1000).setIsKey(true).setGenerated(true);
        textAttr = testClassInfo.addTextAttr("normalAttr", 1000);
        notNullAttr = testClassInfo.addTextAttr("notNullNormalAttr", 1000).setNotNull(true);

        try
        {
            tableCreator.deleteTableForClassInfo(testClassInfo);
        } catch (JdyPersistentException excp)
        {
            // ignore
        }
        tableCreator.buildTableForClassInfo(testClassInfo);

    }

    protected abstract SqlTableCreator createTableCreator(Connection aConnection) throws Exception;

    protected abstract JdbcConnectionHolder createBaseConnection() throws Exception;

    protected abstract ObjectWriter createObjectWriter(JdbcConnectionHolder aConnection) throws JdyPersistentException;

}
