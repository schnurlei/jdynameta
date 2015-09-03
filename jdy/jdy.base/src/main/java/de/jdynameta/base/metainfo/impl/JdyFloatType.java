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

import de.jdynameta.base.metainfo.PrimitiveAttributeHandler;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 24.07.2002
 */
@SuppressWarnings("serial")
public class JdyFloatType extends JdyPrimitiveType implements FloatType, Serializable
{

    /**
     * Constructor for BooleanType.
     */
    public JdyFloatType()
    {
        super(Double.class);
    }

    /**
     * @param objToHandle
     * @see
     * de.jdynameta.base.metainfo.PrimitiveType#handlePrimitiveKey(PrimitiveAttributeHandler)
     */
    @Override
    public void handlePrimitiveKey(PrimitiveTypeVisitor aHandler, Object objToHandle) throws JdyPersistentException
    {
        aHandler.handleValue((Double) objToHandle, this);
    }

    @Override
    public Object handlePrimitiveKey(PrimitiveTypeGetVisitor aHandler) throws JdyPersistentException
    {
        return aHandler.handleValue(this);
    }

}
