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

import java.sql.SQLException;
import java.sql.SQLWarning;

import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public class UtilSql
{

    private static final UtilSql defaultUtil = new UtilSql();

    /**
     * UtilSql - Konstruktorkommentar.
     */
    public UtilSql()
    {
        super();
    }

    /**
     * 
     *
     * @author	Rainer Schneider
     * @param sqlExcept
     * @return 
     */
    public JdyPersistentException buildDbException(Exception sqlExcept)
    {

        String message = sqlExcept.getMessage() + '\n';
        message += sqlExcept.getLocalizedMessage() + '\n';

        return new JdyPersistentException(message);
    }

    /**
     * 
     *
     * @author	Rainer Schneider
     * @param sqlExcept
     * @return 
     */
    public JdyPersistentException buildDbException(Throwable sqlExcept)
    {

        String message = sqlExcept.getMessage() + '\n';
        message += sqlExcept.getLocalizedMessage() + '\n';
        message += sqlExcept.toString();
        sqlExcept.printStackTrace();

        return new JdyPersistentException(message);
    }

    /**
     * 
     * @author	Rainer Schneider
     * @param sqlExcept
     * @return 
     */
    public JdyPersistentException buildDbException(SQLException sqlExcept)
    {

        String message = sqlExcept.getMessage() + '\n';
        message += sqlExcept.getLocalizedMessage() + '\n';
        message += "Vendor Code " + sqlExcept.getErrorCode() + '\n';
        message += "SQL State " + sqlExcept.getSQLState() + '\n';

        return new JdyPersistentException(message);
    }

    /**
     * 
     * @author	Rainer Schneider
     * @return 
     */
    public static UtilSql getDefault()
    {
        return defaultUtil;
    }

    /**
     * !!! Needs COMMENT !!!
     *
     * @author	Rainer Schneider
     * @param aWarning java.sql.SQLWarning
     */
    public void printWarnings(SQLWarning aWarning)
    {

        if (aWarning != null)
        {
            System.out.println(
                    aWarning.getMessage()
                    + "\n"
                    + aWarning.getLocalizedMessage());
        }
    }
}
