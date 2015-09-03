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
import de.jdynameta.base.value.ChangeableValueObject;

public class WrappedChangeableValueObject implements ChangeableValueObject
{
    private final ChangeableValueObject wrappedObject;

    /**
     * @param aWrappedObject
     */
    public WrappedChangeableValueObject(ChangeableValueObject aWrappedObject)
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

    @Override
    public ObjectList<? extends ChangeableValueObject> getValue(AssociationInfo aInfo)
    {
        return wrappedObject.getValue(aInfo);
    }

    @Override
    public boolean hasValueFor(AssociationInfo aInfo)
    {
        return wrappedObject.hasValueFor(aInfo);
    }

    @Override
    public void setValue(AttributeInfo aInfo, Object aValue)
    {
        wrappedObject.setValue(aInfo, aValue);

    }

}
