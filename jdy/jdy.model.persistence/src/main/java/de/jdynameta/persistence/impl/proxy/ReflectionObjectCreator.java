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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.AttributeNameCreator;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultAttributeNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.cache.UpdatableObjectCreator;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.impl.ProxyAssociationListModel;

/**
 * Creates an Object an set the values in it with the Java Reflection Methods.
 * Creates an instance of Proxy for new created Proxy objects Use an external
 * Object creator to create referenced Objects (maybe they are cached and have
 * not be created)
 *
 * @author Rainer
 * @param <TTransformToValueObj>
 * @param <TCreatedUpdateableObjFromValueObj>
 *
 */
@SuppressWarnings("serial")
public class ReflectionObjectCreator<TTransformToValueObj, TCreatedUpdateableObjFromValueObj> extends ObjectCreatorWithProxy<TCreatedUpdateableObjFromValueObj>
        implements UpdatableObjectCreator<TTransformToValueObj, TCreatedUpdateableObjFromValueObj>
{
    /**
     * Converts the names for the reflection model from the ClassInfo
     */
    private final ClassNameCreator nameCreator;
    /**
     * Resolver for the proxy objects created by this creator
     */
    private final ProxyResolver proxyResolver;

    private PersistentObjectManager objectManager;

    public ReflectionObjectCreator(ProxyResolver aProxyResolver, PersistentObjectManager aObjectManager)
    {
        this(aProxyResolver, new DefaultClassNameCreator(), aObjectManager);
    }

    public ReflectionObjectCreator(ProxyResolver aProxyResolver, ClassNameCreator aNameCreator, PersistentObjectManager aObjectManager)
    {
        this.nameCreator = aNameCreator;
        this.proxyResolver = aProxyResolver;
        this.objectManager = aObjectManager;

    }

    @Override
    public TCreatedUpdateableObjFromValueObj createNewObjectFor(ClassInfo aClassInfo) throws ObjectCreationException
    {
        try
        {
            final Class<TCreatedUpdateableObjFromValueObj> metaClass = (Class<TCreatedUpdateableObjFromValueObj>) Class.forName(nameCreator.getAbsolutClassNameFor(aClassInfo));

            Constructor<TCreatedUpdateableObjFromValueObj> classConstructor = metaClass.getConstructor((Class[]) null);
            final TCreatedUpdateableObjFromValueObj newObject = classConstructor.newInstance((Object[]) null);
            return newObject;
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new ObjectCreationException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected TCreatedUpdateableObjFromValueObj createNoProxyObjectFor(TypedValueObject aValueModel) throws ObjectCreationException
    {
        try
        {
            final Class<TCreatedUpdateableObjFromValueObj> metaClass = (Class<TCreatedUpdateableObjFromValueObj>) Class.forName(nameCreator.getAbsolutClassNameFor(aValueModel.getClassInfo()));

            Constructor<TCreatedUpdateableObjFromValueObj> classConstructor = metaClass.getConstructor((Class[]) null);
            final TCreatedUpdateableObjFromValueObj newObject = classConstructor.newInstance((Object[]) null);

            setValuesInObject(metaClass, newObject, aValueModel.getClassInfo(), aValueModel);

            return newObject;
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | JdyPersistentException e)
        {
            throw new ObjectCreationException(e);
        }
    }

    protected void setValuesInObject(Class aMetaClass, Object aObjectToSetValues, ClassInfo aClassInfo, ValueObject aValueModelToGetValues) throws JdyPersistentException
    {
        GenericSetValueAttributeHandler handler = new GenericSetValueAttributeHandler();
        handler.metaClass = aMetaClass;
        handler.objectToSetValues = aObjectToSetValues;

        for (AttributeInfo curInfo : aClassInfo.getAttributeInfoIterator())
        {

            curInfo.handleAttribute(handler, (aValueModelToGetValues == null) ? null : aValueModelToGetValues.getValue(curInfo));
        }

        for (AssociationInfo curAssocInfo : aClassInfo.getAssociationInfoIterator())
        {

            try
            {
                Method setter = aMetaClass.getMethod(nameCreator.getSetterNameFor(curAssocInfo), new Class[]
                {
                    ObjectList.class
                });

                if (aValueModelToGetValues == null)
                {
                    setter.invoke(aObjectToSetValues, new Object[]
                    {
                        createNonProxyObjectList(curAssocInfo, new DefaultObjectList(), aObjectToSetValues)
                    });
                } else if (aValueModelToGetValues.hasValueFor(curAssocInfo))
                {
                    setter.invoke(aObjectToSetValues, new Object[]
                    {
                        createNonProxyObjectList(curAssocInfo, aValueModelToGetValues.getValue(curAssocInfo), aObjectToSetValues)
                    });
                } else
                {
                    setter.invoke(aObjectToSetValues, new Object[]
                    {
                        createProxyObjectList(objectManager, curAssocInfo, (ValueObject) aObjectToSetValues)
                    });
                }
            } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException excp)
            {
                throw new JdyPersistentException(excp);
            }
        }

    }

    protected ObjectList<TCreatedUpdateableObjFromValueObj> createNonProxyObjectList(AssociationInfo aAssocInfo, ObjectList aListToSet, Object aParent)
    {
        return aListToSet;
    }

    protected ObjectList createProxyObjectList(PersistentObjectManager aObjectManager, AssociationInfo aAssocInfo, ValueObject aMasterObject)
    {
        // TODO
        return new ProxyAssociationListModel(aObjectManager, aAssocInfo, aMasterObject);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.persistence.ObjectCreatorWithProxy#createProxyObjectFor(de.comafra.model.metainfo.ClassInfo, de.comafra.model.value.ValueObject)
     */
    @Override
    protected TCreatedUpdateableObjFromValueObj createProxyObjectFor(TypedValueObject aValueModel) throws ObjectCreationException
    {
        try
        {
            return getProxy(aValueModel.getClassInfo(), aValueModel, this.nameCreator, this.proxyResolver);
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException excp)
        {
            throw new ObjectCreationException(excp);
        }
    }

    /**
     *
     * @author Rainer
     *
     */
    private class GenericSetValueAttributeHandler implements AttributeHandler, Serializable
    {
        private Class metaClass;
        private Object objectToSetValues;

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                throws JdyPersistentException
        {
            try
            {
//				ClassNameCreator interfacenameCreator = new MetaModelClassFileGenerator.InterfaceNameCreator();				

                if (objToHandle != null)
                {
                    Method setter = metaClass.getMethod(nameCreator.getSetterNameFor(aInfo), new Class[]
                    {
                        Class.forName(nameCreator.getReferenceClassNameFor(aInfo.getReferencedClass()))
                    });
                    setter.invoke(objectToSetValues, new Object[]
                    {
                        objToHandle
                    });
                }
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e)
            {
                throw new JdyPersistentException(e);
            }
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                throws JdyPersistentException
        {
            try
            {
                Method setter = metaClass.getMethod(nameCreator.getSetterNameFor(aInfo), new Class[]
                {
                    aInfo.getJavaTyp()
                });
                setter.invoke(objectToSetValues, new Object[]
                {
                    objToHandle
                });
            } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
            {
                throw new JdyPersistentException(e);
            }
        }

    }

    @Override
    public void updateValuesInObject(TypedValueObject aValueModel, Object aObjectToUpdate) throws ObjectCreationException
    {
        if (aObjectToUpdate instanceof Proxy)
        {
            // @TODO check if profy complete resolved		boolean allValuesSet = setValuesInObject((GenericPersistentValueObjectImpl) aObjectToUpdate, aClassInfo, aValueModel);
            DynamicProxyValueObjectHandler handler = (DynamicProxyValueObjectHandler) Proxy.getInvocationHandler(aObjectToUpdate);
            handler.setProxiedValueObject(aValueModel);
            handler.setResolvedObject(createObjectFor(aValueModel));
        } else
        {
            try
            {
                final Class metaClass = Class.forName(this.nameCreator.getAbsolutClassNameFor(aValueModel.getClassInfo()));
                setValuesInObject(metaClass, aObjectToUpdate, aValueModel.getClassInfo(), aValueModel);
            } catch (ClassNotFoundException | JdyPersistentException excp)
            {
                throw new ObjectCreationException(excp);
            }
        }
    }

    public TCreatedUpdateableObjFromValueObj getProxy(ClassInfo aClassInfo, TypedValueObject aValueModel, ClassNameCreator aNameCreator, ProxyResolver aProxyResolver)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException
    {
        Class metaClass = Class.forName(aNameCreator.getAbsolutClassNameFor(aClassInfo));
        ClassLoader clsLoader = metaClass.getClassLoader();

        Class[] interfaces;
        if (metaClass.isInterface())
        {
            interfaces = new Class[]
            {
                metaClass
            };
        } else
        {
            interfaces = metaClass.getInterfaces();
        }

        InvocationHandler handler = new DynamicProxyValueObjectHandler(aClassInfo, aValueModel, metaClass, aNameCreator, aProxyResolver);
        return (TCreatedUpdateableObjFromValueObj) Proxy.newProxyInstance(clsLoader, interfaces, handler);
    }

    /**
     *
     * @author Rainer
     *
     */
    public static class DynamicProxyValueObjectHandler implements InvocationHandler, Serializable
    {
        private TypedValueObject proxiedValueObject;
        private Object resolvedObject;
        private final boolean isProxy;
        // Cache getter name instead of the methods to get the Method 
        // when the Method is from the impl Calls or from the interface  
        private final Map<String, AttributeInfo> getterName2AttributeMap;
        private final ProxyResolver proxyResolver;
        private final ClassInfo objectClassInfo;

        private DynamicProxyValueObjectHandler(ClassInfo aClassInfo, TypedValueObject aValueModel, Class metaClass, AttributeNameCreator aNameCreator, ProxyResolver aProxyResolver) throws SecurityException, NoSuchMethodException
        {
            this.proxiedValueObject = aValueModel;
            this.isProxy = true;
            this.resolvedObject = null;
            this.getterName2AttributeMap = new HashMap<>();
            this.proxyResolver = aProxyResolver;
            this.objectClassInfo = aClassInfo;

            // collect Methods for Attributes that have a value in a Map
            // for this Methods theres no resolve necessary 
            for (AttributeInfo curInfo : aClassInfo.getAttributeInfoIterator())
            {

                if (aValueModel.hasValueFor(curInfo))
                {
                    Method getter = metaClass.getMethod(aNameCreator.getGetterNameFor(curInfo), (Class[]) null);
                    this.getterName2AttributeMap.put(getter.getName(), curInfo);
                }
            }
        }

        @Override
        public Object invoke(Object obj, Method aMethod, Object[] parms) throws Throwable
        {
            Object result;
            if (isProxy)
            {
                if (aMethod.getName().equals("getClassInfo"))
                {
                    result = this.proxiedValueObject.getClassInfo();
                } else
                {

                    AttributeInfo attributeToRead = null;
                    if (aMethod.getName().equals("getValue") && parms[0] instanceof AttributeInfo)
                    {

                        String attrInternalName = DefaultAttributeNameCreator.stringWithFirstLetterUppercase(((AttributeInfo) parms[0]).getInternalName());
                        attributeToRead = this.getterName2AttributeMap.get("get" + attrInternalName);
                    } else
                    {
                        attributeToRead = this.getterName2AttributeMap.get(aMethod.getName());
                    }

                    if (attributeToRead != null)
                    { // read from proxy value
                        result = this.proxiedValueObject.getValue(attributeToRead);
                    } else
                    {
                        resolveProxy();

                        if (resolvedObject != null)
                        {
                            result = aMethod.invoke(resolvedObject, parms);
                        } else
                        {
                            throw new Exception("Proxy Resolve Error");
                        }
                    }
                }
            } else
            {
                result = aMethod.invoke(resolvedObject, parms);
            }

            return result;
        }

        /**
         *
         */
        private void resolveProxy() throws ObjectCreationException
        {
            this.proxyResolver.resolveProxy(proxiedValueObject);
        }

        /**
         * @param aObject
         */
        protected void setProxiedValueObject(TypedValueObject aObject)
        {
            proxiedValueObject = aObject;
        }

        /**
         * @param aObject
         */
        protected void setResolvedObject(Object aObject)
        {
            resolvedObject = aObject;
        }

    }

    /**
     * @return Returns the nameCreator.
     */
    protected ClassNameCreator getNameCreator()
    {
        return this.nameCreator;
    }

    @Override
    public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,
            TTransformToValueObj aObjectToTransform)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
