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
package de.jdynameta.dbaccess.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;
import de.jdynameta.testcommon.util.sql.SqlUtil;

public class JdbcDbAcessUtil
{

    public static Map getKeyMapFor(ClassInfo aClassInfo, ValueObject aValue) throws JdyPersistentException
    {

        final Map<String, Object> keyMap = new HashMap<>();

        AttributeHandler handler = new AttributeHandler()
        {
            private final ArrayList<AttributeInfo> attributeStack = new ArrayList<>();

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
            {
                if (aInfo.isKey())
                {
                    attributeStack.add(aInfo);
                    aInfo.getReferencedClass().handleAttributes(this, objToHandle);
                    attributeStack.remove(aInfo);
                }
            }

            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
            {
                if (aInfo.isKey())
                {
                    attributeStack.add(aInfo);
                    String columnName = getDefaultColumnNameFor(attributeStack);
                    if (aInfo.getType().getJavaType().equals(String.class))
                    {
                        keyMap.put(columnName, "'" + objToHandle + "'");
                    } else
                    {
                        keyMap.put(columnName, objToHandle);
                    }
                    attributeStack.remove(aInfo);
                }
            }
        };

        aClassInfo.handleAttributes(handler, aValue);

        return keyMap;
    }

    public static String getDefaultColumnNameFor(List anAttributePath)
    {
        assert (anAttributePath != null && anAttributePath.size() > 0);
        String columnName = "";

        for (Iterator attrIter = anAttributePath.iterator(); attrIter.hasNext();)
        {
            AttributeInfo curAttrInfo = (AttributeInfo) attrIter.next();

            if (columnName.length() == 0)
            {
                columnName = curAttrInfo.getExternalName();
            } else if (curAttrInfo instanceof ObjectReferenceAttributeInfo)
            {
                columnName += "_" + ((ObjectReferenceAttributeInfo) curAttrInfo).getReferencedClass().getShortName();
            } else
            {
                columnName += "_" + curAttrInfo.getExternalName();
            }
        }

        return columnName;
    }

    public static boolean isValueSet(Connection aBaseConnection, ClassInfo aClassInfo, PrimitiveAttributeInfo aPrimitivAttr, ValueObject aValue) throws JdyPersistentException, SQLException
    {
        boolean hasColumnValue = true;

        Map keyMap = JdbcDbAcessUtil.getKeyMapFor(aClassInfo, aValue);
        Object valueFromDb = new SqlUtil(aBaseConnection).getObjectValueForColumn(aClassInfo.getExternalName(), aPrimitivAttr.getExternalName(), keyMap);
        if (valueFromDb instanceof String)
        {
            valueFromDb = ((String) valueFromDb).trim();
        }
        Object compareValue = aValue.getValue(aPrimitivAttr);
        hasColumnValue = compareValue.equals(valueFromDb);

        return hasColumnValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws InvalidClassInfoException
     *
     * @throws SQLException DOCUMENT ME!
     */
    public static void initializeTables(Connection aBaseConnection, SqlTableCreator aTableCreator) throws Exception
    {
        for (ClassInfo curInfo : new ComplexTestDataMetaInfoRepository().getAllClassInfosIter())
        {
            try
            {
                aTableCreator.deleteTableForClassInfo(curInfo);
            } catch (JdyPersistentException excp)
            {
                //excp.printStackTrace();
            }
            aTableCreator.buildTableForClassInfo(curInfo);
        }
    }

}
