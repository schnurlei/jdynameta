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
package de.jdynameta.testcommon.model.simple;

import de.jdynameta.base.value.defaultimpl.ReflectionValueObject;

/**
 * @author rsc
 */
public class Employee extends ReflectionValueObject
{
    private Integer employeeId;
    private String employeeName;
    private Company company;

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
    public Integer getEmployeeIdValue()
    {
        return employeeId;
    }

    /**
     * @return
     */
    public Integer getEmployeeId()
    {
        return employeeId;
    }

    /**
     * @return
     */
    public String getEmployeeName()
    {
        return employeeName;
    }

    /**
     * @param aCompany
     */
    public void setCompany(Company aCompany)
    {
        company = aCompany;
    }

    /**
     * @param aInteger
     */
    public void setEmployeeId(Integer aInteger)
    {
        employeeId = aInteger;
    }

    /**
     * @param aString
     */
    public void setEmployeeName(String aString)
    {
        employeeName = aString;
    }

}
