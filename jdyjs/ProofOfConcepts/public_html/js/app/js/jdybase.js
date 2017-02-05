 // jdynameta, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true*/
/* jslint global $ alert */
"use strict";
// initialize global jdy namespace
var JDY = JDY || {};

JDY.base = {};

JDY.inherit = (function () {

	var TempConstructor = function () {
	};

	return function (child, parent) {

		TempConstructor.prototype = parent.prototype;
		child.prototype = new TempConstructor();
		child.uber = parent.prototype;
		child.prototype.constructor = child;
	};

}());

JDY.base.ValidationError = function (message) {
    this.name = "ValidationError";
    this.message = message || "Validation Error";
};

JDY.base.JdyPersistentException = function (message) {
    this.name = "JdyPersistentException";
    this.message = message || "JdyPersistentException";
};


JDY.base.ValidationError.prototype = new Error();
JDY.base.ValidationError.prototype.constructor = JDY.base.ValidationError;

JDY.base.Repository = function (repositoryName) {
	this.repoName = repositoryName;
	this.classes = {};
};

JDY.base.Repository.prototype.addClassInfo = function (aInternalName, aSuperclass) {

	var newClass = null;
	if (this.classes[aInternalName]) {
		throw new JDY.base.ValidationError("Class already exists with name: " + aInternalName);
	}

	newClass = new JDY.base.ClassInfo(aInternalName, this.repoName, aSuperclass);
	this.classes[aInternalName] = newClass;
	return newClass;
};

JDY.base.Repository.prototype.getClassInfo = function (aInternalName) {
	return this.classes[aInternalName];
};

JDY.base.Repository.prototype.addAssociation = function (anAssocName, aMasterClass, aDetailClass,
			aDetailInternalName, aDetailExternalName, keyInDetail, notNullInDetail, aIsDependent) {
	var newAssoc,
		detailToMasterAssoc;

	detailToMasterAssoc = new JDY.base.ObjectReferenceInfo(aDetailInternalName, aDetailExternalName,
								keyInDetail, notNullInDetail, aMasterClass);
	detailToMasterAssoc.setIsDependent(aIsDependent);
	detailToMasterAssoc.setIsInAssociation(true);
	aDetailClass.addReferenceToAttrList(detailToMasterAssoc);
	newAssoc = new JDY.base.AssociationModel(detailToMasterAssoc, aDetailClass, anAssocName);
	aMasterClass.addAssociation(newAssoc);
};

JDY.base.ClassInfo = function (aInternalName, aRepoName, aSuperclass) {

	this.nameSpace = null;
	this.repoName = aRepoName;
	this.internalName = aInternalName;
	this.externalName = null;
	this.shortName = null;
	this.isAbstract = false;
	this.superclass = aSuperclass;
	if (this.superclass) {
		this.superclass.subclasses.push(this);
	}
	this.attributs = {};
	this.attrList = [];
	this.associations = {};
	this.assocList = [];
	this.subclasses = [];
};



JDY.base.ClassInfo.prototype.getSuperclass = function () {

	return this.superclass;
};


JDY.base.ClassInfo.prototype.forEachAttr = function (aFunc) {

	this.getAllAttributeList().forEach(aFunc);
};

JDY.base.ClassInfo.prototype.forEachAssoc = function (aFunc) {

	this.getAllAssociationList().forEach(aFunc);
};

JDY.base.ClassInfo.prototype.getAllAttributeList = function () {
	var tmpAttrList = this.attrList;

	if (this.superclass) {
		tmpAttrList = tmpAttrList.concat(this.superclass.getAllAttributeList());
	}

	return tmpAttrList;
};


JDY.base.ClassInfo.prototype.getAllAssociationList = function () {

	var tmpAssocList = this.assocList;

	if (this.superclass) {
		tmpAssocList = tmpAssocList.concat(this.superclass.getAllAssociationList());
	}

	return tmpAssocList;
};



JDY.base.ClassInfo.prototype.addAssociation = function (aAssoc) {

	if (this.associations[aAssoc.getAssocName()]) {
		throw new JDY.base.ValidationError("Associtaion already exists with name: " + aAssoc.getAssocName());
	}

	this.associations[aAssoc.getAssocName()] = aAssoc;
	this.assocList.push(aAssoc);
	return aAssoc;
};

JDY.base.ClassInfo.prototype.addReference = function (anInternalName, aReferencedClass) {

	var newAttr;
	newAttr = new JDY.base.ObjectReferenceInfo(anInternalName, anInternalName, false, false, aReferencedClass);
	this.addReferenceToAttrList(newAttr);
	return newAttr;
};


JDY.base.ClassInfo.prototype.addReferenceToAttrList = function (aObjRef) {

	if (this.attributs[aObjRef.getInternalName()]) {
		throw new JDY.base.ValidationError("Attribute already exists with name: " + aObjRef.getInternalName());
	}

	this.attributs[aObjRef.getInternalName()] = aObjRef;
	this.attrList.push(aObjRef);
};

JDY.base.ClassInfo.prototype.addPrimitiveAttr = function (aInternalName, aPrimitiveType) {

	var newAttr;
	if (this.attributs[aInternalName]) {
		throw new JDY.base.ValidationError("Attribute already exists with name: " + aInternalName);
	}

	newAttr = new JDY.base.PrimitiveAttributeInfo(aInternalName, aInternalName, false, false, aPrimitiveType);
	this.attributs[aInternalName] = newAttr;
	this.attrList.push(newAttr);
	return newAttr;
};


JDY.base.ClassInfo.prototype.addTextAttr = function (aInternalName, aLength, aDomainValueList) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.TextType(aLength, null, aDomainValueList));
};

JDY.base.ClassInfo.prototype.addEmailAttr = function (aInternalName, aLength, aDomainValueList) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.TextType(aLength, JDY.base.TextType.hint.EMAIL, aDomainValueList));
};

JDY.base.ClassInfo.prototype.addUrlAttr = function (aInternalName, aLength, aDomainValueList) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.TextType(aLength, JDY.base.TextType.hint.URL, aDomainValueList));
};

JDY.base.ClassInfo.prototype.addTelephoneAttr = function (aInternalName, aLength, aDomainValueList) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.TextType(aLength, JDY.base.TextType.hint.TELEPHONE, aDomainValueList));
};

JDY.base.ClassInfo.prototype.addBooleanAttr = function (aInternalName) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.BooleanType());
};

JDY.base.ClassInfo.prototype.addVarCharAttr = function (aInternalName, aLength) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.VarCharType(aLength, false));
};

JDY.base.ClassInfo.prototype.addDecimalAttr = function (aInternalName, aMinValue, aMaxValue, aScale, aDomainValueList) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.DecimalType(aMinValue, aMaxValue, aScale, aDomainValueList));
};

JDY.base.ClassInfo.prototype.addLongAttr = function (aInternalName, aMinValue, aMaxValue, aDomainValueList) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.LongType(aMinValue, aMaxValue, aDomainValueList));
};

JDY.base.ClassInfo.prototype.addTimeStampAttr = function (aInternalName, isDatePartUsed, isTimePartUsed) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.TimeStampType(isDatePartUsed, isTimePartUsed));
};

JDY.base.ClassInfo.prototype.addFloatAttr = function (aInternalName) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.FloatType());
};

JDY.base.ClassInfo.prototype.addBlobAttr = function (aInternalName) {

	return this.addPrimitiveAttr(aInternalName, new JDY.base.BlobType());
};

JDY.base.ClassInfo.prototype.getShortName = function () {

	return this.shortName;
};

JDY.base.ClassInfo.prototype.setShortName = function (aShortName) {
	this.shortName = aShortName;
	return this;
};

JDY.base.ClassInfo.prototype.setAbstract = function (newValue) {
	this.isAbstract = newValue;
	return this;
};

JDY.base.ClassInfo.prototype.getInternalName = function () {

	return this.internalName;
};

JDY.base.ClassInfo.prototype.getAssoc = function (aAssocName) {

	return this.associations[aAssocName];
};

JDY.base.ClassInfo.prototype.getAttr = function (aInternalName) {

	return this.attributs[aInternalName];
};

JDY.base.ClassInfo.prototype.getNameSpace = function () {

	return this.nameSpace;
};

JDY.base.ClassInfo.prototype.getRepoName = function () {

	return this.repoName;
};


JDY.base.ClassInfo.prototype.getAllSubclasses = function () {

	return this.subclasses;
};

JDY.base.addSubclasses = function addSubclasses(aClassInfo, resultList) {

	var curSubclass, i;

	if (aClassInfo.getAllSubclasses() &&
			aClassInfo.getAllSubclasses().length > 0) {

		for (i = 0; i < aClassInfo.getAllSubclasses().length; i++) {
			curSubclass = aClassInfo.getAllSubclasses()[i];
			resultList.push(curSubclass);
			JDY.base.addSubclasses(curSubclass, resultList);
		}
	}
	return resultList;
};


JDY.base.ClassInfo.prototype.getSubclassesRec = function () {

	var resultList = [];

	JDY.base.addSubclasses(this, resultList);

	return resultList;
};



JDY.base.AssociationModel = function (aMasterClassRef, aDetailClass, anAssocName) {

	this.detailClass = aDetailClass;
	this.masterClassReference = aMasterClassRef;
	this.assocName = anAssocName;
};

JDY.base.AssociationModel.prototype.getAssocName = function () {

	return this.assocName;
};

JDY.base.AssociationModel.prototype.getDetailClass = function () {

	return this.detailClass;
};

JDY.base.AssociationModel.prototype.getMasterClassReference = function () {

	return this.masterClassReference;
};



JDY.base.AttributeInfo = function (aInternalName,  aExternalName,  isKey, isNotNull) {

	this.internalName = aInternalName;
	this.externalName = aExternalName;
	this.key = isKey;
	this.isNotNull = isNotNull;
	this.isGenerated = null;
	this.attrGroup = null;
	this.pos = null;
};

JDY.base.AttributeInfo.prototype.getInternalName = function () {

	return this.internalName;
};


JDY.base.AttributeInfo.prototype.isPrimitive = function () {

    return (this.primitive) ? this.primitive : false;
};

JDY.base.AttributeInfo.prototype.isKey = function () {

    return (this.key) ? this.key : false;
};

JDY.base.AttributeInfo.prototype.setIsKey = function (isKey) {
	this.key = isKey;
	return this;
};

JDY.base.AttributeInfo.prototype.setNotNull = function (isNotNull) {

	this.isNotNull = isNotNull;
	return this;
};

JDY.base.AttributeInfo.prototype.setGenerated = function (isGenerated) {

	this.isGenerated = isGenerated;
	return this;
};


JDY.base.AttributeInfo.prototype.setExternalName = function (aExternalName) {

	this.externalName = aExternalName;
	return this;
};

JDY.base.AttributeInfo.prototype.setAttrGroup = function (anAttrGroup) {

	this.attrGroup = anAttrGroup;
	return this;
};

JDY.base.AttributeInfo.prototype.setPos = function (aPos) {

	this.pos = aPos;
	return this;
};

JDY.base.ObjectReferenceInfo = function (aInternalName, aExternalName, isKeyFlag, isNotNullFlag, aReferencedClass) {

	JDY.base.AttributeInfo.apply(this, arguments);
	this.referencedClass = aReferencedClass;
	this.inAssociation = null;
	this.dependent = null;
	this.primitive = false;
};

JDY.inherit(JDY.base.ObjectReferenceInfo, JDY.base.AttributeInfo);

JDY.base.ObjectReferenceInfo.prototype.getReferencedClass = function () {

	return this.referencedClass;
};



JDY.base.ObjectReferenceInfo.prototype.setIsDependent = function (isDependent) {

	this.dependent = isDependent;
	return this;
};

JDY.base.ObjectReferenceInfo.prototype.setIsInAssociation = function (inAssociation) {

	this.inAssociation = inAssociation;
	return this;
};


JDY.base.PrimitiveAttributeInfo = function (aInternalName,  aExternalName,  isKey, isNotNull, aType) {

	JDY.base.AttributeInfo.apply(this, arguments);
	this.type = aType;
	this.primitive = true;
};

JDY.inherit(JDY.base.PrimitiveAttributeInfo, JDY.base.AttributeInfo);

JDY.base.AttributeInfo.prototype.getType = function () {

	return this.type;
};

JDY.base.PrimitiveType = function () {
};

JDY.base.BlobType = function () {
	this.typeHint = null;
};

JDY.base.BlobType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleBlob(this);
	this.$type = "BlobType";
};

JDY.base.FloatType = function () {
};

JDY.base.FloatType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleFloat(this);
	this.$type = "FloatType";
};

JDY.base.BooleanType = function () {
};

JDY.base.BooleanType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleBoolean(this);
	this.$type = "BooleanType";

};


JDY.base.LongType = function (aMinValue, aMaxValue, aDomainValueList) {

	this.minValue = aMinValue;
	this.maxValue = aMaxValue;
	this.domainValues = aDomainValueList;
	this.$type = "LongType";

};

JDY.base.LongType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleLong(this);
};

JDY.base.DecimalType = function (aMinValue, aMaxValue, aScale, aDomainValueList) {

	this.minValue = aMinValue;
	this.maxValue = aMaxValue;
	this.scale = aScale;
	this.domainValues = aDomainValueList;
	this.$type = "DecimalType";
	
};

JDY.base.DecimalType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleDecimal(this);
};

JDY.base.TextType = function (aLength, aTypeHint, aDomainValueList) {

	this.length = aLength;
	this.typeHint = aTypeHint;
	this.domainValues = [];
	this.domainValues = aDomainValueList;
	this.$type = "TextType";
};

JDY.base.TextType.hint = {};
JDY.base.TextType.hint.EMAIL = "EMAIL";
JDY.base.TextType.hint.TELEPHONE = "TELEPHONE";
JDY.base.TextType.hint.URL = "URL";

JDY.base.TextType.prototype.setTypeHint = function(aTypeHint) {
	this.typeHint = aTypeHint;
};

JDY.base.TextType.prototype.handlePrimitiveKey = function (aHandler) {

    return aHandler.handleText(this);
};

JDY.base.TimeStampType = function (isDatePartUsed, isTimePartUsed) {

    this.datePartUsed = isDatePartUsed;
    this.timePartUsed = isTimePartUsed;
	this.$type = "TimeStampType";
};

JDY.base.TimeStampType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleTimeStamp(this);
};


JDY.base.VarCharType = function (aLength, isClobFlag) {

	this.length = aLength;
	this.mimeType = null;
	this.clob = isClobFlag;
	this.$type = "VarCharType";

};

JDY.base.VarCharType.prototype.handlePrimitiveKey = function (aHandler) {

	return aHandler.handleVarChar(this);
};


JDY.base.ObjectList = function (anAssocInfo) {

	this.assocObj = anAssocInfo;
	this.objects = [];

};

JDY.base.ObjectList.prototype.done = function (anCallback) {
	
	anCallback(this.objects);
};

JDY.base.ObjectList.prototype.add = function (anValueObject) {
	
	this.objects.push(anValueObject);
};

JDY.base.ProxyObjectList = function (anAssocInfo, aProxyResolver, aMasterObj) {

	this.objects = null;
	this.assocObj = anAssocInfo;
	this.proxyResolver = aProxyResolver;
	this.masterObject = aMasterObj;
	this.promise = null;

};
JDY.base.ProxyObjectList.prototype.done = function (anCallback) {
	
	var dfrd,
		that = this;
	
	if(!this.promise) {

        dfrd = $.Deferred();
        this.promise = dfrd.promise();
        if(this.proxyResolver) {
            
            this.proxyResolver.resolveAssoc(that.assocObj,that.masterObject, function(aAssocList){
				that.objects = aAssocList;
				dfrd.resolve(aAssocList);
            });
        } else {
          dfrd.reject("Proxy Error: no proxy resolver " + assocName);		
        }
    }
	
	this.promise.done(anCallback);
};

JDY.base.ProxyObjectList.prototype.add = function (anValueObject) {
	
	this.objects.push(anValueObject);
};

JDY.base.TypedValueObject = function (aClassInfo, aProxyResolver, asProxy) {
	
	var that = this;
	this.$typeInfo = aClassInfo;
	this.$assocs = {};
	this.$proxyResolver = aProxyResolver;
	
	if(asProxy) {
		this.$proxy = {};
	}
	
	aClassInfo.forEachAttr(function (curAttrInfo) {

		that[curAttrInfo.getInternalName()] = null;
	});

	aClassInfo.forEachAssoc(function (curAssocInfo) {

		that.$assocs[curAssocInfo.getAssocName()] = new JDY.base.ProxyObjectList(curAssocInfo, that.$proxyResolver, that);
	});
};

JDY.base.TypedValueObject.prototype.setAssocVals = function (anAssoc, anObjectList) {

    var assocName = (typeof anAssoc === "string") ? anAssoc : anAssoc.getAssocName();
	this.$assocs[assocName] = anObjectList;
};

JDY.base.TypedValueObject.prototype.assocVals = function (anAssoc) {

    var assocName = (typeof anAssoc === "string") ? anAssoc : anAssoc.getAssocName(),
		assocObj = this.$assocs[assocName];
    return assocObj;
};

JDY.base.TypedValueObject.prototype.val = function (anAttr) {

	return this[(typeof anAttr === "string") ? anAttr : anAttr.getInternalName()];
};

JDY.base.TypedValueObject.prototype.setVal = function (anAttr, aValue) {

	this[anAttr.getInternalName()] = aValue;
};

JDY.base.TypedValueObject.prototype.setProxyVal = function (anAttr, aValue) {

	this.$proxy[anAttr.getInternalName()] = aValue;
};

JDY.base.ClassInfoQuery = function (aTypeInfo, aFilterExpr) {
	this.resultType = aTypeInfo;
	this.filterExpression = aFilterExpr;
};

JDY.base.ClassInfoQuery.prototype.getResultInfo = function () {
	return this.resultInfo;
};

JDY.base.ClassInfoQuery.prototype.matchesObject = function (aModel) {
	return this.filterExpression === null
			|| this.filterExpression.matchesObject(aModel);	
};

JDY.base.ClassInfoQuery.prototype.getFilterExpression = function () {
	return this.filterExpression;
};

JDY.base.ClassInfoQuery.prototype.setFilterExpression  = function (aExpression) {
	filterExpression = aExpression;
};

JDY.base.AndExpression = function(aExprVect){

	this.expressionVect = aExprVect;
	this.$exprType = "AndExpression";
};

JDY.base.AndExpression.prototype.visit = function (aVisitor){
	return aVisitor.visitAndExpression(this);
};

JDY.base.AndExpression.prototype.matchesObject = function ( aModel ) {
		
	var matches = true;
	for (i = 0; i < this.expressionVect.length; i++) {
		matches = this.expressionVect[i].matchesObject(aModel);
	}
	return matches;
};
	

JDY.base.OrExpression = function(aExprVect){

	this.expressionVect = aExprVect;
	this.$exprType = "OrExpression";
};

JDY.base.OrExpression.prototype.visit = function (aVisitor){
	return aVisitor.visitOrExpression(this);
};

JDY.base.OrExpression.prototype.matchesObject = function ( aModel ) {
		
	var matches = true;
	for (i = 0; i < this.expressionVect.length; i++) {
		matches = this.expressionVect[i].matchesObject(aModel);
	}
	return matches;
};


JDY.base.OperatorExpression = function(aExprVect){

	this.myOperator = aExprVect;
	this.attributeInfo = aExprVect;
	this.compareValue = aExprVect;	
};

JDY.base.OperatorExpression.prototype.visit = function (aVisitor){
	return aVisitor.visitOperatorExpression(this);
};

JDY.base.OperatorExpression.prototype.matchesObject = function ( aModel ) {
		
	var modelValue = aModel.getValue(this.attributeInfo);
	return this.myOperator.compareValues(modelValue, this.compareValue, this.attributeInfo);
};

JDY.base.OperatorExpression.prototype.getOperator = function ( ) {
		
	return this.myOperator;
};

JDY.base.EqualOperator = function(notEqual){

	this.isNotEqual = (notEqual) ? true : false;
};

JDY.base.EqualOperator.prototype.visitOperatorHandler = function (aVisitor){
	
	return aVisitor.visitEqualOperator(this);
};

JDY.base.EqualOperator.prototype.compareValues = function ( value1, value2, attributeInfo ) {
		
    var result = false;
	if ( value1 !== null && value2 !== null) {
		result =  attributeInfo.compareObjects( value1, value2) === 0;
		if( isNotEqual) {
			result = !result;
		}
	}
	return result;
};

JDY.base.EqualOperator.prototype.toString = function () {

    return (this.isNotEqual) ? "<>" : "=";
};

JDY.base.GreatorOperator = function(alsoEqual){

    this.isAlsoEqual = (alsoEqual) ? true : false;
};

JDY.base.GreatorOperator.prototype.visitOperatorHandler = function (aVisitor){
	
	return aVisitor.visitGreatorOperator(this);
};

JDY.base.GreatorOperator.prototype.compareValues = function ( value1, value2, attributeInfo ) {
		
    var result = false;
	if ( value1 !== null && value2 !== null) {
		result =  attributeInfo.compareObjects( value1, value2) > 0;
		if( isAlsoEqual) {
			result =  result && attributeInfo.compareObjects( value1, value2) === 0;
		}
	}
	return result;
};

JDY.base.GreatorOperator.prototype.toString = function () {

    return (this.isAlsoEqual) ? ">=" : ">";
};

JDY.base.LessOperator = function(alsoEqual) {

    this.isAlsoEqual = (alsoEqual) ? true : false;
};

JDY.base.LessOperator.prototype.visitOperatorHandler = function (aVisitor){
	
	return aVisitor.visitLessOperator(this);
};

JDY.base.LessOperator.prototype.compareValues = function ( value1, value2, attributeInfo ) {
		
    var result = false;
	if ( value1 !== null && value2 !== null) {
		result =  attributeInfo.compareObjects( value1, value2) < 0;
		if( isAlsoEqual) {
			result =  result && attributeInfo.compareObjects( value1, value2) === 0;
		}
	}
	return result;
};

JDY.base.LessOperator.prototype.toString = function () {

    return (this.isAlsoEqual) ? "<=" : "<";
};

JDY.base.FilterCreationException = function (message) {
    this.name = "FilterCreationException";
    this.message = message || "FilterCreationException";
};

JDY.base.QueryCreator = function(aResultInfo){

	this.resultInfo = aResultInfo;
	this.createdExpr = null;

};

JDY.base.QueryCreator.prototype.query = function (){
	
	return new JDY.base.ClassInfoQuery(this.resultInfo, this.createdExpr);
};

JDY.base.QueryCreator.prototype.greater = function ( anExAttrName, aCompareValue ) {
		
	this.addOperatorExpression(anExAttrName, aCompareValue, new JDY.base.GreatorOperator());
	return this;
};

JDY.base.QueryCreator.prototype.greaterOrEqual = function ( anExAttrName, aCompareValue ) {
		
	this.addOperatorExpression(anExAttrName, aCompareValue, new JDY.base.GreatorOperator(true));
	return this;
};

JDY.base.QueryCreator.prototype.less = function ( anExAttrName, aCompareValue ) {
		
	this.addOperatorExpression(anExAttrName, aCompareValue, new JDY.base.LessOperator());
	return this;
};

JDY.base.QueryCreator.prototype.lessOrEqual = function ( anExAttrName, aCompareValue ) {
		
	this.addOperatorExpression(anExAttrName, aCompareValue, new JDY.base.LessOperator(true));
	return this;
};


JDY.base.QueryCreator.prototype.equal = function ( anExAttrName, aCompareValue ) {
		
	this.addOperatorExpression(anExAttrName, aCompareValue, new JDY.base.EqualOperator());
	return this;
};

JDY.base.QueryCreator.prototype.notEqual = function ( anExAttrName, aCompareValue ) {
		
	this.addOperatorExpression(anExAttrName, aCompareValue, new JDY.base.EqualOperator(true));
	return this;
};

JDY.base.QueryCreator.prototype.addOperatorExpression = function ( anAttrName, aCompareValue, aOperator) {
		
	var opExpr = new JDY.base.OperatorExpression();
	opExpr.attributeInfo = this.resultInfo.getAttr(anAttrName);
	opExpr.compareValue = aCompareValue;
	opExpr.myOperator = aOperator;
	this.addExpression(opExpr);
	return this;
};

JDY.base.QueryCreator.prototype.addExpression = function ( anExpr ) {
		
	this.createdExpr = anExpr;
	return this;
};

JDY.base.QueryCreator.prototype.and = function () {
		
	return new JDY.base.AndQueryCreator(this.resultInfo, this);
};

JDY.base.QueryCreator.prototype.or = function ( ) {
		
	return new JDY.base.OrQueryCreator(this.resultInfo, this);
};

JDY.base.QueryCreator.prototype.end = function () {
		
	throw new JDY.base.FilterCreationException("No Multiple Expression open");
};


JDY.base.AndQueryCreator = function (aResultInfo, aParentCreator) {

	JDY.base.AttributeInfo.apply(this, arguments);
	this.resultInfo = aResultInfo;
	this.parentCreator = aParentCreator;
	this.expressions = [];
};

JDY.inherit(JDY.base.AndQueryCreator, JDY.base.QueryCreator);


JDY.base.AndQueryCreator.prototype.addExpression = function ( anExpr ) {
		
	this.expressions.push(anExpr);
};


JDY.base.AndQueryCreator.prototype.query = function (){
	
	throw new FilterCreationException("And not closes");
};


JDY.base.AndQueryCreator.prototype.end = function () {
		
	this.parentCreator.addExpression(new JDY.base.AndExpression(this.expressions));
	return this.parentCreator;
};

JDY.base.OrQueryCreator = function (aResultInfo, aParentCreator) {

	JDY.base.AttributeInfo.apply(this, arguments);
	this.resultInfo = aResultInfo;
	this.parentCreator = aParentCreator;
	this.expressions = [];
};

JDY.inherit(JDY.base.OrQueryCreator, JDY.base.QueryCreator);


JDY.base.OrQueryCreator.prototype.addExpression = function ( anExpr ) {
		
	this.expressions.push(anExpr);
};


JDY.base.OrQueryCreator.prototype.query = function (){
	
	throw new FilterCreationException("Or not closes");
};


JDY.base.OrQueryCreator.prototype.end = function () {
		
	this.parentCreator.addExpression(new JDY.base.OrExpression(this.expressions));
	return this.parentCreator;
};

