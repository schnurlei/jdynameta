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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

public class PropertyFileGenerator
{

    private final PropertyNameCreator propertyNameGenerator;

    /**
     * @param aPropertyNameGenerator
     */
    public PropertyFileGenerator(PropertyNameCreator aPropertyNameGenerator)
    {
        super();
        this.propertyNameGenerator = aPropertyNameGenerator;
    }

    public void generatePropertyFileForRepository(ClassRepository aRepository, String repositoryName, String aFilePath) throws IOException
    {
        StringBuffer propertyFileBuffer = new StringBuffer(100);
        for (ClassInfo curInfo : aRepository.getAllClassInfosIter())
        {
            addPropertiesForClassInfo(curInfo, propertyFileBuffer);
        }
        writeToFile(propertyFileBuffer, aFilePath, repositoryName + ".properties");
    }

    protected void writeToFile(StringBuffer aPropertyFileBuffer, String aFilePath, String aFileName) throws IOException
    {
        File targetPath = new File(aFilePath);
        targetPath.mkdirs();

        String fileContent = aPropertyFileBuffer.toString();

        File targetFile = new File(targetPath, aFileName);

        System.out.println(targetFile.getAbsolutePath());

        try (FileWriter writer = new FileWriter(targetFile))
        {
            writer.write(fileContent);
        }

    }

    public void addPropertiesForClassInfo(ClassInfo aClassInfo, StringBuffer aPropertyFileBuffer)
    {
        try
        {
            aPropertyFileBuffer.append(propertyNameGenerator.getPropertyNameFor(aClassInfo));
            aPropertyFileBuffer.append("=").append(aClassInfo.getInternalName()).append("\n");
            aClassInfo.handleAttributes(createPropertyHandler(aClassInfo, aPropertyFileBuffer), null);
            createAssociationProperties(aClassInfo, aPropertyFileBuffer);
        } catch (JdyPersistentException excp)
        {
            excp.printStackTrace();
        }
    }

    private AttributeHandler createPropertyHandler(final ClassInfo aClassInfo, final StringBuffer aPropertyFileBuffer)
    {
        return new AttributeHandler()
        {
            @Override
            public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object aObjToHandle) throws JdyPersistentException
            {
                aPropertyFileBuffer.append(propertyNameGenerator.getPropertyNameFor(aClassInfo, aInfo));
                aPropertyFileBuffer.append("=").append(aInfo.getInternalName()).append("\n");
            }

            @Override
            public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject aObjToHandle) throws JdyPersistentException
            {
                aPropertyFileBuffer.append(propertyNameGenerator.getPropertyNameFor(aClassInfo, aInfo));
                aPropertyFileBuffer.append("=").append(aInfo.getInternalName()).append("\n");
            }
        };
    }

    private void createAssociationProperties(final ClassInfo aClassInfo, final StringBuffer aPropertyFileBuffer)
    {
        for (AssociationInfo curAssoc : aClassInfo.getAssociationInfoIterator())
        {

            aPropertyFileBuffer.append(propertyNameGenerator.getPropertyNameFor(aClassInfo, curAssoc));
            aPropertyFileBuffer.append("=").append(curAssoc.getNameResource()).append("\n");
        }

    }
}
