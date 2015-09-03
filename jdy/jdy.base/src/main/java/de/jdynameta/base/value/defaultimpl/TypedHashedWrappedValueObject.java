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
package de.jdynameta.base.value.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ValueObject;

@SuppressWarnings("serial")
public class TypedHashedWrappedValueObject<TWrapObj extends ValueObject> extends TypedHashedValueObject implements ChangeableTypedValueObject
{
    private TWrapObj wrappedValueObject;

    /**
     * @param aClassInfo
     * @param aWrappedValueObject
     */
    public TypedHashedWrappedValueObject(ClassInfo aClassInfo, TWrapObj aWrappedValueObject)
    {
        super();
        this.setClassInfo(aClassInfo);
        this.wrappedValueObject = aWrappedValueObject;
    }

    @Override
    public Object getValue(AttributeInfo aInfo)
    {
        return (super.hasValueFor(aInfo))
                ? super.getValue(aInfo)
                : wrappedValueObject.getValue(aInfo);
    }

    @Override
    public void setValue(AttributeInfo aInfo, Object aValue)
    {
        super.setValue(aInfo, aValue);
    }

    @Override
    public boolean hasValueFor(AttributeInfo aInfo)
    {
        return super.hasValueFor(aInfo) || wrappedValueObject.hasValueFor(aInfo);
    }

    @Override
    public ObjectList<ChangeableTypedValueObject> getValue(AssociationInfo aAssocInfo)
    {
        ChangeableObjectList<ChangeableTypedValueObject> result;
        if (super.hasValueFor(aAssocInfo))
        {
            result = (ChangeableObjectList<ChangeableTypedValueObject>) super.getValue(aAssocInfo);
        } else
        {

            List<ChangeableTypedValueObject> wrappedList = new ArrayList<>();
            if (wrappedValueObject.getValue(aAssocInfo) != null)
            {
                for (TWrapObj wrapObj : (ObjectList<TWrapObj>) wrappedValueObject.getValue(aAssocInfo))
                {
                    wrappedList.add(new TypedHashedWrappedValueObject(getClassInfo(), wrapObj));
                }
            }
            result = new ChangeableObjectList<>(wrappedList);
            super.setValue(aAssocInfo, result);
        }
        return result;
    }

    @Override
    public boolean hasValueFor(AssociationInfo aInfo)
    {
        return super.hasValueFor(aInfo) || wrappedValueObject.hasValueFor(aInfo);
    }

    /**
     * @return Returns the wrappedValueObject.
     */
    public TWrapObj getWrappedValueObject()
    {
        return this.wrappedValueObject;
    }

    protected void setWrappedValueObject(TWrapObj aWrappedValueObject)
    {
        this.wrappedValueObject = aWrappedValueObject;
    }

    @Override
    public ObjectList<? extends ChangeableTypedValueObject> getValues(String anAssocName)
    {
        assert (this.getClassInfo().getAssoc(anAssocName) != null);
        return getValue(this.getClassInfo().getAssoc(anAssocName));
    }

    @Override
    public void setValues(String anAssocName, ObjectList<? extends ChangeableTypedValueObject> aValue)
    {
        assert (this.getClassInfo().getAssoc(anAssocName) != null);
        setValue(this.getClassInfo().getAssoc(anAssocName), aValue);
    }

    @Override
    public int hashCode()
    {
        return wrappedValueObject.hashCode();
    }

    public void clearCachedData()
    {
        super.clearAllValues();
    }

    /**
     *
     * @param anExternlName
     * @return
     */
    @Override
    public Object getValue(String anExternlName)
    {
        return getValue(this.getClassInfo().getAttributeInfoForExternalName(anExternlName));

    }

    /**
     *
     * @param anExternlName
     * @param aValue
     */
    @Override
    public void setValue(String anExternlName, Object aValue)
    {
        setValue(this.getClassInfo().getAttributeInfoForExternalName(anExternlName), aValue);
    }

}
