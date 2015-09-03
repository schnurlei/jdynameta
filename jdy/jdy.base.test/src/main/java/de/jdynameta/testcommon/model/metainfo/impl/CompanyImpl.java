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
package de.jdynameta.testcommon.model.metainfo.impl;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.defaultimpl.ReflectionValueObject;
import de.jdynameta.testcommon.model.simple.Company;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;

/**
 * @author rsc
 */
public class CompanyImpl extends ReflectionValueObject implements Company, TypedValueObject
{
    private Integer companyId;
    private String companyName;
    private String zip;
    private String city;
    private String street;

    /**
     * @return
     */
    @Override
    public String getCity()
    {
        return city;
    }

    /**
     * @return
     */
    public Integer getCompanyIdValue()
    {
        return companyId;
    }

    /**
     * @return
     */
    @Override
    public Integer getCompanyId()
    {
        return companyId;
    }

    /**
     * @return
     */
    @Override
    public String getCompanyName()
    {
        return companyName;
    }

    /**
     * @return
     */
    @Override
    public String getStreet()
    {
        return street;
    }

    /**
     * @return
     */
    @Override
    public String getZip()
    {
        return zip;
    }

    /**
     * @param aString
     */
    @Override
    public void setCity(String aString)
    {
        city = aString;
    }

    /**
     * @param aInteger
     */
    @Override
    public void setCompanyId(Integer aInteger)
    {
        companyId = aInteger;
    }

    /**
     * @param aString
     */
    @Override
    public void setCompanyName(String aString)
    {
        companyName = aString;
    }

    /**
     * @param aString
     */
    @Override
    public void setStreet(String aString)
    {
        street = aString;
    }

    /**
     * @param aString
     */
    @Override
    public void setZip(String aString)
    {
        zip = aString;
    }

    @Override
    public ClassInfo getClassInfo()
    {
        return SimpleMetaInfoRepository.getSingleton().getCompanyClassInfo();
    }

}
