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
package de.jdynameta.dbaccess.jdbc.generation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JdyMappingUtil;
import de.jdynameta.base.creation.db.JdyRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.metainfo.AttributeAdaptor;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;

/**
 * Creates and delete tables over a JDBC Database connection
 *
 * @author Rainer
 *
 * @version 25.07.2002
 */
public class JdyJdbcTableCreator extends SqlTableCreator
{
    private Connection dbConnection = null;
    public JdyRepositoryTableMapping tableMapping;
    /**
     * handler to create the Type String for the Columns
     */
    private final JDBCTypeHandler typeHandler;

    public JdyJdbcTableCreator(Connection theConnection, JdyRepositoryTableMapping aTableMapping, JDBCTypeHandler aTypehandler)
    {
        this.dbConnection = theConnection;
        this.tableMapping = aTableMapping;
        this.typeHandler = aTypehandler;
    }

    @Override
    public JdyRepositoryTableMapping getTableMapping()
    {
        return tableMapping;
    }

    @Override
    public BufferedPrimitiveTypeVisitor getTypeHandler()
    {
        return typeHandler;
    }

    @Override
    protected void buildTableForClassInfo(ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings) throws JdyPersistentException
    {
        try
        {
            String sqlStmt = createCreateTableStmt(aClassInfo, aTableName, mappings);

            try (Statement createStmt = dbConnection.createStatement())
            {
                System.out.println(sqlStmt);
                createStmt.execute(sqlStmt);
            }

        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc);
        }
    }

    @Override
    protected void createPrimaryKeyForClass(ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings) throws JdyPersistentException
    {
        try
        {
            StringBuffer sqlBuffer = new StringBuffer(300);

            try (Statement createStmt = dbConnection.createStatement())
            {
                sqlBuffer.append("ALTER TABLE  ").append(aTableName).append(" ADD PRIMARY KEY  ");
                sqlBuffer.append('(');
                addIndexAttributesForClassInfo(mappings, sqlBuffer);
                sqlBuffer.append(')');

                System.out.println(sqlBuffer.toString());
                createStmt.execute(sqlBuffer.toString());
            }
        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc);
        }
    }

    @Override
    protected void createForeignKeysForClass(final ClassInfo aClassInfo, final String aTableName, final List<AspectMapping> mappings) throws JdyPersistentException
    {
        for (AttributeInfo curAttrInfo : aClassInfo.getAttributeInfoIterator())
        {
            // dont't create foreign keys for references in superclasses
            if (aClassInfo.isSubclassAttribute(curAttrInfo) || curAttrInfo.isKey())
            {

                curAttrInfo.handleAttribute(new AttributeAdaptor()
                {
                    @Override
                    public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
                    {
                        createForeignKeysFor(aTableName, mappings, aInfo);
                    }
                }, null);
            }

        }

    }

    protected void createForeignKeysFor(String aMasterTableName, List<AspectMapping> masterMappings, ObjectReferenceAttributeInfo aInfo) throws JdyPersistentException
    {

        JDyClassInfoToTableMapping aRefClassMapping = getTableMapping().getTableMappingFor(aInfo.getReferencedClass());
        Map<String, List<AspectMapping>> refTableName2ColumnMap = JdyMappingUtil.createTableMapping(aInfo.getReferencedClass(), aRefClassMapping, true);

        for (Map.Entry<String, List<AspectMapping>> entry : refTableName2ColumnMap.entrySet())
        {
            try
            {
                createForeignKeysForClass(aMasterTableName, masterMappings, entry.getKey(), entry.getValue(), aInfo);
            } catch (Exception ex)
            {
                throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
            }
        }

    }

    private void createForeignKeysForClass(String aMasterTableName, List<AspectMapping> masterMappings, String aRefTableName, List<AspectMapping> refMappings, ObjectReferenceAttributeInfo aRefInfo) throws JdyPersistentException
    {

        try (Statement createStmt = dbConnection.createStatement())
        {
            StringBuffer sqlBuffer = new StringBuffer(300);

            sqlBuffer.append("ALTER TABLE  ").append(aMasterTableName).append(" ADD FOREIGN KEY ");
            sqlBuffer.append('(');
            addReferenceAttributesForClassInfo(masterMappings, sqlBuffer, aRefInfo);
            sqlBuffer.append(')');
            StringBuffer append = sqlBuffer.append("REFERENCES   ").append(aRefTableName);
            sqlBuffer.append('(');
            addIndexAttributesForClassInfo(refMappings, sqlBuffer);
            sqlBuffer.append(')');

            System.out.println(sqlBuffer.toString());
            createStmt.execute(sqlBuffer.toString());
        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc.getLocalizedMessage(), catchedExc);
        }
    }

    private void addReferenceAttributesForClassInfo(List<AspectMapping> refMappings, StringBuffer aSqlBuffer, ObjectReferenceAttributeInfo aRefInfo)
    {
        boolean isFirstAttribute = true;
        for (Iterator<AspectMapping> attrPathIter = refMappings.iterator(); attrPathIter.hasNext();)
        {
            AspectMapping curMapping = attrPathIter.next();
            if (curMapping.getMappedPath().getReferenceListSize() > 0
                    && curMapping.getMappedPath().getReferenceAt(0).equals(aRefInfo))
            {
                if (!isFirstAttribute)
                {
                    aSqlBuffer.append(',');
                } else
                {
                    isFirstAttribute = false;
                }
                aSqlBuffer.append(curMapping.getColumnName());
                isFirstAttribute = false;
            }
        }

    }

    /**
     * ALTER TABLE tablename ADD PRIMARY KEY (column_one, column_two)
     *
     * @param aTableName
     * @param mappings
     * @throws JdyPersistentException
     */
    protected void createUniqueIndexForClass(String aTableName, List<AspectMapping> mappings) throws JdyPersistentException
    {
        try (Statement createStmt = dbConnection.createStatement())
        {
            StringBuffer sqlBuffer = new StringBuffer(300);
            sqlBuffer.append("CREATE UNIQUE INDEX ").append(aTableName).append("_PK ON ");
            sqlBuffer.append(aTableName);
            sqlBuffer.append('(');
            addIndexAttributesForClassInfo(mappings, sqlBuffer);
            sqlBuffer.append(')');

            System.out.println(sqlBuffer.toString());
            createStmt.execute(sqlBuffer.toString());
        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc);
        }
    }

    @Override
    protected void deleteTableForClassInfo(String aTableName) throws JdyPersistentException
    {
        try (Statement createStmt = dbConnection.createStatement())
        {
            String sqlText = "DROP TABLE " + aTableName + ";";
            System.out.println(sqlText);
            createStmt.executeUpdate(sqlText);
        } catch (SQLException catchedExc)
        {
            throw new JdyPersistentException(catchedExc);
        }
    }

    /**
     * @return Returns the dbConnection.
     */
    protected Connection getDbConnection()
    {
        return this.dbConnection;
    }
}
