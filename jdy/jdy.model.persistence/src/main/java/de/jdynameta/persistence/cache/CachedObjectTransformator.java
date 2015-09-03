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
package de.jdynameta.persistence.cache;

import de.jdynameta.base.cache.MultibleClassInfoIdentityCache;
import de.jdynameta.base.cache.SingelClassInfoIdentityCache;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AttributeAdaptor;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;

/**
 * Delegate the object creation to another object creator Cache already created
 * objects in an identity cache. If the object already exists, the existing
 * reference is only updated and returned
 *
 * @author rsc
 * @param <TTransformToValueObj>
 * @param <TCreatedFromValueObj>
 */
@SuppressWarnings("serial")
public class CachedObjectTransformator<TTransformToValueObj, TCreatedFromValueObj> implements ObjectTransformator<TTransformToValueObj, TCreatedFromValueObj>
{
    private MultibleClassInfoIdentityCache<TCreatedFromValueObj> classInfoCache;
    private UpdatableObjectCreator<TTransformToValueObj, TCreatedFromValueObj> newObjectCreator;

    /**
     *
     */
    public CachedObjectTransformator()
    {
        this(new MultibleClassInfoIdentityCache<>(), null);
    }

    /**
     *
     * @param aClassinfoCache
     * @param aNewObjectCreator
     */
    public CachedObjectTransformator(MultibleClassInfoIdentityCache<TCreatedFromValueObj> aClassinfoCache, UpdatableObjectCreator<TTransformToValueObj, TCreatedFromValueObj> aNewObjectCreator)
    {
        super();
        this.classInfoCache = aClassinfoCache;
        this.newObjectCreator = aNewObjectCreator;
    }

    @Override
    public TCreatedFromValueObj createObjectFor(final TypedValueObject aValueObject) throws ObjectCreationException
    {

        TCreatedFromValueObj result = null;

        SingelClassInfoIdentityCache<TCreatedFromValueObj> tmpInfoCache = classInfoCache.getCacheForClassInfo(aValueObject.getClassInfo());

        final TypedHashedValueObject objWithResolvedRefs = new TypedHashedValueObject(aValueObject.getClassInfo());

        try
        {
            aValueObject.getClassInfo().handleAttributes(new AttributeAdaptor()
            {
                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
                {
                    if (aValueObject.hasValueFor(aInfo))
                    {
                        objWithResolvedRefs.setValue(aInfo, objToHandle);
                    }
                }

                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
                {
                    try
                    {
                        if (aValueObject.hasValueFor(aInfo))
                        {
                            if (objToHandle != null)
                            {
                                objWithResolvedRefs.setValue(aInfo, createObjectFor((TypedValueObject) objToHandle));
                            } else
                            {
                                // has to be set because of hasValueFor()
                                objWithResolvedRefs.setValue(aInfo, null);
                            }
                        }
                    } catch (ObjectCreationException e)
                    {
                        throw new JdyPersistentException(e.getLocalizedMessage(), e);
                    }
                }
            }, aValueObject);
        } catch (JdyPersistentException e)
        {
            throw new ObjectCreationException(e.getLocalizedMessage(), e);
        }

        if (aValueObject != null)
        {
            if (tmpInfoCache.isObjectInCache(aValueObject))
            {
                result = tmpInfoCache.getObject(aValueObject);
                if (areAllValuesSetForClassInfo(aValueObject.getClassInfo(), aValueObject))
                {
                    this.newObjectCreator.updateValuesInObject(objWithResolvedRefs, result);
                }
            } else
            {
                result = this.newObjectCreator.createObjectFor(objWithResolvedRefs);
                tmpInfoCache.insertObjectForValueModel(aValueObject, result);
            }
        }

        return result;
    }

    /**
     *
     * @param aClassinfo
     * @return
     * @throws ObjectCreationException
     */
    @Override
    public TCreatedFromValueObj createNewObjectFor(ClassInfo aClassinfo) throws ObjectCreationException
    {
        return this.newObjectCreator.createNewObjectFor(aClassinfo);
    }

    private boolean areAllValuesSetForClassInfo(ClassInfo aClassInfo, ValueObject aValueModel)
    {
        boolean allValuesSet = true;

        for (AttributeInfo curInfo : aClassInfo.getAttributeInfoIterator())
        {

            allValuesSet = aValueModel.hasValueFor(curInfo);
            if (!allValuesSet)
            {
                break;
            }
        }

        return allValuesSet;
    }

    @Override
    public TypedValueObject getValueObjectFor(ClassInfo aClassinfo, TTransformToValueObj aObjectToTransform)
    {
        return newObjectCreator.getValueObjectFor(aClassinfo, aObjectToTransform);
    }

    /**
     * @return
     */
    public UpdatableObjectCreator<TTransformToValueObj, TCreatedFromValueObj> getNewObjectCreator()
    {
        return newObjectCreator;
    }

    /**
     * @param aCreator
     */
    public void setNewObjectCreator(UpdatableObjectCreator<TTransformToValueObj, TCreatedFromValueObj> aCreator)
    {
        newObjectCreator = aCreator;
    }

}
