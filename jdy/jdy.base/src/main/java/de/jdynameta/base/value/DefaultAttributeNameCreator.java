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
package de.jdynameta.base.value;

import java.io.Serializable;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;

@SuppressWarnings("serial")
public class DefaultAttributeNameCreator implements AttributeNameCreator, Serializable
{
    @Override
    public String getGetterNameFor(AttributeInfo aInfo)
    {
        return "get" + stringWithFirstLetterUppercase(aInfo.getInternalName());
    }

    @Override
    public String getSetterNameFor(AttributeInfo aInfo)
    {
        return "set" + stringWithFirstLetterUppercase(aInfo.getInternalName());
    }

    @Override
    public String getGetterNameFor(AssociationInfo aInfo)
    {
        return "get" + stringWithFirstLetterUppercase(aInfo.getNameResource()) + "Coll";
    }

    @Override
    public String getSetterNameFor(AssociationInfo aInfo)
    {
        return "set" + stringWithFirstLetterUppercase(aInfo.getNameResource()) + "Coll";
    }

    /**
     * Create a copy of the givven String with the first letter uppercase
     *
     * @param textToAppend
     * @return parameter String with first Letter Uppercase
     */
    public static String stringWithFirstLetterUppercase(String textToAppend)
    {
        return "" + Character.toUpperCase(textToAppend.charAt(0)) + textToAppend.substring(1);
    }

    /**
     * Create a copy of the givven String with the first letter uppercase
     *
     * @param textToAppend
     * @return parameter String with first Letter Uppercase
     */
    public String stringWithFirstLetterLowerCase(String textToAppend)
    {
        return "" + Character.toLowerCase(textToAppend.charAt(0)) + textToAppend.substring(1);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.AttributeNameCreator#getMethodName(de.comafra.model.metainfo.AttributeInfo)
     */
    @Override
    public String getInstanceVariableName(AttributeInfo aInfo)
    {
        return stringWithFirstLetterLowerCase(aInfo.getInternalName());
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.AttributeNameCreator#getParameterName(de.comafra.model.metainfo.AttributeInfo)
     */
    @Override
    public String getParameterName(AttributeInfo aInfo)
    {
        return "a" + stringWithFirstLetterUppercase(aInfo.getInternalName());
    }

}
