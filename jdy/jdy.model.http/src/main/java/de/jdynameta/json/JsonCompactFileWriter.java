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
package de.jdynameta.json;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
import de.jdynameta.base.metainfo.util.AttributeVisibility;
import de.jdynameta.base.metainfo.util.AttributeVisibilityAdaptor;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.json.JsonFileWriter.WriteReferenceStrategy;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;

/**
 *
 * @author Rainer Schneider
 *
 */
public class JsonCompactFileWriter 
{
	public static final String TYPE_TAG = "@t";
	public static final String PERSISTENCE_TAG = "@p";

	private final ObjectMapper mapper;
	private final JsonPrimitiveHandler primitiveHandler;
	
	private final WriteReferenceStrategy writeStrategy;
	private final boolean flush;
	private final boolean writeNullValues = false;
	private final boolean writeGenreatedAtr = false;
	private final boolean writePersistence = false;
	
	// name to abbreviation map for attribute and association anmes
	private final HashMap<String, String> name2Abbr;
	
	public JsonCompactFileWriter(WriteReferenceStrategy aWriteStrategy, boolean doFlush, HashMap<String, String> aName2Abbr)
	{
		this.mapper = new ObjectMapper();
		this.primitiveHandler = new JsonPrimitiveHandler();
		this.writeStrategy = aWriteStrategy;
		this.flush = doFlush;
		this.name2Abbr = (aName2Abbr == null) ? new HashMap<>() : aName2Abbr;
	}	
	
	
	public void writeObjectList(Writer out, ClassInfo aInfo, ObjectList<? extends TypedValueObject> aObjectList, Operation aPersistenceType )
	 	throws TransformerConfigurationException, JdyPersistentException
	{
		
		ArrayNode jsonObjects = writeModelCollection( aObjectList, aPersistenceType, null);
		try {
			 JsonGenerator jsonGenerator = mapper.getJsonFactory().createJsonGenerator(out);
			 
			mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, this.flush);
			mapper.writeValue(jsonGenerator, jsonObjects);
		} catch (IOException ex) {
			throw new JdyPersistentException(ex);
		} 
	}
	
	
	private ArrayNode writeModelCollection( ObjectList<? extends TypedValueObject> aModelColl, Operation aPersistenceType, AssociationInfo aAssocInfo) 
		throws JdyPersistentException
	{
        final ArrayNode jsonArray = mapper.createArrayNode();
		for (Iterator<? extends TypedValueObject> modelIter = aModelColl.iterator(); modelIter.hasNext();) {
			
			TypedValueObject curObj = modelIter.next();
			if(curObj != null) {
	
				ObjectNode newJsonObj = writeObjectToJson(curObj, aPersistenceType
						, (aAssocInfo == null) ? null : new AssocClmnVisibility(aAssocInfo));
				jsonArray.add(newJsonObj);
			}
		}
		
		return jsonArray;
	}
	
	private synchronized ObjectNode writeObjectToJson( TypedValueObject objToWrite, Operation aPersistenceType, AttributeVisibility aClmnVisibility) 
		throws JdyPersistentException
	{
		ObjectNode jsonObject = createClassInfoNode(objToWrite, aPersistenceType, false, aClmnVisibility);
		return jsonObject;
	}

	private ObjectNode createClassInfoNode(TypedValueObject objToWrite, Operation aPersistenceType, boolean asProxy, AttributeVisibility aClmnVisibility) throws JdyPersistentException
	{
		ObjectNode jsonObject = mapper.createObjectNode();
		addMetaDataFields(jsonObject, objToWrite.getClassInfo(), (asProxy) ? Operation.PROXY : aPersistenceType);
		
		for (AttributeInfo curAttrInfo : objToWrite.getClassInfo().getAttributeInfoIterator()) 
		{
			if( aClmnVisibility == null || aClmnVisibility.isAttributeVisible(curAttrInfo)) {
				if( (!asProxy || curAttrInfo.isKey())) {
					if( curAttrInfo instanceof ObjectReferenceAttributeInfo) 
					{
						TypedValueObject refObj =  (TypedValueObject) objToWrite.getValue(curAttrInfo);
						if( refObj != null) {
							boolean isProxy = asProxy || writeStrategy.isWriteAsProxy(objToWrite.getClassInfo(), (ObjectReferenceAttributeInfo) curAttrInfo,refObj);
							ObjectNode refJsonNode = createClassInfoNode(refObj, aPersistenceType, isProxy, null);
							jsonObject.put(nameForAttr(curAttrInfo), refJsonNode);
						} else {
							if(writeNullValues) {
								jsonObject.putNull(nameForAttr(curAttrInfo));
							}
						}
	
					} else if(  curAttrInfo instanceof PrimitiveAttributeInfo ) {
						Object primObj =  objToWrite.getValue(curAttrInfo);
						if(primObj != null || writeNullValues) {
							
							if( !curAttrInfo.isGenerated() || writeGenreatedAtr) {
								primitiveHandler.setCurPrimitivName(jsonObject, nameForAttr(curAttrInfo));
								((PrimitiveAttributeInfo)curAttrInfo).getType().handlePrimitiveKey(primitiveHandler, primObj);
							}
						}
	
					} else {
						throw new InvalidClassInfoException("Unknown Attribute Type");
					}
				}
			}
		}		
		
		for (AssociationInfo assocInfo :objToWrite.getClassInfo().getAssociationInfoIterator()) {
			
			if ( !writeStrategy.isWriteAsProxy(objToWrite.getClassInfo(), assocInfo) && !asProxy) {
				ObjectList<? extends TypedValueObject> aModelColl = (ObjectList<? extends TypedValueObject>) objToWrite.getValue(assocInfo);
				
				ArrayNode jsonArray =  mapper.createArrayNode();
				if( aModelColl!= null) {
					jsonArray = writeModelCollection(aModelColl, aPersistenceType, assocInfo);
					jsonObject.put(nameForAssoc(assocInfo), jsonArray);
				} else {
					if(writeNullValues) {
						jsonObject.put(nameForAssoc(assocInfo), jsonArray);
					}
				}
			}
			
		}

		
		
		return jsonObject;
	}

	private String nameForAssoc(AssociationInfo anAssocInfo)
	{
		return (this.name2Abbr.containsKey(anAssocInfo.getNameResource())) 
					? this.name2Abbr.get(anAssocInfo.getNameResource()) 
					: anAssocInfo.getNameResource();
	}

	
	private String nameForAttr(AttributeInfo attrInfo)
	{
		return (this.name2Abbr.containsKey(attrInfo.getInternalName())) 
					? this.name2Abbr.get(attrInfo.getInternalName()) 
					: attrInfo.getInternalName();
	}
	
	private void addMetaDataFields(ObjectNode jsonObject, ClassInfo aClassInfo, Operation aPersistenceType)
	{
		jsonObject.put(TYPE_TAG, aClassInfo.getShortName() );
		if(writePersistence) {
			jsonObject.put(PERSISTENCE_TAG, (aPersistenceType == null) ? "" : aPersistenceType.name() );
		}
	}

	private static class AssocClmnVisibility extends AttributeVisibilityAdaptor
	{
		private final AssociationInfo assocInfo; 
		
		public AssocClmnVisibility(AssociationInfo aAssocInfo)
		{
			super();
			assocInfo = aAssocInfo;
		}

                @Override
		public boolean isAttributeVisible(AttributeInfo aAttrInfo)
		{
			return !assocInfo.getMasterClassReference().equals(aAttrInfo) && !aAttrInfo.isGenerated();
		}
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
}
