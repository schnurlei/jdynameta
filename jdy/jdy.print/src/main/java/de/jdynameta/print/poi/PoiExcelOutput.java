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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.text.ReferenceToTextHandler;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Write a list of ValueObject into a excel file or stream
 *
 * @author rs
 *
 */
public class PoiExcelOutput
{
    private final Workbook workbook;
    private final CreationHelper createHelper;
    private final ReferenceToTextHandler referenceTexthandler;

    public PoiExcelOutput()
    {
        workbook = new HSSFWorkbook(); // XSSFWorkbook
        createHelper = workbook.getCreationHelper();
        referenceTexthandler = new ReferenceToTextHandler(null);
    }

    public void createExcelDocument(File targetFile, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo)
            throws IOException
    {
        try (FileOutputStream fileOut = new FileOutputStream(targetFile))
        {
            createExcelDocument(fileOut, anObjList, aClassInfo);
        }

    }

    public void createExcelDocument(OutputStream targetStream, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo)
            throws IOException
    {
        Sheet sheet = workbook.createSheet(aClassInfo.getInternalName());
        writeObjectListIntoExcel(workbook, sheet, anObjList, aClassInfo);
        workbook.write(targetStream);
    }

    private void writeObjectListIntoExcel(Workbook workbook, Sheet sheet, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo)
    {
        short colNr = 0;
        short titleRowNr = 0;
        short rowNr = (short) (titleRowNr + 1);

        // write cells
        final WorkbookPrimitiveCellWriter typeVisitor = new WorkbookPrimitiveCellWriter(workbook, createHelper);
        for (ValueObject curValueObj : anObjList)
        {

            Row row = sheet.createRow(rowNr++);
            colNr = 0;
            for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
            {

                Cell newCell = row.createCell(colNr++);
                try
                {
                    setCellValueForAttribute(typeVisitor, curValueObj, newCell, curAttr);
                } catch (JdyPersistentException e)
                {
                    e.printStackTrace();
                }
            }
        }

        // write title row and auto size columns
        colNr = 0;
        Row titleRow = sheet.createRow(titleRowNr);
        CellStyle headerCellStyle = createHeaderCellStyle(workbook);
        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {

            Cell newCell = titleRow.createCell(colNr++);
            newCell.setCellValue(createHelper.createRichTextString(curAttr.getInternalName()));
            newCell.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(newCell.getColumnIndex());
        }

    }

    private CellStyle createHeaderCellStyle(Workbook workbook)
    {
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("TEXT"));
        return headerCellStyle;
    }

    protected void setCellValueForAttribute(final WorkbookPrimitiveCellWriter typeVisitor, ValueObject curValueObj,
            final Cell newCell, AttributeInfo curAttr)
            throws JdyPersistentException
    {

        curAttr.handleAttribute(new AttributeHandler()
        {

            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
            {
                typeVisitor.setCurrentCell(newCell);
                aInfo.getType().handlePrimitiveKey(typeVisitor, objToHandle);
            }

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
            {
                referenceTexthandler.clear();
                aInfo.getReferencedClass().handleAttributes(referenceTexthandler, (ValueObject) objToHandle);
                newCell.setCellStyle(typeVisitor.getTextStyle());
                newCell.setCellValue(createHelper.createRichTextString(referenceTexthandler.getResultText()));
            }
        }, curValueObj.getValue(curAttr));

    }

    public static final class WorkbookPrimitiveCellWriter implements PrimitiveTypeVisitor
    {
        private final Workbook workbook;
        private final CreationHelper createHelper;
        private final CellStyle textStyle;
        private final CellStyle timeStampStyle;
        private final CellStyle timeStyle;
        private final CellStyle dateStyle;
        private Cell currentCell;

        private WorkbookPrimitiveCellWriter(Workbook workbook, CreationHelper createHelper)
        {
            this.workbook = workbook;
            this.createHelper = createHelper;
            this.textStyle = workbook.createCellStyle();
            this.textStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("TEXT"));
            this.timeStampStyle = workbook.createCellStyle();
            this.timeStampStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
            this.timeStyle = workbook.createCellStyle();
            this.timeStyle.setDataFormat(createHelper.createDataFormat().getFormat("h:mm"));
            this.dateStyle = workbook.createCellStyle();
            this.dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));

        }

        public CellStyle getTextStyle()
        {
            return textStyle;
        }

        public void setCurrentCell(Cell aCell)
        {
            this.currentCell = aCell;
        }

        @Override
        public void handleValue(Long aValue, LongType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {
                currentCell.setCellValue(aValue.doubleValue());
                currentCell.setCellType(Cell.CELL_TYPE_NUMERIC);
            }
        }

        @Override
        public void handleValue(Boolean aValue, BooleanType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {
                currentCell.setCellValue(aValue);
            }
        }

        @Override
        public void handleValue(Double aValue, FloatType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {
                currentCell.setCellValue(aValue);
            }
        }

        @Override
        public void handleValue(Date aValue, TimeStampType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {
                currentCell.setCellValue(aValue);
                currentCell.setCellStyle(timeStampStyle);
                if (!aType.isDatePartUsed())
                {
                    currentCell.setCellStyle(timeStyle);
                }
                if (!aType.isTimePartUsed())
                {
                    currentCell.setCellStyle(dateStyle);
                }
            }
        }

        @Override
        public void handleValue(BigDecimal aValue, CurrencyType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {

                StringBuilder buffer = new StringBuilder(
                        (int) (2 + aType.getScale()));
                buffer.append((aType.getScale() > 0) ? "0." : "0");
                for (int i = 0; i < aType.getScale(); i++)
                {
                    buffer.append("0");
                }

                currentCell.setCellValue(aValue.doubleValue());
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setDataFormat(createHelper.createDataFormat()
                        .getFormat(buffer.toString()));
                currentCell.setCellStyle(cellStyle);
            }
        }

        @Override
        public void handleValue(String aValue, TextType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {
                currentCell.setCellStyle(textStyle);
                currentCell.setCellValue(createHelper.createRichTextString(aValue));
            }
        }

        @Override
        public void handleValue(String aValue, VarCharType aType)
                throws JdyPersistentException
        {
            if (aValue != null)
            {
                currentCell.setCellStyle(textStyle);
                currentCell.setCellValue(createHelper.createRichTextString(aValue));
            }
        }

        @Override
        public void handleValue(BlobByteArrayHolder aValue, BlobType aType)
                throws JdyPersistentException
        {
        }
    }

}
