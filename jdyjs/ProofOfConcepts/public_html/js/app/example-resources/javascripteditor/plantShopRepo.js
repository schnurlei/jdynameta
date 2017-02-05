var JDYEXAMPLE = JDYEXAMPLE || {};

JDYEXAMPLE.createPlantShopRepository = function () {
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

JDYEXAMPLE.createPlantShopRepository();