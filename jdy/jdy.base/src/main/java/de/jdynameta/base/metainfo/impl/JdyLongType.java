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
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.view.DbDomainValue;

/**
 * @author Rainer
 *
 * @version 24.07.2002
 */
@SuppressWarnings("serial")
public class JdyLongType extends JdyPrimitiveType implements LongType, Serializable
{

    private final long minValue;
    private final long maxValue;
    private final List<DbDomainValue<Long>> domainValues;

    /**
     * Constructor for BooleanType.
     *
     * @param aJavaType
     */
    public JdyLongType()
    {
        super(Long.class);
        minValue = (long) Integer.MIN_VALUE;
        maxValue = (long) Integer.MAX_VALUE;
        this.domainValues = new ArrayList<>();
    }

    /**
     * Constructor for BooleanType.
     *
     * @param aMinValue
     * @param aMaxValue
     */
    public JdyLongType(Long aMinValue, Long aMaxValue)
    {
        super(Long.class);
        this.minValue = (aMinValue == null) ? Integer.MIN_VALUE : aMinValue;
        this.maxValue = (aMaxValue == null) ? Integer.MAX_VALUE : aMaxValue;
        this.domainValues = new ArrayList<>();
    }

    public JdyLongType(Long aMinValue, Long aMaxValue,
            List<DbDomainValue<Long>> aDomainValueList)
    {
        super(Long.class);
        this.minValue = (aMinValue == null) ? Integer.MIN_VALUE : aMinValue;
        this.maxValue = (aMaxValue == null) ? Integer.MAX_VALUE : aMaxValue;
        this.domainValues = aDomainValueList;
    }

    /**
     * @param objToHandle
     * @see
     * de.jdynameta.base.metainfo.PrimitiveType#handlePrimitiveKey(PrimitiveAttributeHandler)
     */
    @Override
    public void handlePrimitiveKey(PrimitiveTypeVisitor aHandler, Object objToHandle) throws JdyPersistentException
    {
        aHandler.handleValue((objToHandle == null) ? null : ((Number) objToHandle).longValue(), this);
    }

    @Override
    public Object handlePrimitiveKey(PrimitiveTypeGetVisitor aHandler) throws JdyPersistentException
    {
        return aHandler.handleValue(this);
    }

    @Override
    public long getMinValue()
    {
        return minValue;
    }

    @Override
    public long getMaxValue()
    {
        return maxValue;
    }

    @Override
    public List<DbDomainValue<Long>> getDomainValues()
    {
        return domainValues;
    }
}
