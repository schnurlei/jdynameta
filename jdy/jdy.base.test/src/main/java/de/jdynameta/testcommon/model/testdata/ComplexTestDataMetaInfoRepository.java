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
package de.jdynameta.testcommon.model.testdata;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.*;
import java.math.BigDecimal;

/**
 *
 *
 * @author rsc
 */
public class ComplexTestDataMetaInfoRepository extends JdyRepositoryModel
{
    /**
     * Class with all possible primitive Datatypes
     */
    private final JdyClassInfoModel allPrimitiveTypeObj;
    /**
     * Class with one primitve type as key
     */
    private final JdyClassInfoModel simpleKeyObj;
    /**
     * Class with several primitve type as key
     */
    private final JdyClassInfoModel compoundKeyObj;
    /**
     * Class with object reference to another object
     */
    private final JdyClassInfoModel referencesObj;
    /**
     * Class with object reference as key
     */
    private final JdyClassInfoModel referenceInKeyObj;
    /**
     * Class with reference on an object wich has an reference as key
     */
    private final JdyClassInfoModel referenceInKeyDeepObj;
    /**
     * Meta data with references to it self
     */
    private final JdyClassInfoModel referenceOnSelfObj;
    /**
     * Meta data with 2 references on the same Data
     */
    private final JdyClassInfoModel referenceTwoOnSameObj;

    /**
     * Masterclass for the following detail classes
     */
    private final JdyClassInfoModel associationMasterObj;
    private final JdyClassInfoModel assocDetailSimpleObj;
    private final JdyClassInfoModel assocDetailKeyObj;
    private final JdyClassInfoModel assocDetailNotNullObj;

    /**
     * Class with one primitve type as key wich has Sublasses
     */
    private final JdyClassInfoModel simpleKeyWithSubclassObj;

    /**
     * Class with object reference as key with Sublass
     */
    private final JdyClassInfoModel referenceInKeyWthSubObj;

    /**
     * Subclass of simpleKeyObj
     */
    private final JdyClassInfoModel subClassSimpleKeyObj;

    private final JdyClassInfoModel subClassReferenceInKeyDeepObj;

    /**
     * Class with a reference on a Class wich has subclasses
     */
    private final JdyClassInfoModel referenceOnSubClassObj;

    public ComplexTestDataMetaInfoRepository() throws InvalidClassInfoException
    {
        super("ComplexTestDataMeta");
        this.addListener(new DefaultClassRepositoryValidator());

        this.allPrimitiveTypeObj = this.addClassInfo("AllAttributeTypes").setExternalName("AllAttributeTypesEx").setShortName("AP");
        this.allPrimitiveTypeObj.addLongAttr("IntegerData", Integer.MIN_VALUE, Integer.MAX_VALUE).setIsKey(true).setExternalName("IntegerDataEx");
        this.allPrimitiveTypeObj.addBooleanAttr("BooleanData").setExternalName("BooleanDataEx");
        this.allPrimitiveTypeObj.addBlobAttr("BlobData").setExternalName("BlobDataEx");
        this.allPrimitiveTypeObj.addVarCharAttr("ClobData", 5000, true).setExternalName("ClobDataEx");
        this.allPrimitiveTypeObj.addDecimalAttr("CurrencyData", new BigDecimal("-999999999999.999"), new BigDecimal("999999999999.999"), 3).setExternalName("CurrencyDataEx");
        this.allPrimitiveTypeObj.addTimestampAttr("DateData", true, false).setExternalName("DateDataEx");
        this.allPrimitiveTypeObj.addFloatAttr("FloatData").setExternalName("FloatDataEx");
        this.allPrimitiveTypeObj.addLongAttr("LongData", Long.MIN_VALUE, Long.MAX_VALUE).setExternalName("LongDataEx");
        this.allPrimitiveTypeObj.addTextAttr("TextData", 70).setExternalName("TextDataEx");
        this.allPrimitiveTypeObj.addTimestampAttr("TimestampData", true, true).setExternalName("TimestampDataEx");
        this.allPrimitiveTypeObj.addTimestampAttr("TimeData", false, true).setExternalName("TimeDataEx");
        this.allPrimitiveTypeObj.addVarCharAttr("VarCharData", 200).setExternalName("VarCharDataEx");

        this.compoundKeyObj = this.addClassInfo("CompoundKeyObj").setExternalName("CompKeyObjEx").setShortName("CK");
        this.compoundKeyObj.addLongAttr("CompoundIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("CmpIntKyEx").setIsKey(true);
        this.compoundKeyObj.addTextAttr("CompoundTxtKey", 50).setExternalName("CmpTxtKyEx").setIsKey(true);
        this.compoundKeyObj.addTimestampAttr("CompoundKeyData", true, false).setExternalName("CompoundKeyDataEx");

        this.simpleKeyObj = this.addClassInfo("SimpleKeyObj").setExternalName("SimpleKeyObjEx").setShortName("SK");
        this.simpleKeyObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.simpleKeyObj.addTextAttr("SimpleKeyData1", 50).setExternalName("SimpleKeyData1Ex").setNotNull(true);
        this.simpleKeyObj.addTimestampAttr("SimpleKeyData2", true, false).setExternalName("SimpleKeyData2Ex");

        this.referenceInKeyObj = this.addClassInfo("ReferenceInPrimaryKey").setExternalName("RefInPrimaryKeyEx").setShortName("RK");
        this.referenceInKeyObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.referenceInKeyObj.addReference("SimpleRefKey", simpleKeyObj).setExternalName("SimpleRefKeyEx").setIsKey(true);
        this.referenceInKeyObj.addReference("CompoundRefKey", compoundKeyObj).setExternalName("CompoundRefKeyEx").setIsKey(true);
        this.referenceInKeyObj.addTextAttr("ReferenceData", 50).setExternalName("ReferenceDataEx").setNotNull(true);

        this.referenceInKeyDeepObj = this.addClassInfo("KeyRefDeepObj").setExternalName("KeyRefDeepObjEx").setShortName("RD");
        this.referenceInKeyDeepObj.addReference("DeepRef", this.referenceInKeyObj).setExternalName("DeepRefEx").setIsKey(true);
        this.referenceInKeyDeepObj.addTimestampAttr("BonusDate", true, false).setExternalName("BonusDateEx").setNotNull(true);

        this.referenceOnSelfObj = this.addClassInfo("ReferenceOnSelfObj").setExternalName("RefOnSelfObjEx").setShortName("RO");
        this.referenceOnSelfObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.referenceOnSelfObj.addTextAttr("SimpleTextKey", 50).setExternalName("SimpleTextKeyEx").setIsKey(true);
        this.referenceOnSelfObj.addTimestampAttr("SimpleData", true, false).setExternalName("SimpleDataEx");
        this.referenceOnSelfObj.addReference("SelfRef", this.referenceOnSelfObj).setExternalName("SelfRefEx");

        this.referencesObj = this.addClassInfo("ReferenceObj").setExternalName("ReferenceObjEx").setShortName("RS");
        this.referencesObj.addLongAttr("ReferenceKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("RefKeyEx").setIsKey(true);
        this.referencesObj.addTextAttr("ReferenceData", 50).setExternalName("RefDataEx").setNotNull(true);
        this.referencesObj.addReference("SimpleRef", simpleKeyObj).setExternalName("SimpleRefEx").setNotNull(true);
        this.referencesObj.addReference("CompoundRef", compoundKeyObj).setExternalName("CompoundRefEx").setNotNull(true);

        this.referenceTwoOnSameObj = this.addClassInfo("ReferenceTwoOnSameObj").setExternalName("RefTwoOnSameObjEx").setShortName("R");
        this.referenceTwoOnSameObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SmpIntKeyEx").setIsKey(true);
        this.referenceTwoOnSameObj.addTextAttr("SimpleData1", 50).setExternalName("SimpleData1Ex").setNotNull(true);
        this.referenceTwoOnSameObj.addTimestampAttr("SimpleData2", true, false).setExternalName("SimpleData2Ex");
        this.referenceTwoOnSameObj.addReference("CompoundRef1", compoundKeyObj).setExternalName("CompRef1Ex").setNotNull(true);
        this.referenceTwoOnSameObj.addReference("CompoundRef2", compoundKeyObj).setExternalName("CompRef2Ex").setNotNull(true);

        this.assocDetailKeyObj = this.addClassInfo("AssocDetailKeyObj").setExternalName("AscDetKyObjEx").setShortName("AK");
        this.assocDetailKeyObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.assocDetailKeyObj.addTextAttr("SimpleKeyData1", 50).setExternalName("SimpleKeyData1Ex").setNotNull(true);

        this.assocDetailNotNullObj = this.addClassInfo("AssocDetailNotNullObj").setExternalName("AscDetNtNllObjEx").setShortName("AN");
        this.assocDetailNotNullObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.assocDetailNotNullObj.addTextAttr("SimpleKeyData1", 50).setExternalName("SimpleKeyData1Ex").setNotNull(true);

        this.assocDetailSimpleObj = this.addClassInfo("AssocDetailSimpleObj").setExternalName("AscDetSmplObjEx").setShortName("ASS");
        this.assocDetailSimpleObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.assocDetailSimpleObj.addTextAttr("SimpleKeyData1", 50).setExternalName("SimpleKeyData1Ex").setNotNull(true);

        this.associationMasterObj = this.addClassInfo("AssociationMasterObj").setExternalName("AssocMasterObjEx").setShortName("AM");
        this.associationMasterObj.addReference("DeepRef", referenceInKeyObj).setExternalName("DeepRefEx").setIsKey(true);
        this.associationMasterObj.addTimestampAttr("BonusDate", true, false).setExternalName("BonusDateEx");

        addAssociation("SimpleRef", associationMasterObj, assocDetailSimpleObj, "AssocSimpleRef", "AscSmplRefEx", false, false, false);
        addAssociation("SimpleRef2", associationMasterObj, assocDetailSimpleObj, "AssocSimpleRef2", "AscSmplRef2Ex", false, false, false);
        addAssociation("KeyRef", associationMasterObj, assocDetailKeyObj, "AssocKeyRef", "AscKyRfEx", true, false, false);
        addAssociation("NotNullRef", associationMasterObj, assocDetailNotNullObj, "AssocNotNullRef", "AscNtNlRefEx", false, true, false);
        addAssociation("SelfRef", associationMasterObj, associationMasterObj, "AssocSelfRef", "AscSlfRfEx", false, false, false);

        this.simpleKeyWithSubclassObj = this.addClassInfo("SimpleKeyWithSubObj").setExternalName("SimpleKeyWithSubObjEx").setShortName("SKWS");
        this.simpleKeyWithSubclassObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.simpleKeyWithSubclassObj.addTextAttr("SimpleKeyData1", 50).setExternalName("SimpleKeyData1Ex").setNotNull(true);
        this.simpleKeyWithSubclassObj.addTimestampAttr("SimpleKeyData2", true, false).setExternalName("SimpleKeyData2Ex");

        this.referenceInKeyWthSubObj = this.addClassInfo("ReferendeInPrimaryKeyWithSub").setExternalName("RefInPrimKeyWithSubEx").setShortName("RKWS");
        this.referenceInKeyWthSubObj.addLongAttr("SimpleIntKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("SplIntKyEx").setIsKey(true);
        this.referenceInKeyWthSubObj.addReference("SimpleRefKey", simpleKeyObj).setExternalName("SimpleRefKeyEx").setIsKey(true);
        this.referenceInKeyWthSubObj.addReference("CompoundRefKey", compoundKeyObj).setExternalName("CompoundRefKeyEx").setIsKey(true);
        this.referenceInKeyWthSubObj.addTextAttr("ReferenceData", 50).setExternalName("ReferenceDataEx").setNotNull(true);

        this.subClassSimpleKeyObj = this.addClassInfo("SubClassSimpleKeyObj", simpleKeyWithSubclassObj).setExternalName("SubClassSimpleKeyObjEx").setShortName("SSK");
        this.subClassSimpleKeyObj.addTextAttr("SimpleDataSub1", 50).setExternalName("SimpleDataSub1Ex").setNotNull(true);

        this.subClassReferenceInKeyDeepObj = this.addClassInfo("SubClassReferenceInKeyObj", referenceInKeyWthSubObj).setExternalName("SubClRefInKeyObjEx").setShortName("SRD");
        this.subClassReferenceInKeyDeepObj.addReference("CompundRefSub1", compoundKeyObj).setExternalName("CompundRefSub1Ex");

        this.referenceOnSubClassObj = this.addClassInfo("ReferenceOnSubclassObj").setExternalName("ReferenceOnSubclassObj").setShortName("RSUB");
        this.referenceOnSubClassObj.addLongAttr("ReferenceKey", Integer.MIN_VALUE, Integer.MAX_VALUE).setExternalName("RefKeyEx").setIsKey(true);
        this.referenceOnSubClassObj.addTextAttr("ReferenceData", 50).setExternalName("RefDataEx").setNotNull(true);
        this.referenceOnSubClassObj.addReference("SimpleRef", simpleKeyWithSubclassObj).setExternalName("SimpleRefEx");
        this.referenceOnSubClassObj.addReference("RefInKeyRef", this.referenceInKeyWthSubObj).setExternalName("RefInKeyRefEx");
    }

    public ClassInfo getAllPrimitiveTypeClassInfo()
    {

        return this.allPrimitiveTypeObj;
    }

    /**
     * Meta data for a Class with an Simple Key (one Attribute is Key) and no
     * References
     *
     * @return
     */
    public ClassInfo getSimpleKeyClassInfo()
    {
        return this.simpleKeyObj;
    }

    /**
     * Meta data for a Class with a several Attributes in key
     *
     * @return
     */
    public ClassInfo getCompoundKeyClassInfo()
    {
        return this.compoundKeyObj;
    }

    /**
     * Meta Data for a Class with References to other objects
     *
     * @return
     */
    public ClassInfo getReferencesClassInfo()
    {
        return this.referencesObj;
    }

    public ClassInfo getReferenceInPrimaryKeyClassInfo()
    {
        return this.referenceInKeyObj;
    }

    public ClassInfo getReferenceInKeyDeepClassInfo()
    {
        return this.referenceInKeyDeepObj;
    }

    /**
     * Meta data for a Class with an Simple Key (one Attribute is Key) and no
     * References
     *
     * @return
     */
    public ClassInfo getReferenceOnSelfKeyClassInfo()
    {
        return this.referenceOnSelfObj;
    }

    /**
     * Meta data for a Class with an Simple Key (one Attribute is Key) and no
     * References
     *
     * @return
     */
    public ClassInfo getReferenceTwoOnSameObjClassInfo()
    {
        return this.referenceTwoOnSameObj;
    }

    public JdyClassInfoModel getAssocMasterClassInfo()
    {
        return this.associationMasterObj;
    }

    public JdyClassInfoModel getAssocDetailSimpleInfo()
    {
        return this.assocDetailSimpleObj;
    }

    public JdyClassInfoModel getAssocDetailKeyInfo()
    {
        return this.assocDetailKeyObj;
    }

    public JdyClassInfoModel getAssocDetailNotNullInfo()
    {
        return this.assocDetailNotNullObj;
    }

    /**
     * Meta data for a Class with an Simple Key (one Attribute is Key) and no
     * References
     *
     * @return
     */
    public ClassInfo getSimpleKeyWithSubClassClassInfo()
    {
        return this.simpleKeyWithSubclassObj;
    }

    public JdyClassInfoModel getSubClassSimpleKeyObjInfo()
    {
        return this.subClassSimpleKeyObj;
    }

    public ClassInfo getReferenceInPrimaryKeyWithSubclassClassInfo()
    {
        return this.referenceInKeyWthSubObj;
    }

    public ClassInfo getSubClassReferenceInKeyClassInfo()
    {
        return this.subClassReferenceInKeyDeepObj;
    }

    /**
     * Meta Data for a Class with References to other objects
     *
     * @return
     */
    public ClassInfo getReferencOnSubclassClassInfo()
    {
        return this.referenceOnSubClassObj;
    }

}
