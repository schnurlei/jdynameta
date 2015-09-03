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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ValueObject;

/**
 * Provides Information of the Workflow for ApplicationObejcts, like the next
 * Workflow stepes or whether the object and its fields are editable in a
 * specific state
 *
 * @author Rainer Schneider
 *
 * @param <TWorkflObj>
 */
public interface WorkflowManager<TWorkflObj extends ValueObject>
{
    /**
     * Get the possible next workflow steps for the given object It is
     * application dependent, whether all Steps are returned or only the
     * possible. Not possible steps could also be marked by the Method
     * {@link de.jdynameta.persistence.workflow.WorkflowApplicationStep.isPossible isPossible}
     * in ApplicationWorkflowStep
     *
     * @param obj the valueobject for which the next steps are returned or null
     * to get the initial steps
     * @param aClassInfo
     * @return a List of workflow steps or null if not steps are defined
     */
    public ObjectList<WorkflowApplicationStep<TWorkflObj>> getWorkflowStepsFor(ValueObject obj, ClassInfo aClassInfo);

    /**
     * Check whether an Attribute is editable in a specific state of an object
     * in a defined context
     *
     * @param obj object of type aClassInfo to get the state from
     * @param aClassInfo type of the Object to check
     * @param aAttributeInfo attribute of classinfo to check
     * @param context Context of the application for that we check
     * @return
     */
    public boolean isAttributeEditable(ValueObject obj, ClassInfo aClassInfo, AttributeInfo aAttributeInfo, WorkflowCtxt context);

    public boolean isAssociationEditable(ValueObject obj, ClassInfo aClassInfo, AssociationInfo aAttributeInfo, WorkflowCtxt context);

    /**
     * check whether the attribute is visible in the given context
     *
     * @param aAttrInfo
     * @param aClassInfo
     * @param context
     * @return
     */
    public boolean isAttributeVisible(AttributeInfo aAttrInfo, final ClassInfo aClassInfo, WorkflowCtxt context);

    /**
     * check whether the association is visible in the given context
     *
     * @param aAttrInfo
     * @param aClassInfo
     * @param context
     * @return
     */
    public boolean isAssociationVisible(AssociationInfo aAssocInfo, final ClassInfo aClassInfo, WorkflowCtxt context);

}
