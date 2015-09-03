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

import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.value.JdyPersistentException;

/**
 *
 */
@SuppressWarnings("serial")
public abstract class JdyPrimitiveType implements PrimitiveType, Serializable
{

    private final Class<? extends Object> javaType;

    public JdyPrimitiveType(Class<? extends Object> aJavaType)
    {
        javaType = aJavaType;
    }

    /**
     * Gets the javaType.
     *
     * @return Returns a Class
     */
    @Override
    public Class<? extends Object> getJavaType()
    {
        return javaType;
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
        return value1.toString().compareTo(value2.toString());
    }

    /**
     * @see
     * de.comafra.model.metainfo.AbstractAttributeInfo#handlePrimitiveKeys(PrimitiveAttributeHandler)
     */
    @Override
    public abstract void handlePrimitiveKey(PrimitiveTypeVisitor aHandler, Object aValue) throws JdyPersistentException;

    /**
     *
     * @param aHandler
     * @return
     * @throws JdyPersistentException
     */
    @Override
    public abstract Object handlePrimitiveKey(PrimitiveTypeGetVisitor aHandler) throws JdyPersistentException;

}
