/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

test(" JDY.taffy.crudSimpleTest", function () {
	"use strict";
	var rep,
	    createdClass,
	    refClass,
	    value,
	    refValue,
	    service,
	    filter,
	    result;

	// Test Repository
	rep = new JDY.base.Repository("testrep");
	createdClass = rep.addClassInfo("testclass");
	createdClass.addTextAttr("testTextAttr", 99).setIsKey(true);
	createdClass.addBooleanAttr("testBooleanAttr");
	createdClass.addLongAttr("testLongAttr", -100, 1000);
	refClass= rep.addClassInfo("refClass");
	refClass.addTextAttr("refTextAttr", 99).setIsKey(true);
	refClass.addTextAttr("refValue1", 99);
	refClass.addTextAttr("refValue2", 99);
	createdClass.addReference("masterClassReference", refClass);

	service =  new JDY.taffy.TaffyObjectReaderWriter();

	value = service.createNewObject(createdClass);
	value.testTextAttr = "Plant Family";
	value.testBooleanAttr = true;
	value.testLongAttr = 123456;
	refValue = service.createNewObject(refClass);
	refValue.refTextAttr = "Ref text key";
	refValue.refValue1 = "Ref text 1";
	refValue.refValue2 = "Ref text 2";
	value.masterClassReference = refValue;

	service.insertObjectInDb(value,function(insertedObject){
				
	    equal(insertedObject.testTextAttr, "Plant Family");
	});


	value.testLongAttr = 454545;

	service.updateObjectInDb(value,function(updatedObject){
				
	    equal(updatedObject.testLongAttr, "454545");
	});
	
	filter = new JDY.base.QueryCreator(rep.getClassInfo("testclass")).query();
	service.loadValuesFromDb(filter, function(loadedObjects){
	    equal(loadedObjects.length, 1);
	});

	service.deleteObjectInDb(value,function(){
	});

	service.loadValuesFromDb(filter, function(loadedObjects){
	    equal(loadedObjects.length, 0);
	});
	
});

test(" JDY.taffy.insertTest", function () {
	"use strict";
	var rep,
	    createdClass,
	    refClass,
		refClassLvl2,
	    value,
	    refValue,
		refValueLvl2,
	    service,
	    filter,
	    result;

	// Test Repository
	rep = new JDY.base.Repository("testrep");
	createdClass = rep.addClassInfo("testclass");
	createdClass.addTextAttr("testTextAttr", 99).setIsKey(true);
	createdClass.addBooleanAttr("testBooleanAttr");
	createdClass.addLongAttr("testLongAttr", -100, 1000);
	refClass= rep.addClassInfo("refClass");
	refClass.addTextAttr("refTextAttr", 99).setIsKey(true);
	refClass.addTextAttr("refValue1", 99);
	refClass.addTextAttr("refValue2", 99);
	createdClass.addReference("masterClassReference", refClass);
	refClassLvl2= rep.addClassInfo("refClassLvl2");
	refClassLvl2.addTextAttr("refClassLvl2Value1", 99);

	rep.addAssociation("Attributes", createdClass, refClass, "Masterclass", "Masterclass", true, true, true);
	rep.addAssociation("RefLvl2", refClass, refClassLvl2, "RefLevlsKey", "RefLevlsKey", true, true, true);

	service =  new JDY.taffy.TaffyObjectReaderWriter();

	value = service.createNewObject(createdClass);
	value.testTextAttr = "Plant Family";
	value.testBooleanAttr = true;
	value.testLongAttr = 123456;
	refValue = service.createNewObject(refClass);
	refValue.refTextAttr = "Ref text key";
	refValue.refValue1 = "Ref text 1";
	refValue.refValue2 = "Ref text 2";
	refValue.Masterclass = value;
	value.masterClassReference = refValue;
	refValueLvl2 = service.createNewObject(refClassLvl2);
	refValueLvl2.refClassLvl2Value1 = "Ref level 2 value key";
	refValueLvl2.RefLevlsKey = refValue;

	service.insertObjectInDb(refValueLvl2,function(insertedObject){
				
	    equal(insertedObject.RefLevlsKey.Masterclass.testTextAttr, "Plant Family");
	});

});





test(" JDY.taffy.insertAttributesInDb", function () {

	var rep = JDYTEST.meta.createTestRepository(),
		testClass = rep.getClassInfo("AllAttributeTypes"),
		testViewModel = new JDY.base.TypedValueObject(testClass);
	testViewModel.IntegerData = 22;
	testViewModel.BooleanData = true;
	testViewModel.ClobData = 'Clob data';
	testViewModel.CurrencyData = 33.89;
	testViewModel.DateData = new Date();
	testViewModel.FloatData = 77.89;
	testViewModel.LongData = 49;
	testViewModel.TextData = 'texta';
	testViewModel.TimestampData = new Date();
	testViewModel.TimeData = new Date();
	testViewModel.VarCharData = 'VarCharData';

	equal(rep.repoName, "TestRepository", "Class AppRepository exists");


});

test(" JDY.taffy.testFilter", function () {

	var value,
	    createdClass,
		service,
		filter;

	// Test Repository
	createdClass = new JDY.base.Repository("testrep").addClassInfo("testclass");
	createdClass.addTextAttr("testId", 99).setIsKey(true);
	createdClass.addTextAttr("testTextAttr", 99);
	createdClass.addBooleanAttr("testBooleanAttr");
	createdClass.addLongAttr("testLongAttr", -100, 1000);

	service =  new JDY.taffy.TaffyObjectReaderWriter();
	value = service.createNewObject(createdClass);
	value.testId = 1;
	value.testTextAttr = "Iris";
	value.testBooleanAttr = true;
	value.testLongAttr = 120;
	service.insertObjectInDb(value);

	value = service.createNewObject(createdClass);
	value.testId = 2;
	value.testTextAttr = "No Iris";
	value.testBooleanAttr = true;
	value.testLongAttr = 120;
	service.insertObjectInDb(value);

	filter = new JDY.base.QueryCreator(createdClass)
				.or()
					.equal("testTextAttr", "Iris")
					.and()
						.equal("testBooleanAttr", true)
						.less("testLongAttr", 100)
					.end()
				.end().query();

	service.loadValuesFromDb(filter, function (loadedObjects) {
	    equal(loadedObjects.length, 1);
	});

});

test(" JDY.taffy.testBooleanFilter", function () {

	var testClass,
	    service,
	    filter,
	    value;

	// Test Repository
	testClass = new JDY.base.Repository("testrep").addClassInfo("testclass");;
	testClass.addTextAttr("testId", 99).setIsKey(true);
	testClass.addBooleanAttr("testBoolean");

	service =  new JDY.taffy.TaffyObjectReaderWriter();

	value = service.createNewObject(testClass);
	value.testId = 1;
	value.testBoolean = true;
	service.insertObjectInDb(value);
	value.testId = 2;
	value.testBoolean = false;
	service.insertObjectInDb(value);
	value.testId = 3;
	value.testBoolean = true;
	service.insertObjectInDb(value);
	value.testId = 4;
	value.testBoolean = false;
	service.insertObjectInDb(value);
	value.testId = 5;
	value.testBoolean = true;
	service.insertObjectInDb(value);

	filter = new JDY.base.QueryCreator(testClass)
				.equal("testBoolean", true).query();

	service.loadValuesFromDb(filter, function (loadedObjects) {
	    equal(loadedObjects.length, 3);
	});
});

test(" JDY.taffy.testStringFilter", function () {

	var testClass,
	    service,
	    filter,
	    value;

	// Test Repository
	testClass = new JDY.base.Repository("testrep").addClassInfo("testclass");;
	testClass.addTextAttr("testId", 99).setIsKey(true);
	testClass.addTextAttr("testString");

	service =  new JDY.taffy.TaffyObjectReaderWriter();

	value = service.createNewObject(testClass);
	value.testId = 1;
	value.testString = "rot";
	service.insertObjectInDb(value);
	value.testId = 2;
	value.testString =  "blau";
	service.insertObjectInDb(value);
	value.testId = 3;
	value.testString =  "gelb";
	service.insertObjectInDb(value);
	value.testId = 4;
	value.testString =  "orange";
	service.insertObjectInDb(value);
	value.testId = 5;
	value.testString =  "rot";
	service.insertObjectInDb(value);

	filter = new JDY.base.QueryCreator(testClass)
				.equal("testString", "rot").query();

	service.loadValuesFromDb(filter, function (loadedObjects) {
	    equal(loadedObjects.length, 2);
	});
	
	filter = new JDY.base.QueryCreator(testClass)
				.greater("testString", "hhh").query();

	service.loadValuesFromDb(filter, function (loadedObjects) {
	    equal(loadedObjects.length, 3);
	});
});