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
package de.jdynameta.base.objectlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Rainer
 * @param <TListObj>
 *
 */
@SuppressWarnings("serial")
public class DefaultObjectListModel<TListObj>
        implements ObjectListModel<TListObj>, Serializable
{
    private final List<TListObj> allObjects;
    protected final ArrayList<ObjectListModelListener<TListObj>> listenerList;

    /**
     *
     * @param aListContent
     */
    public DefaultObjectListModel(List<TListObj> aListContent)
    {
        super();

        this.listenerList = new ArrayList<>();

        this.allObjects = new ArrayList<>(aListContent != null ? aListContent.size() : 10);

        setListContent(aListContent);
    }

    /**
     *
     * @param aListContent
     */
    public DefaultObjectListModel(ObjectList<TListObj> aListContent)
    {
        super();

        this.listenerList = new ArrayList<>();

        this.allObjects = new ArrayList<>(aListContent != null ? aListContent.size() : 10);

        setListContent(aListContent);
    }

    protected List<TListObj> getAllObjects()
    {
        return allObjects;
    }

    /**
     * @return
     */
    private void setListContent(List<TListObj> aListContent) throws ProxyResolveException
    {
        this.allObjects.clear();
//		this.fireIntervalRemoved(this, 0, this.allObjects.size()-1);

        if (aListContent != null)
        {
            for (Iterator<TListObj> iterator = aListContent.iterator(); iterator.hasNext();)
            {
                TListObj curObject = iterator.next();
                this.allObjects.add(curObject);
            }
        }

        this.fireContentsChanged(this, 0, this.allObjects.size() - 1);

    }

    /**
     * @return
     */
    private void setListContent(ObjectList<TListObj> aListContent) throws ProxyResolveException
    {
        this.allObjects.clear();

        if (aListContent != null)
        {
            for (Iterator<TListObj> iterator = aListContent.iterator(); iterator.hasNext();)
            {
                TListObj curObject = iterator.next();
                this.allObjects.add(curObject);
            }
        }

        this.fireContentsChanged(this, 0, this.allObjects.size() - 1);

    }

    /**
     * Synchronize access to allObjects
     *
     * @param anObject
     */
    protected synchronized void addToListContent(TListObj anObject)
    {
        this.allObjects.add(anObject);
        fireIntervalAdded(this, this.allObjects.size() - 1, this.allObjects.size() - 1);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#get(int)
     */
    @Override
    public TListObj get(int index)
    {
        return this.allObjects.get(index);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#iterator()
     */
    @Override
    public Iterator<TListObj> iterator()
    {
        return this.allObjects.iterator();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#size()
     */
    @Override
    public int size()
    {
        return this.allObjects.size();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectList#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(TListObj anObject)
    {
        return this.allObjects.indexOf(anObject);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectListModel#addObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
     */
    @Override
    public void addObjectListModelListener(ObjectListModelListener<TListObj> aListener)
    {
        listenerList.add(aListener);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.objectlist.ObjectListModel#removeObjectListModelListener(de.comafra.model.objectlist.ObjectListModelListener)
     */
    @Override
    public void removeObjectListModelListener(ObjectListModelListener<TListObj> aListener)
    {
        listenerList.remove(aListener);
    }

    protected void fireContentsChanged(ObjectListModel<TListObj> source, int index0, int index1)
    {
        ObjectListModelEvent<TListObj> event = null;
        for (ObjectListModelListener<TListObj> curListener : listenerList)
        {
            if (event == null)
            {
                event = new ObjectListModelEvent<>(source, index0, index1);
            }
            curListener.contentsChanged(event);
        }
    }

    protected void fireIntervalAdded(ObjectListModel<TListObj> source, int index0, int index1)
    {
        ObjectListModelEvent<TListObj> event = null;
        for (ObjectListModelListener<TListObj> curListener : listenerList)
        {
            if (event == null)
            {
                event = new ObjectListModelEvent<>(source, index0, index1);
            }
            curListener.intervalAdded(event);
        }

    }

    protected void fireIntervalRemoved(ObjectListModel<TListObj> source, int index0, int index1, List<TListObj> removedObj)
    {
        ObjectListModelEvent<TListObj> event = null;
        for (ObjectListModelListener<TListObj> curListener : listenerList)
        {
            if (event == null)
            {
                event = new ObjectListModelEvent<>(source, index0, index1, removedObj);
            }
            curListener.intervalRemoved(event);
        }
    }
}
