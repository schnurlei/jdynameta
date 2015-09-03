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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


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
import de.jdynameta.base.value.JdyPersistentException;

public final class FopTypeVisitor implements PrimitiveTypeVisitor 
{
	private PrintWriter fopWriter;
	
	public void setFopWriter(PrintWriter fopWriter) 
	{
		this.fopWriter = fopWriter;
	}
	
	@Override
	public void handleValue(Long aValue, LongType aType) throws JdyPersistentException 
	{
		fopWriter.println("      <fo:block wrap-option=\"wrap\" text-align=\"right\">");
		if( aValue != null) {
			String textValue = aValue.toString();
			fopWriter.println("      <fo:block>"+FopDirectOutput.escapeAndHyphenateString(textValue)+"</fo:block>");
		}
		fopWriter.println("      </fo:block>");
	}

	@Override
	public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException 
	{
		String checboxText;
		if( aValue != null && aValue) {
			checboxText = "<fo:inline font-family=\"ZapfDingbats\" font-size=\"10pt\" border=\"1pt black solid\">&#x2715;</fo:inline>";	
		}  else {
			checboxText = "<fo:inline font-family=\"ZapfDingbats\" font-size=\"14pt\">&#x274F;</fo:inline>";
		}
		fopWriter.println("      <fo:block text-align=\"center\">"+checboxText+"</fo:block>");

	}

	@Override
	public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException 
	{
		fopWriter.println("      <fo:block wrap-option=\"wrap\" text-align=\"right\">");
		if( aValue != null) {
			String textValue = aValue.toString();
			fopWriter.println("      <fo:block>"+FopDirectOutput.escapeAndHyphenateString(textValue)+"</fo:block>");
		}
		fopWriter.println("      </fo:block>");
	}

	@Override
	public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException 
	{
		fopWriter.println("      <fo:block wrap-option=\"wrap\" text-align=\"right\">");
		if( aValue != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
			if( !aType.isTimePartUsed()) {
				dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			} else if ( !aType.isDatePartUsed()) {
				dateFormat = new SimpleDateFormat("HH:mm:ss");
			} 
			fopWriter.println("      <fo:block>"+FopDirectOutput.escapeAndHyphenateString(dateFormat.format(aValue))+"</fo:block>");
		}
		fopWriter.println("      </fo:block>");
	}

	@Override
	public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException 
	{
		fopWriter.println("      <fo:block wrap-option=\"wrap\" text-align=\"right\">");
		if( aValue != null) {
			String textValue = aValue.toString();
			fopWriter.println("      <fo:block>"+FopDirectOutput.escapeAndHyphenateString(textValue)+"</fo:block>");
		}
		fopWriter.println("      </fo:block>");
	}

	@Override
	public void handleValue(String aValue, TextType aType) throws JdyPersistentException 
	{
		fopWriter.println("      <fo:block wrap-option=\"wrap\">");
		if( aValue != null) {
			fopWriter.println("      <fo:block>"+FopDirectOutput.escapeAndHyphenateString(aValue)+"</fo:block>");
		}
		fopWriter.println("      </fo:block>");
	}

	@Override
	public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException 
	{
		fopWriter.println("      <fo:block wrap-option=\"wrap\">");
		if( aValue != null) {
			fopWriter.println("      <fo:block>"+FopDirectOutput.escapeAndHyphenateString(aValue)+"</fo:block>");
		}
		fopWriter.println("      </fo:block>");
	}

	@Override
	public void handleValue(BlobByteArrayHolder aValue, BlobType aType)	throws JdyPersistentException 
	{
	}
}