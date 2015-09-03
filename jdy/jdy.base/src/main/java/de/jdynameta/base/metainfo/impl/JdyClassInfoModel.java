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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyAbstractAttributeModel.AttributeInfoListener;
import de.jdynameta.base.metainfo.primitive.VarCharType.TextMimeType;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.DbDomainValue;

/**
 * Java implementation of an ClassInfo
 *
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class JdyClassInfoModel implements ClassInfo, Serializable
{

    private String nameSpace;
    private String repoName;
    private String internalName;
    private String externalName;
    private String shortName;
    private boolean isAbstract;

    private final List<AttributeInfo> attributeList;
    private final List<AssociationInfo> associationList;
    private final ChangeableObjectList<ClassInfo> allSubClassesList;

    private transient List<ClassInfoListener> allListener;

    private ClassInfo superclass;
    private AttributeInfoListener attributeListener;

    /**
     * Constructor for ClassInfo.
     */
    public JdyClassInfoModel()
    {
        super();
        this.attributeList = new ArrayList<>();
        this.associationList = new ArrayList<>();
        this.allSubClassesList = new ChangeableObjectList<>();
        this.allListener = new ArrayList<>();
        this.isAbstract = false;
        this.attributeListener = createAttributelistener();
    }

    /**
     * Constructor for ClassInfo.
     * @param anInternalName
     * @param aSuperclass
     * @param aRepoName
     */
    public JdyClassInfoModel(String anInternalName, JdyClassInfoModel aSuperclass, String aRepoName)
    {
        this();
        this.internalName = anInternalName;
        this.repoName = aRepoName;
        this.superclass = aSuperclass;
        if (aSuperclass != null)
        {
            aSuperclass.addSubclass(this);
        }
    }

    /**
     * Constructor for ClassInfo.
     * @param aSuperclass
     * @param aRepoName
     */
    public JdyClassInfoModel(JdyClassInfoModel aSuperclass, String aRepoName)
    {
        this(null, aSuperclass, aRepoName);
    }

    public JdyClassInfoModel(String anInternalName)
    {
        this(anInternalName, null, null);
    }

    private JdyAbstractAttributeModel.AttributeInfoListener createAttributelistener()
    {
        return this::attributeValueChanged;
    }

    /**
     *
     * @param changedAttribute
     */
    protected void attributeValueChanged(JdyAbstractAttributeModel changedAttribute)
    {
        fireValueChanged();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.ClassInfo#isAbstract()
     */
    @Override
    public boolean isAbstract()
    {
        return this.isAbstract;
    }

    public JdyClassInfoModel setAbstract(boolean newValue)
    {
        this.isAbstract = newValue;
        return this;
    }

    /**
     *
     * Returns the externalName.
     *
     * @return String
     */
    @Override
    public String getNameSpace()
    {
        return this.nameSpace;
    }

    public void setNameSpace(String aNameSpace)
    {
        nameSpace = aNameSpace;
    }

    @Override
    public String getRepoName()
    {
        return this.repoName;
    }

    /**
     * Returns the externalName.
     *
     * @return String
     */
    @Override
    public String getInternalName()
    {
        return this.internalName;
    }

    /**
     * Returns the externalName.
     *
     * @return String
     */
    @Override
    public String getExternalName()
    {
        return (externalName == null) ? internalName : externalName;
    }

    /**
     * Returs the aliasName
     *
     * @return String
     */
    @Override
    public String getShortName()
    {
        return (this.shortName == null) ? internalName : shortName;
    }

    /**
     * Sets the externalName.
     *
     * @param aExternalName
     * @return
     */
    public JdyClassInfoModel setExternalName(String aExternalName)
    {
        this.externalName = aExternalName;
        fireValueChanged();
        return this;
    }

    /**
     * Sets the aliasName
     *
     * @param aShortName
     * @return
     */
    public JdyClassInfoModel setShortName(String aShortName)
    {
        this.shortName = aShortName;
        fireValueChanged();
        return this;
    }

    /**
     * @param string
     * @return
     */
    public JdyClassInfoModel setInternalName(String string)
    {
        internalName = string;
        fireValueChanged();
        return this;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.ClassInfo#handleAttributes(de.comafra.model.metainfo.PrimitiveAttributeHandler, de.comafra.model.value.ValueObject)
     */
    @Override
    public void handleAttributes(AttributeHandler aHandler, ValueObject objToHandle)
            throws JdyPersistentException
    {
        for (AttributeInfo curAttr : getAttributeInfoIterator())
        {

            if (objToHandle != null)
            {
                curAttr.handleAttribute(aHandler, objToHandle.getValue(curAttr));
            } else
            {
                curAttr.handleAttribute(aHandler, null);
            }
        }
    }

    public JdyAbstractAttributeModel addAttributeInfo(JdyAbstractAttributeModel aInfo)
    {
        this.attributeList.add(aInfo);
        fireValueChanged();
        aInfo.addListener(attributeListener);
        return aInfo;
    }

    public JdyAssociationModel addAssociation(JdyAssociationModel aAssoc)
    {
        this.associationList.add(aAssoc);
        return aAssoc;
    }

    public ClassInfo addSubclass(JdyClassInfoModel aSubClass)
    {
        this.allSubClassesList.addObject(aSubClass);
        aSubClass.superclass = this;
        return aSubClass;
    }

    @Override
    public final AttributeInfo getAttributeInfoForExternalName(String aAttributeName)
    {
        AttributeInfo result = null;
        for (AttributeInfo curAttr : getAttributeInfoIterator())
        {
            if (curAttr.getExternalName().equals(aAttributeName))
            {
                result = curAttr;
            }
        }
        return result;
    }

    public final AttributeInfo getAttribute(String aInternalName)
    {
        AttributeInfo result = null;
        for (AttributeInfo curAttr : getAttributeInfoIterator())
        {
            if (curAttr.getInternalName().equals(aInternalName))
            {
                result = curAttr;
            }
        }
        return result;
    }

    @Override
    public AssociationInfo getAssoc(String aAssocName)
    {
        return getAssociationNameResource(aAssocName);
    }

    public final AssociationInfo getAssociationNameResource(String aName)
    {
        AssociationInfo result = null;
        for (AssociationInfo curAssoc : getAssociationInfoIterator())
        {
            if (curAssoc.getNameResource().equals(aName))
            {
                result = curAssoc;
                break;
            }
        }
        return result;
    }

    @Override
    public final Iterable<? extends AttributeInfo> getAttributeInfoIterator()
    {
        return getAllAttributeList();
    }

    @Override
    public final int attributeInfoSize()
    {
        return getAllAttributeList().size();
    }

    @Override
    public final int getAssociationInfoSize()
    {
        return getAllAssociationList().size();
    }

    /**
     * Get all attributes of this class. Subclass could overwite this Method
     *
     * @return
     */
    protected List<AttributeInfo> getAllAttributeList()
    {
        ArrayList<AttributeInfo> tmpAttrList = new ArrayList<>(this.attributeList.size());

        if (this.superclass != null)
        {
            for (AttributeInfo curAttr : superclass.getAttributeInfoIterator())
            {
                tmpAttrList.add(curAttr);
            }
        }

        tmpAttrList.addAll(this.attributeList);
        return tmpAttrList;
    }

    /**
     * Get all attributes of this class. Subclass could overwite this Method
     *
     * @return
     */
    protected List<AssociationInfo> getAllAssociationList()
    {
        ArrayList<AssociationInfo> tmpAssocList = new ArrayList<>(this.associationList.size());

        if (this.superclass != null)
        {
            for (AssociationInfo curAssoc : superclass.getAssociationInfoIterator())
            {
                tmpAssocList.add(curAssoc);
            }
        }

        tmpAssocList.addAll(this.associationList);

        return tmpAssocList;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.ClassInfo#getAssociationInfoIterator()
     */
    @Override
    public Iterable<AssociationInfo> getAssociationInfoIterator()
    {
        return this.getAllAssociationList();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.ClassInfo#getAllSubclasses()
     */
    @Override
    public ObjectList<ClassInfo> getAllSubclasses()
    {
        return this.allSubClassesList;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.metainfo.ClassInfo#hasSubClasses()
     */
    @Override
    public boolean hasSubClasses()
    {
        return this.allSubClassesList != null && this.allSubClassesList.size() > 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "@" + "ClassInfo: " + " " + this.repoName + "#" + this.internalName;
    }

    /**
     * @return
     */
    @Override
    public ClassInfo getSuperclass()
    {
        return superclass;
    }

    @Override
    public boolean isSubclassAttribute(AttributeInfo aAttrInfo)
    {
        return this.attributeList.contains(aAttrInfo);
    }

    protected void fireValueChanged()
    {
        this.allListener.stream().forEach((curListener) ->
        {
            curListener.valueChanged(this);
        });
    }

    public void addListener(ClassInfoListener aListener2Add)
    {
        this.allListener.add(aListener2Add);
    }

    public JdyObjectReferenceModel addReference(String anInternalName, JdyClassInfoModel aReferencedClass)
    {
        JdyObjectReferenceModel newRef = new JdyObjectReferenceModel(aReferencedClass, anInternalName, anInternalName, false, false);
        this.addAttributeInfo(newRef);
        return newRef;
    }

    public JdyPrimitiveAttributeModel addBooleanAttr(String aInternalName)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyBooleanType(), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addBlobAttr(String aInternalName)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyBlobType(), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addDecimalAttr(String aInternalName, BigDecimal aMinValue, BigDecimal aMaxValue, int aScale, DbDomainValue<BigDecimal>... allDomainValues)
    {
        List<DbDomainValue<BigDecimal>> domainValueList = Arrays.asList(allDomainValues);
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyDecimalType(aMinValue, aMaxValue, aScale, domainValueList), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addFloatAttr(String aInternalName)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyFloatType(), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addLongAttr(String aInternalName, long aMinValue, long aMaxValue, DbDomainValue<Long>... allDomainValues)
    {
        List<DbDomainValue<Long>> domainValueList = Arrays.asList(allDomainValues);
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyLongType(aMinValue, aMaxValue, domainValueList), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addTextAttr(String aInternalName, int aLength, DbDomainValue<String>... allDomainValues)
    {
        List<DbDomainValue<String>> domainValueList = Arrays.asList(allDomainValues);
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyTextType(aLength, null, domainValueList), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    /**
     * Add a Timestamp attribute with the geven Internal name to the Class
     *
     * @param aInternalName
     * @return
     */
    public JdyPrimitiveAttributeModel addTimestampAttr(String aInternalName)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyTimeStampType(), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addTimestampAttr(String aInternalName, boolean isDatePartUsed, boolean isTimePartUsed)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyTimeStampType(isDatePartUsed, isTimePartUsed), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addVarCharAttr(String aInternalName, int aLength)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyVarCharType(aLength, false), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addVarCharAttr(String aInternalName, int aLength, TextMimeType aMimeType)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyVarCharType(aLength, false, aMimeType), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    public JdyPrimitiveAttributeModel addVarCharAttr(String aInternalName, int aLength, boolean aIsClobFlag)
    {
        JdyPrimitiveAttributeModel newAttr = new JdyPrimitiveAttributeModel(new JdyVarCharType(aLength, aIsClobFlag), aInternalName, aInternalName, false, false);
        this.addAttributeInfo(newAttr);
        return newAttr;
    }

    /**
     * Listener to changes in the class info
     *
     * @author rs
     *
     */
    public static interface ClassInfoListener
    {

        public void valueChanged(JdyClassInfoModel changedClassInfo);

    }

}
