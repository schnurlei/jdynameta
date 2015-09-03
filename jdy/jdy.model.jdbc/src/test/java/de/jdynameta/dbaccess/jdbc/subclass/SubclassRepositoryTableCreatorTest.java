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
package de.jdynameta.dbaccess.jdbc.subclass;

import junit.framework.TestCase;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.JdbcTestConnection;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;
import de.jdynameta.testcommon.model.subclass.SubclassRepository;

/**
 * Test the Table Creation for the Subclasses
 *
 * @author rs
 *
 */
public class SubclassRepositoryTableCreatorTest extends TestCase
{

    private SubclassRepository getRepository()
    {
        return new SubclassRepository();
    }

    public void testTableMappingReferencedObject() throws JdyPersistentException
    {
        JdbcTestConnection testConnection = new JdbcTestConnection();
        ClassInfo refType = getRepository().getReferencedObjectType();
        new JdyJdbcTableCreator(testConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler()).buildTableForClassInfo(refType);
        assertEquals(2, testConnection.getStatementStack().size());
        assertEquals("CREATE TABLE ReferencedObject(refKey1 BIGINT  NOT NULL ,refKey2 CHAR(50)  NOT NULL ,refData DATE )", testConnection.getStatementStack().get(0).getExecuteStatement());
        assertEquals("ALTER TABLE  ReferencedObject ADD PRIMARY KEY  (refKey1,refKey2)", testConnection.getStatementStack().get(1).getExecuteStatement());
    }

    public void testTableMappingMainClass() throws JdyPersistentException
    {
        JdbcTestConnection testConnection = new JdbcTestConnection();
        ClassInfo mainType = getRepository().getMainClassType();
        new JdyJdbcTableCreator(testConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler()).buildTableForClassInfo(mainType);
        assertEquals(2, testConnection.getStatementStack().size());
        assertEquals("CREATE TABLE mainClass(keyMain1_refKey1 BIGINT  NOT NULL ,keyMain1_refKey2 CHAR(50)  NOT NULL ,mainData CHAR(50) ,keyMain2 CHAR(50)  NOT NULL )", testConnection.getStatementStack().get(0).getExecuteStatement());
        assertEquals("ALTER TABLE  mainClass ADD PRIMARY KEY  (keyMain1_refKey1,keyMain1_refKey2,keyMain2)", testConnection.getStatementStack().get(1).getExecuteStatement());
    }

    public void testTableMappingSubclassLevel1() throws JdyPersistentException
    {
        JdbcTestConnection testConnection = new JdbcTestConnection();
        ClassInfo levl1Type = getRepository().getSubclassLevel1Type();
        new JdyJdbcTableCreator(testConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler()).buildTableForClassInfo(levl1Type);
        assertEquals(2, testConnection.getStatementStack().size());
        assertEquals("CREATE TABLE subclassLevel1(keyMain1_refKey1 BIGINT  NOT NULL ,keyMain1_refKey2 CHAR(50)  NOT NULL ,keyMain2 CHAR(50)  NOT NULL ,dataLvl1 CHAR(50) )", testConnection.getStatementStack().get(0).getExecuteStatement());
        assertEquals("ALTER TABLE  subclassLevel1 ADD PRIMARY KEY  (keyMain1_refKey1,keyMain1_refKey2,keyMain2)", testConnection.getStatementStack().get(1).getExecuteStatement());
    }

    public void testTableMappingFirstSubclassLevel2() throws JdyPersistentException
    {
        JdbcTestConnection testConnection = new JdbcTestConnection();
        ClassInfo levl2Type = getRepository().getFirstSubLevel2Type();
        new JdyJdbcTableCreator(testConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler()).buildTableForClassInfo(levl2Type);
        assertEquals(2, testConnection.getStatementStack().size());
        assertEquals("CREATE TABLE firstSubLevel2(keyMain1_refKey1 BIGINT  NOT NULL ,keyMain1_refKey2 CHAR(50)  NOT NULL ,keyMain2 CHAR(50)  NOT NULL ,dataALvl2 CHAR(50) )", testConnection.getStatementStack().get(0).getExecuteStatement());
        assertEquals("ALTER TABLE  firstSubLevel2 ADD PRIMARY KEY  (keyMain1_refKey1,keyMain1_refKey2,keyMain2)", testConnection.getStatementStack().get(1).getExecuteStatement());
    }

    public void testTableMappingSecondSubclassLevel2() throws JdyPersistentException
    {
        JdbcTestConnection testConnection = new JdbcTestConnection();
        ClassInfo levl2Type = getRepository().getSecondSubLevel2Type();
        new JdyJdbcTableCreator(testConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler()).buildTableForClassInfo(levl2Type);
        assertEquals(2, testConnection.getStatementStack().size());
        assertEquals("CREATE TABLE secondSubLevel2(keyMain1_refKey1 BIGINT  NOT NULL ,keyMain1_refKey2 CHAR(50)  NOT NULL ,keyMain2 CHAR(50)  NOT NULL ,dataBLvl2 CHAR(50) )", testConnection.getStatementStack().get(0).getExecuteStatement());
        assertEquals("ALTER TABLE  secondSubLevel2 ADD PRIMARY KEY  (keyMain1_refKey1,keyMain1_refKey2,keyMain2)", testConnection.getStatementStack().get(1).getExecuteStatement());
    }

    public void testTableMappingSubLevel3() throws JdyPersistentException
    {
        JdbcTestConnection testConnection = new JdbcTestConnection();
        ClassInfo levl3Type = getRepository().getSubLevel3Type();
        new JdyJdbcTableCreator(testConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler()).buildTableForClassInfo(levl3Type);
        assertEquals(2, testConnection.getStatementStack().size());
        assertEquals("CREATE TABLE subLevel3(keyMain1_refKey1 BIGINT  NOT NULL ,keyMain1_refKey2 CHAR(50)  NOT NULL ,keyMain2 CHAR(50)  NOT NULL ,dataLevel3 CHAR(50) )", testConnection.getStatementStack().get(0).getExecuteStatement());
        assertEquals("ALTER TABLE  subLevel3 ADD PRIMARY KEY  (keyMain1_refKey1,keyMain1_refKey2,keyMain2)", testConnection.getStatementStack().get(1).getExecuteStatement());
    }

}
