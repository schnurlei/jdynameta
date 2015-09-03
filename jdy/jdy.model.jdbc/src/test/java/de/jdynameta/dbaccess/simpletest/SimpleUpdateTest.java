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
package de.jdynameta.dbaccess.simpletest;

import java.sql.SQLException;

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.simple.ChiefEmployee;
import de.jdynameta.testcommon.model.simple.Contact;
import de.jdynameta.testcommon.model.simple.Employee;
import de.jdynameta.testcommon.model.simple.Note;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;
import de.jdynameta.testcommon.util.DateCreator;

/**
 *
 */
public class SimpleUpdateTest extends SimpleBaseTestCase
{

    /**
     * Creates a new MetainfoUpdateTest object.
     *
     * @param name DOCUMENT ME!
     */
    public SimpleUpdateTest(String name)
    {
        super(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        SimpleTableCreatorTest creator = new SimpleTableCreatorTest("insert");
        creator.createTables(this.baseConnection.getConnection(), new SimpleMetaInfoRepository());
    }

    /**
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testUpdateValues() throws Exception
    {
        ObjectWriter writer = new JdyJdbcObjectWriter(this.baseConnection, new JDyDefaultRepositoryTableMapping());

        SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();
        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(100);
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");

        Contact testContact = new Contact();
        testContact.setContactId(20);
        testContact.setContactName("Hans Wurst");
        testContact.setPhone("042342");
        testContact.setCompany(testCompany);

        Note testNote = new Note();
        testNote.setCompany(testCompany);
        testNote.setContact(testContact);
        testNote.setNoteValue(3.4);

        Employee testEmployee = new Employee();
        testEmployee.setCompany(testCompany);
        testEmployee.setEmployeeName("Big Balls");
        testEmployee.setEmployeeId(22);

        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        writer.insertObjectInDb(testContact, repository.getContactClassInfo());
        writer.insertObjectInDb(testNote, repository.getNoteClassInfo());
        writer.insertObjectInDb(testEmployee, repository.getEmployeeClassInfo());

        testCompany.setCity(null);
        testCompany.setStreet("Miricalway");
        testCompany.setZip("42");
        writer.updateObjectToDb(testCompany, repository.getCompanyClassInfo());

        testContact.setContactName("Dr. med Wurst");
        testContact.setPhone(null);
        writer.updateObjectToDb(testContact, repository.getContactClassInfo());

        testNote.setNoteValue(4.2);
        writer.updateObjectToDb(testNote, repository.getNoteClassInfo());

        testEmployee.setCompany(null);
        testEmployee.setEmployeeName(null);
        try
        {
            writer.updateObjectToDb(testEmployee, repository.getEmployeeClassInfo());
            fail("EmployeeName must not be null");
        } catch (Exception e)
        {
        }
    }

    /**
     * Delete a Object that is a instance of a SublcassInfo
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testUpdateSubclassObject() throws Exception
    {

        ObjectWriter writer = new JdyJdbcObjectWriter(this.baseConnection, new JDyDefaultRepositoryTableMapping());

        SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();

        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(300);
        testCompany.setCompanyName("Money Talk Inc.");

        ChiefEmployee testObject = new ChiefEmployee();
        testObject.setCompany(testCompany);
        testObject.setEmployeeId(100);
        testObject.setEmployeeName("Hans August");
        testObject.setBonus(200);
        testObject.setBonusDate(DateCreator.createDate(2003, 19, 8));

        writer.insertObjectInDb(testObject, repository.getChiefEmployeeClassInfo());

        testObject.setBonus(500);
        testObject.setBonusDate(DateCreator.createDate(2002, 30, 8));

        writer.updateObjectToDb(testObject, repository.getChiefEmployeeClassInfo());
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args)
    {
        new SimpleUpdateTest("Create Tables").run();
    }

}
