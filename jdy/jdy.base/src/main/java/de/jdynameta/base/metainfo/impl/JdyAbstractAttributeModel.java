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
import java.util.ArrayList;
import java.util.List;

import de.jdynameta.base.metainfo.AttributeInfo;

/**
 *
 */
@SuppressWarnings("serial")
public abstract class JdyAbstractAttributeModel implements AttributeInfo, Serializable
{

    private final String internalName;
    private String externalName;

    private boolean isKey;

    private boolean isGenerated;

    private boolean isNotNull;

    //private int defaultSortOrderPos;
    private final transient List<AttributeInfoListener> allListener;
    private String attrGroup;

    /**
     * Constructor for AbstractAttributeInfo.
     * @param anInternalName
     * @param anExternalName
     * @param isKeyFlag
     * @param isNotNullFlag
     */
    public JdyAbstractAttributeModel(String anInternalName, String anExternalName, boolean isKeyFlag, boolean isNotNullFlag)
    {
        super();
        this.internalName = anInternalName;
        this.externalName = anExternalName;
        this.isKey = isKeyFlag;
        this.isNotNull = isNotNullFlag;
        this.allListener = new ArrayList<>();
    }

    /**
     * @return 
     * @see de.comafra.model.metainfo.AbstractAttributeInfo#getExternalName()
     */
    @Override
    public String getExternalName()
    {
        return externalName;
    }

    public JdyAbstractAttributeModel setExternalName(String externalName)
    {
        this.externalName = externalName;
        fireValueChanged();
        return this;
    }

    @Override
    public String getAttrGroup()
    {
        return attrGroup;
    }

    public void setGroup(String aGroup)
    {
        this.attrGroup = aGroup;
    }

    /**
     * @return 
     * @see de.comafra.model.metainfo.AbstractAttributeInfo#getName()
     */
    @Override
    public String getInternalName()
    {
        return internalName;
    }

    @Override
    public boolean isGenerated()
    {
        return isGenerated;
    }

    public JdyAbstractAttributeModel setGenerated(boolean aIsGenerated)
    {
        this.isGenerated = aIsGenerated;
        fireValueChanged();
        return this;
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

    public JdyAbstractAttributeModel setIsKey(boolean aNewValue)
    {
        this.isKey = aNewValue;
        this.isNotNull = true;
        fireValueChanged();
        return this;
    }

    public JdyAbstractAttributeModel setNotNull(boolean aNewValue)
    {
        this.isNotNull = aNewValue;
        fireValueChanged();
        return this;
    }

    protected void fireValueChanged()
    {
        this.allListener.stream().forEach((curListener) ->
        {
            curListener.valueChanged(this);
        });
    }

    public void addListener(AttributeInfoListener aListener2Add)
    {
        this.allListener.add(aListener2Add);
    }

    /**
     * Listener to changes in the attribute info
     *
     * @author rs
     *
     */
    public static interface AttributeInfoListener extends Serializable
    {

        public void valueChanged(JdyAbstractAttributeModel changedAttribute);

    }

}
