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
import java.util.ArrayList;
import java.util.List;

import de.jdynameta.base.metainfo.PrimitiveAttributeHandler;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.view.DbDomainValue;

/**
 * @author Rainer
 *
 * @version 24.07.2002
 */
@SuppressWarnings("serial")
public class JdyTextType extends JdyPrimitiveType implements TextType, Serializable
{

    private int length = 40;
    private TypeHint typeHint;
    private final List<DbDomainValue<String>> domainValues;

    /**
     * Constructor for BooleanType.
     *
     */
    public JdyTextType()
    {
        super(String.class);
        this.domainValues = new ArrayList<>();
    }

    public JdyTextType(int aLength)
    {
        super(String.class);

        this.length = aLength;
        this.domainValues = new ArrayList<>();
    }

    public JdyTextType(int aLength, TypeHint aTypeHint)
    {
        super(String.class);

        this.length = aLength;
        this.typeHint = aTypeHint;
        this.domainValues = new ArrayList<>();
    }

    public JdyTextType(int aLength, TypeHint aTypeHint, List<DbDomainValue<String>> allDomainValues)
    {
        super(String.class);

        this.length = aLength;
        this.typeHint = aTypeHint;
        this.domainValues = allDomainValues;
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
    public TypeHint getTypeHint()
    {
        return typeHint;
    }

    public void setTypeHint(TypeHint aTypeHint)
    {
        this.typeHint = aTypeHint;
    }

    @Override
    public List<DbDomainValue<String>> getDomainValues()
    {
        return domainValues;
    }
}
