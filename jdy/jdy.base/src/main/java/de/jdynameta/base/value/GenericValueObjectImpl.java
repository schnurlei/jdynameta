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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;

/**
 * A Value Model can be used generic. No subclasses are needed to respond
 * getValue and setValue for the attributes of the ClassInfo. It also implements
 * a ProxyRsolver
 *
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class GenericValueObjectImpl extends HashedValueObject implements ChangeableTypedValueObject
{
    private final ClassInfo classInfo;
    private ProxyResolver proxyResolver;
    /**
     * In new Objects no check about proxy is needed
     */
    private boolean isNew;

    /**
     *
     * @param aClassInfo
     */
    public GenericValueObjectImpl(ClassInfo aClassInfo)
    {
        super();
        assert (aClassInfo != null);
        this.classInfo = aClassInfo;
        this.proxyResolver = null;
        this.isNew = true;
    }

    /**
     *
     * @param aProxyResolver
     * @param aClassInfo
     * @param aIsNewFlag
     */
    public GenericValueObjectImpl(ProxyResolver aProxyResolver, ClassInfo aClassInfo, boolean aIsNewFlag)
    {
        super();
        assert (aClassInfo != null);
        this.classInfo = aClassInfo;
        this.proxyResolver = aProxyResolver;
        this.isNew = aIsNewFlag;
    }

    @Override
    public Object getValue(AttributeInfo aInfo)
    {
        if (attributeIsProxy(aInfo))
        {
            try
            {
                if (proxyResolver == null)
                {
                    throw new ProxyResolveException("Unable to resolve " + aInfo.getInternalName());
                }
                proxyResolver.resolveProxy(this);
            } catch (ObjectCreationException excp)
            {
                throw new ProxyResolveException(excp);
            }
        }
        assert (!attributeIsProxy(aInfo));
        return super.getValue(aInfo);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#getValue(de.comafra.model.metainfo.AssociationInfo)
     */
    @Override
    public ObjectList<? extends GenericValueObjectImpl> getValue(AssociationInfo aInfo)
    {
        if (attributeIsProxy(aInfo))
        {
            try
            {
                proxyResolver.resolveProxy(this);
            } catch (ObjectCreationException excp)
            {
                throw new ProxyResolveException(excp);
            }
        }
        assert (!attributeIsProxy(aInfo));
        return (ObjectList<? extends GenericValueObjectImpl>) super.getValue(aInfo);
    }

    /**
     *
     * @param anAssocName
     * @return
     */
    @Override
    public ObjectList<? extends GenericValueObjectImpl> getValues(String anAssocName)
    {
        assert (this.classInfo.getAssoc(anAssocName) != null);
        return getValue(this.classInfo.getAssoc(anAssocName));

    }

    @Override
    public void setValue(AssociationInfo aInfo, ObjectList<? extends ChangeableTypedValueObject> aValue)
    {
        super.setValue(aInfo, aValue);
    }

    /**
     *
     * @param anExternlName
     * @return
     */
    @Override
    public Object getValue(String anExternlName)
    {
        assert (this.classInfo.getAttributeInfoForExternalName(anExternlName) != null);
        return (this.classInfo == null) ? null : getValue(this.classInfo.getAttributeInfoForExternalName(anExternlName));
    }

    /**
     *
     * @param anExternlName
     * @param aValue
     */
    @Override
    public void setValue(String anExternlName, Object aValue)
    {
        assert (this.classInfo.getAttributeInfoForExternalName(anExternlName) != null);
        setValue(this.classInfo.getAttributeInfoForExternalName(anExternlName), aValue);
    }

    /**
     *
     * @param anAssocName
     * @param aValue
     */
    @Override
    public void setValues(String anAssocName, ObjectList<? extends ChangeableTypedValueObject> aValue)
    {
        assert (this.classInfo.getAssoc(anAssocName) != null);
        setValue(this.classInfo.getAssoc(anAssocName), aValue);
    }

    @Override
    public void setValue(AttributeInfo aInfo, Object aValue)
    {
        super.setValue(aInfo, aValue);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AttributeInfo)
     */
    private boolean attributeIsProxy(AttributeInfo aInfo)
    {
        return !this.isNew && !hasValueFor(aInfo);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AttributeInfo)
     */
    private boolean attributeIsProxy(AssociationInfo aInfo)
    {
        return !this.isNew && !hasValueFor(aInfo);
    }

    /**
     *
     * @return
     */
    @Override
    public ClassInfo getClassInfo()
    {
        return this.classInfo;
    }

    public void setNew(boolean aIsNew)
    {
        this.isNew = aIsNew;
    }

    public void setProxyResolver(ProxyResolver aProxyResolver)
    {
        this.proxyResolver = aProxyResolver;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(getClass().getSimpleName()).append("->").append(classInfo.getInternalName()).append("\n ");
        for (AttributeInfo curInfo : getClassInfo().getAttributeInfoIterator())
        {

            Object objToSet = this.getValue(curInfo);
            appendAttribute(result, curInfo, objToSet, super.hasValueFor(curInfo));
            result.append(" \n ");
        }
        for (AssociationInfo curInfo : getClassInfo().getAssociationInfoIterator())
        {

            result.append("#").append(curInfo.getNameResource()).append(" - ");
            if (this.getValue(curInfo) == null)
            {
                result.append("<null>");
            } else
            {
                result.append(this.getValue(curInfo).size());
            }
            result.append("\n");
        }

        return result.toString();
    }

    private void appendAttribute(StringBuffer aBuffer, AttributeInfo aInfo, Object objToSet, boolean isChanged)
    {
        if (aInfo instanceof PrimitiveAttributeInfo)
        {
            aBuffer.append(aInfo.getInternalName());
            if (isChanged)
            {
                aBuffer.append("*");
            }
            aBuffer.append("-").append(objToSet);
        } else if (aInfo instanceof ObjectReferenceAttributeInfo)
        {

            aBuffer.append(aInfo.getInternalName());
            if (isChanged)
            {
                aBuffer.append("*");
            }

            aBuffer.append("[");
            if (objToSet == null)
            {
                aBuffer.append("null");
            } else if (objToSet instanceof ValueObject)
            {
                ValueObject refObj = (ValueObject) objToSet;
                aBuffer.append(refObj.getClass().getSimpleName()).append("->");
                ObjectReferenceAttributeInfo refInfo = (ObjectReferenceAttributeInfo) aInfo;
                for (AttributeInfo curRefInfo : refInfo.getReferencedClass().getAttributeInfoIterator())
                {
                    try
                    {
                        appendAttribute(aBuffer, curRefInfo, refObj.getValue(curRefInfo), false);
                    } catch (Throwable ex)
                    {
                        aBuffer.append("#error#");
                    }
                }
            } else
            {
                aBuffer.append("#error#");
            }
            aBuffer.append("]");
        }
    }
}
