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
package de.jdynameta.testcommon.model.subclass.impl;

import java.util.Date;

/**
 * ReferencedObjectImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class ReferencedObjectImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
    private java.lang.Long refKey1In;
    private java.lang.String refKey2In;
    private java.util.Date refData;

    /**
     * Get the refKey1In
     *
     * @generated
     * @return get the refKey1In
     */
    public Long getRefKey1In()
    {
        return refKey1In;
    }

    /**
     * set the refKey1In
     *
     * @param aRefKey1In
     * @generated
     */
    public void setRefKey1In(Long aRefKey1In)
    {
        refKey1In = aRefKey1In;
    }

    /**
     * Get the refKey2In
     *
     * @generated
     * @return get the refKey2In
     */
    public String getRefKey2In()
    {
        return refKey2In;
    }

    /**
     * set the refKey2In
     *
     * @param aRefKey2In
     * @generated
     */
    public void setRefKey2In(String aRefKey2In)
    {
        refKey2In = (aRefKey2In != null) ? aRefKey2In.trim() : null;
    }

    /**
     * Get the refData
     *
     * @generated
     * @return get the refData
     */
    public Date getRefData()
    {
        return refData;
    }

    /**
     * set the refData
     *
     * @param aRefData
     * @generated
     */
    public void setRefData(Date aRefData)
    {
        refData = aRefData;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        ReferencedObjectImpl typeObj = (ReferencedObjectImpl) compareObj;
        return typeObj != null
                && (((getRefKey1In() != null
                && typeObj.getRefKey1In() != null
                && this.getRefKey1In().equals(typeObj.getRefKey1In()))
                && (getRefKey2In() != null
                && typeObj.getRefKey2In() != null
                && this.getRefKey2In().equals(typeObj.getRefKey2In())))
                || (getRefKey1In() == null
                && typeObj.getRefKey1In() == null
                && getRefKey2In() == null
                && typeObj.getRefKey2In() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((refKey1In != null) ? refKey1In.hashCode() : super.hashCode())
                ^ ((refKey2In != null) ? refKey2In.hashCode() : super.hashCode());
    }

}
