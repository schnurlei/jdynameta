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

import java.io.Serializable;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;

/**
 * @author rsc
 */
@SuppressWarnings("serial")
public class JdyAssociationModel implements AssociationInfo, Serializable
{

    private final JdyClassInfoModel detailClass;
    private final ObjectReferenceAttributeInfo masterClassReference;
    private final String assocName;

    /**
     *
     * @param aMasterClassRef
     * @param aDetailClass
     * @param anAssocName
     */
    public JdyAssociationModel(JdyObjectReferenceModel aMasterClassRef, JdyClassInfoModel aDetailClass, String anAssocName)
    {
        super();

        this.assocName = anAssocName;
        this.detailClass = aDetailClass;
        this.masterClassReference = aMasterClassRef;
    }

    /**
     *
     * @param aMasterClass
     * @param aDetailClass
     * @param aDetailInternalName
     * @param aNameResource
     * @param keyInDetail
     * @param aDetailExternalName
     * @param aIsDependentFlag
     * @param notNullInDetail
     */
    public JdyAssociationModel(JdyClassInfoModel aMasterClass, JdyClassInfoModel aDetailClass, String aDetailInternalName, String aDetailExternalName, boolean keyInDetail, boolean notNullInDetail, boolean aIsDependentFlag, String aNameResource)
    {
        super();
        JdyAssociationModel addAssociation = aMasterClass.addAssociation(this);
        this.detailClass = aDetailClass;
        JdyObjectReferenceModel detailAssoc
                = new JdyObjectReferenceModel(aMasterClass, aDetailInternalName, aDetailExternalName, keyInDetail, notNullInDetail, aIsDependentFlag);
        detailAssoc.setIsInAssociation(true);
        this.masterClassReference = detailAssoc;
        this.detailClass.addAttributeInfo(detailAssoc);
        this.assocName = aNameResource;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.AssociationInfo#getDetailClass()
     */
    @Override
    public ClassInfo getDetailClass()
    {
        return detailClass;
    }


    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.AssociationInfo#getMasterClassReference()
     */
    @Override
    public ObjectReferenceAttributeInfo getMasterClassReference()
    {
        return masterClassReference;
    }

    /**
     * @return Returns the nameResource.
     */
    @Override
    public String getNameResource()
    {
        return this.assocName;
    }
}
