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
package de.jdynameta.base.metainfo.filter.defaultimpl;

import java.io.Serializable;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * @author rsc
 *
 */
@SuppressWarnings("serial")
public class DefaultClassInfoQuery implements ClassInfoQuery, Serializable
{

    private final ClassInfo resultInfo;
    private ObjectFilterExpression filterExpression;

    public DefaultClassInfoQuery(ClassInfo aResultInfo)
    {
        assert (aResultInfo != null);
        this.resultInfo = aResultInfo;
    }

    public DefaultClassInfoQuery(ClassInfo aResultInfo, ObjectFilterExpression aFilterExpr)
    {
        assert (aResultInfo != null);
        this.resultInfo = aResultInfo;
        this.filterExpression = aFilterExpr;
    }

    /**
     * Returns the resultInfo.
     *
     * @return ClassInfo
     */
    @Override
    public ClassInfo getResultInfo()
    {
        return resultInfo;
    }

    /**
     *
     * @param aModel
     * @return
     * @throws JdyPersistentException
     */
    @Override
    public boolean matchesObject(ValueObject aModel) throws JdyPersistentException
    {
        return this.filterExpression == null
                || this.filterExpression.matchesObject(aModel);
    }

    /**
     * Returns the filterExpression.
     *
     * @return ObjectFilterExpression
     */
    @Override
    public ObjectFilterExpression getFilterExpression()
    {
        return this.filterExpression;
    }

    /**
     * @param aExpression
     */
    public void setFilterExpression(ObjectFilterExpression aExpression)
    {
        filterExpression = aExpression;
    }

}
