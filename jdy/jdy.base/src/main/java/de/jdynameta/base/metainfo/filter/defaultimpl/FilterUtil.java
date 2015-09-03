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

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

public class FilterUtil
{

    public static DefaultClassInfoQuery createSearchEqualObjectFilter(ClassInfo aClassInfo, final ValueObject aValueModel) throws JdyPersistentException
    {
        final ArrayList<ObjectFilterExpression> keyValuesExprList = new ArrayList<>();
        AttributeHandler aHandler = new AttributeHandler()
        {

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
            {
                if (aInfo.isKey())
                {
                    DefaultObjectReferenceEqualExpression expr = new DefaultObjectReferenceEqualExpression(aInfo, objToHandle);
                    keyValuesExprList.add(expr);
                }
            }

            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
            {
                if (aInfo.isKey())
                {
                    DefaultOperatorExpression expr = new DefaultOperatorExpression();
                    expr.setAttributeInfo(aInfo);
                    expr.setCompareValue(aValueModel.getValue(aInfo));
                    keyValuesExprList.add(expr);
                }
            }
        };

        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {
            if (curAttr.isKey())
            {
                if (aValueModel == null)
                {
                    curAttr.handleAttribute(aHandler, null);
                } else
                {
                    curAttr.handleAttribute(aHandler, aValueModel.getValue(curAttr));
                }
            }
        }

        ObjectFilterExpression aExpression;

        if (keyValuesExprList.size() > 1)
        {
            aExpression = new DefaultExpressionAnd(keyValuesExprList);

        } else
        {
            aExpression = keyValuesExprList.get(0);
        }

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(aClassInfo);
        filter.setFilterExpression(aExpression);
        return filter;
    }

}
