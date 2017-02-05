/*global test: false, equal: false, JDY:false, raises:false */

test(" Repository creation", function () {
    "use strict";
    var rep = new JDY.base.Repository("testrep"),
        createdClass = rep.addClassInfo("testclass");
    equal(createdClass.internalName, "testclass", "Class name exits");
    equal(createdClass.repoName, "testrep", "namespace exists");

    raises(function () {
        rep.addClassInfo("testclass");
    }, JDY.base.ValidationError, "ValidationError class already added ");

    createdClass = rep.getClassInfo("testclass");
    equal(createdClass.internalName, "testclass", "Class name exits");
    equal(createdClass.repoName, "testrep", "namespace exists");

});


test(" Add attributes to class", function () {
    "use strict";
    var rep = new JDY.base.Repository("testrep"),
        createdClass = rep.addClassInfo("testclass"),
        createdTextAttr = createdClass.addTextAttr("testTextAttr", 99),
        createdBooleanAttr = createdClass.addBooleanAttr("testBooleanAttr"),
        createdVarCharAttr = createdClass.addVarCharAttr("testVarCharAttr", 22),
        createdDecimalAttr = createdClass.addDecimalAttr("testDecimalAttr", -10.22, 10.44, 3),
        createdLongAttr = createdClass.addLongAttr("testLongAttr", -100, 1000),
        createdTimeStampAttr = createdClass.addTimeStampAttr("testTimeStampAttr", true, true),
        createdFloatAttr = createdClass.addBooleanAttr("testFloatAttr"),
        createdBlobAttr = createdClass.addBlobAttr("testBlobAttr");

    equal(createdTextAttr.getInternalName(), "testTextAttr", "Text Attribute exits");
    equal(createdBooleanAttr.getInternalName(), "testBooleanAttr", "Boolean Attribute exits");
    equal(createdVarCharAttr.getInternalName(), "testVarCharAttr", "VarChar Attribute exits");
    equal(createdDecimalAttr.getInternalName(), "testDecimalAttr", "Decimal Attribute exits");
    equal(createdLongAttr.getInternalName(), "testLongAttr", "Long Attribute exits");
    equal(createdTimeStampAttr.getInternalName(), "testTimeStampAttr", "TimeStamp Attribute exits");
    equal(createdFloatAttr.getInternalName(), "testFloatAttr", "Float Attribute exits");
    equal(createdBlobAttr.getInternalName(), "testBlobAttr", "Blob Attribute exits");

    raises(function () {
        createdClass.addTextAttr("testBooleanAttr");
    }, JDY.base.ValidationError, "ValidationError attribute already added ");


});

test(" Add association to class", function () {
    "use strict";
    var rep = new JDY.base.Repository("testrep"),
        masterclass = rep.addClassInfo("Masterclass"),
        detailclass = rep.addClassInfo("Detailclass"),
        assoc = null,
        backReference = null;
    rep.addAssociation("AssocName", masterclass, detailclass, "Masterclass", "Masterclass", true, true, true);
    assoc = masterclass.getAssoc("AssocName");
    backReference = detailclass.getAttr("Masterclass");
    equal(assoc.getAssocName(), "AssocName", "Association exits");
    equal(backReference.getInternalName(), "Masterclass", "Backreference exits");

});


test(" Subclass creation", function () {
    "use strict";
    var rep = new JDY.base.Repository("testrep"),
        baseClass = rep.addClassInfo("baseClass"),
		subClass1 = rep.addClassInfo("subClass1", baseClass),
		subClass2 = rep.addClassInfo("subClass2", subClass1),
		allAttr;

	baseClass.addTextAttr("baseTextAttr", 50);
	subClass1.addTextAttr("sub1TextAttr", 100);
	subClass2.addTextAttr("sub2TextAttr", 100);

	allAttr = subClass2.getAllAttributeList();

    equal(allAttr.length, 3, "Sublass has aslo Suberclass attributes");

});	


test(" Filter creation", function () {
    "use strict";
    var query,
		rep = JDYTEST.meta.createPlantShopRepository(),
		plantType = rep.getClassInfo("Plant");

		query = new JDY.base.QueryCreator(plantType)
				.or()
					.equal("BotanicName", "Iris")
					.and()
						.greater("HeigthInCm", 30)
						.less("HeigthInCm", 100)
					.end()
				.end().query();
	equal(query.resultType.getInternalName(), "Plant", "");
});

