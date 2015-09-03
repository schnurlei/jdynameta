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
package de.jdynameta.testcommon.testdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ClassInfoQuery;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
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
import de.jdynameta.base.objectlist.DefaultObjectListModel;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * Class to create a list of random data for a given {@link ClassInfo}
 *
 * @author rs
 *
 * @param <TResult>
 */
public abstract class TestDataCreator<TResult extends ChangeableValueObject>
{
    private ArrayList<String> wordList;

    public TestDataCreator()
    {
        this.wordList = new ArrayList<>();

        TestDataCreator.class.getResourceAsStream("wordlist.txt");

        try
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(TestDataCreator.class.getResourceAsStream("wordlist.txt"), "ISO-8859-1"));

            try
            {
                String line;
                while ((line = input.readLine()) != null)
                {
                    wordList.add(line);
                }
            } catch (IOException e)
            {
                // ignore
            } finally
            {
                if (null != input)
                {
                    try
                    {
                        input.close();
                    } catch (IOException e)
                    {
                        // ignore
                    }
                }
            }
        } catch (UnsupportedEncodingException e)
        {
            // ignore
        }

    }

    /**
     * Create a list of ValueObjects for the given ClassInfo
     *
     * @param aClassInfo
     * @param objToCreateCount count of objects that are created
     * @param query
     * @return
     */
    public ObjectListModel<TResult> createTestData(ClassInfo aClassInfo, int objToCreateCount, ClassInfoQuery query)
    {
        ArrayList<TResult> allData = new ArrayList<>();
        for (int i = 0; i < objToCreateCount; i++)
        {

            TResult model = createValueModelForClassInfo(aClassInfo);

            try
            {
                if (query == null || query.matchesObject(model))
                {
                    allData.add(model);
                }
            } catch (JdyPersistentException e)
            {
                e.printStackTrace();
            }
        }

        return new DefaultObjectListModel<>(allData);
    }

    /**
     * @param aClassInfo
     * @return
     */
    protected TResult createValueModelForClassInfo(ClassInfo aClassInfo)
    {
        TResult model = createEmptyResult(aClassInfo);

        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {

            try
            {
                setCellValueForAttribute(model, curAttr);
            } catch (JdyPersistentException e)
            {
                e.printStackTrace();
            }
        }
        return model;
    }

    protected abstract TResult createEmptyResult(ClassInfo aClassInfo);

    protected void setCellValueForAttribute(final ChangeableValueObject curValueObj, AttributeInfo curAttr) throws JdyPersistentException
    {
        final PrimitiveTypeGetVisitor typeVisitor = new PrimitiveTypeGetVisitor()
        {

            @Override
            public BlobByteArrayHolder handleValue(BlobType aType) throws JdyPersistentException
            {
                return null;
            }

            @Override
            public String handleValue(VarCharType aType) throws JdyPersistentException
            {
                int charCount = (int) (Math.random() * aType.getLength());

                StringBuilder buffer = new StringBuilder(charCount);
                // use wordlist when it is filled
                if (wordList.size() > 50)
                {

                    while (buffer.length() < charCount)
                    {

                        int wordPos = (int) (Math.random() * wordList.size());
                        String word = wordList.get(wordPos);
                        buffer.append(word).append(' ');
                    }
                    buffer.setLength(charCount);
                } else
                {

                    for (int i = 0; i < charCount; i++)
                    {

                        int asciiRange = (int) 'z' - (int) 'a';
                        buffer.append(new Character((char) ('a' + Math.random() * asciiRange)));
                    }
                }

                return buffer.toString();
            }

            @Override
            public String handleValue(TextType aType) throws JdyPersistentException
            {
                int charCount = (int) (Math.random() * aType.getLength());
                StringBuilder buffer = new StringBuilder(charCount);
                for (int i = 0; i < charCount; i++)
                {
                    int asciiRange = (int) 'Z' - (int) 'A';
                    buffer.append(new Character((char) ('A' + Math.random() * asciiRange)));
                }
                return buffer.toString();
            }

            @Override
            public BigDecimal handleValue(CurrencyType aType) throws JdyPersistentException
            {
                return new BigDecimal(Math.random() * aType.getMaxValue().doubleValue()).setScale((int) aType.getScale(), BigDecimal.ROUND_HALF_UP);
            }

            @Override
            public Date handleValue(TimeStampType aType) throws JdyPersistentException
            {
                return new Date(System.currentTimeMillis() + (long) (Math.random() * 24 * 60 * 60 * 1000));
            }

            @Override
            public Double handleValue(FloatType aType) throws JdyPersistentException
            {
                return Math.random() * 1000000.0;
            }

            @Override
            public Boolean handleValue(BooleanType aType) throws JdyPersistentException
            {
                return Math.random() > 0.5;
            }

            @Override
            public Long handleValue(LongType aType) throws JdyPersistentException
            {
                return (long) (Math.random() * aType.getMaxValue());
            }
        };

        curAttr.handleAttribute(new AttributeHandler()
        {

            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle) throws JdyPersistentException
            {
                curValueObj.setValue(aInfo, aInfo.getType().handlePrimitiveKey(typeVisitor));
            }

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject objToHandle) throws JdyPersistentException
            {
                curValueObj.setValue(aInfo, createValueModelForClassInfo(aInfo.getReferencedClass()));

            }
        }, curValueObj.getValue(curAttr));

    }

    public static JdyClassInfoModel createSmallClassInfo()
    {
        JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("SmallClassInfo");
        classInfo.addLongAttr("LongAttribute", 0, 500).setIsKey(true);
        classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
        classInfo.addBooleanAttr("BooleanAttribute");

        return classInfo;
    }

    public static JdyClassInfoModel createMediumClassInfo()
    {
        JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("MediumClassInfo");
        classInfo.addLongAttr("LongAttribute", 0, 500).setIsKey(true);
        classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
        classInfo.addDecimalAttr("DecimalAttribute", new BigDecimal(-100.00), new BigDecimal(100.00), 2).setNotNull(true);
        classInfo.addBooleanAttr("BooleanAttribute");
        classInfo.addFloatAttr("FloatAttribute").setNotNull(true);
        classInfo.addTimestampAttr("TimestampAttribute").setNotNull(true);
        classInfo.addVarCharAttr("VarcharAttribute", 1000).setNotNull(true);

        return classInfo;
    }

    public static JdyClassInfoModel createLongClassInfo()
    {
        JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("LargeClassInfo");
        classInfo.addLongAttr("LongAttribute", 0, 500).setIsKey(true);
        classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
        classInfo.addDecimalAttr("DecimalAttribute", new BigDecimal(-100.00), new BigDecimal(100.00), 2).setNotNull(true);
        classInfo.addBooleanAttr("BooleanAttribute");
        classInfo.addFloatAttr("FloatAttribute").setNotNull(true);
        classInfo.addTimestampAttr("TimestampAttribute").setNotNull(true);
        classInfo.addVarCharAttr("VarcharAttribute", 1000).setNotNull(true);
        classInfo.addTimestampAttr("TimeAttr", false, true);
        classInfo.addTimestampAttr("DateAttr", true, false);
        classInfo.addDecimalAttr("DecimalAttribute2", new BigDecimal(-1000000.00), new BigDecimal(1000000.00), 2).setNotNull(true);
        classInfo.addLongAttr("LongAttribute5", -500, 500);
        classInfo.addLongAttr("LongAttribute6", 0, 500);

        return classInfo;
    }

    public static JdyClassInfoModel createClassWithReference()
    {
        JdyClassInfoModel referencedInfo = new JdyClassInfoModel().setInternalName("ReferencedClass");
        referencedInfo.addLongAttr("RefKeyLongAttr", 0, 500).setIsKey(true);
        referencedInfo.addTextAttr("RefKeyTextAttr", 50).setIsKey(true);
        referencedInfo.addDecimalAttr("RefKeyDecimalAttr", new BigDecimal(-100000000), new BigDecimal(100000000), 3).setIsKey(true);
        referencedInfo.addTimestampAttr("RefKeyTimeStampAttr").setIsKey(true);
        referencedInfo.addTextAttr("RefTextAttr", 100).setIsKey(false);

        JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("ClassWithReference");
        classInfo.addLongAttr("LongAttribute", -500, 500).setIsKey(true);
        classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
        classInfo.addBooleanAttr("BooleanAttribute");
        classInfo.addReference("Reference", referencedInfo);

        return classInfo;
    }

    public static JdyClassInfoModel createClassWithDeepReference()
    {
        JdyClassInfoModel deepReferencedInfo = new JdyClassInfoModel().setInternalName("DeepReferencedClass");
        deepReferencedInfo.addLongAttr("DeepRefKeyLongAttr", -500, 500).setIsKey(true);
        deepReferencedInfo.addTextAttr("DeepRefKeyTextAttr", 50).setIsKey(true);
        deepReferencedInfo.addTimestampAttr("DeepRefKeyTimeStampAttr");

        JdyClassInfoModel referencedInfo = new JdyClassInfoModel().setInternalName("ReferencedClass");
        referencedInfo.addReference("DeepReference", deepReferencedInfo).setIsKey(true);
        referencedInfo.addLongAttr("RefKeyLongAttr", -500, 500).setIsKey(true);
        referencedInfo.addTextAttr("RefKeyTextAttr", 50).setIsKey(true);
        referencedInfo.addDecimalAttr("RefKeyDecimalAttr", new BigDecimal(100000000.000), new BigDecimal(100000000.000), 3).setIsKey(true);
        referencedInfo.addTimestampAttr("RefKeyTimeStampAttr").setIsKey(true);
        referencedInfo.addTextAttr("RefTextAttr", 100).setIsKey(false);

        JdyClassInfoModel classInfo = new JdyClassInfoModel().setInternalName("ClassWithReference");
        classInfo.addLongAttr("LongAttribute", 0, 500).setIsKey(true);
        classInfo.addTextAttr("TextAttribute", 50).setNotNull(true);
        classInfo.addBooleanAttr("BooleanAttribute");
        classInfo.addReference("Reference", referencedInfo);

        return classInfo;
    }

    public static ClassRepository createRepository()
    {
        JdyRepositoryModel repository = new JdyRepositoryModel("MetaData");
        repository.addListener(new DefaultClassRepositoryValidator());
        repository.addClassInfo(createSmallClassInfo());
        repository.addClassInfo(createMediumClassInfo());
        repository.addClassInfo(createLongClassInfo());

        return repository;
    }

}
