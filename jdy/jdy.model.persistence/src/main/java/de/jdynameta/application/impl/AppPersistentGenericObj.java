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
package de.jdynameta.application.impl;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.persistence.state.ApplicationModel;

/**
 * Convenience class to define a PersistentValueObjectImpl which holds a
 * GenericValueObject
 *
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class AppPersistentGenericObj extends ApplicationModel<GenericValueObjectImpl>
{

    public AppPersistentGenericObj(ClassInfo aClassInfo, GenericValueObjectImpl aWrappedValueObject, boolean isNew, PersistentObjectReader<AppPersistentGenericObj> persistentReader)
    {
        super(aClassInfo, aWrappedValueObject, isNew, persistentReader);
    }

    @Override
    public AppPersistentGenericObj createModelFor(ClassInfo aClassInfo, GenericValueObjectImpl persistentObj, boolean isNew)
    {
        return new AppPersistentGenericObj(aClassInfo, persistentObj, isNew, (PersistentObjectReader<AppPersistentGenericObj>) getPersistentReader());
    }

}
