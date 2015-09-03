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
import java.util.ArrayList;

import de.jdynameta.base.creation.ObjectCreator;
import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultExpressionAnd;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorExpression;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.reader.JdbcObjectReader;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.testcommon.model.metainfo.impl.CompanyImpl;
import de.jdynameta.testcommon.model.simple.Contact;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;

/**
 *
 */
public class SimpleReaderTest extends SimpleBaseTestCase
{

    /**
     * Creates a new NumbersTest object.
     *
     * @param name DOCUMENT ME!
     */
    public SimpleReaderTest(String name)
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
     * @throws SQLException DOCUMENT ME!
     */
    public void testLoadValuesCompany() throws Exception
    {
        SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();
        ObjectWriter writer = new JdyJdbcObjectWriter(this.baseConnection, new JDyDefaultRepositoryTableMapping());

        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(100);
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");

        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        testCompany.setCompanyId(101);
        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        testCompany.setCompanyId(102);
        testCompany.setCity(null);
        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());

        JdbcObjectReader reader = new JdbcObjectReader(this.baseConnection, new JDyDefaultRepositoryTableMapping());
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(repository.getCompanyClassInfo());
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        DefaultOperatorExpression idExpr = new DefaultOperatorExpression();
        idExpr.setAttributeInfo((PrimitiveAttributeInfo) repository.getCompanyClassInfo().getAttributeInfoForExternalName("CompanyId"));
        idExpr.setCompareValue(101);
        idExpr.setMyOperator(new DefaultOperatorEqual());
        filter.setFilterExpression(idExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        DefaultOperatorExpression nameExpr = new DefaultOperatorExpression();
        nameExpr.setAttributeInfo((PrimitiveAttributeInfo) repository.getCompanyClassInfo().getAttributeInfoForExternalName("CompanyName"));
        nameExpr.setCompareValue("Wurstfabrik");
        nameExpr.setMyOperator(new DefaultOperatorEqual());
        filter.setFilterExpression(nameExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        ArrayList<ObjectFilterExpression> exprList = new ArrayList<>();
        exprList.add(idExpr);
        exprList.add(nameExpr);
        DefaultExpressionAnd andExpr = new DefaultExpressionAnd(exprList);
        filter.setFilterExpression(andExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public void testLoadNullValuesCompany() throws Exception
    {
        SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();
        ObjectWriter writer = new JdyJdbcObjectWriter(this.baseConnection, new JDyDefaultRepositoryTableMapping());

        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(100);
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");

        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        testCompany.setCompanyId(101);
        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());
        testCompany.setCompanyId(102);
        testCompany.setCity(null);
        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());

        JdbcObjectReader reader = new JdbcObjectReader(this.baseConnection, new JDyDefaultRepositoryTableMapping());
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(repository.getCompanyClassInfo());

        DefaultOperatorExpression idExpr = new DefaultOperatorExpression();
        idExpr.setAttributeInfo((PrimitiveAttributeInfo) repository.getCompanyClassInfo().getAttributeInfoForExternalName("Zip"));
        idExpr.setCompareValue(null);
        idExpr.setMyOperator(new DefaultOperatorEqual());
        filter.setFilterExpression(idExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        DefaultOperatorExpression nameExpr = new DefaultOperatorExpression();
        nameExpr.setAttributeInfo((PrimitiveAttributeInfo) repository.getCompanyClassInfo().getAttributeInfoForExternalName("City"));
        nameExpr.setCompareValue(null);
        nameExpr.setMyOperator(new DefaultOperatorEqual());
        filter.setFilterExpression(nameExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        ArrayList<ObjectFilterExpression> exprList = new ArrayList<>();
        exprList.add(idExpr);
        exprList.add(nameExpr);
        DefaultExpressionAnd andExpr = new DefaultExpressionAnd(exprList);
        filter.setFilterExpression(andExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());
    }

    public void testLoadValuesContact() throws Exception
    {
        SimpleMetaInfoRepository repository = new SimpleMetaInfoRepository();
        ObjectWriter writer = new JdyJdbcObjectWriter(this.baseConnection, new JDyDefaultRepositoryTableMapping());

        CompanyImpl testCompany = new CompanyImpl();
        testCompany.setCompanyId(100);
        testCompany.setCompanyName("Wurstfabrik");
        testCompany.setCity("Roggenburg");
        writer.insertObjectInDb(testCompany, repository.getCompanyClassInfo());

        Contact testContact = new Contact();
        testContact.setContactId(20);
        testContact.setContactName("Hans Wurst");
        testContact.setPhone("042342");
        testContact.setCompany(testCompany);
        writer.insertObjectInDb(testContact, repository.getContactClassInfo());
        testContact.setContactId(21);
        testContact.setCompany(null);
        writer.insertObjectInDb(testContact, repository.getContactClassInfo());

        JdbcObjectReader reader = new JdbcObjectReader(this.baseConnection, new JDyDefaultRepositoryTableMapping());
        ValueModelObjectCreator tmpObjCreator = new ValueModelObjectCreator();

        DefaultClassInfoQuery filter = new DefaultClassInfoQuery(repository.getContactClassInfo());
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        DefaultOperatorExpression idExpr = new DefaultOperatorExpression();
        idExpr.setAttributeInfo((PrimitiveAttributeInfo) repository.getContactClassInfo().getAttributeInfoForExternalName("Phone"));
        idExpr.setCompareValue("042342");
        idExpr.setMyOperator(new DefaultOperatorEqual());
        filter.setFilterExpression(idExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());

        DefaultOperatorExpression nameExpr = new DefaultOperatorExpression();
        nameExpr.setAttributeInfo((PrimitiveAttributeInfo) repository.getContactClassInfo().getAttributeInfoForExternalName("ContactName"));
        nameExpr.setCompareValue("Hans Wurst");
        nameExpr.setMyOperator(new DefaultOperatorEqual());
        filter.setFilterExpression(nameExpr);
        reader.loadValuesFromDb(filter, tmpObjCreator).size();

        ArrayList<ObjectFilterExpression> exprList = new ArrayList<>();
        exprList.add(idExpr);
        exprList.add(nameExpr);
        DefaultExpressionAnd andExpr = new DefaultExpressionAnd(exprList);
        filter.setFilterExpression(andExpr);
        System.out.println(reader.loadValuesFromDb(filter, tmpObjCreator).size());
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args)
    {
        new SimpleReaderTest("Create Tables").run();
    }

    /**
     * @author rsc
     */
    @SuppressWarnings("serial")
    private class ValueModelObjectCreator implements ObjectCreator<ValueObject>
    {

        @Override
        public ValueObject createObjectFor(TypedValueObject aValueModel)
        {
            return aValueModel;
        }

        @Override
        public ValueObject createNewObjectFor(ClassInfo aClassinfo)
        {
            return null;
        }
    }
}
