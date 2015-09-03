package de.jdynameta.dbaccess.xml.writer;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
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
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
 
public class WriteXMLFile {
 
	private static final String XSD_NS_URI = "http://www.w3.org/2001/XMLSchema";
	private static final String XSD_PREFIX = "xsd";
	private static final String XSD_PREFIX_ = XSD_PREFIX + ":";
	

	public static Document createSchema(ClassRepository repository) throws ParserConfigurationException, JdyPersistentException 
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element schemaElement = doc.createElement("schema");
		schemaElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + XSD_PREFIX, XSD_NS_URI);
		doc.appendChild(schemaElement);
		Attr elemFormAttr = doc.createAttribute("elementFormDefault");
		elemFormAttr.setValue("1");
		schemaElement.setAttributeNode(elemFormAttr);
		
		for (ClassInfo curType: repository.getAllClassInfosIter()) {
			
	        Element complexType = createElementForClassInfo(doc, curType);
	        complexType.setAttribute("name", curType.getInternalName());
	        schemaElement.appendChild(complexType);
		}
		
		return doc;
		
	}

	private static Element createElementForClassInfo(Document doc, ClassInfo curType) throws JdyPersistentException {
		Element complexType = doc.createElement("complexType" );
        Element sequenceType = doc.createElement("sequence" );

		for (AttributeInfo curAttr : curType.getAttributeInfoIterator()) {

			Element attrElem = doc.createElement("element" );
			attrElem.setAttribute("name", curAttr.getInternalName());
			
			if( curAttr.isKey()) {
				
				attrElem.setAttribute("use", "required");
			}
			
			if( curAttr.isNotNull() ) {
				attrElem.setAttribute("minOccurs", "0");
			} else {
				attrElem.setAttribute("minOccurs", "1");
			}
			attrElem.setAttribute("maxOccurs", "1");
			
			XmlSchemaAttributeHandler handler = new XmlSchemaAttributeHandler(doc, attrElem);
			curAttr.handleAttribute(handler, null);
			
			sequenceType.appendChild(attrElem);
		}
        
        
        
        complexType.appendChild(sequenceType);
		return complexType;
	}
	

	public static void writeDocIntoFile(Document doc, File aFileToWrite)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(aFileToWrite);
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
	}
	
	private static class XmlSchemaAttributeHandler implements AttributeHandler
	{
        final Element typeElem;
		private XmlSchemaPrimitiveHandler primitiveHandler;
		
		/**
		 * @param aTransformerHandler
		 * @param aNameSpaceUri
		 */
		public XmlSchemaAttributeHandler(Document aDoc,Element aTypeElem) 
		{
			super();
			this.typeElem = aTypeElem;
			this.primitiveHandler = new XmlSchemaPrimitiveHandler(aDoc);
			this.primitiveHandler.setTypeElem(aTypeElem);
		}

		/* (non-Javadoc)
		 * @see de.comafra.model.metainfo.AttributeHandler#handleObjectReference(de.comafra.model.metainfo.ObjectReferenceAttributeInfo, de.comafra.model.value.ValueObject)
		 */
		@Override
		public void handleObjectReference(ObjectReferenceAttributeInfo aInfo,ValueObject objToHandle) 
			throws JdyPersistentException 
		{
		}
		
		/* (non-Javadoc)
		 * @see de.comafra.model.metainfo.PrimitiveAttributeHandler#handlePrimitiveAttribute(de.comafra.model.metainfo.PrimitiveAttributeInfo, java.lang.Object)
		 */
		@Override
		public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException 
		{
			aInfo.getType().handlePrimitiveKey(primitiveHandler, objToHandle);
		}
	}
	
	/**
	 * Add Primitive values to a Json Object. Set #setCurPrimitivName before call o handleValue
	 */
	private static class XmlSchemaPrimitiveHandler implements PrimitiveTypeVisitor
	{		 
		private Element typeElem;
		private Document doc;

		private XmlSchemaPrimitiveHandler(Document aDoc)
		{
			this.doc = aDoc;
		}

		public void setTypeElem(Element aTypeElem)
		{
			this.typeElem = aTypeElem;
		}

		
		
		@Override
		public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
		{
			typeElem.setAttribute("type", "decimal");
		}

		@Override
		public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
		{
			typeElem.setAttribute("type", "boolean");
		}

		@Override
		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
		{		
			if(aType.isDatePartUsed() && aType.isTimePartUsed()) {
				typeElem.setAttribute("type", "dateTime");
			} else if (aType.isDatePartUsed() ) {
				typeElem.setAttribute("type", "date");
			} else if( aType.isTimePartUsed()) {
				typeElem.setAttribute("type", "time");
			} else {
				typeElem.setAttribute("type", "boolean");
			}
		
		}

		@Override
		public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
		{
			typeElem.setAttribute("type", "double");
		}

		@Override
		public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
		{
//			typeElem.setAttribute("type", "integer");

			Element simpleElem = doc.createElement("simpleType" );
			typeElem.appendChild(simpleElem);
			Element restrictElem = doc.createElement("restriction" );
			restrictElem.setAttribute("base", "integer");
			simpleElem.appendChild(restrictElem);
			Element minElem = doc.createElement("minInclusive" );
			minElem.setAttribute("value", "0");
			restrictElem.appendChild(minElem);
			Element maxElem = doc.createElement("maxInclusive" );
			maxElem.setAttribute("value", "100");
			restrictElem.appendChild(maxElem);
		
		}

		@Override
		public void handleValue(String aValue, TextType aType) throws JdyPersistentException
		{
			typeElem.setAttribute("type", "string");
		}

		@Override
		public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
		{
			typeElem.setAttribute("type", "string");
		}


		/* (non-Javadoc)
		 * @see de.comafra.model.metainfo.primitive.PrimitiveTypeVisitor#handleValue(de.comafra.model.metainfo.primitive.BlobByteArrayHolder, de.comafra.model.metainfo.primitive.BlobType)
		 */
		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			
			typeElem.setAttribute("type", "base64Binary");
			
		}


	}
	
} 