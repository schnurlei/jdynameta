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
import java.sql.Date;
import java.sql.SQLException;

import junit.framework.TestCase;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;
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
public abstract class JdyObjectDeleteTest extends TestCase
{

    public JdyObjectDeleteTest()
    {
        super();
    }

    /**
     * Creates the ObjectWriterTest object.
     *
     * @param name DOCUMENT ME!
     */
    public JdyObjectDeleteTest(String name)
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
    public void testDeleteSimpleKeyObj() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo simpleKeyClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();

        SimpleKeyObjImpl simpleKeyObj = new SimpleKeyObjImpl();
        simpleKeyObj.setSimpleIntKey(new Long(10));
        simpleKeyObj.setSimpleKeyData1("Test String");
        simpleKeyObj.setSimpleKeyData2(new Date(System.currentTimeMillis()));
        writer.insertObjectInDb(simpleKeyObj, simpleKeyClassInfo);
        assertValueIsSet(simpleKeyClassInfo, (PrimitiveAttributeInfo) simpleKeyClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"), simpleKeyObj);

        writer.deleteObjectInDb(simpleKeyObj, simpleKeyClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteCompoundKeyClassInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo testClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

        CompoundKeyObjImpl compoundKeyObj = new CompoundKeyObjImpl();
        compoundKeyObj.setCompoundIntKey(new Long(10));
        compoundKeyObj.setCompoundKeyData(UtilDate.createSqlDate(2006, 5, 25));
        compoundKeyObj.setCompoundTxtKey("Test coumpoundkey");
        writer.insertObjectInDb(compoundKeyObj, testClassInfo);

        assertValueIsSet(testClassInfo, (PrimitiveAttributeInfo) testClassInfo.getAttributeInfoForExternalName("CompoundKeyDataEx"), compoundKeyObj);
        writer.deleteObjectInDb(compoundKeyObj, testClassInfo);

    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteReferencesClassInfo() throws Exception
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

        assertValueIsSet(testClassInfo, (PrimitiveAttributeInfo) testClassInfo
                .getAttributeInfoForExternalName("RefDataEx"), valueObjWithRefToInsert);
        writer.deleteObjectInDb(valueObjWithRefToInsert, testClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteReferenceInPrimaryKeyClassInfo() throws Exception
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

        assertValueIsSet(refInPrimKeyClassInfo, (PrimitiveAttributeInfo) refInPrimKeyClassInfo.getAttributeInfoForExternalName("ReferenceDataEx"), valueObjWithRefToInsert);

        writer.deleteObjectInDb(valueObjWithRefToInsert, refInPrimKeyClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteReferenceInKeyDeepClassInfo() throws Exception
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

        writer.deleteObjectInDb(valueObjDeepRef, refDeepClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteReferenceOnSelfKeyClassInfo() throws Exception
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
        compoundObjToInsert2.setSimpleData(UtilDate.createSqlDate(2006, 5, 25));
        compoundObjToInsert2.setSelfRef(compoundObjToInsert);
        writer.insertObjectInDb(compoundObjToInsert2, refOnSelfClassInfo);

        assertValueIsSet(refOnSelfClassInfo, (PrimitiveAttributeInfo) refOnSelfClassInfo.getAttributeInfoForExternalName("SimpleDataEx"), compoundObjToInsert2);
        writer.deleteObjectInDb(compoundObjToInsert2, refOnSelfClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteReferenceTwoOnSameObjClassInfo() throws Exception
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

        assertValueIsSet(refOnSameClassInfo, (PrimitiveAttributeInfo) refOnSameClassInfo.getAttributeInfoForExternalName("SimpleData1Ex"), refObjToInsert);

        writer.deleteObjectInDb(refObjToInsert, refOnSameClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteAssocMasterClassInfo() throws Exception
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
        assocMasterObj.setBonusDate(UtilDate.createSqlDate(2005, 10, 21));
        writer.insertObjectInDb(assocMasterObj, assocMasterClassInfo);

        assertValueIsSet(assocMasterClassInfo, (PrimitiveAttributeInfo) assocMasterClassInfo.getAttributeInfoForExternalName("BonusDateEx"), assocMasterObj);
        writer.deleteObjectInDb(assocMasterObj, assocMasterClassInfo);
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
        assocMasterObj.setBonusDate(UtilDate.createSqlDate(2005, 10, 21));
        aWriter.insertObjectInDb(assocMasterObj, assocMasterClassInfo);

        return assocMasterObj;

    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteAssocDetailSimpleInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailSimpleInfo();

        AssocDetailSimpleObjImpl assocDetailSimpl = new AssocDetailSimpleObjImpl();
        assocDetailSimpl.setSimpleIntKey(new Long(10));
        assocDetailSimpl.setSimpleKeyData1("Test simple ref");
        assocDetailSimpl.setAssocSimpleRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        assocDetailSimpl.setAssocSimpleRef2(createMasterObj(writer, new Long(666), new Long(777), new Long(888)));
        writer.insertObjectInDb(assocDetailSimpl, assocDetailClassInfo);

        assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"), assocDetailSimpl);
        writer.deleteObjectInDb(assocDetailSimpl, assocDetailClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteAssocDetailKeyInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailKeyInfo();

        AssocDetailKeyObjImpl assocDetailKey = new AssocDetailKeyObjImpl();
        assocDetailKey.setSimpleIntKey(new Long(10));
        assocDetailKey.setSimpleKeyData1("Test simple ref");
        assocDetailKey.setAssocKeyRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        writer.insertObjectInDb(assocDetailKey, assocDetailClassInfo);

        assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"), assocDetailKey);
        writer.deleteObjectInDb(assocDetailKey, assocDetailClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteAssocDetailNotNullInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailNotNullInfo();

        AssocDetailNotNullObjImpl assocDetailNotNull = new AssocDetailNotNullObjImpl();
        assocDetailNotNull.setSimpleIntKey(new Long(10));
        assocDetailNotNull.setSimpleKeyData1("Test simple ref");
        assocDetailNotNull.setAssocNotNullRef(createMasterObj(writer, new Long(222), new Long(333), new Long(444)));
        writer.insertObjectInDb(assocDetailNotNull, assocDetailClassInfo);

        assertValueIsSet(assocDetailClassInfo, (PrimitiveAttributeInfo) assocDetailClassInfo.getAttributeInfoForExternalName("SimpleKeyData1Ex"), assocDetailNotNull);
        writer.deleteObjectInDb(assocDetailNotNull, assocDetailClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteSubclassSimpleKeyObjInfo() throws Exception
    {
        ObjectWriter writer = getObjectWriter();
        ClassInfo subclassSimpleKeyClassInfo = new ComplexTestDataMetaInfoRepository().getSubClassSimpleKeyObjInfo();

        SubClassSimpleKeyObjImpl assocSubclassDetailKey = new SubClassSimpleKeyObjImpl();
        assocSubclassDetailKey.setSimpleIntKey(new Long(10));
        assocSubclassDetailKey.setSimpleKeyData1("Test simple ref");
        assocSubclassDetailKey.setSimpleDataSub1("sub class data");
        writer.insertObjectInDb(assocSubclassDetailKey, subclassSimpleKeyClassInfo);

        assertValueIsSet(subclassSimpleKeyClassInfo, (PrimitiveAttributeInfo) subclassSimpleKeyClassInfo.getAttributeInfoForExternalName("SimpleDataSub1Ex"), assocSubclassDetailKey);
        writer.deleteObjectInDb(assocSubclassDetailKey, subclassSimpleKeyClassInfo);
    }

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testDeleteSubClassReferenceInKeyDeepClassInfo() throws Exception
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

        writer.deleteObjectInDb(assocSubclassDetailKey, assocDetailClassInfo);
    }

    /**
     * assert that the attribute of the given valueobject is set in the target
     * db
     *
     */
    protected abstract void assertValueIsSet(ClassInfo aClassInfo, PrimitiveAttributeInfo aPrimitivAttr, ValueObject value);
}
