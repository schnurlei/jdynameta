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
package de.jdynameta.base.metainfo.impl;

import java.util.ArrayList;
import java.util.List;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;

/**
 * @author rs
 * @version 13.06.2002
 */
public class DefaultClassRepository implements ClassRepository
{

    private final List<ClassInfo> allClassInfos;
    private final ClassRepositoryValidator classValidator;
    private final String repoName;

    public DefaultClassRepository(String aRepoName)
    {
        this.repoName = aRepoName;
        this.allClassInfos = new ArrayList<>();
        this.classValidator = new DefaultClassRepositoryValidator();
    }

    @Override
    public String getRepoName()
    {
        return repoName;
    }

    public void addClassInfo(ClassInfo aInfo) throws InvalidClassInfoException
    {
        validateClassInfo(aInfo);
        allClassInfos.add(aInfo);
    }

    /**
     * Gets the allClassInfos.
     *
     * @return Returns a List
     */
    @Override
    public Iterable<? extends ClassInfo> getAllClassInfosIter()
    {
        return allClassInfos;
    }

    /**
     * Make sure that the class breaks no validation rule
     *
     * @param aClassInfoToAdd
     * @throws InvalidClassInfoException
     */
    protected void validateClassInfo(ClassInfo aClassInfoToAdd) throws InvalidClassInfoException
    {
        this.classValidator.validateClassInfo(aClassInfoToAdd, this.allClassInfos);
    }

    @Override
    public ClassInfo getClassForName(String internalClassName)
    {
        ClassInfo resultClass = null;

        for (ClassInfo type : this.getAllClassInfosIter())
        {
            if (type.getInternalName().equals(internalClassName))
            {
                resultClass = type;
                break;
            }
        }

        return resultClass;
    }
}
