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

import java.util.Date;

import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;

public class MqSqlTypeHandler extends JDBCTypeHandler
{

    @Override
    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
    {
        if (!aType.isClob())
        {
            getBuffer().append("VARCHAR(").append(aType.getLength()).append(")");  // "LONGCHAR"  for Access or Microsoft Jet-SQL
        } else
        {
            getBuffer().append("BLOB");
        }
    }

    @Override
    public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
    {
        getBuffer().append("Bool"); //"BIT" for Access;");
    }

    @Override
    public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
    {
        if (aType.isDatePartUsed() && !aType.isTimePartUsed())
        {
            getBuffer().append("DATE");
        } else if (!aType.isDatePartUsed() && aType.isTimePartUsed())
        {
            getBuffer().append("DATETIME");
        } else
        {
            getBuffer().append("DATETIME");
        }
    }

    @Override
    public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
    {
        getBuffer().append("BLOB");
    }

}
