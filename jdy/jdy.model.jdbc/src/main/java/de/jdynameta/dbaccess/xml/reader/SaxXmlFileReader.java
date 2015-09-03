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
package de.jdynameta.dbaccess.xml.reader;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.dbaccess.xml.sax.SaxDocumentHandler;
import de.jdynameta.dbaccess.xml.sax.SaxElementHandler;
import de.jdynameta.dbaccess.xml.sax.SaxHandler;

/**
 * Read the content of an XmlFile into a list of ValueModels
 *
 * @author Rainer
 */
public class SaxXmlFileReader
{
    public ObjectList<ValueObject> readObjectList(InputStream aStreamToParse, ClassInfo aClassInfo) throws JdyPersistentException
    {
        // Use an instance of ourselves as the SAX event handler

        ValueModelListCreator creator = new ValueModelListCreator(aClassInfo);

        DefaultHandler handler = new SaxHandler(creator);
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            // Parse the input 
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(aStreamToParse, handler);
        } catch (Throwable thr)
        {

            throw new JdyPersistentException(thr);
        }

        return creator.getResultList();
    }

    public static class ValueModelListCreator implements SaxDocumentHandler
    {
        private RootElementHandler rootHandler;
        private final ClassInfo resultClassInfo;
        private boolean isClosed;

        public ValueModelListCreator(ClassInfo aClassInfo)
        {
            super();
            this.resultClassInfo = aClassInfo;
            this.isClosed = false;
        }

        @Override
        public SaxElementHandler createRootObject()
        {
            this.rootHandler = new RootElementHandler(this.resultClassInfo);
            return this.rootHandler;
        }

        @Override
        public void close()
        {
            this.isClosed = true;
        }

        public ObjectList<ValueObject> getResultList()
        {
            assert (isClosed);
            return this.rootHandler.getResultList();
        }
    }

    public static class RootElementHandler implements SaxElementHandler
    {
        private ClassInfoElementHandler classInfoHandler;
        private final ClassInfo resultClassInfo;
        private boolean isClosed;

        /**
         * @param aResultClassInfo
         */
        public RootElementHandler(final ClassInfo aResultClassInfo)
        {
            super();
            this.resultClassInfo = aResultClassInfo;
            this.isClosed = false;
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            if (this.classInfoHandler != null)
            {
                throw new SAXException("Only one class info Node allowed");
            }
            if (!aElementName.equals("ClassInfo"))
            {
                throw new SAXException("Child of root Node must be ClassInfo");
            }

            String externalNameTmp = attrs.getValue("ExternalName");
            if (externalNameTmp == null)
            {
                throw new SAXException("No External name defined in ClassInfo");
            } else if (!this.resultClassInfo.getExternalName().equals(externalNameTmp))
            {
                throw new SAXException("External name " + externalNameTmp
                        + " is not the expected one: " + this.resultClassInfo.getExternalName());
            }

            this.classInfoHandler = new ClassInfoElementHandler(this.resultClassInfo);
            return this.classInfoHandler;
        }

        @Override
        public void close()
        {
            this.isClosed = true;
        }

        public ObjectList<ValueObject> getResultList()
        {
            assert (isClosed);
            return this.classInfoHandler.getResultList();
        }
    }

    public static class ClassInfoElementHandler implements SaxElementHandler
    {
        private ValueModelListHandler valueModelListHandler;
        private final ClassInfo resultClassInfo;
        private boolean isClosed;

        public ClassInfoElementHandler(final ClassInfo aResultClassInfo)
        {
            super();
            this.resultClassInfo = aResultClassInfo;
            this.isClosed = false;
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            if (!aElementName.equals("ValueModelList"))
            {
                throw new SAXException("Child of ClassInfo Node must be ValueModelList");
            }

            this.valueModelListHandler = new ValueModelListHandler(this.resultClassInfo);

            return this.valueModelListHandler;
        }

        @Override
        public void close()
        {
            this.isClosed = true;
        }

        /**
         * @return Returns the resultList.
         */
        public ObjectList<ValueObject> getResultList()
        {
            assert (isClosed);
            return this.valueModelListHandler.getResultList();
        }
    }

    public static class ValueModelListHandler implements SaxElementHandler
    {
        private final ChangeableObjectList<ValueObject> resultList;
        private final ClassInfo resultClassInfo;

        /**
         * @param aResultClassInfo
         */
        public ValueModelListHandler(final ClassInfo aResultClassInfo)
        {
            super();
            this.resultClassInfo = aResultClassInfo;
            this.resultList = new ChangeableObjectList<>();
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            if (!aElementName.equals("ValueModel"))
            {
                throw new SAXException("Child of ValueModelList Node must be ValueModel");
            }

            return new ValueModelElementHandler(this.resultList, this.resultClassInfo);
        }

        @Override
        public void close()
        {
        }

        /**
         * @return Returns the resultList.
         */
        public ObjectList<ValueObject> getResultList()
        {
            return this.resultList;
        }
    }

    public static class ValueModelElementHandler implements SaxElementHandler
    {
        private final ChangeableObjectList<ValueObject> resultList;
        private final HashedValueObject createdValueModel;
        private final ClassInfo resultClassInfo;

        /**
         * @param aResultList
         * @param aResultClassInfo
         */
        public ValueModelElementHandler(ChangeableObjectList<ValueObject> aResultList, ClassInfo aResultClassInfo)
        {
            super();
            this.resultList = aResultList;
            this.createdValueModel = new HashedValueObject();
            this.resultClassInfo = aResultClassInfo;
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            if (!aElementName.equals("PrimitiveAttribute") && !aElementName.equals("ObjectReference"))
            {
                throw new SAXException("Child of ValueModel Node must not be " + aElementName);
            }

            SaxElementHandler resultHandlerTmp;

            String externalNameTmp = attrs.getValue("ExternalName");
            if (externalNameTmp == null)
            {
                throw new SAXException("No External name defined in ClassInfo");
            }

            AttributeInfo attrInfo = this.resultClassInfo.getAttributeInfoForExternalName(externalNameTmp);

            if (attrInfo == null)
            {
                throw new SAXException("Attribute info with External name " + externalNameTmp
                        + " does not exist in Class: " + this.resultClassInfo.getExternalName());
            }

            if (aElementName.equals("PrimitiveAttribute"))
            {
                if (!(attrInfo instanceof PrimitiveAttributeInfo))
                {
                    throw new SAXException("Attribute info with External name " + externalNameTmp
                            + " should be a Primitive in Class: " + this.resultClassInfo.getExternalName());
                }
                resultHandlerTmp = new PrimitiveAttributeElementHandler(this.createdValueModel, (PrimitiveAttributeInfo) attrInfo);
            } else
            {
                if (!(attrInfo instanceof ObjectReferenceAttributeInfo))
                {
                    throw new SAXException("Attribute info with External name " + externalNameTmp
                            + " should be a ObjectReference in Class: " + this.resultClassInfo.getExternalName());
                }
                resultHandlerTmp = new ObjectReferenceElementHandler(this.createdValueModel, (ObjectReferenceAttributeInfo) attrInfo);
            }

            return resultHandlerTmp;
        }

        @Override
        public void close()
        {
            resultList.addObject(createdValueModel);
        }

    }

    public static class PrimitiveAttributeElementHandler implements SaxElementHandler
    {
        /**
         * Value to which this handler should add its created attribute
         */
        private final HashedValueObject valueModel;
        private final PrimitiveAttributeInfo attributeInfo;

        /**
         * @param aValueModel
         * @param aAttributeInfo
         */
        public PrimitiveAttributeElementHandler(final HashedValueObject aValueModel, PrimitiveAttributeInfo aAttributeInfo)
        {
            super();
            this.valueModel = aValueModel;
            this.attributeInfo = aAttributeInfo;
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            String valueTmp = attrs.getValue("value");

            return new PrimitiveTypeElementHandler(this.valueModel, valueTmp, this.attributeInfo, aElementName);
        }

        @Override
        public void close()
        {
        }
    }

    public static class PrimitiveTypeElementHandler implements SaxElementHandler
    {
        /**
         * Value to which this handler should add its created attribute
         */
        private final HashedValueObject valueModel;
        private final String value;
        private final PrimitiveAttributeInfo attributeInfo;
        private final String typeElementName;

        /**
         * @param aValueModel
         * @param aValue
         * @param aAttributeInfo
         * @param aTypeElementName
         */
        public PrimitiveTypeElementHandler(final HashedValueObject aValueModel, final String aValue, PrimitiveAttributeInfo aAttributeInfo, String aTypeElementName)
        {
            super();
            this.valueModel = aValueModel;
            this.value = aValue;
            this.attributeInfo = aAttributeInfo;
            this.typeElementName = aTypeElementName;
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            throw new SAXException("No Child allowed below type " + attributeInfo.getExternalName()
                    + " elemnt name: " + aElementName);
        }

        @Override
        public void close() throws SAXException
        {
            StringValueGetVisitor typeVisitor = new StringValueGetVisitor(this.value, this.typeElementName);

            try
            {
                this.valueModel.setValue(attributeInfo, attributeInfo.getType().handlePrimitiveKey(typeVisitor));
            } catch (JdyPersistentException excp)
            {
                throw new SAXException(excp);
            }
        }
    }

    public static class StringValueGetVisitor implements PrimitiveTypeGetVisitor
    {
        private final String textValue;
        private final String typeName;

        /**
         *
         * @param aTextValue
         * @param aElementName
         */
        public StringValueGetVisitor(String aTextValue, String aElementName)
        {
            super();
            this.textValue = aTextValue;
            this.typeName = aElementName;
        }

        @Override
        public Boolean handleValue(BooleanType aType)
                throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public BigDecimal handleValue(CurrencyType aType)
                throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Date handleValue(TimeStampType aType) throws JdyPersistentException
        {
            if (!this.typeName.equals("TimeStampType"))
            {
                throw new JdyPersistentException("Error in type: " + this.typeName
                        + "should be " + "DateType");
            }
            return Date.valueOf(this.textValue);
        }

        @Override
        public Double handleValue(FloatType aType) throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Long handleValue(LongType aType) throws JdyPersistentException
        {
            if (!this.typeName.equals("IntegerType"))
            {
                throw new JdyPersistentException("Error in type: " + this.typeName
                        + "should be " + "IntegerType");
            }
            return Long.decode(this.textValue);
        }

        @Override
        public String handleValue(TextType aType) throws JdyPersistentException
        {
            if (!this.typeName.equals("TextType"))
            {
                throw new JdyPersistentException("Error in type: " + this.typeName
                        + "should be " + "TextType");
            }
            return this.textValue;
        }

        @Override
        public String handleValue(VarCharType aType) throws JdyPersistentException
        {
            if (!this.typeName.equals("VarCharType"))
            {
                throw new JdyPersistentException("Error in type: " + this.typeName
                        + "should be " + "VarCharType");
            }
            return this.textValue;
        }

        @Override
        public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class ObjectReferenceElementHandler implements SaxElementHandler
    {
        /**
         * Value to which this handler should add its created attribute
         */
        private final HashedValueObject valueModel;
        private final ObjectReferenceAttributeInfo attributeInfo;
        private final HashedValueObject referenceValueModel;

        /**
         * @param aValueModel
         * @param aAttrInfo
         */
        public ObjectReferenceElementHandler(HashedValueObject aValueModel, ObjectReferenceAttributeInfo aAttrInfo)
        {
            super();
            this.valueModel = aValueModel;
            this.attributeInfo = aAttrInfo;
            this.referenceValueModel = new HashedValueObject();
        }

        @Override
        public SaxElementHandler createChild(String aElementName, Attributes attrs) throws SAXException
        {
            if (!aElementName.equals("PrimitiveAttribute") && !aElementName.equals("ObjectReference"))
            {
                throw new SAXException("Child of ValueModel Node must not be " + aElementName);
            }

            SaxElementHandler resultHandlerTmp;

            String externalNameTmp = attrs.getValue("ExternalName");
            if (externalNameTmp == null)
            {
                throw new SAXException("No External name defined in ClassInfo");
            }

            AttributeInfo attrInfo = this.attributeInfo.getReferencedClass().getAttributeInfoForExternalName(externalNameTmp);

            if (attrInfo == null)
            {
                throw new SAXException("Attribute info with External name " + externalNameTmp
                        + " does not exist in Class: " + this.attributeInfo.getReferencedClass().getExternalName());
            }

            if (aElementName.equals("PrimitiveAttribute"))
            {
                if (!(attrInfo instanceof PrimitiveAttributeInfo))
                {
                    throw new SAXException("Attribute info with External name " + externalNameTmp
                            + " should be a Primitive in Class: " + this.attributeInfo.getReferencedClass().getExternalName());
                }
                resultHandlerTmp = new PrimitiveAttributeElementHandler(this.referenceValueModel, (PrimitiveAttributeInfo) attrInfo);
            } else
            {
                if (!(attrInfo instanceof ObjectReferenceAttributeInfo))
                {
                    throw new SAXException("Attribute info with External name " + externalNameTmp
                            + " should be a ObjectReference in Class: " + this.attributeInfo.getReferencedClass().getExternalName());
                }
                resultHandlerTmp = new ObjectReferenceElementHandler(this.referenceValueModel, (ObjectReferenceAttributeInfo) attrInfo);
            }

            return resultHandlerTmp;
        }

        @Override
        public void close()
        {
            this.valueModel.setValue(attributeInfo, this.referenceValueModel);
        }
    }

}
