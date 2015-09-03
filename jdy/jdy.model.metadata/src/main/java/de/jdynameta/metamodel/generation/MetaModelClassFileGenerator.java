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
package de.jdynameta.metamodel.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.jdynameta.base.generation.AbstractClassCodeGenerator;
import de.jdynameta.base.generation.ClassInfoCodeGenerator;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultAttributeNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.DefaultInterfaceClassNameCreator;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

/**
 * @author Rainer
 *
 */
public class MetaModelClassFileGenerator
{
    private static final String ROOT_DIR = "src/main/java/";

    /**
     *
     */
    public MetaModelClassFileGenerator()
    {
        super();
    }

    protected void generateInterfaces(ClassRepository aRepository) throws IOException
    {

        for (ClassInfo classInfo : aRepository.getAllClassInfosIter())
        {
            generateInterface(classInfo);
        }
    }

    private void generateInterface(ClassInfo curInfo) throws IOException
    {
        ClassNameCreator nameCreator = new InterfaceNameCreator();
        ArrayList<String> superInterfaceColl = new ArrayList<>();
        if (curInfo.getSuperclass() != null)
        {
            superInterfaceColl.add(nameCreator.getAbsolutClassNameFor(curInfo.getSuperclass()));
        } else
        {
            superInterfaceColl.add("de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface");
        }

        String nameSpace = "de.jdynameta.base.metainfo.";

        superInterfaceColl.add(nameSpace
                + curInfo.getInternalName().substring(0,
                        curInfo.getInternalName().length() - "Model".length()));

        ClassInfoInterfaceGenerator generator
                = new ClassInfoInterfaceGenerator(curInfo, nameCreator, superInterfaceColl);
        writeToFile(generator);
    }

    /**
     * Create for all ClassInfo in the Repository the Files for the Model
     * Classes
     *
     * @param aRepository
     * @throws IOException
     */
    protected void generateModelImpl(ClassRepository aRepository) throws IOException
    {
        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            generateModelClass(curInfo);
        }
    }

    private void generateModelClass(ClassInfo curInfo) throws IOException
    {
        final ClassNameCreator nameCreator = new ModelNameCreator();
        ClassNameCreator interfaceNameCreator = new InterfaceNameCreator();
        String superClassName = (curInfo.getSuperclass() != null)
                ? nameCreator.getAbsolutClassNameFor(curInfo.getSuperclass())
                : "de.jdynameta.base.value.defaultimpl.TypedReflectionValueObject";

        ClassInfoCodeGenerator generator = new ClassInfoCodeGenerator(curInfo, nameCreator, interfaceNameCreator, new String[]
        {
            interfaceNameCreator.getAbsolutClassNameFor(curInfo)
        }, superClassName, false)
        {
            @Override
            protected void createInstanceGetterSetterFor(PrimitiveAttributeInfo aInfo)
            {
                super.createInstanceGetterSetterFor(aInfo);
                if (isBooleanAttribute(aInfo))
                {
                    appendBooleanGetterDeclaration(aInfo);
                }
                if (isIntegerAttribute(aInfo))
                {
                    appendLongGetterDeclaration(aInfo);
                }
            }

            protected void appendBooleanGetterDeclaration(AttributeInfo aInfo)
            {
                // add Method
                this.appendWithTabs(1, "public ", "boolean", " ");
                this.append(aInfo.getInternalName());
                this.append("() \n");
                this.appendLineWithTabs(1, "{");
                this.appendWithTabs(1, "\treturn ", nameCreator.getInstanceVariableName(aInfo) + ".booleanValue()");
                this.append(";\n");
                this.appendLineWithTabs(1, "}");
                this.appendEmptyLine();
            }

            protected void appendLongGetterDeclaration(AttributeInfo aInfo)
            {
                // add Method
                this.appendWithTabs(1, "public ", "long", " ");
                this.append("get" + DefaultAttributeNameCreator.stringWithFirstLetterUppercase(aInfo.getInternalName()));
                this.append("() \n");
                this.appendLineWithTabs(1, "{");
                this.appendWithTabs(1, "\treturn ", nameCreator.getInstanceVariableName(aInfo) + ".intValue()");
                this.append(";\n");
                this.appendLineWithTabs(1, "}");
                this.appendEmptyLine();
            }

            @Override
            protected void appendConstructorDeclaration()
            {
                // appen default Constructor
                this.appendLineWithTabs(1, "/**");
                this.appendLineWithTabs(1, " *Constructor ");
                this.appendLineWithTabs(1, " */");
                this.appendLineWithTabs(1, "public ", getClassName(), " ()");
                this.appendLineWithTabs(1, "{");
                this.appendLineWithTabs(2, "super();");
                this.appendLineWithTabs(1, "}");
            }

            @Override
            public void appendImportDeclarations()
            {
                super.appendImportDeclarations();
            }
        };

        writeToFile(generator);
    }

    protected void writeToFile(AbstractClassCodeGenerator generator) throws IOException
    {
        String path = generator.getPackageName().replace('.', '/');
        path = ROOT_DIR + path;
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

    @SuppressWarnings("serial")
    public static class ModelNameCreator extends DefaultClassNameCreator
    {
        @Override
        public String getReferenceClassNameFor(ClassInfo aInfo)
        {
            return "de.jdynameta.base.metainfo." + aInfo.getInternalName().substring(0,
                    aInfo.getInternalName().length() - "Model".length());
        }

        
        @Override
        public String getGetterNameFor(AttributeInfo aInfo)
        {
            String result = super.getGetterNameFor(aInfo);
            if (isBooleanAttribute(aInfo) || isIntegerAttribute(aInfo))
            {
                result += "Value";
            }
            return result;
        }
    }

    @SuppressWarnings("serial")
    public static class InterfaceNameCreator extends DefaultInterfaceClassNameCreator
    {
        @Override
        public String getReferenceClassNameFor(ClassInfo aInfo)
        {
            return "de.jdynameta.base.metainfo." + aInfo.getInternalName().substring(0,
                    aInfo.getInternalName().length() - "Model".length());
        }

        @Override
        public String getGetterNameFor(AttributeInfo aInfo)
        {
            String result = super.getGetterNameFor(aInfo);
            if (isBooleanAttribute(aInfo) || isIntegerAttribute(aInfo))
            {
                result += "Value";
            }
            return result;
        }

    }

    private static boolean isBooleanAttribute(AttributeInfo aInfo)
    {
        return aInfo instanceof PrimitiveAttributeInfo
                && ((PrimitiveAttributeInfo) aInfo).getType() instanceof BooleanType;
    }

    private static boolean isIntegerAttribute(AttributeInfo aInfo)
    {
        return aInfo instanceof PrimitiveAttributeInfo
                && ((PrimitiveAttributeInfo) aInfo).getType() instanceof LongType;
    }

    public static void main(String[] args)
    {
        System.out.println("Test start");

        try
        {
            MetaModelClassFileGenerator generator = new MetaModelClassFileGenerator();

//			generator.generateInterface(new MetaModelRepository().getAssociationInfo());
//			generator.generateModelClass(new MetaModelRepository().getAssociationInfo());
            generator.generateInterfaces(new MetaModelRepository());
            generator.generateModelImpl(new MetaModelRepository());
        } catch (IOException | InvalidClassInfoException e)
        {
            e.printStackTrace();
        }

    }

}
