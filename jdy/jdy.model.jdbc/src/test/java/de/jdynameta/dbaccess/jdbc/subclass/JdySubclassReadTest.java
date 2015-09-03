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
package de.jdynameta.dbaccess.jdbc.subclass;

/*
       
 */
import java.sql.Date;

import junit.framework.TestCase;
import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.creation.ObjectReader;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedClassInfoObject;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.WrappedValueObject;
import de.jdynameta.testcommon.model.subclass.SubclassRepository;
import de.jdynameta.testcommon.model.subclass.impl.FirstSubLevel2Impl;
import de.jdynameta.testcommon.model.subclass.impl.MainClassImpl;
import de.jdynameta.testcommon.model.subclass.impl.ReferencedObjectImpl;
import de.jdynameta.testcommon.model.subclass.impl.SecondSubLevel2Impl;
import de.jdynameta.testcommon.model.subclass.impl.SubLevel3Impl;
import de.jdynameta.testcommon.model.subclass.impl.SubclassLevel1Impl;

/**
 *
 */
public abstract class JdySubclassReadTest extends TestCase
{

    /**
     * Creates the ObjectWriterTest object.
     *
     */
    public JdySubclassReadTest()
    {
        super();
    }

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdySubclassReadTest(String name)
    {
        super(name);
    }

    /**
     *
     * @throws Exception DOCUMENT ME!
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
    }

    protected abstract ObjectWriter getObjectWriter();

    protected abstract ObjectReader getObjectReader();

    /**
     * Insert and Read Object Reference
     *
     * @throws java.lang.Exception
     */
    public void testReadReferencedObjectType() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo refObjType = SubclassRepository.getSingleton().getReferencedObjectType();
        ReferencedObjectImpl refObjToInsert = new ReferencedObjectImpl();

        refObjToInsert.setRefData(new Date(System.currentTimeMillis()));
        refObjToInsert.setRefKey1In(new Long(10));
        refObjToInsert.setRefKey2In("Obj Reference 1");

        writer.insertObjectInDb(refObjToInsert, refObjType);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(refObjType);
        ObjectList<ClassInfoValueObject> readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
    }

    /**
     * Insert and Read Main Class Type
     *
     * @throws java.lang.Exception
     */
    public void testReadMainClassType() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo mainType = SubclassRepository.getSingleton().getMainClassType();
        ClassInfo refObjType = SubclassRepository.getSingleton().getReferencedObjectType();

        ReferencedObjectImpl refObjToInsert = new ReferencedObjectImpl();
        MainClassImpl mainObj = new MainClassImpl();

        refObjToInsert.setRefData(new Date(System.currentTimeMillis()));
        refObjToInsert.setRefKey1In(new Long(10));
        refObjToInsert.setRefKey2In("Obj Reference 1");
        writer.insertObjectInDb(refObjToInsert, refObjType);

        mainObj.setKeyMain1(refObjToInsert);
        mainObj.setKeyMain2("Key Main1");
        mainObj.setMainData("Main Data1");
        writer.insertObjectInDb(mainObj, mainType);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(mainType);
        ObjectList<ClassInfoValueObject> readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
    }

    /**
     * Insert and Read Main Class Type
     *
     * @throws java.lang.Exception
     */
    public void testReadSubclassLevel1Type() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo mainType = SubclassRepository.getSingleton().getMainClassType();
        ClassInfo subLevel1Type = SubclassRepository.getSingleton().getSubclassLevel1Type();
        ClassInfo refObjType = SubclassRepository.getSingleton().getReferencedObjectType();

        ReferencedObjectImpl refObjToInsert = new ReferencedObjectImpl();
        refObjToInsert.setRefData(new Date(System.currentTimeMillis()));
        refObjToInsert.setRefKey1In(new Long(10));
        refObjToInsert.setRefKey2In("Obj Reference 1");
        writer.insertObjectInDb(refObjToInsert, refObjType);

        MainClassImpl mainObj = new MainClassImpl();
        mainObj.setKeyMain1(refObjToInsert);
        mainObj.setKeyMain2("Key Main1");
        mainObj.setMainData("Main Data1");
        writer.insertObjectInDb(mainObj, mainType);

        SubclassLevel1Impl lvl1Obj = new SubclassLevel1Impl();
        lvl1Obj.setKeyMain1(refObjToInsert);
        lvl1Obj.setKeyMain2("Key lvl1");
        lvl1Obj.setMainData("Main Data1 lvl1");
        lvl1Obj.setDataLvl1("Data1 lvl1 first");
        writer.insertObjectInDb(lvl1Obj, subLevel1Type);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(subLevel1Type);
        ObjectList<ClassInfoValueObject> readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        DefaultClassInfoQuery mainfilter = new DefaultClassInfoQuery(mainType);
        readedObjectColl = reader.loadValuesFromDb(mainfilter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 2);
    }

    /**
     * Insert and Read Main Class Type
     *
     * @throws java.lang.Exception
     */
    public void testReadFirstSubLevel2Impl() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo mainType = SubclassRepository.getSingleton().getMainClassType();
        ClassInfo subLevel1Type = SubclassRepository.getSingleton().getSubclassLevel1Type();
        ClassInfo refObjType = SubclassRepository.getSingleton().getReferencedObjectType();
        ClassInfo firstLvl2Type = SubclassRepository.getSingleton().getFirstSubLevel2Type();

        ReferencedObjectImpl refObjToInsert = new ReferencedObjectImpl();
        refObjToInsert.setRefData(new Date(System.currentTimeMillis()));
        refObjToInsert.setRefKey1In(new Long(10));
        refObjToInsert.setRefKey2In("Obj Reference 1");
        writer.insertObjectInDb(refObjToInsert, refObjType);

        MainClassImpl mainObj = new MainClassImpl();
        mainObj.setKeyMain1(refObjToInsert);
        mainObj.setKeyMain2("Key Main1");
        mainObj.setMainData("Main Data1");
        writer.insertObjectInDb(mainObj, mainType);

        SubclassLevel1Impl lvl1Obj = new SubclassLevel1Impl();
        lvl1Obj.setKeyMain1(refObjToInsert);
        lvl1Obj.setKeyMain2("Key lvl1");
        lvl1Obj.setMainData("Main Data1 lvl1");
        lvl1Obj.setDataLvl1("Data1 lvl1 first");
        writer.insertObjectInDb(lvl1Obj, subLevel1Type);

        FirstSubLevel2Impl firstLvl2 = new FirstSubLevel2Impl();
        firstLvl2.setKeyMain1(refObjToInsert);
        firstLvl2.setKeyMain2("Key lvl2First");
        firstLvl2.setMainData("Main Data1 lvl2First");
        firstLvl2.setDataLvl1("Data1 lvl2First");
        firstLvl2.setDataALvl2("Data2 lvl2First");
        writer.insertObjectInDb(firstLvl2, firstLvl2Type);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery firstLvl2Filter = new DefaultClassInfoQuery(firstLvl2Type);
        ObjectList<ClassInfoValueObject> readedObjectColl = reader.loadValuesFromDb(firstLvl2Filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(subLevel1Type);
        readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 2);
        DefaultClassInfoQuery mainfilter = new DefaultClassInfoQuery(mainType);
        readedObjectColl = reader.loadValuesFromDb(mainfilter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 3);
    }

    /**
     * Insert and Read Main Class Type
     *
     * @throws java.lang.Exception
     */
    public void testReadSecondLvl2Impl() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo mainType = SubclassRepository.getSingleton().getMainClassType();
        ClassInfo subLevel1Type = SubclassRepository.getSingleton().getSubclassLevel1Type();
        ClassInfo refObjType = SubclassRepository.getSingleton().getReferencedObjectType();
        ClassInfo firstLvl2Type = SubclassRepository.getSingleton().getFirstSubLevel2Type();
        ClassInfo secondLvl2Type = SubclassRepository.getSingleton().getSecondSubLevel2Type();

        ReferencedObjectImpl refObjToInsert = new ReferencedObjectImpl();
        refObjToInsert.setRefData(new Date(System.currentTimeMillis()));
        refObjToInsert.setRefKey1In(new Long(10));
        refObjToInsert.setRefKey2In("Obj Reference 1");
        writer.insertObjectInDb(refObjToInsert, refObjType);

        MainClassImpl mainObj = new MainClassImpl();
        mainObj.setKeyMain1(refObjToInsert);
        mainObj.setKeyMain2("Key Main1");
        mainObj.setMainData("Main Data1");
        writer.insertObjectInDb(mainObj, mainType);

        SubclassLevel1Impl lvl1Obj = new SubclassLevel1Impl();
        lvl1Obj.setKeyMain1(refObjToInsert);
        lvl1Obj.setKeyMain2("Key lvl1");
        lvl1Obj.setMainData("Main Data1 lvl1");
        lvl1Obj.setDataLvl1("Data1 lvl1 first");
        writer.insertObjectInDb(lvl1Obj, subLevel1Type);

        FirstSubLevel2Impl firstLvl2 = new FirstSubLevel2Impl();
        firstLvl2.setKeyMain1(refObjToInsert);
        firstLvl2.setKeyMain2("Key lvl2First");
        firstLvl2.setMainData("Main Data1 lvl2First");
        firstLvl2.setDataLvl1("Data1 lvl2First");
        firstLvl2.setDataALvl2("Data2 lvl2First");
        writer.insertObjectInDb(firstLvl2, firstLvl2Type);

        SecondSubLevel2Impl secondLvl2 = new SecondSubLevel2Impl();
        secondLvl2.setKeyMain1(refObjToInsert);
        secondLvl2.setKeyMain2("Key lvl2Second");
        secondLvl2.setMainData("Main Data1 lvl2Second");
        secondLvl2.setDataLvl1("DataA1 lvl2Second");
        secondLvl2.setDataBLvl2("DataB2 lvl2Second");
        writer.insertObjectInDb(secondLvl2, secondLvl2Type);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery secondLvl2Filter = new DefaultClassInfoQuery(secondLvl2Type);
        ObjectList<ClassInfoValueObject> readedObjectColl = reader.loadValuesFromDb(secondLvl2Filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        DefaultClassInfoQuery firstLvl2Filter = new DefaultClassInfoQuery(firstLvl2Type);
        readedObjectColl = reader.loadValuesFromDb(firstLvl2Filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(subLevel1Type);
        readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 3);
        DefaultClassInfoQuery mainfilter = new DefaultClassInfoQuery(mainType);
        readedObjectColl = reader.loadValuesFromDb(mainfilter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 4);
    }

    /**
     * Insert and Read Main Class Type
     *
     * @throws java.lang.Exception
     */
    public void testReadSubLevel3Impl() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo mainType = SubclassRepository.getSingleton().getMainClassType();
        ClassInfo subLevel1Type = SubclassRepository.getSingleton().getSubclassLevel1Type();
        ClassInfo refObjType = SubclassRepository.getSingleton().getReferencedObjectType();
        ClassInfo firstLvl2Type = SubclassRepository.getSingleton().getFirstSubLevel2Type();
        ClassInfo secondLvl2Type = SubclassRepository.getSingleton().getSecondSubLevel2Type();
        ClassInfo subLevel3Type = SubclassRepository.getSingleton().getSubLevel3Type();

        ReferencedObjectImpl refObjToInsert = new ReferencedObjectImpl();
        refObjToInsert.setRefData(new Date(System.currentTimeMillis()));
        refObjToInsert.setRefKey1In(new Long(10));
        refObjToInsert.setRefKey2In("Obj Reference 1");
        writer.insertObjectInDb(refObjToInsert, refObjType);

        MainClassImpl mainObj = new MainClassImpl();
        mainObj.setKeyMain1(refObjToInsert);
        mainObj.setKeyMain2("Key Main1");
        mainObj.setMainData("Main Data1");
        writer.insertObjectInDb(mainObj, mainType);

        SubclassLevel1Impl lvl1Obj = new SubclassLevel1Impl();
        lvl1Obj.setKeyMain1(refObjToInsert);
        lvl1Obj.setKeyMain2("Key lvl1");
        lvl1Obj.setMainData("Main Data1 lvl1");
        lvl1Obj.setDataLvl1("Data1 lvl1 first");
        writer.insertObjectInDb(lvl1Obj, subLevel1Type);

        FirstSubLevel2Impl firstLvl2 = new FirstSubLevel2Impl();
        firstLvl2.setKeyMain1(refObjToInsert);
        firstLvl2.setKeyMain2("Key lvl2First");
        firstLvl2.setMainData("Main Data1 lvl2First");
        firstLvl2.setDataLvl1("Data1 lvl2First");
        firstLvl2.setDataALvl2("Data2 lvl2First");
        writer.insertObjectInDb(firstLvl2, firstLvl2Type);

        SecondSubLevel2Impl secondLvl2 = new SecondSubLevel2Impl();
        secondLvl2.setKeyMain1(refObjToInsert);
        secondLvl2.setKeyMain2("Key lvl2Second");
        secondLvl2.setMainData("Main Data1 lvl2Second");
        secondLvl2.setDataLvl1("DataA1 lvl2Second");
        secondLvl2.setDataBLvl2("DataB2 lvl2Second");
        writer.insertObjectInDb(secondLvl2, secondLvl2Type);

        SubLevel3Impl subLevel3 = new SubLevel3Impl();
        subLevel3.setKeyMain1(refObjToInsert);
        subLevel3.setKeyMain2("Key lvl3");
        subLevel3.setMainData("Main Data1 lvl3");
        subLevel3.setDataLvl1("DataA1 lvl3");
        subLevel3.setDataBLvl2("DataB2 lvl3");
        subLevel3.setDataLevel3("Data lvl3");
        writer.insertObjectInDb(subLevel3, subLevel3Type);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery subLevel3Filter = new DefaultClassInfoQuery(subLevel3Type);
        ObjectList<ClassInfoValueObject> readedObjectColl = reader.loadValuesFromDb(subLevel3Filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        DefaultClassInfoQuery secondLvl2Filter = new DefaultClassInfoQuery(secondLvl2Type);
        readedObjectColl = reader.loadValuesFromDb(secondLvl2Filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 2);
        DefaultClassInfoQuery firstLvl2Filter = new DefaultClassInfoQuery(firstLvl2Type);
        readedObjectColl = reader.loadValuesFromDb(firstLvl2Filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(subLevel1Type);
        readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 4);
        DefaultClassInfoQuery mainfilter = new DefaultClassInfoQuery(mainType);
        readedObjectColl = reader.loadValuesFromDb(mainfilter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 5);
    }

    /**
     * assert that the attribute of the given valueobject is set in the target
     * db
     *
     * @param aClassInfo
     * @param aPrimitivAttr
     * @param value
     */
    protected abstract void assertValueIsSet(ClassInfo aClassInfo, PrimitiveAttributeInfo aPrimitivAttr, ValueObject value);

    /**
     * @author rsc
     */
    @SuppressWarnings("serial")
    private class ValueModelObjectCreator implements ObjectCreator<ClassInfoValueObject>
    {

        @Override
        public ClassInfoValueObject createObjectFor(TypedValueObject aValueModel)
        {
            return new ClassInfoValueObject(aValueModel, aValueModel.getClassInfo());
        }

        @Override
        public ClassInfoValueObject createNewObjectFor(ClassInfo aClassinfo)
                throws ObjectCreationException
        {
            return null;
        }
    }

    private static class ClassInfoValueObject extends WrappedValueObject
            implements TypedClassInfoObject
    {

        private final ClassInfo type;

        public ClassInfoValueObject(ValueObject aWrappedObject, ClassInfo aType)
        {
            super(aWrappedObject);
            this.type = aType;
        }

        @Override
        public ClassInfo getClassInfo()
        {
            return type;
        }

    }
}
