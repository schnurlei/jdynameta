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

import de.jdynameta.persistence.state.PersistentValueObject;

public class WorkflowPersistenceHookAdaptor<TEditedApplicObj extends PersistentValueObject>
        implements WorkflowPersistenceHook<TEditedApplicObj>
{

    @Override
    public void afterCreate(TEditedApplicObj createdObject)
    {
    }

    @Override
    public void beforeSave(TEditedApplicObj editedObject)
            throws WorkflowException
    {
    }

    @Override
    public void afterSave(TEditedApplicObj editedObject, boolean wasNew)
    {

    }

    ;

    @Override
    public void beforeDelete(TEditedApplicObj objToDelete) throws WorkflowException
    {
    }

    @Override
    public void afterDelete(TEditedApplicObj deleteObj) throws WorkflowException
    {
    }
;

}
