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
package de.jdynameta.json;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.simple.ChiefEmployee;
import de.jdynameta.testcommon.model.simple.Contact;
import de.jdynameta.testcommon.model.simple.Employee;
import de.jdynameta.testcommon.model.simple.Note;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;
import de.jdynameta.testcommon.util.DateCreator;
import de.jdynameta.testcommon.util.UtilFileLoading;


/** 
 *
 */
public class JsonObjectWriterTest  
{
	private ObjectWriter writer;
	
	@Before
	public void setup() throws FileNotFoundException, URISyntaxException
	{
		String testPath = UtilFileLoading.getFile("", JsonObjectWriterTest.class.getSimpleName() + ".class", JsonObjectWriterTest.class).getParent();
		File jsonFile = new File(testPath, "JsonTest");
		UtilFileLoading.deleteDir(jsonFile);
		System.out.println(jsonFile.getAbsolutePath());
		this.writer = new JsonObjectWriter(jsonFile, true);
		
	}
	
    /**
     * Test insert of simple object 
     * @throws Exception test failed
     */
	@Test
    public void testInsertCompany() throws Exception 
    {
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		

		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCompanyId(new Integer(100));		
		testCompany.setCity("Roggenburg");		
		testCompany.setCompanyName("Successfull living Comp.");		
		testCompany.setStreet("I did it my Way");		
		testCompany.setZip("D-89297");		
		
		writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
    }

	/**
	 * Test insert of simple object 
	 * @throws Exception test failed
	 */
	@Test
	public void testInsertSubclassObject() throws Exception 
	{
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		

		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCompanyId(new Integer(300));		
		testCompany.setCompanyName("Money Talk Inc.");		

		ChiefEmployee testObject = new ChiefEmployee();
		testObject.setCompany(testCompany);
		testObject.setEmployeeId(new Integer(100));		
		testObject.setEmployeeName("Hans August");		
		testObject.setBonus(new Integer(200));		
		testObject.setBonusDate(DateCreator.createDate(2003,19, 8));		
		
		writer.insertObjectInDb(testObject, repository.getChiefEmployeeClassInfo());
	}

	/**
	 * Test insert of simple object 
	 * @throws Exception test failed
	 */
	@Test
	public void testInsertCompanyAndContact() throws Exception 
	{

		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		
		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCompanyId(new Integer(100));		
		testCompany.setCompanyName("Wurstfabrik");		
		testCompany.setCity("Roggenburg");		
		
		Contact testContact = new Contact();
		testContact.setContactId(new Integer(20));
		testContact.setContactName("Hans Wurst");
		testContact.setPhone("042342");
		testContact.setCompany(testCompany);
		
		Note testNote = new Note();
		testNote.setCompany(testCompany);
		testNote.setContact(testContact);
		testNote.setNoteValue(new Double(3.4));
		
		Employee testEmployee = new Employee();
		testEmployee.setCompany(testCompany);
		testEmployee.setEmployeeName("Big Balls");
		testEmployee.setEmployeeId(new Integer(22));

		writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
		writer.insertObjectInDb(testContact, repository.getContactClassInfo());
		writer.insertObjectInDb(testNote, repository.getNoteClassInfo());
		writer.insertObjectInDb(testEmployee, repository.getEmployeeClassInfo());
		
		
	}


	/**
	 * Test insert of simple object with null values 
	 * @throws Exception test failed
	 */
	@Test
	public void testInsertNullValues() throws Exception 
	{
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		

		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCompanyId(new Integer(100));		
		testCompany.setCompanyName("Successfull living Comp.");		

		Contact testContact = new Contact();
		testContact.setContactId(new Integer(20));
		testContact.setContactName("Dr. Kama Sutra");
		
		writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
		writer.insertObjectInDb(testContact, repository.getContactClassInfo());
	}

	/**
	 * Test insert of simple object with null key values 
	 * @throws Exception test failed
	 */
	@Test
	public void testInsertValuesInvalidKey() throws Exception 
	{
		SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();		

		CompanyImpl testCompany = new CompanyImpl();
		testCompany.setCity("Roggenburg");		
		testCompany.setCompanyName("Successfull living Comp.");		
		testCompany.setStreet("I did it my Way");		
		testCompany.setZip("D-89297");		
		
		try {
			writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
//			fail("Must throw SqlException because of invalid key");
		} catch (Exception sqlExp) {
			System.out.println(sqlExp);
		}
	}

}

