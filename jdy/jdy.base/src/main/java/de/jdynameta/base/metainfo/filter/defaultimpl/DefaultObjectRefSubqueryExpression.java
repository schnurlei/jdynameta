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
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.filter.ExpressionVisitor;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.ObjectReferenceSubqueryExpression;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Filter Expression which defines the Comparision from a Attribute with a Value
 *
 * @author	Rainer Schneider
 */
@SuppressWarnings("serial")
public class DefaultObjectRefSubqueryExpression implements ObjectReferenceSubqueryExpression
{

    private final ObjectReferenceAttributeInfo refInfo;
    private final ObjectFilterExpression filterExpr;

    /**
     * ValueExpression - Konstruktorkommentar.
     *
     * @param aRefInfo
     * @param aFilterExpr
     */
    public DefaultObjectRefSubqueryExpression(ObjectReferenceAttributeInfo aRefInfo, ObjectFilterExpression aFilterExpr)
    {
        super();
        assert (aRefInfo != null && aFilterExpr != null);
        refInfo = aRefInfo;
        filterExpr = aFilterExpr;
    }

    @Override
    public void visit(ExpressionVisitor aVisitor) throws JdyPersistentException
    {
        aVisitor.visitReferenceQueryExpr(this);
    }

    @Override
    public boolean matchesObject(ValueObject aModel) throws JdyPersistentException
    {
        boolean match = false;
        if (aModel != null)
        {
            ValueObject valueObject = (ValueObject) aModel.getValue(this.refInfo);
            match = filterExpr.matchesObject(valueObject);
        }

        return match;
    }

    @Override
    public ObjectFilterExpression getFilterExpression()
    {
        return filterExpr;
    }

    @Override
    public ObjectReferenceAttributeInfo getAttributeInfo()
    {
        return refInfo;
    }

    public static DefaultObjectRefSubqueryExpression createObjSubqueryExpr(String exAttrName, ObjectFilterExpression aFilterExpr, ClassInfo aClassinfo)
    {
        return new DefaultObjectRefSubqueryExpression(
                (ObjectReferenceAttributeInfo) aClassinfo.getAttributeInfoForExternalName(exAttrName), aFilterExpr);
    }

}
