/*jslint browser: true*/
/*global JDY, equal, test*/

var JDYTEST = JDYTEST || {};

JDYTEST.json = {};

test(" jdyjson.getConcreteClass", function () {

	"use strict";
    var rep = new JDY.base.Repository("testrep"),
        baseClass = rep.addClassInfo("baseClass"),
		subClass1 = rep.addClassInfo("subClass1", baseClass),
		subClass2 = rep.addClassInfo("subClass2", subClass1),
		jsonReader = new JDY.json.JsonFileReader(),
		concreteClass;

	concreteClass = jsonReader.getConcreteClass(baseClass, "testrep", "subClass2");
	
	equal(concreteClass.internalName, "subClass2", "Class name exits");
    equal(concreteClass.repoName, "testrep", "repo exists");

});

test(" jdyjson.jsonValueGetVisitor", function () {
	"use strict";
	
	var visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor(true),
		dateUtc, dateParsed;
	
    equal(visitor.handleBoolean(), true, "handleBoolean");
	visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor(100.67);
    equal(visitor.handleDecimal(100.67), 100.67, "handleDecimal");
	visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor("2012-06-15T14:57:46Z");
	dateParsed = visitor.handleTimeStamp();
	dateUtc = new Date(Date.UTC(2012,5,15,14,57,46,0));
    equal(dateParsed.getTime(), dateUtc.getTime(), "handleTimeStamp");
	visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor(1.20932);
    equal(visitor.handleFloat(1.20932), 1.20932, "handleFloat");
	visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor(1234567890);
    equal(visitor.handleLong(), 1234567890, "handleLong");
	visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor("test test");
    equal(visitor.handleText(), "test test", "handleText");
	visitor = JDY.json.JsonFileReader.prototype.jsonValueGetVisitor("varchar test");
    equal(visitor.handleVarChar(), "varchar test", "handleVarChar");

});

test(" jdyjson.writeObjectList", function () {
	"use strict";

	
	var jsonWriter = new JDY.json.JsonFileWriter(),
		resultList;
	resultList = jsonWriter.writeObjectList( JDYTEST.json.testAppRepository()[0].$assocs.Classes,JDY.json.Operation.INSERT);

	equal(resultList.length, 2, "Empty list");

});

JDYTEST.json.testAppRepository = function () {
	"use strict";

	var rep = JDY.meta.createAppRepository(),
		repositoryObj,
		nameSpace,
		classObj,
		attrObj,
		repositoryList = [];

	repositoryObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppRepository"));
	repositoryObj.applicationName = "Plants_sfs";
	repositoryObj.Name = "Plants sfs";
	repositoryObj.$assocs.Classes = [];
	repositoryList.push(repositoryObj);

	classObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppClassInfo"));
	classObj.Name = "Plant Family";
	classObj.InternalName = "Plant_Family";
	classObj.isAbstract = false;
	classObj.beforeSaveScript = "";
	classObj.Superclass = null;
	classObj.Repository = repositoryObj;
	classObj.$assocs.Attributes = [];
	classObj.$assocs.Associations = [];
	classObj.$assocs.Subclasses = [];
	repositoryObj.$assocs.Classes.push(classObj);

	classObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppClassInfo"));
	classObj.Name = "Plant Test";
	classObj.InternalName = "Plant_Test";
	classObj.isAbstract = false;
	classObj.beforeSaveScript = "delegate.first = second + double1";
	classObj.Superclass = null;
	classObj.Repository = repositoryObj;
	classObj.$assocs.Attributes = [];
	classObj.$assocs.Associations = [];
	classObj.$assocs.Subclasses = [];
	repositoryObj.$assocs.Classes.push(classObj);

	attrObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppTextType"));
	attrObj.Name = "Botanic Name";
	attrObj.InternalName = "Botanic_Name";
	attrObj.isKey = true;
	attrObj.isNotNull = true;
	attrObj.isGenerated = false;
	attrObj.AttrGroup = null;
	attrObj.pos = 0;
	attrObj.Masterclass = classObj;
	attrObj.length = 100;
	attrObj.typeHint = null;
	classObj.$assocs.Attributes.push(attrObj);

	attrObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppLongType"));
	attrObj.Name = "Heigth in cm";
	attrObj.InternalName = "Heigth_in_cm";
	attrObj.isKey = false;
	attrObj.isNotNull = true;
	attrObj.isGenerated = false;
	attrObj.AttrGroup = null;
	attrObj.pos = 1;
	attrObj.Masterclass = classObj;
	attrObj.MinValue = 0;
	attrObj.MaxValue = 50000;
	classObj.$assocs.Attributes.push(attrObj);

	return repositoryList;

};
