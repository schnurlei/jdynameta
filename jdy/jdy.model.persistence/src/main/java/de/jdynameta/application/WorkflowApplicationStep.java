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

import de.jdynameta.base.value.ValueObject;

/**
 * Defines an Step of an Workflow in the Application
 *
 * @author Rainer Schneider
 * @param <TWorkflObj>
 *
 */
public interface WorkflowApplicationStep<TWorkflObj extends ValueObject>
{
    /**
     * Execute this step of the workflow on the given Object
     *
     * @param anObjectToProcess
     * @throws de.jdynameta.application.WorkflowException
     */
    public void executeOn(TWorkflObj anObjectToProcess) throws WorkflowException;

    /**
     * Get the name Resource of this step of the workflow
     *
     * @return
     */
    public String getNameResource();

    /**
     * Is this step possible on the current state of the object
     *
     * @param anObjectToProcess
     * @return 
     */
    public boolean isPossible(ValueObject anObjectToProcess);

}
