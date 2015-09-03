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
import de.jdynameta.base.metainfo.filter.OperatorGreater;
import de.jdynameta.base.metainfo.filter.OperatorVisitor;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * Defines a Operator for the comparison of Equality
 *
 * @author	Rainer Schneider
 */
public class DefaultOperatorGreater implements OperatorGreater
{

    private static final DefaultOperatorGreater GREATER_INSTANCE = new DefaultOperatorGreater(false);
    private static final DefaultOperatorGreater GREATER_EQUAL_INSTANCE = new DefaultOperatorGreater(true);

    private boolean isAlsoEqual;

    /**
     * EqualOperator - Konstruktorkommentar.
     */
    public DefaultOperatorGreater()
    {
        this(false);
    }

    /**
     * EqualOperator - Konstruktorkommentar.
     *
     * @param isAlsoEqualFlag
     */
    public DefaultOperatorGreater(boolean isAlsoEqualFlag)
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
        aVisitor.visitOperatorGreater(this);
    }

    @Override
    public boolean compareValues(Object value1, Object value2, PrimitiveAttributeInfo attributeInfo) throws JdyPersistentException
    {
        // @TODO : Test
        boolean result = false;
        if (value1 != null && value2 != null)
        {
            result = attributeInfo.compareObjects(value1, value2) > 0;
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
        return (isAlsoEqual) ? ">=" : ">";
    }

    public static DefaultOperatorExpression createGreaterExpr(String exAttrName, Object aCompareValue, ClassInfo aClassinfo)
    {
        DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
        opExpr.setAttributeInfo((PrimitiveAttributeInfo) aClassinfo.getAttributeInfoForExternalName(exAttrName));
        opExpr.setCompareValue(aCompareValue);
        opExpr.setMyOperator(getGreateInstance());
        return opExpr;
    }

    public static DefaultOperatorGreater getGreateInstance()
    {
        return GREATER_INSTANCE;
    }

    public static DefaultOperatorGreater getGreaterOrEqualInstance()
    {
        return GREATER_EQUAL_INSTANCE;
    }

}
