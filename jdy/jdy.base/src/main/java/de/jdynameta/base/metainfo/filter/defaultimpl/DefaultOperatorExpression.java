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

import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ExpressionPrimitiveOperator;
import de.jdynameta.base.metainfo.filter.ExpressionVisitor;
import de.jdynameta.base.metainfo.filter.OperatorExpression;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Filter Expression which defines the Comparision from a Attribute with a Value
 *
 * @author	Rainer Schneider
 */
@SuppressWarnings("serial")
public class DefaultOperatorExpression implements OperatorExpression
{

    private ExpressionPrimitiveOperator myOperator;
    private PrimitiveAttributeInfo attributeInfo;
    private Object compareValue;

    /**
     * ValueExpression - Konstruktorkommentar.
     */
    public DefaultOperatorExpression()
    {
        super();
        this.myOperator = new DefaultOperatorEqual();
    }

    /**
     * Get the Attribute Info for the Atribute to compare
     *
     * @author	Rainer Schneider
     * @return int
     */
    @Override
    public PrimitiveAttributeInfo getAttributeInfo()
    {
        return attributeInfo;
    }

    /**
     * Get the Operator of the Expression
     *
     * @author	Rainer Schneider
     * @return
     */
    @Override
    public ExpressionPrimitiveOperator getOperator()
    {
        return myOperator;
    }

    @Override
    public boolean matchesObject(ValueObject aModel) throws JdyPersistentException
    {
        Object modelValue = aModel.getValue(attributeInfo);
        return myOperator.compareValues(modelValue, this.compareValue, this.attributeInfo);
    }

    @Override
    public void visit(ExpressionVisitor aVisitor) throws JdyPersistentException
    {
        aVisitor.visitOperatorExpression(this);
    }

    /**
     * Returns the compareValue.
     *
     * @return Object
     */
    @Override
    public Object getCompareValue()
    {
        return compareValue;
    }

    /**
     * @param aInfo
     */
    public void setAttributeInfo(PrimitiveAttributeInfo aInfo)
    {
        attributeInfo = aInfo;
    }

    /**
     * @param aObject
     */
    public void setCompareValue(Object aObject)
    {
        compareValue = aObject;
    }

    /**
     * @param aOperator
     */
    public void setMyOperator(ExpressionPrimitiveOperator aOperator)
    {
        myOperator = aOperator;
    }

}
