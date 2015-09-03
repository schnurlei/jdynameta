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

import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;

public class HSqlDbSubclassReadTest extends JdbcSubclassReadTest
{
    private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    protected static String dbUrl = "jdbc:hsqldb:mem:testMeata";

    /**
     * @param name DOCUMENT ME !
     */
    public HSqlDbSubclassReadTest(String name)
    {
        super(name);
    }

    @Override
    protected JdbcConnectionHolder createBaseConnection() throws Exception
    {
        Class.forName(HSQLDB_JDBC_DRIVER);  //loads the driver
        return new JdbcConnectionHolder(DriverManager.getConnection(dbUrl, "sa", ""));
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
    }

    @Override
    protected SqlTableCreator createTableCreator(Connection aConnection) throws Exception
    {
        return new JdyJdbcTableCreator(aConnection, new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());
    }
}
