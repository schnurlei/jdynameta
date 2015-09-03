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

import java.util.EventObject;
import java.util.List;

/**
 * Event that defines a change to a OjectListModel
 *
 * @author Rainer
 * @param <TListObj>
 *
 */
@SuppressWarnings("serial")
public class ObjectListModelEvent<TListObj> extends EventObject
{
    private final int lowerIndex;
    private final int upperIndex;
    private List<TListObj> removedObjects;

    /**
     *
     * @param aSource
     * @param aLowerIndex
     * @param aUpperIndex
     */
    public ObjectListModelEvent(ObjectListModel<TListObj> aSource, int aLowerIndex, int aUpperIndex)
    {
        super(aSource);

        this.lowerIndex = aLowerIndex;
        this.upperIndex = aUpperIndex;
    }

    public ObjectListModelEvent(ObjectListModel<TListObj> aSource, int aLowerIndex, int aUpperIndex, List<TListObj> allRemovedObj)
    {
        super(aSource);

        this.lowerIndex = aLowerIndex;
        this.upperIndex = aUpperIndex;
        this.removedObjects = allRemovedObj;
    }

    /**
     * @return
     */
    public int getLowerIndex()
    {
        return lowerIndex;
    }

    /**
     * @return
     */
    public int getUpperIndex()
    {
        return upperIndex;
    }

    public List<TListObj> getRemovedObjects()
    {
        return this.removedObjects;
    }

}
