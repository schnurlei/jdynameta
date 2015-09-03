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
/*
 * Created on 13.03.2004
 *
 */
package de.jdynameta.base.creation.db;

import java.io.Serializable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;

/**
 * Mapping from a ClassInfo Repository to one or more tables
 *
 * @author Rainer
 *
 */
public interface JdyRepositoryTableMapping extends Serializable
{

    public JDyClassInfoToTableMapping getTableMappingFor(ClassInfo aClassInfo);

    /**
     * get the attribute of the Class which is used to generate an unique id
     *
     * @param aClassInfo
     * @return null - no generic id is generated
     */
    public PrimitiveAttributeInfo getGeneratedIdAttribute(ClassInfo aClassInfo);

    public PrimitiveAttributeInfo getDiscriminatorAttribute(ClassInfo aClassInfo);

}
