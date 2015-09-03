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
 * ChangeableValueObject
 *
 * @author <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 * Copyright 2006 Rainer Schneider, 89297 Schiessen, Roggenburg
 */
public interface ChangeableValueObject extends ValueObject
{

    /**
     * Set the value for the given AttributeInfo
     *
     * @param aInfo
     * @param value
     */
    public void setValue(AttributeInfo aInfo, Object value);

    @Override
    public ObjectList<? extends ChangeableValueObject> getValue(AssociationInfo info);
}
