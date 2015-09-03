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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.SAXException;

public class FopConverter
{

    public void convertXslFoToPdf(File outputPdf, InputStream fopInput) throws FopConverterException
    {
        try
        {
//          System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");

            FopFactory fopFactory = FopFactory.newInstance();

			// Step 2: Set up output stream.
            // Note: Using BufferedOutputStream for performance reasons (helpful
            // with FileOutputStreams).

            OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPdf));

            try
            {
                // Step 3: Construct fop with desired output format

                DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
                Configuration cfg = cfgBuilder.build(FopConverter.class.getResourceAsStream("fop.xconf"));
                fopFactory.setUserConfig(cfg);
                //		fopFactory.setURIResolver(createImageUriResolver(aLogger));

                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

				// Step 5: Setup input and output for XSLT transformation
                // Setup input stream
                Source foFileSource = new StreamSource(fopInput);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

                // Step 6: Start XSLT transformation and FOP processing
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer(); // identity transformer
                transformer.transform(foFileSource, res);
            } catch (SAXException | TransformerException | ConfigurationException | IOException | TransformerFactoryConfigurationError e)
            {
                throw new FopConverterException(e.getLocalizedMessage(), e);
            } 
        } catch (FileNotFoundException e)
        {
                throw new FopConverterException(e.getLocalizedMessage(), e);
        } 

        System.out.println("End Processing");
    }

//	private URIResolver createImageUriResolver(final MessageLogger aLogger)
//	{
//		return new URIResolver()
//		{
//				
//			
//			@Override
//			public Source resolve(String href, String base) throws TransformerException
//			{
//				if( href.startsWith("Symbols/")) {
//					String resourceName = href.substring("Symbols/".length(), href.length());
//					return createImageStream(aLogger, href, base, SYMBOLS_PATH, resourceName);
//				} else if( href.startsWith("./Icons_MDB/")) {
//					String resourceName = href.substring("./Icons_MDB/".length(), href.length());
//					return createImageStream(aLogger, href, base, ICONS_MDB_PATH, resourceName);
//				} else if( href.startsWith("./Icons_WDB/")) {
//					String resourceName = href.substring("./Icons_WDB/".length(), href.length());
//					return createImageStream(aLogger, href, base, Icons_WDB_PATH, resourceName);
//					
//				} else {
//					aLogger.log("Uri Resolver href: " + href + "  base: " + base, Level.FINE);
//					return null;
//				}
//			}
//
//			private Source createImageStream(final MessageLogger aLogger,String href, String base, String imagePath, String resourceName)
//			{
//				File imageDir = new File(iconsBasePath, imagePath);
//				
//				if( imageDir.exists() && imageDir.isDirectory()) {
//					
//					File resourceFile = new File(imageDir, resourceName);
//
//					if( resourceFile.exists() && resourceFile.isFile() ) {
//						return new StreamSource(resourceFile);
//					} else {
//						aLogger.log("Image not found: " + resourceFile.getAbsolutePath(), Level.SEVERE);
//						return null;
//					}
//					
//				} else {
//					aLogger.log("Image Directroy not found " + imageDir.getAbsolutePath(), Level.SEVERE);
//					InputStream resourceUri = FopConverter.class.getResourceAsStream(resourceName);
//					if ( resourceUri != null) {
//						return new StreamSource(resourceUri);
//					} else {
//						return null;
//					}
//				}
//			}
//		};
//	}
    public static void main(String[] args)
    {
        try
        {
            File resultPdf = new File("output.pdf");
            new FopConverter().convertXslFoToPdf(resultPdf, FopConverter.class.getResourceAsStream("FopTestFile.xml"));
            Runtime.getRuntime().exec(new String[]
            {
                "cmd", "/c", "start", "\"DummyTitle\"", resultPdf.getAbsolutePath()
            });

        } catch (FopConverterException | IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
