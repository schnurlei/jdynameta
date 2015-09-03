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
package de.jdynameta.dbaccess.xml.writer;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.jdynameta.base.creation.db.KeyAttributeHandler;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
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
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 *
 * @author Rainer Schneider
 *
 */
public class SaxXmlFileWriter 
{
	private final static String XML_NAME_SPACE = "http://de.comafra.model.dbaccess.xml";
	
	public SaxXmlFileWriter()
	{
	}	
	
	
	public void writeObjectList(PrintWriter out, ClassInfo aInfo, ObjectList<? extends ValueObject> aObjectList )
	 	throws TransformerConfigurationException, JdyPersistentException, SAXException
	{
		StreamResult streamResult = new StreamResult(out);
		TransformerHandler transformerHandler	= createTransformerHandler();	
		transformerHandler.setResult(streamResult);
		
		initialize(transformerHandler);
		
		writeObjectCollToXml(transformerHandler, aObjectList, aInfo, XML_NAME_SPACE);
		close(transformerHandler);
	}
	
	/**
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 */
	private TransformerHandler createTransformerHandler() 
		throws TransformerFactoryConfigurationError, TransformerConfigurationException 
	{
		SAXTransformerFactory transformerFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		TransformerHandler transformerHandler = transformerFactory.newTransformerHandler();
		Transformer serializer = transformerHandler.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		//serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		return transformerHandler;
	}


	private void initialize(TransformerHandler transformerHandler) 
		throws SAXException
	{
		transformerHandler.startDocument();
	}
	
	private void close(TransformerHandler transformerHandler) throws SAXException
	{
		transformerHandler.endDocument();
	}
	
	
	private void writeObjectCollToXml(TransformerHandler transformerHandler, ObjectList<? extends ValueObject> aModelColl, ClassInfo aInfo,  String nameSpaceUri) 
		throws JdyPersistentException, SAXException
	{		
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		atts.addAttribute(nameSpaceUri,"","ExternalName","CDATA",aInfo.getExternalName());
		if ( aInfo.getNameSpace() !=  null){
			atts.addAttribute(nameSpaceUri,"","NameSpace","CDATA",aInfo.getRepoName());
		}

		transformerHandler.startElement(nameSpaceUri,"","ClassInfo",atts);
		
		writeModelCollection(transformerHandler, aModelColl, aInfo, nameSpaceUri);

		transformerHandler.endElement(nameSpaceUri,"","ClassInfo");
	}	
	
	private void writeModelCollection(TransformerHandler transformerHandler, ObjectList<? extends ValueObject> aModelColl, ClassInfo aInfo,   String nameSpaceUri) throws JdyPersistentException, SAXException
	{
		try
		{
			AttributesImpl atts = new AttributesImpl();
			transformerHandler.startElement(nameSpaceUri,"","ValueModelList",atts);

			for (Iterator<? extends ValueObject> modelIter = aModelColl.iterator(); modelIter.hasNext();) {
				
				ValueObject curObj = modelIter.next();
				if(curObj != null) {
					writeObjectToXml(transformerHandler, curObj,  aInfo,  nameSpaceUri,false);
				}
			}
			transformerHandler.endElement(nameSpaceUri,"","ValueModelList");

		} catch (ProxyResolveException excp)
		{
			throw new JdyPersistentException(excp);
		} 
	}
	
	private synchronized void writeObjectToXml (TransformerHandler aTransformerHandler, ValueObject objToWrite, ClassInfo aInfo, String nameSpaceUri,
										  boolean asReferenz) throws JdyPersistentException, SAXException
	{
		AttributesImpl atts = new AttributesImpl();

		aTransformerHandler.startElement(nameSpaceUri,"","ValueModel",atts);

		aInfo.handleAttributes(new SaxObjectAttributeHandler(aTransformerHandler,nameSpaceUri), objToWrite);

		if (objToWrite != null && !asReferenz) {
		
			for( AssociationInfo	curAssoc : aInfo.getAssociationInfoIterator()){

				if (curAssoc != null) {
					//ObjectList aColl = objToWrite.getValue(curAssoc);
					//writeModelCollection( aColl, curAssoc.getDetailClass(), curAssoc.getMasterClassReference().getReferencedClass(), !curAssoc.isDependent(), namespacePrefix, classUrl); 
				}
			}
		}

		aTransformerHandler.endElement(nameSpaceUri,"","ValueModel");
	}

	private class SaxObjectAttributeHandler implements AttributeHandler
	{
		private TransformerHandler transformerHandler;
		private String nameSpaceUri;
		private AttributesImpl cachedAtts;
		private SaxPrimitiveHandler primitiveHandler;
		
		/**
		 * @param aTransformerHandler
		 * @param aNameSpaceUri
		 */
		public SaxObjectAttributeHandler(TransformerHandler aTransformerHandler,
				String aNameSpaceUri) 
		{
			super();
			this.transformerHandler = aTransformerHandler;
			this.nameSpaceUri = aNameSpaceUri;
			this.cachedAtts = new AttributesImpl();
			this.primitiveHandler = new SaxPrimitiveHandler(this.transformerHandler, this.nameSpaceUri);
		}
		/* (non-Javadoc)
		 * @see de.comafra.model.metainfo.AttributeHandler#handleObjectReference(de.comafra.model.metainfo.ObjectReferenceAttributeInfo, de.comafra.model.value.ValueObject)
		 */
		public void handleObjectReference(ObjectReferenceAttributeInfo aInfo,
				ValueObject objToHandle) throws JdyPersistentException 
		{
			try 
			{
				cachedAtts.clear();
				cachedAtts.addAttribute(nameSpaceUri,"","ExternalName", "CDATA", aInfo.getExternalName());

				this.transformerHandler.startElement(nameSpaceUri,"","ObjectReference",cachedAtts);

				SaxObjectAttributeHandler tmpReferenceHandler = new SaxObjectAttributeHandler(this.transformerHandler, this.nameSpaceUri); 
				KeyAttributeHandler tmpKeyHandlder = new KeyAttributeHandler(tmpReferenceHandler);
				aInfo.getReferencedClass().handleAttributes(tmpKeyHandlder, objToHandle);	
				
				this.transformerHandler.endElement(nameSpaceUri,"","ObjectReference");
			} catch (SAXException excp) 
			{
				throw new JdyPersistentException(excp);
			}
		}
		
		/* (non-Javadoc)
		 * @see de.comafra.model.metainfo.PrimitiveAttributeHandler#handlePrimitiveAttribute(de.comafra.model.metainfo.PrimitiveAttributeInfo, java.lang.Object)
		 */
		public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,
				Object objToHandle) throws JdyPersistentException 
		{
			try 
			{
				cachedAtts.clear();
				cachedAtts.addAttribute(nameSpaceUri,"","ExternalName", "CDATA", aInfo.getExternalName());

				this.transformerHandler.startElement(nameSpaceUri,"","PrimitiveAttribute",cachedAtts);

				aInfo.getType().handlePrimitiveKey(primitiveHandler, objToHandle);
				
				this.transformerHandler.endElement(nameSpaceUri,"","PrimitiveAttribute");
			} catch (SAXException excp) 
			{
				throw new JdyPersistentException(excp);
			}
		}
	}
	
	private class SaxPrimitiveHandler implements PrimitiveTypeVisitor
	{		 
		private TransformerHandler transformerHandler;
		private String nameSpaceUri;
		private AttributesImpl cachedAtts;

		public SaxPrimitiveHandler(TransformerHandler aTransformerHandler,
				String aNameSpaceUri) 
		{
			super();
			this.transformerHandler = aTransformerHandler;
			this.nameSpaceUri = aNameSpaceUri;
			this.cachedAtts = new AttributesImpl();
		}

		public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
		{
			// TODO Auto-generated method stub
		}

		public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
		{
			try {
				cachedAtts.clear();
				if( aValue != null) {
					cachedAtts.addAttribute(nameSpaceUri,"","value", "CDATA", aValue.toString());
				}
				this.transformerHandler.startElement(nameSpaceUri,"","BooleanType",cachedAtts);

				this.transformerHandler.endElement(nameSpaceUri,"","BooleanType");
			} catch (SAXException excp) {
				throw new JdyPersistentException(excp);
			}
		}

		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
		{			
			try {
				cachedAtts.clear();
				if( aValue != null) {
					cachedAtts.addAttribute(nameSpaceUri,"","value", "CDATA", aValue.toString());
				}
				this.transformerHandler.startElement(nameSpaceUri,"","DateType",cachedAtts);

				this.transformerHandler.endElement(nameSpaceUri,"","DateType");
			} catch (SAXException excp) {
				throw new JdyPersistentException(excp);
			}
		}

		public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
		{
			// TODO Auto-generated method stub
		}

		public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
		{
			try {
				cachedAtts.clear();
				if( aValue != null) {
					cachedAtts.addAttribute(nameSpaceUri,"","value", "CDATA", aValue.toString());
				}
				this.transformerHandler.startElement(nameSpaceUri,"","IntegerType",cachedAtts);

				this.transformerHandler.endElement(nameSpaceUri,"","IntegerType");
			} catch (SAXException excp) {
				throw new JdyPersistentException(excp);
			}
		}

		public void handleValue(String aValue, TextType aType) throws JdyPersistentException
		{
			try {
				cachedAtts.clear();
				if( aValue != null) {
					cachedAtts.addAttribute(nameSpaceUri,"","value", "CDATA", aValue);
				}
				this.transformerHandler.startElement(nameSpaceUri,"","TextType",cachedAtts);

				this.transformerHandler.endElement(nameSpaceUri,"","TextType");
			} catch (SAXException excp) {
				throw new JdyPersistentException(excp);
			}
		}

		public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
		{
			try {
				cachedAtts.clear();
				if( aValue != null) {
					cachedAtts.addAttribute(nameSpaceUri,"","value", "CDATA", aValue);
				}
				this.transformerHandler.startElement(nameSpaceUri,"","VarCharType",cachedAtts);

				this.transformerHandler.endElement(nameSpaceUri,"","VarCharType");
			} catch (SAXException excp) {
				throw new JdyPersistentException(excp);
			}
		}


		/* (non-Javadoc)
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeVisitor#handleValue(de.comafra.model.metainfo.primitive.BlobByteArrayHolder, de.comafra.model.metainfo.primitive.BlobType)
		 */
		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			// TODO Auto-generated method stub
			
		}


	}
	
//	private static String quoteString(String s) 
//	 {
//		 if (s == null) {
//			 return "null";
//		 }
//		 StringBuffer str = new StringBuffer();
//		 int length = s.length();
//		 for (int i = 0; i < length; i++) {
//			 char c = s.charAt(i);
//			 switch (c) {
//				case '<': {
//					str.append("&lt;");
//					break;
//				}
//				case '>': {
//					str.append("&gt;");
//					break;
//				}
//				case '&': {
//					str.append("&amp;");
//					break;
//				}
//            
//				case '\'': {
//					str.append("&apos;");
//					break;
//				}
//				case '"': {
//					str.append("&quot;");
//					break;
//				}
//				case '???': {
//					str.append("&#228;");
//					break;
//				}
//				case '???': {
//					str.append("&#196;");
//					break;
//				}
//				case '???': {
//					str.append("&#246;");
//					break;
//				}
//				case '???': {
//					str.append("&#214;");
//					break;
//				}
//				case '???': {
//					str.append("&#252;");
//					break;
//				}
//				case '???': {
//					str.append("&#220;");
//					break;
//				}
//				default: {
//					str.append(c);
//				}
//			 }//switch
//		 }//for
//		 return str.toString();     
//	 }
}
