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
package de.jdynameta.persistence.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.model.PersistentObjectReader;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModelEvent;
import de.jdynameta.base.objectlist.ObjectListModelListener;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.objectlist.ObjectFilter;

/**
 * @author Rainer
 * @param <TPersistentObject>
 *
 */
@SuppressWarnings("serial")
public class AssociationInfoListModel<TPersistentObject> implements ObjectListModel<TPersistentObject>
{
    private final PersistentObjectReader.PersistentListener<TPersistentObject> persistentListener;
    private final List<TPersistentObject> allObjects;
    protected final ArrayList<ObjectListModelListener<TPersistentObject>> listenerList;
    private final ObjectList<TPersistentObject> wrappedObjectList;
    private final ObjectFilter<TPersistentObject> filter;

    /**
     *
     * @param aPersistentReader
     * @param aAssocInfo
     * @param aWrappedList
     * @param aParent
     */
    public AssociationInfoListModel(PersistentObjectReader<TPersistentObject> aPersistentReader, final AssociationInfo aAssocInfo, ObjectList<TPersistentObject> aWrappedList, final Object aParent) throws ProxyResolveException
    {
        super();

        this.listenerList = new ArrayList<>();

        this.allObjects = new ArrayList<>(aWrappedList.size());
        wrappedObjectList = aWrappedList;

        this.filter = new ObjectFilter<TPersistentObject>()
        {
            @Override
            public boolean accept(Object object)
            {
                return aParent.equals(((ValueObject) object).getValue(aAssocInfo.getMasterClassReference()));
            }
        };

        this.persistentListener = (PersistentObjectManager.PersistentEvent<TPersistentObject> aEvent) ->
        {
            switch (aEvent.getState())
            {
                case OBJECT_CREATED:
                    if (filter.accept(aEvent.getChangedObject()))
                    {
                        allObjects.add(aEvent.getChangedObject());
                        fireIntervalAdded(AssociationInfoListModel.this, allObjects.size(), allObjects.size());
                    }
                    break;
                case OBJECT_DELETED:
                    int index = allObjects.indexOf(aEvent.getChangedObject());
                    if (index >= 0)
                    {
                        allObjects.remove(aEvent.getChangedObject());
                        fireIntervalRemoved(AssociationInfoListModel.this, index, index);
                    }
                    break;
                case OBJECT_MODIFIED:
                    int modIdx = allObjects.indexOf(aEvent.getChangedObject());
                    if (modIdx >= 0)
                    {
                        fireIntervalUpdated(AssociationInfoListModel.this, modIdx, modIdx);
                    }
                    break;
            }
            aEvent.getState();
        };

        aPersistentReader.addListener(aAssocInfo.getDetailClass(), persistentListener);
    }

    /**
     * Retrieves the filter.
     *
     * @return	The filter currently applied to the model.
     */
    public ObjectFilter<TPersistentObject> getFilter()
    {
        return filter;
    }

    /**
     * Refreshes the collection.
     */
    protected void refresh() throws ProxyResolveException
    {
        this.allObjects.clear();
        this.fireIntervalRemoved(this, 0, this.allObjects.size() - 1);

        if (this.wrappedObjectList != null)
        {
            for (Iterator<TPersistentObject> iterator = this.wrappedObjectList.iterator(); iterator.hasNext();)
            {
                TPersistentObject object = iterator.next();
                if (this.filter == null || this.filter.accept(object))
                {
                    this.addToAllObjects(object);
                }
            }
        }

        this.fireIntervalAdded(this, 0, this.allObjects.size() - 1);
    }

    /**
     * Sychronize access to allObjects
     *
     * @param anObject
     */
    private synchronized void addToAllObjects(TPersistentObject anObject)
    {
        this.allObjects.add(anObject);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#get(int)
     */
    public TPersistentObject get(int index)
    {
        return this.allObjects.get(index);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#iterator()
     */
    public Iterator<TPersistentObject> iterator()
    {
        return this.allObjects.iterator();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#size()
     */
    public int size()
    {
        return this.allObjects.size();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#indexOf(java.lang.Object)
     */
    public int indexOf(Object anObject)
    {
        return this.allObjects.indexOf(anObject);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectListModel#addObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
     */
    public void addObjectListModelListener(ObjectListModelListener<TPersistentObject> aListener)
    {
        listenerList.add(aListener);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
     */
    public void removeObjectListModelListener(ObjectListModelListener<TPersistentObject> aListener)
    {
        listenerList.remove(aListener);
    }

    protected final void fireIntervalAdded(ObjectListModel<TPersistentObject> source, int index0, int index1)
    {
        ObjectListModelEvent<TPersistentObject> event = new ObjectListModelEvent<TPersistentObject>(source, index0, index1);
        for (ObjectListModelListener<TPersistentObject> curListener : listenerList)
        {
            curListener.intervalAdded(event);
        }
    }

    protected final void fireIntervalRemoved(ObjectListModel<TPersistentObject> source, int index0, int index1)
    {
        ObjectListModelEvent<TPersistentObject> event = new ObjectListModelEvent<TPersistentObject>(source, index0, index1);
        for (ObjectListModelListener<TPersistentObject> curListener : listenerList)
        {
            curListener.intervalRemoved(event);
        }
    }

    protected final void fireIntervalUpdated(ObjectListModel<TPersistentObject> source, int index0, int index1)
    {
        ObjectListModelEvent<TPersistentObject> event = new ObjectListModelEvent<TPersistentObject>(source, index0, index1);
        for (ObjectListModelListener<TPersistentObject> curListener : listenerList)
        {
            curListener.intervalUpdated(event);
        }

    }

}
