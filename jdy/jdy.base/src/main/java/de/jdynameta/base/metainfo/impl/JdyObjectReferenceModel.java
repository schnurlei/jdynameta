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

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class JdyObjectReferenceModel extends JdyAbstractAttributeModel implements ObjectReferenceAttributeInfo, Serializable
{

    private ClassInfo referencedClass;
    private boolean isDependent;
    private boolean isInAssociation;

    /**
     * Constructor for ObjectReferenceInfo.
     *
     * @param aReferencedClass
     * @param aInternalName
     * @param aExternalName
     * @param isKeyFlag
     * @param isNotNullFlag
     */
    public JdyObjectReferenceModel(ClassInfo aReferencedClass, String aInternalName, String aExternalName, boolean isKeyFlag, boolean isNotNullFlag)
    {
        this(aReferencedClass, aInternalName, aExternalName, isKeyFlag, isNotNullFlag, false);
    }

    /**
     * Constructor for ObjectReferenceInfo.
     *
     * @param aReferencedClass
     * @param anInternalName
     * @param anExternalName
     * @param isKeyFlag
     * @param isNotNullFlag
     * @param aIsDependentFlag
     */
    public JdyObjectReferenceModel(ClassInfo aReferencedClass, String anInternalName, String anExternalName, boolean isKeyFlag, boolean isNotNullFlag, boolean aIsDependentFlag)
    {
        super(anInternalName, anExternalName, isKeyFlag, isNotNullFlag);
        this.referencedClass = aReferencedClass;
        this.isDependent = aIsDependentFlag;
    }


    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.AttributeInfo#handleAttribute(de.comafra.model.metainfo.AttributeHandler, java.lang.Object)
     */

    /**
     *
     * @param aHandler
     * @param aValue
     * @throws JdyPersistentException
     */
    
    @Override
    public void handleAttribute(AttributeHandler aHandler, Object aValue)
            throws JdyPersistentException
    {
        aHandler.handleObjectReference(this, (ValueObject) aValue);
    }

    public boolean isPrimitive()
    {
        return false;
    }

    /**
     * Returns the referencedClass.
     *
     * @return ClassInfo
     */
    @Override
    public ClassInfo getReferencedClass()
    {
        return referencedClass;
    }

    /**
     * @return
     */
    @Override
    public boolean isDependent()
    {
        return isDependent;
    }

    /* (non-Javadoc)
     * @see metainfo.ObjectReferenceAttributeInfo#isMultipleAssociation()
     */
    @Override
    public boolean isInAssociation()
    {
        return this.isInAssociation;
    }

    public JdyObjectReferenceModel setDependent(boolean isDependent)
    {
        this.isDependent = isDependent;
        return this;
    }

    /**
     * @param aAssocFlag
     */
    public void setIsInAssociation(boolean aAssocFlag)
    {
        this.isInAssociation = aAssocFlag;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {

        return "[ObjectReference] " + this.getInternalName() + " " + this.referencedClass.getInternalName();
    }
}
