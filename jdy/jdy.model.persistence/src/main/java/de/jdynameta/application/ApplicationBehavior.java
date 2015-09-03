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
package de.jdynameta.application;

import java.io.Serializable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.view.ClassInfoAttrSource;

/**
 * Handles application behavior independent of the GUI
 *
 * @author Rainer Schneider
 * @param <TEditObj>
 */
public interface ApplicationBehavior<TEditObj extends ChangeableTypedValueObject>
        extends ClassInfoAttrSource, Serializable
{

    /**
     * Create a new Object for the given ClassInfo
     *
     * @param aClassInfo
     * @param aContext TODO
     * @return
     * @throws ObjectCreationException
     */
    public TEditObj createObject(ClassInfo aClassInfo, WorkflowCtxt aContext) throws ObjectCreationException;

    /**
     * Get a Workflow manager which handles the Workflow steps and the
     * Customability of the Attributes
     *
     * @return
     */
    public WorkflowManager<TEditObj> getWorkflowManager();

}
