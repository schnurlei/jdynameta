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

	repositoryObj = JDYTEST.view.f(rep, "Plants_sfs");
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


JDYTEST.view.writeTestAppReposPrms= function (writer) {
    
    var rep = JDY.meta.createAppRepository(),
        repoPrmsObj,
        orderTypePrms,
        plantTypePrms,
        textAttrPrms,
        longAttrPrms,
        textAttr2Prms,
        longAttr2Prms;

    repoPrmsObj = JDYTEST.view.createRepoPrms(rep, "Plants",writer);
    plantTypePrms = JDYTEST.view.createTypePrms(rep, repoPrmsObj, "Plant", "plant.de", writer);
    textAttrPrms = JDYTEST.view.createTextAttributePrms(rep, plantTypePrms, 1, "plantName", true, writer);
    longAttrPrms = JDYTEST.view.createLongAttributePrms(rep, plantTypePrms, 2, "heightInCm", false, writer);
    orderTypePrms = JDYTEST.view.createTypePrms(rep, repoPrmsObj, "Order", "plant.de",writer);
    textAttr2Prms = JDYTEST.view.createLongAttributePrms(rep, orderTypePrms, 1, "OrderId", true, writer);
    longAttr2Prms = JDYTEST.view.createTextAttributePrms(rep, orderTypePrms, 2, "OrderItemName", false, writer);

    return $.when(repoPrmsObj, longAttrPrms, textAttrPrms, textAttr2Prms, longAttr2Prms);
};


JDYTEST.view.createRepo = function(rep, repoName, writer)
{
	var repoObj = writer.createNewObject(rep.getClassInfo("AppRepository"));
	repoObj.applicationName = repoName;
	repoObj.Name = repoName;
	return repoObj;
};

JDYTEST.view.createRepoPrms = function(rep, repoName, writer) {
    
    var dfrd = $.Deferred();
    var newObj = JDYTEST.view.createRepo(rep, repoName, writer);
    writer.insertObjectInDb(newObj, function(aCreatedRepo){
            
        dfrd.resolve(aCreatedRepo);
    });
    
    return dfrd.promise();
};
    
    

JDYTEST.view.createType = function(rep, repositoryObj, typeName, nameSpace, writer)
{
	var classObj = writer.createNewObject(rep.getClassInfo("AppClassInfo"));
	classObj.Name = typeName;
	classObj.InternalName = typeName;
	classObj.isAbstract = false;
	classObj.NameSpace = nameSpace;
	classObj.beforeSaveScript = null;
	classObj.Superclass = null;
	classObj.Repository = repositoryObj;
	return classObj;
};

JDYTEST.view.createTypePrms = function(rep, aRepPrms, typeName, nameSpace, writer) {
    
    var dfrd = $.Deferred();
    aRepPrms.done(function(aRepoObj) {
        var newType = JDYTEST.view.createType(rep, aRepoObj, typeName, nameSpace, writer);
        writer.insertObjectInDb(newType, function(aCreatedType){
            
			aRepoObj.assocVals("Classes").done(function(){
	            aRepoObj.assocVals("Classes").add(aCreatedType);
		        dfrd.resolve(aCreatedType);
			});
        });
    });
    
    return dfrd.promise();
};

JDYTEST.view.createLongAttribute = function(rep, typeObj, pos, attrName, isKey, writer)
{
	var attrObj = JDYTEST.view.createAttribute(rep, typeObj, pos, attrName, isKey,"AppLongType", writer);
	attrObj.MinValue = 0;
	attrObj.MaxValue = 50000;
        attrObj.$assocs.DomainValues = $.when([]).promise(); 

	return attrObj;
};

JDYTEST.view.createLongAttributePrms = function(rep, typeObjPrms, pos, attrName, isKey, writer) {

    var dfrd = $.Deferred();
    typeObjPrms.done(function(aTypeObj) {
        var newAttr = JDYTEST.view.createLongAttribute(rep, aTypeObj, pos, attrName, isKey, writer);
        writer.insertObjectInDb(newAttr, function(aCreatedAttr){
            
			aTypeObj.assocVals("Attributes").done(function(){
	            aTypeObj.assocVals("Attributes").add(aCreatedAttr);
		        dfrd.resolve(aCreatedAttr);
			});
        });
    });
    
    return dfrd.promise();
};

JDYTEST.view.createTextAttribute = function(rep, typeObj, pos, attrName, isKey, writer)
{
	var attrObj = JDYTEST.view.createAttribute(rep, typeObj, pos, attrName, isKey,"AppTextType", writer);
	attrObj.length = 100;
	attrObj.typeHint = null;
	return attrObj;
};

JDYTEST.view.createTextAttributePrms = function(rep, typeObjPrms, pos, attrName, isKey, writer) {

    var dfrd = $.Deferred();
    typeObjPrms.done(function(aTypeObj) {
        var newAttr = JDYTEST.view.createTextAttribute(rep, aTypeObj, pos, attrName, isKey, writer);
        writer.insertObjectInDb(newAttr, function(aCreatedAttr){
            
			aTypeObj.assocVals("Attributes").done(function(){
	            aTypeObj.assocVals("Attributes").add(aCreatedAttr);
		        dfrd.resolve(aCreatedAttr);
			});
        });
    });
    
    return dfrd.promise();
};

JDYTEST.view.createAttribute = function(rep, typeObj, pos, attrName, isKey, type, writer)
{
	var attrObj = writer.createNewObject(rep.getClassInfo(type));
	attrObj.Name = attrName;
	attrObj.InternalName = attrName;
	attrObj.isKey = isKey;
	attrObj.isNotNull = isKey;
	attrObj.isGenerated = false;
	attrObj.AttrGroup = null;
	attrObj.pos = pos;
	attrObj.Masterclass = typeObj;
	return attrObj;
};