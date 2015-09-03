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

import de.jdynameta.base.value.defaultimpl.ReflectionValueObject;
import de.jdynameta.testcommon.model.simple.Company;

/**
 * @author rsc
 */
public class ReferenceInPrimaryKeyImpl extends ReflectionValueObject
{
    private Company company;
    private String value1;

    /**
     * @return
     */
    public Company getCompany()
    {
        return company;
    }

    /**
     * @return
     */
    public String getValue1()
    {
        return value1;
    }

    /**
     * @param aValue
     */
    public void setCompany(Company aValue)
    {
        company = aValue;
    }

    /**
     * @param aValue
     */
    public void setValue1(String aValue)
    {
        value1 = aValue;
    }

}
