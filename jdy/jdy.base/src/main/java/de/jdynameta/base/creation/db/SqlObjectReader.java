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
package de.jdynameta.base.creation.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.creation.ObjectReader;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectPath;
import de.jdynameta.base.metainfo.AttributeHandler;
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
import de.jdynameta.base.metainfo.filter.OperatorEqual;
import de.jdynameta.base.metainfo.filter.OperatorExpression;
import de.jdynameta.base.metainfo.filter.OperatorGreater;
import de.jdynameta.base.metainfo.filter.OperatorLess;
import de.jdynameta.base.metainfo.filter.OperatorVisitor;
import de.jdynameta.base.metainfo.filter.OrExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

@SuppressWarnings("serial")
public abstract class SqlObjectReader implements ObjectReader
{

    public abstract JdyRepositoryTableMapping getTableMapping();

    @Override
    public <TCreatedObjFromValueObj> ObjectList<TCreatedObjFromValueObj> loadValuesFromDb(ClassInfoQuery aFilter, ObjectCreator<TCreatedObjFromValueObj> aObjCreator)
            throws JdyPersistentException
    {
        try
        {
            return loadValuesFromDbFor(aFilter, aObjCreator);
        } catch (ProxyResolveException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    /**
     * Get al sublcasses for the given Class
     *
     * @param aClassInfo
     * @return
     * @throws ProxyResolveException
     */
    protected List<ClassInfo> getSubclassCollsFor(ClassInfo aClassInfo) throws ProxyResolveException
    {
        ArrayList<ClassInfo> resultList = new ArrayList<>();

        if (aClassInfo.getAllSubclasses() != null
                && aClassInfo.getAllSubclasses().size() > 0)
        {

            for (Iterator<ClassInfo> subclassIter = aClassInfo.getAllSubclasses().iterator(); subclassIter.hasNext();)
            {
                ClassInfo curSubclass = subclassIter.next();
                resultList.add(curSubclass);
                resultList.addAll(getSubclassCollsFor(curSubclass));
            }
        }

        return resultList;
    }

    protected abstract <TCreatedObjFromValueObj> ObjectList<TCreatedObjFromValueObj> loadValuesFromDbFor(ClassInfoQuery aFilter, ObjectCreator<TCreatedObjFromValueObj> aObjCreator) throws JdyPersistentException;

    protected List<AspectMapping> getKeyAspects(List<AspectMapping> aSourceMapping)
    {
        List<AspectMapping> keyList = new ArrayList<>();
        for (AspectMapping curAspectMapping : aSourceMapping)
        {
            if (curAspectMapping.getMappedPath().firstAttribute().isKey())
            {
                keyList.add(curAspectMapping);
            }
        }
        return keyList;
    }

    protected boolean addClassAndSuperclassColumns(Map<String, List<AspectMapping>> tableName2ColumnMap,
            StringBuffer stmtBuffer)
    {
        boolean isFirstAttribute = true;
        for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet())
        {
            addColumnNames(entry.getValue(), stmtBuffer, isFirstAttribute);
            isFirstAttribute = false;
        }
        return isFirstAttribute;
    }

    /**
     * add the comma separated column names to the statement string
     *
     * @param mappings
     * @param stmtBuffer
     */
    protected void addColumnNames(List<AspectMapping> mappings, StringBuffer stmtBuffer, boolean isFirstAttribute)
    {
        for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
        {
            AspectMapping curMapping = attrPathIter.next();
            if (!isFirstAttribute)
            {
                stmtBuffer.append(',');
            }
            stmtBuffer.append(createQualifiedColumnName(curMapping)).append(' ');
            stmtBuffer.append(createAbbreviationColumnName(curMapping));
            isFirstAttribute = false;
        }
    }

    private String createQualifiedColumnName(AspectMapping curMapping)
    {
        return curMapping.getTableName() + "." + curMapping.getColumnName();
    }

    /**
     *
     * @param curMapping
     * @return
     */
    protected String createAbbreviationColumnName(AspectMapping curMapping)
    {
        return curMapping.getTableShortName() + "_" + curMapping.getColumnName();
    }

    /**
     * @author rsc
     *
     */
    public static class JdbcExpressionVisitor implements ExpressionVisitor
    {

        private final StringBuffer buffer;
        private final JdbcOperatorVisitor operatorVisitor;
        private final JDyClassInfoToTableMapping mapping;

        public JdbcExpressionVisitor(StringBuffer aBuffer, ClassInfo aExprClassInfo, JDyClassInfoToTableMapping aMapping)
        {
            super();
            this.buffer = aBuffer;
            this.operatorVisitor = new JdbcOperatorVisitor(this.buffer);
            this.mapping = aMapping;
        }

        /**
         * @see
         * de.comafra.model.reader.ExpressionVisitor#visitAndExpression(ExpressionAnd)
         */
        @Override
        public void visitAndExpression(AndExpression aAndExpr) throws JdyPersistentException
        {
            boolean isFirstExpr = true;

            buffer.append(" ( ");
            for (Iterator<ObjectFilterExpression> exprIter = aAndExpr.getExpressionIterator(); exprIter.hasNext();)
            {
                ObjectFilterExpression curExpr = exprIter.next();
                if (isFirstExpr)
                {
                    isFirstExpr = false;
                } else
                {
                    buffer.append(" AND ");
                }
                buffer.append(" ( ");
                curExpr.visit(this);
                buffer.append(" ) ");
            }
            buffer.append(" ) ");

        }

        /**
         * @see
         * de.comafra.model.reader.ExpressionVisitor#visitAndExpression(ExpressionAnd)
         */
        @Override
        public void visitOrExpression(OrExpression aOrExpr) throws JdyPersistentException
        {
            boolean isFirstExpr = true;

            buffer.append(" ( ");
            for (Iterator<ObjectFilterExpression> exprIter = aOrExpr.getExpressionIterator(); exprIter.hasNext();)
            {
                ObjectFilterExpression curExpr = exprIter.next();
                if (isFirstExpr)
                {
                    isFirstExpr = false;
                } else
                {
                    buffer.append(" OR ");
                }
                buffer.append(" ( ");
                curExpr.visit(this);
                buffer.append(" ) ");
            }
            buffer.append(" ) ");

        }

        /* (non-Javadoc)
         * @see de.comafra.model.metainfo.filter.ExpressionVisitor#visitReferenceEqualExpression(de.comafra.model.metainfo.filter.ObjectReferenceEqualExpression)
         */
        @Override
        public void visitReferenceEqualExpression(final ObjectReferenceEqualExpression aReferenceExpr)
                throws JdyPersistentException
        {
            final AspectPath callStack = new AspectPath();

            if (aReferenceExpr.isNotEqual())
            {
                buffer.append(" NOT ");
            }
            buffer.append(" ( ");

            AttributeHandler exprHandler = new AttributeHandler()
            {
                boolean isFirstExpr = true;
                DefaultOperatorEqual equalOp = new DefaultOperatorEqual();

                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                        throws JdyPersistentException
                {
                    callStack.addReference(aInfo);
                    aInfo.getReferencedClass().handleAttributes(new KeyAttributeHandler(this), objToHandle);
                    callStack.removeLastReference(aInfo);
                }

                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                        throws JdyPersistentException
                {
                    if (isFirstExpr)
                    {
                        isFirstExpr = false;
                    } else
                    {
                        buffer.append(" AND ");
                    }
                    buffer.append(" ( ");

                    callStack.setLastInfo(aInfo);
                    List<AspectMapping> columnName = mapping.getColumnMappingsFor(callStack);
                    callStack.setLastInfo(null);

                    if (aReferenceExpr.getCompareValue() != null)
                    {
                        buffer.append(columnName.get(0).getTableName());
                        buffer.append(".");
                        buffer.append(columnName.get(0).getColumnName());
                        operatorVisitor.visitOperatorEqual(equalOp);
                        buffer.append(" ? ");
                        buffer.append(" ) ");
                    } else
                    {
                        buffer.append(columnName.get(0).getTableName());
                        buffer.append(".");
                        buffer.append(columnName.get(0).getColumnName());
                        buffer.append("  IS NULL  ");
                        buffer.append(" ) ");

                    }
                }
            };

            exprHandler.handleObjectReference(aReferenceExpr.getAttributeInfo(), aReferenceExpr.getCompareValue());
            buffer.append(" ) ");
        }

        /**
         * @see
         * de.comafra.model.reader.ExpressionVisitor#visitOperatorExpression(OperatorExpression)
         */
        @Override
        public void visitOperatorExpression(OperatorExpression aOpExpr)
        {
            List<AspectMapping> columnMap = mapping.getColumnMappingsFor(new AspectPath(aOpExpr.getAttributeInfo()));

            String tableName = columnMap.get(0).getTableName();
            String columnName = columnMap.get(0).getColumnName();

            if (aOpExpr.getCompareValue() != null)
            {
                buffer.append(tableName);
                buffer.append(".");
                buffer.append(columnName);
                aOpExpr.getOperator().visitOperatorHandler(operatorVisitor);
                buffer.append(" ? ");
            } else
            {
                buffer.append(tableName);
                buffer.append(".");
                buffer.append(columnName);
                if (aOpExpr.getOperator() instanceof OperatorEqual
                        && ((OperatorEqual) aOpExpr.getOperator()).isNotEqual())
                {
                    buffer.append(" IS NOT NULL ");
                } else
                {
                    buffer.append(" IS NULL ");
                }
            }
        }

        public void visitAssociationExpression(AssociationExpression opExpr) throws JdyPersistentException
        {
            throw new UnsupportedOperationException("Associations are not supported at the moment");
        }

        public void visitReferenceQueryExpr(ObjectReferenceSubqueryExpression expression) throws JdyPersistentException
        {
            throw new UnsupportedOperationException("ObjectReferenceSubqueryExpression is not supported at the moment");
        }
    }

    /**
     *
     * @author rainer
     *
     */
    protected static class JdbcOperatorVisitor implements OperatorVisitor
    {

        private StringBuffer buffer;

        public JdbcOperatorVisitor(StringBuffer aBuffer)
        {
            this.buffer = aBuffer;
        }

        /**
         * @see de.comafra.model.reader.OperatorVisitor#visitOperatorEqual()
         */
        public void visitOperatorEqual(OperatorEqual aOperator)
        {
            if (aOperator.isNotEqual())
            {
                buffer.append(" <> ");
            } else
            {
                buffer.append(" = ");
            }
        }

        public void visitOperatorGreater(OperatorGreater aOperator)
        {
            if (aOperator.isAlsoEqual())
            {
                buffer.append(" >= ");
            } else
            {
                buffer.append(" > ");
            }
        }

        public void visitOperatorLess(OperatorLess aOperator)
        {
            if (aOperator.isAlsoEqual())
            {
                buffer.append(" <= ");
            } else
            {
                buffer.append(" < ");
            }
        }
    }

}
