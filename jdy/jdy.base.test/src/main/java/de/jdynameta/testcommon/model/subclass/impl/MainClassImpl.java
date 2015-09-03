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

/**
 * MainClassImpl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class MainClassImpl extends de.jdynameta.base.value.defaultimpl.ReflectionValueObject

{
    private ReferencedObjectImpl keyMain1;
    private java.lang.String keyMain2;
    private java.lang.String mainData;

    /**
     * Get the keyMain1
     *
     * @generated
     * @return get the keyMain1
     */
    public ReferencedObjectImpl getKeyMain1()
    {
        return keyMain1;
    }

    /**
     * set the keyMain1
     *
     * @param aKeyMain1
     * @generated
     */
    public void setKeyMain1(ReferencedObjectImpl aKeyMain1)
    {
        keyMain1 = aKeyMain1;
    }

    /**
     * Get the keyMain2
     *
     * @generated
     * @return get the keyMain2
     */
    public String getKeyMain2()
    {
        return keyMain2;
    }

    /**
     * set the keyMain2
     *
     * @param aKeyMain2
     * @generated
     */
    public void setKeyMain2(String aKeyMain2)
    {
        keyMain2 = (aKeyMain2 != null) ? aKeyMain2.trim() : null;
    }

    /**
     * Get the mainData
     *
     * @generated
     * @return get the mainData
     */
    public String getMainData()
    {
        return mainData;
    }

    /**
     * set the mainData
     *
     * @param aMainData
     * @generated
     */
    public void setMainData(String aMainData)
    {
        mainData = (aMainData != null) ? aMainData.trim() : null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object
     */
    @Override
    public boolean equals(Object compareObj)
    {
        MainClassImpl typeObj = (MainClassImpl) compareObj;
        return typeObj != null
                && (((getKeyMain1() != null
                && typeObj.getKeyMain1() != null
                && typeObj.getKeyMain1().equals(typeObj.getKeyMain1()))
                && (getKeyMain2() != null
                && typeObj.getKeyMain2() != null
                && this.getKeyMain2().equals(typeObj.getKeyMain2())))
                || (getKeyMain1() == null
                && typeObj.getKeyMain1() == null
                && getKeyMain2() == null
                && typeObj.getKeyMain2() == null
                && this == typeObj));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        return ((keyMain1 != null) ? keyMain1.hashCode() : super.hashCode())
                ^ ((keyMain2 != null) ? keyMain2.hashCode() : super.hashCode());
    }

}
