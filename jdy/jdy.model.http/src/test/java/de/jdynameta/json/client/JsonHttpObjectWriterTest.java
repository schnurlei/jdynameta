package de.jdynameta.json.client;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.json.StartRestServer;
import de.jdynameta.json.persistence.JsonConnection;
import de.jdynameta.metamodel.application.AppClassInfo;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.servlet.JDynametaRestServlet;
import de.jdynameta.testcommon.util.UtilFileLoading;

public class JsonHttpObjectWriterTest 
{
	private Server server;
	private String host = "localhost";
	private int port = 9081;
	private String basePath = "/";
	
	@Before
	public void startServer() throws Exception
	{
		File databasePath = new File("." + File.separator + ".jDyAppeditor");
		System.out.println("+++++++++++Context Path " + databasePath.getAbsolutePath());
		File databaseFile = new File(databasePath,"TestDbRsc");
		UtilFileLoading.deleteDir(databasePath);
		
		new StartRestServer().startDatabase(databaseFile);
		server = new StartRestServer().startServer(new JDynametaRestServlet(), port);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).addHandler(handler);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).setLevel(Level.ALL);
	}
	
	public void stopServer() throws Exception
	{
		server.stop();
	}
	
	
	
	@Test
	public void writeData()
	{
		try
		{

			ApplicationRepository rep = ApplicationRepository.getSingleton();
			AppRepository newRepository = new AppRepository();
			newRepository.setName("Plants sfs");
			newRepository.setApplicationName("Plants");
			newRepository.setAppVersion(1L);

			
//			NameSpaceInfoModelImpl newNameSpace = new NameSpaceInfoModelImpl();
//			newNameSpace.setNameResource("TestNameSpace1"+ System.currentTimeMillis());
//			ClassInfoModelImpl newClassInfo = new ClassInfoModelImpl();
//			newClassInfo.setExternalName("TestRscEx"+ System.currentTimeMillis());
//			newClassInfo.setInternalName("TestRscItoday"+ System.currentTimeMillis());
//			newClassInfo.setNameSpace(newNameSpace);
//			newClassInfo.setShortName("TRSH");
//			newClassInfo.setIsAbstract(false);
// new JsonClient(host, port, basePath, ApplicationRepository.META_MODEL_NAME_SPACE.getNameResource())
			
			
			ObjectTransformator<GenericValueObjectImpl, GenericValueObjectImpl> transformator = new AppTransformator();
			JsonConnection<GenericValueObjectImpl, GenericValueObjectImpl> connection = new JsonConnection<GenericValueObjectImpl, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);
			JsonHttpObjectWriter client = new JsonHttpObjectWriter(host, port, basePath, ApplicationRepository.META_REPO_NAME);
			TypedValueObject repository = connection.insertObjectInDb(newRepository, newRepository.getClassInfo());

			AppClassInfo classInfo = new AppClassInfo();
			classInfo.setInternalName("Plant");
			classInfo.setName("Plant");
			classInfo.setRepository( (AppRepository) repository);
			classInfo.setIsAbstract(false);
			
			TypedValueObject appClassInfo = client.insertObjectInDb(classInfo, classInfo.getClassInfo());
			
//			DefaultClassInfoQuery query = new DefaultClassInfoQuery(MetaModelRepository.getSingleton().getClassInfoModelInfo());
//			ObjectList<TypedValueObject> readObjects = new JsonClient(host, port, basePath).loadValuesFromDb(query);
//			System.out.println(readObjects);
//			
//			new JsonClient(host, port, basePath).deleteObjectInDb(readObjects.get(0), MetaModelRepository.getSingleton().getClassInfoModelInfo());
		
			System.out.println(appClassInfo);
		} catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JdyPersistentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}	
	

}
