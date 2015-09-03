/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.print.xslfo;

import java.io.PrintWriter;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;


public class FopDirectOutputForm extends de.jdynameta.print.xslfo.FopDirectOutput 
{

	public FopDirectOutputForm() {
		super(PageMaster.A4Portrait);
	}

        @Override
	public void writeRegionBody(PrintWriter aFopWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo )
	{
		aFopWriter.println("<fo:flow flow-name=\"xsl-region-body\">");
		writeValuesObjectsTable(aFopWriter, anObjList,aClassInfo);
		aFopWriter.println(" <fo:block id=\"theEnd\"/> ");
		aFopWriter.println("</fo:flow>");
		
	}
	
	protected void writeValuesObjectsTable(PrintWriter aFopWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo )
	{
		for (ValueObject curValueObj : anObjList) {

			aFopWriter.println("<fo:block keep-together.within-column=\"always\" space-before=\"10mm\">");
			
			aFopWriter.println("<fo:table width=\"100%\" border-style=\"none\" >");
			aFopWriter.println("<fo:table-column column-width=\"25%\" column-number=\"1\"/>");
			aFopWriter.println("<fo:table-column column-width=\"75%\" column-number=\"2\"/>");

			
			writeFormTableHeader(aFopWriter); 
			aFopWriter.println("<fo:table-body>");
			writeFormLines( aFopWriter, curValueObj,  aClassInfo );
			aFopWriter.println("</fo:table-body>");
			aFopWriter.println("   </fo:table>");

			aFopWriter.println("  </fo:block>");
		
		}
	}
	
	/**
	 * Write Table Header with 2 columns for field name and field value
	 * @param aFopWriter
	 * @param aClassInfo
	 */

	private void writeFormTableHeader(PrintWriter aFopWriter) 
	{
		
		aFopWriter.println("<fo:table-header>");
		aFopWriter.println("<fo:table-row border-bottom-style=\"solid\" border-bottom-width=\"3px\" border-bottom-color=\"rgb(255,255,255)\" font-size=\"10pt\" font-family=\"Helvetica 65 Medium\" >");

			
		aFopWriter.println("<fo:table-cell border-right-style=\"solid\"" +
						"	border-right-width=\"3px\" border-right-color=\"rgb(255,255,255)\" " +
						"  padding-top=\"15pt\" padding-left=\"10pt\" padding-right=\"10pt\" padding-bottom=\"15pt\" background-color=\"rgb(206,218,196)\" >");
		aFopWriter.println("<fo:block font-weight=\"bold\"> "+ "Name" +" </fo:block>");
		aFopWriter.println("</fo:table-cell>");

		aFopWriter.println("<fo:table-cell border-right-style=\"solid\"" +
				"	border-right-width=\"3px\" border-right-color=\"rgb(255,255,255)\" " +
				"  padding-top=\"15pt\" padding-left=\"10pt\" padding-right=\"10pt\" padding-bottom=\"15pt\" background-color=\"rgb(206,218,196)\" >");
		aFopWriter.println("<fo:block font-weight=\"bold\"> "+ "Wert" +" </fo:block>");
		aFopWriter.println("</fo:table-cell>");
		
		
		aFopWriter.println("</fo:table-row>");

		aFopWriter.println("</fo:table-header>");
	}
	
	
	private void writeFormLines(PrintWriter aFopWriter, ValueObject curValueObj,	ClassInfo aClassInfo) 
	{

		for ( AttributeInfo curAttr :aClassInfo.getAttributeInfoIterator()) {
			
			aFopWriter.println("   <fo:table-row border-bottom-style=\"solid\" border-bottom-width=\"3px\" border-bottom-color=\"rgb(255,255,255)\" font-family=\"Helvetica 65 Medium\" >");
		    
			aFopWriter.println("    <fo:table-cell border-right-style=\"solid\" border-right-width=\"3px\" background-color=\"rgb(210,210,210)\" border-right-color=\"rgb(255,255,255)\"" 
					+  " padding-top=\"3pt\" padding-left=\"3pt\" padding-right=\"3pt\" padding-bottom=\"3pt\" >");
			aFopWriter.println("      <fo:block wrap-option=\"wrap\" text-align=\"right\">");
			aFopWriter.println("      <fo:block>"+curAttr.getInternalName()+"</fo:block>");
			aFopWriter.println("      </fo:block>");
			aFopWriter.println("    </fo:table-cell>");
			
		    try {
				writeTableCellForAttribute(aFopWriter, curValueObj, curAttr);
			} catch (JdyPersistentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aFopWriter.println("   </fo:table-row>");
		}
	}
	
	
}
