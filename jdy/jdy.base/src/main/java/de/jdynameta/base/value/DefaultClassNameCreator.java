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
package de.jdynameta.base.value;

import de.jdynameta.base.metainfo.ClassInfo;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class DefaultClassNameCreator extends DefaultAttributeNameCreator
        implements ClassNameCreator
{
    /**
     *
     */
    public DefaultClassNameCreator()
    {
        super();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ClassNameCreator#getClassNameFor(de.comafra.model.metainfo.ClassInfo)
     */
    @Override
    public String getClassNameFor(ClassInfo aInfo)
    {
        return aInfo.getInternalName() + "Impl";
    }
    /* (non-Javadoc)
     * @see de.comafra.model.value.ClassNameCreator#getPackageNameFor(de.comafra.model.metainfo.ClassInfo)
     */

    @Override
    public String getPackageNameFor(ClassInfo aInfo)
    {
        return aInfo.getRepoName() + ".impl";
    }
    /* (non-Javadoc)
     * @see de.comafra.model.value.ClassNameCreator#getAbsolutClassNameFor(de.comafra.model.metainfo.ClassInfo)
     */

    @Override
    public String getAbsolutClassNameFor(ClassInfo aInfo)
    {
        return getPackageNameFor(aInfo) + "." + getClassNameFor(aInfo);
    }

    @Override
    public String getReferenceClassNameFor(ClassInfo aInfo)
    {
        return getClassNameFor(aInfo);
    }

}
