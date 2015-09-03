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
package de.jdynameta.print.xslfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.text.ReferenceToTextHandler;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Convert a list of values object direct to FOP (without XSLT)
 *
 * @author rs
 *
 */
public class FopDirectOutput
{
    protected enum PageMaster
    {
        A4Portrait, A4landscape
    }

    private final ReferenceToTextHandler referenceTexthandler;
    private final FopTypeVisitor typeVisitor;
    private final PageMaster pageType;

    public FopDirectOutput(PageMaster aPageType)
    {
        this.referenceTexthandler = new ReferenceToTextHandler(null);
        this.typeVisitor = new FopTypeVisitor();
        this.pageType = aPageType;
    }

    public void createOutputDocument(File targetFile, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo) throws IOException
    {
        BufferedWriter fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UnicodeBig"));

        try
        {
            createFopDocument(fileOut, anObjList, aClassInfo);
        } finally
        {
            if (fileOut != null)
            {
                fileOut.close();
            }
        }
    }

    public void createFopDocument(Writer targetWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo) throws IOException
    {
        PrintWriter fopWriter = new PrintWriter(targetWriter);

        writeFopHeader(fopWriter);
        writeLayoutMaster(fopWriter);
        fopWriter.println("<fo:page-sequence master-reference=\"" + pageType + "\">");
        writePageHeader(fopWriter);
        writeRegionBody(fopWriter, anObjList, aClassInfo);
        fopWriter.println("</fo:page-sequence>");
        writeFopFooter(fopWriter);
    }

    private void writeFopHeader(PrintWriter aFopWriter)
    {
        aFopWriter.println("<?xml version=\"1.0\" encoding=\"UTF-16\"?>");
        aFopWriter.println("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">");

    }

    private void writeLayoutMaster(PrintWriter aFopWriter)
    {
        aFopWriter.println("<fo:layout-master-set>");
        aFopWriter.println(" <fo:simple-page-master master-name=\"" + PageMaster.A4landscape + "\" page-width=\"297mm\"");
        aFopWriter.println("   page-height=\"210mm\" margin-top=\"1cm\" margin-bottom=\"1cm\"");
        aFopWriter.println("   margin-left=\"10mm\" margin-right=\"10mm\">");
        aFopWriter.println("  <fo:region-body margin=\"2cm\" />");
        aFopWriter.println("  <fo:region-before region-name=\"header-region\" extent=\"2cm\"/>");
        aFopWriter.println("  <fo:region-after extent=\"2cm\"/>");
        aFopWriter.println("  <fo:region-start extent=\"2cm\"/>");
        aFopWriter.println("  <fo:region-end extent=\"2cm\"/>");
        aFopWriter.println(" </fo:simple-page-master>");
        aFopWriter.println(" <fo:simple-page-master master-name=\"" + PageMaster.A4Portrait + "\" page-width=\"210mm\"");
        aFopWriter.println("   page-height=\"297mm\" margin-top=\"1cm\" margin-bottom=\"1cm\"");
        aFopWriter.println("   margin-left=\"10mm\" margin-right=\"10mm\">");
        aFopWriter.println("  <fo:region-body margin=\"2cm\" />");
        aFopWriter.println("  <fo:region-before region-name=\"header-region\" extent=\"2cm\"/>");
        aFopWriter.println("  <fo:region-after extent=\"2cm\"/>");
        aFopWriter.println("  <fo:region-start extent=\"2cm\"/>");
        aFopWriter.println("  <fo:region-end extent=\"2cm\"/>");
        aFopWriter.println(" </fo:simple-page-master>");
        aFopWriter.println("</fo:layout-master-set>	");
    }

    private void writePageHeader(PrintWriter aFopWriter)
    {
        aFopWriter.println("<fo:static-content flow-name=\"header-region\">");
        aFopWriter.println(" <fo:table font-size=\"10pt\" width=\"auto\">");
        aFopWriter.println("  <fo:table-column column-number=\"1\" column-width=\"25%\"/>");
        aFopWriter.println("  <fo:table-column column-number=\"2\" column-width=\"50%\"/>");
        aFopWriter.println("  <fo:table-column column-number=\"3\" column-width=\"25%\"/>");
        aFopWriter.println("  <fo:table-body>");
        aFopWriter.println("   <fo:table-row border-bottom-style=\"solid\">");
        aFopWriter.println("    <fo:table-cell column-number=\"1\" display-align=\"after\">");
        aFopWriter.println("     <fo:block> 	-Bild- </fo:block>");
        aFopWriter.println("	</fo:table-cell> ");
        aFopWriter.println("    <fo:table-cell column-number=\"2\" display-align=\"after\">");
        aFopWriter.println("     <fo:block text-align=\"center\"> 	JDynameta </fo:block>");
        aFopWriter.println("	</fo:table-cell> ");

        aFopWriter.println("	<fo:table-cell column-number=\"3\" display-align=\"after\"> ");
        aFopWriter.println("	 <fo:block text-align=\"right\">  Seite - <fo:page-number/> von <fo:page-number-citation ref-id=\"theEnd\"/> </fo:block>");
        aFopWriter.println("	</fo:table-cell> ");
        aFopWriter.println("   </fo:table-row>");
        aFopWriter.println("  </fo:table-body>");
        aFopWriter.println(" </fo:table>");
        aFopWriter.println("</fo:static-content> ");
    }

//	private void writeHeader(PrintWriter aFopWriter) 
//	{
//		aFopWriter.println("<fo:layout-master-set>");
//	}	
    private void writeFopFooter(PrintWriter aFopWriter)
    {
        aFopWriter.println("</fo:root>");
    }

    public void writeRegionBody(PrintWriter aFopWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo)
    {
        aFopWriter.println("<fo:flow flow-name=\"xsl-region-body\">");
        aFopWriter.println("<fo:block space-before=\"10mm\">");

        writeValuesObjectTable(aFopWriter, anObjList, aClassInfo);
        aFopWriter.println("  </fo:block>");
        aFopWriter.println(" <fo:block id=\"theEnd\"/> ");
        aFopWriter.println("</fo:flow>");

    }

    protected void writeValuesObjectTable(PrintWriter aFopWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo)
    {
        aFopWriter.println("<fo:table width=\"100%\" border-style=\"none\" >");

        writeTableHeader(aFopWriter, aClassInfo);

        aFopWriter.println("<fo:table-body>");
        for (ValueObject curValueObj : anObjList)
        {

            writeTableLine(aFopWriter, curValueObj, aClassInfo);
        }

        aFopWriter.println("</fo:table-body>");

        aFopWriter.println("   </fo:table>");

    }

    private void writeTableLine(PrintWriter aFopWriter, ValueObject curValueObj, ClassInfo aClassInfo)
    {
        aFopWriter.println("   <fo:table-row border-bottom-style=\"solid\" border-bottom-width=\"3px\" border-bottom-color=\"rgb(255,255,255)\" font-family=\"Helvetica 65 Medium\" >");

        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {

            try
            {
                writeTableCellForAttribute(aFopWriter, curValueObj, curAttr);
            } catch (JdyPersistentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        aFopWriter.println("   </fo:table-row>");
    }

    protected void writeTableCellForAttribute(final PrintWriter aFopWriter, ValueObject curValueObj, AttributeInfo curAttr) throws JdyPersistentException
    {
        curAttr.handleAttribute(new AttributeHandler()
        {

            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
            {
                typeVisitor.setFopWriter(aFopWriter);
                aFopWriter.println("    <fo:table-cell border-right-style=\"solid\" border-right-width=\"3px\" background-color=\"rgb(210,210,210)\" border-right-color=\"rgb(255,255,255)\""
                        + " padding-top=\"3pt\" padding-left=\"3pt\" padding-right=\"3pt\" padding-bottom=\"3pt\" >");
                aInfo.getType().handlePrimitiveKey(typeVisitor, objToHandle);
                aFopWriter.println("    </fo:table-cell>");
            }

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
            {
                referenceTexthandler.clear();
                aInfo.getReferencedClass().handleAttributes(referenceTexthandler, (ValueObject) objToHandle);
                aFopWriter.println("    <fo:table-cell border-right-style=\"solid\" border-right-width=\"3px\" background-color=\"rgb(210,210,210)\" border-right-color=\"rgb(255,255,255)\""
                        + " padding-top=\"3pt\" padding-left=\"3pt\" padding-right=\"3pt\" padding-bottom=\"3pt\" >");
                aFopWriter.println("      <fo:block>" + escapeAndHyphenateString(referenceTexthandler.getResultText()) + "</fo:block>");
                aFopWriter.println("    </fo:table-cell>");
            }
        }, curValueObj.getValue(curAttr));

    }

    private void writeTableHeader(PrintWriter aFopWriter, ClassInfo aClassInfo)
    {

        aFopWriter.println("<fo:table-header>");
        aFopWriter.println("<fo:table-row border-bottom-style=\"solid\" border-bottom-width=\"3px\" border-bottom-color=\"rgb(255,255,255)\" font-size=\"10pt\" font-family=\"Helvetica 65 Medium\" text-transform=\"uppercase\">");

        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {

            aFopWriter.println("<fo:table-cell width=\"auto\" border-right-style=\"solid\""
                    + "	border-right-width=\"3px\" border-right-color=\"rgb(255,255,255)\" "
                    + "  padding-top=\"15pt\" padding-left=\"10pt\" padding-right=\"10pt\" padding-bottom=\"15pt\" background-color=\"rgb(206,218,196)\" >");

            aFopWriter.println("<fo:block reference-orientation=\"90\" font-weight=\"bold\"> " + escapeAndHyphenateString(curAttr.getInternalName()) + " </fo:block>");
            aFopWriter.println("</fo:table-cell>");
        }
        aFopWriter.println("</fo:table-row>");

        aFopWriter.println("</fo:table-header>");
    }

    /**
     * <p>
     * Escapes the characters in a <code>String</code> using XML entities.</p>
     * and add a zero-width space &#x200B; to support line wrapping
     *
     * @param sourceString
     * @return
     */
    public static String escapeAndHyphenateString(String sourceString)
    {
        StringBuilder result = new StringBuilder(sourceString.length() * 2);

        for (int i = 0; i < sourceString.length(); i++)
        {
            result.append(sourceString.charAt(i));
            if (i > 0 && i % 5 == 0)
            {
                result.append('\u200B');
            }
        }

        return StringEscapeUtils.escapeXml(result.toString());
    }

}
