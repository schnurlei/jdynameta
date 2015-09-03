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
package de.jdynameta.dbaccess.jdbc.reader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectPath;
import de.jdynameta.base.creation.db.JdyMappingUtil;
import de.jdynameta.base.creation.db.JdyRepositoryTableMapping;
import de.jdynameta.base.creation.db.KeyAttributeHandler;
import de.jdynameta.base.creation.db.SqlObjectReader;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.AndExpression;
import de.jdynameta.base.metainfo.filter.AssociationExpression;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.ExpressionVisitor;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.ObjectReferenceEqualExpression;
import de.jdynameta.base.metainfo.filter.ObjectReferenceSubqueryExpression;
import de.jdynameta.base.metainfo.filter.OperatorExpression;
import de.jdynameta.base.metainfo.filter.OrExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.FilterUtil;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnectionHolder;
import de.jdynameta.dbaccess.jdbc.connection.PreparedStatementWriter;
import de.jdynameta.dbaccess.jdbc.connection.UtilSql;

/**
 * @author Rainer
 *
 * @version 10.07.2002
 */
@SuppressWarnings("serial")
public class JdbcObjectReader extends SqlObjectReader
{
    private static final Logger log = Logger.getLogger(JdbcObjectReader.class.getName());
    private JdbcConnectionHolder dbConnection = null;
    private final JdyRepositoryTableMapping tableMapping;

    public JdbcObjectReader(JdbcConnectionHolder theConnection, JdyRepositoryTableMapping aTableMapping)
    {
        dbConnection = theConnection;
        this.tableMapping = aTableMapping;
    }

    @Override
    public JdyRepositoryTableMapping getTableMapping()
    {
        return tableMapping;
    }

    @Override
    protected <TCreatedObjFromValueObj> ObjectList<TCreatedObjFromValueObj> loadValuesFromDbFor(ClassInfoQuery aFilter, ObjectCreator<TCreatedObjFromValueObj> aObjCreator) throws JdyPersistentException
    {
        ClassInfo classInfo = aFilter.getResultInfo();
        JDyClassInfoToTableMapping aClassMapping = tableMapping.getTableMappingFor(classInfo);
        // add tables of the queried class and its superclasses
        LinkedHashMap<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(classInfo, aClassMapping, false);

        ArrayList<TableInfo> tableList = new ArrayList<>();
        for (String tableName : tableName2ColumnMap.keySet())
        {
            tableList.add(new TableInfo(tableName, false));
        }

        try
        {
            log.log(Level.FINER, "Beginn loadValues {0}", System.currentTimeMillis());

            StringBuffer stmtBuffer = new StringBuffer(300);
            stmtBuffer.append(" SELECT ");

            boolean isFirstAttribute = addClassAndSuperclassColumns(tableName2ColumnMap, stmtBuffer);

            // add tables of the queried class and its subclasses
            List<ClassInfo> subclassColl = getSubclassCollsFor(classInfo);
            for (Iterator<ClassInfo> subclassMapIter = subclassColl.iterator(); subclassMapIter.hasNext();)
            {

                ClassInfo curSubclassInfo = subclassMapIter.next();
                JDyClassInfoToTableMapping aSubClassMapping = tableMapping.getTableMappingFor(curSubclassInfo);
                Map<String, List<AspectMapping>> subtableName2ColumnMap = JdyMappingUtil.createTableMapping(curSubclassInfo, aSubClassMapping, true);

                for (Map.Entry<String, List<AspectMapping>> entry : subtableName2ColumnMap.entrySet())
                {
                    addColumnNames(entry.getValue(), stmtBuffer, isFirstAttribute);

                    List<AspectMapping> mappingList = tableName2ColumnMap.get(entry.getKey());
                    if (mappingList == null)
                    {
                        tableList.add(new TableInfo(entry.getKey(), true));
                        mappingList = new ArrayList<AspectMapping>();
                        tableName2ColumnMap.put(entry.getKey(), mappingList);
                    }
                    mappingList.addAll(entry.getValue());
                }
            }
            stmtBuffer.append("\n From ");

            isFirstAttribute = true;
            List<AspectMapping> firstKeyList = null;
            for (TableInfo tableInfo : tableList)
            {

                if (isFirstAttribute)
                {
                    stmtBuffer.append(tableInfo.getTableName());
                    firstKeyList = getKeyAspects(tableName2ColumnMap.get(tableInfo.getTableName()));
                    isFirstAttribute = false;
                } else
                {
                    String joinedTableName = tableInfo.getTableName();
                    List<AspectMapping> joinedAspects = getKeyAspects(tableName2ColumnMap.get(tableInfo.getTableName()));
                    if (tableInfo.IsSubclass())
                    {
                        stmtBuffer.append("\n left outer join ");
                    } else
                    {
                        stmtBuffer.append("\n inner join ");
                    }
                    stmtBuffer.append(joinedTableName);
                    stmtBuffer.append("\n on ");

                    boolean isFirstExpr = true;

                    for (int i = 0; i < firstKeyList.size(); i++)
                    {

                        if (isFirstExpr)
                        {
                            isFirstExpr = false;
                        } else
                        {
                            stmtBuffer.append(" AND ");
                        }
                        stmtBuffer.append(firstKeyList.get(i).getTableName()).append(".").append(firstKeyList.get(i).getColumnName());
                        stmtBuffer.append(" = ");
                        stmtBuffer.append(joinedAspects.get(i).getTableName()).append(".").append(joinedAspects.get(i).getColumnName());
                    }
                }
            }

            if (aFilter.getFilterExpression() != null)
            {

                stmtBuffer.append(" WHERE ");
                aFilter.getFilterExpression().visit(new JdbcExpressionVisitor(stmtBuffer, classInfo, aClassMapping));
            }

            log.fine(stmtBuffer.toString());
            log.log(Level.FINER, "Beginn execute {0}", System.currentTimeMillis());

            ResultSet selectResult;
            Statement selectStatement;
            if (aFilter.getFilterExpression() != null)
            {

                PreparedStatement prepStmt = dbConnection.getConnection().prepareStatement(stmtBuffer.toString());
                aFilter.getFilterExpression().visit(new JdbcExprPreparedStatementVisitor(prepStmt));
                selectResult = prepStmt.executeQuery();
                selectStatement = prepStmt;
            } else
            {

                selectStatement = dbConnection.getConnection().createStatement();
                selectResult = selectStatement.executeQuery(stmtBuffer.toString());
                UtilSql.getDefault().printWarnings(selectStatement.getWarnings());

                log.log(Level.FINER, "Stmt executed {0}", System.currentTimeMillis());
            }

            ArrayList<TCreatedObjFromValueObj> resultModelColl = new ArrayList<>(20);
            while (selectResult != null && selectResult.next())
            {

                TypedHashedValueObject result = createModelForResultSetLine(selectResult, classInfo, aObjCreator, subclassColl);
                resultModelColl.add(aObjCreator.createObjectFor(result));
            }

            log.log(Level.FINER, "Attributes read{0}", System.currentTimeMillis());

            selectStatement.close();

            return new DefaultObjectList<>(resultModelColl);

            // build order

            /*String orderStmt = "";
             if( filter.getResultOrder() != null && filter.getResultOrder().length != 0) {
             for (int i = 0; i < filter.getResultOrder().length; i++) {
             AbstractAttributeInfo info = filter.getMetainfo().getAttributeInfoAt(filter.getResultOrder()[i]);
             if ( info != null) {
             if ( orderStmt.length() != 0)
             orderStmt += ", ";
             orderStmt += info.getSqlAttributeNames("");
             }
             }
             }
             if ( orderStmt.length() != 0)
             stmtText += " ORDER BY " + orderStmt;
             */
        } catch (SQLException catchedExcep)
        {
            catchedExcep.printStackTrace();
            log.log(Level.SEVERE, catchedExcep.getLocalizedMessage(), catchedExcep);
            throw UtilSql.getDefault().buildDbException(catchedExcep);
        } catch (Throwable catchedExcep)
        {
            catchedExcep.printStackTrace();
            throw UtilSql.getDefault().buildDbException(catchedExcep);
        }

    }

    /**
     * Create a value model for the current line in the given resultset The
     * correct sub
     *
     * @param <TCreatedObjFromValueObj>
     * @param resultSet
     * @param aClassInfo - superclass for which the resultset was read. the
     * correct subclass has to be detected
     * @param aObjCreator
     * @param aSubclassColl
     * @return
     * @throws JdyPersistentException
     */
    protected <TCreatedObjFromValueObj> TypedHashedValueObject createModelForResultSetLine(ResultSet resultSet, ClassInfo aClassInfo, ObjectCreator<TCreatedObjFromValueObj> aObjCreator, List<ClassInfo> aSubclassColl) throws JdyPersistentException
    {
        TypedHashedValueObject result = new TypedHashedValueObject();
        ClassInfo resultInfo = aClassInfo;

        ResultSetPrimitiveTypeVisitor typeVisitor = createPrimitveTypeVisitor(resultSet);

        {
            for (AttributeInfo curAttributeInfo : aClassInfo.getAttributeInfoIterator())
            {
                Object aValue = createValueFor(typeVisitor, resultInfo, curAttributeInfo, aObjCreator, new AspectPath());
                result.setValue(curAttributeInfo, aValue);
            }
        }

        for (Iterator<ClassInfo> subclassMapIter = aSubclassColl.iterator(); subclassMapIter.hasNext();)
        {

            ClassInfo curSubclassInfo = subclassMapIter.next();
            for (AttributeInfo curAttributeInfo : curSubclassInfo.getAttributeInfoIterator())
            {
                if (curSubclassInfo.isSubclassAttribute(curAttributeInfo)
                        || curAttributeInfo.isKey())
                {
                    Object aValue = createValueFor(typeVisitor, curSubclassInfo, curAttributeInfo, aObjCreator, new AspectPath());
                    if (curAttributeInfo.isKey())
                    {
                        if (aValue != null)
                        {
                            resultInfo = curSubclassInfo;
                        }
                    } else
                    {
                        if (curSubclassInfo.isSubclassAttribute(curAttributeInfo))
                        {
                            result.setValue(curAttributeInfo, aValue);
                        }
                    }
                }
            }
        }

        result.setClassInfo(resultInfo);
        return result;
    }

    /**
     * @param resultSet
     * @return
     */
    protected ResultSetPrimitiveTypeVisitor createPrimitveTypeVisitor(ResultSet resultSet)
    {
        return new ResultSetPrimitiveTypeVisitor(resultSet, "");
    }

    protected <TCreatedObjFromValueObj> Object createValueFor(ResultSetPrimitiveTypeVisitor aTypeVisitor, ClassInfo aClassInfo, AttributeInfo aAttrInfo, ObjectCreator<TCreatedObjFromValueObj> aObjCreator, AspectPath callStack) throws JdyPersistentException
    {
        Object result;

        if (aAttrInfo instanceof ObjectReferenceAttributeInfo)
        {

            TypedHashedValueObject referencedModel = new TypedHashedValueObject();
            ClassInfo refInfo = ((ObjectReferenceAttributeInfo) aAttrInfo).getReferencedClass();

            boolean valueWasNull = false;
            for (AttributeInfo curAttributeInfo : refInfo.getAttributeInfoIterator())
            {
                if (curAttributeInfo.isKey())
                {
                    callStack.addReference((ObjectReferenceAttributeInfo) aAttrInfo);
                    Object value = createValueFor(aTypeVisitor, aClassInfo, curAttributeInfo, aObjCreator, callStack);
                    callStack.removeLastReference((ObjectReferenceAttributeInfo) aAttrInfo);
                    if (value == null)
                    {
                        valueWasNull = true;
                    }
                    referencedModel.setValue(curAttributeInfo, value);
                }
            }

            if (!valueWasNull)
            {
                // determine the concrete subclass for the referenced object
                if (refInfo.hasSubClasses())
                {
                    DefaultClassInfoQuery filter = FilterUtil.createSearchEqualObjectFilter(refInfo, referencedModel);
                    ObjectList<TCreatedObjFromValueObj> loadedObjects = loadValuesFromDbFor(filter, aObjCreator);
                    assert (loadedObjects.size() == 1);
                    result = loadedObjects.get(0);
                } else
                {
                    referencedModel.setClassInfo(refInfo);
                    result = referencedModel;
                }
            } else
            {
                result = null;
            }
        } else
        {
            callStack.setLastInfo((PrimitiveAttributeInfo) aAttrInfo);
            List<AspectMapping> mapping = tableMapping.getTableMappingFor(aClassInfo).getColumnMappingsFor(callStack);
            aTypeVisitor.setIndex(createAbbreviationColumnName(mapping.get(0)));
            result = ((PrimitiveAttributeInfo) aAttrInfo).getType().handlePrimitiveKey(aTypeVisitor);
            callStack.setLastInfo(null);
        }

        return result;
    }

    /**
     * @author rsc
     */
    public static class JdbcExprPreparedStatementVisitor implements ExpressionVisitor
    {
        private final PreparedStatementWriter prepStmtTypeVisitor;

        public JdbcExprPreparedStatementVisitor(PreparedStatement aPrepStmt)
        {
            super();
            this.prepStmtTypeVisitor = new PreparedStatementWriter(aPrepStmt, 1);
        }

        @Override
        public void visitAndExpression(AndExpression aAndExpr) throws JdyPersistentException
        {
            for (Iterator<ObjectFilterExpression> exprIter = aAndExpr.getExpressionIterator(); exprIter.hasNext();)
            {
                ObjectFilterExpression curExpr = exprIter.next();
                curExpr.visit(this);
            }
        }

        @Override
        public void visitOrExpression(OrExpression anOrExpr) throws JdyPersistentException
        {
            for (Iterator<ObjectFilterExpression> exprIter = anOrExpr.getExpressionIterator(); exprIter.hasNext();)
            {
                ObjectFilterExpression curExpr = exprIter.next();
                curExpr.visit(this);
            }
        }

        @Override
        public void visitOperatorExpression(OperatorExpression aOpExpr) throws JdyPersistentException
        {
            if (aOpExpr.getCompareValue() != null)
            {
                aOpExpr.getAttributeInfo().getType().handlePrimitiveKey(prepStmtTypeVisitor, aOpExpr.getCompareValue());
            }
        }

        @Override
        public void visitReferenceEqualExpression(ObjectReferenceEqualExpression aReferenceExpr) throws JdyPersistentException
        {

            AttributeHandler exprHandler = new AttributeHandler()
            {
                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                        throws JdyPersistentException
                {
                    if (objToHandle != null)
                    {
                        aInfo.getReferencedClass().handleAttributes(new KeyAttributeHandler(this), objToHandle);
                    }
                }

                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                        throws JdyPersistentException
                {
                    aInfo.getType().handlePrimitiveKey(prepStmtTypeVisitor, objToHandle);
                }
            };

            exprHandler.handleObjectReference(aReferenceExpr.getAttributeInfo(), aReferenceExpr.getCompareValue());
        }

        @Override
        public void visitAssociationExpression(AssociationExpression opExpr) throws JdyPersistentException
        {
            throw new UnsupportedOperationException("Associations are not supported at the moment");

        }

        @Override
        public void visitReferenceQueryExpr(ObjectReferenceSubqueryExpression expression) throws JdyPersistentException
        {
            throw new UnsupportedOperationException("ObjectReferenceSubqueryExpression is not supported at the moment");
        }
    }

    private static class TableInfo
    {
        private final String tableName;
        private final boolean subclass;

        public TableInfo(String tableName, boolean isSubclass)
        {
            super();
            this.tableName = tableName;
            this.subclass = isSubclass;
        }

        public String getTableName()
        {
            return tableName;
        }

        public boolean IsSubclass()
        {
            return subclass;
        }

    }
}
