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

import java.sql.Connection;

import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlObjectWriter;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;
import de.jdynameta.dbaccess.jdbc.connection.MqSqlTypeHandler;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.testcommon.util.sql.SqlUtil;

public class MySqlObjectInsertTest extends JdbcObjectInsertTest
{

    /**
     * @param name DOCUMENT ME!
     */
    public MySqlObjectInsertTest(String name)
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
    }

    @Override
    protected JdbcConnectionHolder createBaseConnection() throws Exception
    {
        return new JdbcConnectionHolder(SqlUtil.createMySqlBaseConnection());
    }

    @Override
    protected SqlTableCreator createTableCreator(Connection aConnection) throws Exception
    {
        return new JdyJdbcTableCreator(aConnection, new JDyDefaultRepositoryTableMapping(), new MqSqlTypeHandler());
    }

    @Override
    protected SqlObjectWriter createObjectWriter(JdbcConnectionHolder aConnection) throws JdyPersistentException
    {
        return new JdyJdbcObjectWriter(aConnection, new JDyDefaultRepositoryTableMapping());
    }
}
