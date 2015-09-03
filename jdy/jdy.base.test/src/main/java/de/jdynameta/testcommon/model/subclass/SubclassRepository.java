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
package de.jdynameta.testcommon.model.subclass;

import java.io.IOException;

import de.jdynameta.base.generation.ClassInfoCodeGenerator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
import de.jdynameta.testcommon.model.testdata.TestDataClassFileGenerator;

public final class SubclassRepository extends JdyRepositoryModel
{
    private static final SubclassRepository singleton = new SubclassRepository();

    /**
     * Class with several primitve type as key
     */
    private final JdyClassInfoModel referencedObject;
    private final JdyClassInfoModel mainClass;
    private final JdyClassInfoModel subclassLevel1;
    private final JdyClassInfoModel firstSubLevel2;
    private final JdyClassInfoModel secondSubLevel2;
    private final JdyClassInfoModel subLevel3;

    public SubclassRepository()
    {
        super("SubclassRepository"); // "de.jdynameta.testcommon.model.subclass"
        this.addListener(new DefaultClassRepositoryValidator());

        this.referencedObject = this.addClassInfo("ReferencedObject").setShortName("RfO");
        this.referencedObject.addLongAttr("refKey1In", 0, Integer.MAX_VALUE).setIsKey(true).setExternalName("refKey1");
        this.referencedObject.addTextAttr("refKey2In", 50).setIsKey(true).setExternalName("refKey2");
        this.referencedObject.addTimestampAttr("refData", true, false);

        this.mainClass = this.addClassInfo("MainClass").setShortName("MCl").setExternalName("mainClass").setAbstract(true);
        this.mainClass.addReference("keyMain1", referencedObject).setIsKey(true);
        this.mainClass.addTextAttr("mainData", 50);
        this.mainClass.addTextAttr("keyMain2", 50).setIsKey(true);

        this.subclassLevel1 = this.addClassInfo("SubclassLevel1", this.getMainClassType()).setShortName("SCL1").setAbstract(true).setExternalName("subclassLevel1");
        this.subclassLevel1.addTextAttr("dataLvl1", 50);

        this.firstSubLevel2 = this.addClassInfo("FirstSubLevel2", this.getSubclassLevel1Type()).setShortName("FSL2").setExternalName("firstSubLevel2");
        this.firstSubLevel2.addTextAttr("dataALvl2", 50);

        this.secondSubLevel2 = this.addClassInfo("SecondSubLevel2", this.getSubclassLevel1Type()).setShortName("SSL2").setExternalName("secondSubLevel2");
        this.secondSubLevel2.addTextAttr("dataBLvl2", 50);

        this.subLevel3 = this.addClassInfo("SubLevel3", this.getSecondSubLevel2Type()).setShortName("SL3").setExternalName("subLevel3");
        this.subLevel3.addTextAttr("dataLevel3", 50);
    }

    /**
     * Meta data for a Class with a several Attributes in key
     *
     * @return
     */
    public ClassInfo getReferencedObjectType()
    {

        return this.referencedObject;
    }

    /**
     * Main Class with reference
     *
     * @return
     */
    public JdyClassInfoModel getMainClassType()
    {
        return this.mainClass;
    }

    /**
     *
     * @return
     */
    public JdyClassInfoModel getSubclassLevel1Type()
    {
        return this.subclassLevel1;
    }

    /**
     * Subclass Level 2 First
     *
     * @return
     */
    public JdyClassInfoModel getFirstSubLevel2Type()
    {
        return this.firstSubLevel2;
    }

    /**
     * Second Subclass on Level 2
     *
     * @return
     */
    public JdyClassInfoModel getSecondSubLevel2Type()
    {

        return this.secondSubLevel2;
    }

    /**
     * Subclass on Level 3
     *
     * @return
     */
    public JdyClassInfoModel getSubLevel3Type()
    {
        return this.subLevel3;
    }

    public static void main(String[] args)
    {
        try
        {
            ClassInfoCodeGenerator generator1 = new ClassInfoCodeGenerator(null, null, null);
            TestDataClassFileGenerator generator = new TestDataClassFileGenerator();
            generator.generateModelImpl(new SubclassRepository());
        } catch (IOException | InvalidClassInfoException e)
        {
            e.printStackTrace();
        }
    }

    public static SubclassRepository getSingleton()
    {
        return singleton;
    }

}
