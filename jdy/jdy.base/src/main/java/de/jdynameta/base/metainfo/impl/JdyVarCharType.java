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
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 24.07.2002
 */
@SuppressWarnings("serial")
public class JdyVarCharType extends JdyPrimitiveType implements VarCharType, Serializable
{

    private int length;
    private boolean isClob;
    private TextMimeType mimeType;

    /**
     * Constructor for DefaultVarCharType.
     *
     */
    public JdyVarCharType()
    {
        this(50);
    }

    /**
     * Constructor for DefaultVarCharType.
     *
     * @param aLength
     */
    public JdyVarCharType(int aLength)
    {
        this(aLength, false);
    }

    /**
     * Constructor for DefaultVarCharType.
     *
     * @param aLength
     * @param aIsClobFlag
     */
    public JdyVarCharType(int aLength, boolean aIsClobFlag)
    {
        super(String.class);
        this.length = aLength;
        this.isClob = aIsClobFlag;
    }

    public JdyVarCharType(int aLength, boolean aIsClobFlag, TextMimeType aMimeType)
    {
        super(String.class);
        this.length = aLength;
        this.isClob = aIsClobFlag;
        this.mimeType = aMimeType;
    }

    @Override
    public boolean isClob()
    {
        return isClob;
    }

    /**
     * @param objToHandle
     * @see
     * de.jdynameta.base.metainfo.PrimitiveType#handlePrimitiveKey(PrimitiveAttributeHandler)
     */
    @Override
    public void handlePrimitiveKey(PrimitiveTypeVisitor aHandler, Object objToHandle) throws JdyPersistentException
    {
        aHandler.handleValue((String) objToHandle, this);
    }

    /**
     * Returns the length.
     *
     * @return int
     */
    @Override
    public long getLength()
    {
        return length;
    }

    @Override
    public Object handlePrimitiveKey(PrimitiveTypeGetVisitor aHandler) throws JdyPersistentException
    {
        return aHandler.handleValue(this);
    }

    @Override
    public TextMimeType getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(TextMimeType aMimeType)
    {
        this.mimeType = aMimeType;
    }

}
