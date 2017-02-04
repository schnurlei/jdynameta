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
package de.jdynameta.jdy.model.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.transform.TransformerConfigurationException;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.persistence.state.ApplicationObj;

/**
 * @author Rainer Copyright 2004 Rainer Schneider, Schiessen All rights
 * reserved.
 */
public class JsonObjectWriter implements ObjectWriter
{
    private final File baseDirectory;
    private final JsonFileWriter writer;

    /**
     * @param aBaseDirectory
     * @param doFlush
     */
    public JsonObjectWriter(File aBaseDirectory, boolean doFlush)
    {
        super();
        this.baseDirectory = aBaseDirectory;
        this.writer = new JsonFileWriter(new JsonFileWriter.WriteDependentAsProxyStrategy(), doFlush);
    }

    /* (non-Javadoc)
     * @see de.comafra.model.dbaccess.base.writer.ObjectWriter#deleteObjectInDb(de.comafra.model.value.ValueObject, de.comafra.model.metainfo.ClassInfo)
     */
    @Override
    public void deleteObjectInDb(ValueObject aObjToDelete, ClassInfo aInfo)
            throws JdyPersistentException
    {
        JsonFileReader fileReader = new JsonFileReader();

        File modelFile = new File(baseDirectory, aInfo.getExternalName() + ".json");

        try
        {
            ObjectList<ApplicationObj> objectList;
            if (modelFile.exists())
            {
                objectList = fileReader.readObjectList(new FileInputStream(modelFile), aInfo);
            } else
            {
                objectList = new DefaultObjectList<>();
            }
            ChangeableObjectList<TypedValueObject> newList = new ChangeableObjectList<>();
            for (Iterator<ApplicationObj> valueModelIter = objectList.iterator(); valueModelIter.hasNext();)
            {

                TypedValueObject curModel = valueModelIter.next();
                if (!areValueObjectsEqual(aInfo, aObjToDelete, curModel))
                {
                    newList.addObject(curModel);
                }
            }

            try (PrintWriter outWriter = new PrintWriter(new FileOutputStream(modelFile)))
            {
                writer.writeObjectList(outWriter, aInfo, newList, Operation.DELETE);
            }

        } catch (FileNotFoundException | TransformerConfigurationException | ProxyResolveException | JdyPersistentException excp)
        {
            throw new JdyPersistentException(excp);
        }

    }
    /* (non-Javadoc)
     * @see de.comafra.model.dbaccess.base.writer.ObjectWriter#insertObjectInDb(de.comafra.model.value.ValueObject, de.comafra.model.metainfo.ClassInfo)
     */

    @Override
    public TypedValueObject insertObjectInDb(ValueObject aObjToInsert, ClassInfo aInfo)
            throws JdyPersistentException
    {
        JsonFileReader fileReader = new JsonFileReader();
        baseDirectory.mkdirs();
        File modelFile = new File(baseDirectory, aInfo.getExternalName() + ".json");

        try
        {

            ObjectList<ApplicationObj> objectList;
            if (modelFile.exists())
            {
                objectList = fileReader.readObjectList(new FileInputStream(modelFile), aInfo);
            } else
            {
                objectList = new DefaultObjectList<>();
            }
            ChangeableObjectList<TypedValueObject> newList = new ChangeableObjectList<>();
            for (Iterator<ApplicationObj> valueModelIter = objectList.iterator(); valueModelIter.hasNext();)
            {

                ApplicationObj curModel = valueModelIter.next();
                if (areValueObjectsEqual(aInfo, aObjToInsert, curModel))
                {
                    throw new JdyPersistentException("Object already exist");
                }
                newList.addObject(curModel);
            }

            newList.addObject(new TypedWrappedValueObject(aObjToInsert, aInfo));

            try (PrintWriter outWriter = new PrintWriter(new FileOutputStream(modelFile)))
            {
                writer.writeObjectList(outWriter, aInfo, newList, Operation.INSERT);
            }

            fileReader.readObjectList(new FileInputStream(modelFile), aInfo);
        } catch (ProxyResolveException | FileNotFoundException | TransformerConfigurationException excp)
        {
            throw new JdyPersistentException(excp);
        } finally
        {
        }
        return new TypedWrappedValueObject(aObjToInsert, aInfo);

    }

    private static boolean areValueObjectsEqual(ClassInfo aInfo, final ValueObject object1, final ValueObject object2) throws JdyPersistentException
    {
        boolean isEqual = true;

        if (object1 == null && object2 == null)
        {
            isEqual = true;
        } else if ((object1 == null && object2 != null)
                || (object1 != null && object2 == null))
        {
            isEqual = false;
        } else
        {
            try
            {
                ObjectCompareAttrHandler compareHandler = new ObjectCompareAttrHandler();
                compareHandler.isEqual = true;

                for (AttributeInfo curAttr : aInfo.getAttributeInfoIterator())
                {
                    if (curAttr.isKey())
                    {

                        compareHandler.object1 = object1;
                        compareHandler.object2 = object2;
                        curAttr.handleAttribute(compareHandler, null);
                        isEqual = compareHandler.isEqual;
                        if (!isEqual)
                        {
                            break;
                        }

                    }
                }
            } catch (JdyPersistentException excp)
            {
                throw excp;
            }
        }

        return isEqual;
    }

    private static class ObjectCompareAttrHandler implements AttributeHandler
    {
        private boolean isEqual;
        private ValueObject object1;
        private ValueObject object2;

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo,
                ValueObject aObjToHandle) throws JdyPersistentException
        {
            isEqual = areValueObjectsEqual(aInfo.getReferencedClass(), (ValueObject) object1.getValue(aInfo), (ValueObject) object2.getValue(aInfo));
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,
                Object aObjToHandle) throws JdyPersistentException
        {
            Object val1 = object1.getValue(aInfo);
            Object val2 = object2.getValue(aInfo);

            if (val1 == null && val2 == null)
            {
                isEqual = true;
            } else if ((val1 == null && val2 != null)
                    || (val1 != null && val2 == null))
            {
                isEqual = false;
            } else
            {
                isEqual = val1.equals(val2);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.comafra.model.dbaccess.base.writer.ObjectWriter#updateObjectToDb(de.comafra.model.value.ValueObject, de.comafra.model.metainfo.ClassInfo)
     */
    @Override
    public void updateObjectToDb(ValueObject aObjToModify, ClassInfo aInfo)
            throws JdyPersistentException
    {

        JsonFileReader fileReader = new JsonFileReader();

        File modelFile = new File(baseDirectory, aInfo.getExternalName() + ".json");

        try
        {
            ObjectList<ApplicationObj> objectList;
            if (modelFile.exists())
            {
                objectList = fileReader.readObjectList(new FileInputStream(modelFile), aInfo);
            } else
            {
                objectList = new DefaultObjectList<>();
            }

            ChangeableObjectList<TypedValueObject> newList = new ChangeableObjectList<>();
            for (Iterator<ApplicationObj> valueModelIter = objectList.iterator(); valueModelIter.hasNext();)
            {
                newList.addObject(valueModelIter.next());
            }

            for (Iterator<ApplicationObj> valueModelIter = objectList.iterator(); valueModelIter.hasNext();)
            {

                ApplicationObj curModel = valueModelIter.next();
                if (areValueObjectsEqual(aInfo, aObjToModify, curModel))
                {
                    newList.addObject(new TypedWrappedValueObject(aObjToModify, aInfo));
                } else
                {
                    newList.addObject(curModel);
                }
            }

            try (PrintWriter outWriter = new PrintWriter(new FileOutputStream(modelFile)))
            {
                writer.writeObjectList(outWriter, aInfo, newList, Operation.UPDATE);
            }
        } catch (FileNotFoundException | TransformerConfigurationException | ProxyResolveException | JdyPersistentException excp)
        {
            throw new JdyPersistentException(excp);
        }
    }

}
