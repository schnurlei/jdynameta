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
package de.jdynameta.persistence.impl.proxy;

import java.io.Serializable;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.FilterUtil;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;

/**
 * Resolves the Proxy by reading the value to resolve from the
 * PersistentConnection
 *
 * @author Rainer
 * @param <TTransformToValueObj>
 * @param <TCreatedFromValueObj>
 *
 */
@SuppressWarnings("serial")
public class SimpleProxyResolver<TTransformToValueObj, TCreatedFromValueObj> implements ProxyResolver, Serializable
{
    private DbAccessConnection<TTransformToValueObj, TCreatedFromValueObj> connection;

    /**
     *
     * @param aConnection
     */
    public SimpleProxyResolver(DbAccessConnection<TTransformToValueObj, TCreatedFromValueObj> aConnection)
    {
        super();
        this.connection = aConnection;
    }

    public void setConnection(DbAccessConnection<TTransformToValueObj, TCreatedFromValueObj> aConnection)
    {
        this.connection = aConnection;
    }

    @Override
    public void resolveProxy(TypedValueObject aValueModel) throws ObjectCreationException
    {
        try
        {
            DefaultClassInfoQuery filter = FilterUtil.createSearchEqualObjectFilter(aValueModel.getClassInfo(), aValueModel);
            this.connection.loadValuesFromDb(filter);
//			this.connection = null;

        } catch (JdyPersistentException excp)
        {
            throw new ObjectCreationException(excp);
        }
    }

//	public ObjectList resolveProxyList(AssociationInfo assocInfo, final ValueObject aMasterObject)
//	{
//
//		ObjectReferenceEqualExpression aResolveExpression = new DefaultObjectReferenceEqualExpression(assocInfo.getMasterClassReference(), aMasterObject);
//		
//		DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocInfo.getDetailClass());
//		filter.setFilterExpression(aResolveExpression);
//		
//		return null ; // new PersistentObjectListModel(this.objectManager, filter);
//	}
}
