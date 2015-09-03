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
package de.jdynameta.base.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;

/**
 * @author Rainer Schneider
 * @version 13.06.2002
 */
public abstract class AbstractClassCodeGenerator extends CodeGenerationBuffer
{

    public AbstractClassCodeGenerator()
    {
    }

    public String getModelCode()
    {

        clear();
        appendImportDeclarations();
        this.appendEmptyLine();
        appendClassDeclarations();
        this.append("\n{\n");
        appendInstanceVariableDeclaration();
        this.appendEmptyLine();
        appendMethodDeclarations();
        this.append("\n}");

        return this.getStringBuffer().toString();

    }

    public void appendImportDeclarations()
    {
        this.append("package ");
        this.append(getPackageName());
        this.append(";\n");
        this.appendEmptyLine();
    }

    public abstract String getClassName();

    public abstract String getPackageName();

    protected void appendClassDeclarationForAuthor(String authorName, String implementedInterfaces[])
    {
        this.appendClassDeclarationForAuthor(authorName, implementedInterfaces, null, false);
    }

    /**
     * @param authorName
     * @param implementedInterfaces
     * @param extendsClassName
     * @param generateAbstract
     * @see GeneratorForClassCode#appendClassDeclarations(StringBuffer)
     */
    protected void appendClassDeclarationForAuthor(String authorName, String implementedInterfaces[], String extendsClassName, boolean generateAbstract)
    {
        this.append("/**\n");
        this.appendLineWithTabs(0, " * ", getClassName());
        this.append(" *\n");
        this.append(" * @author Copyright &copy;  \n");
        this.appendLineWithTabs(0, " * @author ", authorName);
        this.append(" * @version \n");
        this.append(" */\n");

        String abstractString = (generateAbstract) ? "abstract" : "";
        this.appendWithTabs(0, "public " + abstractString + " class ", getClassName());

        if (extendsClassName != null)
        {
            this.append(" extends " + extendsClassName);
            appendEmptyLine();
        } else
        {
            appendEmptyLine();
        }

        if (implementedInterfaces != null && implementedInterfaces.length > 0)
        {

            boolean isFirstInterface = true;

            for (int i = 0; i < implementedInterfaces.length; i++)
            {
                if (isFirstInterface)
                {
                    this.appendWithTabs(1, "implements ");
                    isFirstInterface = false;
                } else
                {
                    this.append(", ");
                }

                this.append(implementedInterfaces[i]);
            }
            this.appendEmptyLine();
        }
    }

    /**
     * @param aAuthorName
     * @param aInterfacesColl
     * @see GeneratorForClassCode#appendClassDeclarations(StringBuffer)
     */
    protected void appendInterfaceDeclarationForAuthor(String aAuthorName, Collection<String> aInterfacesColl)
    {
        this.append("/**\n");
        this.appendLineWithTabs(0, " * ", getClassName());
        this.append(" *\n");
        this.append(" * @author Copyright &copy;  \n");
        this.appendLineWithTabs(0, " * @author ", aAuthorName);
        this.append(" * @version \n");
        this.append(" */\n");
        this.appendWithTabs(0, "public interface ", getClassName());

        if (aInterfacesColl != null && aInterfacesColl.size() > 0)
        {

            boolean isFirstInterface = true;

            for (Iterator<String> interfaceiter = aInterfacesColl.iterator(); interfaceiter.hasNext();)
            {
                String curInterface = interfaceiter.next();

                if (isFirstInterface)
                {
                    this.appendWithTabs(1, "extends ");
                    isFirstInterface = false;
                } else
                {
                    this.append(", ");
                }

                this.append(curInterface);
            }
            this.appendEmptyLine();
        }
    }

    public abstract void appendClassDeclarations();

    public abstract void appendInstanceVariableDeclaration();

    public abstract void appendMethodDeclarations();

    public static void writeToFile(String rootDir, AbstractClassCodeGenerator generator) throws IOException
    {
        String path = generator.getPackageName().replace('.', '/');
        path = rootDir + path;
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

    public static void generateSourceFiles(ClassRepository aRepository, ClassInfoCodeGenerator generator, Class rootClass, String rootDir) throws IOException
    {

        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            System.out.println(curInfo.getInternalName());
            String superClassName = (curInfo.getSuperclass() != null)
                    ? generator.getClassNameCreator().getAbsolutClassNameFor(curInfo.getSuperclass())
                    : rootClass.getCanonicalName();

            generator.setExtendsClassName(superClassName);
            generator.setInfo(curInfo);
            AbstractClassCodeGenerator.writeToFile(rootDir, generator);
        }
    }

}
