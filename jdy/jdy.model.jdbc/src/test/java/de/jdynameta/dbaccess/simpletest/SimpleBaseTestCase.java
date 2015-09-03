/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.dbaccess.simpletest;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;

/** 
 * Base class for all test cases. Creates connections, 
 * statements, etc. and closes them.
 * 
 */
public abstract class SimpleBaseTestCase extends TestCase 
{
    protected JdbcConnectionHolder baseConnection = null;
    protected ResultSet rs = null;
	private static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbcDriver";
	protected static String dbUrl ="jdbc:hsqldb:mem:testMeata";


    /**
     * Creates a new BaseTestCase object.
     * 
     * @param name DOCUMENT ME!
     */
    public SimpleBaseTestCase(String name) 
    {
        super(name);

    }


    /**
     * DOCUMENT ME!
     * 
     * @throws Exception DOCUMENT ME!
     */
    @Override
	public void setUp() throws Exception 
    {
        baseConnection = createConnection();
    }

    protected JdbcConnectionHolder createConnection() throws Exception
    {
        String newDbUrl = System.getProperty("com.mysql.jdbc.testsuite.url");

        if (newDbUrl != null && newDbUrl.trim().length() != 0) {
            dbUrl = newDbUrl;
        }
		Class.forName(HSQLDB_JDBC_DRIVER);  //loads the driver
		return new JdbcConnectionHolder( DriverManager.getConnection( dbUrl , "sa"   , ""));
    }
    
    /**
     * DOCUMENT ME!
     * 
     * @throws Exception DOCUMENT ME!
     */
    @Override
	public void tearDown()
                  throws Exception 
    {

        if (rs != null) {

            try {
                rs.close();
            } catch (SQLException SQLE) {
                
            }
        }

        if (baseConnection != null) {

            try {
                baseConnection.getConnection().close();
            } catch (SQLException SQLE) {
                
            }
        }
    }
}
