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
package de.jdynameta.persistence.impl.persistentobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.jdynameta.persistence.state.EditablePersistentStateModel;
import de.jdynameta.persistence.state.PersistentStateModel;

/**
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class PersistentStateModelImpl implements EditablePersistentStateModel, Serializable
{
    private boolean isNew;
    private boolean isDirty;
    private boolean isMarkedAsDeleted;
    private final List<PersistentStateModel.Listener> listenerList;

    /**
     *
     */
    public PersistentStateModelImpl()
    {
        super();
        this.listenerList = new ArrayList<>();
        this.isNew = true;
        this.isDirty = false;
        this.isMarkedAsDeleted = false;
    }

    @Override
    public void addPersistenceListener(Listener aStateListener)
    {
        listenerList.add(aStateListener);
    }

    @Override
    public void removePersistenceListener(Listener aStateListener)
    {
        listenerList.remove(aStateListener);
    }

    @Override
    public boolean isDirty()
    {
        return this.isDirty;
    }

    @Override
    public boolean isNew()
    {
        return this.isNew;
    }

    @Override
    public void setState(boolean aIsNewFlag, boolean aIsDirtyFlag)
    {
        this.isNew = aIsNewFlag;
        this.isDirty = aIsDirtyFlag;
        firePerstistenceChanged();
    }

    public void setIsDirty(boolean aFlag)
    {
        this.isDirty = aFlag;
        firePerstistenceChanged();
    }

    @Override
    public boolean isMarkedAsDeleted()
    {
        return isMarkedAsDeleted;
    }

    @Override
    public void setMarkedAsDeleted(boolean isMarkedAsDeleted)
    {
        this.isMarkedAsDeleted = isMarkedAsDeleted;
    }

    protected void firePerstistenceChanged()
    {

        listenerList.stream().forEach((curListener) ->
        {
            curListener.stateChanged();
        });
    }

}
