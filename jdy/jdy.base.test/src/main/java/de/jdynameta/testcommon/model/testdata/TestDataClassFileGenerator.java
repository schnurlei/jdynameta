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
package de.jdynameta.testcommon.model.testdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.jdynameta.base.generation.AbstractClassCodeGenerator;
import de.jdynameta.base.generation.ClassInfoCodeGenerator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;

/**
 * @author Rainer
 *
 */
public class TestDataClassFileGenerator
{
    /**
     *
     */
    public TestDataClassFileGenerator()
    {
        super();
    }

    public void generateModelImpl(ClassRepository aRepository) throws IOException
    {
        final ClassNameCreator nameCreator = new DefaultClassNameCreator();

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            String superClassName = (curInfo.getSuperclass() != null)
                    ? nameCreator.getAbsolutClassNameFor(curInfo.getSuperclass())
                    : "de.jdynameta.base.value.defaultimpl.ReflectionValueObject";

            ClassInfoCodeGenerator generator = new ReflectionClassInfoCreator(curInfo, nameCreator, nameCreator, new String[]
            {
            }, superClassName, false);
            writeToFile(generator);
        }
    }

    protected void writeToFile(AbstractClassCodeGenerator generator) throws IOException
    {
        String path = "src/main/java/" + generator.getPackageName().replace('.', '/');
        File targetPath = new File(path);
        targetPath.mkdirs();

        String code = generator.getModelCode();

        File targetFile = new File(targetPath, generator.getClassName() + ".java");

        System.out.println(targetFile.getAbsolutePath());

        try (FileWriter writer = new FileWriter(targetFile))
        {
            writer.write(code);
        }

    }

    public static class ReflectionClassInfoCreator extends ClassInfoCodeGenerator
    {

        /**
         * @param aInfo
         * @param aClassNameCreator
         * @param aReferencedClassNameCreator
         * @param allImplementedInterfaces
         * @param aExtendedClassName
         * @param aGenerateAbstactClassFlag
         */
        public ReflectionClassInfoCreator(ClassInfo aInfo, ClassNameCreator aClassNameCreator, ClassNameCreator aReferencedClassNameCreator, String[] allImplementedInterfaces, String aExtendedClassName, boolean aGenerateAbstactClassFlag)
        {
            super(aInfo, aClassNameCreator, aReferencedClassNameCreator,
                    allImplementedInterfaces, aExtendedClassName, aGenerateAbstactClassFlag);
        }

        @Override
        protected void appendConstructorDeclaration()
        {
//			if(info.getSuperclass() == null) {
//				// appen default Constructor
//				this.appendLineWithTabs(1,  "/**");
//				this.appendLineWithTabs(1,  " *Constructor ");
//				this.appendLineWithTabs(1,  " */");
//				this.appendLineWithTabs(1,  "public ", getClassName(), " ()" );
//				this.appendLineWithTabs(1,  "{");
//				this.appendLineWithTabs(2,  "super(new de.comafra.model.value.DefaultClassNameCreator());");
//				this.appendLineWithTabs(1,  "}");
//			} else {
//				super.appendConstructorDeclaration();
//			}
        }

    }

    public static void main(String[] args)
    {
        try
        {
            TestDataClassFileGenerator generator = new TestDataClassFileGenerator();
            generator.generateModelImpl(new ComplexTestDataMetaInfoRepository());
        } catch (IOException | InvalidClassInfoException e)
        {
            e.printStackTrace();
        }
    }
}
