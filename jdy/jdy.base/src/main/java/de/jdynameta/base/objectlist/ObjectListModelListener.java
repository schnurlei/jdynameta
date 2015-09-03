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
 * @param <T>
 *
 */
public interface ObjectListModelListener<T>
{
    /**
     * Sent after the indices in the index0,index1 interval have been inserted
     * in the data model. The new interval includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    void intervalAdded(ObjectListModelEvent<T> e);

    /**
     * Sent after the indices in the index0,index1 interval have been removed
     * from the data model. The interval includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    void intervalRemoved(ObjectListModelEvent<T> e);

    /**
     * Sent when the contents an list element has changed Index0 and index1
     * bracket the change.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    void intervalUpdated(ObjectListModelEvent<T> e);

    /**
     * Sent when the structure of an an list has changed Index0 and index1
     * bracket the change.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the event information
     */
    void contentsChanged(ObjectListModelEvent<T> e);

}
