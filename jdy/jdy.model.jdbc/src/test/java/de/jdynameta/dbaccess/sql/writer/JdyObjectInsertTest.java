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
package de.jdynameta.dbaccess.sql.writer;

/*
       
 */
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

import junit.framework.TestCase;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.value.ValueObject;
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
import de.jdynameta.testcommon.model.testdata.impl.impl.ReferenceTwoOnSameObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.SimpleKeyObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.SubClassReferenceInKeyObjImpl;
import de.jdynameta.testcommon.model.testdata.impl.impl.SubClassSimpleKeyObjImpl;
import de.jdynameta.testcommon.util.sql.UtilDate;

/**
 *
 */
public abstract class JdyObjectInsertTest extends TestCase
{

    public JdyObjectInsertTest()
    {
        super();
    }

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdyObjectInsertTest(String name)
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

    /**
     * Insert Object with Objectwriter
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testInsertAllPrimitiveTypeObj() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getAllPrimitiveTypeClassInfo();

        AllAttributeTypesImpl valueObjToInsert = new AllAttributeTypesImpl();

        byte[] clobValue = new byte[4000];
        Arrays.fill(clobValue, (byte) 'r');

        valueObjToInsert.setBooleanData(true);
        valueObjToInsert.setClobData(new String(clobValue));
        valueObjToInsert.setBlobData(new BlobByteArrayHolder(clobValue));
        valueObjToInsert.setCurrencyData(new BigDecimal(3456.78));
        valueObjToInsert.setDateData(new Date(System.currentTimeMillis()));
        valueObjToInsert.setFloatData(876.989);
        valueObjToInsert.setIntegerData(new Long(1234567));
        valueObjToInsert.setLongData(new Long(1234567));
        valueObjToInsert.setTextData("Test data");
        valueObjToInsert.setTimeData(new Timestamp(System.currentTimeMillis()));
        valueObjToInsert.setTimestampData(new Timestamp(System.currentTimeMillis()));
        valueObjToInsert.setVarCharData("Varchar data");

        writer.insertObjectInDb(valueObjToInsert, testClassInfo);

        // insert null values
        valueObjToInsert.setBooleanData(null);
        valueObjToInsert.setClobData(null);
        valueObjToInsert.setBlobData(null);
        valueObjToInsert.setCurrencyData(null);
        valueObjToInsert.setDateData(null);
        valueObjToInsert.setFloatData(null);
        valueObjToInsert.setIntegerData(new Long(55555));
        valueObjToInsert.setLongData(null);
        valueObjToInsert.setTextData(null);
        valueObjToInsert.setTimeData(null);
        valueObjToInsert.setTimestampData(null);
        valueObjToInsert.setVarCharData(null);

        writer.insertObjectInDb(valueObjToInsert, testClassInfo);

    }

    /**
     * Insert Object with Objectwriter
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testInsertSimpleKeyObj() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();

        SimpleKeyObjImpl valueObjToInsert = new SimpleKeyObjImpl();

        valueObjToInsert.setSimpleIntKey(new Long(10));
        valueObjToInsert.setSimpleKeyData1("Test String");
        valueObjToInsert.setSimpleKeyData2(new Date(System.currentTimeMillis()));

        writer.insertObjectInDb(valueObjToInsert, testClassInfo);

        assertValueIsSet(testClassInfo, (PrimitiveAttributeInfo) testClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"), valueObjToInsert);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testCompoundKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl valueObjToInsert = new CompoundKeyObjImpl();
        valueObjToInsert.setCompoundIntKey(new Long(10));
        valueObjToInsert.setCompoundKeyData(new Date(System.currentTimeMillis()));
        valueObjToInsert.setCompoundTxtKey("Test coumpoundkey");

        writer.insertObjectInDb(valueObjToInsert, testClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReferencesClassInfo() throws Exception
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
        valueObjWithRefToInsert.setReferenceData("Test referece data");
        valueObjWithRefToInsert.setSimpleRef(simpleRefObjToInsert);
        valueObjWithRefToInsert.setCompoundRef(compoundObjToInsert);

        writer.insertObjectInDb(valueObjWithRefToInsert, testClassInfo);

    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReferenceInPrimaryKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInPrimaryKeyClassInfo();
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
        writer.insertObjectInDb(valueObjWithRefToInsert, testClassInfo);

        assertValueIsSet(testClassInfo, (PrimitiveAttributeInfo) testClassInfo.getAttributeInfoForExternalName("ReferenceDataEx"), valueObjWithRefToInsert);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReferenceInKeyDeepClassInfo() throws Exception
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

        assertValueIsSet(refDeepClassInfo, (PrimitiveAttributeInfo) refDeepClassInfo.getAttributeInfoForExternalName("BonusDateEx"), valueObjDeepRef);

    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReferenceOnSelfKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo refOnSelfClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceOnSelfKeyClassInfo();

        ReferenceOnSelfObjImpl compoundObjToInsert = new ReferenceOnSelfObjImpl();
        compoundObjToInsert.setSimpleIntKey(new Long(234324));
        compoundObjToInsert.setSimpleTextKey("Test selfref");
        compoundObjToInsert.setSimpleData(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(compoundObjToInsert, refOnSelfClassInfo);

        ReferenceOnSelfObjImpl compoundObjToInsert2 = new ReferenceOnSelfObjImpl();
        compoundObjToInsert2.setSimpleIntKey(new Long(77777));
        compoundObjToInsert2.setSimpleTextKey("Test selfref 222");
        compoundObjToInsert2.setSimpleData(new Date(System.currentTimeMillis()));
        compoundObjToInsert2.setSelfRef(compoundObjToInsert);
        writer.insertObjectInDb(compoundObjToInsert2, refOnSelfClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testReferenceTwoOnSameObjClassInfo() throws Exception
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

    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testAssocMasterClassInfo() throws Exception
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
        assocMasterObj.setBonusDate(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(assocMasterObj, assocMasterClassInfo);

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
    public void testAssocDetailSimpleInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailSimpleInfo();

        AssocDetailSimpleObjImpl assocDetailSimpl = new AssocDetailSimpleObjImpl();
        assocDetailSimpl.setSimpleIntKey(new Long(10));
        assocDetailSimpl.setSimpleKeyData1("Test simple ref");
        assocDetailSimpl.setAssocSimpleRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        assocDetailSimpl.setAssocSimpleRef2(createMasterObj(writer, new Long(666), new Long(777), new Long(888)));
        writer.insertObjectInDb(assocDetailSimpl, assocDetailClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testAssocDetailKeyInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailKeyInfo();

        AssocDetailKeyObjImpl assocDetailKey = new AssocDetailKeyObjImpl();
        assocDetailKey.setSimpleIntKey(new Long(10));
        assocDetailKey.setSimpleKeyData1("Test simple ref");
        assocDetailKey.setAssocKeyRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        writer.insertObjectInDb(assocDetailKey, assocDetailClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testAssocDetailNotNullInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailNotNullInfo();

        AssocDetailNotNullObjImpl assocDetailNorNull = new AssocDetailNotNullObjImpl();
        assocDetailNorNull.setSimpleIntKey(new Long(10));
        assocDetailNorNull.setSimpleKeyData1("Test simple ref");
        assocDetailNorNull.setAssocNotNullRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        writer.insertObjectInDb(assocDetailNorNull, assocDetailClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testSubclassSimpleKeyObjInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo subclassSimpleKeyClassInfo = new ComplexTestDataMetaInfoRepository().getSubClassSimpleKeyObjInfo();

        SubClassSimpleKeyObjImpl assocSubclassDetailKey = new SubClassSimpleKeyObjImpl();
        assocSubclassDetailKey.setSimpleIntKey(new Long(10));
        assocSubclassDetailKey.setSimpleKeyData1("Test simple ref");
        assocSubclassDetailKey.setSimpleKeyData2(new java.util.Date());
        assocSubclassDetailKey.setSimpleDataSub1("sub class data");
        writer.insertObjectInDb(assocSubclassDetailKey, subclassSimpleKeyClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testSubClassReferenceInKeyDeepClassInfo() throws Exception
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
    }

    /**
     * assert that the attribute of the given valueobject is set in the target
     * db
     *
     */
    protected abstract void assertValueIsSet(ClassInfo aClassInfo, PrimitiveAttributeInfo aPrimitivAttr, ValueObject value);
}
