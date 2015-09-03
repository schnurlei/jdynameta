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

import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.persistence.state.PersistentValueObject;

public interface ApplicationHook<TEditedApplicObj extends PersistentValueObject>
{
    public void beforeSave(TEditedApplicObj editedObject) throws JdyPersistentException;

    public void afterSave(TEditedApplicObj editedObject, boolean wasNew) throws JdyPersistentException;

    public void afterCreate(TEditedApplicObj createdObject);

    public void beforeDelete(TEditedApplicObj objToDelete) throws JdyPersistentException;

    public void afterDelete(TEditedApplicObj deleteObj) throws JdyPersistentException;

}
