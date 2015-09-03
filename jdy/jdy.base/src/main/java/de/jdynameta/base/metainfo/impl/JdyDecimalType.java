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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.jdynameta.base.metainfo.PrimitiveAttributeHandler;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
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
public class JdyDecimalType extends JdyPrimitiveType implements CurrencyType, Serializable
{

    private int scale = 3;
    private final BigDecimal minValue;
    private final BigDecimal maxValue;
    private final List<DbDomainValue<BigDecimal>> domainValues;

    /**
     *
     */
    public JdyDecimalType()
    {
        super(BigDecimal.class);
        minValue = CurrencyType.MIN_VALUE.setScale(scale, BigDecimal.ROUND_DOWN);
        maxValue = CurrencyType.MAX_VALUE.setScale(scale, BigDecimal.ROUND_DOWN);
        this.domainValues = new ArrayList<>();
    }

    public JdyDecimalType(BigDecimal aMinValue, BigDecimal aMaxValue, int aScale)
    {
        super(BigDecimal.class);
        this.scale = aScale;
        this.minValue = aMinValue.setScale(aScale, BigDecimal.ROUND_DOWN);
        this.maxValue = aMaxValue.setScale(aScale, BigDecimal.ROUND_DOWN);
        this.domainValues = new ArrayList<>();
    }

    public JdyDecimalType(BigDecimal aMinValue, BigDecimal aMaxValue,
            int aScale, List<DbDomainValue<BigDecimal>> aDomainValueList)
    {
        super(BigDecimal.class);
        this.scale = aScale;
        this.minValue = aMinValue.setScale(aScale, BigDecimal.ROUND_DOWN);
        this.maxValue = aMaxValue.setScale(aScale, BigDecimal.ROUND_DOWN);
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
        aHandler.handleValue((BigDecimal) objToHandle, this);
    }

    /**
     * Returns the scale.
     *
     * @return int
     */
    @Override
    public long getScale()
    {
        return scale;
    }

    @Override
    public BigDecimal getMinValue()
    {
        return minValue;
    }

    @Override
    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    @Override
    public Object handlePrimitiveKey(PrimitiveTypeGetVisitor aHandler) throws JdyPersistentException
    {
        return aHandler.handleValue(this);
    }

    @Override
    public List<DbDomainValue<BigDecimal>> getDomainValues()
    {
        return domainValues;
    }
}
