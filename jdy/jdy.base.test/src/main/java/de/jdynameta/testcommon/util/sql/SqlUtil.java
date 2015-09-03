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
package de.jdynameta.testcommon.util.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class SqlUtil
{
    private final Connection baseConnection;

    /**
     * @param aConnection
     */
    public SqlUtil(Connection aConnection)
    {
        super();
        this.baseConnection = aConnection;
    }

    public Object getObjectValueForColumn(String aTableName, String aColumnName, Map keys) throws SQLException
    {

        StringBuilder stmtBuffer = new StringBuilder(100);
        stmtBuffer.append("Select ").append(aColumnName);
        stmtBuffer.append(" from ").append(aTableName);
        stmtBuffer.append(" where ");

        for (Iterator keyIter = keys.entrySet().iterator(); keyIter.hasNext();)
        {
            Map.Entry curKeyEntry = (Map.Entry) keyIter.next();
            stmtBuffer.append(curKeyEntry.getKey()).append(" = ").append(curKeyEntry.getValue());
            if (keyIter.hasNext())
            {
                stmtBuffer.append(" and ");
            }
        }

        Statement stmtToExecute = this.baseConnection.createStatement();
        stmtToExecute.execute(stmtBuffer.toString());
        ResultSet stmtResult = stmtToExecute.getResultSet();

        stmtResult.next();
        Object resultObj = stmtResult.getObject(aColumnName);

        return resultObj;
    }

    /**
     * Check whether tabel exist in the database
     *
     * @param aTableName
     * @return
     * @throws SQLException
     */
    public boolean existsTableInDatabase(String aTableName) throws SQLException
    {
		// .getTables(null, "%", "%", names);
        // getTables( null, null, "%", new String[] { "TABLE" } );	

//		ResultSet tableResultSet = this.baseConnection.getMetaData().getTables(null, null,aTableName,null );
//		ResultSet tableResultSet = this.baseConnection.getMetaData().getTables(null, null, aTableName.toUpperCase(), null );
        ResultSet tableResultSet = this.baseConnection.getMetaData().getTables(null, null, "%", null);

        boolean existsTable = false;

        while (tableResultSet.next() && !existsTable)
        {

            System.out.println(tableResultSet.getString("TABLE_NAME"));
            if (tableResultSet.getString("TABLE_NAME").equalsIgnoreCase(aTableName))
            {
                existsTable = true;
            }
        }

        return existsTable;
    }

    /**
     * Check whether the given column exist in the table
     *
     * @param aTableName
     * @param aColumnName
     * @return
     * @throws SQLException
     */
    public boolean existsColumnInTable(String aTableName, String aColumnName) throws SQLException
    {
		// .getTables(null, "%", "%", names);
        // getTables( null, null, "%", new String[] { "TABLE" } );	

        DatabaseMetaData dbMetaData = this.baseConnection.getMetaData();
        ResultSet columnsResultSet = dbMetaData.getColumns(null, "%", aTableName, "%");

        boolean existsColumn = false;

        while (columnsResultSet.next() && !existsColumn)
        {

            String columnName = columnsResultSet.getString("COLUMN_NAME");
//			String datatype = columns.getString("TYPE_NAME");
//			int datasize = columns.getInt("COLUMN_SIZE");
//			int digits = columns.getInt("DECIMAL_DIGITS");
//			int nullable = columns.getInt("NULLABLE");
//			boolean isNull = (nullable == 1);

            if (columnName.equals(aColumnName))
            {
                existsColumn = true;
            }
        }

        return existsColumn;
    }

    /**
     * Create mysql Test database mysql> CREATE DATABASE jdynameta CHARACTER SET
     * utf8 COLLATE utf8_general_ci; mysql> grant all privileges on jdynameta.*
     * to 'jdynameta'@'localhost' identified by 'jdynameta';
     *
     * @return
     * @throws Exception
     */
    public static Connection createMySqlBaseConnection() throws Exception
    {
        Connection baseConnection;

        String dbUrl = System.getProperty("de.jdynameta.base.dbaccess.url");

        if (dbUrl == null || dbUrl.trim().length() == 0)
        {
            dbUrl = "jdbc:mysql:///jdynameta";
        }

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        baseConnection = DriverManager.getConnection(dbUrl, "jdynameta", "jdynameta");

        return baseConnection;
    }

    public static void createMySqlDb(String aDatabaseName) throws Exception
    {
        Connection baseConnection = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=rootpassword");
        Statement stmt = baseConnection.createStatement();

//	      Class.forName("org.gjt.mm.mysql.Driver").newInstance();
//	      String url = "jdbc:mysql://localhost/mysql";
//	      connection = DriverManager.getConnection(url, "username", "password");
        int Result = stmt.executeUpdate("CREATE DATABASE " + aDatabaseName);
        baseConnection.setCatalog(aDatabaseName);

    }

    public static Connection createOracleConnection() throws Exception
    {
        Connection baseConnection;
        String driverName = "oracle.jdbc.driver.OracleDriver";
        String userName = "jdynameta";
        String password = "jdynameta";
        String dbHost = "localhost";
        String dbPort = "1521";
        String dbName = "orcl";
        String dbUrl = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbName;

        Class.forName(driverName);
        baseConnection = DriverManager.getConnection(dbUrl, userName, password);

        return baseConnection;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            new SqlUtil(SqlUtil.createMySqlBaseConnection()).existsTableInDatabase("assocdetailnotnullobjex");
        } catch (Exception excp)
        {
            // TODO Auto-generated catch block
            excp.printStackTrace();
        }

    }

}
