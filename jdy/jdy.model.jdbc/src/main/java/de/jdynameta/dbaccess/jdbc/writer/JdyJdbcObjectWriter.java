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
package de.jdynameta.dbaccess.jdbc.writer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.creation.db.JdyMappingUtil;
import de.jdynameta.base.creation.db.JdyRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlObjectWriter;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;
import de.jdynameta.dbaccess.jdbc.connection.PreparedStatementWriter;
import de.jdynameta.dbaccess.jdbc.connection.UtilSql;
import java.util.logging.Level;

@SuppressWarnings("serial")
public class JdyJdbcObjectWriter extends SqlObjectWriter
{
    private static final Logger log = Logger.getLogger(JdyJdbcObjectWriter.class.getName());
    private JdbcConnectionHolder dbConnection = null;
    private final JdyRepositoryTableMapping tableMapping;

    public JdyJdbcObjectWriter(JdbcConnectionHolder theConnection, JdyRepositoryTableMapping aTableMapping)
    {
        this.dbConnection = theConnection;
        this.tableMapping = aTableMapping;
    }

    protected JdyPersistentException buildDbException(SQLException excp)
    {
        return new JdyPersistentException(excp.getLocalizedMessage(), excp);
    }

    @Override
    public final synchronized void deleteObjectInDb(ValueObject aObjToDelete, ClassInfo aClassInfo)
            throws JdyPersistentException
    {

        JDyClassInfoToTableMapping aClassMapping = tableMapping.getTableMappingFor(aClassInfo);
        Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aClassInfo, aClassMapping, false);

        for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet())
        {
            deleteBufferInDb(aObjToDelete, entry.getKey(), entry.getValue(), aClassInfo);
        }
    }

    /**
     * Delete the row for the given object in the specified table
     *
     * @param aObjToDelete
     * @param aTableName
     * @param mappings
     * @param aClassInfo
     * @throws JdyPersistentException
     */
    private synchronized void deleteBufferInDb(ValueObject aObjToDelete, String aTableName, List<AspectMapping> mappings, ClassInfo aClassInfo)
            throws JdyPersistentException
    {
        PreparedStatement deleteStmt = null;
        try
        {
            String stmt = createDeleteStatement(aTableName, mappings);

            log.fine(stmt);

            deleteStmt = dbConnection.getConnection().prepareStatement(stmt);

            // set values in where clause
            setValuesInPreparedStmt(aObjToDelete, mappings, deleteStmt, KeysOrValues.ONLY_KEYS, 1, null);

            deleteStmt.executeUpdate();
            UtilSql.getDefault().printWarnings(deleteStmt.getWarnings());
            deleteStmt.close();
            deleteStmt = null;
        } catch (SQLException catchedExcep)
        {
            throw this.buildDbException(catchedExcep);
        } catch (Throwable catchedExcep)
        {
            throw new JdyPersistentException(catchedExcep.getLocalizedMessage(), catchedExcep);
        } finally
        {
            if (deleteStmt != null)
            {
                try
                {
                    deleteStmt.close();
                } catch (SQLException catchedExcep)
                {
                    throw this.buildDbException(catchedExcep);
                }
            }
        }
    }

    @Override
    public TypedValueObject insertObjectInDb(ValueObject aObjToInsert, ClassInfo aInfo) throws JdyPersistentException
    {
        JDyClassInfoToTableMapping aClassMapping = tableMapping.getTableMappingFor(aInfo);
        Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aInfo, aClassMapping, false);

        TypedHashedValueObject clonedObj = new TypedHashedValueObject(aInfo);
        for (AttributeInfo curAttrInfo : aInfo.getAttributeInfoIterator())
        {

            clonedObj.setValue(curAttrInfo, aObjToInsert.getValue(curAttrInfo));
        }

        for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet())
        {
            insertBufferInDb(aObjToInsert, entry.getKey(), entry.getValue(), aInfo, clonedObj);
        }

        return clonedObj;
    }

    /**
     * Insert a row for the given object in the specified table
     *
     * @param objToInsert
     * @param aTableName
     * @param mappings
     * @param aClassInfo
     * @param aNewObj
     * @throws JdyPersistentException
     */
    private synchronized void insertBufferInDb(ValueObject objToInsert, String aTableName,
            List<AspectMapping> mappings, ClassInfo aClassInfo, TypedHashedValueObject aNewObj) throws JdyPersistentException
    {
        PreparedStatement insertStmt = null;
        try
        {
            PrimitiveAttributeInfo generatedKeyAttr = tableMapping.getGeneratedIdAttribute(aClassInfo);

            String stmt = createInsertStatment(aTableName, mappings, generatedKeyAttr);

            log.fine(stmt);

            if (generatedKeyAttr != null)
            {
                insertStmt = dbConnection.getConnection().prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            } else
            {
                insertStmt = dbConnection.getConnection().prepareStatement(stmt);
            }

            setValuesInPreparedStmt(objToInsert, mappings, insertStmt, KeysOrValues.ALL, 1, generatedKeyAttr);

            insertStmt.setQueryTimeout(5);

            insertStmt.executeUpdate();
            UtilSql.getDefault().printWarnings(insertStmt.getWarnings());

            if (generatedKeyAttr != null)
            {

                ResultSet keysRs = insertStmt.getGeneratedKeys();
                Long newKey = null;
                while (keysRs.next())
                {
                    newKey = keysRs.getLong(1);     // Get automatically generated key 
                }
                aNewObj.setValue(generatedKeyAttr, newKey);
            }

            insertStmt.close();
            insertStmt = null;

//			doLobUpdateWorkaround(objToInsert, aTableName, aList, aClassInfo);
        } catch (SQLException catchedExcep)
        {
            throw this.buildDbException(catchedExcep);
        } catch (Throwable catchedExcep)
        {
            throw new JdyPersistentException(catchedExcep);
        } finally
        {
            if (insertStmt != null)
            {
                try
                {
                    insertStmt.close();
                } catch (SQLException catchedExcep)
                {
                    throw this.buildDbException(catchedExcep);
                }
            }
        }

    }

    @Override
    public void updateObjectToDb(ValueObject aObjToModify, ClassInfo aClassInfo) throws JdyPersistentException
    {
        boolean hasOnlyKeys = true;
        for (AttributeInfo curAtr : aClassInfo.getAttributeInfoIterator())
        {
            hasOnlyKeys &= curAtr.isKey();
        }

        if (!hasOnlyKeys)
        {
            JDyClassInfoToTableMapping aClassMapping = tableMapping.getTableMappingFor(aClassInfo);
            Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aClassInfo, aClassMapping, false);

            for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet())
            {
                updateBufferToDb(aObjToModify, entry.getKey(), entry.getValue(), aClassInfo);
            }
        }
    }

    protected final synchronized void updateBufferToDb(ValueObject objToUpdate, String aTableName,
            List<AspectMapping> mappings, ClassInfo aClassInfo) throws JdyPersistentException
    {
        PreparedStatement updateStmt = null;
        try
        {
            String stmt;
            try
            {
                stmt = creatUpdateStatement(aTableName, mappings);

                updateStmt = dbConnection.getConnection().prepareStatement(stmt);

                // set non-key values to update
                int nextIdx = setValuesInPreparedStmt(objToUpdate, mappings, updateStmt, KeysOrValues.ONLY_VALUES, 1, null);

                // set key values in where clause
                setValuesInPreparedStmt(objToUpdate, mappings, updateStmt, KeysOrValues.ONLY_KEYS, nextIdx, null);

                updateStmt.executeUpdate();
                UtilSql.getDefault().printWarnings(updateStmt.getWarnings());
                updateStmt.close();
                updateStmt = null;

                doLobUpdateWorkaround(objToUpdate, aTableName, mappings, aClassInfo);
            } catch (NoColumnSetException ex)
            {
                log.fine("No Column to change in statement ");
            }

        } catch (SQLException catchedExcep)
        {
            throw UtilSql.getDefault().buildDbException(catchedExcep);
        } catch (Throwable catchedExcep)
        {
            throw UtilSql.getDefault().buildDbException(catchedExcep);
        } finally
        {
            if (updateStmt != null)
            {
                try
                {
                    updateStmt.close();
                } catch (SQLException catchedExcep)
                {
                    throw this.buildDbException(catchedExcep);
                }
            }
        }
    }

    protected void doLobUpdateWorkaround(ValueObject objToUpdate, String tableName, List<AspectMapping> mappings, ClassInfo classInfo)
            throws JdyPersistentException
    {
    }

    /**
     *
     * @param objToInsert
     * @param mappings
     * @param insertStmt
     * @param aGeneratedKeyAttr
     * @param onlyKeys
     * @return
     * @throws JdyPersistentException
     */
    private int setValuesInPreparedStmt(ValueObject objToInsert, List<AspectMapping> mappings,
            PreparedStatement insertStmt, KeysOrValues keysOrValues, int startIndx, PrimitiveAttributeInfo aGeneratedKeyAttr) throws JdyPersistentException
    {
        PreparedStatementWriter typeHandler = createPreparedStatementWriter(insertStmt, startIndx);
        for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
        {
            AspectMapping curMapping = attrPathIter.next();
            if (KeysOrValues.ALL == keysOrValues
                    || (KeysOrValues.ONLY_KEYS == keysOrValues && curMapping.getMappedPath().firstAttribute().isKey())
                    || (KeysOrValues.ONLY_VALUES == keysOrValues && !curMapping.getMappedPath().firstAttribute().isKey()))
            {

                Object primtivObjToinsert = objToInsert;
                if (curMapping.getMappedPath().referenceIterator() != null)
                {
                    // iterate through aspect path
                    for (Iterator<ObjectReferenceAttributeInfo> referenceIter = curMapping.getMappedPath().referenceIterator(); referenceIter.hasNext();)
                    {
                        ObjectReferenceAttributeInfo curObjAttr = referenceIter.next();
                        if (primtivObjToinsert != null)
                        {
                            primtivObjToinsert = ((ValueObject) primtivObjToinsert).getValue(curObjAttr);
                        }
                    }
                }

                if (primtivObjToinsert != null)
                {
                    primtivObjToinsert = ((ValueObject) primtivObjToinsert).getValue(curMapping.getMappedPath().getLastInfo());
                }

                if ((aGeneratedKeyAttr == null) || ((aGeneratedKeyAttr != null) && curMapping.getMappedPath().getLastInfo() != aGeneratedKeyAttr))
                {

                    log.log(Level.FINE, "param + {0} {1}", new Object[]{typeHandler.getIndex(), (primtivObjToinsert == null) ? "<null>" : primtivObjToinsert.toString()});

                    curMapping.getMappedPath().getLastInfo().getType().handlePrimitiveKey(typeHandler, primtivObjToinsert);
                }
            }
        }
        return typeHandler.getIndex();
    }

    protected PreparedStatementWriter createPreparedStatementWriter(PreparedStatement insertStmt, int startIndx)
    {
        return new PreparedStatementWriter(insertStmt, startIndx);
    }

}
