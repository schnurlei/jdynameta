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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

public class CsvOutput
{
    final CsvTypeVisitor typeVisitor = new CsvTypeVisitor();

    public void createOutputDocument(File targetFile, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo) throws IOException
    {
        try (FileWriter fileOut = new FileWriter(targetFile))
        {
            createCsvDocument(fileOut, anObjList, aClassInfo);
        }
    }

    public void createCsvDocument(Writer targetWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo) throws IOException
    {
        CSVWriter csvWriter = new CSVWriter(targetWriter); // XSSFWorkbook

        writeObjectListIntoCsv(csvWriter, anObjList, aClassInfo);
    }

    public void writeObjectListIntoCsv(CSVWriter csvWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo)
    {
        writeHeaderLineIntoCsv(csvWriter, aClassInfo);
        for (ValueObject curValueObj : anObjList)
        {

            writeLineIntoCsv(csvWriter, curValueObj, aClassInfo);
        }
    }

    private void writeLineIntoCsv(CSVWriter csvWriter, ValueObject curValueObj, ClassInfo aClassInfo)
    {
        List<String> fields = new ArrayList<>();
        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {

            try
            {
                fields.add(getCellValueForAttribute(curValueObj, curAttr));
            } catch (JdyPersistentException e)
            {
                e.printStackTrace();
            }
        }
        csvWriter.writeNext(fields.toArray(new String[fields.size()]));
    }

    private void writeHeaderLineIntoCsv(CSVWriter csvWriter, ClassInfo aClassInfo)
    {
        List<String> headerFields = new ArrayList<>();
        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {

            headerFields.add(curAttr.getInternalName());
        }

        csvWriter.writeNext(headerFields.toArray(new String[headerFields.size()]));
    }

    protected String getCellValueForAttribute(ValueObject curValueObj, AttributeInfo curAttr) throws JdyPersistentException
    {
        typeVisitor.resetValue();

        curAttr.handleAttribute(new AttributeHandler()
        {

            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
            {
                aInfo.getType().handlePrimitiveKey(typeVisitor, objToHandle);
            }

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
            {
            }
        }, curValueObj.getValue(curAttr));

        String result = typeVisitor.getValue();
        return result;
    }
}
