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
 * SubLevel3Impl
 *
 * @author Copyright &copy;
 * @author Rainer Schneider
 * @version
 */
public class SubLevel3Impl extends de.jdynameta.testcommon.model.subclass.impl.SecondSubLevel2Impl

{
    private java.lang.String dataLevel3;

    /**
     * Get the dataLevel3
     *
     * @generated
     * @return get the dataLevel3
     */
    public String getDataLevel3()
    {
        return dataLevel3;
    }

    /**
     * set the dataLevel3
     *
     * @param aDataLevel3
     * @generated
     */
    public void setDataLevel3(String aDataLevel3)
    {
        dataLevel3 = (aDataLevel3 != null) ? aDataLevel3.trim() : null;
    }

}
