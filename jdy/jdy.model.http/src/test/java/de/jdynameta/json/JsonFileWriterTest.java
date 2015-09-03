/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.jdynameta.json;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.AssocObjectList;
import de.jdynameta.base.objectlist.ChangeableObjectList;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.persistence.manager.PersistentOperation;
import de.jdynameta.testcommon.model.testdata.ComplexTestDataMetaInfoRepository;

/**
 *
 * @author rainer
 */
public class JsonFileWriterTest
{
	@Test
	public void testWritePrimitives() throws TransformerConfigurationException, SAXException, JdyPersistentException
	{
		ComplexTestDataMetaInfoRepository rep = new ComplexTestDataMetaInfoRepository();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 5, 15, 16, 57, 46);
		
		
		ClassInfo allPrimitivesType = rep.getAllPrimitiveTypeClassInfo();
		GenericValueObjectImpl obj2Write = new GenericValueObjectImpl(allPrimitivesType);
		obj2Write.setValue("IntegerDataEx", new Long(452));
		obj2Write.setValue("BooleanDataEx", Boolean.TRUE);
		obj2Write.setValue("BlobDataEx", null);
		obj2Write.setValue("ClobDataEx", "Clob Data");
		obj2Write.setValue("CurrencyDataEx", new BigDecimal("425.67"));
		obj2Write.setValue("DateDataEx", calendar.getTime());
		obj2Write.setValue("FloatDataEx", new Double(12312.45));
		obj2Write.setValue("LongDataEx", new Long(3234));
		obj2Write.setValue("TextDataEx", "text data");
		obj2Write.setValue("TimestampDataEx", calendar.getTime());
		obj2Write.setValue("TimeDataEx", calendar.getTime());
		obj2Write.setValue("VarCharDataEx", "Var char data");
		
		ChangeableObjectList<GenericValueObjectImpl> objColl = new ChangeableObjectList<GenericValueObjectImpl>();
		objColl.addObject(obj2Write);
		
		StringWriter writerOut = new StringWriter();
		new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true).writeObjectList(writerOut, allPrimitivesType, objColl, PersistentOperation.Operation.READ);
	
		JsonStringBuilderHelper result = new JsonStringBuilderHelper();
		result.listStart();
		result.addString("@namespace", "ComplexTestDataMeta");
		result.addString("@classInternalName", "AllAttributeTypes");
		result.addString("@persistence", "READ");
		result.addValue("IntegerData", "452");
		result.addValue("BooleanData", "true");
		result.addString("ClobData", "Clob Data");
		result.addValue("CurrencyData", "425.67");
		result.addString("DateData", "2012-06-15T14:57:46Z");
		result.addValue("FloatData", "12312.45");
		result.addValue("LongData", "3234");
		result.addString("TextData", "text data");
		result.addString("TimestampData", "2012-06-15T14:57:46Z");
		result.addString("TimeData", "2012-06-15T14:57:46Z");
		result.addLastString("VarCharData", "Var char data");
		result.listEnd();
		
		Assert.assertEquals(result.toString(), writerOut.toString());
	}
	
	/**
	 * @throws SQLException DOCUMENT ME!
	 */
	@Test
	public void testAssocDetailSimpleInfo() throws Exception 
	{
		ClassInfo assocDetailClassInfo = new ComplexTestDataMetaInfoRepository().getAssocDetailSimpleInfo();

		GenericValueObjectImpl  assocDetailSimpl = new GenericValueObjectImpl(assocDetailClassInfo);
		assocDetailSimpl.setValue("SplIntKyEx", new Long(10));
		assocDetailSimpl.setValue("SimpleKeyData1Ex", "Test simple ref");
		assocDetailSimpl.setValue("AscSmplRefEx",createMasterObj( new Long(222), new Long(333), new Long(444)));
		assocDetailSimpl.setValue("AscSmplRef2Ex",createMasterObj( new Long(666), new Long(777), new Long(888)));
		
		ChangeableObjectList<GenericValueObjectImpl> objColl = new ChangeableObjectList<GenericValueObjectImpl>();
		objColl.addObject(assocDetailSimpl);
		
		StringWriter writerOut = new StringWriter();
		new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true).writeObjectList(writerOut, assocDetailClassInfo, objColl, PersistentOperation.Operation.READ);
		
		System.out.println( writerOut.toString());
	}
	
	private GenericValueObjectImpl createMasterObj( Long aCoumpoundKey, Long aSimpleKey, Long aRefKey)  throws Exception 
	{
		ClassInfo assocMasterClassInfo = new ComplexTestDataMetaInfoRepository().getAssocMasterClassInfo();
		ClassInfo referenceInPrimaryKeyClassInfo = new ComplexTestDataMetaInfoRepository().getReferenceInPrimaryKeyClassInfo();
		ClassInfo simpleClassInfo = new ComplexTestDataMetaInfoRepository().getSimpleKeyClassInfo();
		ClassInfo compoundClassInfo = new ComplexTestDataMetaInfoRepository().getCompoundKeyClassInfo();

		GenericValueObjectImpl  compoundObjToInsert = new GenericValueObjectImpl(compoundClassInfo);
		compoundObjToInsert.setValue("CmpIntKyEx",aCoumpoundKey);
		compoundObjToInsert.setValue("CompoundKeyDataEx",new Date(System.currentTimeMillis()));
		compoundObjToInsert.setValue("CmpTxtKyEx","Test coumpoundkey");

		GenericValueObjectImpl  simpleRefObjToInsert = new GenericValueObjectImpl(simpleClassInfo);
		simpleRefObjToInsert.setValue("SplIntKyEx",aSimpleKey);
		simpleRefObjToInsert.setValue("SimpleKeyData1Ex","Test String");
		simpleRefObjToInsert.setValue("SimpleKeyData2Ex",new Date(System.currentTimeMillis()));

		GenericValueObjectImpl refInPrimKeyToInsert = new GenericValueObjectImpl(referenceInPrimaryKeyClassInfo);
		refInPrimKeyToInsert.setValue("CompoundRefKeyEx",compoundObjToInsert);
		refInPrimKeyToInsert.setValue("ReferenceDataEx","Test referece data");
		refInPrimKeyToInsert.setValue("SplIntKyEx",aRefKey);
		refInPrimKeyToInsert.setValue("SimpleRefKeyEx",simpleRefObjToInsert);

		
		GenericValueObjectImpl  assocMasterObj = new GenericValueObjectImpl(assocMasterClassInfo);
		assocMasterObj.setValue("DeepRefEx",refInPrimKeyToInsert);
		assocMasterObj.setValue("BonusDateEx",new Date(System.currentTimeMillis()));
		
		return assocMasterObj;
		
	}	
	
	@Test
	public void writeDependentData() throws TransformerConfigurationException, JdyPersistentException
	{	
		TestRepository repository = new TestRepository();
		GenericValueObjectImpl deptObj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.DependentObj));
		deptObj.setValue(TestRepository.Field.DeptKey.name(), "key2");
		deptObj.setValue(TestRepository.Field.DeptValue.name(), "value2");
		GenericValueObjectImpl noDeptObj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.NotDependentObj));
		noDeptObj.setValue(TestRepository.Field.NoDeptKey.name(), "key3");
		noDeptObj.setValue(TestRepository.Field.NoDeptValue.name(), "value3");

		GenericValueObjectImpl assocDetail1Obj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.AssocDetail));
		assocDetail1Obj.setValue(TestRepository.Field.AssocDetailKey.name(), "key4");
		assocDetail1Obj.setValue(TestRepository.Field.AssocDetailValue.name(), "value4");
		GenericValueObjectImpl assocDetail2Obj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.AssocDetail));
		assocDetail2Obj.setValue(TestRepository.Field.AssocDetailKey.name(), "key5");
		assocDetail2Obj.setValue(TestRepository.Field.AssocDetailValue.name(), "value5");

		
		ClassInfo masterInfo = repository.getType(TestRepository.Type.MASTER);
		AssociationInfo assoc = repository.getType(TestRepository.Type.MASTER).getAssoc(TestRepository.Field.AssocRef.name());

		GenericValueObjectImpl masterObj = new GenericValueObjectImpl(masterInfo);
		masterObj.setValue(TestRepository.Field.MasterKey.name(), "key1");
		masterObj.setValue(TestRepository.Field.MasterValue.name(), "value1");
		masterObj.setValue(TestRepository.Field.DeptRef.name(), deptObj);
		masterObj.setValue(TestRepository.Field.NoDeptRef.name(), noDeptObj);
		
		AssocObjectList<GenericValueObjectImpl> objList = new AssocObjectList<GenericValueObjectImpl>(assoc, masterObj);
		objList.addObject(assocDetail1Obj);
		objList.addObject(assocDetail2Obj);
		
		masterObj.setValues(TestRepository.Field.AssocRef.name(), objList);

		ChangeableObjectList<GenericValueObjectImpl> objColl = new ChangeableObjectList<GenericValueObjectImpl>();
		objColl.addObject(masterObj);
		
		StringWriter writerOut = new StringWriter();
		new JsonFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true).writeObjectList(writerOut, masterInfo, objColl, PersistentOperation.Operation.READ);
		
		JsonStringBuilderHelper result = new JsonStringBuilderHelper();
		result.listStart();
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "MASTER");
		result.addString("@persistence", "READ");
		result.addString("MasterKey", "key1");
		result.addString("MasterValue", "value1");
		result.addObject("NoDeptRef");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "NotDependentObj");
		result.addString("@persistence", "PROXY");
		result.addLastString("NoDeptKey", "key3");
		result.endObject();

		result.addObject("DeptRef");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "DependentObj");
		result.addString("@persistence", "READ");
		result.addString("DeptKey", "key2");
		result.addLastString("DeptValue", "value2");
		result.endObject();

		result.addAssoc("AssocRef");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "AssocDetail");
		result.addString("@persistence", "READ");
		result.addString("AssocDetailKey", "key4");
		result.addString("AssocDetailValue", "value4");

		result.addObject("MASTER");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "MASTER");
		result.addString("@persistence", "PROXY");
		result.addLastString("MasterKey", "key1");
		result.endObjectLast();
		result.nextAssocObj();
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "AssocDetail");
		result.addString("@persistence", "READ");
		result.addString("AssocDetailKey", "key5");
		result.addString("AssocDetailValue", "value5");
		result.addObject("MASTER");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "MASTER");
		result.addString("@persistence", "PROXY");
		result.addLastString("MasterKey", "key1");
		result.endObjectLast();
		result.endAssoc();
		result.listEnd();

		Assert.assertEquals(result.toString(), writerOut.toString());
	}
	
	@Test
	public void writeDependentDataCompact() throws TransformerConfigurationException, JdyPersistentException
	{	
		TestRepository repository = new TestRepository();
		GenericValueObjectImpl deptObj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.DependentObj));
		deptObj.setValue(TestRepository.Field.DeptKey.name(), "key2");
		deptObj.setValue(TestRepository.Field.DeptValue.name(), "value2");
		GenericValueObjectImpl noDeptObj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.NotDependentObj));
		noDeptObj.setValue(TestRepository.Field.NoDeptKey.name(), "key3");
		noDeptObj.setValue(TestRepository.Field.NoDeptValue.name(), "value3");

		GenericValueObjectImpl assocDetail1Obj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.AssocDetail));
		assocDetail1Obj.setValue(TestRepository.Field.AssocDetailKey.name(), "key4");
		assocDetail1Obj.setValue(TestRepository.Field.AssocDetailValue.name(), "value4");
		GenericValueObjectImpl assocDetail2Obj = new GenericValueObjectImpl(repository.getType(TestRepository.Type.AssocDetail));
		assocDetail2Obj.setValue(TestRepository.Field.AssocDetailKey.name(), "key5");
		assocDetail2Obj.setValue(TestRepository.Field.AssocDetailValue.name(), "value5");

		
		ClassInfo masterInfo = repository.getType(TestRepository.Type.MASTER);
		AssociationInfo assoc = repository.getType(TestRepository.Type.MASTER).getAssoc(TestRepository.Field.AssocRef.name());

		GenericValueObjectImpl masterObj = new GenericValueObjectImpl(masterInfo);
		masterObj.setValue(TestRepository.Field.MasterKey.name(), "key1");
		masterObj.setValue(TestRepository.Field.MasterValue.name(), "value1");
		masterObj.setValue(TestRepository.Field.DeptRef.name(), deptObj);
		masterObj.setValue(TestRepository.Field.NoDeptRef.name(), noDeptObj);
		
		AssocObjectList<GenericValueObjectImpl> objList = new AssocObjectList<GenericValueObjectImpl>(assoc, masterObj);
		objList.addObject(assocDetail1Obj);
		objList.addObject(assocDetail2Obj);
		
		masterObj.setValues(TestRepository.Field.AssocRef.name(), objList);

		ChangeableObjectList<GenericValueObjectImpl> objColl = new ChangeableObjectList<GenericValueObjectImpl>();
		objColl.addObject(masterObj);
		
		StringWriter writerOut = new StringWriter();
		new JsonCompactFileWriter(new JsonFileWriter.WriteAllDependentStrategy(), true, null).writeObjectList(writerOut, masterInfo, objColl, PersistentOperation.Operation.READ);
		
		JsonStringBuilderHelper result = new JsonStringBuilderHelper();
		result.listStart();
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "MASTER");
		result.addString("@persistence", "READ");
		result.addString("MasterKey", "key1");
		result.addString("MasterValue", "value1");
		result.addObject("NoDeptRef");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "NotDependentObj");
		result.addString("@persistence", "PROXY");
		result.addLastString("NoDeptKey", "key3");
		result.endObject();

		result.addObject("DeptRef");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "DependentObj");
		result.addString("@persistence", "READ");
		result.addString("DeptKey", "key2");
		result.addLastString("DeptValue", "value2");
		result.endObject();

		result.addAssoc("AssocRef");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "AssocDetail");
		result.addString("@persistence", "READ");
		result.addString("AssocDetailKey", "key4");
		result.addString("AssocDetailValue", "value4");

		result.addObject("MASTER");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "MASTER");
		result.addString("@persistence", "PROXY");
		result.addLastString("MasterKey", "key1");
		result.endObjectLast();
		result.nextAssocObj();
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "AssocDetail");
		result.addString("@persistence", "READ");
		result.addString("AssocDetailKey", "key5");
		result.addString("AssocDetailValue", "value5");
		result.addObject("MASTER");
		result.addString("@namespace", "TestRepository");
		result.addString("@classInternalName", "MASTER");
		result.addString("@persistence", "PROXY");
		result.addLastString("MasterKey", "key1");
		result.endObjectLast();
		result.endAssoc();
		result.listEnd();

		Assert.assertEquals(result.toString(), writerOut.toString());
	}	
}
