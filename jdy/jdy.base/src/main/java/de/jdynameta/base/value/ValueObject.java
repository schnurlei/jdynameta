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
import de.jdynameta.base.objectlist.ObjectList;

/**
 * A ValueObject is an Object which Attributes could retrieved using an
 * {@link de.comafra.model.metainfo.AttributesInfo AttributesInfo}. A
 * ValueObject could be used as Superclass, Wrapper oder as ReflectionHandler
 * for other classes to get their values.
 *
 * @author Rainer
 *
 */
public interface ValueObject
{
    /**
     * Get the value of the object for the given Attribute info
     *
     * @param aInfo
     * @return
     */
    public Object getValue(AttributeInfo aInfo);

    /**
     * Check, if the ValueObject has an Valid Value for the given AttributeInfo
     * Either the value has not been set in the Model or the Object does not
     * belong to the ClassInfo of the Attribute 
	 *
     */
    public boolean hasValueFor(AttributeInfo aInfo);

    /**
     * Get the value of the object for the given AssociationInfo info
     *
     * @param aInfo
     * @return
     */
    public ObjectList<? extends ValueObject> getValue(AssociationInfo aInfo);

    /**
     * Check, if the ValueObject has an Valid Value for the given
     * AssociationInfo Either the value has not been set in the Model or the
     * Object does not belong to the ClassInfo of the AssociationInfo 
	 *
     */
    public boolean hasValueFor(AssociationInfo aInfo);

}
