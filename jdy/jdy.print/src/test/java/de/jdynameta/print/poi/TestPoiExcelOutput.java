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
package de.jdynameta.print.poi;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import de.jdynameta.testcommon.util.UtilFileLoading;

public class TestPoiExcelOutput
{
    private File tempDir;
    private UtilFileLoading fileUtil;

    @Before
    public void setup() throws Exception
    {
        String testPath = UtilFileLoading.getFile("", TestPoiExcelOutput.class.getSimpleName() + ".class", TestPoiExcelOutput.class).getParent();
        tempDir = new File(new File(testPath), "testTemp");
        tempDir.mkdirs();
        fileUtil = new UtilFileLoading(tempDir);
    }

    @Test
    public void testExcel() throws IOException
    {

        TestDataCreator<HashedValueObject> dataCreator = new TestDataCreator<HashedValueObject>()
        {
            @Override
            protected HashedValueObject createEmptyResult(ClassInfo aClassInfo)
            {
                return new HashedValueObject();
            }
        };

        ClassInfo aClassInfo = TestDataCreator.createClassWithDeepReference();
        ObjectListModel<HashedValueObject> testData = dataCreator.createTestData(aClassInfo, 100, null);

        PoiExcelOutput output = new PoiExcelOutput();
        File excelFile = new File(tempDir, "TestExcel.xls");
        output.createExcelDocument(excelFile, testData, aClassInfo);

        System.out.println(excelFile.getAbsolutePath());
    }

    @After
    public void teardown() throws Exception
    {
        UtilFileLoading.deleteDir(tempDir);
    }

    public static void main(String[] args)
    {

        try
        {
            new TestPoiExcelOutput().testExcel();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
