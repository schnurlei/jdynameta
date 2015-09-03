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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.AbstractAttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.ClassNameCreator;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ReflectionChangeableValueObject extends ReflectionValueObject
        implements ChangeableTypedValueObject
{
    private ClassNameCreator nameCreator;
    private final ClassInfo classInfo;

    /**
     *
     * @param aClassInfo
     */
    public ReflectionChangeableValueObject(ClassInfo aClassInfo)
    {
        this(aClassInfo, new ModelNameCreator());

    }

    /**
     *
     * @param aClassInfo
     * @param aNameCreator
     */
    public ReflectionChangeableValueObject(ClassInfo aClassInfo, ClassNameCreator aNameCreator)
    {
        super(aNameCreator);
        this.nameCreator = aNameCreator;
        this.classInfo = aClassInfo;
    }

    @Override
    public ObjectList<? extends ReflectionChangeableValueObject> getValue(AssociationInfo aInfo)
    {
        return (ObjectList<? extends ReflectionChangeableValueObject>) super.getValue(aInfo);
    }

    /**
     * @see de.jdynameta.base.value.ValueModel#setValue(AbstractAttributeInfo,
     * Object)
     */
    @Override
    public void setValue(AttributeInfo aInfo, Object value)
    {
        try
        {
            Class<? extends Object> me = this.getClass();
            if (aInfo instanceof PrimitiveAttributeInfo)
            {
                PrimitiveAttributeInfo prim = (PrimitiveAttributeInfo) aInfo;
                Method getter = me.getMethod(this.nameCreator.getSetterNameFor(aInfo), new Class[]
                {
                    prim.getJavaTyp()
                });
                getter.invoke(this, new Object[]
                {
                    value
                });

            } else
            {
                ObjectReferenceAttributeInfo objRef = (ObjectReferenceAttributeInfo) aInfo;

                String className = this.nameCreator.getReferenceClassNameFor(objRef.getReferencedClass());
                Class refClass = this.getClass().getClassLoader().loadClass(className);
                Method getter = me.getMethod(this.nameCreator.getSetterNameFor(aInfo), new Class[]
                {
                    refClass
                });
                getter.invoke(this, new Object[]
                {
                    value
                });

            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | IllegalArgumentException exp)
        {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public Object getValue(String anExternlName)
    {
        return getValue(this.classInfo.getAttributeInfoForExternalName(anExternlName));

    }

    @Override
    public void setValue(String anExternlName, Object aValue)
    {
        setValue(this.classInfo.getAttributeInfoForExternalName(anExternlName), aValue);
    }

    @Override
    public ObjectList<? extends ChangeableTypedValueObject> getValues(String anAssocName)
    {
        assert (this.classInfo.getAssoc(anAssocName) != null);
        return getValue(this.classInfo.getAssoc(anAssocName));
    }

    @Override
    public void setValues(String anAssocName, ObjectList<? extends ChangeableTypedValueObject> aValue)
    {
        assert (this.classInfo.getAssoc(anAssocName) != null);
        setValue(this.classInfo.getAssoc(anAssocName), aValue);
    }

    @Override
    public ClassInfo getClassInfo()
    {
        return this.classInfo;
    }

    @Override
    public void setValue(AssociationInfo aInfo, ObjectList<? extends ChangeableTypedValueObject> aValue)
    {
        try
        {
            Class<? extends Object> me = this.getClass();

            Method setter = me.getMethod(this.nameCreator.getSetterNameFor(aInfo), new Class[]
            {
                ObjectList.class
            });
            setter.invoke(this, new Object[]
            {
                aValue
            });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exp)
        {
            exp.printStackTrace();
        }
    }

}
