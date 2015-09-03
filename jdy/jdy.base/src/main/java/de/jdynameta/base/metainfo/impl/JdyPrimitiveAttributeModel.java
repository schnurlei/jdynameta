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
package de.jdynameta.base.metainfo.impl;

import java.io.Serializable;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.PrimitiveAttributeHandler;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.value.JdyPersistentException;

@SuppressWarnings("serial")
public class JdyPrimitiveAttributeModel extends JdyAbstractAttributeModel implements PrimitiveAttributeInfo, Serializable
{

    private final PrimitiveType type;

    /**
     * Constructor for PrimitiveAttributeInfo.
     *
     * @param aType
     * @param internalName
     * @param isKeyFlag
     * @param aExternalName
     * @param isNotNullFlag
     */
    public JdyPrimitiveAttributeModel(PrimitiveType aType, String internalName, String aExternalName, boolean isKeyFlag, boolean isNotNullFlag)
    {
        super(internalName, aExternalName, isKeyFlag, isNotNullFlag);
        this.type = aType;
    }

    @Override
    public Class<? extends Object> getJavaTyp()
    {

        return type.getJavaType();
    }

    /**
     *
     * @return
     */
    @Override
    public String getJavaTypName()
    {

        String result = "";

        if (type.getJavaType().isPrimitive())
        {

            result += type.getJavaType().getName();
        } else
        {

            result = type.getJavaType().getName();
            result = result.substring(result.lastIndexOf('.') + 1);
        }

        return result;
    }

    /**
     * @param aHandler
     * @param objToHandle
     * @throws de.jdynameta.base.value.JdyPersistentException
     * @see
     * de.comafra.model.metainfo.AbstractAttributeInfo#handlePrimitiveValues(PrimitiveAttributeHandler)
     */
    public void handlePrimitiveValues(PrimitiveAttributeHandler aHandler, Object objToHandle) throws JdyPersistentException
    {
        aHandler.handlePrimitiveAttribute(this, objToHandle);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.AttributeInfo#handleAttribute(de.comafra.model.metainfo.AttributeHandler, java.lang.Object)
     */

    /**
     *
     * @param aHandler
     * @param aValue
     * @throws JdyPersistentException
     */
    
    @Override
    public void handleAttribute(AttributeHandler aHandler, Object aValue)
            throws JdyPersistentException
    {
        aHandler.handlePrimitiveAttribute(this, aValue);
    }

    /**
     * Returns the type.
     *
     * @return PrimitiveType
     */
    @Override
    public PrimitiveType getType()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "[JdyPrimitiveAttributeModel] " + getInternalName();
    }

    /**
     *
     * @param value1
     * @param value2
     * @return
     */
    @Override
    public int compareObjects(Object value1, Object value2)
    {
        int result;
        if (value1 instanceof Comparable)
        {
            result = ((Comparable) value1).compareTo(value2);
        } else
        {
            result = value1.toString().compareTo(value2.toString());
        }

        return result;
    }

}
