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
package de.jdynameta.dbaccess.jdbc.hsqldb;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnection;
import de.jdynameta.dbaccess.jdbc.generation.HsqlDbJdbcTableCreator;
import de.jdynameta.dbaccess.jdbc.validation.JdbcSchemaValidator;

/**
 * Handle hsql database schemas and creates a schema from a class repository
 *
 * @author rainer
 */
@SuppressWarnings("serial")
public class HSqlSchemaHandler implements Serializable, JdbcSchemaHandler
{
    private final DataSource datasource;

    public HSqlSchemaHandler(DataSource aDatasource) throws JdyPersistentException
    {
        this.datasource = aDatasource;
    }

    /* (non-Javadoc)
     * @see de.jdynameta.metamodel.connection.JdbcSchemaHandler#existsSchema()
     */
    @Override
    public boolean existsSchema(String aSchemaName) throws JdyPersistentException
    {
        String schemaName = aSchemaName.toUpperCase();

        try
        {
            Statement createStmt = this.datasource.getConnection().createStatement();
            String stmtText = "Select * from INFORMATION_SCHEMA.SYSTEM_SCHEMAS where TABLE_SCHEM = '" + schemaName + "'";
            createStmt.execute(stmtText);
            ResultSet schemas = createStmt.getResultSet();
            return schemas.next();
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }
    }

    /* (non-Javadoc)
     * @see de.jdynameta.metamodel.connection.JdbcSchemaHandler#deleteSchema()
     */
    @Override
    public void deleteSchema(String aSchemaName) throws JdyPersistentException
    {
        String schemaName = aSchemaName.toUpperCase();
        Connection connection = null;
        try
        {
            connection = this.datasource.getConnection();
            connection.prepareStatement("DROP SCHEMA " + schemaName + " CASCADE").executeUpdate();
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } finally
        {
            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            } catch (SQLException ex)
            {
                throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.jdynameta.metamodel.connection.JdbcSchemaHandler#createSchema(de.jdynameta.base.metainfo.ClassRepository)
     */
    @Override
    public void createSchema(String aSchemaName, ClassRepository aRepository) throws JdyPersistentException
    {
        String schemaName = aSchemaName.toUpperCase();
        Connection connection = null;
        try
        {
            connection = this.datasource.getConnection();
            String createSchemaStmt = "CREATE SCHEMA  " + schemaName + " AUTHORIZATION DBA ";
            System.out.println(createSchemaStmt);
            connection.prepareStatement("CREATE SCHEMA  " + schemaName + " AUTHORIZATION DBA ").executeUpdate();
            createMetainfoTablesInDb(schemaName, this.datasource.getConnection(), aRepository);
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } finally
        {
            try
            {
                if (connection != null)
                {
                    connection.close();
                }
            } catch (SQLException ex)
            {
                throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
            }
        }

    }

    /* (non-Javadoc)
     * @see de.jdynameta.metamodel.connection.JdbcSchemaHandler#validateSchema(de.jdynameta.base.metainfo.ClassRepository)
     */
    @Override
    public void validateSchema(String aSchemaName, ClassRepository aRepository) throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException
    {
        String schemaName = aSchemaName.toUpperCase();
        try
        {
            validateMetainfoTablesInDb(schemaName, this.datasource.getConnection(), aRepository);
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

    }

    private void existsTablesForMetaInfo(String aSchemaName, ClassInfo curInfo) throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException, SQLException
    {
        String schemaName = aSchemaName.toUpperCase();
        JdbcSchemaValidator validator = new JdbcSchemaValidator(this.datasource.getConnection(), new HsqlUtil.HSqlDbClassInfoToTableMapping(schemaName), new JDBCTypeHandler(), schemaName);
        validator.validateTablesForClassInfo(curInfo);
    }

    private void validateMetainfoTablesInDb(String schemaName, Connection baseConnection, ClassRepository aRepository) throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException
    {
        JdbcSchemaValidator validator = new JdbcSchemaValidator(baseConnection, new HsqlUtil.HSqlDbClassInfoToTableMapping(schemaName), new JDBCTypeHandler(), schemaName);

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            validator.validateTablesForClassInfo(curInfo);
        }
    }

    public <TObjToWrite, TReadedObj> JdbcConnection<TObjToWrite, TReadedObj> createBaseConnection(String aSchemaName)
    {
        String schemaName = aSchemaName.toUpperCase();
        JdbcConnection<TObjToWrite, TReadedObj> newBaseConnection
                = new JdbcConnection<>(datasource, null, new HsqlUtil.HSqlDbClassInfoToTableMapping(schemaName));
        return newBaseConnection;
    }

    private void createMetainfoTablesInDb(String schemaName, Connection baseConnection, ClassRepository aRepository) throws JdyPersistentException
    {
        SqlTableCreator tableCreator = new HsqlDbJdbcTableCreator(baseConnection, new HsqlUtil.HSqlDbClassInfoToTableMapping(schemaName), new JDBCTypeHandler());

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            tableCreator.buildTableForClassInfo(curInfo);
        }

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            tableCreator.buildForeignKeysForClassInfo(curInfo);
        }

    }

}
