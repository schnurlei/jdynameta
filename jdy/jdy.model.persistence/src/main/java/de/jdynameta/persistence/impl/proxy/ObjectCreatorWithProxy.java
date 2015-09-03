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
package de.jdynameta.persistence.impl.proxy;

import java.io.Serializable;

import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;

/**
 * Creates objects for a Value Model. When not all values are set for the
 * ClassInfo the object is created as ProxyObjects.
 *
 * @param <TCreatedObjFromValueObj>
 * @see ValueObject#hasValueFor(AttributeInfo)
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public abstract class ObjectCreatorWithProxy<TCreatedObjFromValueObj> implements ObjectCreator<TCreatedObjFromValueObj>, Serializable
{

    @Override
    public TCreatedObjFromValueObj createObjectFor(TypedValueObject aTypedValueObject) throws ObjectCreationException
    {
        TCreatedObjFromValueObj result;

        if (areAllValuesSetForClassInfo(aTypedValueObject))
        {
            result = createNoProxyObjectFor(aTypedValueObject);
        } else
        {
            result = createProxyObjectFor(aTypedValueObject);
        }

        return result;
    }

    /**
     *
     * @param aClassInfo
     * @return
     * @throws ObjectCreationException
     */
    @Override
    public abstract TCreatedObjFromValueObj createNewObjectFor(ClassInfo aClassInfo) throws ObjectCreationException;

    /**
     * @param aTypedValueObject
     * @return
     * @throws de.jdynameta.base.value.ObjectCreationException
     */
    protected abstract TCreatedObjFromValueObj createNoProxyObjectFor(TypedValueObject aTypedValueObject) throws ObjectCreationException;

    /**
     * @param aTypedValueObject
     * @return
     * @throws de.jdynameta.base.value.ObjectCreationException
     */
    protected abstract TCreatedObjFromValueObj createProxyObjectFor(TypedValueObject aTypedValueObject) throws ObjectCreationException;

    /**
     * Check whether a Proxy object or a Normal object nees to be created
     *
     * @see ValueObject#hasValueFor(AttributeInfo)
     * @param aClassInfo
     * @param aValueModel
     * @return
     */
    private boolean areAllValuesSetForClassInfo(TypedValueObject aTypedValueObject)
    {
        boolean allValuesSet = true;

        for (AttributeInfo curInfo : aTypedValueObject.getClassInfo().getAttributeInfoIterator())
        {

            allValuesSet = aTypedValueObject.hasValueFor(curInfo);
            if (!allValuesSet)
            {
                break;
            }
        }

        return allValuesSet;
    }
}
