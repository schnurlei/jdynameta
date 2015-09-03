/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.print.xslfo;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.defaultimpl.HashedValueObject;
import de.jdynameta.testcommon.testdata.TestDataCreator;
import de.jdynameta.testcommon.util.UtilFileLoading;

public class TestFopDirectOutput 
{
	private File tempDir;
	private UtilFileLoading fileUtil;
	
	@Before
	public void setup() throws Exception
	{
		String testPath = UtilFileLoading.getFile("", TestFopDirectOutput.class.getSimpleName() + ".class", TestFopDirectOutput.class).getParent();
		tempDir = new File(new File(testPath),"testTemp");
		tempDir.mkdirs();
		fileUtil = new UtilFileLoading(tempDir);
	}
	
	@Test
	public void testFopDirectOutputForm() throws Exception 
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
		
		FopDirectOutput output = new FopDirectOutputTable();
		File fopInputFileFile = new File(tempDir, "TestFop.xml");
		output.createOutputDocument(fopInputFileFile, testData, aClassInfo);

		File resultPdf = new File(tempDir, "TestFormFop.pdf");
		new FopConverter().convertXslFoToPdf(resultPdf, new FileInputStream(fopInputFileFile)); 

		// show pdf in windows
//		Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "\"DummyTitle\"", resultPdf.getAbsolutePath()});
		Thread.sleep(1500);
	}

	
	@Test 
	public void testEscapeAndHyphenateString()
	{
		Assert.assertEquals("123456&#8203;7890", FopDirectOutput.escapeAndHyphenateString("1234567890"));
		Assert.assertEquals("!&quot;&#167;$%&amp;&#8203;/()=?&#8203;`&lt;&gt;+*&#8203;#&apos;-_:&#8203;.;,", FopDirectOutput.escapeAndHyphenateString("!\"??$%&/()=?`<>+*#'-_:.;,"));

		Assert.assertEquals("&#12473;&#12479;&#12540;&#12488;&#24460;", FopDirectOutput.escapeAndHyphenateString("???????????????")); // Japanese
		Assert.assertEquals("&#21855;&#21205;&#24460;", FopDirectOutput.escapeAndHyphenateString("?????????")); // Chinese
		
		

		
	}
	
	
	
	@After
	public void teardown() throws Exception
	{
		UtilFileLoading.deleteDir(tempDir);
	}
	
}
