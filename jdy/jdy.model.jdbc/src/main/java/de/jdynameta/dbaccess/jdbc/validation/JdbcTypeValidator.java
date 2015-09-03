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
package de.jdynameta.dbaccess.jdbc.validation;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import de.jdynameta.dbaccess.jdbc.validation.JdbcSchemaValidator.JdbcColumnMetaInfo;

public class JdbcTypeValidator implements PrimitiveTypeVisitor
{

    private JdbcColumnMetaInfo jdbcColInfo;

    boolean matchesType;

    public void setJdbcColInfo(JdbcColumnMetaInfo jdbcColInfo)
    {
        this.jdbcColInfo = jdbcColInfo;
    }

    public boolean isMatchesType()
    {
        return matchesType;
    }

    @Override
    public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
    {
        if (aType.getMaxValue() < Integer.MAX_VALUE)
        {
            matchesType = jdbcColInfo.getTypeName().equals("INTEGER");
        } else
        {
            matchesType = jdbcColInfo.getTypeName().equals(("BIGINT"));
        }
    }

    @Override
    public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
    {
        matchesType = jdbcColInfo.getTypeName().equals("DECIMAL");
        //buffer.append("DECIMAL(18,"+ aType.getScale() +")");
    }

    @Override
    public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
    {
        matchesType = jdbcColInfo.getTypeName().equals("BOOLEAN"); //"BIT" for Access;");
    }

    @Override
    public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
    {
        matchesType = jdbcColInfo.getTypeName().equals("DOUBLE"); // DOUBLE for ACCESS
    }

    @Override
    public void handleValue(String aValue, TextType aType) throws JdyPersistentException
    {
        matchesType = jdbcColInfo.getTypeName().equals("CHARACTER");
        //buffer.append("CHAR(" +aType.getLength()+")"); 
    }

    @Override
    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
    {
        if (!aType.isClob())
        {
            matchesType = jdbcColInfo.getTypeName().equals("VARCHAR");
//			buffer.append("VARCHAR(" +aType.getLength()+")");  // "LONGCHAR"  for Access or Microsoft Jet-SQL
        } else
        {
            matchesType = jdbcColInfo.getTypeName().equals("LONGVARCHAR");
//			buffer.append("LONGVARCHAR");
        }
    }

    @Override
    public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
    {
        if (aType.isDatePartUsed() && !aType.isTimePartUsed())
        {
            matchesType = jdbcColInfo.getTypeName().equals("DATE");
        } else if (!aType.isDatePartUsed() && aType.isTimePartUsed())
        {
            matchesType = jdbcColInfo.getTypeName().equals("TIMESTAMP");
        } else
        {
            matchesType = jdbcColInfo.getTypeName().equals("TIMESTAMP");
        }
    }

    @Override
    public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
    {
        matchesType = jdbcColInfo.getTypeName().equals("BINARY");
    }
}
