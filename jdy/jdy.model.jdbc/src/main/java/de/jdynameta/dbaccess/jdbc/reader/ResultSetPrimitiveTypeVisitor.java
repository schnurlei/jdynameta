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
package de.jdynameta.dbaccess.jdbc.reader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public class ResultSetPrimitiveTypeVisitor implements PrimitiveTypeGetVisitor
{

    private final ResultSet resultSet;
    private String index;

    /**
     * Constructor for PreparedStatementWriter.
     * @param aResultSet
     * @param aStartIndex
     */
    public ResultSetPrimitiveTypeVisitor(ResultSet aResultSet, String aStartIndex)
    {
        super();
        this.resultSet = aResultSet;
        this.index = aStartIndex;
    }

    @Override
    public Long handleValue(LongType aType) throws JdyPersistentException
    {

        Long result = null;
        try
        {
            result = this.resultSet.getLong(index);
            if (resultSet.wasNull())
            {
                result = null;
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    @Override
    public BigDecimal handleValue(CurrencyType aType) throws JdyPersistentException
    {

        BigDecimal result = null;
        try
        {
            result = this.resultSet.getBigDecimal(index);
            if (resultSet.wasNull())
            {
                result = null;
            } else
            {
                result = result.setScale((int) aType.getScale(), RoundingMode.DOWN);
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    @Override
    public Boolean handleValue(BooleanType aType) throws JdyPersistentException
    {

        Boolean result = null;
        try
        {
            result = this.resultSet.getBoolean(index);
            if (resultSet.wasNull())
            {
                result = null;
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    @Override
    public Double handleValue(FloatType aType) throws JdyPersistentException
    {

        Double result = null;
        try
        {
            result = this.resultSet.getDouble(index);
            if (resultSet.wasNull())
            {
                result = null;
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    @Override
    public String handleValue(VarCharType aType) throws JdyPersistentException
    {

        String result = null;
        if (!aType.isClob())
        {
            try
            {
                result = this.resultSet.getString(index);
                if (resultSet.wasNull())
                {
                    result = null;
                }
            } catch (SQLException ex)
            {
                throw new JdyPersistentException(ex);
            }
        } else
        {
            try
            {
                String retrBytes = this.resultSet.getString(index);

                if (resultSet.wasNull())
                {
                    result = null;
                } else
                {
                    result = retrBytes;
                }
            } catch (SQLException ex)
            {
                throw new JdyPersistentException(ex);
            }
        }

        return result;
    }

    @Override
    public java.util.Date handleValue(TimeStampType aType) throws JdyPersistentException
    {

        java.util.Date result = null;
        try
        {
            if (aType.isDatePartUsed() && !aType.isTimePartUsed())
            {
                result = this.resultSet.getDate(index);
            } else if (!aType.isDatePartUsed() && aType.isTimePartUsed())
            {
                result = this.resultSet.getTime(index);
            } else
            {
                result = this.resultSet.getTimestamp(index);
            }
            if (resultSet.wasNull())
            {
                result = null;
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    @Override
    public String handleValue(TextType aType) throws JdyPersistentException
    {

        String result = null;
        try
        {
            result = this.resultSet.getString(index);
            if (resultSet.wasNull())
            {
                result = null;
            }
            if (result != null)
            {
                result = removeTrailingWhiteSpace(result);
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    public String removeTrailingWhiteSpace(String source)
    {
        if (source.length() == 0 || source.charAt(source.length() - 1) != ' ')
        {
            return source;
        } else
        {
            int len = source.length();
            while ((0 < len) && (source.charAt(len - 1) == ' '))
            {
                len--;
            }
            return source.substring(0, len);

        }
    }

    @Override
    public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
    {
        BlobByteArrayHolder result = null;
        try
        {
            byte[] retrBytes = this.resultSet.getBytes(index);

            if (resultSet.wasNull())
            {
                result = null;
            } else
            {
                result = new BlobByteArrayHolder(retrBytes);
            }
        } catch (SQLException ex)
        {
            throw new JdyPersistentException(ex);
        }

        return result;
    }

    protected ResultSet getResultSet()
    {
        return resultSet;
    }

    /**
     * Returns the index.
     *
     * @return int
     */
    public String getIndex()
    {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param aIndex
     */
    public void setIndex(String aIndex)
    {
        this.index = aIndex;
    }

}
