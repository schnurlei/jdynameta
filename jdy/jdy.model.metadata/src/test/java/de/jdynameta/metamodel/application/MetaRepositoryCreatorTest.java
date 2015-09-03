package de.jdynameta.metamodel.application;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import de.jdynameta.base.creation.AbstractReflectionCreator;
import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.creation.db.JdbcSchemaHandler;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.test.PlantShopRepository;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.TypedValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.value.defaultimpl.TypedWrappedValueObject;
import de.jdynameta.dbaccess.jdbc.connection.JdbcConnection;
import de.jdynameta.dbaccess.jdbc.hsqldb.HSqlSchemaHandler;
import de.jdynameta.dbaccess.jdbc.hsqldb.HsqlUtil;
import de.jdynameta.dbaccess.jdbc.writer.JdyJdbcObjectWriter;
import de.jdynameta.metamodel.filter.FilterRepository;
import de.jdynameta.testcommon.model.simple.SimpleMetaInfoRepository;
import de.jdynameta.testcommon.model.subclass.SubclassRepository;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;

public class MetaRepositoryCreatorTest
{
	private static String dbUrl ="jdbc:hsqldb:mem:testMeta";
	private JdbcSchemaHandler  schemaHandler;
	private String schemaName =  "JDynaMeta";
	
	private ApplicationRepository metaRep = ApplicationRepository.getSingleton();
	
	private JdbcConnection<ValueObject, GenericValueObjectImpl> metaCon;
	

    /**
      */
    @Before
	public void setUp() throws Exception 
    {
		DataSource datasource = HsqlUtil.createDatasource(dbUrl, HsqlUtil.DEFAULT_USER, HsqlUtil.DEFAULT_PASSWD);
		this.schemaHandler = new HSqlSchemaHandler(datasource);
        if( schemaHandler.existsSchema(schemaName)){
        	schemaHandler.deleteSchema(schemaName);
        }
		schemaHandler.createSchema(schemaName,metaRep);
		schemaHandler.validateSchema(schemaName, metaRep);
		
		this.metaCon 
			= HsqlUtil.<ValueObject, GenericValueObjectImpl>createBaseConnection(datasource,schemaName);
		this.metaCon.setObjectTransformator(new ValueModelObjectCreator<GenericValueObjectImpl>(new ApplicationRepositoryClassFileGenerator.ModelNameCreator()));
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).addHandler(handler);
		Logger.getLogger(JdyJdbcObjectWriter.class.getName()).setLevel(Level.ALL);
		   
    }
	
	@Test
	public void testSimpleRepository() throws JdyPersistentException
	{
		MetaRepositoryCreator creator = new MetaRepositoryCreator(metaCon);
		AppRepository appRep = creator.createAppRepository(SimpleMetaInfoRepository.getSingleton());
		ClassRepository metaRep = creator.createMetaRepository(appRep);
		try {
			schemaHandler.deleteSchema(metaRep.getRepoName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		schemaHandler.createSchema(metaRep.getRepoName(),metaRep);
				
	}    
    
	@Test
	public void testComplexRepository() throws JdyPersistentException
	{
		MetaRepositoryCreator creator = new MetaRepositoryCreator(metaCon);
		
		AppRepository appRep = creator.createAppRepository(new ComplexTestDataMetaInfoRepository());
		ClassRepository metaRep = creator.createMetaRepository(appRep);
		try {
			schemaHandler.deleteSchema(metaRep.getRepoName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		schemaHandler.createSchema(metaRep.getRepoName(),metaRep);
				
	}
	
	@Test
	public void testSubclassRepository() throws JdyPersistentException
	{
		MetaRepositoryCreator creator = new MetaRepositoryCreator(metaCon);
		
		AppRepository appRep = creator.createAppRepository(SubclassRepository.getSingleton());
		ClassRepository metaRep = creator.createMetaRepository(appRep);
		try {
			schemaHandler.deleteSchema(metaRep.getRepoName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		schemaHandler.createSchema(metaRep.getRepoName(),metaRep);
				
	}
	
	@Test
	public void testPlantShopRepository() throws JdyPersistentException
	{
		MetaRepositoryCreator creator = new MetaRepositoryCreator(metaCon);
		
		AppRepository appRep = creator.createAppRepository(PlantShopRepository.createPlantShopRepository());
		ClassRepository metaRep = creator.createMetaRepository(appRep);
		schemaHandler.createSchema(metaRep.getRepoName(),metaRep);
				
	}
	
	@Test
	public void testFilterRepository() throws JdyPersistentException
	{
		MetaRepositoryCreator creator = new MetaRepositoryCreator(metaCon);
		
		AppRepository appRep = creator.createAppRepository(FilterRepository.getSingleton());
		ClassRepository metaRep = creator.createMetaRepository(appRep);
		schemaHandler.createSchema(metaRep.getRepoName(),metaRep);
				
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
