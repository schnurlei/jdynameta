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

/**
 * @author Rainer
 * @param <TListType>
 *
 */
public interface ObjectListModel<TListType> extends ObjectList<TListType>
{

    /**
     * Adds a listener to the list that's notified each time a change to the
     * data model occurs.
     *
     * @param aListener
     * @paramaListener the <code>ObjectListModelListener</code> to be added
     */
    void addObjectListModelListener(ObjectListModelListener<TListType> aListener);

    /**
     * Removes a listener from the list that's notified each time a change to
     * the data model occurs.
     *
     * @param aListener the <code>ObjectListModelListener</code> to be removed
     */
    void removeObjectListModelListener(ObjectListModelListener<TListType> aListener);

}
