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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.OperatorLess;
import de.jdynameta.base.metainfo.filter.OperatorVisitor;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * Defines a Operator for the comparison of Equality
 *
 * @author	Rainer Schneider
 */
public class DefaultOperatorLess implements OperatorLess
{

    private static final DefaultOperatorLess LESS_INSTANCE = new DefaultOperatorLess(false);
    private static final DefaultOperatorLess LESS_EQUAL_INSTANCE = new DefaultOperatorLess(true);

    private boolean isAlsoEqual;

    /**
     * EqualOperator - Konstruktorkommentar.
     */
    public DefaultOperatorLess()
    {
        this(false);
    }

    /**
     * EqualOperator - Konstruktorkommentar.
     *
     * @param isAlsoEqualFlag
     */
    public DefaultOperatorLess(boolean isAlsoEqualFlag)
    {
        super();
        this.isAlsoEqual = isAlsoEqualFlag;
    }

    @Override
    public boolean isAlsoEqual()
    {
        return isAlsoEqual;
    }

    @Override
    public void visitOperatorHandler(OperatorVisitor aVisitor)
    {
        aVisitor.visitOperatorLess(this);
    }

    @Override
    public boolean compareValues(Object value1, Object value2, PrimitiveAttributeInfo attributeInfo) throws JdyPersistentException
    {
        // @TODO : Test
        boolean result = false;
        if (value1 != null && value2 != null)
        {
            result = attributeInfo.compareObjects(value1, value2) < 0;
            if (isAlsoEqual)
            {
                result = result && attributeInfo.compareObjects(value1, value2) == 0;
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return (isAlsoEqual) ? "<=" : "<";
    }

    public static DefaultOperatorExpression createLessExpr(String exAttrName, Object aCompareValue, ClassInfo aClassinfo)
    {
        DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
        opExpr.setAttributeInfo((PrimitiveAttributeInfo) aClassinfo.getAttributeInfoForExternalName(exAttrName));
        opExpr.setCompareValue(aCompareValue);
        opExpr.setMyOperator(getLessInstance());
        return opExpr;
    }

    public static DefaultOperatorLess getLessInstance()
    {
        return LESS_INSTANCE;
    }

    public static DefaultOperatorLess getLessOrEqualInstance()
    {
        return LESS_EQUAL_INSTANCE;
    }

}
