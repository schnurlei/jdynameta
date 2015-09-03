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

/**
 * @author Rainer
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Company
{
    /**
     * @return
     */
    public abstract String getCity();

    /**
     * @return
     */
    public abstract Integer getCompanyId();

    /**
     * @return
     */
    public abstract String getCompanyName();

    /**
     * @return
     */
    public abstract String getStreet();

    /**
     * @return
     */
    public abstract String getZip();

    /**
     * @param aString
     */
    public abstract void setCity(String aString);

    /**
     * @param aInteger
     */
    public abstract void setCompanyId(Integer aInteger);

    /**
     * @param aString
     */
    public abstract void setCompanyName(String aString);

    /**
     * @param aString
     */
    public abstract void setStreet(String aString);

    /**
     * @param aString
     */
    public abstract void setZip(String aString);
}
