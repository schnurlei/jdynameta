/*jslint browser: true*/
/*global JDY, equal, test*/

test(" jdyviewtest AppRepository creation", function () {

	"use strict";
	var rep = JDYTEST.meta.createTestRepository(),
		allAttrTyp = rep.getClassInfo("AllAttributeTypes");
	equal(allAttrTyp.internalName, "AllAttributeTypes", "Class AllAttributeTypes exists");

});


test(" JDY.view.getDisplayAttributesFor", function () {

	"use strict";
	var rep = JDYTEST.meta.createTestRepository(),
		allAttrTyp = rep.getClassInfo("AllAttributeTypes"),
		aspectPaths = JDY.view.getDisplayAttributesFor(allAttrTyp),
		appRep = JDY.meta.createAppRepository();
		
	equal(aspectPaths.length, 1, "AllAttributeTypes 1 path");
	equal(aspectPaths[0].length, 1, "AllAttributeTypes One Attribute in first path");
	equal(aspectPaths[0][0].internalName, "IntegerData", "One Attribute in first path");
	
	aspectPaths = JDY.view.getDisplayAttributesFor(appRep.getClassInfo("AppRepository"));
	equal(aspectPaths.length, 1, "AppRepository 1 path");
	equal(aspectPaths[0].length, 1, "AppRepository One Attribute in first path");
	equal(aspectPaths[0][0].internalName, "applicationName", "One Attribute in first path");

	aspectPaths = JDY.view.getDisplayAttributesFor(appRep.getClassInfo("AppTextType"));
	equal(aspectPaths.length, 3, "AppTextType 3 paths");
	equal(aspectPaths[0].length, 1, "AppTextType 1 Attribute in path 0");
	equal(aspectPaths[1].length, 2, "AppTextType 2 Attributes in path 1");
	equal(aspectPaths[2].length, 3, "AppTextType 3 Attributes in path 2");
	equal(aspectPaths[0][0].internalName, "InternalName", "AppTextType 0 0");
	equal(aspectPaths[1][0].internalName, "Masterclass", "AppTextType 1 0");
	equal(aspectPaths[1][1].internalName, "InternalName", "AppTextType 1 1");
	equal(aspectPaths[2][0].internalName, "Masterclass", "AppTextType 2 0");
	equal(aspectPaths[2][1].internalName, "Repository", "AppTextType 2 1");
	equal(aspectPaths[2][2].internalName, "applicationName", "AppTextType 2 2");

});