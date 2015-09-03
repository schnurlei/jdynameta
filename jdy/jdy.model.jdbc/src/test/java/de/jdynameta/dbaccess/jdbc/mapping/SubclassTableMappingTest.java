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
package de.jdynameta.dbaccess.jdbc.mapping;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectPath;
import de.jdynameta.base.creation.db.JDyDefaultClassInfoToTableMapping;
import de.jdynameta.base.creation.db.JdyMappingUtil;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.testcommon.model.subclass.SubclassRepository;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;

public class SubclassTableMappingTest extends TestCase
{

    private SubclassRepository getRepository()
    {
        return new SubclassRepository();
    }

    public void testTableMappingReferencedObject() throws JdyPersistentException
    {
        ClassInfo refType = getRepository().getReferencedObjectType();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(refType);

        for (AttributeInfo curInfo : refType.getAttributeInfoIterator())
        {

            List<AspectPath> resultAspectList = new ArrayList<AspectPath>();
            JdyMappingUtil.AttributePathsObjectReferenceHandler handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            if (curInfo.getInternalName().equals("refKey1In"))
            {
                assertEquals("refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("refKey2In"))
            {
                assertEquals("refKey2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else
            {
                assertEquals("refData", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            }
        }
    }

    public void testTableMappingMainClass() throws JdyPersistentException
    {
        ClassInfo mainType = getRepository().getMainClassType();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(mainType);
        List<AspectPath> resultAspectList;
        JdyMappingUtil.AttributePathsObjectReferenceHandler handler;
        for (AttributeInfo curInfo : mainType.getAttributeInfoIterator())
        {
            resultAspectList = new ArrayList<>();
            handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            resultAspectList.size();
            if (curInfo.getInternalName().equals("keyMain1"))
            {
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("keyMain2"))
            {
                assertEquals(1, resultAspectList.size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("keyMain2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("mainData"))
            {
                assertEquals(1, resultAspectList.size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("mainData", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("mainClass", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else
            {
                fail("Unknown Attribute");
            }
        }
    }

    public void testTableMappingSubclassLevel1() throws JdyPersistentException
    {
        ClassInfo levl1Type = getRepository().getSubclassLevel1Type();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(levl1Type);
        List<AspectPath> resultAspectList;
        JdyMappingUtil.AttributePathsObjectReferenceHandler handler;
        for (AttributeInfo curInfo : levl1Type.getAttributeInfoIterator())
        {

            resultAspectList = new ArrayList<>();
            handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            resultAspectList.size();
            if (curInfo.getInternalName().equals("keyMain1"))
            {
                assertEquals(2, resultAspectList.size());
                assertEquals(2, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getTableName());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(1).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getColumnName());
                assertEquals("mainClass", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getTableName());
            } else if (curInfo.getInternalName().equals("keyMain2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(2, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("keyMain2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("mainData"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("mainData", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("mainClass", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataLvl1"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataLvl1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else
            {
                fail("Unknown Attribute");
            }
        }
    }

    public void testTableMappingFirstSubclassLevel2() throws JdyPersistentException
    {
        ClassInfo levl2Type = getRepository().getFirstSubLevel2Type();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(levl2Type);
        List<AspectPath> resultAspectList;
        JdyMappingUtil.AttributePathsObjectReferenceHandler handler;
        for (AttributeInfo curInfo : levl2Type.getAttributeInfoIterator())
        {
            resultAspectList = new ArrayList<>();
            handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            resultAspectList.size();
            if (curInfo.getInternalName().equals("keyMain1"))
            {
                assertEquals(2, resultAspectList.size());
                assertEquals(3, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
                assertEquals("firstSubLevel2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getTableName());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(1).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getTableName());
            } else if (curInfo.getInternalName().equals("keyMain2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(3, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("keyMain2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("mainData"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("mainData", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("mainClass", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataLvl1"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataLvl1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataALvl2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataALvl2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("firstSubLevel2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else
            {
                fail("Unknown Attribute");
            }
        }
    }

    public void testTableMappingSecondSubclassLevel2() throws JdyPersistentException
    {
        ClassInfo levl2Type = getRepository().getSecondSubLevel2Type();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(levl2Type);
        List<AspectPath> resultAspectList;
        JdyMappingUtil.AttributePathsObjectReferenceHandler handler;
        for (AttributeInfo curInfo : levl2Type.getAttributeInfoIterator())
        {
            resultAspectList = new ArrayList<>();
            handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            resultAspectList.size();
            if (curInfo.getInternalName().equals("keyMain1"))
            {
                assertEquals(2, resultAspectList.size());
                assertEquals(3, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
                assertEquals("secondSubLevel2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getTableName());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(1).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getTableName());
            } else if (curInfo.getInternalName().equals("keyMain2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(3, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("keyMain2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("mainData"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("mainData", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("mainClass", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataLvl1"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataLvl1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataBLvl2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataBLvl2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("secondSubLevel2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else
            {
                fail("Unknown Attribute");
            }
        }
    }

    public void testTableMappingSubLevel3() throws JdyPersistentException
    {
        ClassInfo levl3Type = getRepository().getSubLevel3Type();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(levl3Type);
        List<AspectPath> resultAspectList;
        JdyMappingUtil.AttributePathsObjectReferenceHandler handler;
        for (AttributeInfo curInfo : levl3Type.getAttributeInfoIterator())
        {
            resultAspectList = new ArrayList<>();
            handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            resultAspectList.size();
            if (curInfo.getInternalName().equals("keyMain1"))
            {
                assertEquals(2, resultAspectList.size());
                assertEquals(4, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
                assertEquals("subLevel3", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getTableName());
                assertEquals("keyMain1_refKey1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(1).getColumnName());
                assertEquals("keyMain1_refKey2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getColumnName());
                assertEquals("secondSubLevel2", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(1).getTableName());
            } else if (curInfo.getInternalName().equals("keyMain2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(4, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("keyMain2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("mainData"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("mainData", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("mainClass", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataLvl1"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataLvl1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("subclassLevel1", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataBLvl2"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataBLvl2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("secondSubLevel2", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else if (curInfo.getInternalName().equals("dataLevel3"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals(1, mapping.getColumnMappingsFor(resultAspectList.get(0)).size());
                assertTrue(resultAspectList.get(0).referenceIterator() == null);
                assertEquals("dataLevel3", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("subLevel3", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getTableName());
            } else
            {
                fail("Unknown Attribute");
            }
        }
    }

    public void testTableReferenceInKeyDeepObject() throws JdyPersistentException
    {
        ClassInfo refType = new ComplexTestDataMetaInfoRepository().getReferenceInKeyDeepClassInfo();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(refType);

        for (AttributeInfo curInfo : refType.getAttributeInfoIterator())
        {

            List<AspectPath> resultAspectList = new ArrayList<>();
            JdyMappingUtil.AttributePathsObjectReferenceHandler handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            if (curInfo.getInternalName().equals("DeepRef"))
            {
                assertEquals(4, resultAspectList.size());
                assertEquals("DeepRefEx_SplIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("DeepRefEx_SK_SplIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
                assertEquals("DeepRefEx_CK_CmpIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(2)).get(0).getColumnName());
                assertEquals("DeepRefEx_CK_CmpTxtKyEx", mapping.getColumnMappingsFor(resultAspectList.get(3)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("BonusDate"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals("BonusDateEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else
            {
                fail("Unknown Attribute");
            }
        }
    }

    public void testTableSubClassReferenceInKeyObject() throws JdyPersistentException
    {
        ClassInfo subclassType = new ComplexTestDataMetaInfoRepository().getSubClassReferenceInKeyClassInfo();
        JDyDefaultClassInfoToTableMapping mapping = new JDyDefaultClassInfoToTableMapping(subclassType);

        for (AttributeInfo curInfo : subclassType.getAttributeInfoIterator())
        {

            List<AspectPath> resultAspectList = new ArrayList<>();
            JdyMappingUtil.AttributePathsObjectReferenceHandler handler = new JdyMappingUtil.AttributePathsObjectReferenceHandler(resultAspectList, null);
            curInfo.handleAttribute(handler, null);
            if (curInfo.getInternalName().equals("CompundRefSub1"))
            {
                assertEquals(2, resultAspectList.size());
                assertEquals("CompundRefSub1Ex_CmpIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("CompundRefSub1Ex_CmpTxtKyEx", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("ReferenceData"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals("ReferenceDataEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("CompoundRefKey"))
            {
                assertEquals(2, resultAspectList.size());
                assertEquals("CompoundRefKeyEx_CmpIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
                assertEquals("CompoundRefKeyEx_CmpTxtKyEx", mapping.getColumnMappingsFor(resultAspectList.get(1)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("SimpleRefKey"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals("SimpleRefKeyEx_SplIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else if (curInfo.getInternalName().equals("SimpleIntKey"))
            {
                assertEquals(1, resultAspectList.size());
                assertEquals("SplIntKyEx", mapping.getColumnMappingsFor(resultAspectList.get(0)).get(0).getColumnName());
            } else
            {
                fail("Unknown Attribute");
            }

        }
    }

}
