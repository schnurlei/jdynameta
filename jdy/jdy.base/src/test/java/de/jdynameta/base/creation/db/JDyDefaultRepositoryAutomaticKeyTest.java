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
package de.jdynameta.base.creation.db;

import org.junit.Assert;
import org.junit.Test;

import de.jdynameta.base.metainfo.impl.JdyAbstractAttributeModel;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;

/**
 * Test whether the JDyDefaultRepositoryTableMapping identifies a generated key
 * correct
 *
 * @author rs
 *
 */
public class JDyDefaultRepositoryAutomaticKeyTest
{
    @Test
    public void testGeneratedIdAttribute()
    {
        JDyDefaultRepositoryTableMapping tableMapping = new JDyDefaultRepositoryTableMapping();

        JdyClassInfoModel classInfo = new JdyClassInfoModel("TestClass");

        JdyAbstractAttributeModel idAttr = classInfo.addLongAttr("genrateKeyAttrId", 0, 1000).setIsKey(true).setGenerated(true);
        classInfo.addTextAttr("normalAttr", 1000);
        classInfo.addTextAttr("generatedNormalAttr", 1000).setGenerated(true);
        classInfo.addTextAttr("notNullNormalAttr", 1000).setNotNull(true);
        Assert.assertEquals(idAttr, tableMapping.getGeneratedIdAttribute(classInfo));
    }

    /**
     * Key has to be the only key to be automatic
     */
    @Test
    public void testGeneratedIdTwoKeys()
    {
        JDyDefaultRepositoryTableMapping tableMapping = new JDyDefaultRepositoryTableMapping();

        JdyClassInfoModel classInfo = new JdyClassInfoModel("TestClass");
        classInfo.addLongAttr("genrateKeyAttrId", 0, 1000).setIsKey(true).setGenerated(true);
        classInfo.addLongAttr("secondKeyAttr", 0, 1000).setIsKey(true);
        classInfo.addTextAttr("normalAttr", 1000);
        Assert.assertNull("Second Key exits", tableMapping.getGeneratedIdAttribute(classInfo));
    }

    /**
     * Key has to be generated to be automatic
     */
    @Test
    public void testGeneratedIdNotGenerated()
    {
        JDyDefaultRepositoryTableMapping tableMapping = new JDyDefaultRepositoryTableMapping();

        JdyClassInfoModel classInfo = new JdyClassInfoModel("TestClass");
        classInfo.addLongAttr("genrateKeyAttrId", 0, 1000).setIsKey(true);
        classInfo.addTextAttr("normalAttr", 1000);
        Assert.assertNull("Key is not generated", tableMapping.getGeneratedIdAttribute(classInfo));
    }

    /**
     * Key has to be of type long to be automatic
     */
    @Test
    public void testGeneratedIdNotLong()
    {
        JDyDefaultRepositoryTableMapping tableMapping = new JDyDefaultRepositoryTableMapping();

        JdyClassInfoModel classInfo = new JdyClassInfoModel("TestClass");
        classInfo.addTextAttr("genrateKeyAttrId", 1000).setIsKey(true).setGenerated(true);
        classInfo.addTextAttr("normalAttr", 1000);
        Assert.assertNull("Key is not of type long", tableMapping.getGeneratedIdAttribute(classInfo));
    }

    /**
     * Key has to be to end with Id to be automatic
     */
    @Test
    public void testGeneratedIdWrongEnding()
    {
        JDyDefaultRepositoryTableMapping tableMapping = new JDyDefaultRepositoryTableMapping();

        JdyClassInfoModel classInfo = new JdyClassInfoModel("TestClass");
        classInfo.addLongAttr("genrateKeyAttrID", 0, 1000).setIsKey(true).setGenerated(true);
        classInfo.addTextAttr("normalAttr", 1000);
        Assert.assertNull("Key is not of type long", tableMapping.getGeneratedIdAttribute(classInfo));
    }

}
