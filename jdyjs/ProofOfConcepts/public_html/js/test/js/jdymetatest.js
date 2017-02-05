/*jslint browser: true*/
/*global JDY, equal, test*/

var JDYTEST = JDYTEST || {};

test(" jdymeta AppRepository creation", function () {

	"use strict";
	var rep = JDY.meta.createAppRepository(),
		appRep = rep.getClassInfo("AppRepository");
	equal(appRep.internalName, "AppRepository", "Class AppRepository exists");

});

test(" jdymeta.getConcreteClass", function () {

	"use strict";
	var rep = JDY.meta.createAppRepository(),
		appAttributes = rep.getClassInfo("AppAttribute"),
		jsonReader = new JDY.json.JsonFileReader(),
		concreteClass;

	concreteClass = jsonReader.getConcreteClass(appAttributes, "ApplicationRepository", "AppLongType");

	equal(concreteClass.internalName, "AppLongType", "Class name exits");
    equal(concreteClass.repoName, "ApplicationRepository", "namespace exists");

});

test(" jdymeta AppRepository read data", function () {

	"use strict";
	var rep = JDY.meta.createAppRepository(),
		appRep = rep.getClassInfo("AppRepository"),
		jsonReader = new JDY.json.JsonFileReader(),
		readedList = jsonReader.readObjectList(JDYTEST.testData(), appRep),
		expectedResult = JDYTEST.testResult();

	equal(readedList[0].applicationName, expectedResult[0].applicationName, "AppRepository exists");

});

test(" jdymeta convertAppRepositoryToRepository", function () {

	"use strict";
	var rep = JDY.meta.createAppRepository(),
		appRep = rep.getClassInfo("AppRepository"),
		jsonReader = new JDY.json.JsonFileReader(),
		readedList = jsonReader.readObjectList(JDYTEST.testData(), appRep);
		
		JDY.meta.convertAppRepositoryToRepository(readedList[0], function(newRepository) {
			equal(readedList[0].applicationName, newRepository.repoName, "AppRepository exists");
		});

});

test(" jdymeta transform filter to app filter object ", function () {

	"use strict";
	var rep = JDY.meta.createFilterRepository(),
		appRep = rep.getClassInfo("AppQuery"),
		plantType,
		query,
		appQuery,
		creator = new JDY.meta.FilterCreator(),
		att2AbbrMap = {};
		
	att2AbbrMap.repoName="rn";
	att2AbbrMap.className="cn";
	att2AbbrMap.expr="ex";
	att2AbbrMap.orSubExpr="ose";
	att2AbbrMap.andSubExpr="ase";
	att2AbbrMap.attrName="an";
	att2AbbrMap.operator="op";
	att2AbbrMap.isNotEqual="ne";
	att2AbbrMap.isAlsoEqual="ae";
	att2AbbrMap.longVal="lv";
	att2AbbrMap.textVal="tv";
	

	equal(appRep.repoName, "FilterRepository", "FilterRepository exists");

	plantType = JDYTEST.meta.createPlantShopRepository().getClassInfo("Plant");

	query = new JDY.base.QueryCreator(plantType)
				.or()
					.equal("BotanicName", "Iris")
					.and()
						.greater("HeigthInCm", 30)
						.less("HeigthInCm", 100)
					.end()
				.end().query();


	appQuery = creator.convertMetaFilter2AppFilter(query);
	
	var jsonWriter = new JDY.json.JsonCompactFileWriter(att2AbbrMap),
		resultList,
		jsonString;
	resultList = jsonWriter.writeObjectList( [appQuery],JDY.json.Operation.INSERT);
	jsonString = JSON.stringify(resultList);
	console.log(jsonString);
	
	

});

JDYTEST.testData = function () {

	"use strict";
	return [{
		"@namespace": "ApplicationRepository",
		"@classInternalName": "AppRepository",
		"@persistence": "READ",
		"Name": "Plants sfs",
		"applicationName": "Plants_sfs",
		"appVersion": 1,
		"closed": true,
		"Classes": [{
			"@namespace": "ApplicationRepository",
			"@classInternalName": "AppClassInfo",
			"@persistence": "READ",
			"Name": "Plant Family",
			"InternalName": "Plant_Family",
			"NameSpace": null,
			"isAbstract": false,
			"beforeSaveScript": null,
			"Superclass": null,
			"Repository": {
				"@namespace": "ApplicationRepository",
				"@classInternalName": "AppRepository",
				"@persistence": "PROXY",
				"applicationName": "Plants_sfs"
			},
			"Attributes": [],
			"Associations": [],
			"Subclasses": []
		}, {
			"@namespace": "ApplicationRepository",
			"@classInternalName": "AppClassInfo",
			"@persistence": "READ",
			"Name": "Plant Test",
			"NameSpace": null,
			"InternalName": "Plant_Test",
			"isAbstract": false,
			"beforeSaveScript": "delegate.first = second + double1",
			"Superclass": null,
			"Repository": {
				"@namespace": "ApplicationRepository",
				"@classInternalName": "AppRepository",
				"@persistence": "PROXY",
				"applicationName": "Plants_sfs"
			},
			"Attributes": [{
				"@namespace": "ApplicationRepository",
				"@classInternalName": "AppTextType",
				"@persistence": "READ",
				"Name": "Botanic Name",
				"InternalName": "Botanic_Name",
				"isKey": true,
				"isNotNull": true,
				"isGenerated": false,
				"AttrGroup": null,
				"pos": 0,
				"DomainValues" : [ {
					"@namespace" : "ApplicationRepository",
					"@classInternalName" : "AppStringDomainModel",
					"@persistence" : "READ",
					"representation" : "Iridaceae",
					"dbValue" : "Iridaceae",
					"Type" : {
					  "@namespace" : "ApplicationRepository",
					  "@classInternalName" : "AppTextType",
					  "@persistence" : "PROXY",
					  "InternalName" : "PlantFamily",
					  "Masterclass" : {
						"@namespace" : "ApplicationRepository",
						"@classInternalName" : "AppClassInfo",
						"@persistence" : "PROXY",
						"InternalName" : "Plant",
						"Repository" : {
						  "@namespace" : "ApplicationRepository",
						  "@classInternalName" : "AppRepository",
						  "@persistence" : "PROXY",
						  "applicationName" : "PlantShop"
						}
					  }
					}
				  }, {
					"@namespace" : "ApplicationRepository",
					"@classInternalName" : "AppStringDomainModel",
					"@persistence" : "READ",
					"representation" : "Malvaceae",
					"dbValue" : "Malvaceae",
					"Type" : {
					  "@namespace" : "ApplicationRepository",
					  "@classInternalName" : "AppTextType",
					  "@persistence" : "PROXY",
					  "InternalName" : "PlantFamily",
					  "Masterclass" : {
						"@namespace" : "ApplicationRepository",
						"@classInternalName" : "AppClassInfo",
						"@persistence" : "PROXY",
						"InternalName" : "Plant",
						"Repository" : {
						  "@namespace" : "ApplicationRepository",
						  "@classInternalName" : "AppRepository",
						  "@persistence" : "PROXY",
						  "applicationName" : "PlantShop"
						}
					  }
					}
				  }, {
					"@namespace" : "ApplicationRepository",
					"@classInternalName" : "AppStringDomainModel",
					"@persistence" : "READ",
					"representation" : "Geraniaceae",
					"dbValue" : "Geraniaceae",
					"Type" : {
					  "@namespace" : "ApplicationRepository",
					  "@classInternalName" : "AppTextType",
					  "@persistence" : "PROXY",
					  "InternalName" : "PlantFamily",
					  "Masterclass" : {
						"@namespace" : "ApplicationRepository",
						"@classInternalName" : "AppClassInfo",
						"@persistence" : "PROXY",
						"InternalName" : "Plant_Test",
						"Repository" : {
						  "@namespace" : "ApplicationRepository",
						  "@classInternalName" : "AppRepository",
						  "@persistence" : "PROXY",
						  "applicationName" : "Plants_sfs"
						}
					  }
					}
				  } ]
				,
				"Masterclass": {
					"@namespace": "ApplicationRepository",
					"@classInternalName": "AppClassInfo",
					"@persistence": "PROXY",
					"InternalName": "Plant_Test",
					"Repository": {
						"@namespace": "ApplicationRepository",
						"@classInternalName": "AppRepository",
						"@persistence": "PROXY",
						"applicationName": "Plants_sfs"
					}
				},
				"length": 100,
				"typeHint": null
			}, {
				"@namespace": "ApplicationRepository",
				"@classInternalName": "AppLongType",
				"@persistence": "READ",
				"Name": "Heigth in cm",
				"InternalName": "Heigth_in_cm",
				"isKey": false,
				"isNotNull": true,
				"isGenerated": false,
				"AttrGroup": null,
				"pos": 1,
				"DomainValues" : [],
				"Masterclass": {
					"@namespace": "ApplicationRepository",
					"@classInternalName": "AppClassInfo",
					"@persistence": "PROXY",
					"InternalName": "Plant_Test",
					"Repository": {
						"@namespace": "ApplicationRepository",
						"@classInternalName": "AppRepository",
						"@persistence": "PROXY",
						"applicationName": "Plants_sfs"
					}
				},
				"MinValue": 0,
				"MaxValue": 50000
			}],
			"Associations": [],
			"Subclasses": []
		}]
	}];
};

JDYTEST.testResult = function () {

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
	repositoryObj.appVersion = 1;
	repositoryObj.Classes = [];
	repositoryList.push(repositoryObj);

	classObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppClassInfo"));
	classObj.Name = "Plant Family";
	classObj.InternalName = "Plant_Family";
	classObj.isAbstract = false;
	classObj.beforeSaveScript = "";
	classObj.Superclass = null;
	classObj.Repository = repositoryObj;
	classObj.Attributes = [];
	classObj.Associations = [];
	classObj.Subclasses = [];
	repositoryObj.Classes.push(classObj);

	classObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppClassInfo"));
	classObj.Name = "Plant Test";
	classObj.InternalName = "Plant_Test";
	classObj.isAbstract = false;
	classObj.beforeSaveScript = "delegate.first = second + double1";
	classObj.Superclass = null;
	classObj.Repository = repositoryObj;
	classObj.Attributes = [];
	classObj.Associations = [];
	classObj.Subclasses = [];
	repositoryObj.Classes.push(classObj);

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
	classObj.Attributes.push(attrObj);

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
	classObj.Attributes.push(attrObj);

	return repositoryList;
	
};
