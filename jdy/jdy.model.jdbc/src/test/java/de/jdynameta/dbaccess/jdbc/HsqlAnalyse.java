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
package de.jdynameta.dbaccess.jdbc;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

public class HsqlAnalyse extends TestCase
{
    private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";
    protected static String dbUrl = "jdbc:hsqldb:mem:testMeata";
    private Connection hsqlConnection;

    @Override
    protected void setUp() throws Exception
    {
        // TODO Auto-generated method stub
        super.setUp();
        hsqlConnection = createBaseConnection();
    }

    public void testIdentityColumn() throws SQLException
    {
        try (Statement createTableStmt = hsqlConnection.createStatement())
        {
            String stmt1 = "CREATE TABLE pctask(id BIGINT identity ,name varchar(40) NOT NULL ,  description varchar(1000) )";
            createTableStmt.execute(stmt1);
        }

        for (int i = 0; i < 10; i++)
        {
            String writeStmt = "INSERT INTO pctask ( name, description )  VALUES ('test one',  'Description for task 1') ";

            try (PreparedStatement insertStmt = hsqlConnection.prepareStatement(writeStmt, Statement.RETURN_GENERATED_KEYS))
            {
                insertStmt.executeUpdate();
                
//			Statement insertStmt =  hsqlConnection.createStatement();
//			insertStmt.execute(writeStmt, Statement.RETURN_GENERATED_KEYS);
                ResultSet keysRs = insertStmt.getGeneratedKeys();
                
                while (keysRs.next())
                {
                    BigDecimal newKey = keysRs.getBigDecimal(1);     // Get automatically generated key
                    System.out.println("automatically generated key value = " + newKey);
                }
                keysRs.close();
            }

        }

        String readStmt = "SELECT id, name, description from pctask";
        try (Statement createdStmt = hsqlConnection.createStatement())
        {
            createdStmt.execute(readStmt);
            ResultSet selectResult = createdStmt.getResultSet();
            while (selectResult.next())
            {
                System.out.println("Generated key: " + selectResult.getInt("id"));
            }
        }

    }

    public void testReadData() throws SQLException
    {
        createTable();
//		 String readStmt = "SELECT SubClassSimpleKeyObjEx.SplIntKyEx,SubClassSimpleKeyObjEx.SimpleDataSub1ExSimpleKeyWithSubObjEx.SplIntKyEx,SimpleKeyWithSubObjEx.SimpleKeyData1Ex,SimpleKeyWithSubObjEx.SimpleKeyData2Ex"

        String writeStmt = "INSERT INTO SubClassSimpleKeyObjEx ( SplIntKyEx,SimpleDataSub1Ex )  VALUES (10,'test' ) ";
        String writeStmt2 = "INSERT INTO SimpleKeyWithSubObjEx ( SplIntKyEx,SimpleKeyData1Ex, SimpleKeyData2Ex )  VALUES (10,'test', null ) ";

        hsqlConnection.prepareStatement(writeStmt).executeUpdate();
        hsqlConnection.prepareStatement(writeStmt2).executeUpdate();

        String readStmt = "SELECT SubClassSimpleKeyObjEx.SplIntKyEx sbc_SplIntKyEx,SubClassSimpleKeyObjEx.SimpleDataSub1Ex"
                + ", SimpleKeyWithSubObjEx.SplIntKyEx,SimpleKeyWithSubObjEx.SimpleKeyData1Ex,SimpleKeyWithSubObjEx.SimpleKeyData2Ex"
                + " From SubClassSimpleKeyObjEx "
                + " inner join SimpleKeyWithSubObjEx "
                + " on SubClassSimpleKeyObjEx.SplIntKyEx = SimpleKeyWithSubObjEx.SplIntKyEx AND SubClassSimpleKeyObjEx.SimpleDataSub1Ex = SimpleKeyWithSubObjEx.SimpleKeyData1Ex ";
        try (Statement createStmt = hsqlConnection.createStatement())
        {
            createStmt.execute(readStmt);
            ResultSet selectResult = createStmt.getResultSet();
            selectResult.next();
            selectResult.getInt("sbc_SplIntKyEx");
        }

    }

    public void testMultibleUserss() throws SQLException
    {
        File aDbFile = new File("testDb");
        String testDbUrl = "jdbc:hsqldb:file:" + aDbFile.getAbsolutePath();

        Connection testDbaConnection = DriverManager.getConnection(testDbUrl, "sa", "");

        executeStmtIgnoreException(testDbaConnection, "DROP SCHEMA JDYNAMETA CASCADE ");
        executeStmtIgnoreException(testDbaConnection, "DROP USER TEST");
        executeStmtIgnoreException(testDbaConnection, "DROP USER TEST2 ");
        executeStmtIgnoreException(testDbaConnection, "DROP SCHEMA APPLICATION CASCADE");
        executeStmtIgnoreException(testDbaConnection, "DROP SCHEMA JDYNAM_ETA CASCADE");

        testDbaConnection.prepareStatement("CREATE USER TEST PASSWORD 'test'").executeUpdate();
        testDbaConnection.prepareStatement("CREATE USER TEST2 PASSWORD 'test2'").executeUpdate();

        testDbaConnection.prepareStatement("CREATE SCHEMA JDYNAM_ETA AUTHORIZATION DBA ").executeUpdate();
        testDbaConnection.prepareStatement("CREATE SCHEMA APPLICATION AUTHORIZATION DBA").executeUpdate();

        Statement createStmt = testDbaConnection.createStatement();

        testDbaConnection.prepareStatement("CREATE TABLE APPLICATION.table1(id BIGINT  NOT NULL ,firstname CHAR(50)  NOT NULL ,lastname CHAR(50) )").executeUpdate();
//		createStmt.execute("CREATE TABLE APPLICATION.table2(var1 BIGINT  NOT NULL ,var2 CHAR(50)  NOT NULL ,var3 DATE  )");
//		createStmt.execute("CREATE TABLE TABLE2(var1 BIGINT  NOT NULL ,var2 CHAR(50)  NOT NULL ,var3 DATE  )");

        createStmt.execute("Select * from INFORMATION_SCHEMA.SYSTEM_SCHEMAS where TABLE_SCHEM = 'APPLICATION'");
        ResultSet schemas = createStmt.getResultSet();
        while (schemas.next())
        {
            for (int i = 1; i < schemas.getMetaData().getColumnCount(); i++)
            {
                System.out.println(schemas.getMetaData().getColumnName(i));
                System.out.println(schemas.getObject(i));
            }
        }

        Connection testConnection = DriverManager.getConnection(testDbUrl, "TEST", "test");
        Statement testStmt = testConnection.createStatement();

        //		testStmt.execute("SET INITIAL SCHEMA APPLICATION");
        createStmt.execute("INSERT INTO APPLICATION.table1 (id, firstname, lastname) VALUES (1, 'Felix', 'the Cat')");

        testDbaConnection.prepareStatement("DROP SCHEMA APPLICATION CASCADE").executeUpdate();

        createStmt.execute("SHUTDOWN");
    }

    private void executeStmtIgnoreException(Connection testDbaConnection, String aStatement)
    {
        try
        {
            testDbaConnection.prepareStatement(aStatement).executeUpdate();
        } catch (Exception e)
        {
        }
    }

    private void createTable() throws SQLException
    {
        try (Statement createStmt = hsqlConnection.createStatement())
        {
            String stmt1 = "CREATE TABLE SimpleKeyWithSubObjEx(SplIntKyEx BIGINT  NOT NULL ,SimpleKeyData1Ex CHAR(50)  NOT NULL ,SimpleKeyData2Ex DATE )";
            String stmt2 = "ALTER TABLE  SimpleKeyWithSubObjEx ADD PRIMARY KEY  (SplIntKyEx)";
            
            String stmt3 = "CREATE TABLE SubClassSimpleKeyObjEx(SplIntKyEx BIGINT  NOT NULL ,SimpleDataSub1Ex CHAR(50)  NOT NULL )";
            String stmt4 = "ALTER TABLE  SubClassSimpleKeyObjEx ADD PRIMARY KEY  (SplIntKyEx)";
            
            createStmt.execute(stmt1);
            createStmt.execute(stmt2);
            createStmt.execute(stmt3);
            createStmt.execute(stmt4);
        }

    }

    protected Connection createBaseConnection() throws Exception
    {
        Class.forName(HSQLDB_JDBC_DRIVER);  //loads the driver
        return DriverManager.getConnection(dbUrl, "sa", "");
    }

}
