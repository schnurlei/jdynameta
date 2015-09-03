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
import java.io.IOException;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public class HsqlPreparedStatementWriter extends PreparedStatementWriter
{

    public HsqlPreparedStatementWriter(PreparedStatement aPreparedStatement, int aStartIndex)
    {
        super(aPreparedStatement, aStartIndex);
    }

    @Override
    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
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
                    getPreparedStatement().setCharacterStream(index, new StringReader(aValue), aValue.length());
                } else
                // bugfix for Access setAsciiStream don't work with ""
                {
                    getPreparedStatement().setString(index, aValue);
                }

            } else
            {
                getPreparedStatement().setNull(index, Types.VARCHAR);
            }
            index++;
        } catch (SQLException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

}
