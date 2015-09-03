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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.hsqldb.jdbc.JDBCDataSource;

import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.creation.db.JDyDefaultRepositoryTableMapping;
import de.jdynameta.base.creation.db.SqlTableCreator;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.filter.ObjectFilterExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultExpressionAnd;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultObjectReferenceEqualExpression;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorEqual;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultOperatorExpression;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedReflectionObjectInterface;
import de.jdynameta.dbaccess.jdbc.connection.JDBCTypeHandler;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnection;
import de.jdynameta.dbaccess.jdbc.generation.JdyJdbcTableCreator;
import de.jdynameta.metamodel.metainfo.MetaModelPersistenceObjectManager;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;
import de.jdynameta.metamodel.metainfo.model.AttributeInfoModel;
import de.jdynameta.metamodel.metainfo.model.BlobTypeModel;
import de.jdynameta.metamodel.metainfo.model.BooleanTypeModel;
import de.jdynameta.metamodel.metainfo.model.ClassInfoModel;
import de.jdynameta.metamodel.metainfo.model.CurrencyTypeModel;
import de.jdynameta.metamodel.metainfo.model.impl.AssociationInfoModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.BlobTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.BooleanTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.ClassInfoModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.CurrencyTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.FloatTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.LongTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.ObjectReferenceAttributeInfoModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.TextTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.TimestampTypeModelImpl;
import de.jdynameta.metamodel.metainfo.model.impl.VarCharTypeModelImpl;


/** 
 *
 */
public class MetaInfoObjectCreatorTest extends TestCase 
{
//	protected static String dbUrl = "jdbc:mysql:///test";
	
	private static String dbUrl ="jdbc:hsqldb:mem:testMeata";
	private JdbcConnection<ValueObject, TypedReflectionObjectInterface> baseConnection = null;
	private MetaModelRepository repository;
	private MetaModelPersistenceObjectManager persistenceManager; 

	
    /**
     * Creates a new Test object.
     * 
     * @param name DOCUMENT ME!
     */
   	public MetaInfoObjectCreatorTest(String name) 
   	{
        super(name);
    }

    /**
     * @throws Exception DOCUMENT ME!
     */
    @SuppressWarnings("serial")
	@Override
	public void setUp() throws Exception 
    {
        super.setUp();
        
		ObjectTransformator<ValueObject, TypedReflectionObjectInterface> tranformer = new ObjectTransformator() 
		{
			public Object createObjectFor( TypedValueObject aValueModel)
				throws ObjectCreationException
			{
				return null;
			}
			public TypedReflectionObjectInterface getValueObjectFor(ClassInfo aClassinfo, Object aObjectToTransform)
			{
				return (TypedReflectionObjectInterface) aObjectToTransform;
			}

			public Object createNewObjectFor(ClassInfo aClassinfo)
				throws ObjectCreationException
			{
				return null;
			}
		};
		
		baseConnection = createBaseConnection();

		this.repository = new MetaModelRepository();    

		new MetaModelTableGenerator().createTables( createDatasource(dbUrl).getConnection(), this.repository );
		persistenceManager = new MetaModelPersistenceObjectManager(baseConnection);
		   
    }

	public JdbcConnection<ValueObject, TypedReflectionObjectInterface> createBaseConnection()
	{
		 
		JdbcConnection<ValueObject, TypedReflectionObjectInterface> newBaseConnection = new JdbcConnection<ValueObject, TypedReflectionObjectInterface>(createDatasource(dbUrl))
		{
		};
		
		return newBaseConnection;
	}
    
	private JDBCDataSource createDatasource(final String aDbUrl) {
		JDBCDataSource datasource = new JDBCDataSource();
		 datasource.setDatabase(aDbUrl);
		 datasource.setUser("sa");
		 datasource.setPassword("");
//		 Context ctx = new InitialContext();
//		 ctx.bind("jdbc/dsName", datasource);
		return datasource;
	}
    

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

    /**
     * @throws SQLException DOCUMENT ME!
     */
    public void testCreateClassInfo() throws Exception 
    {
		ClassInfoModelImpl newClassInfo = new ClassInfoModelImpl();
		newClassInfo.setExternalName("TestRscEx");
		newClassInfo.setInternalName("TestRscInt");
		newClassInfo.setShortName("TRSC");
		newClassInfo.setIsAbstract(new Boolean(false));

		this.baseConnection.insertObjectInDb(newClassInfo, this.repository.getClassInfoModelInfo());

		SqlTableCreator tableCreator = new JdyJdbcTableCreator( createDatasource(dbUrl).getConnection(), new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());
		tableCreator.buildTableForClassInfo(newClassInfo);
    }

	/**
	 * @throws SQLException DOCUMENT ME!
	 */
	public void testCreateSubClassInfo() throws Exception 
	{
		ClassInfoModelImpl newClassInfo = new ClassInfoModelImpl();
		
		
		newClassInfo.setExternalName("TestRscSuper");
		newClassInfo.setInternalName("TestRscSuper");
		newClassInfo.setShortName("TRSC");
		newClassInfo.setIsAbstract(new Boolean(false));

		ClassInfoModelImpl newSubClassInfo = new ClassInfoModelImpl();
		
		newSubClassInfo.setExternalName("TestRscSubEx");
		newSubClassInfo.setInternalName("TestRscSub");
		newSubClassInfo.setShortName("TRSCSub");
		newSubClassInfo.setSuperclass(newClassInfo);		
		newSubClassInfo.setIsAbstract(new Boolean(false));
	
		this.baseConnection.insertObjectInDb(newClassInfo, this.repository.getClassInfoModelInfo());
		this.baseConnection.insertObjectInDb(newSubClassInfo, this.repository.getClassInfoModelInfo());
		newSubClassInfo.setShortName("TRSCSubUpdate");
		this.baseConnection.updateObjectToDb(newSubClassInfo, this.repository.getClassInfoModelInfo());

		DefaultClassInfoQuery filter = new DefaultClassInfoQuery(repository.getClassInfoModelInfo());
		DefaultOperatorExpression nameExpr = new DefaultOperatorExpression();
		nameExpr.setAttributeInfo((PrimitiveAttributeInfo)repository.getClassInfoModelInfo()
											.getAttributeInfoForExternalName("Internal"));
		nameExpr.setCompareValue("testify");
		nameExpr.setMyOperator(new DefaultOperatorEqual());
		filter.setFilterExpression(nameExpr);
		
		ObjectList resultList =  this.baseConnection.loadValuesFromDb(filter);
		assertEquals(0, resultList.size());

		nameExpr.setCompareValue("TestRscSuper");
		resultList =  this.baseConnection.loadValuesFromDb(filter);
		assertEquals(1, resultList.size());
		
		ArrayList<ObjectFilterExpression> andColl = new ArrayList<ObjectFilterExpression>();
		andColl.add(nameExpr);
		DefaultExpressionAnd andExpr = new DefaultExpressionAnd(andColl);

		filter.setFilterExpression(andExpr);
		resultList =  this.baseConnection.loadValuesFromDb(filter);
		assertEquals(1, resultList.size());
		
		this.baseConnection.deleteObjectInDb(newSubClassInfo, this.repository.getClassInfoModelInfo());
	}


	public void testCreatePrimitiveAttributes() throws Exception
	{
		MetaModelRepository rep = MetaModelRepository.getSingleton();

		long id = System.currentTimeMillis();
		
		ClassInfoModelImpl newClass = new ClassInfoModelImpl();
    	newClass.setInternalName("SecondClass99");
    	newClass.setExternalName("SecondClassEx99");
    	newClass.setIsAbstract(Boolean.FALSE);
    	newClass.setShortName("ShortSecond99");
   		persistenceManager.insertObject(newClass, rep.getClassInfoModelInfo());
   		
   		ChangeableObjectList<AttributeInfo> attributeList = new ChangeableObjectList<AttributeInfo>();
   		BooleanTypeModelImpl booleanAttr = createBooleanAttribute(newClass, "Boolean");
   		attributeList.addObject(booleanAttr);
   		BlobTypeModelImpl blobAttr = createBlobAttribute(newClass, "TestBlob");
   		attributeList.addObject(blobAttr);
   		CurrencyTypeModelImpl currencyAttr = createCurrencyAttribute(newClass, "TestCurrency");
   		attributeList.addObject(currencyAttr);
   		FloatTypeModelImpl floatAttr = createFloatAttribute(newClass, "TestFloat");
   		attributeList.addObject(floatAttr);
   		LongTypeModelImpl longAttr = createLongAttribute(newClass, "TestLong");
   		attributeList.addObject(longAttr);
   		TextTypeModelImpl textAttr = createTextAttribute(newClass, "TestText");
   		attributeList.addObject(textAttr);
   		TimestampTypeModelImpl timestampAttr = createTimestampAttribute(newClass, "TestTimestamp");
   		attributeList.addObject(timestampAttr);
   		VarCharTypeModelImpl varCharAttr = createVarCharAttribute(newClass, "TestChar");
   		attributeList.addObject(varCharAttr);
   		newClass.setAttributesColl(attributeList);
   		
   		persistenceManager.insertObject(booleanAttr, rep.getBooleanTypeModelInfo());
   		persistenceManager.insertObject(blobAttr, rep.getBlobTypeModelInfo());
   		persistenceManager.insertObject(currencyAttr, rep.getCurrencyTypeModelInfo());
   		persistenceManager.insertObject(floatAttr, rep.getFloatTypeModelInfo());
   		persistenceManager.insertObject(longAttr, rep.getLongTypeModelInfo());
   		persistenceManager.insertObject(textAttr, rep.getTextTypeModelInfo());
   		persistenceManager.insertObject(timestampAttr, rep.getTimeStampTypeModelInfo());
   		persistenceManager.insertObject(varCharAttr, rep.getVarCharTypeModelInfo());

		ObjectList<? extends ValueObject> allBool = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getBooleanTypeModelInfo()));
		assertEquals(1, allBool.size());
		assertEquals(new Long(1), ((BooleanTypeModel)allBool.get(0)).getTempValue());
		ObjectList<? extends ValueObject> allBlob = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getBlobTypeModelInfo()));
		assertEquals(1, allBlob.size());
		assertEquals(new Long(1), ((BlobTypeModel)allBlob.get(0)).getTypeHintIdValue());
		ObjectList<? extends ValueObject> allCurrency = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getCurrencyTypeModelInfo()));
		assertEquals(1, allCurrency.size());
		assertEquals(new Long(3), ((CurrencyTypeModel)allCurrency.get(0)).getScaleValue());
		ObjectList<? extends ValueObject> allFloat = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getFloatTypeModelInfo()));
		assertEquals(1, allFloat.size());
		ObjectList<? extends ValueObject> allLong = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getLongTypeModelInfo()));
		assertEquals(1, allLong.size());
		ObjectList<? extends ValueObject> allText = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getTextTypeModelInfo()));
		assertEquals(1, allText.size());
		ObjectList<? extends ValueObject> allTime = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getTimeStampTypeModelInfo()));
		assertEquals(1, allTime.size());
		ObjectList<? extends ValueObject> allVarchar = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getVarCharTypeModelInfo()));
		assertEquals(1, allVarchar.size());

		// read all subclasses of Promitve Attribue
		DefaultClassInfoQuery query2 = new DefaultClassInfoQuery(rep.getPrimitiveAttributeInfo());
		ObjectList<? extends ValueObject> allPrimitives = persistenceManager.loadObjectsFromDb(query2);

		CurrencyTypeModel currencyObj = (CurrencyTypeModel) getObjectForType(allPrimitives, CurrencyTypeModel.class);
		assertEquals(new Long(3), currencyObj.getScaleValue());
   		
		DefaultClassInfoQuery query = new DefaultClassInfoQuery(rep.getClassInfoModelInfo());
		ObjectList<? extends ValueObject> allClasses = persistenceManager.loadObjectsFromDb(query);
		AttributeInfoModel attr =  (AttributeInfoModel) ((ClassInfoModelImpl) allClasses.get(0)).getAttributesColl().get(0);
		assertEquals(1, allClasses.size());

		
		ClassInfoModel anObj = (ClassInfoModel) allClasses.get(0);
		
		SqlTableCreator tableCreator = new JdyJdbcTableCreator(createDatasource(dbUrl).getConnection(), new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());
		tableCreator.buildTableForClassInfo(anObj);
	
		MetaModelPersistenceObjectManager persistenceManager2 = new MetaModelPersistenceObjectManager(baseConnection);
		ObjectList<TypedReflectionObjectInterface> list =  persistenceManager2.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getAttributeInfoModelInfo()));
		assertEquals(8, list.size());
	}

	
	public void testObjectReference() throws Exception
	{
		MetaModelRepository rep = MetaModelRepository.getSingleton();

		ClassInfoModelImpl referencedClass = createClassInfo("TestClass1",null,rep.getClassInfoModelInfo());
   		ChangeableObjectList<AttributeInfo> attributeListClass1 = new ChangeableObjectList<AttributeInfo>();
   		referencedClass.setAttributesColl(attributeListClass1);
   		VarCharTypeModelImpl varCharAttr = createVarCharAttribute(referencedClass, "TestChar");
   		attributeListClass1.addObject(varCharAttr);
   		persistenceManager.insertObject(varCharAttr, rep.getVarCharTypeModelInfo());

		ClassInfoModelImpl mainClass = createClassInfo("MainClass",null,rep.getClassInfoModelInfo());
   		ChangeableObjectList<AttributeInfo> attributeListmainClass = new ChangeableObjectList<AttributeInfo>();
   		mainClass.setAttributesColl(attributeListmainClass);
   		VarCharTypeModelImpl varCharAttr2 = createVarCharAttribute(mainClass, "TestChar");
   		attributeListmainClass.addObject(varCharAttr2);
   		persistenceManager.insertObject(varCharAttr2, rep.getVarCharTypeModelInfo());
   		ObjectReferenceAttributeInfoModelImpl objRef = createObjectReference(referencedClass, mainClass, "TestRef");
   		attributeListmainClass.addObject(objRef);
   		persistenceManager.insertObject(objRef, rep.getObjectReferenceInfo());
   		
   		// load varchars
		ObjectList<? extends ValueObject> allVarchar = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getVarCharTypeModelInfo()));
		assertEquals(2, allVarchar.size());

		ObjectList<? extends ValueObject> allAttributes = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getAttributeInfoModelInfo()));
		assertEquals(3, allAttributes.size());

		ObjectList<? extends ValueObject> allClasses = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getClassInfoModelInfo()));
		assertEquals(2, allClasses.size());

		for (ValueObject curClass : allClasses)
		{
			SqlTableCreator tableCreator = new JdyJdbcTableCreator( createDatasource(dbUrl).getConnection(), new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());
			tableCreator.buildTableForClassInfo((ClassInfo) curClass);
		}
		
		MetaModelPersistenceObjectManager persistenceManager2 = new MetaModelPersistenceObjectManager(baseConnection);
		ObjectList<TypedReflectionObjectInterface> list =  persistenceManager2.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getAttributeInfoModelInfo()));
		assertEquals(3, list.size());
	}

	
	public void testSubclassReference() throws Exception
	{
		MetaModelRepository rep = MetaModelRepository.getSingleton();

		ClassInfoModelImpl referencedClass = createClassInfo("Superclass",null,rep.getClassInfoModelInfo());
   		ChangeableObjectList<AttributeInfo> attributeListClass1 = new ChangeableObjectList<AttributeInfo>();
   		referencedClass.setAttributesColl(attributeListClass1);
   		VarCharTypeModelImpl varCharAttr = createVarCharAttribute(referencedClass, "TestCharSuper");
   		attributeListClass1.addObject(varCharAttr);
   		persistenceManager.insertObject(varCharAttr, rep.getVarCharTypeModelInfo());

		ClassInfoModelImpl mainClass = createClassInfo("FirstMainClass",referencedClass,rep.getClassInfoModelInfo());
   		ChangeableObjectList<AttributeInfo> attributeListmainClass = new ChangeableObjectList<AttributeInfo>();
   		mainClass.setAttributesColl(attributeListmainClass);
   		TimestampTypeModelImpl timestampAttr = createTimestampAttribute(mainClass, "TestDate");
   		attributeListmainClass.addObject(timestampAttr);
   		persistenceManager.insertObject(timestampAttr, rep.getTimeStampTypeModelInfo());
   		
   		// load varchars
		ObjectList<? extends ValueObject> allVarchar = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getVarCharTypeModelInfo()));
		assertEquals(1, allVarchar.size());

		ObjectList<? extends ValueObject> allAttributes = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getAttributeInfoModelInfo()));
		assertEquals(2, allAttributes.size());

		ObjectList<? extends ValueObject> allClasses = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getClassInfoModelInfo()));
		assertEquals(2, allClasses.size());

		for (ValueObject curClass : allClasses)
		{
			SqlTableCreator tableCreator = new JdyJdbcTableCreator( createDatasource(dbUrl).getConnection(), new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());
			tableCreator.buildTableForClassInfo((ClassInfo) curClass);
		}
	}
	
	
	public void testAssociation() throws Exception
	{
		MetaModelRepository rep = MetaModelRepository.getSingleton();

		ClassInfoModelImpl referencedClass = createClassInfo("TestClass12",null,rep.getClassInfoModelInfo());
   		ChangeableObjectList<AttributeInfo> attributeListClass1 = new ChangeableObjectList<AttributeInfo>();
   		referencedClass.setAttributesColl(attributeListClass1);
   		VarCharTypeModelImpl varCharAttr = createVarCharAttribute(referencedClass, "TestChar");
   		attributeListClass1.addObject(varCharAttr);
   		persistenceManager.insertObject(varCharAttr, rep.getVarCharTypeModelInfo());

		ClassInfoModelImpl mainClass = createClassInfo("TestMainClass",null,rep.getClassInfoModelInfo());
   		ChangeableObjectList<AttributeInfo> attributeListmainClass = new ChangeableObjectList<AttributeInfo>();
   		mainClass.setAttributesColl(attributeListmainClass);
   		VarCharTypeModelImpl varCharAttr2 = createVarCharAttribute(mainClass, "TestChar");
   		attributeListmainClass.addObject(varCharAttr2);
   		persistenceManager.insertObject(varCharAttr2, rep.getVarCharTypeModelInfo());
   		
   		ObjectReferenceAttributeInfoModelImpl objRef = createObjectReference(referencedClass, mainClass, "TestRef");
   		attributeListmainClass.addObject(objRef);
   		persistenceManager.insertObject(objRef, rep.getObjectReferenceInfo());
   		
   		AssociationInfoModelImpl assoc  = new AssociationInfoModelImpl();
    		assoc.setMasterclass(mainClass);
   		assoc.setMasterClassReference(objRef);
   		assoc.setNameResource("Test Assoc"); 
   		persistenceManager.insertObject(assoc, rep.getAssociationInfo());
   		
   		
   		
   		// load varchars
		ObjectList<? extends ValueObject> allVarchar = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getVarCharTypeModelInfo()));
		assertEquals(2, allVarchar.size());

		ObjectList<? extends ValueObject> allAttributes = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getAttributeInfoModelInfo()));
		assertEquals(3, allAttributes.size());

		ObjectList<? extends ValueObject> allClasses = persistenceManager.loadObjectsFromDb(new DefaultClassInfoQuery(rep.getClassInfoModelInfo()));
		assertEquals(2, allClasses.size());

		for (ValueObject curClass : allClasses)
		{
			SqlTableCreator tableCreator = new JdyJdbcTableCreator( createDatasource(dbUrl).getConnection(), new JDyDefaultRepositoryTableMapping(), new JDBCTypeHandler());
			tableCreator.buildTableForClassInfo((ClassInfo) curClass);
		}
	}
	
	public void testLoadAttributes() throws JdyPersistentException
	{
//		MetaModelRepository metaRepository;
//		PersistentObjectManager<ValueObject, TypedReflectionObjectInterface> metaPersManager;
//
//		
//		HSqlConnectionHolder<ValueObject, TypedReflectionObjectInterface> hsqlConnection;
//		hsqlConnection = new MetaInfoHSqlConnectionHolder("jdbc:hsqldb:hsql://localhost/JDynaMeta",  "JDynaMeta");
//		
//		metaRepository = MetaModelRepository.getSingleton();
//		metaPersManager = hsqlConnection.createPersistentManager();
//		hsqlConnection.existsSchema();
//		
//		ObjectList<TypedReflectionObjectInterface> list =  metaPersManager.loadObjectsFromDb(new DefaultClassInfoQuery(metaRepository.getAttributeInfoModelInfo()));
		
			
		
	}
	
	private ClassInfoModelImpl createClassInfo(String aClassName,ClassInfoModelImpl superClass, ClassInfo aClassInfo)
			throws JdyPersistentException
	{
		ClassInfoModelImpl newClass1 = new ClassInfoModelImpl();
    	newClass1.setInternalName(aClassName);
    	newClass1.setExternalName(aClassName);
    	newClass1.setIsAbstract(Boolean.FALSE);
    	newClass1.setShortName(aClassName);
    	newClass1.setSuperclass(superClass);
   		persistenceManager.insertObject(newClass1, aClassInfo);
		return newClass1;
	}

	private ValueObject getObjectForType(ObjectList<? extends ValueObject> allPrimitives,	Class<CurrencyTypeModel> aClassToSearch)
	{
		for (ValueObject valueObject : allPrimitives)
		{
			if( aClassToSearch.isInstance(valueObject ) ) {
				return valueObject;
			}
		}
		return null;
	}

		
	
	private ObjectReferenceAttributeInfoModelImpl createObjectReference(ClassInfoModelImpl refClass, ClassInfoModelImpl newClass, String name)
	{
		ObjectReferenceAttributeInfoModelImpl primitivAttr = new ObjectReferenceAttributeInfoModelImpl(); 
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.FALSE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setIsDependent(Boolean.FALSE);
   		primitivAttr.setIsMultipleAssociation(Boolean.FALSE);
   		primitivAttr.setReferencedClass(newClass);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	private VarCharTypeModelImpl createVarCharAttribute(ClassInfoModelImpl newClass, String name)
	{
		VarCharTypeModelImpl primitivAttr = new VarCharTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setLength(50l);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setIsClob(Boolean.FALSE);
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	private BlobTypeModelImpl createBlobAttribute(ClassInfoModelImpl newClass, String name)
	{
		BlobTypeModelImpl primitivAttr = new BlobTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.FALSE);
   		primitivAttr.setIsNotNull(Boolean.FALSE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setTypeHintId(new Long(1));
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	private BooleanTypeModelImpl createBooleanAttribute(ClassInfoModelImpl newClass, String name)
	{
		BooleanTypeModelImpl primitivAttr = new BooleanTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setTemp(new Long(1));
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	
	private CurrencyTypeModelImpl createCurrencyAttribute(ClassInfoModelImpl newClass, String name)
	{
		CurrencyTypeModelImpl primitivAttr = new CurrencyTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setMinValue(new BigDecimal(-110000));
   		primitivAttr.setMaxValue(new BigDecimal(110000));
   		primitivAttr.setScale(new Long(3));
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	private FloatTypeModelImpl createFloatAttribute(ClassInfoModelImpl newClass, String name)
	{
		FloatTypeModelImpl primitivAttr = new FloatTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setMaxValue(new Long(110000));
   		primitivAttr.setScale(new Long(3));
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	private LongTypeModelImpl createLongAttribute(ClassInfoModelImpl newClass, String name)
	{
		LongTypeModelImpl primitivAttr = new LongTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setMinValue(new Long(-110000));
   		primitivAttr.setMaxValue(new Long(110000));
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	

	private TextTypeModelImpl createTextAttribute(ClassInfoModelImpl newClass, String name)
	{
		TextTypeModelImpl primitivAttr = new TextTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setLength(50l);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
	private TimestampTypeModelImpl createTimestampAttribute(ClassInfoModelImpl newClass, String name)
	{
		TimestampTypeModelImpl primitivAttr = new TimestampTypeModelImpl(); 
   		primitivAttr.setId(10l);
   		primitivAttr.setExternalName(name);
   		primitivAttr.setInternalName(name);
   		primitivAttr.setIsKey(Boolean.TRUE);
   		primitivAttr.setIsNotNull(Boolean.TRUE);
   		primitivAttr.setIsGenerated(Boolean.FALSE);
   		primitivAttr.setIsChangeable(Boolean.TRUE);
   		primitivAttr.setIsDatePartUsed(Boolean.TRUE);
   		primitivAttr.setIsTimePartUsed(Boolean.TRUE);
   		primitivAttr.setName(name);
   		primitivAttr.setMasterclass(newClass);
		return primitivAttr;
	}
	
   /**
     * DOCUMENT ME!
     * 
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) 
    {
        new MetaInfoObjectCreatorTest("Test Metaino Persistence").run();
    }
}
