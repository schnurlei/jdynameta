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
package de.jdynameta.base.value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author rsc
 *
 */
@SuppressWarnings("serial")
public class ValueObjectKey implements Serializable
{
    private final List<Object> keyList;

    /**
     *
     */
    public ValueObjectKey()
    {
        super();
        this.keyList = new ArrayList<>();
    }

    public void addKey(Object aKey)
    {
        assert (aKey != null);
        this.keyList.add(aKey);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int hashCode = 0;

        Iterator myObjIter = keyList.iterator();
        while (myObjIter.hasNext())
        {
            hashCode ^= myObjIter.next().hashCode();
        }

        return hashCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object compareObj)
    {
        boolean isEqual = compareObj instanceof ValueObjectKey;

        Iterator myObjIter = keyList.iterator();
        Iterator compareObjIter = ((ValueObjectKey) compareObj).keyList.iterator();

        while (isEqual && myObjIter.hasNext() && compareObjIter.hasNext())
        {
            isEqual = myObjIter.next().equals(compareObjIter.next());
        }
        isEqual = isEqual && !myObjIter.hasNext() && !compareObjIter.hasNext();

        return isEqual;
    }

    @Override
    public String toString()
    {
        String result = "ValueObjectKey( ";
        result = keyList.stream().map((curKey) -> curKey.toString() + " ").reduce(result, String::concat);
        result += " )";
        return result;
    }
}
