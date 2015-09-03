package de.jdynameta.base.creation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;

@SuppressWarnings("serial")
public abstract class AbstractReflectionCreator<TCreatedObjFromValueObj> extends AbstractObjectCreator<TCreatedObjFromValueObj>
  {

    /**
     * Converts the names for the reflection model from the ClassInfo
     */
    private final ClassNameCreator nameCreator;

    public AbstractReflectionCreator(ClassNameCreator aNameCreator)
    {
        this.nameCreator = aNameCreator;
    }

    public ClassNameCreator getNameCreator()
    {
        return nameCreator;
    }

    @Override
    public TCreatedObjFromValueObj createNewObjectFor(ClassInfo aClassinfo)
            throws ObjectCreationException
    {
        try
        {
            final Class<TCreatedObjFromValueObj> metaClass
                    = (Class<TCreatedObjFromValueObj>) Class.forName(getNameCreator().getAbsolutClassNameFor(aClassinfo));

            Constructor<TCreatedObjFromValueObj> classConstructor = metaClass.getConstructor((Class[]) null);
            final TCreatedObjFromValueObj newObject = classConstructor.newInstance((Object[]) null);

            return newObject;
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new ObjectCreationException(e);
        }
    }

    @Override
    protected void setListForAssoc(AssociationInfo aAssocInfo, Object aObjoSetVals, TypedValueObject aObjToGetVals)
            throws ObjectCreationException
    {
        Method setterMethod = createReflectionSetter(aAssocInfo, aObjoSetVals);
        ObjectList<? extends TypedValueObject> aListToGet = (ObjectList<? extends TypedValueObject>) aObjToGetVals.getValue(aAssocInfo);
        ChangeableObjectList<TCreatedObjFromValueObj> newObjList = new ChangeableObjectList<>();
        for (TypedValueObject tCreatedObjFromValueObj : aListToGet)
        {
            newObjList.addObject(createObjectFor(tCreatedObjFromValueObj));
        }
        try
        {
            setterMethod.invoke(aObjoSetVals, new Object[]
            {
                newObjList
            });
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException excp)
        {
            throw new ObjectCreationException(excp);
        }

    }

    private Method createReflectionSetter(AssociationInfo aAssocInfo, Object aObjoSetVals)
            throws ObjectCreationException
    {
        try
        {
            return aObjoSetVals.getClass().getMethod(getNameCreator().getSetterNameFor(aAssocInfo), new Class[]
            {
                ObjectList.class
            });
        } catch (SecurityException | NoSuchMethodException e)
        {
            throw new ObjectCreationException(e);
        }
    }

    @Override
    protected void setEmptyList(AssociationInfo aAssocInfo,
            Object aObjoSetVals) throws ObjectCreationException
    {
        Method setterMethod = createReflectionSetter(aAssocInfo, aObjoSetVals);
        ChangeableObjectList<TCreatedObjFromValueObj> newObjList = new ChangeableObjectList<>();
        try
        {
            setterMethod.invoke(aObjoSetVals, new Object[]
            {
                newObjList
            });
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException excp)
        {
            throw new ObjectCreationException(excp);
        }

    }

    @Override
    protected void setObjectReferenceVal(ObjectReferenceAttributeInfo aRefInfo,
            Object aObjoSetVals, TypedValueObject aObjToHandle)
            throws JdyPersistentException, ObjectCreationException
    {
        try
        {
            Method setter = aObjoSetVals.getClass()
                    .getMethod(getNameCreator().getSetterNameFor(aRefInfo), new Class[]
                            {
                                Class.forName(getNameCreator().getReferenceClassNameFor(aRefInfo.getReferencedClass()))
                    });

            TCreatedObjFromValueObj newObject = null;
            if (aObjToHandle != null)
            {
                if (areAllValuesSetForClassInfo(aObjToHandle))
                {
                    newObject = createObjectFor(aObjToHandle);
                } else
                {
                    newObject = createProxyObjectFor(aObjToHandle);
                }
            }

            setter.invoke(aObjoSetVals, new Object[]
            {
                newObject
            });
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e)
        {
            throw new JdyPersistentException(e);
        }

    }

    protected abstract TCreatedObjFromValueObj createProxyObjectFor(TypedValueObject aObjToHandle);

    @Override
    protected void setPrimitiveVal(PrimitiveAttributeInfo aInfo, Object aObjoSetVals, Object aValueToHandle)
            throws JdyPersistentException
    {
        try
        {
            Method setter = aObjoSetVals.getClass().getMethod(getNameCreator().getSetterNameFor(aInfo), new Class[]
            {
                aInfo.getJavaTyp()
            });
            setter.invoke(aObjoSetVals, new Object[]
            {
                aValueToHandle
            });
        } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            throw new JdyPersistentException(e);
        }
    }

  }
