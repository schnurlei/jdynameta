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

import java.io.Serializable;

import de.jdynameta.base.metainfo.AttributeInfo;

/**
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractAttributeInfo implements AttributeInfo, Serializable
{

    private final boolean isKey;

    private boolean isGenerated;

    private final boolean isNotNull;

	//private int defaultSortOrderPos;
    /**
     * Constructor for AbstractAttributeInfo.
     *
     * @param isKeyFlag
     * @param isNotNullFlag
     */
    public AbstractAttributeInfo(boolean isKeyFlag, boolean isNotNullFlag)
    {
        super();
        this.isKey = isKeyFlag;
        this.isNotNull = isNotNullFlag;
    }

    @Override
    public boolean isGenerated()
    {
        return isGenerated;
    }

    public void setGenerated(boolean aIsGenerated)
    {
        this.isGenerated = aIsGenerated;
    }

    /**
     * Returns the isKey.
     *
     * @return boolean
     */
    @Override
    public boolean isKey()
    {
        return isKey;
    }

    /**
     * Returns the isNotNull.
     *
     * @return boolean
     */
    @Override
    public boolean isNotNull()
    {
        return isNotNull;
    }

}
