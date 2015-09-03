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
 *
 * List wich can not be changed be external objects
 *
 * @author Rainer
 * @param <T>
 *
 */
@SuppressWarnings("serial")
public class DefaultObjectList<T>
        implements ObjectList<T>, Serializable
{
    private final List<T> allObjects;

    public DefaultObjectList()
    {
        this.allObjects = new ArrayList<>();
    }

    public DefaultObjectList(T... allElements)
    {
        this.allObjects = new ArrayList<>();
        for (T curElem : allElements)
        {
            this.allObjects.add(curElem);
        }
    }

    public DefaultObjectList(List<T> aList)
    {
        this.allObjects = aList;
    }

    public DefaultObjectList(ObjectList<T> aList) throws ProxyResolveException
    {
        this.allObjects = new ArrayList<>(aList.size());

        for (Iterator<T> objectIter = aList.iterator(); objectIter.hasNext();)
        {
            this.allObjects.add(objectIter.next());
        }
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ObjectList#get(int)
     */
    @Override
    public T get(int index)
    {
        return this.allObjects.get(index);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ObjectList#iterator()
     */
    @Override
    public Iterator<T> iterator()
    {
        return this.allObjects.iterator();
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ObjectList#size()
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
    public int indexOf(T anObject)
    {
        return this.allObjects.indexOf(anObject);
    }

    protected List<T> getAllObjects()
    {
        return allObjects;
    }
}
