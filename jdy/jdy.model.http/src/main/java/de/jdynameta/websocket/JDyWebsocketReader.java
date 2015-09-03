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
package de.jdynameta.websocket;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.mapper.helper.impl.DateMapper;
import com.sdicons.json.model.JSONArray;
import com.sdicons.json.model.JSONBoolean;
import com.sdicons.json.model.JSONDecimal;
import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONNull;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONString;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedHashedValueObject;
import de.jdynameta.json.JsonFileWriter;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.persistence.manager.impl.DefaultPersistentOperation;

/**
 * Read the content of an XmlFile into a list of ValueModels
 *
 * @author Rainer
 */
public abstract class JDyWebsocketReader
{
    public ObjectList<PersistentOperation<TypedValueObject>> readObjectList(StringReader stringReader) throws JdyPersistentException
    {
        JSONParser lParser = new JSONParser(stringReader);
        return readObjectList(lParser);
    }

    public ObjectList<PersistentOperation<TypedValueObject>> readObjectList(JSONParser lParser) throws JdyPersistentException
    {
        ChangeableObjectList<PersistentOperation<TypedValueObject>> resultList = new ChangeableObjectList<>();

        try
        {
            JSONValue valueArray = lParser.nextValue();
            if (valueArray instanceof JSONArray)
            {
                JSONArray jsonArray = (JSONArray) valueArray;
                for (JSONValue curValue : jsonArray.getValue())
                {
                    if (curValue instanceof JSONObject)
                    {
                        resultList.addObject(createModelForJsonObj((JSONObject) curValue));
                    } else
                    {
                        throw new JdyPersistentException("Error parsing JSON. No JSONObject: " + curValue.render(true));
                    }
                }
            }
        } catch (TokenStreamException | RecognitionException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

        return resultList;
    }

    protected PersistentOperation<TypedValueObject> createModelForJsonObj(JSONObject jsonObj)
            throws JdyPersistentException
    {
        String namespaceName = (String) ((JSONString) jsonObj.get(JsonFileWriter.NAMESPACE_TAG)).getValue();
        String classInternalName = (String) ((JSONString) jsonObj.get(JsonFileWriter.CLASS_INTERNAL_NAME_TAG)).getValue();
        String persistenceOperation = (String) ((JSONString) jsonObj.get(JsonFileWriter.PERSISTENCE_TAG)).getValue();

        ClassInfo concreteClass = getConcreteClass(namespaceName, classInternalName);

        TypedHashedValueObject result = new TypedHashedValueObject();
        result.setClassInfo(concreteClass);

        JsonAttributeHandler attrHandler = new JsonAttributeHandler(jsonObj, result);
        for (AttributeInfo curAttributeInfo : concreteClass.getAttributeInfoIterator())
        {
            curAttributeInfo.handleAttribute(attrHandler, null);
        }

        DefaultPersistentOperation<TypedValueObject> operation = new DefaultPersistentOperation<>(Operation.valueOf(persistenceOperation), concreteClass, result);

        return operation;
    }

    protected abstract ClassInfo getConcreteClass(String namespaceName, String classInternalName);

    protected TypedHashedValueObject createProxyModelForJsonObj(JSONObject jsonObj, ClassInfo aClassInfo)
            throws JdyPersistentException
    {
        String namespaceName = (String) ((JSONString) jsonObj.get(JsonFileWriter.NAMESPACE_TAG)).getValue();
        String classInternalName = (String) ((JSONString) jsonObj.get(JsonFileWriter.CLASS_INTERNAL_NAME_TAG)).getValue();
        String persistenceType = (String) ((JSONString) jsonObj.get(JsonFileWriter.PERSISTENCE_TAG)).getValue();

        ClassInfo concreteClass = getConcreteClass(namespaceName, classInternalName);

        TypedHashedValueObject result = new TypedHashedValueObject();
        result.setClassInfo(concreteClass);

        JsonAttributeHandler attrHandler = new JsonAttributeHandler(jsonObj, result);

        for (AttributeInfo curAttributeInfo : aClassInfo.getAttributeInfoIterator())
        {
            if (curAttributeInfo.isKey())
            {
                curAttributeInfo.handleAttribute(attrHandler, null);
            }
        }

        return result;
    }

    public class JsonAttributeHandler implements AttributeHandler
    {
        private final JSONObject jsonObj;
        private final TypedHashedValueObject result;

        public JsonAttributeHandler(JSONObject jsonObj, TypedHashedValueObject result)
        {
            super();
            this.jsonObj = jsonObj;
            this.result = result;
        }

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                throws JdyPersistentException
        {
            JSONValue attrValue = jsonObj.get(aInfo.getInternalName());
            if (attrValue instanceof JSONNull)
            {
                result.setValue(aInfo, null);
            } else if (attrValue instanceof JSONObject)
            {
                result.setValue(aInfo, createProxyModelForJsonObj((JSONObject) attrValue, aInfo.getReferencedClass()));
            } else
            {
                throw new JdyPersistentException("Wrong type for attr value: " + aInfo.getInternalName());
            }
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                throws JdyPersistentException
        {
            JSONValue attrValue = jsonObj.get(aInfo.getInternalName());

            if (attrValue == null)
            {
                throw new JdyPersistentException("Missing value for type in attr value: " + aInfo.getInternalName());
            }
            if (attrValue instanceof JSONNull)
            {
                result.setValue(aInfo, null);
            } else
            {
                this.result.setValue(aInfo, aInfo.getType().handlePrimitiveKey(new JsonValueGetVisitor(attrValue)));

            }
        }
    }

    public static class JsonValueGetVisitor implements PrimitiveTypeGetVisitor
    {
        private final JSONValue attrValue;

        public JsonValueGetVisitor(JSONValue anAttrValue)
        {
            super();
            this.attrValue = anAttrValue;
        }

        @Override
        public Boolean handleValue(BooleanType aType) throws JdyPersistentException
        {
            return ((JSONBoolean) attrValue).getValue();
        }

        @Override
        public BigDecimal handleValue(CurrencyType aType)
                throws JdyPersistentException
        {
            return ((JSONDecimal) attrValue).getValue();
        }

        @Override
        public Date handleValue(TimeStampType aType) throws JdyPersistentException
        {
            System.out.println(attrValue);
            try
            {
                return DateMapper.fromISO8601(((JSONString) attrValue).getValue());
            } catch (MapperException e)
            {
                throw new JdyPersistentException("Wrong type for time value: ");
            }
        }

        @Override
        public Double handleValue(FloatType aType) throws JdyPersistentException
        {
            return ((JSONDecimal) attrValue).getValue().doubleValue();
        }

        @Override
        public Long handleValue(LongType aType) throws JdyPersistentException
        {
            return ((JSONInteger) attrValue).getValue().longValue();
        }

        @Override
        public String handleValue(TextType aType) throws JdyPersistentException
        {
            return ((JSONString) attrValue).getValue();
        }

        @Override
        public String handleValue(VarCharType aType) throws JdyPersistentException
        {
            return ((JSONString) attrValue).getValue();
        }

        @Override
        public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
