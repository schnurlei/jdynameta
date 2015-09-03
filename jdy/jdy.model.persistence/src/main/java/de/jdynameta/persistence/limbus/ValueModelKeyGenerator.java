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
package de.jdynameta.persistence.limbus;

import java.io.Serializable;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.ValueObjectKey;

/**
 * Key creator for a ValueModel
 *
 * @author rainer
 * @param <TValueObject>
 *
 */
@SuppressWarnings("serial")
public class ValueModelKeyGenerator<TValueObject extends ValueObject> implements KeyGenerator<TValueObject>, Serializable
{
    @Override
    public ValueObjectKey createKeyForValue(TValueObject aValueObject, ClassInfo aClassInfo)
    {
        return createKeyForValueObject(aValueObject, aClassInfo);
    }

    protected ValueObjectKey createKeyForValueObject(ValueObject aValueObject, ClassInfo aClassInfo)
    {
        final ValueObjectKey resultKey = new ValueObjectKey();

        try
        {
            aClassInfo.handleAttributes(new AttributeHandler()
            {
                @Override
                public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject aObjToHandle) throws JdyPersistentException
                {
                    if (aInfo.isKey())
                    {
                        resultKey.addKey(createKeyForValueObject(aObjToHandle, aInfo.getReferencedClass()));
                    }
                }

                @Override
                public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object aObjToHandle) throws JdyPersistentException
                {
                    if (aInfo.isKey())
                    {
                        resultKey.addKey(aObjToHandle);
                    }
                }
            }, aValueObject);
        } catch (JdyPersistentException excp)
        {
            excp.printStackTrace();
        }

        return resultKey;
    }

}
