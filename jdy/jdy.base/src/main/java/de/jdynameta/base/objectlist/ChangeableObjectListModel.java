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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class ChangeableObjectListModel<TListObj> extends DefaultObjectListModel<TListObj>
{
    public ChangeableObjectListModel()
    {
        super(new ArrayList<>());
    }

    public ChangeableObjectListModel(List<TListObj> listContent)
    {
        super(listContent);
    }

    /**
     * Sychronize access to allObjects
     *
     * @param anObject
     */
    public synchronized void removeFromListContent(TListObj anObject)
    {
        int index = indexOf(anObject);
        if (index >= 0)
        {
            this.getAllObjects().remove(anObject);
            fireIntervalRemoved(this, index, index, Collections.singletonList(anObject));
        }
    }

    @Override
    public synchronized void addToListContent(TListObj anObject)
    {
        super.addToListContent(anObject);
    }

    public List<TListObj> getAllObjectsAsArray()
    {
        return new ArrayList<>(super.getAllObjects());
    }
}
