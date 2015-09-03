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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ChangeableValueObject;

/**
 * A Value Model which keeps its values in a HashMap.
 *
 * @author rsc
 *
 */
@SuppressWarnings("serial")
public class HashedValueObject
        implements ChangeableValueObject, Serializable
{

    private final HashMap<AttributeInfo, Object> infoNameToValue = new HashMap<>();
    private final HashMap<AssociationInfo, ObjectList<? extends ChangeableTypedValueObject>> assocToValue = new HashMap<>();

    @Override
    public Object getValue(AttributeInfo aInfo)
    {
        return infoNameToValue.get(aInfo);
    }

    @Override
    public void setValue(AttributeInfo aInfo, Object value)
    {
        infoNameToValue.put(aInfo, value);
    }

    protected void removeValue(AttributeInfo aInfo)
    {
        infoNameToValue.remove(aInfo);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AttributeInfo)
     */
    @Override
    public boolean hasValueFor(AttributeInfo aInfo)
    {
        return infoNameToValue.containsKey(aInfo);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#getValue(de.comafra.model.metainfo.AssociationInfo)
     */
    @Override
    public ObjectList<? extends ChangeableTypedValueObject> getValue(AssociationInfo aInfo)
    {
        return assocToValue.get(aInfo);
    }

    public void setValue(AssociationInfo aInfo, ObjectList<? extends ChangeableTypedValueObject> value)
    {
        assocToValue.put(aInfo, value);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AssociationInfo)
     */
    @Override
    public boolean hasValueFor(AssociationInfo aInfo)
    {
        return assocToValue.containsKey(aInfo);
    }

    protected void clearAllValues()
    {
        infoNameToValue.clear();
        assocToValue.clear();
    }

    protected void clearAssociationValues()
    {
        assocToValue.clear();
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        for (Entry<AttributeInfo, Object> curEntry : infoNameToValue.entrySet())
        {
            result.append(curEntry.getKey().getInternalName());
            result.append(" - ");
            result.append(curEntry.getValue() == null ? "" : curEntry.getValue().toString());
            result.append("\n");
        }

        return result.toString();
    }

}
