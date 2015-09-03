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
import de.jdynameta.base.metainfo.filter.OperatorEqual;
import de.jdynameta.base.metainfo.filter.OperatorVisitor;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * Defines a Operator for the comparison of Equality
 *
 * @author	Rainer Schneider
 */
public class DefaultOperatorEqual implements OperatorEqual
{

    private static final DefaultOperatorEqual EQUAL_INSTANCE = new DefaultOperatorEqual(false);
    private static final DefaultOperatorEqual NOT_EQUAL_INSTANCE = new DefaultOperatorEqual(true);

    private boolean isNotEqual;

    /**
     * EqualOperator - Konstruktorkommentar.
     */
    public DefaultOperatorEqual()
    {
        this(false);
    }

    /**
     * EqualOperator - Konstruktorkommentar.
     *
     * @param isNotEqualFlag
     */
    public DefaultOperatorEqual(boolean isNotEqualFlag)
    {
        super();
        this.isNotEqual = isNotEqualFlag;
    }

    @Override
    public boolean isNotEqual()
    {
        return isNotEqual;
    }

    @Override
    public void visitOperatorHandler(OperatorVisitor aVisitor)
    {
        aVisitor.visitOperatorEqual(this);
    }

    @Override
    public boolean compareValues(Object value1, Object value2, PrimitiveAttributeInfo attributeInfo) throws JdyPersistentException
    {
        boolean result = false;
        if (value1 != null && value2 != null)
        {
            result = attributeInfo.compareObjects(value1, value2) == 0;
            if (isNotEqual)
            {
                result = !result;
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return (isNotEqual) ? "<>" : "=";
    }

    public static DefaultOperatorExpression createEqualExpr(String exAttrName, Object aCompareValue, ClassInfo aClassinfo)
    {
        DefaultOperatorExpression opExpr = new DefaultOperatorExpression();
        opExpr.setAttributeInfo((PrimitiveAttributeInfo) aClassinfo.getAttributeInfoForExternalName(exAttrName));
        opExpr.setCompareValue(aCompareValue);
        opExpr.setMyOperator(EQUAL_INSTANCE);
        return opExpr;
    }

    public static DefaultOperatorEqual getEqualInstance()
    {
        return EQUAL_INSTANCE;
    }

    public static DefaultOperatorEqual getNotEqualInstance()
    {
        return NOT_EQUAL_INSTANCE;
    }

}
