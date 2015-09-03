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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public class PreparedStatementWriter implements PrimitiveTypeVisitor
{

    private final PreparedStatement preparedStatement;
    protected int index;

    /**
     * Constructor for PreparedStatementWriter.
     * @param aPreparedStatement
     * @param aStartIndex
     */
    public PreparedStatementWriter(PreparedStatement aPreparedStatement, int aStartIndex)
    {
        super();
        this.preparedStatement = aPreparedStatement;
        this.index = aStartIndex;
    }

    @Override
    public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
    {
        try
        {
            if (aValue != null)
            {
                if (aValue.intValue() < Integer.MAX_VALUE)
                {
                    preparedStatement.setInt(index, aValue.intValue());
                } else
                {
                    preparedStatement.setLong(index, aValue);
                }
            } else
            {
                preparedStatement.setNull(index, Types.INTEGER);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
    {

        try
        {
            if (aValue != null)
            {
                preparedStatement.setBigDecimal(index, aValue);
            } else
            {
                preparedStatement.setNull(index, Types.NUMERIC);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
    {

        try
        {
            if (aValue != null)
            {
                preparedStatement.setBoolean(index, aValue);
            } else
            {
                preparedStatement.setNull(index, Types.BOOLEAN);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
    {

        try
        {
            if (aValue != null)
            {
                preparedStatement.setDouble(index, aValue);
            } else
            {
                preparedStatement.setNull(index, Types.DOUBLE);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }

    }

    @Override
    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
    {
        if (aType.isClob())
        {

            try
            {
                if (aValue != null)
                {

                    byte[] bytesToInsert = aValue.getBytes();
                    ByteArrayInputStream bIn = new ByteArrayInputStream(bytesToInsert);
                    preparedStatement.setBinaryStream(index, bIn, bytesToInsert.length);
                } else
                {
                    preparedStatement.setNull(index, Types.CLOB);
                }
                index++;
            } catch (SQLException excp)
            {
                throw new JdyPersistentException(excp);
            }
        } else
        {
            try
            {
                if (aValue != null)
                {
                    if (aValue.length() > 0)
                    {
                        StringBuilder resultBuffer = new StringBuilder(aValue.length() + aValue.length() / 20);
                        try
                        {
                            // replace \n with \r\n
                            BufferedReader bufReader = new BufferedReader(new StringReader(aValue));
                            String curLine = bufReader.readLine();
                            while (curLine != null)
                            {
                                resultBuffer.append(curLine);
                                curLine = bufReader.readLine();
                                if (curLine != null || aValue.endsWith("\n"))
                                {
                                    resultBuffer.append("\r\n");
                                }
                            }
                        } catch (IOException ioExcp)
                        {
                            ioExcp.printStackTrace();
                        }
                        aValue = resultBuffer.toString();
                        preparedStatement.setCharacterStream(index, new StringReader(aValue));
                    } else
                    // bugfix for Access setAsciiStream don't work with ""
                    {
                        preparedStatement.setString(index, aValue);
                    }

                } else
                {
                    preparedStatement.setNull(index, Types.VARCHAR);
                }
                index++;
            } catch (SQLException excp)
            {
                throw new JdyPersistentException(excp);
            }
        }
    }

    @Override
    public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
    {

        try
        {
            if (aValue != null)
            {
                if (aType.isDatePartUsed() && !aType.isTimePartUsed())
                {
                    preparedStatement.setDate(index, new java.sql.Date(aValue.getTime()));
                } else if (!aType.isDatePartUsed() && aType.isTimePartUsed())
                {
                    preparedStatement.setTimestamp(index, new java.sql.Timestamp(aValue.getTime()));
                } else
                {
                    preparedStatement.setTimestamp(index, new java.sql.Timestamp(aValue.getTime()));
                }
            } else
            {
                if (aType.isDatePartUsed() && !aType.isTimePartUsed())
                {
                    preparedStatement.setNull(index, Types.DATE);
                } else if (!aType.isDatePartUsed() && aType.isTimePartUsed())
                {
                    preparedStatement.setNull(index, Types.TIME);
                } else
                {
                    preparedStatement.setNull(index, Types.TIMESTAMP);
                }
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public void handleValue(String aValue, TextType aType) throws JdyPersistentException
    {

        try
        {
            if (aValue != null)
            {
                preparedStatement.setString(index, aValue.trim());
                //	preparedStatement.setString(index, aOperator.prepareSqlValue(aValue.trim()));
            } else
            {
                preparedStatement.setNull(index, Types.CHAR);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    @Override
    public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
    {
        try
        {
            if (aValue != null)
            {

                byte[] bytesToInsert = aValue.getBytes();
                ByteArrayInputStream bIn = new ByteArrayInputStream(bytesToInsert);
                preparedStatement.setBinaryStream(index, bIn, bytesToInsert.length);
            } else
            {
                preparedStatement.setNull(index, Types.BLOB);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

    /**
     * Returns the index.
     *
     * @return int
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Sets the index.
     *
     * @param aIndex
     */
    public void setIndex(int aIndex)
    {
        this.index = aIndex;
    }

    protected final PreparedStatement getPreparedStatement()
    {
        return this.preparedStatement;
    }

}
