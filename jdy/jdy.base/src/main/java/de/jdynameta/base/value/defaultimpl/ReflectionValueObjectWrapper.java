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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.AbstractAttributeInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ValueObject;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ReflectionValueObjectWrapper implements ValueObject
{
    private final Object wrappedObject;

    /**
     *
     * @param aObject
     */
    public ReflectionValueObjectWrapper(Object aObject)
    {
        super();
        assert (aObject != null);
        wrappedObject = aObject;
    }

    public Object getWrappedObject()
    {
        return wrappedObject;
    }

    /**
     * @see de.jdynameta.base.value.ValueModel#getValue(AbstractAttributeInfo)
     */
    @Override
    public Object getValue(AttributeInfo aInfo)
    {
        try
        {
            Object result = null;

            if (wrappedObject != null)
            {
                if (aInfo instanceof PrimitiveAttributeInfo)
                {
                    Class<? extends Object> me = wrappedObject.getClass();
                    Method getter = me.getMethod("get" + stringWithFirstLetterUppercase(aInfo.getInternalName()), (Class[]) null);
                    result = getter.invoke(wrappedObject, (Object[]) null);
                } else
                {
                    Class<? extends Object> me = wrappedObject.getClass();
                    Method getter = me.getMethod("get" + stringWithFirstLetterUppercase(aInfo.getInternalName()), (Class[]) null);
                    result = getter.invoke(wrappedObject, (Object[]) null);

                    if (!(result instanceof ValueObject) && result != null)
                    {
                        result = new ReflectionValueObjectWrapper(result);
                    }
                }
            }

            return result;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param aInfo
     * @return 
     * @see de.jdynameta.base.value.ValueModel#getValue(AbstractAttributeInfo)
     */
    public Collection<Object> getWrappedCollFor(AssociationInfo aInfo)
    {
        try
        {
            ChangeableObjectList<ValueObject> resultColl = new ChangeableObjectList<>();
            Class<? extends Object> me = wrappedObject.getClass();
            Method getter = me.getMethod("get" + stringWithFirstLetterUppercase(aInfo.getDetailClass().getInternalName()) + "Coll", (Class[]) null);
            Collection<Object> assocColl = (Collection<Object>) getter.invoke(wrappedObject, (Object[]) null);
            return assocColl;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param aInfo
     * @param newColl
     * @see de.jdynameta.base.value.ValueModel#getValue(AbstractAttributeInfo)
     */
    public void setWrappedCollFor(AssociationInfo aInfo, Collection<Object> newColl)
    {
        try
        {
            ChangeableObjectList<ValueObject> resultColl = new ChangeableObjectList<>();
            Class<? extends Object> me = wrappedObject.getClass();

            Method setter = null;
            Method[] allMethods = me.getDeclaredMethods();
            for (int i = 0; i < allMethods.length && setter == null; i++)
            {
                if (allMethods[i].getName().equals("set" + stringWithFirstLetterUppercase(aInfo.getDetailClass().getInternalName()) + "Coll")
                        && allMethods[i].getParameterTypes().length == 1)
                {
                    setter = allMethods[i];
                }
            }

            setter.invoke(wrappedObject, new Object[]
            {
                newColl
            });

        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
        }
    }

    /**
     * @see de.jdynameta.base.value.ValueModel#getValue(AbstractAttributeInfo)
     */
    @Override
    public ObjectList<? extends ValueObject> getValue(AssociationInfo aInfo)
    {
        try
        {
            ChangeableObjectList<ValueObject> resultColl = new ChangeableObjectList<>();
            Class<? extends Object> me = wrappedObject.getClass();
            Method getter = me.getMethod("get" + stringWithFirstLetterUppercase(aInfo.getDetailClass().getInternalName()) + "Coll", (Class[]) null);
            Collection<Object> assocColl = (Collection<Object>) getter.invoke(wrappedObject, (Object[]) null);

            for (Object object : assocColl)
            {
                resultColl.addObject(new ReflectionValueObjectWrapper(object));

            }

            return resultColl;

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param aInfo
     * @param value
     * @see de.jdynameta.base.value.ValueModel#setValue(AbstractAttributeInfo,
     * Object)
     */
    public void setValue(AttributeInfo aInfo, Object value)
    {
        try
        {
            Class<? extends Object> targetObjectClass = wrappedObject.getClass();
            Class<? extends Object> typeToSet = null;
            if (aInfo instanceof PrimitiveAttributeInfo)
            {
                typeToSet = ((PrimitiveAttributeInfo) aInfo).getJavaTyp();
            } else if (aInfo instanceof ObjectReferenceAttributeInfo)
            {

                ClassInfo typeInfo = ((ObjectReferenceAttributeInfo) aInfo).getReferencedClass();
                typeToSet = Class.forName(typeInfo.getRepoName() + "." + typeInfo.getInternalName());
            }
            Method setter = targetObjectClass.getMethod("set" + stringWithFirstLetterUppercase(aInfo.getInternalName()), (Class[]) new Class[]
            {
                typeToSet
            });
            setter.invoke(wrappedObject, new Object[]
            {
                value
            });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException exp)
        {
            exp.printStackTrace();
        }
    }

    public String stringWithFirstLetterUppercase(String textToAppend)
    {

        return "" + Character.toUpperCase(textToAppend.charAt(0)) + textToAppend.substring(1);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AttributeInfo)
     */
    @Override
    public boolean hasValueFor(AttributeInfo aInfo)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.comafra.model.value.ValueObject#hasValueFor(de.comafra.model.metainfo.AssociationInfo)
     */
    @Override
    public boolean hasValueFor(AssociationInfo aInfo)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj != null && obj instanceof ReflectionValueObjectWrapper)
                ? ((ReflectionValueObjectWrapper) obj).getWrappedObject().equals(this.getWrappedObject())
                : obj == this;
    }

    @Override
    public int hashCode()
    {
        return this.getWrappedObject().hashCode();
    }
}
