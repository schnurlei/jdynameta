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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Test connection to check the proper generation of code in the Junit tests
 *
 * @author rs
 *
 */
public class JdbcTestConnection implements Connection
{
    private List<JdbcTestStatement> statementStack = new ArrayList<>();

    public List<JdbcTestStatement> getStatementStack()
    {
        return statementStack;
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        // only dummy 

    }

    @Override
    public void close() throws SQLException
    {
        // only dummy 

    }

    @Override
    public void commit() throws SQLException
    {
        // only dummy 

    }

    @Override
    public Array createArrayOf(String aTypeName, Object[] aElements) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Clob createClob() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Statement createStatement() throws SQLException
    {
        statementStack.add(new JdbcTestStatement());
        return statementStack.get(statementStack.size() - 1);
    }

    @Override
    public Statement createStatement(int aResultSetType, int aResultSetConcurrency) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Statement createStatement(int aResultSetType, int aResultSetConcurrency, int aResultSetHoldability)
            throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Struct createStruct(String aTypeName, Object[] aAttributes) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public boolean getAutoCommit() throws SQLException
    {
        // only dummy 
        return false;
    }

    @Override
    public String getCatalog() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public String getClientInfo(String aName) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public int getHoldability() throws SQLException
    {
        // only dummy 
        return 0;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public int getTransactionIsolation() throws SQLException
    {
        // only dummy 
        return 0;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        // only dummy 
        return false;
    }

    @Override
    public boolean isReadOnly() throws SQLException
    {
        // only dummy 
        return false;
    }

    @Override
    public boolean isValid(int aTimeout) throws SQLException
    {
        // only dummy 
        return false;
    }

    @Override
    public String nativeSQL(String aSql) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public CallableStatement prepareCall(String aSql) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency)
            throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public CallableStatement prepareCall(String aSql, int aResultSetType, int aResultSetConcurrency,
            int aResultSetHoldability) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String aSql) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int aAutoGeneratedKeys) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int[] aColumnIndexes) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, String[] aColumnNames) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency)
            throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String aSql, int aResultSetType, int aResultSetConcurrency,
            int aResultSetHoldability) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public void releaseSavepoint(Savepoint aSavepoint) throws SQLException
    {
        // only dummy 

    }

    @Override
    public void rollback() throws SQLException
    {
        // only dummy 

    }

    @Override
    public void rollback(Savepoint aSavepoint) throws SQLException
    {
        // only dummy 

    }


    @Override
    public void setAutoCommit(boolean aAutoCommit) throws SQLException
    {
        // only dummy 

    }

    @Override
    public void setCatalog(String aCatalog) throws SQLException
    {
        // only dummy 

    }

    @Override
    public void setClientInfo(Properties aProperties) throws SQLClientInfoException
    {
        // only dummy 

    }

    @Override
    public void setClientInfo(String aName, String aValue) throws SQLClientInfoException
    {
        // only dummy 

    }

    @Override
    public void setHoldability(int aHoldability) throws SQLException
    {
        // only dummy 

    }

    @Override
    public void setReadOnly(boolean aReadOnly) throws SQLException
    {
        // only dummy 

    }

    @Override
    public Savepoint setSavepoint() throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public Savepoint setSavepoint(String aName) throws SQLException
    {
        // only dummy 
        return null;
    }

    @Override
    public void setTransactionIsolation(int aLevel) throws SQLException
    {
        // only dummy 

    }

    @Override
    public void setTypeMap(Map<String, Class<?>> aMap) throws SQLException
    {
        // only dummy 

    }

    @Override
    public boolean isWrapperFor(Class<?> aArg0) throws SQLException
    {
        // only dummy 
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aArg0) throws SQLException
    {
        // only dummy 
        return null;
    }

//	@Override java 7
    @Override
    public void setSchema(String aSchema) throws SQLException
    {
		// TODO Auto-generated method stub

    }

//	@Override java 7
    @Override
    public String getSchema() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

//	@Override java 7
    @Override
    public void abort(Executor aExecutor) throws SQLException
    {
		// TODO Auto-generated method stub

    }

//	@Override java 7
    @Override
    public void setNetworkTimeout(Executor aExecutor, int aMilliseconds)
            throws SQLException
    {
		// TODO Auto-generated method stub

    }

//	@Override java 7
    @Override
    public int getNetworkTimeout() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
