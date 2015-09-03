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
import de.jdynameta.base.metainfo.filter.ObjectReferenceEqualExpression;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * @author Rainer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("serial")
public class DefaultObjectReferenceEqualExpression
        implements ObjectReferenceEqualExpression
{

    private final ValueObject compareValue;
    private final ObjectReferenceAttributeInfo attributeInfo;
    private final boolean isNotEqual;

    public DefaultObjectReferenceEqualExpression(ObjectReferenceAttributeInfo anAttrInfo, ValueObject aCompareValue)
    {
        super();
        assert (anAttrInfo != null);
        this.attributeInfo = anAttrInfo;
        this.compareValue = aCompareValue;
        this.isNotEqual = false;
    }

    public DefaultObjectReferenceEqualExpression(ObjectReferenceAttributeInfo anAttrInfo, ValueObject aCompareValue, boolean aNotEqual)
    {
        super();
        assert (anAttrInfo != null);
        this.attributeInfo = anAttrInfo;
        this.compareValue = aCompareValue;
        this.isNotEqual = aNotEqual;
    }

    @Override
    public boolean isNotEqual()
    {
        return isNotEqual;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.filter.ObjectReferenceEqualExpression#getAttributeInfo()
     */
    @Override
    public ObjectReferenceAttributeInfo getAttributeInfo()
    {
        return this.attributeInfo;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.filter.ObjectReferenceEqualExpression#getCompareValue()
     */
    @Override
    public ValueObject getCompareValue()
    {
        return this.compareValue;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.filter.ObjectFilterExpression#matchesObject(de.comafra.model.value.ValueObject)
     */
    @Override
    public boolean matchesObject(ValueObject aModel) throws JdyPersistentException
    {
        Object modelValue = aModel.getValue(attributeInfo);
        return isNotEqual ? !compareValue.equals(modelValue) : compareValue.equals(modelValue);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.filter.ObjectFilterExpression#visit(de.comafra.model.metainfo.filter.ExpressionVisitor)
     */
    @Override
    public void visit(ExpressionVisitor aModel) throws JdyPersistentException
    {
        aModel.visitReferenceEqualExpression(this);
    }

    public static DefaultObjectReferenceEqualExpression createEqualExpr(String exAttrName, ValueObject aCompareValue, ClassInfo aClassinfo)
    {
        return new DefaultObjectReferenceEqualExpression(
                (ObjectReferenceAttributeInfo) aClassinfo.getAttributeInfoForExternalName(exAttrName), aCompareValue);
    }

}
