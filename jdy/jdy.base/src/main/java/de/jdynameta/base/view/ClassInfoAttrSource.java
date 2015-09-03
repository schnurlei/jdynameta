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
package de.jdynameta.base.view;

import java.io.Serializable;
import java.util.List;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * Delivers the Attributes for a ClassInfo used to display the Class in a List,
 * Table Cell, ...
 *
 * @author Rainer Schneider
 *
 */
public interface ClassInfoAttrSource extends Serializable
{
    public List<AttributeInfo> getDisplayAttributesFor(ClassInfo aClassInfo);

}
