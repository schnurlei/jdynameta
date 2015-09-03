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
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ClassNameCreator extends AttributeNameCreator
{
    /**
     * Get the Class name for the ClassInfo (without package name)
     *
     * @param aInfo
     * @return
     */
    public String getClassNameFor(ClassInfo aInfo);

    /**
     * Get the package name for the given ClassInfo
     *
     * @param aInfo
     * @return
     */
    public String getPackageNameFor(ClassInfo aInfo);

    /**
     * Create the Name of the class concatenated with the package name for
     * excample 'de.comafra.model.metainfo.ClassInfo'
     *
     * @param aInfo
     * @return
     */
    public String getAbsolutClassNameFor(ClassInfo aInfo);

    /**
     * Get the Class name a referenced class (without package name)
     *
     * @param aInfo
     * @return
     */
    public String getReferenceClassNameFor(ClassInfo aInfo);

}
