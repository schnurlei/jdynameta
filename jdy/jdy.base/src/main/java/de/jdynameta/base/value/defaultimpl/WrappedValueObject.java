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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ValueObject;

/**
 * ValueObject that wraps another ValueObject
 *
 * @author * <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 *
 */
public class WrappedValueObject implements ValueObject
{
    private final ValueObject wrappedObject;

    /**
     * @param aWrappedObject
     */
    public WrappedValueObject(ValueObject aWrappedObject)
    {
        super();
        this.wrappedObject = aWrappedObject;
    }

    @Override
    public Object getValue(AttributeInfo aInfo)
    {
        return wrappedObject.getValue(aInfo);
    }

    @Override
    public boolean hasValueFor(AttributeInfo aInfo)
    {
        return wrappedObject.hasValueFor(aInfo);
    }

    public ObjectList<? extends ValueObject> getValue(AssociationInfo aInfo)
    {
        return wrappedObject.getValue(aInfo);
    }

    @Override
    public boolean hasValueFor(AssociationInfo aInfo)
    {
        return wrappedObject.hasValueFor(aInfo);
    }

}
