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
package de.jdynameta.base.metainfo.filter.defaultimpl;

import java.util.ArrayList;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ExpressionPrimitiveOperator;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.value.ValueObject;

/**
 * Convenience creator to create {@link DefaultClassInfoQuery}
 *
 * @author rs
 *
 */
public class QueryCreator
{

    private final ClassInfo resultInfo;
    private ObjectFilterExpression createdExpr;

    public QueryCreator(ClassInfo aResultInfo)
    {
        assert (aResultInfo != null);
        this.resultInfo = aResultInfo;
    }

    public DefaultClassInfoQuery query() throws FilterCreationException
    {
        return new DefaultClassInfoQuery(resultInfo, createdExpr);
    }

    public QueryCreator referenceEqual(String refExternalName, ValueObject equalObj) throws FilterCreationException
    {
        DefaultObjectReferenceEqualExpression refExpr = new DefaultObjectReferenceEqualExpression((ObjectReferenceAttributeInfo) resultInfo.getAttributeInfoForExternalName(refExternalName), equalObj);
        addExpression(refExpr);
        return this;
    }

    public QueryCreator greater(String anExAttrName, Object aCompareValue) throws FilterCreationException
    {
        addOperatorExpression(anExAttrName, aCompareValue, DefaultOperatorGreater.getGreateInstance());
        return this;
    }

    public QueryCreator less(String anExAttrName, Object aCompareValue) throws FilterCreationException
    {
        addOperatorExpression(anExAttrName, aCompareValue, DefaultOperatorLess.getLessInstance());
        return this;
    }

    public QueryCreator equal(String anExAttrName, Object aCompareValue) throws FilterCreationException
    {
        addOperatorExpression(anExAttrName, aCompareValue, DefaultOperatorEqual.getEqualInstance());
        return this;
    }

    private void addOperatorExpression(String anExAttrName, Object aCompareValue, ExpressionPrimitiveOperator aOperator) throws FilterCreationException
    {
        DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
        opExpr.setAttributeInfo((PrimitiveAttributeInfo) resultInfo.getAttributeInfoForExternalName(anExAttrName));
        opExpr.setCompareValue(aCompareValue);
        opExpr.setMyOperator(aOperator);
        addExpression(opExpr);
    }

    public AndQueryCreator and()
    {
        return new AndQueryCreator(resultInfo, this);
    }

    public QueryCreator or()
    {
        return new OrQueryCreator(resultInfo, this);
    }

    public QueryCreator end() throws FilterCreationException
    {
        throw new FilterCreationException("No Multiple Expression open");
    }

    protected void addExpression(ObjectFilterExpression anExpr) throws FilterCreationException
    {
        if (createdExpr != null)
        {
            throw new FilterCreationException("Expression already exists");
        }
        createdExpr = anExpr;
    }

    /**
     *
     * @author rs
     *
     */
    public static class AndQueryCreator extends QueryCreator
    {

        private final ArrayList<ObjectFilterExpression> expressions = new ArrayList<>();
        private final QueryCreator parentCreator;

        public AndQueryCreator(ClassInfo aResultInfo, QueryCreator aParentCreator)
        {
            super(aResultInfo);
            this.parentCreator = aParentCreator;
        }

        @Override
        protected void addExpression(ObjectFilterExpression anExpr)
        {
            expressions.add(anExpr);
        }

        @Override
        public QueryCreator end() throws FilterCreationException
        {
            this.parentCreator.addExpression(new DefaultExpressionAnd(expressions));
            return parentCreator;
        }

        @Override
        public DefaultClassInfoQuery query() throws FilterCreationException
        {
            throw new FilterCreationException("And not closes");
        }

    }

    /**
     *
     * @author rs
     *
     */
    public static class OrQueryCreator extends QueryCreator
    {

        private ArrayList<ObjectFilterExpression> expressions;
        private final QueryCreator parentCreator;

        public OrQueryCreator(ClassInfo aResultInfo, QueryCreator aParentCreator)
        {
            super(aResultInfo);
            this.expressions = new ArrayList<>();
            this.parentCreator = aParentCreator;
        }

        @Override
        protected void addExpression(ObjectFilterExpression anExpr)
        {
            expressions.add(anExpr);
        }

        @Override
        public QueryCreator end() throws FilterCreationException
        {
            this.parentCreator.addExpression(new DefaultOrExpression(expressions));
            return parentCreator;
        }

        @Override
        public DefaultClassInfoQuery query() throws FilterCreationException
        {
            throw new FilterCreationException("And not closes");
        }

    }

    /**
     * Create a query the reads all object fo the gieven ClassInfo
     *
     * @param aResultInfo
     * @return
     */
    public static DefaultClassInfoQuery readAll(ClassInfo aResultInfo)
    {
        return new DefaultClassInfoQuery(aResultInfo);
    }

    public static QueryCreator start(ClassInfo aResultInfo)
    {
        return new QueryCreator(aResultInfo);
    }

}
