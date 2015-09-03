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
package de.jdynameta.base.creation;

import java.io.Serializable;

import de.jdynameta.base.cache.MultibleClassInfoIdentityCache;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;

/**
 * Creates an Object an set the values in it with the Java Reflection Methods.
 * Creates an instance of Proxy for new created Proxy objects
 *
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractObjectCreator<TCreatedObjFromValueObj>
        implements ObjectCreator<TCreatedObjFromValueObj>, Serializable
  {

    private MultibleClassInfoIdentityCache<TCreatedObjFromValueObj> cache;

    public AbstractObjectCreator()
    {
        this.cache = new MultibleClassInfoIdentityCache<>();
    }

    protected MultibleClassInfoIdentityCache<TCreatedObjFromValueObj> getCache()
    {
        return cache;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.persistence.ObjectCreator#createObjectFor(de.comafra.model.metainfo.ClassInfo, de.comafra.model.value.ValueObject)
     */
    @Override
    public TCreatedObjFromValueObj createObjectFor(TypedValueObject aTypedValueObject) throws ObjectCreationException
    {
        TCreatedObjFromValueObj newObject = cache.getObject(aTypedValueObject);

        if (newObject == null)
        {
            newObject = createNewObjectFor(aTypedValueObject.getClassInfo());
            cache.insertObject(aTypedValueObject, newObject);
            try
            {
                setValuesInObject(newObject, aTypedValueObject);
            } catch (JdyPersistentException e)
            {
                throw new ObjectCreationException(e);
            }
        }

        return newObject;
    }

    @Override
    public abstract TCreatedObjFromValueObj createNewObjectFor(ClassInfo aClassinfo) throws ObjectCreationException;

    protected void setValuesInObject(TCreatedObjFromValueObj aObjoSetVals, TypedValueObject aObjToGetVals) throws JdyPersistentException, ObjectCreationException
    {
        setAttributeValues(aObjoSetVals, aObjToGetVals);
        setAssocValues(aObjoSetVals, aObjToGetVals);
    }

    protected void setAssocValues(TCreatedObjFromValueObj aObjoSetVals, TypedValueObject aObjToGetVals)
            throws JdyPersistentException, ObjectCreationException
    {
        for (AssociationInfo curAssocInfo : aObjToGetVals.getClassInfo().getAssociationInfoIterator())
        {

            setAssocVal(curAssocInfo, aObjoSetVals, aObjToGetVals);
        }
    }

    protected void setAssocVal(AssociationInfo curAssocInfo, TCreatedObjFromValueObj aObjoSetVals, TypedValueObject aObjToGetVals)
            throws ObjectCreationException, JdyPersistentException
    {
        if (aObjToGetVals == null)
        {
            setEmptyList(curAssocInfo, aObjoSetVals);
        } else if (aObjToGetVals.hasValueFor(curAssocInfo))
        {
            setListForAssoc(curAssocInfo, aObjoSetVals, aObjToGetVals);
        } else
        {
            setProxyListForAssoc(curAssocInfo, aObjoSetVals, aObjToGetVals);
        }
    }

    protected abstract void setListForAssoc(AssociationInfo curAssocInfo, TCreatedObjFromValueObj aObjoSetVals, TypedValueObject aObjToGetVals)
            throws ObjectCreationException;

    protected abstract void setEmptyList(AssociationInfo curAssocInfo, TCreatedObjFromValueObj aObjoSetVals)
            throws ObjectCreationException;

    protected abstract void setProxyListForAssoc(AssociationInfo curAssocInfo, TCreatedObjFromValueObj aObjoSetVals, TypedValueObject aObjToGetVals)
            throws ObjectCreationException;

    private void setAttributeValues(TCreatedObjFromValueObj aObjoSetVals,
            TypedValueObject aObjToGetVals) throws JdyPersistentException, ObjectCreationException
    {
        for (AttributeInfo curInfo : aObjToGetVals.getClassInfo().getAttributeInfoIterator())
        {

            setAttributeVal(curInfo, aObjoSetVals, aObjToGetVals);
        }
    }

    private void setAttributeVal(AttributeInfo curInfo, TCreatedObjFromValueObj aObjoSetVals, TypedValueObject aObjToGetVals)
            throws JdyPersistentException, ObjectCreationException
    {

        Object objToHandle = aObjToGetVals.getValue((curInfo));
        if (curInfo instanceof ObjectReferenceAttributeInfo)
        {
            setObjectReferenceVal((ObjectReferenceAttributeInfo) curInfo, aObjoSetVals, (TypedValueObject) objToHandle);
        } else
        {
            setPrimitiveVal((PrimitiveAttributeInfo) curInfo, aObjoSetVals, objToHandle);
        }
    }

    protected abstract void setObjectReferenceVal(ObjectReferenceAttributeInfo refInfo,
            TCreatedObjFromValueObj aObjoSetVals, TypedValueObject objToHandle)
            throws JdyPersistentException, ObjectCreationException;

    protected abstract void setPrimitiveVal(PrimitiveAttributeInfo refInfo,
            TCreatedObjFromValueObj aObjoSetVals, Object valueToHandle)
            throws JdyPersistentException;

    protected boolean areAllValuesSetForClassInfo(TypedValueObject aTypedValueObject)
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
