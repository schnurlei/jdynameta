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
package de.jdynameta.print.csv;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

public final class CsvTypeVisitor implements PrimitiveTypeVisitor
{
    private String value = "";

    private void setValue(String value)
    {
        this.value = value;
    }

    public void resetValue()
    {
        this.value = "";
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            setValue(aValue.toString());
        }
    }

    @Override
    public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            setValue(aValue.toString());
        }
    }

    @Override
    public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            setValue(aValue.toString());
        }
    }

    @Override
    public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            if (!aType.isTimePartUsed())
            {
                dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            } else if (!aType.isDatePartUsed())
            {
                dateFormat = new SimpleDateFormat("HH:mm:ss");
            }
            setValue(dateFormat.format(aValue));
        }
    }

    @Override
    public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            setValue(aValue.toString());
        }
    }

    @Override
    public void handleValue(String aValue, TextType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            setValue(aValue);
        }
    }

    @Override
    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
    {
        if (aValue != null)
        {
            setValue(aValue);
        }
    }

    @Override
    public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
    {
    }
}
