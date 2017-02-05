var JDYTEST = JDYTEST || {};

JDYTEST.meta = {};

JDYTEST.meta.createTestRepository = function () {
	"use strict";
	var TEST_NAME_SPACE = "de.jdynameta.base.test",
		appRep = new JDY.base.Repository("TestRepository"),
		allPrimitiveTypeObj;

	allPrimitiveTypeObj = appRep.addClassInfo("AllAttributeTypes").setShortName("AP");

	allPrimitiveTypeObj.addLongAttr("IntegerData", 1, 100).setIsKey(true).setExternalName("IntegerDataEx");
	allPrimitiveTypeObj.addBooleanAttr("BooleanData");
	//allPrimitiveTypeObj.addBlobAttr("BlobData");
	allPrimitiveTypeObj.addVarCharAttr("ClobData", 5000, true);
	allPrimitiveTypeObj.addDecimalAttr("CurrencyData", -100.00, 200.00, 3);
	allPrimitiveTypeObj.addTimeStampAttr("DateData", true, false);
	allPrimitiveTypeObj.addFloatAttr("FloatData");
	allPrimitiveTypeObj.addLongAttr("LongData", -30, 50);
	allPrimitiveTypeObj.addTextAttr("TextData", 10);
	allPrimitiveTypeObj.addTimeStampAttr("TimestampData", true, true);
	allPrimitiveTypeObj.addTimeStampAttr("TimeData", false, true);
	allPrimitiveTypeObj.addVarCharAttr("VarCharData", 20);

	return appRep;
};

JDYTEST.meta.createPlantShopRepository = function () {
	"use strict";
	var rep = new JDY.base.Repository("PlantShop"),
		plantType,
		addressType,
		customerType,
		orderType,
		orderItemType;

	plantType =  rep.addClassInfo("Plant");
	plantType.addTextAttr("BotanicName", 200).setIsKey(true);
	plantType.addLongAttr("HeigthInCm", 0, 5000);
	plantType.addTextAttr("PlantFamily", 100);
	plantType.addTextAttr("Color", 100);

	orderItemType =  rep.addClassInfo("OrderItem");
	orderItemType.addLongAttr("ItemNr", 0, 1000).setIsKey(true);
	orderItemType.addLongAttr("ItemCount", 0, 1000).setNotNull(true);
	orderItemType.addDecimalAttr("Price", 0.0000, 1000.0000, 4).setNotNull(true);
	orderItemType.addReference("Plant", plantType).setIsDependent(false).setNotNull(true);

	addressType =  rep.addClassInfo("Address");
	addressType.addTextAttr("AddressId", 30).setIsKey(true);
	addressType.addTextAttr("Street", 30).setNotNull(true);
	addressType.addTextAttr("ZipCode", 30).setNotNull(true);
	addressType.addTextAttr("City", 30).setNotNull(true);


	customerType =  rep.addClassInfo("Customer");
	customerType.addTextAttr("CustomerId", 30).setIsKey(true);
	customerType.addTextAttr("FirstName", 30).setNotNull(true);
	customerType.addTextAttr("MiddleName", 30).setNotNull(false);
	customerType.addTextAttr("LastName", 30).setNotNull(true);
	orderItemType.addReference("PrivateAddress", addressType).setIsDependent(true).setNotNull(true);
	orderItemType.addReference("InvoiceAddress", addressType).setIsDependent(true).setNotNull(false);


	orderType =  rep.addClassInfo("PlantOrder");
	orderType.addLongAttr("OrderNr", 0, 999999999).setIsKey(true);
	orderType.addTimeStampAttr("OrderDate", true, false ).setNotNull(true);

	rep.addAssociation("Items", orderType, orderItemType, true, true);

	return rep;
};

JDYTEST.view = {};

JDYTEST.view.testAppRepository = function (service) {
	"use strict";

	var rep = JDY.meta.createAppRepository(),
		repositoryObj,
		nameSpace,
		classObj,
		attrObj,
		repositoryList = [];

	repositoryObj = JDYTEST.view.createRepo(rep, "Plants_sfs");
	repositoryList.push(repositoryObj);

	classObj =JDYTEST.view.createType(rep, repositoryObj, "Plant_Family", "de_jdy_plants");
	service.insertObjectInDb(classObj);

	classObj =JDYTEST.view.createType(rep, repositoryObj, "Plant_Test", "de_jdy_plants");
	classObj.beforeSaveScript = "delegate.first = second + double1";

	JDYTEST.view.createTextAttribute(rep, classObj, 1, "Botanic_Name", true);
	JDYTEST.view.createLongAttribute(rep, classObj, 2, "Heigth_in_cm", false);
	service.insertObjectInDb(classObj);

	service.insertObjectInDb(repositoryObj);

	return repositoryList;
};


JDYTEST.view.writeTestAppRepos= function (writer) {
	"use strict";

	var rep = JDY.meta.createAppRepository(),
		repoObj,
		plantType,
		orderType,
		orderItemType,
		repositoryList = [];

	repoObj = JDYTEST.view.createRepo(rep, "Plants");
	plantType =JDYTEST.view.createType(rep, repoObj, "Plant", "plant.de");
	JDYTEST.view.createTextAttribute(rep, plantType, 1, "plantName", true);
	JDYTEST.view.createLongAttribute(rep, plantType, 2, "heightInCm", false);
	
	orderType = JDYTEST.view.createType(rep, repoObj, "Order", "plant.de");
	JDYTEST.view.createLongAttribute(rep, orderType, 1, "OrderId", true);
	JDYTEST.view.createTextAttribute(rep, orderType, 2, "OrderName", false);
	orderItemType = JDYTEST.view.createType(rep, repoObj, "OrderItem", "plant.de");
	JDYTEST.view.createLongAttribute(rep, orderItemType, 1, "OrderItemId", true);
	JDYTEST.view.createTextAttribute(rep, orderItemType, 2, "OrderItemName", false);

	writer.insertObjectInDb(repoObj);
	writer.insertObjectInDb(JDYTEST.view.createRepo(rep, "TestRepo A"));
	writer.insertObjectInDb(JDYTEST.view.createRepo(rep, "TestRepo B"));
	writer.insertObjectInDb(JDYTEST.view.createRepo(rep, "TestRepo C"));
	writer.insertObjectInDb(JDYTEST.view.createRepo(rep, "TestRepo D"));
	
	
};

JDYTEST.view.createRepo = function(rep, repoName)
{
	var repoObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppRepository"));
	repoObj.applicationName = repoName;
	repoObj.Name = repoName;
	repoObj.$assocs.Classes = [];
	return repoObj;
};

JDYTEST.view.createType = function(rep, repositoryObj, typeName, nameSpace)
{
	var classObj = new JDY.base.TypedValueObject(rep.getClassInfo("AppClassInfo"));
	classObj.Name = typeName;
	classObj.InternalName = typeName;
	classObj.isAbstract = false;
	classObj.NameSpace = nameSpace;
	classObj.beforeSaveScript = null;
	classObj.Superclass = null;
	classObj.Repository = repositoryObj;
	classObj.$assocs.Attributes = [];
	classObj.$assocs.Associations = [];
	classObj.$assocs.Subclasses = [];
	repositoryObj.$assocs.Classes.push(classObj);
	return classObj;
};

JDYTEST.view.createLongAttribute = function(rep, typeObj, pos, attrName, isKey)
{
	var attrObj = JDYTEST.view.createAttribute(rep, typeObj, pos, attrName, isKey,"AppLongType");
	attrObj.MinValue = 0;
	attrObj.MaxValue = 50000;
        attrObj.$assocs.DomainValues =[];

	return attrObj;
};

JDYTEST.view.createTextAttribute = function(rep, typeObj, pos, attrName, isKey)
{
	var attrObj = JDYTEST.view.createAttribute(rep, typeObj, pos, attrName, isKey,"AppTextType");
	attrObj.length = 100;
	attrObj.typeHint = null;
       	attrObj.$assocs.DomainValues =[];

	return attrObj;
};

JDYTEST.view.createAttribute = function(rep, typeObj, pos, attrName, isKey, type)
{
	var attrObj = new JDY.base.TypedValueObject(rep.getClassInfo(type));
	attrObj.Name = attrName;
	attrObj.InternalName = attrName;
	attrObj.isKey = isKey;
	attrObj.isNotNull = isKey;
	attrObj.isGenerated = false;
	attrObj.AttrGroup = null;
	attrObj.pos = pos;
	attrObj.Masterclass = typeObj;
	typeObj.$assocs.Attributes.push(attrObj);
	return attrObj;
};