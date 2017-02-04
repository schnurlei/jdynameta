package de.jdynameta.json.client;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.filter.defaultimpl.DefaultClassInfoQuery;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.jdy.net.httpclient.JsonConnection;
import de.jdynameta.metamodel.application.AppClassInfo;
import de.jdynameta.metamodel.application.AppLongType;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.AppTextType;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.servlet.application.MetadataServlet;
import de.jdynameta.testcommon.util.UtilFileLoading;

public class ApplicationManagerServletTest 
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
	public void writeData()
	{
		try
		{
			ObjectTransformator<GenericValueObjectImpl, GenericValueObjectImpl> transformator = new AppTransformator();
			JsonConnection<GenericValueObjectImpl, GenericValueObjectImpl> connection = new JsonConnection<GenericValueObjectImpl, GenericValueObjectImpl>(host, port, basePath, ApplicationRepository.META_REPO_NAME, transformator);

			AppRepository newRepository = new AppRepository();
			newRepository.setName("Plants sfs");
			TypedValueObject repository = connection.insertObjectInDb(newRepository, newRepository.getClassInfo());

			AppClassInfo plantInfo = new AppClassInfo();
			plantInfo.setName("Plant Test");
			plantInfo.setRepository( (AppRepository) repository);
			plantInfo.setIsAbstract(false);
			plantInfo = (AppClassInfo) connection.insertObjectInDb(plantInfo, plantInfo.getClassInfo());

			plantInfo.setBeforeSaveScript("delegate.first = second + double1");
			connection.updateObjectToDb(plantInfo, plantInfo.getClassInfo());

			AppTextType plantNameAttr = new AppTextType();
			plantNameAttr.setName("Botanic Name");
			plantNameAttr.setIsKey(true);
			plantNameAttr.setIsNotNull(true);
			plantNameAttr.setIsGenerated(false);
			plantNameAttr.setLength(100l);
			plantNameAttr.setPos(0l);
			plantNameAttr.setMasterclass(plantInfo);
			connection.insertObjectInDb(plantNameAttr, plantNameAttr.getClassInfo());

			AppLongType heightAttr = new AppLongType();
			heightAttr.setName("Heigth in cm");
			heightAttr.setIsKey(false);
			heightAttr.setIsNotNull(true);
			heightAttr.setIsGenerated(false);
			heightAttr.setMinValue(0l);
			heightAttr.setMaxValue(50000l);
			heightAttr.setPos(1l);
			heightAttr.setMasterclass(plantInfo);
			connection.insertObjectInDb(heightAttr,heightAttr.getClassInfo());
			
			AppClassInfo plantFamilyInfo = new AppClassInfo();
			plantFamilyInfo.setName("Plant Family");
			plantFamilyInfo.setRepository( (AppRepository) repository);
			plantFamilyInfo.setIsAbstract(false);
			plantFamilyInfo = (AppClassInfo) connection.insertObjectInDb(plantFamilyInfo, plantInfo.getClassInfo());

			
			DefaultClassInfoQuery query = new DefaultClassInfoQuery(newRepository.getClassInfo());
			ObjectList<GenericValueObjectImpl> readObjects = connection.loadValuesFromDb(query);
			System.out.println(readObjects);
			
			
			connection.deleteObjectInDb((GenericValueObjectImpl) plantFamilyInfo, plantFamilyInfo.getClassInfo());
		
			System.out.println(plantInfo);
		} catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JdyPersistentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
