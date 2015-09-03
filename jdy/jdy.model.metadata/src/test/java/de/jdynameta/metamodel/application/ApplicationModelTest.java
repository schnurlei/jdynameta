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
package de.jdynameta.metamodel.application;


import javax.sql.DataSource;

import junit.framework.TestCase;
import de.jdynameta.application.ApplicationManagerImpl.WorkflowApplicationHook;
import de.jdynameta.application.impl.AppPersistentGenericObj;
import de.jdynameta.application.impl.ApplicationManagerGenericObj;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.QueryCreator;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnection;
import de.jdynameta.dbaccess.jdbc.hsqldb.HSqlSchemaHandler;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.manager.impl.ValueModelPersistenceObjectManager;
import de.jdynameta.persistence.state.ApplicationObj;


public class ApplicationModelTest extends TestCase 
{
//	protected static String dbUrl = "jdbc:mysql:///test";
	
	private static String dbUrl ="jdbc:hsqldb:mem:testMeta";
	private ApplicationManagerGenericObj appMngr;
	private HSqlSchemaHandler  hsqlConnection;
	
    /**
     * Creates a new Test object.
     * 
     * @param name DOCUMENT ME!
     */
   	public ApplicationModelTest(String name) 
   	{
        super(name);
    }

    /**
     * @throws Exception DOCUMENT ME!
     */
    @Override
	public void setUp() throws Exception 
    {
        super.setUp();
		DataSource datasource = HsqlUtil.createDatasource(dbUrl, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		hsqlConnection = new HSqlSchemaHandler(datasource);
		
		if (hsqlConnection.existsSchema("JDynameta")) {

			hsqlConnection.deleteSchema("JDynameta");
		};
		
		hsqlConnection.createSchema("JDynameta", new ApplicationRepository());
		DummyAppModelPersistenceManager persManager 
			= new DummyAppModelPersistenceManager(hsqlConnection.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection("JDynameta"));
		WorkflowApplicationHook<ApplicationObj> appHook = new ApplicationHookCreator().createAppHook();	
		appMngr = new ApplicationManagerGenericObj(persManager, appHook);
		   
    }

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		hsqlConnection.deleteSchema("JDynameta");
	}

 

	public void testInsertClassInfo() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();

		AppPersistentGenericObj repositoryObj =  appMngr.createObject(rep.getRepositoryModel(), null);
		repositoryObj.setValue(rep.getRepositoryModel().getAttributeInfoForExternalName("Name"), "repositoryOne");
		appMngr.saveObject(repositoryObj, null);
	
		String nameSpaceName = "NameSpace" + System.currentTimeMillis();
		
		String className = "JUnitTest" + System.currentTimeMillis();
		AppPersistentGenericObj classInfoObj =  appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(), repositoryObj,className, classInfoObj);
	
		appMngr.saveObject(classInfoObj, null);
		
	}

	public void testInsertClassAttributes() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();

		AppPersistentGenericObj repositoryObj =  appMngr.createObject(rep.getRepositoryModel(), null);
		repositoryObj.setValue(rep.getRepositoryModel().getAttributeInfoForExternalName("Name"), "repositoryOne");
		appMngr.saveObject(repositoryObj, null);

		String className = "JUnitTest" + System.currentTimeMillis();
		AppPersistentGenericObj classInfoObj =  appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(),repositoryObj,  className, classInfoObj);
		appMngr.saveObject(classInfoObj, null);

		AppPersistentGenericObj booleanAttrInfoObj =  appMngr.createObject(rep.getBooleanTypeModelInfo(), null);
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("temp"), new Long(10));
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("pos"), new Long(10));
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("Name"), "name");
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("InternalName"), className+"internal");
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("isKey"), Boolean.FALSE);
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("isNotNull"), Boolean.FALSE);
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("isChangeable"), Boolean.FALSE);
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("isGenerated"), Boolean.FALSE);
		booleanAttrInfoObj.setValue(rep.getBooleanTypeModelInfo().getAttributeInfoForExternalName("Masterclass"), classInfoObj);
		 appMngr.saveObject(booleanAttrInfoObj, null);
	
	}
	
	public void testInternalNameCreation() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();

		AppPersistentGenericObj repositoryObj =  appMngr.createObject(rep.getRepositoryModel(), null);
		repositoryObj.setValue(rep.getRepositoryModel().getAttributeInfoForExternalName("Name"), "repository#One+");
		appMngr.saveObject(repositoryObj, null);
		assertEquals("repository_One_", repositoryObj.getValue(rep.getRepositoryModel().getAttributeInfoForExternalName("applicationName")) );

		String className = "_JName<Cla-ss>Test|";
		AppPersistentGenericObj classInfoObj =  appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(),repositoryObj, className, classInfoObj);
		appMngr.saveObject(classInfoObj, null);
		assertEquals("_JName_Cla_ss_Test_", classInfoObj.getValue(rep.getClassInfoModelInfo().getAttributeInfoForExternalName("Internal")) );

		String charAttrName = "Te;st,C:ha.r-";
		AppPersistentGenericObj varcharAttrInfoObj =  appMngr.createObject(rep.getVarCharTypeModelInfo(), null);
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("length"), new Long(10));
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("isClob"), Boolean.FALSE);
		setAttributeValues(rep.getVarCharTypeModelInfo(), classInfoObj, charAttrName, varcharAttrInfoObj, true);
		appMngr.saveObject(varcharAttrInfoObj, null);
		assertEquals("Te_st_C_ha_r_", varcharAttrInfoObj.getValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("InternalName")) );
	
	}	
	
	public void testInsertAndReadSubclassAttributes() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();
		String nameSpaceName = "NameSpace" + System.currentTimeMillis();
		
		AppPersistentGenericObj repositoryObj =  appMngr.createObject(rep.getRepositoryModel(), null);
		repositoryObj.setValue(rep.getRepositoryModel().getAttributeInfoForExternalName("Name"), "repositoryOne");
		appMngr.saveObject(repositoryObj, null);

		String className = "JUnitTest" + System.currentTimeMillis();
		AppPersistentGenericObj classInfoObj =  appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(),repositoryObj, className, classInfoObj);
		appMngr.saveObject(classInfoObj, null);

		String charAttrName = "Junit" + + System.currentTimeMillis();
		AppPersistentGenericObj varcharAttrInfoObj =  appMngr.createObject(rep.getVarCharTypeModelInfo(), null);
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("length"), new Long(10));
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("isClob"), Boolean.FALSE);
		setAttributeValues(rep.getVarCharTypeModelInfo(), classInfoObj, charAttrName, varcharAttrInfoObj, true);
		appMngr.saveObject(varcharAttrInfoObj, null);
	
		String longAttr = "Long" + + System.currentTimeMillis();
		AppPersistentGenericObj longAttrInfoObj =  appMngr.createObject(rep.getLongTypeModelInfo(), null);
		longAttrInfoObj.setValue(rep.getLongTypeModelInfo().getAttributeInfoForExternalName("MaxValue"), new Long(10));
		setAttributeValues(rep.getLongTypeModelInfo(), classInfoObj, longAttr, longAttrInfoObj, false);
		appMngr.saveObject(longAttrInfoObj, null);
		
		
		assertEquals(2, appMngr.loadObjectsFromDb(QueryCreator.readAll(rep.getAttributeInfoModelInfo())).size());
		assertEquals(2, appMngr.loadObjectsFromDb(QueryCreator.readAll(rep.getPrimitiveAttributeInfo())).size());
		assertEquals(1, appMngr.loadObjectsFromDb(QueryCreator.readAll(rep.getVarCharTypeModelInfo())).size()); 
		assertEquals(0, appMngr.loadObjectsFromDb(QueryCreator.readAll(rep.getObjectReferenceInfo())).size());
		
	}
	
	public void testInsertAssociation() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();
		AppPersistentGenericObj assocObj =  appMngr.createObject(rep.getAssociationInfo(), null);
		assertTrue("Created Object have to be new" ,assocObj.getPersistentState().isNew());
		assertFalse("Created Object must not be dirty" ,assocObj.getPersistentState().isDirty());
		AppPersistentGenericObj objRefObj =  appMngr.createObject(rep.getObjectReferenceInfo(), null);
		
		System.out.println(objRefObj);

	}

	public void testCreateTableFromMetaData() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();
		String nameSpaceName = "NameSpace" + System.currentTimeMillis();
		
		AppPersistentGenericObj repositoryObj =  appMngr.createObject(rep.getRepositoryModel(), null);
		repositoryObj.setValue(rep.getRepositoryModel().getAttributeInfoForExternalName("Name"), "repositoryOne");
		appMngr.saveObject(repositoryObj, null);

		String className = "JUnitTest" + System.currentTimeMillis();
		AppPersistentGenericObj classInfoObj =  appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(),repositoryObj,className, classInfoObj);
		appMngr.saveObject(classInfoObj, null);

		String charAttrName = "Junit" + + System.currentTimeMillis();
		AppPersistentGenericObj varcharAttrInfoObj =  appMngr.createObject(rep.getVarCharTypeModelInfo(), null);
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("length"), new Long(10));
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("isClob"), Boolean.FALSE);
		setAttributeValues(rep.getVarCharTypeModelInfo(), classInfoObj, charAttrName, varcharAttrInfoObj, true);
		appMngr.saveObject(varcharAttrInfoObj, null);

		String longAttr = "Long" + + System.currentTimeMillis();
		AppPersistentGenericObj longAttrInfoObj =  appMngr.createObject(rep.getLongTypeModelInfo(), null);
		longAttrInfoObj.setValue(rep.getLongTypeModelInfo().getAttributeInfoForExternalName("MaxValue"), new Long(10));
		setAttributeValues(rep.getLongTypeModelInfo(), classInfoObj, longAttr, longAttrInfoObj, false);
		appMngr.saveObject(longAttrInfoObj, null);
	
		ObjectList<AppPersistentGenericObj> reps = appMngr.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getRepositoryModel()));
		AppRepository firstClass = (AppRepository) reps.get(0).getWrappedValueObject();
		MetaRepositoryCreator creator = new MetaRepositoryCreator(null);
		ClassRepository metaRep = creator.createMetaRepository(firstClass);

		// create db schema vor the new repository
		DataSource datasource = HsqlUtil.createDatasource(dbUrl, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		HSqlSchemaHandler hsqlConnection2 = new HSqlSchemaHandler(datasource);
		if( hsqlConnection2.existsSchema("JDynamicData")) {
			hsqlConnection2.deleteSchema("JDynamicData");
		}
		hsqlConnection2.createSchema("JDynamicData",metaRep);
		PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persmanager 
			= new DummyAppModelPersistenceManager(hsqlConnection2.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection("JDynamicData"));
		ApplicationManagerGenericObj genericAppManager = new ApplicationManagerGenericObj(persmanager);

		// load objects from the new schema
		genericAppManager.loadObjectsFromDb(new DefaultClassInfoQuery(metaRep.getAllClassInfosIter().iterator().next()));
	
	}

	public void testCreateTableForSublcass() throws Exception
	{
		ApplicationRepository rep = ApplicationRepository.getSingleton();
		
		AppPersistentGenericObj repositoryObj =  appMngr.createObject(rep.getRepositoryModel(), null);
		repositoryObj.setValue(rep.getRepositoryModel().getAttributeInfoForExternalName("Name"), "repositoryOne");
		appMngr.saveObject(repositoryObj, null);

		String className = "JUnitTest" + System.currentTimeMillis();
		AppPersistentGenericObj classInfoObj =  appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(),repositoryObj, className, classInfoObj);
		appMngr.saveObject(classInfoObj, null);

		String charAttrName = "Junit" + + System.currentTimeMillis();
		AppPersistentGenericObj varcharAttrInfoObj =  appMngr.createObject(rep.getVarCharTypeModelInfo(), null);
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("length"), new Long(10));
		varcharAttrInfoObj.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("isClob"), Boolean.FALSE);
		setAttributeValues(rep.getVarCharTypeModelInfo(), classInfoObj, charAttrName, varcharAttrInfoObj, true);
		appMngr.saveObject(varcharAttrInfoObj, null);

		String longAttr = "Long" + + System.currentTimeMillis();
		AppPersistentGenericObj longAttrInfoObj =  appMngr.createObject(rep.getLongTypeModelInfo(), null);
		longAttrInfoObj.setValue(rep.getLongTypeModelInfo().getAttributeInfoForExternalName("MaxValue"), new Long(10));
		setAttributeValues(rep.getLongTypeModelInfo(), classInfoObj, longAttr, longAttrInfoObj, false);
		appMngr.saveObject(longAttrInfoObj, null);
	
		AppPersistentGenericObj subclassInfoObj = appMngr.createObject(rep.getClassInfoModelInfo(), null);
		setClassValues(rep.getClassInfoModelInfo(), repositoryObj, "Subclass", subclassInfoObj);
		subclassInfoObj.setValue(rep.getClassInfoModelInfo().getAttributeInfoForExternalName("Superclass"),classInfoObj );
		appMngr.saveObject(subclassInfoObj, null);

		AppPersistentGenericObj subClassAttr1 =  appMngr.createObject(rep.getVarCharTypeModelInfo(), null);
		subClassAttr1.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("length"), new Long(10));
		subClassAttr1.setValue(rep.getVarCharTypeModelInfo().getAttributeInfoForExternalName("isClob"), Boolean.FALSE);
		setAttributeValues(rep.getVarCharTypeModelInfo(), subclassInfoObj, "subClassAttr1", subClassAttr1, false);
		appMngr.saveObject(subClassAttr1, null);

		AppPersistentGenericObj subClassaAttr2 =  appMngr.createObject(rep.getLongTypeModelInfo(), null);
		subClassaAttr2.setValue(rep.getLongTypeModelInfo().getAttributeInfoForExternalName("MaxValue"), new Long(10));
		setAttributeValues(rep.getLongTypeModelInfo(), subclassInfoObj, "subClassAttr2", subClassaAttr2, false);
		appMngr.saveObject(subClassaAttr2, null);
		
		ObjectList<AppPersistentGenericObj> reps = appMngr.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getRepositoryModel()));
		AppRepository appRep = (AppRepository) reps.get(0).getWrappedValueObject();
		MetaRepositoryCreator creator = new MetaRepositoryCreator(null);
		ClassRepository metaRep = creator.createMetaRepository(appRep);

		String schema = "JDynamicData";
		DataSource datasource = HsqlUtil.createDatasource(dbUrl, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		HSqlSchemaHandler hsqlConnection2 = new HSqlSchemaHandler(datasource);
		try {
			hsqlConnection2.deleteSchema(schema);
		} catch (Exception e) {
			e.printStackTrace();
		}
		hsqlConnection2.createSchema(schema,metaRep);
		PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persmanager 
			= new DummyAppModelPersistenceManager(hsqlConnection2.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(schema));
		ApplicationManagerGenericObj genericAppManager = new ApplicationManagerGenericObj(persmanager);
		
		genericAppManager.loadObjectsFromDb(new DefaultClassInfoQuery(metaRep.getAllClassInfosIter().iterator().next()));
	
	}
	
	private void setClassValues(ClassInfo aDefaultClassInfo, ValueObject repository, String className,
			AppPersistentGenericObj classInfoObj)	
	{
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("Name"), className);
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("isAbstract"), Boolean.FALSE);
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("name"), className+" internal");
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("Repository"),repository);
	}

	private void setAttributeValues(ClassInfo attrInfo, ValueObject masterClass, String name,
			AppPersistentGenericObj attrObj, Boolean isKey)
	{
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("pos"), new Long(10));
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("Name"), name);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isKey"), isKey);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isNotNull"), isKey);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isChangeable"), Boolean.FALSE);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isGenerated"), Boolean.FALSE);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("Masterclass"), masterClass);
	}

	protected PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> createPersistentManager(JdbcConnection<ChangeableValueObject, GenericValueObjectImpl> aBaseConnection)
	{
		return new ValueModelPersistenceObjectManager(aBaseConnection);
	}
		
	
}
