package de.jdynameta.json.client;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.creation.DbAccessConnection;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.metainfo.filter.defaultimpl.QueryCreator;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.json.StartRestServer;
import de.jdynameta.json.persistence.JsonConnection;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.ApplicationRepositoryClassFileGenerator;
import de.jdynameta.metamodel.application.MetaRepositoryCreator;
import de.jdynameta.metamodel.filter.FilterRepository;
import de.jdynameta.servlet.application.MetadataServlet;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;
import de.jdynameta.testcommon.model.subclass.SubclassRepository;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;
import de.jdynameta.testcommon.util.UtilFileLoading;

public class MetaRepositoryCreatorHttpTest
{
	private Server server;
	private String host = "localhost";
	private int port = 9081;
	private String basePath = "/";
	private File databasePath = new File("." + File.separator + ".jDyAppeditor");

	@Before
	public void startServer() throws Exception
	{
		
		System.out.println("+++++++++++Context Path " + databasePath.getAbsolutePath());
		File databaseFile = new File(databasePath,"TestDbRsc");
		UtilFileLoading.deleteDir(databasePath);
		
		new StartRestServer().startDatabase(databaseFile);
		server = new StartRestServer().startServer(new MetadataServlet(), port);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).addHandler(handler);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).setLevel(Level.ALL);
	}
	
	@After
	public void stopServer() throws Exception
	{
		server.stop();
		UtilFileLoading.deleteDir(databasePath);
	}	
	@Test
	public void testSimpleRepository() throws JdyPersistentException
	{
		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		DbAccessConnection<ValueObject, GenericValueObjectImpl> connection = new JsonConnection<ValueObject, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);
		
		MetaRepositoryCreator creator = new MetaRepositoryCreator(connection);
		creator.createAppRepository(SimpleMetaInfoRepository.getSingleton());

		DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
		ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
		System.out.println(readObjects);
		
		AppRepository appRep = (AppRepository) readObjects.get(0);
		ClassRepository metaRep = creator.createMetaRepository((AppRepository) readObjects.get(0));
				
		((JsonConnection)connection).executeWorkflowAction("workflow.closeRepository", appRep, appRep.getClassInfo());
	}    
    
	@Test
	public void testComplexRepository() throws JdyPersistentException
	{
		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		DbAccessConnection<ValueObject, GenericValueObjectImpl> connection = new JsonConnection<ValueObject, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);
		MetaRepositoryCreator creator = new MetaRepositoryCreator(connection);
		creator.createAppRepository(new ComplexTestDataMetaInfoRepository());
		
		DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
		ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
		System.out.println(readObjects);
	}
	
	@Test
	public void testSubclassRepository() throws JdyPersistentException
	{
		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		DbAccessConnection<ValueObject, GenericValueObjectImpl> connection = new JsonConnection<ValueObject, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);
		MetaRepositoryCreator creator = new MetaRepositoryCreator(connection);
		creator.createAppRepository(SubclassRepository.getSingleton());
		
		DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
		ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
		System.out.println(readObjects);
	}
	
	@Test
	public void testPlantShopRepository() throws JdyPersistentException
	{
		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		DbAccessConnection<ValueObject, GenericValueObjectImpl> connection = new JsonConnection<ValueObject, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);
		MetaRepositoryCreator creator = new MetaRepositoryCreator(connection);
		creator.createAppRepository(PlantShopRepository.createPlantShopRepository());
		
		DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
		
		ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
		System.out.println(readObjects);

		DefaultClassInfoQuery attrQuery = QueryCreator.start(ApplicationRepository.getSingleton().getAttributeInfoModelInfo())
					.equal("InternalName", "PrivateAddress")
				.query();
		readObjects = connection.loadValuesFromDb(attrQuery);
	}
	
	@Test
	public void testFilterRepository() throws JdyPersistentException
	{
		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		DbAccessConnection<ValueObject, GenericValueObjectImpl> connection = new JsonConnection<ValueObject, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);
		MetaRepositoryCreator creator = new MetaRepositoryCreator(connection);
		creator.createAppRepository(FilterRepository.getSingleton());
		
		DefaultClassInfoQuery query = new DefaultClassInfoQuery(ApplicationRepository.getSingleton().getRepositoryModel());
		ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
		System.out.println(readObjects);
	}
	

}
