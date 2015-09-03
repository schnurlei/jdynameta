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
package de.jdynameta.base.model.metainfo.impl;

import junit.framework.Assert;

import org.junit.Test;

import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyPrimitiveAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
import de.jdynameta.base.metainfo.impl.JdyTextType;

/**
 * Test the validation of ClassInfos when they are added to the
 * DefaultRepository
 */
public class DefaultRepositoryValidationTest
{

    /**
     * Check, that a ValidationExceptionis thrown when the ExternalName of the
     * ClassInfo is missing
     * @throws java.lang.Exception
     */
    @Test
    public void testExternalNameNotExplicitlySet() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");

        Assert.assertEquals("FirstClassInternalName", firstClass.getInternalName());
        Assert.assertEquals(firstClass.getExternalName(), firstClass.getInternalName());
        Assert.assertEquals(firstClass.getShortName(), firstClass.getInternalName());

        firstClass.setExternalName("FirstClassExternalName");
        Assert.assertEquals("FirstClassExternalName", firstClass.getExternalName());
        Assert.assertEquals(firstClass.getShortName(), firstClass.getInternalName());

        firstClass.setShortName("FirstClassShortName");
        Assert.assertEquals("FirstClassInternalName", firstClass.getInternalName());
        Assert.assertEquals("FirstClassExternalName", firstClass.getExternalName());
        Assert.assertEquals("FirstClassShortName", firstClass.getShortName());

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addClassInfo(firstClass);

        JdyClassInfoModel secondClass = testRepository.addClassInfo("SecondClassInternalName").setExternalName("SecondClassExternalName").setShortName("SecondClassShortName");

        Assert.assertEquals("SecondClassInternalName", secondClass.getInternalName());
        Assert.assertEquals("SecondClassExternalName", secondClass.getExternalName());
        Assert.assertEquals("SecondClassShortName", secondClass.getShortName());

    }

    /**
     * Check, that a ValidationExceptionis thrown when the ExternalName of the
     * ClassInfo is missing
     * @throws java.lang.Exception
     */
    @Test
    public void testMissingInternalName() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        //firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());

        try
        {
            testRepository.addClassInfo(firstClass);
            Assert.fail("Internal Name is missing");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check that a ValidationException is thrown when the InternalName of the
     * two different ClassInfos is double of the ClassInfo is missing
     * @throws java.lang.Exception
     */
    @Test
    public void testInternalNameDouble() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        JdyClassInfoModel secondClass = new JdyClassInfoModel();
        secondClass.setInternalName("FirstClassInternalName").setExternalName("SecondClassExternalName").setShortName("SecondClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());

        testRepository.addClassInfo(firstClass);
        try
        {
            testRepository.addClassInfo(secondClass);
            Assert.fail("Internal Name is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check that a ValidationException is thrown when the InternalName of the
     * two different ClassInfos is double of the ClassInfo is missing
     * @throws java.lang.Exception
     */
    @Test
    public void testInternalNameDoubleInsertDynamic() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        JdyClassInfoModel secondClass = new JdyClassInfoModel();
        secondClass.setInternalName("FirstClassInternalName").setExternalName("SecondClassExternalName").setShortName("SecondClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());

        testRepository.addClassInfo(firstClass);
        try
        {
            testRepository.addClassInfo("FirstClassInternalName").setExternalName("SecondClassExternalName").setShortName("SecondClassShortName");
            Assert.fail("Internal Name is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check that a ValidationException is thrown when the ExternalName of the
     * two different ClassInfos is double
     * @throws java.lang.Exception
     */
    @Test
    public void testExternalNameDouble() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        JdyClassInfoModel secondClass = new JdyClassInfoModel();
        secondClass.setInternalName("SecondClassInternalName");
        secondClass.setExternalName("FirstClassExternalName");
        secondClass.setShortName("SecondClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");

        testRepository.addClassInfo(firstClass);
        testRepository.addListener(new DefaultClassRepositoryValidator());
        try
        {
            testRepository.addClassInfo(secondClass);
            Assert.fail("External Name is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check that a ValidationException is thrown when the ExternalName of the
     * two different has changed when the ClassInfo is already inserted in the
     * repository
     * @throws java.lang.Exception
     */
    @Test
    public void testExternalNameDoubleInsertDynamic() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel().setInternalName("FirstClassInternalName")
                .setExternalName("FirstClassExternalName")
                .setShortName("FirstClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());

        testRepository.addClassInfo(firstClass);
        JdyClassInfoModel secondClass = testRepository.addClassInfo("SecondClassInternalName");
        secondClass.setExternalName("SecondClassExternalName");
        try
        {

            secondClass.setExternalName("FirstClassExternalName");
            Assert.fail("External Name is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check that a ValidationException is thrown when the Short of the two
     * different ClassInfos is double
     * @throws java.lang.Exception
     */
    @Test
    public void testShortNameDouble() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());

        testRepository.addClassInfo(firstClass);
        JdyClassInfoModel secondClass = testRepository.addClassInfo("SecondClassInternalName");
        secondClass.setExternalName("SecondClassExternalName");
        secondClass.setShortName("SecondClassShortName");
        try
        {
            secondClass.setShortName("FirstClassShortName");
            Assert.fail("Short Name is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check, that a ValidationExceptionis thrown when the attribute name is
     * double of the ClassInfo is missing
     * @throws java.lang.Exception
     */
    @Test
    public void testDoubleAttributeName() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        firstClass.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(50), "SimpleDataSub1", "SimpleDataSub1Ex", false, true));
        firstClass.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(50), "SimpleDataSub1", "SimpleDataSub1Ex", false, true));

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());

        try
        {
            testRepository.addClassInfo(firstClass);
            Assert.fail("Attribute is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

    /**
     * Check, that a ValidationExceptionis thrown when the attribute of a
     * dynamic added attribute is double of the ClassInfo is missing
     * @throws java.lang.Exception
     */
    @Test
    public void testDoubleAttributeNameDynamic() throws Exception
    {
        JdyClassInfoModel firstClass = new JdyClassInfoModel();
        firstClass.setInternalName("FirstClassInternalName");
        firstClass.setExternalName("FirstClassExternalName");
        firstClass.setShortName("FirstClassShortName");

        JdyRepositoryModel testRepository = new JdyRepositoryModel("MetaData");
        testRepository.addListener(new DefaultClassRepositoryValidator());
        testRepository.addClassInfo(firstClass);
        firstClass.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(50), "SimpleDataSub1", "SimpleDataSub1Ex", false, true));

        try
        {
            firstClass.addAttributeInfo(new JdyPrimitiveAttributeModel(new JdyTextType(50), "SimpleDataSub1", "SimpleDataSub1Ex", false, true));
            Assert.fail("Attribute is double");
        } catch (InvalidClassInfoException ex)
        {

        }
    }

}
