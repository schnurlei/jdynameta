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
package de.jdynameta.base.model;

import java.io.Serializable;
import java.util.EventListener;
import java.util.EventObject;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;

public interface PersistentObjectReader<TReadedObj> extends Serializable
{

    public enum ModifierType
    {
        OBJECT_CREATED, OBJECT_MODIFIED, OBJECT_DELETED
    }

    /**
     * Read list of Object from the db for the given Query
     *
     * @param aFilter
     * @return
     * @throws JdyPersistentException
     */
    public abstract ObjectList<TReadedObj> loadObjectsFromDb(ClassInfoQuery aFilter) throws JdyPersistentException;

    public abstract void addListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener);

    public abstract void removeListener(ClassInfo aClassInfo, PersistentListener<TReadedObj> aListener);

    /**
     *
     * PersistentEvent Copyright 2006 Rainer Schneider, 89297 Roggenburg
     * @param <TReadedObj>
     */
    @SuppressWarnings("serial")
    public static class PersistentEvent<TReadedObj> extends EventObject
    {
        // class which gets the notification, ( could be the changed class or a super class of it)
        private ClassInfo baseClass;
        // type of the changed object
        private final ClassInfo changedClass;
        private final TReadedObj changedObject;
        private final ModifierType state;

        public PersistentEvent(Object aSource, ClassInfo aBaseClass, ClassInfo aChangedClass, TReadedObj aChangedObject, ModifierType aState)
        {
            super(aSource);
            this.changedObject = aChangedObject;
            this.changedClass = aChangedClass;
            this.state = aState;
        }

        /**
         * @return
         */
        public TReadedObj getChangedObject()
        {
            return changedObject;
        }

        /**
         * @return
         */
        public ModifierType getState()
        {
            return state;
        }

        public ClassInfo getChangedClass()
        {
            return changedClass;
        }

        public ClassInfo getBaseClass()
        {
            return baseClass;
        }

    }

    /**
     *
     * PersistentListener
     *
     * @author <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
     * Copyright 2006 Rainer Schneider, 89297 Schiessen, Roggenburg
     * @param <TCreatedFromValueObj>
     */
    public static interface PersistentListener<TCreatedFromValueObj> extends EventListener, Serializable
    {
        public void persistentStateChanged(PersistentEvent<TCreatedFromValueObj> aEvent);
    }

}
