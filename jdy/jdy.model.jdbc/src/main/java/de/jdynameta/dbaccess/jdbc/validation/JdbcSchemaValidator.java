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
package de.jdynameta.dbaccess.jdbc.validation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.creation.db.JdyMappingUtil;
import de.jdynameta.base.creation.db.JdyRepositoryTableMapping;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.metainfo.AttributeAdaptor;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;

public class JdbcSchemaValidator
{
    private Connection dbConnection = null;
    public JdyRepositoryTableMapping tableMapping;
    /**
     * handler to create the Type String for the Columns
     */
    private final JDBCTypeHandler typeHandler;
    private final String schemaName;

    public JdbcSchemaValidator(Connection theConnection, JdyRepositoryTableMapping aTableMapping, JDBCTypeHandler aTypehandler, String aSchemaName)
    {
        this.dbConnection = theConnection;
        this.tableMapping = aTableMapping;
        this.typeHandler = aTypehandler;
        this.schemaName = aSchemaName;
    }

    public JdyRepositoryTableMapping getTableMapping()
    {
        return tableMapping;
    }

    protected void validateSchemaExists() throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException
    {
        try
        {

            ResultSet schemas = dbConnection.getMetaData().getSchemas();

            if (schemas != null)
            {

//				for (int i = 1; i <= schemas.getMetaData().getColumnCount(); i++) {
//					System.out.println(schemas.getMetaData().getColumnName(i));
//				}
                boolean existsSchema = false;
                while (schemas.next() && !existsSchema)
                {

//					System.out.println(schemas.getString("TABLE_SCHEM"));
//					System.out.println(schemas.getString("TABLE_CATALOG"));
//					System.out.println(schemas.getString("IS_DEFAULT"));
                    if (schemas.getString("TABLE_SCHEM") != null)
                    {
                        existsSchema = schemas.getString("TABLE_SCHEM").equalsIgnoreCase(schemaName);
                    }
                }
                schemas.close();
                if (!existsSchema)
                {
                    throw new JdbcSchemaHandler.SchemaValidationException(schemaName + "does not exists");
                }
            } else
            {
                throw new JdbcSchemaHandler.SchemaValidationException("Metadata for schemas does not exists");
            }

        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc);
        }
    }

    public void validateTablesForClassInfo(ClassInfo aClassInfo) throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException
    {
        JDyClassInfoToTableMapping aClassMapping = getTableMapping().getTableMappingFor(aClassInfo);

        Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aClassInfo, aClassMapping, true);

        for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet())
        {
            String tableName = removeSchemaPrefixFromTable(entry.getKey());
            validateTableForClassInfo(aClassInfo, tableName, entry.getValue());
        }
    }

    private String removeSchemaPrefixFromTable(String tableName)
    {
        if (tableName.toUpperCase().startsWith(schemaName.toUpperCase() + "."))
        {
            tableName = tableName.toUpperCase().replace(schemaName.toUpperCase() + ".", "");
        }
        return tableName;
    }

    protected void validateTableForClassInfo(ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings) throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException
    {
        try
        {

            ResultSet tablesRsltSet = dbConnection.getMetaData().getTables(null, schemaName.toUpperCase(), aTableName.toUpperCase(), null);

            boolean tableExists = tablesRsltSet.first();
            if (!tableExists)
            {

//				ResultSet alltablesRsltSet = dbConnection.getMetaData().getTables(null,  schemaName.toUpperCase(),null, null);
//				dumpResultSetColumns(alltablesRsltSet);
//				
//				while( alltablesRsltSet.next()) {
//					dumpResultSetAtCursor(alltablesRsltSet, new String[]{"TABLE_NAME"});
//				}
                throw new JdbcSchemaHandler.SchemaValidationException("Table does not exist: " + aTableName);
            }

            validateClassAttributes(aClassInfo, mappings, aTableName);

            validatePrimaryKeyForClass(aClassInfo, aTableName.toUpperCase(), mappings);

            validateForeignKeysForClass(aClassInfo, aTableName.toUpperCase(), mappings);

        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc);
        }
    }

    protected void validateClassAttributes(ClassInfo aClassInfo, List<AspectMapping> mappings, String aTableName) throws JdbcSchemaHandler.SchemaValidationException, SQLException
    {
        List<JdbcColumnMetaInfo> colMetaInfos = new ArrayList<>();
        ResultSet columnsRsltSet = dbConnection.getMetaData().getColumns(null, schemaName.toUpperCase(), aTableName.toUpperCase(), null);

        while (columnsRsltSet.next())
        {
            colMetaInfos.add(new JdbcColumnMetaInfo(columnsRsltSet));
        }

        for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
        {
            AspectMapping curMapping = attrPathIter.next();
            validateColumn(aClassInfo, curMapping, colMetaInfos);
        }

    }

    protected void validateColumn(ClassInfo aClassInfo, AspectMapping columnMapping, List<JdbcColumnMetaInfo> colMetaInfos) throws JdbcSchemaHandler.SchemaValidationException
    {
        boolean colExists = false;

        for (JdbcColumnMetaInfo jdbcColumnMetaInfo : colMetaInfos)
        {

            if (jdbcColumnMetaInfo.getColName().toUpperCase().equals(columnMapping.getColumnName().toUpperCase()))
            {
                colExists = true;
                if (columnMapping.getMappedPath().firstAttribute().isNotNull())
                {
                    if (!jdbcColumnMetaInfo.isNotNull())
                    {
                        throw new JdbcSchemaHandler.SchemaValidationException("Column: " + columnMapping.getColumnName() + " isNot Null in DB");
                    }
                } else
                {
                    if (jdbcColumnMetaInfo.isNotNull())
                    {
                        throw new JdbcSchemaHandler.SchemaValidationException("Column: " + columnMapping.getColumnName() + " isNot Nullable in DB");
                    }
                }
                validateColumnType(aClassInfo, columnMapping, jdbcColumnMetaInfo);
            }
        }

        if (!colExists)
        {
            throw new JdbcSchemaHandler.SchemaValidationException("Column: " + columnMapping.getColumnName() + " does not exists in  DB");
        }

    }

    protected void validateColumnType(ClassInfo aClassInfo, AspectMapping columnMapping, JdbcColumnMetaInfo jdbcColumnMetaInfo) throws JdbcSchemaHandler.SchemaValidationException
    {
        JdbcTypeValidator typeValidator = new JdbcTypeValidator();
        typeValidator.setJdbcColInfo(jdbcColumnMetaInfo);

        try
        {
            columnMapping.getMappedPath().getLastInfo().getType().handlePrimitiveKey(typeValidator, null);
            if (!typeValidator.isMatchesType())
            {
                throw new JdbcSchemaHandler.SchemaValidationException("Column: " + columnMapping.getColumnName() + " has wrong type DB");
            }
        } catch (JdyPersistentException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Check whether the primaryKeyColumns exists in the mappings
     *
     * @param aClassInfo
     * @param aTableName
     * @param mappings
     * @throws JdyPersistentException
     * @throws JdbcSchemaHandler.SchemaValidationException
     * @throws SQLException
     * @todo check whether columns exists in the db that are not in the mappings
     */
    protected void validatePrimaryKeyForClass(ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings) throws JdyPersistentException, JdbcSchemaHandler.SchemaValidationException, SQLException
    {
        List<String> primaryKeyColumns = new ArrayList<>();
        ResultSet primaryKeyRsltSet = dbConnection.getMetaData().getPrimaryKeys(null, schemaName.toUpperCase(), aTableName.toUpperCase());
        while (primaryKeyRsltSet.next())
        {
            primaryKeyColumns.add(primaryKeyRsltSet.getString("COLUMN_NAME").toUpperCase());
        }

        for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
        {
            AspectMapping curMapping = attrPathIter.next();
            if (curMapping.getMappedPath().firstAttribute().isKey())
            {
                if (!primaryKeyColumns.contains(curMapping.getColumnName().toUpperCase()))
                {
                    throw new JdbcSchemaHandler.SchemaValidationException("Missing primary key column: " + curMapping.getColumnName() + " in table: " + aTableName);
                }
            }
        }
    }

    /**
     * Add primary kex by alter table
     *@param aClassInfo aTableName
     * @param mappings
     * @throws JdyPersistentException
     * @throws SQLException
     */
    protected void validateForeignKeysForClass(final ClassInfo aClassInfo, final String aTableName, final List<AspectMapping> mappings) throws JdyPersistentException, SQLException
    {
        final Map<String, List<String>> pkTable2PkColNames = new HashMap<>();
        ResultSet foreignKeyRsltSet = dbConnection.getMetaData().getImportedKeys(null, schemaName.toUpperCase(), aTableName.toUpperCase());
        while (foreignKeyRsltSet.next())
        {

            String pktableName = foreignKeyRsltSet.getString("PKTABLE_NAME");
            String pkcolumnName = foreignKeyRsltSet.getString("PKCOLUMN_NAME");

            List<String> allPkCols = pkTable2PkColNames.get(pktableName);
            if (allPkCols == null)
            {
                allPkCols = new ArrayList<>();
                pkTable2PkColNames.put(pktableName, allPkCols);
            }
            allPkCols.add(pkcolumnName);
//	    	dumpResultSetAtCursor(foreignKeyRsltSet, new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME","FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME"
//	    			,"FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE","DELETE_RULE" ,"FK_NAME", "PK_NAME","DEFERRABILITY" });
        }

        for (AttributeInfo curAttrInfo : aClassInfo.getAttributeInfoIterator())
        {
            curAttrInfo.handleAttribute(new AttributeAdaptor()
            {
                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
                {
                    validateForeignKeysFor(aTableName, mappings, aInfo, pkTable2PkColNames);
                }
            }, null);

        }

    }

    private void dumpResultSetColumns(ResultSet resultSet) throws SQLException
    {
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
        {
            System.out.println(resultSet.getMetaData().getColumnName(i));
        }
    }

    private void dumpResultSetAtCursor(ResultSet resultSet, String[] columnsToDump) throws SQLException
    {
//		for (int i = 1; i <= tablesRsltSet.getMetaData().getColumnCount(); i++) {
//		 System.out.println(tablesRsltSet.getMetaData().getColumnName(i));
//	}

        for (String curColName : columnsToDump)
        {
            System.out.println("Column: " + curColName + " Value: " + resultSet.getString(curColName));
        }

    }

    protected void validateForeignKeysFor(String aMasterTableName, List<AspectMapping> masterMappings, ObjectReferenceAttributeInfo aRefInfo, Map<String, List<String>> pkTable2PkColNames) throws JdyPersistentException
    {

        JDyClassInfoToTableMapping aRefClassMapping = getTableMapping().getTableMappingFor(aRefInfo.getReferencedClass());
        Map<String, List<AspectMapping>> refTableName2ColumnMap = JdyMappingUtil.createTableMapping(aRefInfo.getReferencedClass(), aRefClassMapping, true);

        for (Map.Entry<String, List<AspectMapping>> entry : refTableName2ColumnMap.entrySet())
        {
            try
            {
//	   			for (Iterator<AspectMapping> attrPathIter = masterMappings.iterator(); attrPathIter.hasNext();)
//	   			{
//	   				AspectMapping curMapping = attrPathIter.next();
//	   				if( curMapping.getMappedPath().getReferenceListSize() > 0 
//	   						&& curMapping.getMappedPath().getReferenceAt(0).equals(aRefInfo) ) {
//	   					curMapping.getColumnName();
//	   				}
//	   			}

                String refTableName = removeSchemaPrefixFromTable(entry.getKey());
                List<String> refColumns = pkTable2PkColNames.get(refTableName.toUpperCase());
                if (refColumns == null)
                {
                    throw new JdbcSchemaHandler.SchemaValidationException("Missing foreign key from table: " + refTableName + " to table: " + aMasterTableName + " for: " + aRefInfo.getInternalName());
                }

                for (Iterator<AspectMapping> attrPathIter = entry.getValue().iterator(); attrPathIter.hasNext();)
                {
                    AspectMapping curMapping = attrPathIter.next();
                    if (curMapping.getMappedPath().firstAttribute().isKey())
                    {
                        if (!refColumns.contains(curMapping.getColumnName().toUpperCase()))
                        {
                            throw new JdbcSchemaHandler.SchemaValidationException("Missing foreign key column: " + curMapping.getColumnName() + " from table: " + refTableName + " to table: " + aMasterTableName);
                        }
                    }
                }

            } catch (Exception ex)
            {
                throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
            }
        }

    }

    /**
     * @return Returns the dbConnection.
     */
    protected Connection getDbConnection()
    {
        return this.dbConnection;
    }

    public static class JdbcColumnMetaInfo
    {
        private final String colName;
        private final String colSize;
        private final int dataType;
        private final String typeName;
        private final int decimalDigits;
        private final String isNullable;

        public JdbcColumnMetaInfo(ResultSet columnsRsltSet) throws SQLException
        {
            colName = columnsRsltSet.getString("COLUMN_NAME");
            colSize = columnsRsltSet.getString("COLUMN_SIZE");
            dataType = columnsRsltSet.getInt("DATA_TYPE");
            typeName = columnsRsltSet.getString("TYPE_NAME");
            decimalDigits = columnsRsltSet.getInt("DECIMAL_DIGITS");
            isNullable = columnsRsltSet.getString("IS_NULLABLE");
        }

        public String getColName()
        {
            return colName;
        }

        public boolean isNotNull()
        {
            return isNullable != null && isNullable.equals("NO");
        }

        public int getDataType()
        {
            return dataType;
        }

        public String getTypeName()
        {
            return typeName;
        }
    }

}
