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
package de.jdynameta.base.view.appinfo;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.action.WorkflowAction;

public interface ViewClassInfo<TEditObj extends ViewObject> extends ClassInfo
{

    /**
     * Get the possible next workflow steps for the given object It is
     * application dependent, if all Steps are returned or only the possible.
     * Not possible steps could also be marked by the Method
     * {@link de.jdynameta.persistence.workflow.WorkflowApplicationStep.isPossible isPossible}
     * in ApplicationWorkflowStep
     *
     * @param obj the valueobject for which the next steps are returned or null
     * to get the initial steps
     * @return a List of workflow steps or null if not steps are defined
     */
    public ObjectList<WorkflowAction<TEditObj>> getWorkflowActionsFor(ValueObject obj);

    /**
     * Check whether an Attribute is editable in a specific state of an object
     * in a defined context
     *
     * @param obj object of type aClassInfo to get the state from
     * @param aAttributeInfo attribute of classinfo to check
     * @param context Context of the application for that we check
     * @return
     */
    public boolean isAttributeEditable(TEditObj obj, AttributeInfo aAttributeInfo, ViewCtxt context);

    public boolean isAssociationEditable(TEditObj obj, AssociationInfo aAttributeInfo, ViewCtxt context);

//	public void beforeSave(TEditObj editedObject) throws WorkflowException;
//	public void afterSave(TEditObj editedObject);
//	public void afterCreate(TEditObj createdObject);
//	public void beforeDelete(TEditObj objToDelete) throws WorkflowException;
}
