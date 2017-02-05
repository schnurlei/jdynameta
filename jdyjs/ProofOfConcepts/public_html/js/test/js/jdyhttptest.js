test(" jdyhttp.parameterGetVisitor", function () {
	"use strict";
	
	var visitor;
	
	visitor = JDY.http.parameterGetVisitor(true);
    equal(visitor.handleBoolean(), "true", "handleBoolean");
	visitor = JDY.http.parameterGetVisitor(100.67);
    equal(visitor.handleDecimal(),"100.67", "handleDecimal");
	visitor = JDY.http.parameterGetVisitor(new Date(Date.UTC(2012,5,15,14,57,46,0)));
    equal(visitor.handleTimeStamp(), "2012-06-15T14:57:46.000Z", "handleTimeStamp");
	visitor = JDY.http.parameterGetVisitor(1.20932);
    equal(visitor.handleFloat(1.20932), "1.20932", "handleFloat");
	visitor = JDY.http.parameterGetVisitor(1234567890);
    equal(visitor.handleLong(), "1234567890", "handleLong");
	visitor = JDY.http.parameterGetVisitor("test test");
    equal(visitor.handleText(), "test test", "handleText");
	visitor = JDY.http.parameterGetVisitor("varchar test");
    equal(visitor.handleVarChar(), "varchar test", "handleVarChar");

});




test(" jdyhttp.createParametersFor", function () {
	"use strict";
	var rep = new JDY.base.Repository("testrep"),
		createdClass = rep.addClassInfo("testclass"),
		refClass = rep.addClassInfo("refClass"),
		value,
		refValue,
		params;

	createdClass.addTextAttr("testTextAttr", 99).setIsKey(true);
	createdClass.addBooleanAttr("testBooleanAttr");
	createdClass.addLongAttr("testLongAttr", -100, 1000).setIsKey(true);
	createdClass.addReference("masterClassReference", refClass).setIsKey(true);

	refClass.addTextAttr("refTextAttr", 99).setIsKey(true),

	value = new JDY.base.TypedValueObject(createdClass);
	value.testTextAttr = "Plant Family";
	value.testBooleanAttr = true;
	value.testLongAttr = 123456;
	
	refValue = new JDY.base.TypedValueObject(refClass);
	refValue.refTextAttr = "Ref text value";
	
	value.masterClassReference = refValue;

	params = JDY.http.createParametersFor(value, createdClass, "");
	equal(params.length, 3, "2 primitive attributes");
	equal(params[0].name, "testTextAttr", "text attr name");
	equal(params[0].value, "Plant Family", "text attr value");
	equal(params[1].name, "testLongAttr", "long attr name");
	equal(params[1].value, "123456", "long attr value");
	equal(params[2].name, "masterClassReference.refTextAttr", "ref attr name");
	equal(params[2].value, "Ref text value", "ref attr value");

});
