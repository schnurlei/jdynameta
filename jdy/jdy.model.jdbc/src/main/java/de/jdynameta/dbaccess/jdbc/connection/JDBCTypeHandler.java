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
package de.jdynameta.dbaccess.jdbc.connection;

import java.math.BigDecimal;
import java.util.Date;

import de.jdynameta.base.creation.db.SqlTableCreator.BufferedPrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public class JDBCTypeHandler implements BufferedPrimitiveTypeVisitor
{

    private StringBuffer buffer;

    @Override
    public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
    {
        if (aType.getMaxValue() < Integer.MAX_VALUE)
        {
            buffer.append("INTEGER");

        } else
        {
            buffer.append("BIGINT");
        }
    }

    @Override
    public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
    {
        long length = Math.max(aType.getMaxValue().toString().length(), aType.getMinValue().toString().length());
        buffer.append("DECIMAL(").append(length).append(",").append(aType.getScale()).append(")");
    }

    @Override
    public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
    {
        buffer.append("BOOLEAN"); //"BIT" for Access;");
    }

    @Override
    public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
    {
        buffer.append("DOUBLE"); // DOUBLE for ACCESS
    }

    @Override
    public void handleValue(String aValue, TextType aType) throws JdyPersistentException
    {
        buffer.append("CHAR(").append(aType.getLength()).append(")");
    }

    @Override
    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
    {
        if (!aType.isClob())
        {
            buffer.append("VARCHAR(").append(aType.getLength()).append(")");  // "LONGCHAR"  for Access or Microsoft Jet-SQL
        } else
        {
            buffer.append("LONGVARCHAR");
        }
    }

    @Override
    public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
    {
        if (aType.isDatePartUsed() && !aType.isTimePartUsed())
        {
            getBuffer().append("DATE");
        } else if (!aType.isDatePartUsed() && aType.isTimePartUsed())
        {
            buffer.append("TIMESTAMP");
        } else
        {
            buffer.append("TIMESTAMP");
        }
    }

    @Override
    public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
    {
        buffer.append("BLOB(4000)");
    }

    /**
     * Returns the buffer.
     *
     * @return StringBuffer
     */
    public StringBuffer getBuffer()
    {
        return buffer;
    }

    @Override
    public void setBuffer(StringBuffer aBuffer)
    {
        this.buffer = aBuffer;
    }

}
