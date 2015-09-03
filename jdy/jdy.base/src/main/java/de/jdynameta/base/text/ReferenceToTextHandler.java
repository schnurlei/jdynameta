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
package de.jdynameta.base.text;

import java.io.Serializable;
import java.util.List;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Converts a Object reference to a text value Per default the key values of the
 * Reference are displayed
 *
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class ReferenceToTextHandler implements AttributeHandler, Serializable
{
    private final String seperatorReferences = "";
    private final String primitiveSeperator = " - ";
    private final StringBuffer text;
    private final List<AttributeInfo> attrList;
    private final PrimitiveToTextVisitor primitiveVisitor;

    public ReferenceToTextHandler(List<AttributeInfo> attrList)
    {
        text = new StringBuffer();
        this.attrList = attrList;
        this.primitiveVisitor = new PrimitiveToTextVisitor();
    }

    public PrimitiveToTextVisitor getPrimitiveVisitor()
    {
        return primitiveVisitor;
    }

    public void clear()
    {
        text.setLength(0);
    }

    public String getResultText()
    {
        return text.toString();
    }

    /**
     *
     * @param aInfo
     * @param objToHandle
     * @throws JdyPersistentException
     */
    @Override
    public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
    {
        if (aInfo.isKey())
        {
            text.append(seperatorReferences);
            aInfo.getReferencedClass().handleAttributes(this, objToHandle);
            text.append(seperatorReferences);
        }
    }

    @Override
    public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
    {
        if (objToHandle != null)
        {
            if ((attrList == null && aInfo.isKey()) || (attrList != null && attrList.contains(aInfo)))
            {
                this.primitiveVisitor.reset();
                aInfo.getType().handlePrimitiveKey(this.primitiveVisitor, objToHandle);
                if (text.length() > 0)
                {
                    text.append(primitiveSeperator);
                }
                text.append(this.primitiveVisitor.getValue());
            }
        }
    }
}
