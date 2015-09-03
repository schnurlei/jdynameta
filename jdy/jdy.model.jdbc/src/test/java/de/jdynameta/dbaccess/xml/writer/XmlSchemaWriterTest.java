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
package de.jdynameta.dbaccess.xml.writer;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;

public class XmlSchemaWriterTest extends TestCase
{

    public void testTableMappingReferencedObject() throws JdyPersistentException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException
    {
        Document doc = WriteXMLFile.createSchema(SimpleMetaInfoRepository.getSingleton());

        WriteXMLFile.writeDocIntoFile(doc, new File("C:\\tmp\\file.xml"));

        System.out.println("File saved!");
    }

}
