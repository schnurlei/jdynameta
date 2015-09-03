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
package de.jdynameta.print.xslfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.xml.sax.SAXException;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.dbaccess.xml.writer.SaxXmlFileWriter;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FopXsltOutput
{

    public void executeTransformation(File aResultFile, File aSourceXmlFile, InputStream aXlsStyleSheet) throws TransformerException, IOException
    {
//		System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");
        TransformerFactory tFactory = TransformerFactory.newInstance();

        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(aSourceXmlFile);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(aXlsStyleSheet);

        javax.xml.transform.Transformer trans = tFactory.newTransformer(xsltSource);
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(new OutputStreamWriter(new FileOutputStream(aResultFile), "UnicodeBig"));
        trans.transform(xmlSource, result);

    }

    public static void main(String[] args)
    {
        try
        {
            TestDataCreator<HashedValueObject> dataCreator = new TestDataCreator<HashedValueObject>()
            {
                @Override
                protected HashedValueObject createEmptyResult(ClassInfo aClassInfo)
                {
                    return new HashedValueObject();
                }
            };
            ClassInfo aClassInfo = TestDataCreator.createMediumClassInfo();
            ObjectListModel<HashedValueObject> testData = dataCreator.createTestData(aClassInfo, 100, null);

            File valueModelXmlFile = new File("Objects.xml");
//			writeValueModelsAsXml(aClassInfo, testData, valueModelXmlFile);

            System.out.println(valueModelXmlFile.getAbsolutePath());

            File resultFoFile = new File("ObjectsFo.xml");
            resultFoFile.createNewFile();

            new FopXsltOutput().executeTransformation(resultFoFile, valueModelXmlFile, FopXsltOutput.class.getResourceAsStream("jdynameta.xsl"));

            File resultPdf = new File("output.pdf");
            new FopConverter().convertXslFoToPdf(resultPdf, new FileInputStream(resultFoFile));
            Runtime.getRuntime().exec(new String[]
            {
                "cmd", "/c", "start", "\"DummyTitle\"", resultPdf.getAbsolutePath()
            });

        } catch (IOException | FopConverterException | TransformerException ex)
        {
            Logger.getLogger(FopXsltOutput.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    private static void writeValueModelsAsXml(ClassInfo aClassInfo,
            ObjectListModel<HashedValueObject> testData, File valueModelXmlFile)
            throws UnsupportedEncodingException, FileNotFoundException,
            TransformerConfigurationException, JdyPersistentException,
            SAXException
    {
        BufferedWriter fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(valueModelXmlFile), "UnicodeBig"));
        try (PrintWriter objectWriter = new PrintWriter(fileOut))
        {
            SaxXmlFileWriter xmlWriter = new SaxXmlFileWriter();
            xmlWriter.writeObjectList(objectWriter, aClassInfo, testData);
        }
    }

}
