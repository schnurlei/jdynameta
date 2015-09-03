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

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;
import de.jdynameta.testcommon.model.subclass.SubclassRepository;

public class HsqlSubclassTableCreatorTest extends TestCase
{
    private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    protected static String dbUrl = "jdbc:hsqldb:mem:testMeata";

    private Connection dbConnection = null;

    @Override
    protected void setUp() throws Exception
    {
        Class.forName(HSQLDB_JDBC_DRIVER);  //loads the driver
        this.dbConnection = DriverManager.getConnection(dbUrl, "sa", "");
    }

    private SubclassRepository getRepository()
    {
        return new SubclassRepository();
    }

    public void testTableMappingReferencedObject() throws JdyPersistentException
    {
        SqlTableCreator tableCreator = new JdyJdbcTableCreator(this.dbConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());

        for (ClassInfo curInfo : getRepository().getAllClassInfosIter())
        {
            try
            {
                tableCreator.deleteTableForClassInfo(curInfo);
            } catch (JdyPersistentException excp)
            {
                // excp.printStackTrace();
            }
            tableCreator.buildTableForClassInfo(curInfo);

        }

    }

}
