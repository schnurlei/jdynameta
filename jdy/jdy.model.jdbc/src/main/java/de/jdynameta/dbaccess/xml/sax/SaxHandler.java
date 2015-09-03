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
package de.jdynameta.dbaccess.xml.sax;

import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class SaxHandler extends DefaultHandler
{
	private ArrayList<SaxElementHandler> elementStack;
	private SaxDocumentHandler documentHandler;
	
	/**
	 * 
	 */
	public SaxHandler(SaxDocumentHandler aDocumentHandler)
	{
		super();
		this.elementStack = new ArrayList<SaxElementHandler>(10);
		this.documentHandler = aDocumentHandler;
	}

	private SaxElementHandler getLastElement()
	{
		return this.elementStack.get(this.elementStack.size()-1);
	}
	private void removeLastElement()
	{
		this.elementStack.remove(this.elementStack.size()-1);
	}
	
	@Override
	public void startDocument()
		throws SAXException
	{
		this.elementStack.add(this.documentHandler.createRootObject());
	}
	@Override
	public void endDocument()
		throws SAXException
	{
		this.documentHandler.close();
	}
	
	@Override
	public void startElement(String namespaceURI
			, String aSimpleName // elementName
			, String aQualifiedName
			, org.xml.sax.Attributes attrs)
		throws SAXException
	{
		String elementName = aSimpleName; // element name
		if ("".equals(elementName)) {
			elementName = aQualifiedName; // not namespace-aware
		}
		SaxElementHandler newHandler = getLastElement().createChild(elementName, attrs);
		this.elementStack.add(newHandler);
	} 
	
	@Override
	public void endElement(String namespaceURI,
	        String aSimpleName, String aQualifiedName )
	throws SAXException
	{
		getLastElement().close();
		removeLastElement();
	} 
	
}