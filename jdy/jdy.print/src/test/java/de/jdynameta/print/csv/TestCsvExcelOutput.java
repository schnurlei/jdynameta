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
package de.jdynameta.print.csv;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.testcommon.testdata.TestDataCreator;

public class TestCsvExcelOutput
{
    private void testExcel() throws IOException
    {
        TestDataCreator<HashedValueObject> dataCreator = new TestDataCreator<HashedValueObject>()
        {
            @Override
            protected HashedValueObject createEmptyResult(ClassInfo aClassInfo)
            {
                return new HashedValueObject();
            }
        };

        ClassInfo aClassInfo = createMediumClassInfo();
        ObjectListModel<HashedValueObject> testData = dataCreator.createTestData(aClassInfo, 100, null);

        CsvOutput output = new CsvOutput();
        File excelFile = new File("TestCsv.csv");
        output.createOutputDocument(excelFile, testData, aClassInfo);

        System.out.println(excelFile.getAbsolutePath());
    }

    public static ClassInfo createMediumClassInfo()
    {
        JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("SmallClassInfo");
        classInfo.addLongAttr("LongAttribute", -500, 500).setIsKey(true);
        classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
        classInfo.addDecimalAttr("DecimalAttribute", new BigDecimal(0.00), new BigDecimal(100.00), 2).setNotNull(true);
        classInfo.addBooleanAttr("BooleanAttribute");
        classInfo.addFloatAttr("FloatAttribute").setNotNull(true);
        classInfo.addTimestampAttr("TimestampAttribute").setNotNull(true);
        classInfo.addVarCharAttr("VarcharAttribute", 1000).setNotNull(true);

        return classInfo;
    }

    public static void main(String[] args)
    {

        try
        {
            new TestCsvExcelOutput().testExcel();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
