/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.json;

import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;

import javax.xml.transform.TransformerConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.jdynameta.base.creation.ObjectTransformator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.value.ChangeableTypedValueObject;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.json.TestRepository.Field;
import de.jdynameta.json.client.JsonHttpObjectReader;
import de.jdynameta.json.client.MetaTransformator;
import de.jdynameta.metamodel.application.AppRepository;
import de.jdynameta.metamodel.application.ApplicationRepository;
import de.jdynameta.metamodel.application.ApplicationRepositoryClassFileGenerator;
import de.jdynameta.metamodel.application.MetaRepositoryCreator;
import de.jdynameta.persistence.state.ApplicationObj;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;

/**
 *
 * @author rainer
 */
public class JsonFileReaderTest
{
	@Test
	public void testReadPrimitives() throws TransformerConfigurationException, SAXException, JdyPersistentException
	{
		ComplexTestDataMetaInfoRepository rep = new ComplexTestDataMetaInfoRepository();
		ClassInfo allPrimitivesType = rep.getAllPrimitiveTypeClassInfo();

		JsonStringBuilderHelper input = new JsonStringBuilderHelper();
		input.listStart();
		input.addString("@namespace", "ComplexTestDataMeta");
		input.addString("@classInternalName", "AllAttributeTypes");
		input.addString("@persistence", "READ");
		input.addValue("IntegerData", "452");
		input.addValue("BooleanData", "true");
		input.addString("ClobData", "Clob Data");
		input.addValue("CurrencyData", "425.67");
		input.addString("DateData", "2012-06-15T14:57:46Z");
		input.addValue("FloatData", "12312.45");
		input.addValue("LongData", "3234");
		input.addString("TextData", "text data");
		input.addString("TimestampData", "2012-06-15T14:57:46Z");
		input.addString("TimeData", "2012-06-15T14:57:46Z");
		input.addLastString("VarCharData", "Var char data");
		input.listEnd();
	
		StringReader reader = new StringReader(input.toString());
		ObjectList<ApplicationObj> readedObjects = new JsonFileReader().readObjectList(reader, allPrimitivesType);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 5, 15, 16, 57, 46);
		calendar.set(Calendar.MILLISECOND, 0);
		ApplicationObj result = readedObjects.get(0);
		Assert.assertEquals(new Long(452), result.getValue("IntegerDataEx"));
		Assert.assertEquals( Boolean.TRUE, result.getValue("BooleanDataEx"));
		Assert.assertEquals(null, result.getValue("BlobDataEx"));
		Assert.assertEquals("Clob Data", result.getValue("ClobDataEx"));
		Assert.assertEquals(new BigDecimal("425.67"), result.getValue("CurrencyDataEx"));
		Assert.assertEquals(new Double(12312.45), result.getValue("FloatDataEx"));
		Assert.assertEquals(new Long(3234), result.getValue("LongDataEx"));
		Assert.assertEquals("text data", result.getValue("TextDataEx"));
		Assert.assertEquals("Var char data", result.getValue("VarCharDataEx"));
		Assert.assertEquals(calendar.getTime(), result.getValue("TimestampDataEx"));
		Assert.assertEquals(calendar.getTime(), result.getValue("DateDataEx"));
	}
	
	
	@Test
	public void readDependentData() throws JdyPersistentException
	{
		JsonStringBuilderHelper input = new JsonStringBuilderHelper();
		input.listStart();
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "MASTER");
		input.addString("@persistence", "READ");
		input.addString("MasterKey", "key1");
		input.addString("MasterValue", "value1");

		input.addObject("NoDeptRef");
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "NotDependentObj");
		input.addString("@persistence", "PROXY");
		input.addLastString("NoDeptKey", "key3");
		input.endObject();

		input.addObject("DeptRef");
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "DependentObj");
		input.addString("@persistence", "READ");
		input.addString("DeptKey", "key2");
		input.addLastString("DeptValue", "value2");
		input.endObject();

		input.addAssoc("AssocRef");
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "AssocDetail");
		input.addString("@persistence", "READ");
		input.addString("AssocDetailKey", "key4");
		input.addString("AssocDetailValue", "value4");

		input.addObject("MASTER");
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "MASTER");
		input.addString("@persistence", "PROXY");
		input.addLastString("MasterKey", "key1");
		input.endObjectLast();
		input.nextAssocObj();
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "AssocDetail");
		input.addString("@persistence", "READ");
		input.addString("AssocDetailKey", "key5");
		input.addString("AssocDetailValue", "value5");
		input.addObject("MASTER");
		input.addString("@namespace", "TestRepository");
		input.addString("@classInternalName", "MASTER");
		input.addString("@persistence", "PROXY");
		input.addLastString("MasterKey", "key1");
		input.endObjectLast();
		input.endAssoc();
		input.listEnd();

		TestRepository repository = new TestRepository();
		ClassInfo masterInfo = repository.getType(TestRepository.Type.MASTER);

		StringReader reader = new StringReader(input.toString());
		ObjectList<ApplicationObj> readedObjects = new JsonFileReader().readObjectList(reader, masterInfo);
		ApplicationObj appObj = readedObjects.get(0);
		
		Assert.assertEquals("key1", appObj.getValue(Field.MasterKey.name()));
		Assert.assertEquals("value1", appObj.getValue(Field.MasterValue.name()));
		
		ApplicationObj depObj = (ApplicationObj) appObj.getValue(Field.DeptRef.name());
		Assert.assertEquals("Wrong key ", "key2" ,depObj.getValue(Field.DeptKey.name()));
		Assert.assertEquals("Wrong value", "value2", depObj.getValue(Field.DeptValue.name()));
		
		ApplicationObj noDepObj = (ApplicationObj) appObj.getValue(Field.NoDeptRef.name());
		Assert.assertEquals("Wrong Proxy key", "key3", noDepObj.getValue(Field.NoDeptKey.name()));
		Assert.assertEquals("Proxy value is not empty", null, noDepObj.getValue(Field.NoDeptValue.name()));

		ObjectList<? extends ChangeableTypedValueObject> objList = (ObjectList<? extends ChangeableTypedValueObject>) appObj.getValues(TestRepository.Field.AssocRef.name());
		
		Assert.assertNotNull("Assoc list is empty", objList);
		ApplicationObj firstAssocObj = (ApplicationObj) objList.get(0);
		ApplicationObj secondAssocObj = (ApplicationObj) objList.get(1);
		Assert.assertEquals("Wrong Assoc 1 key ", "key4" ,firstAssocObj.getValue(Field.AssocDetailKey.name()));
		Assert.assertEquals("Wrong Assoc 1 Value ", "value4", firstAssocObj.getValue(Field.AssocDetailValue.name()));
		Assert.assertEquals("Wrong Assoc 2 key ", "key5" ,secondAssocObj.getValue(Field.AssocDetailKey.name()));
		Assert.assertEquals("Wrong Assoc 2 value", "value5", secondAssocObj.getValue(Field.AssocDetailValue.name()));
	
	}
	
	@Test
	public void readRepositoryData() throws JdyPersistentException, ObjectCreationException
	{
		InputStream resource = JsonFileReaderTest.class.getResourceAsStream("simpleRepo.json");
		ObjectList<ApplicationObj> readedObjects = new JsonFileReader().readObjectList(resource, ApplicationRepository.getSingleton().getRepositoryModel());

		ObjectTransformator<ValueObject, GenericValueObjectImpl> transformator = new MetaTransformator(new ApplicationRepositoryClassFileGenerator.ModelNameCreator());
		ObjectList<GenericValueObjectImpl> convertedList = JsonHttpObjectReader.convertValObjList(readedObjects, transformator);
		
		MetaRepositoryCreator creator = new MetaRepositoryCreator(null);
		ClassRepository metaRep = creator.createMetaRepository((AppRepository) convertedList.get(0));
		
	}
}

