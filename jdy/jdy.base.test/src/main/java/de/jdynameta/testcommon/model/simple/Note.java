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
public class Note extends ReflectionValueObject
{
    private Company company;
    private Contact contact;
    private Double noteValue;

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
    public Contact getContact()
    {
        return contact;
    }

    /**
     * @return
     */
    public Double getNoteValue()
    {
        return noteValue;
    }

    /**
     * @param aCompany
     */
    public void setCompany(Company aCompany)
    {
        company = aCompany;
    }

    /**
     * @param aContact
     */
    public void setContact(Contact aContact)
    {
        contact = aContact;
    }

    /**
     * @param aDouble
     */
    public void setNoteValue(Double aDouble)
    {
        noteValue = aDouble;
    }

}
