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

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import junit.framework.TestCase;
import de.jdynameta.application.ApplicationManagerImpl.WorkflowApplicationHook;
import de.jdynameta.application.impl.AppPersistentGenericObj;
import de.jdynameta.application.impl.ApplicationManagerGenericObj;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.hsqldb.HSqlSchemaHandler;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.persistence.manager.PersistentObjectManager;
import de.jdynameta.persistence.state.ApplicationObj;

public class AppModelRepositoryTest extends TestCase 
{
//	protected static String dbUrl = "jdbc:mysql:///test";
	
//	protected static String dbUrl ="jdbc:hsqldb:file:/home/rainer/projects/db/testdb";
	
	private static String dbUrl ="jdbc:hsqldb:mem:testMeta";
	private ApplicationManagerGenericObj appMngr;
	private JdbcSchemaHandler  schemaHandler;
	private String schemaName =  "JDynaMeta";
	
	private ApplicationRepository metaRep = ApplicationRepository.getSingleton();
	
	private long attrPositionCounter = 0; 
	
    /**
     * Creates a new Test object.
     * 
     */
   	public AppModelRepositoryTest(String name) 
   	{
        super(name);
    }

    /**
      */
    @Override
	public void setUp() throws Exception 
    {
        super.setUp();

//		DataSource datasource = MysqlUtil.createDatasource("root", "****");
//		schemaHandler = new MsqlSchemaHandler(datasource);
		DataSource datasource = HsqlUtil.createDatasource(dbUrl, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		schemaHandler = new HSqlSchemaHandler(datasource);


        if( schemaHandler.existsSchema(schemaName)){
        	schemaHandler.deleteSchema(schemaName);
        }
		schemaHandler.createSchema(schemaName,ApplicationRepository.getSingleton());
		schemaHandler.validateSchema(schemaName, ApplicationRepository.getSingleton());
		
//		hsqlConnection.createSchema(new ApplicationRepository());
		DummyAppModelPersistenceManager persManager = new DummyAppModelPersistenceManager(HsqlUtil.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(datasource,schemaName));
		WorkflowApplicationHook<ApplicationObj> appHook = new ApplicationHookCreator().createAppHook();	
		appMngr = new ApplicationManagerGenericObj(persManager, appHook);
		DataSource datasource2 = HsqlUtil.createDatasource("jdbc:hsqldb:hsql://localhost/JDynaMeta", HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		JdbcSchemaHandler  hsqlConnection2 	= new HSqlSchemaHandler(datasource2);
		appMngr.setWorkflowManager(new ApplicationWorkflowManager(hsqlConnection2));
		
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).addHandler(handler);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).setLevel(Level.ALL);
		   
    }

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		schemaHandler.deleteSchema(schemaName);
	}
	
	
	public void testCreateRepository() throws Exception
	{
		AppPersistentGenericObj repository = createRepository("Meetings " + DateFormat.getDateTimeInstance().format( new Date()));
	
		AppPersistentGenericObj userClassObj = createClassInfo(repository, "User");
		createIdAttribute(userClassObj, "UserId",Boolean.TRUE );
		createTextAttribute(userClassObj,"Surname");
		createTextAttribute(userClassObj,"First Name");

		AppPersistentGenericObj protokollClassObj = createClassInfo(repository, "Protokoll");
		createIdAttribute(protokollClassObj, "ProtocolId",Boolean.TRUE );
		createTextAttribute(protokollClassObj,"Place");
		createTimeStampAttribute(protokollClassObj,"Date Meeting");
		createObjectReferenceAttribute(protokollClassObj, "Head", userClassObj, Boolean.FALSE);	
		createObjectReferenceAttribute(protokollClassObj, "Secretary", userClassObj, Boolean.FALSE);	

		AppPersistentGenericObj protokollTopicClassObj = createClassInfo(repository, "Protokoll Topic");
		createIdAttribute(protokollTopicClassObj, "TopicId", Boolean.FALSE );
		createTextAttribute(protokollTopicClassObj,"Subject");
	
		AppPersistentGenericObj assocObj =  appMngr.createObject(metaRep.getAssociationInfo(), null);
		AppPersistentGenericObj backRef = createObjectReferenceAttribute(protokollTopicClassObj, "ProtocolRef", protokollClassObj, Boolean.TRUE);	

		assocObj.setValue("Name", "Topics");
		assocObj.setValue("masterClassReference", backRef);
		assocObj.setValue("Masterclass", protokollClassObj);
		appMngr.saveObject(assocObj, null);
				
		String schema = "JDynamicData";
		DataSource datasource = HsqlUtil.createDatasource(dbUrl, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		HSqlSchemaHandler hsqlSchema = new HSqlSchemaHandler(datasource);
		if( hsqlSchema.existsSchema(schema)) {
			hsqlSchema.deleteSchema(schema);
		}

		MetaRepositoryCreator creator = new MetaRepositoryCreator(null);
		ClassRepository metaRep = creator.createMetaRepository((AppRepository) repository.getWrappedValueObject());
		
		hsqlSchema.createSchema(schema, metaRep);
		PersistentObjectManager<ChangeableValueObject, GenericValueObjectImpl> persmanager = new DummyAppModelPersistenceManager(hsqlSchema.<ChangeableValueObject, GenericValueObjectImpl>createBaseConnection(schema));
		ApplicationManagerGenericObj genericAppManager = new ApplicationManagerGenericObj(persmanager);
		
		genericAppManager.loadObjectsFromDb((new DefaultClassInfoQuery(metaRep.getAllClassInfosIter().iterator().next())));
		
		
	}

	private AppPersistentGenericObj createClassInfo(AppPersistentGenericObj repository, String className) throws ObjectCreationException, JdyPersistentException
	{
		AppPersistentGenericObj userClassObj =  appMngr.createObject(metaRep.getClassInfoModelInfo(), null);
		setClassValues(metaRep.getClassInfoModelInfo(), repository, className, userClassObj);
		appMngr.saveObject(userClassObj, null);
		return userClassObj;
	}

	private void createIdAttribute(AppPersistentGenericObj classInfoObj, String attributeName, Boolean isGenerated) throws ObjectCreationException, JdyPersistentException
	{
		AppPersistentGenericObj longAttrInfoObj =  appMngr.createObject(metaRep.getLongTypeModelInfo(), null);
		longAttrInfoObj.setValue("length", new Long(40));
		setAttributeValues(metaRep.getLongTypeModelInfo(), classInfoObj, attributeName, longAttrInfoObj, true, true);
		longAttrInfoObj.setValue("isGenerated", isGenerated);
		appMngr.saveObject(longAttrInfoObj, null);
	}
	
	
	private void createTextAttribute(AppPersistentGenericObj classInfoObj, String attributeName) throws ObjectCreationException, JdyPersistentException
	{
		AppPersistentGenericObj textAttrInfoObj =  appMngr.createObject(metaRep.getTextTypeModelInfo(), null);
		textAttrInfoObj.setValue("length", new Long(40));
		setAttributeValues(metaRep.getTextTypeModelInfo(), classInfoObj, attributeName, textAttrInfoObj, false, true);
		appMngr.saveObject(textAttrInfoObj, null);
	}

	private void createTimeStampAttribute(AppPersistentGenericObj classInfoObj, String attributeName) throws ObjectCreationException, JdyPersistentException
	{
		AppPersistentGenericObj timestampInfoObj =  appMngr.createObject(metaRep.getTimeStampTypeModelInfo(), null);
		timestampInfoObj.setValue("isDatePartUsed", Boolean.TRUE);
		timestampInfoObj.setValue("isTimePartUsed", Boolean.TRUE);
		setAttributeValues(metaRep.getTimeStampTypeModelInfo(), classInfoObj, attributeName, timestampInfoObj, false, false);
		appMngr.saveObject(timestampInfoObj, null);
	}
	
	private AppPersistentGenericObj createObjectReferenceAttribute(AppPersistentGenericObj classInfoObj, String attributeName, AppPersistentGenericObj refClass, Boolean isAssoc) throws ObjectCreationException, JdyPersistentException
	{
		AppPersistentGenericObj objRefInfoObj =  appMngr.createObject(metaRep.getObjectReferenceInfo(), null);
		objRefInfoObj.setValue("referencedClass", refClass);
		objRefInfoObj.setValue("isInAssociation", isAssoc);
		objRefInfoObj.setValue("isDependent", Boolean.TRUE);
		setAttributeValues(metaRep.getTextTypeModelInfo(), classInfoObj, attributeName, objRefInfoObj, false, false);
		appMngr.saveObject(objRefInfoObj, null);
		return objRefInfoObj;
	}
	
	
	private void setAttributeValues(ClassInfo attrInfo, ValueObject masterClass, String name,
			AppPersistentGenericObj attrObj, Boolean isKey, Boolean isNotNull)
	{
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("pos"), new Long(attrPositionCounter++));
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("Name"), name);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isKey"), isKey);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isNotNull"), isKey);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isChangeable"), Boolean.TRUE);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("isGenerated"), Boolean.FALSE);
		attrObj.setValue(attrInfo.getAttributeInfoForExternalName("Masterclass"), masterClass);
	}
	
	
	
	private void setClassValues(ClassInfo aDefaultClassInfo, ValueObject repository, String className,
			AppPersistentGenericObj classInfoObj)	
	{
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("Name"), className);
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("isAbstract"), Boolean.FALSE);
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("name"), className+" internal");
		classInfoObj.setValue(aDefaultClassInfo.getAttributeInfoForExternalName("Repository"),repository);
	}
	

	private AppPersistentGenericObj createRepository(String aRepositoryName) throws ObjectCreationException, JdyPersistentException
	{
		AppPersistentGenericObj repositoryObj =  appMngr.createObject(metaRep.getRepositoryModel(), null);
		repositoryObj.setValue("Name", aRepositoryName);
		appMngr.saveObject(repositoryObj, null);
		return repositoryObj;
	}
	
	

}
