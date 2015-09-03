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
package de.jdynameta.base.generation;

/**
 * String Buffer which supports the genration of class code.
 * it writes the code into a String buffer.
 * 
 * @author Rainer Schneider
 * @version 17.06.2002
 */
public class CodeGenerationBuffer 
{

	private StringBuffer codeBuffer;
	
	public void clear() 
	{
		codeBuffer.delete(0,  codeBuffer.length());
	}
	
	public CodeGenerationBuffer(StringBuffer aCodeBuffer) {
		codeBuffer = aCodeBuffer;	
	}

	public CodeGenerationBuffer(int capacity) {
		this(new StringBuffer(capacity));	
	}

	public CodeGenerationBuffer() {
		this(new StringBuffer(1000));	
	}

	public StringBuffer getStringBuffer() {
		return codeBuffer;	
	}


	public void setStringBuffer(StringBuffer aCodeBuffer) {
		codeBuffer = aCodeBuffer;	
	}

	public void append(char aCharToAppend) {

		codeBuffer.append(aCharToAppend);
	}

	public void append(String textToAppend) {

		codeBuffer.append(textToAppend);
	}

	public void appendLine(String textToAppend) {

		codeBuffer.append(textToAppend);
		codeBuffer.append('\n');
	}

	public void appendWithFirstLetterUppercase(String textToAppend) {

		codeBuffer.append(Character.toUpperCase(textToAppend.charAt(0)));
		codeBuffer.append(textToAppend.substring(1));
	}

	public void appendWithFirstLetterLowercase(String textToAppend) {

		codeBuffer.append(Character.toLowerCase(textToAppend.charAt(0)));
		codeBuffer.append(textToAppend.substring(1));
	}
	
	public void appendEmptyLine() {

		codeBuffer.append('\n');
	}

	public void appendWithTabs(int tabCount,String text) {
		
		appendTabs(tabCount);
		codeBuffer.append(text);
	}

	public void appendWithTabs(int tabCount, String text1, String text2) {
		
		appendTabs(tabCount);
		codeBuffer.append(text1);
		codeBuffer.append(text2);
	}

	public void appendWithTabs(int tabCount, String text1, String text2, String text3) {
		
		appendTabs(tabCount);
		codeBuffer.append(text1);
		codeBuffer.append(text2);
		codeBuffer.append(text3);
	}

	
	public void appendLineWithTabs(int tabCount, String text) {
		
		appendTabs(tabCount);
		codeBuffer.append(text);
		codeBuffer.append('\n');
	}

	public void appendLineWithTabs(int tabCount, String text1, String text2) {
		
		appendTabs(tabCount);
		codeBuffer.append(text1);
		codeBuffer.append(text2);
		codeBuffer.append('\n');
	}

	public void appendLineWithTabs(int tabCount, String text1, String text2, String text3) {
		
		appendTabs(tabCount);
		codeBuffer.append(text1);
		codeBuffer.append(text2);
		codeBuffer.append(text3);
		codeBuffer.append('\n');
	}
	
	public void appendLineWithTabs(int tabCount, String text1, String text2, String text3, String text4) {
		
		appendTabs(tabCount);
		codeBuffer.append(text1);
		codeBuffer.append(text2);
		codeBuffer.append(text3);
		codeBuffer.append(text4);
		codeBuffer.append('\n');
	}
	
	public void appendTabs(int tabCount) {
		
		for( int i = 0; i < tabCount; i++) {
			codeBuffer.append('\t');
		} 	
	}
	
}
