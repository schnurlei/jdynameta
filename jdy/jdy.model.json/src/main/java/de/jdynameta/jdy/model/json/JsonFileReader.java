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
package de.jdynameta.jdy.model.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

import de.jdynameta.base.metainfo.AssociationInfo;
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
import de.jdynameta.base.objectlist.AssocObjectList;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.manager.PersistentOperation.Operation;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.persistence.state.ApplicationObjImpl;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Read the content of an XmlFile into a list of ValueModels
 *
 * @author Rainer
 */
public class JsonFileReader
{
    private final ObjectMapper mapper;

    public JsonFileReader()
    {
        this.mapper = new ObjectMapper();
    }

    public ObjectList<ApplicationObj> readObjectList(StringReader stringReader, ClassInfo aClassInfo) throws JdyPersistentException
    {
        try
        {
            JsonNode rootNode = mapper.readValue(stringReader, JsonNode.class);
            return readObjectList(rootNode, aClassInfo);
        } catch (JsonParseException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } catch (JsonMappingException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } catch (IOException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

    }

    public ObjectList<ApplicationObj> readObjectList(InputStream aStreamToParse, ClassInfo aClassInfo) throws JdyPersistentException
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            JsonNode rootNode = mapper.readValue(aStreamToParse, JsonNode.class);
            return readObjectList(rootNode, aClassInfo);
        } catch (JsonParseException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } catch (JsonMappingException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        } catch (IOException ex)
        {
            throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
        }

    }

    public ObjectList<ApplicationObj> readObjectList(JsonNode aJsonNode, ClassInfo aClassInfo) throws JdyPersistentException
    {
        ChangeableObjectList<ApplicationObj> resultList = new ChangeableObjectList<>();

        if (aJsonNode.isArray())
        {
            for (JsonNode curValue : aJsonNode)
            {
                if (curValue.isObject())
                {
                    resultList.addObject(createModelForJsonObj((ObjectNode) curValue, aClassInfo));
                } else
                {
                    throw new JdyPersistentException("Error parsing JSON. No JSONObject: " + curValue.toString());
                }
            }
        }

        return resultList;
    }

    protected ApplicationObj createModelForJsonObj(ObjectNode jsonObj, ClassInfo aClassInfo)
            throws JdyPersistentException
    {
        ClassInfo concreteClass = createClassInfoFromMeta(jsonObj, aClassInfo);
        String persistenceType = jsonObj.get((JsonFileWriter.PERSISTENCE_TAG)).asText();

        ApplicationObjImpl result;
        if (Operation.valueOf(persistenceType) == Operation.PROXY)
        {
            result = new ApplicationObjImpl(concreteClass, false);
            JsonAttributeHandler attrHandler = new JsonAttributeHandler(jsonObj, result);
            for (AttributeInfo curAttributeInfo : aClassInfo.getAttributeInfoIterator())
            {
                if (curAttributeInfo.isKey())
                {
                    curAttributeInfo.handleAttribute(attrHandler, null);
                }
            }

        } else
        {
            boolean isNew = Operation.valueOf(persistenceType) == Operation.INSERT;
            result = new ApplicationObjImpl(concreteClass, isNew);

            JsonAttributeHandler attrHandler = new JsonAttributeHandler(jsonObj, result);
            for (AttributeInfo curAttributeInfo : concreteClass.getAttributeInfoIterator())
            {
                curAttributeInfo.handleAttribute(attrHandler, null);
            }

            for (AssociationInfo curAssoc : concreteClass.getAssociationInfoIterator())
            {
                AssocObjectList<ApplicationObj> objList = createAssociationList(jsonObj, result, curAssoc);
                result.setValue(curAssoc, objList);
            }

        }

        return result;
    }

    private AssocObjectList<ApplicationObj> createAssociationList(ObjectNode aMasterNode, ApplicationObjImpl aMasterObj, AssociationInfo curAssoc) throws JdyPersistentException
    {
        AssocObjectList<ApplicationObj> objList;

        JsonNode assocNode = aMasterNode.get(curAssoc.getNameResource());

        if (assocNode == null)
        {
            objList = null;
        } else
        {
            objList = new AssocObjectList<>(curAssoc, aMasterObj);
            if (assocNode.isArray())
            {
                for (JsonNode curValue : assocNode)
                {
                    if (curValue.isObject())
                    {
                        objList.addObject(createModelForJsonObj((ObjectNode) curValue, curAssoc.getDetailClass()));
                    } else
                    {
                        throw new JdyPersistentException("Error parsing JSON. No JSONObject: " + curValue.toString());
                    }
                }
            }
        }

        return objList;
    }

    private ClassInfo createClassInfoFromMeta(ObjectNode jsonObj, ClassInfo aClassInfo)
    {
        String repoName = jsonObj.get(JsonFileWriter.NAMESPACE_TAG).asText();
        String classInternalName = jsonObj.get((JsonFileWriter.CLASS_INTERNAL_NAME_TAG)).asText();
        ClassInfo concreteClass = getConcreteClass(aClassInfo, repoName, classInternalName);
        return concreteClass;
    }

    private ClassInfo getConcreteClass(ClassInfo aClassInfo, String aRepoName, String classInternalName)
    {
        ClassInfo concreteClass = null;
        if (aClassInfo.getInternalName().equals(classInternalName)
                && aClassInfo.getRepoName().equals(aRepoName))
        {
            concreteClass = aClassInfo;
        }

        for (Iterator<ClassInfo> iterator = aClassInfo.getAllSubclasses().iterator(); concreteClass == null && iterator.hasNext();)
        {
            ClassInfo curClassInfo = iterator.next();
            concreteClass = getConcreteClass(curClassInfo, aRepoName, classInternalName);
        }
        return concreteClass;
    }

    public class JsonAttributeHandler implements AttributeHandler
    {
        private final ObjectNode jsonObj;
        private final ApplicationObj result;

        public JsonAttributeHandler(ObjectNode aCurValue, ApplicationObj result)
        {
            super();
            this.jsonObj = aCurValue;
            this.result = result;
        }

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle)
                throws JdyPersistentException
        {
            JsonNode attrValue = jsonObj.get(aInfo.getInternalName());
            if (attrValue.isNull())
            {
                result.setValue(aInfo, null);
            } else if (attrValue.isObject())
            {
                result.setValue(aInfo, createModelForJsonObj((ObjectNode) attrValue, aInfo.getReferencedClass()));
            } else
            {
                throw new JdyPersistentException("Wrong type for attr value: " + aInfo.getInternalName());
            }
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
                throws JdyPersistentException
        {
            JsonNode attrValue = jsonObj.get(aInfo.getInternalName());

            if (attrValue == null)
            {
//				throw new JdyPersistentException("Missing value for type in attr value: " + aInfo.getInternalName());
            } else
            {
                if (attrValue.isNull())
                {
                    result.setValue(aInfo, null);
                } else
                {
                    this.result.setValue(aInfo, aInfo.getType().handlePrimitiveKey(new JsonValueGetVisitor(attrValue)));
                }
            }
        }
    }

    public static class JsonValueGetVisitor implements PrimitiveTypeGetVisitor
    {
        private final JsonNode attrValue;

        /**
         *
         * @param aAttrValue
         */
        public JsonValueGetVisitor(JsonNode aAttrValue)
        {
            super();
            this.attrValue = aAttrValue;
        }

        @Override
        public Boolean handleValue(BooleanType aType) throws JdyPersistentException
        {
            return attrValue.asBoolean();
        }

        @Override
        public BigDecimal handleValue(CurrencyType aType)
                throws JdyPersistentException
        {
            return new BigDecimal(attrValue.asText());
        }

        @Override
        public Date handleValue(TimeStampType aType) throws JdyPersistentException
        {
           try {
                return ISO8601Utils.parse(attrValue.asText(), new ParsePosition(0));
            } catch (ParseException ex) {
                throw new JdyPersistentException(ex);
            }
        }

        @Override
        public Double handleValue(FloatType aType) throws JdyPersistentException
        {
            return attrValue.asDouble();
        }

        @Override
        public Long handleValue(LongType aType) throws JdyPersistentException
        {
            return attrValue.asLong();
        }
       
        @Override
        public String handleValue(TextType aType) throws JdyPersistentException
        {
            return attrValue.asText();
        }

        @Override
        public String handleValue(VarCharType aType) throws JdyPersistentException
        {
            return attrValue.asText();
        }

        @Override
        public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
