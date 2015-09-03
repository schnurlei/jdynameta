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
package de.jdynameta.persistence.limbus;

import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;

/**
 * Cache the results of queries to the db, to improve the performance of later
 * queries to the DB for the same objects
 *
 * @author Rainer
 * @param <T_ValueObjectClass>
 *
 */
public interface QueryCache<T_ValueObjectClass>
{
    /**
     * Test if all Objects queried by the given filter are in the Cache
     *
     * @param aFilter filter to check for
     * @return <tt>true</tt> if filter query could be satiesfied by this cache
     */
    public boolean areAllObjectsInCache(ClassInfoQuery aFilter);

    /**
     * returns alle Objects in the Cache for the specified Query
     *
     * @param aFilter
     * @return List of alle Objects in the Cache
     */
    public ObjectList<T_ValueObjectClass> getObjectsForFilter(ClassInfoQuery aFilter);

    public void insertObjectsForFilter(ClassInfoQuery aFilter, ObjectList<T_ValueObjectClass> aList);

    public void addNewOject(T_ValueObjectClass aNewCreatedObject);

    public void removeDeletedOject(T_ValueObjectClass aDeletedObjectKey);

    public KeyGenerator<T_ValueObjectClass> getKeyGenerator();

    public void setKeyGenerator(KeyGenerator< T_ValueObjectClass> aGenerator);

}
