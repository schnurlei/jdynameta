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
package de.jdynameta.dbaccess.jdbc.reader;

/*
       
 */
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

import junit.framework.TestCase;
import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.creation.ObjectReader;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedClassInfoObject;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.WrappedValueObject;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;
import de.jdynameta.testcommon.model.testdata.impl.impl.AllAttributeTypesImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.AssocDetailKeyObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.AssocDetailNotNullObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.AssocDetailSimpleObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.AssociationMasterObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.CompoundKeyObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.KeyRefDeepObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.ReferenceInPrimaryKeyImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.ReferenceObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.ReferenceOnSelfObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.ReferenceOnSubclassObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.ReferenceTwoOnSameObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.SimpleKeyObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.SubClassReferenceInKeyObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.SubClassSimpleKeyObjImpl;
import de.jdynameta.testcommon.util.sql.UtilDate;

/**
 *
 */
public abstract class JdyObjectReadTest extends TestCase
{

    /**
     * Creates the ObjectWriterTest object.
     *
     */
    public JdyObjectReadTest()
    {
        super();
    }

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdyObjectReadTest(String name)
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
     * Insert Object with Objectwriter
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadAllPrimitiveTypeObj() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getAllPrimitiveTypeClassInfo();

        AllAttributeTypesImpl valueObjToInsert = new AllAttributeTypesImpl();

        Timestamp testDate = new Timestamp(System.currentTimeMillis());

        byte[] clobValue = new byte[10000];
        Arrays.fill(clobValue, (byte) 'r');

        valueObjToInsert.setBooleanData(true);
        valueObjToInsert.setClobData(new String(clobValue));
        valueObjToInsert.setBlobData(new BlobByteArrayHolder(clobValue));
        valueObjToInsert.setCurrencyData(new BigDecimal(3456.78));
        valueObjToInsert.setDateData(new Date(System.currentTimeMillis()));
        valueObjToInsert.setFloatData(876.989);
        valueObjToInsert.setIntegerData(new Long(1234567));
        valueObjToInsert.setLongData(new Long(33445566));
        valueObjToInsert.setTextData("Test data");
        valueObjToInsert.setTimeData(new Timestamp(System.currentTimeMillis()));
        valueObjToInsert.setTimestampData(testDate);
        valueObjToInsert.setVarCharData("Varchar data");

        writer.insertObjectInDb(valueObjToInsert, testClassInfo);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(testClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        Object result = readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("BlobDataEx"));

        assertEquals(new Boolean(true), readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("BooleanDataEx")));
        assertEquals(new BigDecimal("3456.780"), readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("CurrencyDataEx")));
        assertEquals(new Double(876.989), readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("FloatDataEx")));
        assertEquals(new Long(1234567), readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("IntegerDataEx")));
        assertEquals(new Long(33445566), readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("LongDataEx")));
        assertEquals("Test data", readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("TextDataEx")));
//		assertEquals(testDate, readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("TimestampDataEx")));
        assertEquals("Varchar data", readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("VarCharDataEx")));

    }

    public void testReadVarCharObj() throws Exception
    {

        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getAllPrimitiveTypeClassInfo();

        AllAttributeTypesImpl valueObjToInsert = new AllAttributeTypesImpl();

        String testText = "Varchar data\r\nwith carrige return\r\ntest&auml;&Uuml;";

        valueObjToInsert.setIntegerData(new Long(1234567));
        valueObjToInsert.setVarCharData(testText);

        writer.insertObjectInDb(valueObjToInsert, testClassInfo);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(testClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);

        assertEquals(new Long(1234567), readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("IntegerDataEx")));
        String readedText = (String) readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("VarCharDataEx"));
        assertEquals(testText, readedText);

    }

    /**
     * Insert Object with Objectwriter
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadSimpleKeyObj() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo simpleKeyClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();

        SimpleKeyObjImpl simpleKeyObj = new SimpleKeyObjImpl();

        simpleKeyObj.setSimpleIntKey(new Long(10));
        simpleKeyObj.setSimpleKeyData1("Test String");
        simpleKeyObj.setSimpleKeyData2(UtilDate.createSqlDate(2006, 5, 25));
        writer.insertObjectInDb(simpleKeyObj, simpleKeyClassInfo);

        assertValueIsSet(simpleKeyClassInfo, (PrimitiveAttributeInfo) simpleKeyClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"), simpleKeyObj);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(simpleKeyClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);

        readedObj.getValue(simpleKeyClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex")).equals("Test String");

    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadCompoundKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundKeyObj = new CompoundKeyObjImpl();
        compoundKeyObj.setCompoundIntKey(new Long(10));
        compoundKeyObj.setCompoundKeyData(UtilDate.createSqlDate(2006, 5, 25));
        compoundKeyObj.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundKeyObj, testClassInfo);

        assertValueIsSet(testClassInfo, (PrimitiveAttributeInfo) testClassInfo
                .getAttributeInfoForExternalName("CompoundKeyDataEx"), compoundKeyObj);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(testClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("CompoundKeyDataEx")).equals("Test coumpoundkey");
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadReferencesClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getReferencesClassInfo();
        ClassInfo referenceClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        SimpleKeyObjImpl simpleRefObjToInsert = new SimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(new Long(10));
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleRefObjToInsert, referenceClassInfo);

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(new Long(10));
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        ReferenceObjImpl valueObjWithRefToInsert = new ReferenceObjImpl();
        valueObjWithRefToInsert.setReferenceKey(new Long(10));
        valueObjWithRefToInsert.setReferenceData("Test reference data");
        valueObjWithRefToInsert.setSimpleRef(simpleRefObjToInsert);
        valueObjWithRefToInsert.setCompoundRef(compoundObjToInsert);
        writer.insertObjectInDb(valueObjWithRefToInsert, testClassInfo);

        assertValueIsSet(testClassInfo, (PrimitiveAttributeInfo) testClassInfo.getAttributeInfoForExternalName("RefDataEx"), valueObjWithRefToInsert);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(testClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(testClassInfo.getAttributeInfoForExternalName("RefDataEx")).equals("Test reference data");
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadReferenceInPrimaryKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo refInPrimKeyClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInPrimaryKeyClassInfo();
        ClassInfo referenceClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(new Long(10));
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        SimpleKeyObjImpl simpleRefObjToInsert = new SimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(new Long(10));
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleRefObjToInsert, referenceClassInfo);

        ReferenceInPrimaryKeyImpl valueObjWithRefToInsert = new ReferenceInPrimaryKeyImpl();
        valueObjWithRefToInsert.setCompoundRefKey(compoundObjToInsert);
        valueObjWithRefToInsert.setReferenceData("Test referece data");
        valueObjWithRefToInsert.setSimpleIntKey(new Long(34));
        valueObjWithRefToInsert.setSimpleRefKey(simpleRefObjToInsert);
        writer.insertObjectInDb(valueObjWithRefToInsert, refInPrimKeyClassInfo);

        assertValueIsSet(refInPrimKeyClassInfo, (PrimitiveAttributeInfo) refInPrimKeyClassInfo
                .getAttributeInfoForExternalName("ReferenceDataEx"), valueObjWithRefToInsert);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(refInPrimKeyClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(refInPrimKeyClassInfo.getAttributeInfoForExternalName("ReferenceDataEx")).equals("Test reference data");
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadReferenceInKeyDeepClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo refDeepClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInKeyDeepClassInfo();
        ClassInfo referenceClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInPrimaryKeyClassInfo();
        ClassInfo simpleClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(new Long(10));
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        SimpleKeyObjImpl simpleRefObjToInsert = new SimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(new Long(10));
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleRefObjToInsert, simpleClassInfo);

        ReferenceInPrimaryKeyImpl valueObjWithRefToInsert = new ReferenceInPrimaryKeyImpl();
        valueObjWithRefToInsert.setCompoundRefKey(compoundObjToInsert);
        valueObjWithRefToInsert.setReferenceData("Test referece data");
        valueObjWithRefToInsert.setSimpleIntKey(new Long(34));
        valueObjWithRefToInsert.setSimpleRefKey(simpleRefObjToInsert);
        writer.insertObjectInDb(valueObjWithRefToInsert, referenceClassInfo);

        KeyRefDeepObjImpl valueObjDeepRef = new KeyRefDeepObjImpl();
        valueObjDeepRef.setDeepRef(valueObjWithRefToInsert);
        valueObjDeepRef.setBonusDate(UtilDate.createSqlDate(2005, 10, 21));
        writer.insertObjectInDb(valueObjDeepRef, refDeepClassInfo);

        assertValueIsSet(refDeepClassInfo, (PrimitiveAttributeInfo) refDeepClassInfo
                .getAttributeInfoForExternalName("BonusDateEx"), valueObjDeepRef);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(refDeepClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(refDeepClassInfo.getAttributeInfoForExternalName("BonusDateEx"))
                .equals(UtilDate.createSqlDate(2005, 10, 21));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadReferenceOnSelfKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo refOnSelfClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceOnSelfKeyClassInfo();

        ReferenceOnSelfObjImpl compoundObjToInsert = new ReferenceOnSelfObjImpl();
        compoundObjToInsert.setSimpleIntKey(new Long(234324));
        compoundObjToInsert.setSimpleTextKey("Test selfref");
        compoundObjToInsert.setSimpleData(UtilDate.createSqlDate(2006, 5, 25));
        writer.insertObjectInDb(compoundObjToInsert, refOnSelfClassInfo);

        ReferenceOnSelfObjImpl compoundObjToInsert2 = new ReferenceOnSelfObjImpl();
        compoundObjToInsert2.setSimpleIntKey(new Long(77777));
        compoundObjToInsert2.setSimpleTextKey("Test selfref 222");
        compoundObjToInsert2.setSimpleData(UtilDate.createSqlDate(2006, 5, 25));
        compoundObjToInsert2.setSelfRef(compoundObjToInsert);
        writer.insertObjectInDb(compoundObjToInsert2, refOnSelfClassInfo);

        assertValueIsSet(refOnSelfClassInfo, (PrimitiveAttributeInfo) refOnSelfClassInfo
                .getAttributeInfoForExternalName("SimpleDataEx"), compoundObjToInsert2);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(refOnSelfClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 2);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(refOnSelfClassInfo.getAttributeInfoForExternalName("SimpleDataEx"))
                .equals(UtilDate.createSqlDate(2006, 5, 25));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadReferenceTwoOnSameObjClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo refOnSameClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceTwoOnSameObjClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundObjToInsert1 = new CompoundKeyObjImpl();
        compoundObjToInsert1.setCompoundIntKey(new Long(10));
        compoundObjToInsert1.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert1.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert1, compoundClassInfo);

        CompoundKeyObjImpl compoundObjToInsert2 = new CompoundKeyObjImpl();
        compoundObjToInsert2.setCompoundIntKey(new Long(88888));
        compoundObjToInsert2.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert2.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert2, compoundClassInfo);

        ReferenceTwoOnSameObjImpl refObjToInsert = new ReferenceTwoOnSameObjImpl();
        refObjToInsert.setSimpleIntKey(new Long(234324));
        refObjToInsert.setSimpleData1("Test simple data 1");
        refObjToInsert.setSimpleData2(new Date(System.currentTimeMillis()));
        refObjToInsert.setCompoundRef1(compoundObjToInsert1);
        refObjToInsert.setCompoundRef2(compoundObjToInsert2);
        writer.insertObjectInDb(refObjToInsert, refOnSameClassInfo);

        assertValueIsSet(refOnSameClassInfo, (PrimitiveAttributeInfo) refOnSameClassInfo
                .getAttributeInfoForExternalName("SimpleData1Ex"), refObjToInsert);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(refOnSameClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(refOnSameClassInfo.getAttributeInfoForExternalName("SimpleData1Ex"))
                .equals("Test simple data 1");
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadAssocMasterClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocMasterClassInfo = new ComplexTestDataMetaInfoRepository().getAssocMasterClassInfo();
        ClassInfo referenceClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInPrimaryKeyClassInfo();
        ClassInfo simpleClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(new Long(10));
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        SimpleKeyObjImpl simpleRefObjToInsert = new SimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(new Long(10));
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleRefObjToInsert, simpleClassInfo);

        ReferenceInPrimaryKeyImpl refInPrimKeyToInsert = new ReferenceInPrimaryKeyImpl();
        refInPrimKeyToInsert.setCompoundRefKey(compoundObjToInsert);
        refInPrimKeyToInsert.setReferenceData("Test referece data");
        refInPrimKeyToInsert.setSimpleIntKey(new Long(34));
        refInPrimKeyToInsert.setSimpleRefKey(simpleRefObjToInsert);
        writer.insertObjectInDb(refInPrimKeyToInsert, referenceClassInfo);

        AssociationMasterObjImpl assocMasterObj = new AssociationMasterObjImpl();
        assocMasterObj.setDeepRef(refInPrimKeyToInsert);
        assocMasterObj.setBonusDate(UtilDate.createSqlDate(2006, 5, 25));
        writer.insertObjectInDb(assocMasterObj, assocMasterClassInfo);

        assertValueIsSet(assocMasterClassInfo, (PrimitiveAttributeInfo) assocMasterClassInfo
                .getAttributeInfoForExternalName("BonusDateEx"), assocMasterObj);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocMasterClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(assocMasterClassInfo.getAttributeInfoForExternalName("BonusDateEx"))
                .equals(UtilDate.createSqlDate(2006, 5, 25));
    }

    private AssociationMasterObjImpl createMasterObj(ObjectWriter aWriter, Long aCoumpoundKey, Long aSimpleKey, Long aRefKey) throws Exception
    {
        ClassInfo assocMasterClassInfo = new ComplexTestDataMetaInfoRepository().getAssocMasterClassInfo();
        ClassInfo referenceClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInPrimaryKeyClassInfo();
        ClassInfo simpleClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(aCoumpoundKey);
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        aWriter.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        SimpleKeyObjImpl simpleRefObjToInsert = new SimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(aSimpleKey);
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        aWriter.insertObjectInDb(simpleRefObjToInsert, simpleClassInfo);

        ReferenceInPrimaryKeyImpl refInPrimKeyToInsert = new ReferenceInPrimaryKeyImpl();
        refInPrimKeyToInsert.setCompoundRefKey(compoundObjToInsert);
        refInPrimKeyToInsert.setReferenceData("Test referece data");
        refInPrimKeyToInsert.setSimpleIntKey(aRefKey);
        refInPrimKeyToInsert.setSimpleRefKey(simpleRefObjToInsert);
        aWriter.insertObjectInDb(refInPrimKeyToInsert, referenceClassInfo);

        AssociationMasterObjImpl assocMasterObj = new AssociationMasterObjImpl();
        assocMasterObj.setDeepRef(refInPrimKeyToInsert);
        assocMasterObj.setBonusDate(new Date(System.currentTimeMillis()));
        aWriter.insertObjectInDb(assocMasterObj, assocMasterClassInfo);

        return assocMasterObj;
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadAssocDetailSimpleInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailSimpleInfo();

        AssocDetailSimpleObjImpl assocDetailSimpl = new AssocDetailSimpleObjImpl();
        assocDetailSimpl.setSimpleIntKey(new Long(10));
        assocDetailSimpl.setSimpleKeyData1("Test simple ref");
        assocDetailSimpl.setAssocSimpleRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        assocDetailSimpl.setAssocSimpleRef2(createMasterObj(writer, new Long(666), new Long(777), new Long(888)));
        writer.insertObjectInDb(assocDetailSimpl, assocDetailClassInfo);

        assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo
                .getAttributeInfoForExternalName("SimpleKeyData1Ex"), assocDetailSimpl);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocDetailClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(assocDetailClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"))
                .equals(UtilDate.createSqlDate(2006, 5, 25));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadAssocDetailKeyInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailKeyInfo();

        AssocDetailKeyObjImpl assocDetailKey = new AssocDetailKeyObjImpl();
        assocDetailKey.setSimpleIntKey(new Long(10));
        assocDetailKey.setSimpleKeyData1("Test simple ref");
        assocDetailKey.setAssocKeyRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        writer.insertObjectInDb(assocDetailKey, assocDetailClassInfo);

        assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo
                .getAttributeInfoForExternalName("SimpleKeyData1Ex"), assocDetailKey);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocDetailClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(assocDetailClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"))
                .equals(UtilDate.createSqlDate(2006, 5, 25));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadAssocDetailNotNullInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailNotNullInfo();

        AssocDetailNotNullObjImpl assocDetailNotNull = new AssocDetailNotNullObjImpl();
        assocDetailNotNull.setSimpleIntKey(new Long(10));
        assocDetailNotNull.setSimpleKeyData1("Test simple ref");
        assocDetailNotNull.setAssocNotNullRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        writer.insertObjectInDb(assocDetailNotNull, assocDetailClassInfo);

        assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo
                .getAttributeInfoForExternalName("SimpleKeyData1Ex"), assocDetailNotNull);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocDetailClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(assocDetailClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"))
                .equals(UtilDate.createSqlDate(2006, 5, 25));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadSubclassSimpleKeyObjInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo subclassSimpleKeyClassInfo = new ComplexTestDataMetaInfoRepository().getSubClassSimpleKeyObjInfo();

        SubClassSimpleKeyObjImpl assocSubclassDetailKey = new SubClassSimpleKeyObjImpl();
        assocSubclassDetailKey.setSimpleIntKey(new Long(10));
        assocSubclassDetailKey.setSimpleKeyData1("Test simple ref");
        assocSubclassDetailKey.setSimpleDataSub1("sub class data");
        writer.insertObjectInDb(assocSubclassDetailKey, subclassSimpleKeyClassInfo);

        assertValueIsSet(subclassSimpleKeyClassInfo, (PrimitiveAttributeInfo) subclassSimpleKeyClassInfo
                .getAttributeInfoForExternalName("SimpleDataSub1Ex"), assocSubclassDetailKey);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(subclassSimpleKeyClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(subclassSimpleKeyClassInfo.getAttributeInfoForExternalName("SimpleDataSub1Ex"))
                .equals(UtilDate.createSqlDate(2006, 5, 25));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReadSubClassReferenceInKeyDeepClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getSubClassReferenceInKeyClassInfo();
        ClassInfo simpleClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
        ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(new Long(10));
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        SimpleKeyObjImpl simpleRefObjToInsert = new SimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(new Long(10));
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleRefObjToInsert, simpleClassInfo);

        SubClassReferenceInKeyObjImpl assocSubclassDetailKey = new SubClassReferenceInKeyObjImpl();
        assocSubclassDetailKey.setCompoundRefKey(compoundObjToInsert);
        assocSubclassDetailKey.setReferenceData("Test referece data");
        assocSubclassDetailKey.setSimpleIntKey(new Long(34));
        assocSubclassDetailKey.setSimpleRefKey(simpleRefObjToInsert);
        assocSubclassDetailKey.setCompundRefSub1(compoundObjToInsert);
        writer.insertObjectInDb(assocSubclassDetailKey, assocDetailClassInfo);

//		assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo
//						.getAttributeInfoForExternalName("BonusDateEx"), assocSubclassDetailKey);
        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(assocDetailClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ValueObject readedObj = (ValueObject) readedObjectColl.get(0);
        readedObj.getValue(assocDetailClassInfo.getAttributeInfoForExternalName("ReferenceDataEx"))
                .equals(UtilDate.createSqlDate(2005, 10, 21));
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testgetReferencOnSubclassClassInfo() throws Exception
    {
        ComplexTestDataMetaInfoRepository rep = new ComplexTestDataMetaInfoRepository();
        ObjectWriter writer = getObjectWriter();
        ClassInfo referenceOnSubClassInfo = rep.getReferencOnSubclassClassInfo();
        ClassInfo simpleRefSubClassInfo = rep.getSubClassSimpleKeyObjInfo();
        ClassInfo refSubClassInfo = rep.getSubClassReferenceInKeyClassInfo();
        ClassInfo compoundClassInfo = rep.getCompoundKeyClassInfo();
        ClassInfo simpleKeyClassInfo = rep.getSimpleKeyClassInfo();

        SubClassSimpleKeyObjImpl simpleRefObjToInsert = new SubClassSimpleKeyObjImpl();
        simpleRefObjToInsert.setSimpleIntKey(new Long(10));
        simpleRefObjToInsert.setSimpleKeyData1("Test String");
        simpleRefObjToInsert.setSimpleDataSub1("Datasub1 data");
        simpleRefObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleRefObjToInsert, simpleRefSubClassInfo);

        SimpleKeyObjImpl simpleKeyObj = new SimpleKeyObjImpl();
        simpleKeyObj.setSimpleIntKey(new Long(10));
        simpleKeyObj.setSimpleKeyData1("Test String");
        simpleKeyObj.setSimpleKeyData2(UtilDate.createSqlDate(2006, 5, 25));
        writer.insertObjectInDb(simpleKeyObj, simpleKeyClassInfo);

        CompoundKeyObjImpl compoundObjToInsert = new CompoundKeyObjImpl();
        compoundObjToInsert.setCompoundIntKey(new Long(10));
        compoundObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        compoundObjToInsert.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundObjToInsert, compoundClassInfo);

        SubClassReferenceInKeyObjImpl refInKeySub = new SubClassReferenceInKeyObjImpl();
        refInKeySub.setCompoundRefKey(compoundObjToInsert);
        refInKeySub.setSimpleIntKey(new Long(888));
        refInKeySub.setSimpleRefKey(simpleKeyObj);
        refInKeySub.setReferenceData("Subclass ref data");
        writer.insertObjectInDb(refInKeySub, refSubClassInfo);

        ReferenceOnSubclassObjImpl refOnSubclassToInsert = new ReferenceOnSubclassObjImpl();
        refOnSubclassToInsert.setReferenceKey(new Long(10));
        refOnSubclassToInsert.setReferenceData("Test reference data");
        refOnSubclassToInsert.setSimpleRef(simpleRefObjToInsert);
        refOnSubclassToInsert.setRefInKeyRef(refInKeySub);
        writer.insertObjectInDb(refOnSubclassToInsert, referenceOnSubClassInfo);

        assertValueIsSet(referenceOnSubClassInfo, (PrimitiveAttributeInfo) referenceOnSubClassInfo.getAttributeInfoForExternalName("RefDataEx"), refOnSubclassToInsert);

        ObjectReader reader = getObjectReader();
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();
        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(referenceOnSubClassInfo);
        ObjectList readedObjectColl = reader.loadValuesFromDb(filter, tmpObjCreator);
        assertTrue(readedObjectColl.size() == 1);
        ClassInfoValueObject readedObj = (ClassInfoValueObject) readedObjectColl.get(0);
        readedObj.getValue(referenceOnSubClassInfo.getAttributeInfoForExternalName("RefDataEx")).equals("Test reference data");
        assertEquals(readedObj.getClassInfo(), referenceOnSubClassInfo);

        assertEquals(simpleRefSubClassInfo, ((ClassInfoValueObject) readedObj.getValue(referenceOnSubClassInfo.getAttributeInfoForExternalName("SimpleRefEx"))).getClassInfo());
        assertEquals(refSubClassInfo, ((ClassInfoValueObject) readedObj.getValue(referenceOnSubClassInfo.getAttributeInfoForExternalName("RefInKeyRefEx"))).getClassInfo());
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

        /* (non-Javadoc)
         * @see de.comafra.model.persistence.ObjectCreator#createObjectFor(de.comafra.model.metainfo.ClassInfo, de.comafra.model.value.ValueModel, boolean)
         */
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
