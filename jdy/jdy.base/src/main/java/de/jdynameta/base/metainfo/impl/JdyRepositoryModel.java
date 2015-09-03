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
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel.ClassInfoListener;

/**
 * @author rs
 * @version 13.06.2002
 */
public class JdyRepositoryModel implements ClassRepository
{

    private final List<ClassInfo> allClassInfos;
    private final List<JdyRepositoryListener> allListener;
    private final ClassInfoListener classInfoListener;
    private final String repoName;

    public JdyRepositoryModel(String aRepoName)
    {
        this.repoName = aRepoName;
        this.allClassInfos = new ArrayList<>();
        this.allListener = new ArrayList<>();
        this.classInfoListener = this::validateClassInfo;
    }

    @Override
    public String getRepoName()
    {
        return repoName;
    }

    public JdyClassInfoModel addClassInfo(JdyClassInfoModel aInfo) throws InvalidClassInfoException
    {
        validateClassInfo(aInfo);
        allClassInfos.add(aInfo);
        aInfo.addListener(classInfoListener);
        return aInfo;
    }

    public JdyClassInfoModel addClassInfo(String aInternalName) throws InvalidClassInfoException
    {
        JdyClassInfoModel newInfo = new JdyClassInfoModel(aInternalName, null, this.repoName);
        return this.addClassInfo((newInfo));
    }

    public JdyClassInfoModel addClassInfo(String aInternalName, JdyClassInfoModel aSuperclass) throws InvalidClassInfoException
    {
        JdyClassInfoModel newInfo = new JdyClassInfoModel(aInternalName, aSuperclass, this.repoName);
        return this.addClassInfo((newInfo));
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

    /**
     * Make sure that the class breaks no validation rule
     *
     * @param aClassInfoToAdd
     * @throws InvalidClassInfoException
     */
    protected void validateClassInfo(ClassInfo aClassInfoToAdd) throws InvalidClassInfoException
    {
        for (JdyRepositoryListener curListener : allListener)
        {
            curListener.validateClassInfo(aClassInfoToAdd, this.allClassInfos);
        }
    }

    public void addListener(JdyRepositoryListener aListener2Add)
    {
        this.allListener.add(aListener2Add);
    }

    public static void addAssociation(String anAssocName, JdyClassInfoModel aMasterClass, JdyClassInfoModel aDetailClass, String aDetailInternalName, String aDetailExternalName, boolean keyInDetail, boolean notNullInDetail, boolean aIsDependentFlag)
    {
        JdyObjectReferenceModel detailToMasterAssoc
                = new JdyObjectReferenceModel(aMasterClass, aDetailInternalName, aDetailExternalName, keyInDetail, notNullInDetail, aIsDependentFlag);
        detailToMasterAssoc.setIsInAssociation(true);
        aDetailClass.addAttributeInfo(detailToMasterAssoc);
        JdyAssociationModel newAssoc = new JdyAssociationModel(detailToMasterAssoc, aDetailClass, anAssocName);
        aMasterClass.addAssociation(newAssoc);
    }

    public static void addAssociation(String anAssocName, JdyClassInfoModel aMasterClass, JdyClassInfoModel aDetailClass, boolean keyInDetail, boolean notNullInDetail)
    {
        JdyObjectReferenceModel detailToMasterAssoc
                = new JdyObjectReferenceModel(aMasterClass, aMasterClass.getInternalName(), aMasterClass.getInternalName(), keyInDetail, notNullInDetail, true);
        detailToMasterAssoc.setIsInAssociation(true);
        aDetailClass.addAttributeInfo(detailToMasterAssoc);

        JdyAssociationModel newAssoc = new JdyAssociationModel(detailToMasterAssoc, aDetailClass, anAssocName);
        aMasterClass.addAssociation(newAssoc);
    }

}
