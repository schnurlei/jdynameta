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
package de.jdynameta.base.metainfo.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;

public class DefaultClassRepositoryValidator implements JdyRepositoryListener, ClassRepositoryValidator
{

    public static String[] reservedWordsOracleSql = new String[]
    {
        "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT",
        "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT",
        "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM",
        "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS",
        "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "NUMBER",
        "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS",
        "SELECT", "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER",
        "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH"
    };

    public static String[] reservedWords = new String[]
    {
        "COUNT"
    };

    private final Set<String> reservedWordsColl;

    public DefaultClassRepositoryValidator()
    {
        this.reservedWordsColl = getReservedWords();
    }

    /**
     * Get all reserved Words as Uppercase Strings
     *
     * @return
     */
    protected Set<String> getReservedWords()
    {
        HashSet<String> reservedWordsTemp = new HashSet<>();
        for (String curString : reservedWordsTemp)
        {
            reservedWordsTemp.add(curString.toUpperCase());
        }
        for (String curString : reservedWordsOracleSql)
        {
            reservedWordsTemp.add(curString.toUpperCase());
        }

        return reservedWordsTemp;
    }

    /**
     * Check whether then given String is a reserved Word ignoring the case
     *
     * @param aStringToTest
     * @return
     */
    protected boolean isReservedWord(String aStringToTest)
    {
        return this.reservedWordsColl.contains(aStringToTest.toUpperCase());
    }

    /**
     * Make sure that the class breaks no validation rule
     *
     * @param aClassInfoToCheck
     * @param someExistingClassInfos
     * @throws InvalidClassInfoException
     */
    @Override
    public void validateClassInfo(ClassInfo aClassInfoToCheck, List<ClassInfo> someExistingClassInfos) throws InvalidClassInfoException
    {
        this.validateClassInfoNames(aClassInfoToCheck);
        this.validateAttributes(aClassInfoToCheck);
        this.validateChangedClassInfoAgainstExistingClasses(aClassInfoToCheck, someExistingClassInfos);
    }

    public void validateAttributes(ClassInfo aClassInfoToCheck) throws InvalidClassInfoException
    {

        final HashSet<String> internalNameSet = new HashSet<>(10);
        final HashSet<String> externalNameSet = new HashSet<>(10);

        for (AttributeInfo curAttrInfo : aClassInfoToCheck.getAttributeInfoIterator())
        {

            if (isNullOrEmpty(curAttrInfo.getInternalName()))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("Internal name is null of Attribute: " + curAttrInfo.getExternalName()));
            }
            if (isNullOrEmpty(curAttrInfo.getExternalName()))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("External name is null of Attribute: " + curAttrInfo.getInternalName()));
            }

            if (internalNameSet.contains(curAttrInfo.getInternalName()))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("Internal name is double of Attribute (Check also superclass attributes): " + curAttrInfo.getInternalName()));
            }
            if (externalNameSet.contains(curAttrInfo.getExternalName()))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("External name is double of Attribute (Check also superclass attributes): " + curAttrInfo.getExternalName()));
            }
            if (isReservedWord(curAttrInfo.getInternalName()))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("Internal name is a reserved word " + curAttrInfo.getInternalName()));
            }
            if (isReservedWord(curAttrInfo.getExternalName()))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("External name is a reserved word " + curAttrInfo.getExternalName()));
            }

            if (aClassInfoToCheck.getSuperclass() != null && curAttrInfo.isKey() && aClassInfoToCheck.isSubclassAttribute(curAttrInfo))
            {
                throw new InvalidClassInfoException(new InvalidClassInfoException("Subclass could not extend the key values " + curAttrInfo.getExternalName()));
            }

            internalNameSet.add(curAttrInfo.getInternalName());
            externalNameSet.add(curAttrInfo.getExternalName());
        }
    }

    /**
     * Validate that all names in the class info are set
     *
     * @param aClassInfoToAdd
     * @throws InvalidClassInfoException
     */
    protected void validateClassInfoNames(ClassInfo aClassInfoToAdd) throws InvalidClassInfoException
    {
        if (isNullOrEmpty(aClassInfoToAdd.getInternalName()))
        {
            throw new InvalidClassInfoException(InvalidClassInfoException.INTERNAL_NAME_NULL);
        }
        if (isNullOrEmpty(aClassInfoToAdd.getExternalName()))
        {
            throw new InvalidClassInfoException(InvalidClassInfoException.EXTERNAL_NAME_NULL);
        }
        if (isNullOrEmpty(aClassInfoToAdd.getShortName()))
        {
            throw new InvalidClassInfoException(InvalidClassInfoException.SHORT_NAME_NULL);
        }

        if (isReservedWord(aClassInfoToAdd.getInternalName()))
        {
            throw new InvalidClassInfoException(new InvalidClassInfoException("Class Internal name is a reserved word " + aClassInfoToAdd.getInternalName()));
        }
        if (isReservedWord(aClassInfoToAdd.getExternalName()))
        {
            throw new InvalidClassInfoException(new InvalidClassInfoException("Class External name is a reserved word " + aClassInfoToAdd.getExternalName()));
        }
        if (isReservedWord(aClassInfoToAdd.getShortName()))
        {
            throw new InvalidClassInfoException(new InvalidClassInfoException("Class short name is a reserved word " + aClassInfoToAdd.getShortName()));
        }

    }

    /**
     * Validate that no name in the given class exists in a already added class
     *
     * @param aChangedClass
     * @param someExistingClassInfos
     * @throws InvalidClassInfoException
     */
    protected void validateChangedClassInfoAgainstExistingClasses(ClassInfo aChangedClass, List<ClassInfo> someExistingClassInfos) throws InvalidClassInfoException
    {
        for (Iterator<ClassInfo> classInfoIter = someExistingClassInfos.iterator(); classInfoIter.hasNext();)
        {
            ClassInfo curInfo = classInfoIter.next();

            if (curInfo != aChangedClass)
            {
                if (!notNullAndDifferent(curInfo.getInternalName(), aChangedClass.getInternalName()))
                {
                    throw new InvalidClassInfoException(InvalidClassInfoException.INTERNAL_NAME_DOUBLE, curInfo.getInternalName());
                }
                if (!notNullAndDifferent(curInfo.getExternalName(), aChangedClass.getExternalName()))
                {
                    throw new InvalidClassInfoException(InvalidClassInfoException.EXTERNAL_NAME_DOUBLE, curInfo.getExternalName());
                }
                if (!notNullAndDifferent(curInfo.getShortName(), aChangedClass.getShortName()))
                {
                    throw new InvalidClassInfoException(InvalidClassInfoException.SHORT_NAME_DOUBLE, curInfo.getShortName());
                }
            }
        }

    }

    protected boolean notNullAndDifferent(String aFirstString, String aSecondString)
    {
        return aFirstString != null && aSecondString != null
                && aFirstString.length() > 0 && aSecondString.length() > 0
                && !aFirstString.equals(aSecondString);
    }

    protected boolean isNullOrEmpty(String aString)
    {
        return aString == null || aString.length() == 0;
    }

}
