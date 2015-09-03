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

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ValueObject;


public class FopDirectOutputTable extends de.jdynameta.print.xslfo.FopDirectOutput 
{
	public FopDirectOutputTable() 
	{
		super(PageMaster.A4landscape);
	}

        @Override
	public void writeRegionBody(PrintWriter aFopWriter, ObjectList<? extends ValueObject> anObjList, ClassInfo aClassInfo )
	{
		aFopWriter.println("<fo:flow flow-name=\"xsl-region-body\">");
		aFopWriter.println("<fo:block space-before=\"10mm\">");

		writeValuesObjectTable(aFopWriter, anObjList,aClassInfo);
		aFopWriter.println("  </fo:block>");
		aFopWriter.println(" <fo:block id=\"theEnd\"/> ");
		aFopWriter.println("</fo:flow>");
		
	}
	
	
}
