var JDYEXAMPLE = JDYEXAMPLE || {};

JDYEXAMPLE.meta = {};

JDYEXAMPLE.meta.createPlantTestRepository = function () {
	"use strict";
	var rep = new JDY.base.Repository("PlantShop"),
		plantType,
		addressType,
		customerType,
		orderType,
		orderItemType,
		allPrimitiveTypeObj;

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

	allPrimitiveTypeObj = rep.addClassInfo("AllAttributeTypes").setShortName("AP");

	allPrimitiveTypeObj.addLongAttr("IntegerData", 1, 100).setIsKey(true).setExternalName("IntegerDataEx").setNotNull(true);
	allPrimitiveTypeObj.addBooleanAttr("BooleanData");
	//allPrimitiveTypeObj.addBlobAttr("BlobData");
	allPrimitiveTypeObj.addVarCharAttr("ClobData", 5000, true);
	allPrimitiveTypeObj.addDecimalAttr("CurrencyData", -100.00, 200.00, 3).setNotNull(true);
	allPrimitiveTypeObj.addTimeStampAttr("DateData", true, false);
	allPrimitiveTypeObj.addFloatAttr("FloatData");
	allPrimitiveTypeObj.addLongAttr("LongData", -30, 50).setNotNull(true);
	allPrimitiveTypeObj.addTextAttr("TextData", 10);
	allPrimitiveTypeObj.addEmailAttr("EmailData", 10);
	allPrimitiveTypeObj.addUrlAttr("UrlData", 10);
	allPrimitiveTypeObj.addTelephoneAttr("TelephoneData", 10);

	allPrimitiveTypeObj.addTimeStampAttr("TimestampData", true, true);
	allPrimitiveTypeObj.addTimeStampAttr("TimeData", false, true);
	allPrimitiveTypeObj.addVarCharAttr("VarCharData", 20);

	rep.addAssociation("Items", orderType, orderItemType, true, true);

	return rep;
};

JDYEXAMPLE.meta.createAllAttributeTypesObject = function (typesClass) {

    var testViewModel = new JDY.base.TypedValueObject(typesClass);
    testViewModel.IntegerData = 22;
    testViewModel.BooleanData= true;
    testViewModel.ClobData= 'Clob data';
    testViewModel.CurrencyData= 33.89;
    testViewModel.DateData= new Date();
    testViewModel.FloatData= 77.89;
    testViewModel.LongData= 49;
    testViewModel.TextData= 'texta';
    testViewModel.TimestampData= new Date();
    testViewModel.TimeData= new Date();
    testViewModel.VarCharData= 'VarCharData';
    
    return testViewModel;
};
