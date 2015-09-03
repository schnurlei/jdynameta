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
import java.util.Iterator;

/**
 * An ordered collection (also known as a <i>sequence</i>). The user can access
 * elements by their integer index (position in the list)
 * .<p>
 *
 * this collection allows duplicate elements. The user of this list can not
 * insert or delete Objects in this List. This is done by subclasses or in the
 * constructor
 *
 * @author Rainer
 * @param <TListType>
 *
 */
public interface ObjectList<TListType>
        extends Iterable<TListType>, Serializable
{

	// Positional Access Operations
    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     *
     * @throws IndexOutOfBoundsException if the index is out of range (index
     * &lt; 0 || index &gt;= size()).
     */
    TListType get(int index) throws ProxyResolveException;

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence.
     */
    @Override
    Iterator<TListType> iterator() throws ProxyResolveException;

	// Query Operations
    /**
     * Returns the number of elements in this list. If this list contains more
     * than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list.
     */
    int size() throws ProxyResolveException;

    int indexOf(TListType anObject) throws ProxyResolveException;
}
