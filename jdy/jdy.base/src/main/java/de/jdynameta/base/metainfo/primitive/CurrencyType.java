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
package de.jdynameta.base.metainfo.primitive;

import java.math.BigDecimal;
import java.util.List;

import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.view.DbDomainValue;

/**
 * @author Rainer Schneider
 */
public interface CurrencyType extends PrimitiveType
{

    public static BigDecimal MIN_VALUE = new BigDecimal("-9999999999999.999999");
    public static BigDecimal MAX_VALUE = new BigDecimal("9999999999999.999999");

    /**
     * Returns the scale.
     *
     * @return int
     */
    public long getScale();

    public BigDecimal getMinValue();

    public BigDecimal getMaxValue();

    public List<DbDomainValue<BigDecimal>> getDomainValues();

}
