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
import java.util.Iterator;

import de.jdynameta.base.metainfo.filter.ExpressionVisitor;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.OrExpression;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Filter Expression which defines the Comparision from a Attribute with a Value
 *
 * @author	Rainer Schneider
 */
@SuppressWarnings("serial")
public class DefaultOrExpression implements OrExpression
{

    private final ArrayList<ObjectFilterExpression> expressionVect;

    /**
     * ValueExpression - Konstruktorkommentar.
     *
     * @param aExprVect
     */
    public DefaultOrExpression(ArrayList<ObjectFilterExpression> aExprVect)
    {
        super();
        assert (aExprVect.size() > 1);
        expressionVect = aExprVect;
    }

    /**
     *
     * @return
     */
    @Override
    public Iterator<ObjectFilterExpression> getExpressionIterator()
    {
        return expressionVect.iterator();
    }

    @Override
    public void visit(ExpressionVisitor aVisitor) throws JdyPersistentException
    {
        aVisitor.visitOrExpression(this);
    }

    @Override
    public boolean matchesObject(ValueObject aModel) throws JdyPersistentException
    {
        //@todo Test
        boolean matches = false;
        for (Iterator<ObjectFilterExpression> exprEnum = expressionVect.iterator(); !matches && exprEnum.hasNext();)
        {
            matches = ((ObjectFilterExpression) exprEnum.next()).matchesObject(aModel);
        }

        return matches;
    }

    /**
     * Convenience Method to create And Expression with 2 Subexpressions
     *
     * @param expr1
     * @param expr2
     * @return
     */
    public static DefaultOrExpression createOrExpr(ObjectFilterExpression expr1, ObjectFilterExpression expr2)
    {
        ArrayList<ObjectFilterExpression> andExprColl = new ArrayList<>();
        andExprColl.add(expr1);
        andExprColl.add(expr2);
        return new DefaultOrExpression(andExprColl);
    }
}
