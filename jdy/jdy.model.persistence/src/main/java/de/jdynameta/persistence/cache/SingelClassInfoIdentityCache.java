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

import java.io.Serializable;
import java.util.Hashtable;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.ValueObjectKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Map valueObjects to external Objects Manages the identity of valueObjects
 * depending on their primary key values.
 *
 * @param <TCreatedFromValueObj>
 * @TODO use Weak references
 */
@SuppressWarnings("serial")
public class SingelClassInfoIdentityCache<TCreatedFromValueObj> implements Serializable
{
    private final Map<ValueObjectKey, TCreatedFromValueObj> valueKey2ObjectMap;
    /**
     * Classinfo of the Cached values
     */
    private final ClassInfo valueClassInfo;

    /**
     * ObjectCache - Konstruktorkommentar.
     *
     * @param aInfo
     * @param cacheSize
     */
    public SingelClassInfoIdentityCache(ClassInfo aInfo, int cacheSize)
    {
        super();
        this.valueKey2ObjectMap = new HashMap<>(cacheSize);
        this.valueClassInfo = aInfo;
    }

    /**
     *
     * @author	Rainer Schneider
     * @param aValueModel
     * @return boolean
     */
    public TCreatedFromValueObj getObject(ValueObject aValueModel)
    {
        return this.valueKey2ObjectMap.get(createKeyForValueModel(aValueModel, this.valueClassInfo));
    }

    /**
     * Insert the Model into the Hashtable
     *
     * @author	Rainer Schneider
     * @param aValueModel
     * @param anObject
     */
    public void insertObjectForValueModel(ValueObject aValueModel, TCreatedFromValueObj anObject)
    {
        ValueObjectKey tmpModelKey = createKeyForValueModel(aValueModel, this.valueClassInfo);
        this.valueKey2ObjectMap.put(tmpModelKey, anObject);
    }

    /**
     *
     * @author	Rainer Schneider
     * @param aValueModel
     * @return boolean
     */
    public boolean isObjectInCache(ValueObject aValueModel)
    {
        return this.valueKey2ObjectMap.containsKey(createKeyForValueModel(aValueModel, this.valueClassInfo));
    }

    /**
     * @param aValueModel
     * @param aClassInfo
     * @return
     */
    public static ValueObjectKey createKeyForValueModel(ValueObject aValueModel, ClassInfo aClassInfo)
    {
        final ValueObjectKey resultKey = new ValueObjectKey();

        try
        {
            AttributeHandler handler = new AttributeHandler()
            {
                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject aObjToHandle) throws JdyPersistentException
                {
                    resultKey.addKey(createKeyForValueModel(aObjToHandle, aInfo.getReferencedClass()));
                }

                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object aObjToHandle) throws JdyPersistentException
                {
                    resultKey.addKey(aObjToHandle);
                }
            };

            for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
            {
                if (curAttr.isKey())
                {
                    if (aValueModel != null)
                    {
                        curAttr.handleAttribute(handler, aValueModel.getValue(curAttr));
                    } else
                    {
                        curAttr.handleAttribute(handler, null);
                    }
                }
            }

        } catch (JdyPersistentException excp)
        {
            excp.printStackTrace();
        }

        return resultKey;
    }

}
