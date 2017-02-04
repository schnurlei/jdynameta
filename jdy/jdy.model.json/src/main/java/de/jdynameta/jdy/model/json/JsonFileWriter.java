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
package de.jdynameta.jdy.model.json;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import javax.xml.transform.TransformerConfigurationException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
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
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;

/**
 *
 * @author Rainer Schneider
 *
 */
public class JsonFileWriter 
{
	public static final String CLASS_INTERNAL_NAME_TAG = "@classInternalName";
	public static final String NAMESPACE_TAG = "@namespace";
	public static final String PERSISTENCE_TAG = "@persistence";

	private final ObjectMapper mapper;
	private final JsonPrimitiveHandler primitiveHandler;
	
	private final WriteReferenceStrategy writeStrategy;
	boolean flush;
	
	public JsonFileWriter(WriteReferenceStrategy aWriteStrategy, boolean doFlush)
	{
		this.mapper = new ObjectMapper();
		this.primitiveHandler = new JsonPrimitiveHandler();
		this.writeStrategy = aWriteStrategy;
		this.flush = doFlush;
	}	
	
	
	public void writeObjectList(Writer out, ClassInfo aInfo, ObjectList<? extends TypedValueObject> aObjectList, Operation aPersistenceType )
	 	throws TransformerConfigurationException, JdyPersistentException
	{
		
		ArrayNode jsonObjects = writeModelCollection( aObjectList, aPersistenceType);
		try {
			 JsonGenerator jsonGenerator = mapper.getJsonFactory().createJsonGenerator(out);
			 jsonGenerator.useDefaultPrettyPrinter();
			 
			mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, this.flush);
			mapper.writeValue(jsonGenerator, jsonObjects);
		} catch (IOException ex) {
			throw new JdyPersistentException(ex);
		} 
	}
	
	
	private ArrayNode writeModelCollection( ObjectList<? extends TypedValueObject> aModelColl, Operation aPersistenceType) 
		throws JdyPersistentException
	{
        final ArrayNode jsonArray = mapper.createArrayNode();
		for (Iterator<? extends TypedValueObject> modelIter = aModelColl.iterator(); modelIter.hasNext();) {
			
			TypedValueObject curObj = modelIter.next();
			if(curObj != null) {
				ObjectNode newJsonObj = writeObjectToJson(curObj, aPersistenceType);
				jsonArray.add(newJsonObj);
			}
		}
		
		return jsonArray;
	}
	
	private synchronized ObjectNode writeObjectToJson( TypedValueObject objToWrite, Operation aPersistenceType) 
		throws JdyPersistentException
	{
		ObjectNode jsonObject = createClassInfoNode(objToWrite, aPersistenceType, false);
		return jsonObject;
	}

	private ObjectNode createClassInfoNode(TypedValueObject objToWrite, Operation aPersistenceType, boolean asProxy) throws JdyPersistentException
	{
		ObjectNode jsonObject = mapper.createObjectNode();
		addMetaDataFields(jsonObject, objToWrite.getClassInfo(), (asProxy) ? Operation.PROXY : aPersistenceType);
		
		for (AttributeInfo attrInfo : objToWrite.getClassInfo().getAttributeInfoIterator()) 
		{
			if( !asProxy || attrInfo.isKey()) {
				if( attrInfo instanceof ObjectReferenceAttributeInfo) 
				{
					TypedValueObject refObj =  (TypedValueObject) objToWrite.getValue(attrInfo);
					if( refObj != null) {
						boolean isProxy = asProxy || writeStrategy.isWriteAsProxy(objToWrite.getClassInfo(), (ObjectReferenceAttributeInfo) attrInfo,refObj);
						ObjectNode refJsonNode = createClassInfoNode(refObj, aPersistenceType, isProxy);
						jsonObject.put(attrInfo.getInternalName(), refJsonNode);
					} else {
						jsonObject.putNull(attrInfo.getInternalName());
					}

				} else if(  attrInfo instanceof PrimitiveAttributeInfo ) {
					Object primObj =  objToWrite.getValue(attrInfo);
					primitiveHandler.setCurPrimitivName(jsonObject, attrInfo.getInternalName());
					((PrimitiveAttributeInfo)attrInfo).getType().handlePrimitiveKey(primitiveHandler, primObj);

				} else {
					throw new InvalidClassInfoException("Unknown Attribute Type");
				}
			}
		}		
		
		for (AssociationInfo assocInfo :objToWrite.getClassInfo().getAssociationInfoIterator()) {
			
			if ( !writeStrategy.isWriteAsProxy(objToWrite.getClassInfo(), assocInfo) && !asProxy) {
				ObjectList<? extends TypedValueObject> aModelColl = (ObjectList<? extends TypedValueObject>) objToWrite.getValue(assocInfo);
				
				ArrayNode jsonArray =  mapper.createArrayNode();
				if( aModelColl!= null) {
					jsonArray = writeModelCollection(aModelColl, aPersistenceType);
				}
				jsonObject.put(assocInfo.getNameResource(), jsonArray);
			}
			
		}

		
		
		return jsonObject;
	}

	private void addMetaDataFields(ObjectNode jsonObject, ClassInfo aClassInfo, Operation aPersistenceType)
	{
		jsonObject.put(NAMESPACE_TAG, aClassInfo.getRepoName());
		jsonObject.put(CLASS_INTERNAL_NAME_TAG, aClassInfo.getInternalName() );
		jsonObject.put(PERSISTENCE_TAG, (aPersistenceType == null) ? "" : aPersistenceType.name() );
	}

	
	/**
	 * Add Primitive values to a Json Object. Set #setCurPrimitivName before call o handleValue
	 */
	private static class JsonPrimitiveHandler implements PrimitiveTypeVisitor
	{		 
		private ObjectNode parentJsonObj;
		private String curPrimitivName;

		private JsonPrimitiveHandler()
		{
		}

		public void setCurPrimitivName(ObjectNode aParentJsonObject, String curPrimitivName)
		{
			this.parentJsonObj = aParentJsonObject;
			this.curPrimitivName = curPrimitivName;
		}

		
		
		@Override
		public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException
		{
			parentJsonObj.put(curPrimitivName, aValue);
		}

		@Override
		public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException
		{
			parentJsonObj.put(curPrimitivName, aValue);
		}

		@Override
		public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException
		{		
			if( aValue != null ) {
				parentJsonObj.put(curPrimitivName, ISO8601Utils.format(aValue));
			} else {
				parentJsonObj.putNull(curPrimitivName);
			}
		}

		@Override
		public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException
		{
			parentJsonObj.put(curPrimitivName, aValue);
		}

		@Override
		public void handleValue(Long aValue, LongType aType) throws JdyPersistentException
		{
			parentJsonObj.put(curPrimitivName, aValue);
		}

		@Override
		public void handleValue(String aValue, TextType aType) throws JdyPersistentException
		{
			parentJsonObj.put(curPrimitivName, aValue);
		}

		@Override
		public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException
		{
			parentJsonObj.put(curPrimitivName, aValue);
		}


                @Override
		public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException
		{
			// TODO Auto-generated method stub
			
		}


	}
	
	public static interface WriteReferenceStrategy
	{
		public boolean isWriteAsProxy(ClassInfo classInfo, ObjectReferenceAttributeInfo attrInfo, TypedValueObject refObj);

		public boolean isWriteAsProxy(ClassInfo classInfo,	AssociationInfo assocInfo);
	}
	
	public static class WriteAllDependentStrategy implements WriteReferenceStrategy
	{

		@Override
		public boolean isWriteAsProxy(ClassInfo classInfo, 	ObjectReferenceAttributeInfo attrInfo, TypedValueObject refObj) {
			
			return !attrInfo.isDependent() || attrInfo.isInAssociation();
		}

		@Override
		public boolean isWriteAsProxy(ClassInfo classInfo,	AssociationInfo assocInfo) 
		{
			return false;
		}
		
	}
	
	public static class WriteDependentAsProxyStrategy implements WriteReferenceStrategy
	{

		@Override
		public boolean isWriteAsProxy(ClassInfo classInfo, 	ObjectReferenceAttributeInfo attrInfo, TypedValueObject refObj) {
			
			return true;
		}

		@Override
		public boolean isWriteAsProxy(ClassInfo classInfo,	AssociationInfo assocInfo) 
		{
			return true;
		}
		
	}
	
	public static class WriteAllStrategy implements WriteReferenceStrategy
	{

		@Override
		public boolean isWriteAsProxy(ClassInfo classInfo, 	ObjectReferenceAttributeInfo attrInfo, TypedValueObject refObj) {
			
			return false;
		}

		@Override
		public boolean isWriteAsProxy(ClassInfo classInfo,	AssociationInfo assocInfo) 
		{
			return false;
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
