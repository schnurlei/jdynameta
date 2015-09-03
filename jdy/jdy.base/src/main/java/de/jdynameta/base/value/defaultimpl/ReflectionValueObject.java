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
package de.jdynameta.base.value.defaultimpl;

import java.lang.reflect.Method;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.AbstractAttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.AttributeNameCreator;
import de.jdynameta.base.value.ValueObject;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ReflectionValueObject implements ValueObject
{
    private AttributeNameCreator nameCreator;

    /**
     *
     */
    public ReflectionValueObject()
    {
        this(new ModelNameCreator());

    }

    /**
     *
     * @param aNameCreator
     */
    public ReflectionValueObject(AttributeNameCreator aNameCreator)
    {
        super();
        this.nameCreator = aNameCreator;
    }

    /**
     * @see de.jdynameta.base.value.ValueModel#getValue(AbstractAttributeInfo)
     */
    @Override
    public Object getValue(AttributeInfo aInfo)
    {
        try
        {
            Object result;

            if (aInfo instanceof PrimitiveAttributeInfo)
            {
                Class<? extends Object> me = this.getClass();
                Method getter = me.getMethod(this.nameCreator.getGetterNameFor(aInfo));
                result = getter.invoke(this, (Object[]) null);
            } else
            {
                Class<? extends Object> me = this.getClass();
                Method getter = me.getMethod(this.nameCreator.getGetterNameFor(aInfo));
                result = getter.invoke(this, (Object[]) null);
            }

            return result;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.jdynameta.base.value.ValueModel#getValue(AbstractAttributeInfo)
     */
    @Override
    public ObjectList<? extends ReflectionValueObject> getValue(AssociationInfo aInfo)
    {
        try
        {
            ObjectList<? extends ReflectionValueObject> assocColl;
            Class<? extends Object> me = this.getClass();
            Method getter = me.getMethod(this.nameCreator.getGetterNameFor(aInfo), (Class[]) null);
            assocColl = (ObjectList) getter.invoke(this, (Object[]) null);

//			ArrayList resultColl = new ArrayList(assocColl.size());
//			
//			for( Iterator collObjIter = assocColl.iterator(); collObjIter.hasNext();) {
//				
//				resultColl.add(new ReflectionValueObjectWrapper(collObjIter.next()));	
//			}
            return assocColl;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
           return null;
        }
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AssociationInfo)
     */
    @Override
    public boolean hasValueFor(AssociationInfo aInfo)
    {
        try
        {
            this.getClass().getMethod(this.nameCreator.getGetterNameFor(aInfo), (Class[]) null);
            return true;
        } catch (SecurityException | NoSuchMethodException excp)
        {
            excp.printStackTrace();
            return false;
        }
    }


    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AttributeInfo)
     */
    @Override
    public boolean hasValueFor(AttributeInfo aInfo)
    {
        try
        {
            this.getClass().getMethod(this.nameCreator.getGetterNameFor(aInfo), (Class[]) null);
            return true;
        } catch (SecurityException | NoSuchMethodException excp)
        {
            excp.printStackTrace();
            return false;
        }
    }

    /**
     * @return Returns the nameCreator.
     */
    protected AttributeNameCreator getNameCreator()
    {
        return this.nameCreator;
    }

    /**
     * @param aNameCreator The nameCreator to set.
     */
    protected void setNameCreator(AttributeNameCreator aNameCreator)
    {
        this.nameCreator = aNameCreator;
    }

}
