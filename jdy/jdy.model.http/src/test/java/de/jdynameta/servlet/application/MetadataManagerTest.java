package de.jdynameta.servlet.application;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.QueryCreator;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.jdy.model.json.JsonFileWriter;
import de.jdynameta.json.client.StartRestServer;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.testcommon.util.UtilFileLoading;
import java.io.StringWriter;
import javax.xml.transform.TransformerConfigurationException;
import org.junit.BeforeClass;

public class MetadataManagerTest
{
	private static final File databasePath = new File("." + File.separator + ".jDyAppeditor");
	private static final boolean deleteExistingData = false;
	private static final String schemaName = "MetadataManagerTest";
	private MetadataManager manager; 

	@BeforeClass
	public static void initDb() throws Exception
	{
		System.out.println("+++++++++++Context Path " + databasePath.getAbsolutePath());
		File databaseFile = new File(databasePath,"TestDbRsc");
		if(deleteExistingData) {
			UtilFileLoading.deleteDir(databasePath);
		}
		StartRestServer.startDatabaseServer(databaseFile, schemaName);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).addHandler(handler);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).setLevel(Level.ALL);
	}

	@Before
	public void initManager() throws Exception
	{
		
		ComboPooledDataSource cpds = new ComboPooledDataSource(); 
		cpds.setDriverClass( "org.hsqldb.jdbcDriver" );
		cpds.setJdbcUrl("jdbc:hsqldb:hsql://localhost/" + schemaName);
		//loads the jdbc driver cpds.setJdbcUrl( "jdbc:postgresql://localhost/testdb" ); 
		cpds.setUser(HsqlUtil.DEFAULT_USER); 
		cpds.setPassword(HsqlUtil.DEFAULT_PASSWD); 
		// the settings below are optional -- c3p0 can work with defaults 
		cpds.setMinPoolSize(5); 
		cpds.setAcquireIncrement(5); cpds.setMaxPoolSize(50);
		
		
		DataSource datasource = HsqlUtil.createDatasource("jdbc:hsqldb:hsql://localhost/" + schemaName, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		manager = new MetadataManager(cpds);
		manager.setupApplicationManager(deleteExistingData, deleteExistingData);
		
	}

	
	@Test
	public void readPlantShop() throws JdyPersistentException, TransformerConfigurationException 
	{
		ClassRepository plantShop = PlantShopRepository.createPlantShopRepository();
		ClassInfo plantType = plantShop.getClassForName(PlantShopRepository.Type.Plant.name());
		DefaultClassInfoQuery query = QueryCreator.start(plantType).query();
		System.out.println("readPlantShop");
		
		System.out.println(System.currentTimeMillis());
		ObjectList<GenericValueObjectImpl> objects = manager.loadObjectsFromDb(query);
		System.out.println(System.currentTimeMillis());
		JsonFileWriter fileWriter = new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true);
		StringWriter writer = new StringWriter();
		fileWriter.writeObjectList(writer, plantType, objects, PersistentOperation.Operation.READ);
		System.out.println(System.currentTimeMillis());
		

	}
	
	
	@Test
	public void readMetaData() throws JdyPersistentException, TransformerConfigurationException 
	{
		ClassInfo type = ApplicationRepository.getSingleton().getRepositoryModel();
		DefaultClassInfoQuery query = QueryCreator.start(type).query();
		System.out.println("readMetaData");
		
		System.out.println(System.currentTimeMillis());
		ObjectList<GenericValueObjectImpl> objects = manager.loadObjectsFromDb(query);
		System.out.println(System.currentTimeMillis());
		JsonFileWriter fileWriter = new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true);
		StringWriter writer = new StringWriter();
		fileWriter.writeObjectList(writer, type, objects, PersistentOperation.Operation.READ);
		System.out.println(System.currentTimeMillis());
		

	}

	
	@SuppressWarnings("serial")
	private class ValueModelObjectCreator<TCreatedObjFromValueObj> extends AbstractReflectionCreator<TCreatedObjFromValueObj> 
		implements ObjectTransformator<ValueObject, TCreatedObjFromValueObj> {


		public ValueModelObjectCreator(ClassNameCreator aNameCreator)
		{
			super(aNameCreator);
		}

		@Override
		public TypedValueObject getValueObjectFor(ClassInfo aClassinfo,
				ValueObject aObjectToTransform)
		{
			return new TypedWrappedValueObject(aObjectToTransform, aClassinfo);
		}

		@Override
		protected TCreatedObjFromValueObj createProxyObjectFor(
				TypedValueObject aObjToHandle)
		{
			return null;
		}


		@Override
		protected void setProxyListForAssoc(AssociationInfo aCurAssocInfo,
				TCreatedObjFromValueObj aObjoSetVals,
				TypedValueObject aObjToGetVals) throws ObjectCreationException
		{
			
		}
	}
		
}
