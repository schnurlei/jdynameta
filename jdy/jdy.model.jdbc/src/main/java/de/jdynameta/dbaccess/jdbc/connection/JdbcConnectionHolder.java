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

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcConnectionHolder
{
    private transient Connection connection;

    public JdbcConnectionHolder()
    {
    }

    public JdbcConnectionHolder(Connection aConnection)
    {
        this.connection = aConnection;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public void commit() throws SQLException
    {
        connection.commit();
    }

    public void close() throws SQLException
    {
        connection.close();
    }

    public void rollback() throws SQLException
    {
        connection.rollback();
    }

}
