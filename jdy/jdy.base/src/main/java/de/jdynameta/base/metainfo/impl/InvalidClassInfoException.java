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

/**
 * @author * <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 *
 */
public class InvalidClassInfoException extends RuntimeException
{

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Two different Class Infos have the same internal name
     */
    public static final int INTERNAL_NAME_DOUBLE = 1;
    /**
     * Two different Class Infos have the same external name
     */
    public static final int EXTERNAL_NAME_DOUBLE = 2;
    /**
     * Two different Class Infos have the same short name
     */
    public static final int SHORT_NAME_DOUBLE = 3;
    /**
     * Two different Class Infos have the same internal name
     */
    public static final int INTERNAL_NAME_NULL = 4;
    /**
     * Two different Class Infos have the same external name
     */
    public static final int EXTERNAL_NAME_NULL = 5;
    /**
     * Two different Class Infos have the same short name
     */
    public static final int SHORT_NAME_NULL = 6;

    /**
     * @param aCause
     */
    public InvalidClassInfoException(int aCause)
    {
        super(getMessageForCause(aCause));
    }

    protected static String getMessageForCause(int aCause)
    {
        String message;

        switch (aCause)
        {
            case INTERNAL_NAME_DOUBLE:
                message = "Another ClassInfo has the internalName already assigned";
                break;
            case EXTERNAL_NAME_DOUBLE:
                message = "Another ClassInfo has the externalName already assigned";
                break;
            case SHORT_NAME_DOUBLE:
                message = "Another ClassInfo has the shortName already assigned";
                break;
            case INTERNAL_NAME_NULL:
                message = "internalName is null";
                break;
            case EXTERNAL_NAME_NULL:
                message = "externalName is null";
                break;
            case SHORT_NAME_NULL:
                message = "ShortName is null";
                break;

            default:
                message = "Unknown Cause";
                break;
        }

        return message;
    }

    /**
     * @param aMessage
     */
    public InvalidClassInfoException(String aMessage)
    {
        super(aMessage);
    }

    /**
     * @param aCause
     */
    public InvalidClassInfoException(Throwable aCause)
    {
        super(aCause);
    }

    /**
     * @param aMessage
     * @param aCause
     */
    public InvalidClassInfoException(String aMessage, Throwable aCause)
    {
        super(aMessage, aCause);
    }

    public InvalidClassInfoException(int shortNameDouble, String shortName)
    {
        this(shortNameDouble);
    }

}
