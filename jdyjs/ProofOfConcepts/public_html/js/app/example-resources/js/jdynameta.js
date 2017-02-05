/* 
 * jdynameta, 
 * Copyright (c)2013 Rainer Schneider, Roggenburg.
 * Distributed under Apache 2.0 license
 * http://jdynameta.de
 */

var JDY = JDY || {};

JDY.db = {};

// http://www.taffydb.com/
JDY.db.TaffyObjectReaderWriter = function () {
	"use strict";

	this.repoName2DbMap = {};
};

JDY.db.TaffyObjectReaderWriter.prototype.loadValuesFromDb = function (aFilter, successFunct, failFunc) {
	"use strict";

	var typeDb = this.getDbForType(aFilter.resultType);

	successFunct(typeDb().get());

};

JDY.db.TaffyObjectReaderWriter.prototype.insertObjectInDb = function (aObjToInsert, successFunct, failFunc) {
	"use strict";
	var typeDb = this.getDbForType(aObjToInsert.$typeInfo);
	typeDb.insert(aObjToInsert);
	if(successFunct) {
		successFunct(aObjToInsert);
	}

};

JDY.db.TaffyObjectReaderWriter.prototype.deleteObjectInDb  = function (aObjToDelete, aClassInfo, successFunct, failFunc) {

	if(failFunc) {
		failFunc("not implemented");
	}
};

JDY.db.TaffyObjectReaderWriter.prototype.updateObjectInDb = function (aObjToUpdate, successFunct, failFunc) {
	"use strict";
	if(failFunc) {
		failFunc("not implemented");
	}
};

JDY.db.TaffyObjectReaderWriter.prototype.createNewObject = function (aTypeInfo) {
	"use strict";
	return new JDY.base.TypedValueObject(aTypeInfo);
};

JDY.db.TaffyObjectReaderWriter.prototype.getDbForType = function (aType) {
	"use strict";
	
	var repoDb = this.repoName2DbMap[aType.getRepoName()],
		typeDb;
	
	if(!repoDb) {
		repoDb = {};
		this.repoName2DbMap[aType.getRepoName()] = repoDb;
	}
	
	typeDb = repoDb[aType.getInternalName()];
	if(!typeDb) {
		typeDb = TAFFY();
		repoDb[aType.getInternalName()] = typeDb;
	}
	
	return typeDb;
};

JDY.app = {};

JDY.app.getRepositoryHandlers = function (reader) {
	
	var appRep = JDY.meta.createAppRepository();
	
	
	function loadTypeInfoFunc(aRepoName, aClassName, callback) {
		var repFilter = new JDY.base.QueryCreator(
					appRep.getClassInfo("AppRepository")).equal("applicationName",aRepoName).query(),
			newRepository;
			
		reader.loadValuesFromDb(repFilter, function(resultRepos){

			newRepository = JDY.meta.convertAppRepositoryToRepository(resultRepos[0]),
			callback(newRepository.getClassInfo(aClassName));
		});
	};

	function loadAllReposFunc(callBack) {
		
		var allRepoFilter = new JDY.base.QueryCreator(appRep.getClassInfo("AppRepository")).query();
		reader.loadValuesFromDb(allRepoFilter, callBack);
	};

	return {
		handleAllRepos: loadAllReposFunc,
		loadTypeInfo: loadTypeInfoFunc
	};
};

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


JDY.base.TypedValueObject = function (aClassInfo) {
	
	var that = this;
	this.$typeInfo = aClassInfo;
	this.$assocs = {};
	
	aClassInfo.forEachAttr(function (curAttrInfo) {

		that[curAttrInfo.getInternalName()] = null;
	});
};

JDY.base.TypedValueObject.prototype.assocVals = function (anAssoc, callback) {

	if (callback) {
		callback(this.$assocs[(typeof anAssoc === "string") ? anAssoc : anAssoc.getAssocName()]);
	} else {
		return this.$assocs[(typeof anAssoc === "string") ? anAssoc : anAssoc.getAssocName()];
	}
};

JDY.base.TypedValueObject.prototype.val = function (anAttr) {

	return this[(typeof anAttr === "string") ? anAttr : anAttr.getInternalName()];
};

JDY.base.TypedValueObject.prototype.setVal = function (anAttr, aValue) {

	this[anAttr.getInternalName()] = aValue;
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
	aVisitor.visitAndExpression(this);
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
	aVisitor.visitOrExpression(this);
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


// jdynameta, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true*/
/*global $*/

var JDY = JDY || {};

JDY.http = {};


JDY.http.JsonHttpObjectReader = function (aBasePath, aMetaRepoName) {
	"use strict";
	this.basepath = aBasePath;
	this.reader = new JDY.json.JsonFileReader();
	this.writer = new JDY.json.JsonFileWriter();
	this.filterCreator = new JDY.meta.FilterCreator(),
	this.att2AbbrMap = {};
	this.att2AbbrMap.repoName="rn";
	this.att2AbbrMap.className="cn";
	this.att2AbbrMap.expr="ex";
	this.att2AbbrMap.orSubExpr="ose";
	this.att2AbbrMap.andSubExpr="ase";
	this.att2AbbrMap.attrName="an";
	this.att2AbbrMap.operator="op";
	this.att2AbbrMap.isNotEqual="ne";
	this.att2AbbrMap.isAlsoEqual="ae";
	this.att2AbbrMap.longVal="lv";
	this.att2AbbrMap.textVal="tv";	
	this.jsonWriter = new JDY.json.JsonCompactFileWriter(this.att2AbbrMap);
};

JDY.http.JsonHttpObjectReader.prototype.loadValuesFromDb = function (aFilter, successFunct, failFunc) {
	"use strict";

	var uri = JDY.http.createUriForClassInfo(aFilter.resultType, JDY.meta.META_REPO_NAME, this.basepath),
		deferredCall,
		that = this,
		appQuery = this.filterCreator.convertMetaFilter2AppFilter(aFilter),
		expr;

	if (appQuery.expr) {
		expr = this.jsonWriter.writeObjectList( [appQuery.expr],JDY.json.Operation.INSERT);
		uri = uri +"?"+JDY.http.fixedEncodeURIComponent(JSON.stringify(expr));
	}

	deferredCall = this.createAjaxGetCall(uri);

	deferredCall.done(function (rtoData) {

		var resultObjects = that.reader.readObjectList(rtoData, aFilter.resultType);
		successFunct(resultObjects);
	});
	deferredCall.error(function (data) {
		if (failFunc) {
			failFunc();
		}
	});

};

JDY.http.fixedEncodeURIComponent = function (str) {
  return encodeURIComponent(str).replace(/[!'()]/g, escape).replace(/\*/g, "%2A");
};

JDY.http.JsonHttpObjectReader.prototype.createAjaxGetCall = function (aUrl) {
	"use strict";

	return $.ajax({
		url : aUrl,
		type : "GET",
		dataType: "json",
		contentType: "application/json"
	});
};



JDY.http.JsonHttpObjectWriter = function (aBasePath, aMetaModelRepoName) {
	"use strict";
	this.basepath = aBasePath;
	this.reader = new JDY.json.JsonFileReader();
	this.writer = new JDY.json.JsonFileWriter();

};

JDY.http.JsonHttpObjectWriter.prototype.deleteObjectInDb  = function (aObjToDelete, aClassInfo, successFunct, failFunc) {
	"use strict";

	var uri = JDY.http.createUriForClassInfo(aClassInfo, JDY.meta.META_REPO_NAME, this.basepath),
		params = JDY.http.createParametersFor(aObjToDelete, aClassInfo, "");

	uri = uri + "?" + $.param(params);
	this.sendJsonDeleteRequest(uri, successFunct, failFunc);
};

JDY.http.JsonHttpObjectWriter.prototype.insertObjectInDb = function (aObjToInsert, successFunct, failFunc) {
	"use strict";

	var singleElementList = [],
		result,
		content,
		that = this;


	singleElementList.push(aObjToInsert);

	content = this.writer.writeObjectList(singleElementList, 'INSERT');

	function handleResult(rtoData) {

		result = that.reader.readObjectList(rtoData, aObjToInsert.$typeInfo);
		successFunct(result[0]);

	}
	this.sendJsonPostRequest(JDY.http.createUriForClassInfo(aObjToInsert.$typeInfo, JDY.meta.META_REPO_NAME, this.basepath),
								JSON.stringify(content),
								handleResult,
								failFunc);
};

JDY.http.JsonHttpObjectWriter.prototype.updateObjectInDb = function (aObjToUpdate, successFunct, failFunc) {
	"use strict";

	var singleElementList = [],
		result,
		content,
		that = this;


	singleElementList.push(aObjToUpdate);

	content = this.writer.writeObjectList(singleElementList, 'UPDATE');

	function handleResult(rtoData) {

		result = that.reader.readObjectList(rtoData, aObjToUpdate.$typeInfo);
		successFunct(result[0]);

	}
	this.sendJsonPutRequest(JDY.http.createUriForClassInfo(aObjToUpdate.$typeInfo, JDY.meta.META_REPO_NAME, this.basepath),
								JSON.stringify(content),
								handleResult,
								failFunc);
};

JDY.http.JsonHttpObjectWriter.prototype.executeWorkflowAction = function (actionName, aObjToWorkOn, successFunct, failFunc) {
	"use strict";

	var singleElementList = [],
		result,
		content,
		that = this,
		uri;


	singleElementList.push(aObjToWorkOn);

	content = this.writer.writeObjectList(singleElementList, 'READ');

	function handleResult(rtoData) {

		result = that.reader.readObjectList(rtoData, aObjToWorkOn.$typeInfo);
		successFunct(result[0]);

	}
	
	uri = JDY.http.createUriForClassInfo(aObjToWorkOn.$typeInfo, JDY.meta.META_REPO_NAME, this.basepath);
	uri = uri +"?"+JDY.http.fixedEncodeURIComponent(actionName);
	this.sendJsonPutRequest(uri,
								JSON.stringify(content),
								handleResult,
								failFunc);

};


JDY.http.JsonHttpObjectWriter.prototype.sendJsonPostRequest  = function (uri, content, successFunct, failFunc) {
	"use strict";

	var deferredCall = this.createAjaxPostCall(uri, content);

	deferredCall.done(function (rtoData) {
		successFunct(rtoData);
	});
	deferredCall.error(function (data) {
		if (failFunc) {
			failFunc();
		}
	});
};
JDY.http.JsonHttpObjectWriter.prototype.sendJsonPutRequest  = function (uri, content, successFunct, failFunc) {
	"use strict";

	var deferredCall = this.createAjaxPutCall(uri, content);

	deferredCall.done(function (rtoData) {
		successFunct(rtoData);
	});
	deferredCall.error(function (data) {
		if (failFunc) {
			failFunc();
		}
	});
};


JDY.http.JsonHttpObjectWriter.prototype.sendJsonDeleteRequest  = function (uri, successFunct, failFunc) {
	"use strict";

	var deferredCall = this.createAjaxDeleteCall(uri);

	deferredCall.done(function (rtoData) {
		successFunct(rtoData);
	});
	deferredCall.error(function (data) {
		if (failFunc) {
			failFunc();
		}
	});
};

JDY.http.JsonHttpObjectWriter.prototype.createAjaxDeleteCall = function (aUrl) {
	"use strict";

	return $.ajax({
		url : aUrl,
		type : "DELETE",
		dataType: "json",
		contentType: "application/json"
	});
};

JDY.http.JsonHttpObjectWriter.prototype.createAjaxPostCall = function (aUrl, content) {
	"use strict";

	return $.ajax({
		url : aUrl,
		type : "POST",
		dataType: "json",
		contentType: "application/json",
		data: content
	});
};

JDY.http.JsonHttpObjectWriter.prototype.createAjaxPutCall = function (aUrl, content) {
	"use strict";

	return $.ajax({
		url : aUrl,
		type : "PUT",
		dataType: "json",
		contentType: "application/json",
		data: content
	});
};

JDY.http.createUriForClassInfo = function (aClassInfo, aMetaModelReponame, aBasePath) {
	"use strict";

	var reponame = aClassInfo.repoName,
		repoPart = "@jdy",// pseudo repo for meta information 
		infoPath = (aBasePath === null) ?  "" : aBasePath;

	if (reponame !== aMetaModelReponame) {
		repoPart = reponame;
	}
	// check whether path ends with /
	infoPath = (infoPath.charAt(infoPath.length - 1) === "/") ? infoPath : infoPath + "/";
	infoPath += repoPart + "/" + aClassInfo.getInternalName();

	return infoPath;
};

JDY.http.createParametersFor = function createParametersFor(aValueObj, aClassInfo, aPrefix) {
	"use strict";

	var nameValuePairs = [],
		refObjParams,
		curValue;

	aClassInfo.forEachAttr(function (curAttrInfo) {

		if (curAttrInfo.isKey()) {
			if (curAttrInfo.isPrimitive()) {

				curValue = curAttrInfo.getType().handlePrimitiveKey(JDY.http.parameterGetVisitor(aValueObj.val(curAttrInfo)));
				nameValuePairs.push({name: aPrefix + curAttrInfo.getInternalName(), value: curValue});
			} else {

				if (typeof aValueObj.val(curAttrInfo) === "object") {
					refObjParams = JDY.http.createParametersFor(aValueObj.val(curAttrInfo),
														curAttrInfo.getReferencedClass(),
														aPrefix + curAttrInfo.getInternalName() + ".");
					nameValuePairs = nameValuePairs.concat(refObjParams);
				} else {
					throw new JDY.base.JdyPersistentException("Wrong type for attr value (no object): " + curAttrInfo.getInternalName());
				}
			}
		}
	});

	return nameValuePairs;
};

JDY.http.parameterGetVisitor = function (aAttrValue) {
	"use strict";

	return {

		handleBoolean: function (aType) {
			return aAttrValue.toString();
		},

		handleDecimal: function (aType) {
			return aAttrValue.toString();
		},

		handleTimeStamp: function (aType) {
			return aAttrValue.toISOString();
		},

		handleFloat: function (aType) {
			return aAttrValue.toString();
		},

		handleLong: function (aType) {
			return aAttrValue.toString();
		},

		handleText: function (aType) {
			return aAttrValue;
		},

		handleVarChar: function (aType) {
			return aAttrValue;
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};

JDY.http.JsonHttpPersistentService = function (aBasePath, aMetaRepoName) {

	this.reader = new JDY.http.JsonHttpObjectReader(aBasePath);
	this.writer =  new JDY.http.JsonHttpObjectWriter(aBasePath);

};

JDY.http.JsonHttpPersistentService.prototype.loadValuesFromDb = function (aFilter, successFunct, failFunc) {
	"use strict";
	
	this.reader.loadValuesFromDb(aFilter, successFunct, failFunc);
};

JDY.http.JsonHttpPersistentService.prototype.deleteObjectInDb  = function (aObjToDelete, aClassInfo, successFunct, failFunc) {
	"use strict";
	
	this.writer.deleteObjectInDb(aObjToDelete, aClassInfo, successFunct, failFunc);
};

JDY.http.JsonHttpPersistentService.prototype.insertObjectInDb = function (aObjToInsert, successFunct, failFunc) {
	"use strict";
	
	this.writer.insertObjectInDb(aObjToInsert, successFunct, failFunc);
};

JDY.http.JsonHttpPersistentService.prototype.updateObjectInDb = function (aObjToUpdate, successFunct, failFunc) {
	"use strict";
	
	this.writer.updateObjectInDb(aObjToUpdate, successFunct, failFunc);
};


JDY.http.JsonHttpPersistentService.prototype.executeWorkflowAction = function (actionName, aObjToWorkOn, successFunct, failFunc) {
	"use strict";
	this.writer.executeWorkflowAction(actionName, aObjToWorkOn, successFunct, failFunc);

};

JDY.http.JsonHttpPersistentService.prototype.createNewObject = function (aTypeInfo) {
	"use strict";
	return new JDY.base.TypedValueObject(aTypeInfo);
};


 // jdynameta, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de

/*jslint plusplus: true */

// initialize global jdy namespace
var JDY = JDY || {};

JDY.json = {};

JDY.json.CLASS_INTERNAL_NAME_TAG = "@classInternalName";
JDY.json.NAMESPACE_TAG = "@namespace";
JDY.json.PERSISTENCE_TAG = "@persistence";

JDY.json.COMPACT_TYPE_TAG = "@t";
JDY.json.COMPACT_PERSISTENCE_TAG = "@p";

JDY.json.Operation = {};
JDY.json.Operation.PROXY = "PROXY";
JDY.json.Operation.INSERT = "INSERT";
JDY.json.Operation.UPDATE = "UPDATE";
JDY.json.Operation.DELETE = "DELETE";
JDY.json.Operation.READ = "READ";


JDY.json.JsonFileReader = function () {
	"use strict";
};

JDY.json.JsonFileReader.prototype.readObjectList = function (aJsonNode, aClassInfo) {
	"use strict";

	var resultList = [],
		i;

	if (Array.isArray(aJsonNode)) {

		for (i = 0; i < aJsonNode.length; i++) {

			if (typeof aJsonNode[i] === "object") {
				resultList.push(this.createModelForJsonObj(aJsonNode[i], aClassInfo));
			} else {
				throw new JDY.base.JdyPersistentException("Error parsing JSON. No JSONObject: " + aJsonNode[i].toString());
			}
		}
	}
	return resultList;
};

JDY.json.JsonFileReader.prototype.createModelForJsonObj = function createModelForJsonObj(aJsonNode, aClassInfo) {
	"use strict";

	var concreteClass = this.createClassInfoFromMeta(aJsonNode, aClassInfo),
		persistenceType = aJsonNode[JDY.json.PERSISTENCE_TAG],
		result = null,
		attrValue,
		isNew,
		that = this;


	result = new JDY.base.TypedValueObject(concreteClass, false);
	concreteClass.forEachAttr(function (curAttrInfo) {

		if (persistenceType !== JDY.json.Operation.PROXY || curAttrInfo.isKey()) {
			attrValue = aJsonNode[curAttrInfo.getInternalName()];

			if (attrValue === undefined) {
				throw new JDY.base.JdyPersistentException("Missing value for type in attr value: " + curAttrInfo.getInternalName());
			} else {
				if (attrValue !== null) {

					if (curAttrInfo.isPrimitive()) {

						result[curAttrInfo.getInternalName()] = curAttrInfo.getType().handlePrimitiveKey(that.jsonValueGetVisitor(attrValue));
					} else {

						if (typeof attrValue === "object") {
							result[curAttrInfo.getInternalName()] = that.createModelForJsonObj(attrValue, curAttrInfo.getReferencedClass());
						} else {
							throw new JDY.base.JdyPersistentException("Wrong type for attr value (no object): " + curAttrInfo.getInternalName());
						}
					}
				} else {
					result[curAttrInfo.getInternalName()] =  null;
				}
			}
		}
	});
	if (persistenceType !== JDY.json.Operation.PROXY) {

		concreteClass.forEachAssoc(function (curAssoc) {

			result.$assocs[curAssoc.getAssocName()] = that.createAssociationList(aJsonNode, result, curAssoc);
		});
	}

	return result;
};

JDY.json.JsonFileReader.prototype.createAssociationList = function (aMasterNode, aMasterObj, curAssoc) {
	"use strict";

	var objList = [],
		i,
		assocNode = aMasterNode[curAssoc.getAssocName()];

	if (assocNode === null) {
		objList = [];
	} else {
		objList = [];
		if (Array.isArray(assocNode)) {
			for (i = 0; i < assocNode.length; i++) {
				if (typeof assocNode[i] === "object") {
					objList.push(this.createModelForJsonObj(assocNode[i], curAssoc.getDetailClass()));
				} else {
					throw new JDY.base.JdyPersistentException("Error parsing JSON. No JSONObject: ");
				}
			}
		} else {
			throw new JDY.base.JdyPersistentException("Wrong type for assoc value (no array): " + curAssoc.assocName);
		}
	}

	return objList;
};

JDY.json.JsonFileReader.prototype.createClassInfoFromMeta = function getConcreteClass(jsonObj, aClassInfo) {
	"use strict";

	var repoName = jsonObj[JDY.json.NAMESPACE_TAG],
		classInternalName = jsonObj[JDY.json.CLASS_INTERNAL_NAME_TAG];
	return this.getConcreteClass(aClassInfo, repoName, classInternalName);
};

JDY.json.JsonFileReader.prototype.getConcreteClass = function getConcreteClass(aClassInfo, aRepoName, classInternalName) {
	"use strict";

	var concreteClass = null,
		i,
		curClassInfo;

	if (aClassInfo.getInternalName() === classInternalName &&
			aClassInfo.getRepoName() === aRepoName) {
		concreteClass = aClassInfo;
	} else {
		for (i = 0; i < aClassInfo.getAllSubclasses().length; i++) {

			curClassInfo = aClassInfo.getAllSubclasses()[i];
			concreteClass = getConcreteClass(curClassInfo, aRepoName, classInternalName);
			if (concreteClass) {
				break;
			}
		}
	}

	return concreteClass;
};

JDY.json.JsonFileReader.prototype.jsonValueGetVisitor = function (aAttrValue) {
	"use strict";

	return {

		handleBoolean: function (aType) {

			if (typeof aAttrValue !== 'boolean') {
				throw new JDY.base.JdyPersistentException("Wrong type boolean : " + aAttrValue);
			}
			return aAttrValue;
		},

		handleDecimal: function (aType) {
			if (typeof aAttrValue !== 'number') {
				throw new JDY.base.JdyPersistentException("Wrong type long : " + aAttrValue);
			}
			return aType;
		},

		handleTimeStamp: function (aType) {
			return new Date(aAttrValue);
		},

		handleFloat: function (aType) {
			return aAttrValue;
		},

		handleLong: function (aType) {

			if (typeof aAttrValue !== 'number') {
				throw new JDY.base.JdyPersistentException("Wrong type long : " + aAttrValue);
			}
			return aAttrValue;
		},

		handleText: function (aType) {
			return aAttrValue;
		},

		handleVarChar: function (aType) {
			return aAttrValue;
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};

JDY.json.JsonFileWriter = function () {
	"use strict";
	this.writeStrategy = {
		isWriteAsProxy : function () {
			return true;
		}
	};
};

JDY.json.JsonFileWriter.prototype.writeObjectList = function (aJsonNode, aPersistenceType) {
	"use strict";

	var resultList = [],
		i;

	if (Array.isArray(aJsonNode)) {

		for (i = 0; i < aJsonNode.length; i++) {

			if (typeof aJsonNode[i] === "object") {
				resultList.push(this.writeObjectToJson(aJsonNode[i], aPersistenceType));
			} else {
				throw new JDY.base.JdyPersistentException("Error parsing JSON. No JSONObject: " + aJsonNode[i].toString());
			}
		}
	}
	return resultList;
};


JDY.json.JsonFileWriter.prototype.writeObjectToJson = function (objToWrite, aPersistenceType) {
	"use strict";

	var jsonObject = this.createClassInfoNode(objToWrite, aPersistenceType, false);
	return jsonObject;
};

JDY.json.JsonFileWriter.prototype.createClassInfoNode = function createClassInfoNode(objToWrite, aPersistenceType, asProxy) {
	"use strict";

	var jsonObject = {},
		attrValue,
		that = this,
		isProxy,
		refJsonNode;


	this.addMetaDataFields(jsonObject, objToWrite.$typeInfo, (asProxy) ? 'PROXY' : aPersistenceType);

	objToWrite.$typeInfo.forEachAttr(function (curAttrInfo) {

		if (!asProxy || curAttrInfo.isKey()) {
			attrValue = objToWrite[curAttrInfo.getInternalName()];

			if (attrValue === undefined) {
				throw new JDY.base.JdyPersistentException("Missing value for type in attr value: " + curAttrInfo.getInternalName());
			} else {
				if (attrValue !== null) {

					if (curAttrInfo.isPrimitive()) {

						jsonObject[curAttrInfo.getInternalName()] = curAttrInfo.getType().handlePrimitiveKey(that.jsonValueGetVisitor(attrValue));
					} else {
						isProxy = asProxy || that.writeStrategy.isWriteAsProxy();
						refJsonNode = that.createClassInfoNode(attrValue, aPersistenceType, isProxy);
						jsonObject[curAttrInfo.getInternalName()] =  refJsonNode;
					}
				} else {
					jsonObject[curAttrInfo.getInternalName()] =  null;
				}
			}
		}
	});




	objToWrite.$typeInfo.forEachAssoc(function (curAssoc) {
		if (!asProxy && !that.writeStrategy.isWriteAsProxy() ) { 
			jsonObject[curAssoc.getAssocName()] = that.createAssociationList(objToWrite.getValue(curAssoc), aPersistenceType);
		}
	});

	return jsonObject;
};


JDY.json.JsonFileWriter.prototype.addMetaDataFields = function (jsonObject, aClassInfo, aPersistenceType) {
	"use strict";

	jsonObject[JDY.json.NAMESPACE_TAG] =  aClassInfo.getRepoName();
	jsonObject[JDY.json.CLASS_INTERNAL_NAME_TAG] = aClassInfo.getInternalName();
	jsonObject[JDY.json.PERSISTENCE_TAG] = (aPersistenceType) ? aPersistenceType : "" ;
};

JDY.json.JsonFileWriter.prototype.jsonValueGetVisitor = function (aAttrValue) {
	"use strict";

	return {

		handleBoolean: function (aType) {

			if (typeof aAttrValue !== 'boolean') {
				throw new JDY.base.JdyPersistentException("Wrong type boolean : " + aAttrValue);
			}
			return aAttrValue;
		},

		handleDecimal: function (aType) {
			if (typeof aAttrValue !== 'number') {
				throw new JDY.base.JdyPersistentException("Wrong type long : " + aAttrValue);
			}
			return aType;
		},

		handleTimeStamp: function (aType) {
			return aAttrValue.toISOString();
		},

		handleFloat: function (aType) {
			return aAttrValue;
		},

		handleLong: function (aType) {

			if (typeof aAttrValue !== 'number') {
				throw new JDY.base.JdyPersistentException("Wrong type long : " + aAttrValue);
			}
			return aAttrValue;
		},

		handleText: function (aType) {
			return aAttrValue;
		},

		handleVarChar: function (aType) {
			return aAttrValue;
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};


JDY.json.JsonCompactFileWriter = function (aName2Abbr) {
	"use strict";
	this.writeStrategy = {
		isWriteAsProxy : function () {
			return false;
		}
	};
	
	this.writeNullValues = false;
	this.writeGenreatedAtr = false;
	this.writePersistence = false;
	this.name2Abbr = (aName2Abbr) ? aName2Abbr : {};
};


JDY.json.JsonCompactFileWriter.prototype.writeObjectList = function (aJsonNode, aPersistenceType, aAssocInfo) {
	"use strict";

	var resultList = [],
		i;

	if (Array.isArray(aJsonNode)) {

		for (i = 0; i < aJsonNode.length; i++) {

			if (typeof aJsonNode[i] === "object") {
				resultList.push(this.writeObjectToJson(aJsonNode[i], aPersistenceType, JDY.json.createAssocClmnVisibility(aAssocInfo)));
			} else {
				throw new JDY.base.JdyPersistentException("Error parsing JSON. No JSONObject: " + aJsonNode[i].toString());
			}
		}
	}
	return resultList;
};

JDY.json.JsonCompactFileWriter.prototype.writeObjectToJson = function (objToWrite, aPersistenceType, clmnVisibility) {
	"use strict";

	var jsonObject = this.createClassInfoNode(objToWrite, aPersistenceType, false, clmnVisibility);
	return jsonObject;
};

JDY.json.JsonCompactFileWriter.prototype.createClassInfoNode = function createClassInfoNode(objToWrite, aPersistenceType, asProxy, aClmnVisibility) {
	"use strict";

	var jsonObject = {},
		attrValue,
		that = this,
		isProxy,
		refJsonNode,
		clmnVisib = aClmnVisibility;


	this.addMetaDataFields(jsonObject, objToWrite.$typeInfo, (asProxy) ? 'PROXY' : aPersistenceType);
	console.log("t: "+ objToWrite.$typeInfo.internalName);
	objToWrite.$typeInfo.forEachAttr(function (curAttrInfo) {

		if( !clmnVisib || clmnVisib.isAttributeVisible(curAttrInfo)) {

			if (!asProxy || curAttrInfo.isKey()) {
				attrValue = objToWrite[curAttrInfo.getInternalName()];

				if (attrValue === undefined) {
					throw new JDY.base.JdyPersistentException("Missing value for type in attr value: " + curAttrInfo.getInternalName());
				} else {
					if (attrValue !== null) {

						if (curAttrInfo.isPrimitive()) {

							if( !curAttrInfo.isGenerated || that.writeGenreatedAtr) {
								jsonObject[that.nameForAttr(curAttrInfo)] = curAttrInfo.getType().handlePrimitiveKey(that.jsonValueGetVisitor(attrValue));
							}
						} else {
							console.log(curAttrInfo.getInternalName());

							isProxy = asProxy || that.writeStrategy.isWriteAsProxy();
							refJsonNode = that.createClassInfoNode(attrValue, aPersistenceType, isProxy);
							jsonObject[that.nameForAttr(curAttrInfo)] =  refJsonNode;
						}
					} else {
						if(that.writeNullValues) {
							jsonObject[that.nameForAttr(curAttrInfo)] =  null;
						}
					}
				}
			}
		}
	});


	objToWrite.$typeInfo.forEachAssoc(function (curAssoc) {
		if (!asProxy && !that.writeStrategy.isWriteAsProxy() ) { 

			jsonObject[that.nameForAssoc(curAssoc)] = that.writeObjectList(objToWrite.assocVals(curAssoc), aPersistenceType, curAssoc);
		}
	});

	return jsonObject;
};

JDY.json.JsonCompactFileWriter.prototype.addMetaDataFields = function (jsonObject, aClassInfo, aPersistenceType) {
	"use strict";

	jsonObject[JDY.json.COMPACT_TYPE_TAG] = aClassInfo.getShortName();

	if(this.writePersistence) {
		jsonObject[JDY.json.COMPACT_PERSISTENCE_TAG] = (aPersistenceType) ? aPersistenceType : "" ;
	}
};

JDY.json.JsonCompactFileWriter.prototype.nameForAssoc = function (anAssocInfo) {
	
	return (this.name2Abbr[anAssocInfo.getAssocName()]) 
				? this.name2Abbr[anAssocInfo.getAssocName()]
				: anAssocInfo.getAssocName();
};


JDY.json.JsonCompactFileWriter.prototype.nameForAttr = function (attrInfo) {
	
	return (this.name2Abbr[attrInfo.getInternalName()]) 
				? this.name2Abbr[attrInfo.getInternalName()] 
				: attrInfo.getInternalName();
};

JDY.json.JsonCompactFileWriter.prototype.jsonValueGetVisitor = function (aAttrValue) {
	"use strict";

	return {

		handleBoolean: function (aType) {

			if (typeof aAttrValue !== 'boolean') {
				throw new JDY.base.JdyPersistentException("Wrong type boolean : " + aAttrValue);
			}
			return aAttrValue;
		},

		handleDecimal: function (aType) {
			if (typeof aAttrValue !== 'number') {
				throw new JDY.base.JdyPersistentException("Wrong type long : " + aAttrValue);
			}
			return aType;
		},

		handleTimeStamp: function (aType) {
			return new Date(aAttrValue);
		},

		handleFloat: function (aType) {
			return aAttrValue;
		},

		handleLong: function (aType) {

			if (typeof aAttrValue !== 'number') {
				throw new JDY.base.JdyPersistentException("Wrong type long : " + aAttrValue);
			}
			return aAttrValue;
		},

		handleText: function (aType) {
			return aAttrValue;
		},

		handleVarChar: function (aType) {
			return aAttrValue;
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};


JDY.json.createAssocClmnVisibility = function(aAssocInfo) {
	
	return {
		
		isAttributeVisible: function(aAttrInfo) {

			return (!aAssocInfo) || (aAssocInfo.getMasterClassReference() !== aAttrInfo);
		}
	};
};
// jdynameta, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


var JDY = JDY || {};

JDY.meta = {};

JDY.meta.META_REPO_NAME= "ApplicationRepository";

JDY.meta.TextMimeType= [{dbValue:"XML", representation:"text/xml"},
						{dbValue:"HTML", representation:"text/html"},
						{dbValue:"PLAIN", representation:"text/plain"}];
JDY.meta.TypeHint= [{dbValue:"TELEPHONE", representation:"TELEPHONE"},
						{dbValue:"URL", representation:"URL"},
						{dbValue:"EMAIL", representation:"EMAIL"}];

JDY.meta.createAppRepository = function () {
	"use strict";
	var appRep = new JDY.base.Repository("ApplicationRepository"),
		repositoryModel,
		classInfoModel,
		attributeInfoModel,
		primitiveAttribute,
		objectReferenceInfoModel,
		associationInfoModel,
		booleanTypeModel,
		blobTypeModel,
		decimalTypeModel,
		floatTypeModel,
		longTypeModel,
		textTypeModel,
		timestampTypeModel,
		varcharTypeModel,
		decimalDomainValuesModel,
		longDomainValuesModel,
		stringDomainValuesModel;

	repositoryModel = appRep.addClassInfo("AppRepository").setShortName("REP");
	repositoryModel.addTextAttr("Name", 100).setNotNull(true);
	repositoryModel.addTextAttr("applicationName", 100).setIsKey(true).setGenerated(true);
	repositoryModel.addLongAttr("appVersion" ,0, 9999999).setGenerated(true).setNotNull(true);
	repositoryModel.addBooleanAttr("closed" ).setGenerated(true);

	classInfoModel = appRep.addClassInfo("AppClassInfo").setShortName("CLM");
	classInfoModel.addTextAttr("Name", 30).setNotNull(true);
	classInfoModel.addTextAttr("InternalName", 35).setIsKey(true).setExternalName("Internal").setGenerated(true);
	classInfoModel.addBooleanAttr("isAbstract").setNotNull(true);
	classInfoModel.addTextAttr("NameSpace", 100).setNotNull(true);
	classInfoModel.addVarCharAttr("beforeSaveScript", 4000);

	attributeInfoModel = appRep.addClassInfo("AppAttribute").setShortName("ATM").setAbstract(true);
	attributeInfoModel.addTextAttr("Name", 30).setNotNull(true);
	attributeInfoModel.addTextAttr("InternalName", 35).setIsKey(true).setGenerated(true);
	attributeInfoModel.addBooleanAttr("isKey").setNotNull(true);
	attributeInfoModel.addBooleanAttr("isNotNull").setNotNull(true);
	attributeInfoModel.addBooleanAttr("isGenerated").setNotNull(true);
	attributeInfoModel.addTextAttr("AttrGroup", 100);
	attributeInfoModel.addLongAttr("pos", 0, 999999999).setNotNull(true);

	primitiveAttribute = appRep.addClassInfo("AppPrimitiveAttribute", attributeInfoModel).setShortName("PAM").setAbstract(true);

	objectReferenceInfoModel = appRep.addClassInfo("AppObjectReference", attributeInfoModel).setShortName("ORM");
	objectReferenceInfoModel.addReference("referencedClass", classInfoModel).setNotNull(true);
	objectReferenceInfoModel.addBooleanAttr("isInAssociation").setNotNull(true).setGenerated(true);
	objectReferenceInfoModel.addBooleanAttr("isDependent").setNotNull(true).setGenerated(false);

	associationInfoModel = appRep.addClassInfo("AppAssociation").setShortName("AIM");
	associationInfoModel.addTextAttr("Name", 40).setNotNull(true);
	associationInfoModel.addTextAttr("nameResource", 45).setIsKey(true).setGenerated(true);
	associationInfoModel.addReference("masterClassReference", objectReferenceInfoModel).setNotNull(true).setIsDependent(true);

	booleanTypeModel = appRep.addClassInfo("AppBooleanType", primitiveAttribute).setShortName("BTM");
	booleanTypeModel.addLongAttr("temp", 0, 999999999);

	blobTypeModel = appRep.addClassInfo("AppBlobType", primitiveAttribute).setShortName("BLTM");
	blobTypeModel.addLongAttr("TypeHintId", 0, 999999999);

	decimalTypeModel = appRep.addClassInfo("AppDecimalType", primitiveAttribute).setShortName("CUM");
	decimalTypeModel.addLongAttr("Scale", 0, 10);
	decimalTypeModel.addDecimalAttr("MinValue", -999999999.99999, 999999999.99999, 3);
	decimalTypeModel.addDecimalAttr("MaxValue", -999999999.99999, 999999999.99999, 3);

	decimalDomainValuesModel = appRep.addClassInfo("AppDecimalDomainModel").setShortName("DDM");
	decimalDomainValuesModel.addTextAttr("representation", 100);
	decimalDomainValuesModel.addDecimalAttr("dbValue", Number.MIN_VALUE, Number.MAX_VALUE, 3).setIsKey(true);

	longDomainValuesModel = appRep.addClassInfo("AppLongDomainModel").setShortName("LDM");
	longDomainValuesModel.addTextAttr("representation", 100);
	longDomainValuesModel.addLongAttr("dbValue", -999999999, 99999999).setIsKey(true);

	stringDomainValuesModel= appRep.addClassInfo("AppStringDomainModel").setShortName("SDM");
	stringDomainValuesModel.addTextAttr("representation", 100);
	stringDomainValuesModel.addTextAttr("dbValue", 100).setIsKey(true);


	floatTypeModel = appRep.addClassInfo("AppFloatType", primitiveAttribute).setShortName("FTM");
	floatTypeModel.addLongAttr("Scale", 0, 20);
	floatTypeModel.addLongAttr("MaxValue", -999999999.99999, 99999999.99999);

	longTypeModel = appRep.addClassInfo("AppLongType", primitiveAttribute).setShortName("LTM");
	longTypeModel.addLongAttr("MinValue", -999999999, 99999999);
	longTypeModel.addLongAttr("MaxValue", -999999999, 99999999);

	textTypeModel = appRep.addClassInfo("AppTextType", primitiveAttribute).setShortName("TXM");
	textTypeModel.addLongAttr("length", 1, 1000).setNotNull(true);
	textTypeModel.addTextAttr("typeHint", 20, JDY.meta.TypeHint).setNotNull(false);

	timestampTypeModel = appRep.addClassInfo("AppTimestampType", primitiveAttribute).setShortName("TSM");
	timestampTypeModel.addBooleanAttr("isDatePartUsed").setNotNull(true);
	timestampTypeModel.addBooleanAttr("isTimePartUsed").setNotNull(true);

	varcharTypeModel = appRep.addClassInfo("AppVarCharType", primitiveAttribute).setShortName("VCM");
	varcharTypeModel.addLongAttr("length", 1, Number.MAX_VALUE);
	varcharTypeModel.addBooleanAttr("isClob").setNotNull(true);
	varcharTypeModel.addTextAttr("mimeType", 20, JDY.meta.TextMimeType).setNotNull(false);

	appRep.addAssociation("Attributes", classInfoModel, attributeInfoModel, "Masterclass", "Masterclass", true, true, true);
	appRep.addAssociation("Associations", classInfoModel, associationInfoModel, "Masterclass", "Masterclass", false, true, true);
	appRep.addAssociation("Subclasses", classInfoModel, classInfoModel, "Superclass", "Superclass", false, false, true);
	appRep.addAssociation("Classes", repositoryModel, classInfoModel, "Repository", "Repository", true, true, true);

	appRep.addAssociation("DomainValues", decimalTypeModel, decimalDomainValuesModel, "Type", "Type", true, true, true);
	appRep.addAssociation("DomainValues", textTypeModel, stringDomainValuesModel, "Type", "Type", true, true, true);
	appRep.addAssociation("DomainValues", longTypeModel, longDomainValuesModel, "Type", "Type", true, true, true);

	return appRep;
};

JDY.meta.convertAppRepositoryToRepository = function (appRepository) {

	var newRepository = new JDY.base.Repository(appRepository.applicationName),
		appClassInfos,
		appClassInfo,
		newClassInfoModel,
		i;

	appClassInfos = appRepository.assocVals("Classes");

	for (i = 0; i < appClassInfos.length; i++) {
		appClassInfo = appClassInfos[i];		
		newClassInfoModel = newRepository.addClassInfo(appClassInfo.InternalName).setAbstract(appClassInfo.isAbstract);
	}

	for (i = 0; i < appClassInfos.length; i++) {
		appClassInfo = appClassInfos[i];		
		JDY.meta.buildAttrForMetaRepo(newRepository, appClassInfo);
	}
	
	for (i = 0; i < appClassInfos.length; i++) {
		appClassInfo = appClassInfos[i];		
		JDY.meta.buildAssocsForMetaRepo(newRepository, appClassInfo);
	}

	for (i = 0; i < appClassInfos.length; i++) {
		appClassInfo = appClassInfos[i];		
		JDY.meta.buildSubclassesForMetaRepo(newRepository, appClassInfo);
	}
	
	return newRepository;
};

JDY.meta.addClassToMetaRepo = function(metaRepo, anAppClassInfo) {

	var newMetaClass = metaRepo.addClassInfo(anAppClassInfo.InternalName).setAbstract(anAppClassInfo.isAbstract);
	newMetaClass.setAbstract(anAppClassInfo.isAbstract);
	newMetaClass.setExternalName(anAppClassInfo.InternalName);
	newMetaClass.setShortName(anAppClassInfo.InternalName);
};

JDY.meta.buildAssocsForMetaRepo= function(metaRepo, anAppClassInfo)	{

	var i,
		appAttributes =  anAppClassInfo.assocVals("Associations"),
		appAssoc,
		metaMasterClass,
		metaClass = metaRepo.getClassInfo(anAppClassInfo.InternalName),
		metaMasterClassRef,
		appAssocName,
		metaAssoc,
		metaDetailClass;

	for (i = 0; i < appAttributes.length; i++) {

		appAssoc = appAttributes[i];

		metaMasterClass =  metaRepo.getClassInfo(appAssoc.masterClassReference.Masterclass.InternalName); 
		metaMasterClassRef = metaMasterClass.getAttr(appAssoc.masterClassReference.InternalName);
		appAssocName = appAssoc.NameResource;			
		metaAssoc = new JDY.base.AssociationModel(metaMasterClassRef, metaMasterClass, appAssocName);
		metaClass.addAssociation(metaAssoc);
	}
};

JDY.meta.getDetailClass= function(anAssoc)	{
	return (anAssoc.masterClassReference === null) ? null : anAssoc.masterClassReference.Masterclass;
};

JDY.meta.buildSubclassesForMetaRepo= function(metaRepo, anAppClassInfo)	{

	var metaClass = metaRepo.getClassInfo(anAppClassInfo.InternalName),
		appSuper = anAppClassInfo.Superclass,
		metaSuper;

	if(appSuper) {
		metaSuper = metaRepo.getClassInfo(appSuper.InternalName);
		metaSuper.addSubclass(metaClass);
	}
};
	
JDY.meta.buildAttrForMetaRepo= function(metaRepo, anAppClassInfo)	{

	var i,j,
		appAttributes =  anAppClassInfo.assocVals("Attributes"),
		appAttr,
		metaAttr,
		metaClass = metaRepo.getClassInfo(anAppClassInfo.InternalName),
		refClass,
		appDomainVals,
		domainVals;


	for (i = 0; i < appAttributes.length; i++) {

		appAttr = appAttributes[i];

		switch(appAttr.$typeInfo.internalName) {

			case 'AppBooleanType':
					metaAttr = metaClass.addBooleanAttr(appAttr.InternalName);
				break;
			case 'AppBlobType':
					metaAttr = metaClass.addBlobAttr(appAttr.InternalName);
				break;
			case 'AppDecimalType':
					appDomainVals = appAttr.$assocs.DomainValues;
					domainVals = [];
					if (appDomainVals) {
						for (j = 0; j < appDomainVals.length; j++) {
							domainVals.push({dbValue:appDomainVals[j].dbValue, representation:appDomainVals[j].representation});
						}
					}
					metaAttr = metaClass.addDecimalAttr(appAttr.InternalName, appAttr.MinValue , appAttr.MaxValue, appAttr.Scale, domainVals);
				break;
			case 'AppFloatType':
					metaAttr = metaClass.addFloatAttr(appAttr.InternalName);
				break;
			case 'AppLongType':
					appDomainVals = appAttr.$assocs.DomainValues;
					domainVals = [];
					if (appDomainVals) {
						for (j = 0; j < appDomainVals.length; j++) {
							domainVals.push({dbValue:appDomainVals[j].dbValue, representation:appDomainVals[j].representation});
						}
					}
					metaAttr = metaClass.addLongAttr(appAttr.InternalName, appAttr.MinValue , appAttr.MaxValue, domainVals);

				break;
			case 'AppTextType':
					appDomainVals = appAttr.$assocs.DomainValues;
					domainVals = [];
					if (appDomainVals) {
						for (j = 0; j < appDomainVals.length; j++) {
							domainVals.push({dbValue:appDomainVals[j].dbValue, representation:appDomainVals[j].representation});
						}
					}
				
					metaAttr = metaClass.addTextAttr(appAttr.InternalName, appAttr.length, domainVals);
				break;
			case 'AppTimestampType':
					metaAttr = metaClass.addTimeStampAttr(appAttr.InternalName, appAttr.isDatePartUsed, appAttr.isTimePartUsed);
				break;
			case 'AppVarCharType':
					metaAttr = metaClass.addVarCharAttr(appAttr.InternalName, appAttr.length);
				break;
			case 'AppObjectReference':

				refClass = metaRepo.getClassInfo(appAttr.referencedClass.InternalName);
				metaAttr = metaClass.addReference(appAttr.InternalName, refClass);
				metaAttr.setIsDependent(appAttr.isDependent);
				metaAttr.setIsInAssociation(appAttr.isInAssociation);
				break;
			default:
				throw new JDY.base.ValidationError("Invalid type: " + appAttr.$typeInfo.internalName);
		}

		metaAttr.setIsKey(appAttr.isKey).setNotNull(appAttr.isNotNull).setGenerated(appAttr.isGenerated);
		metaAttr.setPos(appAttr.pos).setAttrGroup(appAttr.attrGroup);
	}
};



JDY.meta.createFilterRepository = function () {
	"use strict";
	var filterRep = new JDY.base.Repository("FilterRepository"),
		classInfoQueryModel,
		filterExprModel,
		andExprModel,
		orExprModel,
		operatorExprModel,
		primitveOperatorModel,
		operatorEqualModel,
		operatorGreaterModel,
		operatorLessModel;


	filterExprModel = filterRep.addClassInfo("AppFilterExpr").setShortName("FEX");
	filterExprModel.addLongAttr("ExprId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true);

	andExprModel = filterRep.addClassInfo("AppAndExpr",filterExprModel).setShortName("FEA");
	andExprModel.addTextAttr("ExprName" , 100);

	orExprModel = filterRep.addClassInfo("AppOrExpr",filterExprModel).setShortName("FEO");
	orExprModel.addTextAttr("ExprName" , 100);

	primitveOperatorModel = filterRep.addClassInfo("AppPrimitiveOperator").setShortName("FPO");
	primitveOperatorModel.addLongAttr("OperatorId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true);

	operatorEqualModel = filterRep.addClassInfo("AppOperatorEqual",primitveOperatorModel).setShortName("FPE");
	operatorEqualModel.addBooleanAttr("isNotEqual").setNotNull(true);

	operatorGreaterModel = filterRep.addClassInfo("AppOperatorGreater",primitveOperatorModel).setShortName("FPG");
	operatorGreaterModel.addBooleanAttr("isAlsoEqual").setNotNull(true);

	operatorLessModel = filterRep.addClassInfo("AppOperatorLess",primitveOperatorModel).setShortName("FPL");
	operatorLessModel.addBooleanAttr("isAlsoEqual").setNotNull(true);

	operatorExprModel = filterRep.addClassInfo("AppOperatorExpr",filterExprModel).setShortName("OEX");
	operatorExprModel.addTextAttr("attrName" , 100).setNotNull(true);
	operatorExprModel.addReference("operator", primitveOperatorModel).setIsDependent(true).setNotNull(true);
	operatorExprModel.addBooleanAttr("booleanVal"); 
	operatorExprModel.addDecimalAttr("decimalVal", 999999999.9999999,999999999.9999999,9); 
	operatorExprModel.addFloatAttr("floatVal"); 
	operatorExprModel.addLongAttr("longVal", -999999999, 999999999); 
	operatorExprModel.addTextAttr("textVal",1000); 
	operatorExprModel.addTimeStampAttr("timestampVal",true,true); 

	classInfoQueryModel = filterRep.addClassInfo("AppQuery").setShortName("FQM");
	classInfoQueryModel.addLongAttr("FilterId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true);
	classInfoQueryModel.addTextAttr("repoName" ,100).setNotNull(true);
	classInfoQueryModel.addTextAttr("className" , 35).setNotNull(true);
	classInfoQueryModel.addReference("expr", filterExprModel);

	filterRep.addAssociation("andSubExpr", andExprModel, filterExprModel, andExprModel.getInternalName(), andExprModel.getInternalName(),false, false,true);
	filterRep.addAssociation("orSubExpr", orExprModel, filterExprModel, orExprModel.getInternalName(), orExprModel.getInternalName(),false, false,true);

	return filterRep;
};

JDY.meta.FilterCreator = function () {
	
	this.rep = JDY.meta.createFilterRepository();
	this.idCounter=0;
	this.that = this;
};

JDY.meta.FilterCreator.prototype.convertMetaFilter2AppFilter = function(metaQuery) {
	
	var queryObj;
	queryObj = new JDY.base.TypedValueObject(this.rep.getClassInfo("AppQuery"));
	queryObj.FilterId = this.idCounter++;
	queryObj.repoName = metaQuery.resultType.getRepoName();
	queryObj.className = metaQuery.resultType.getInternalName();
	if(metaQuery.getFilterExpression()) {
		queryObj.expr = this.createAppExpr(metaQuery.getFilterExpression());
	} else {
		queryObj.expr = null;
	}

	return queryObj;
	
};

JDY.meta.FilterCreator.prototype.visitOrExpression = function(aOrExpr) {

	var orExpr = new JDY.base.TypedValueObject(this.rep.getClassInfo("AppOrExpr")),
		subExprs = [],
		subMetaexpr,
		subAppExpr,
		i;

	orExpr.ExprId = this.idCounter++;

	for (i = 0; i < aOrExpr.expressionVect.length; i++) {
		subMetaexpr = aOrExpr.expressionVect[i];
		subAppExpr = this.createAppExpr(subMetaexpr);
		subAppExpr.AppOrExpr = orExpr;
		subExprs.push(subAppExpr);
	}
	orExpr.$assocs.orSubExpr = subExprs;
	this.curExpr = orExpr;
};

JDY.meta.FilterCreator.prototype.createAppExpr =	function ( aMetaExpr) {
		
	aMetaExpr.visit(this);
	var result = this.curExpr;
	return result;
};

JDY.meta.FilterCreator.prototype.visitAndExpression = function(aAndExpr){

	var andExpr = new JDY.base.TypedValueObject(this.rep.getClassInfo("AppAndExpr")),
		subExprs = [],
		subMetaexpr,
		subAppExpr,
		i;

	andExpr.ExprId = this.idCounter++;

	for (i = 0; i < aAndExpr.expressionVect.length; i++) {
		subMetaexpr = aAndExpr.expressionVect[i];
		subAppExpr = this.createAppExpr(subMetaexpr);
		subAppExpr.AppAndExpr = andExpr;
		subExprs.push(subAppExpr);
	}

	andExpr.$assocs.andSubExpr = subExprs;
	this.curExpr = andExpr;
};


JDY.meta.FilterCreator.prototype.visitOperatorExpression = function( aOpExpr) {

	var appOpExpr = new JDY.base.TypedValueObject(this.rep.getClassInfo("AppOperatorExpr")); 
	appOpExpr.ExprId = this.idCounter++;
	appOpExpr.attrName = aOpExpr.attributeInfo.getInternalName();
	appOpExpr.operator = this.createAppOperator(aOpExpr.myOperator);
	this.setAppCompareValue(appOpExpr, aOpExpr);
	this.curExpr = appOpExpr;
};

JDY.meta.FilterCreator.prototype.createAppOperator = function(aMetaOper) {

	return aMetaOper.visitOperatorHandler(this );
};

JDY.meta.FilterCreator.prototype.visitLessOperator = function(aOperator) {

	var appOp =  new JDY.base.TypedValueObject(this.rep.getClassInfo("AppOperatorLess"));
	appOp.isAlsoEqual = aOperator.isAlsoEqual;
	return appOp;
};
		
JDY.meta.FilterCreator.prototype.visitGreatorOperator = function( aOperator) {

	var appOp =  new JDY.base.TypedValueObject(this.rep.getClassInfo("AppOperatorGreater"));
	appOp.isAlsoEqual = aOperator.isAlsoEqual;
	return appOp;
};
		
JDY.meta.FilterCreator.prototype.visitEqualOperator = function( aOperator) {

	var appOp =  new JDY.base.TypedValueObject(this.rep.getClassInfo("AppOperatorEqual"));
	appOp.isNotEqual = aOperator.isNotEqual;
	return appOp;
};

JDY.meta.FilterCreator.prototype.setAppCompareValue = function( appOpExpr,	aOpExpr) {

	switch(aOpExpr.attributeInfo.type.$type) {

		case 'BooleanType':
				appOpExpr.booleanVal = aOpExpr.compareValue;
			break;
		case 'BlobType':
				throw new JDY.base.FilterCreationException("AppBlobType not supported");
			break;
		case 'DecimalType':
				appOpExpr.decimalVal = aOpExpr.compareValue;
			break;
		case 'FloatType':
				appOpExpr.floatVal = aOpExpr.compareValue;
			break;
		case 'LongType':
				appOpExpr.longVal = aOpExpr.compareValue;
			break;
		case 'TextType':
				appOpExpr.textVal = aOpExpr.compareValue;
			break;
		case 'TimestampType':
				appOpExpr.timestampVal = aOpExpr.compareValue;
			break;
		case 'VarCharType':
				throw new JDY.base.FilterCreationException("AppVarCharType not supported");
			break;
		default:
			throw new JDY.base.FilterCreationException("Invalid type: " + appAttribute.$typeInfo.internalName);
	}

};


JDY.meta.getAppWorkflowManager = function () {
	"use strict";
	
	return {
		getWorkflowStepsFor: function(aTypeInfo) {
			
			if (aTypeInfo.getInternalName() === "AppRepository") {
				return [{name:"Close Repository",action:"workflow.closeRepository"} , {name:"Open Repository",action:"workflow.openRepository"}];
			} else {
				return null;
			}
		}
	};
};

	
// jdyview, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true, strict: false, plusplus: true */
/*global $, moment */
/* http://codepen.io/ericrasch/details/zjDBx */

var JDY = JDY || {};

JDY.view = JDY.view || {};

/**
 * 
 * @param {type} aClassInfo
 * @returns {JDY.view.createDivFilterForClassInfo.Anonym$32}
 */
JDY.view.createDivFilterForClassInfo = function (aClassInfo) {
	"use strict";

	var filterElem = $("<div>"),
		expressionsElem = $("<form>"),
		allFieldComps,
		parentName = "filter",
		exprCounter = 1,
		exprMap = {};

	function removeAllExpressions() {

		expressionsElem.empty();
	}

	function insertExpr() {

		var exprObj = {
			exprElem: $("<div>"),
			attrSelect: $("<select>"),
			operatorSelect: $("<select>"),
			inputElem: null,
			deleteExprActElem: $("<button>"),
			count: exprCounter++
		}, operatorSelectDiv = $("<div>");

		JDY.view.setFilterSelectAttrForClass(exprObj.attrSelect, aClassInfo);
		exprObj.attrSelect.change(function () {

			var typeHandler,
				curAttrInfo = aClassInfo.getAttr(exprObj.attrSelect.val());

			JDY.view.setOperatorsForAttribute(exprObj.operatorSelect, curAttrInfo);
			exprObj.inputElem = JDY.view.addFilterInputFieldForAttribute(operatorSelectDiv, curAttrInfo, parentName, exprObj.inputElem);
		});

		JDY.view.setOperatorsForAttribute(exprObj.operatorSelect, aClassInfo.getAllAttributeList()[0]);

		exprObj.exprElem.addClass("jdyFilterExpr");
		exprObj.attrSelect.addClass("jdyAttrSelect");
		exprObj.operatorSelect.addClass("jdyOpSelect");

		expressionsElem.append(exprObj.exprElem);
		$("<div>").addClass("jdyAttrSelectDiv").appendTo(exprObj.exprElem).append(exprObj.attrSelect);
		operatorSelectDiv.addClass("jdyOpSelectDiv").appendTo(exprObj.exprElem).append(exprObj.operatorSelect);
		exprObj.inputElem = JDY.view.addFilterInputFieldForAttribute(operatorSelectDiv, aClassInfo.getAllAttributeList()[0], parentName, exprObj.inputElem);

		exprObj.deleteExprActElem.addClass("jdyDelete");
		exprObj.deleteExprActElem.text("x");
		exprObj.deleteExprActElem.appendTo(exprObj.exprElem);
		exprObj.deleteExprActElem.click(function () {
			exprObj.exprElem.remove();
			exprMap[exprObj.count] = null;
		});

		exprMap[exprObj.count] = exprObj;
	}

	function createFilterActions() {

		var actionsElem = $("<div>"),
			removeAllActElem = $("<button>"),
			showFilterActElem = $("<button>"),
			insertExprActElem = $("<button>");

		filterElem.append(actionsElem);
		actionsElem.addClass("jdyFilterActions");
		removeAllActElem.addClass("jdyRemoveAll").text("Remove All");
		insertExprActElem.addClass("jdyInsertExpr").text("Add");
		showFilterActElem.addClass("jdyShowFilter").text("Show Filter");
		actionsElem.append(removeAllActElem);
		actionsElem.append(insertExprActElem);
		//actionsElem.append(showFilterActElem);
		removeAllActElem.click(function () {
			removeAllExpressions();
		});
		insertExprActElem.click(function () {
			insertExpr();
		});
		showFilterActElem.click(function () {
			console.log(JDY.view.createFilter(exprMap, aClassInfo));
		});

	}

	expressionsElem.validate();
	createFilterActions();
	filterElem.append(expressionsElem);

	return {
		getFilterElemJquery: function () {
			return filterElem;
		},
		getFilterElem: function () {
			return filterElem[0];
		},
		writeToValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].getValFromField(aValueObj);
			}
		},
		createFilter: function () {
			return JDY.view.createFilter(exprMap, aClassInfo);
		}

	};
};

JDY.view.createFilter = function (anExprMap, aClassInfo) {
	"use strict";

	var prop,
		query,
		hasExpr = false;

	query = new JDY.base.QueryCreator(aClassInfo);

	for (prop in anExprMap) {
		if (anExprMap[prop]) {
			if (!hasExpr) {
				hasExpr = true;
				query = query.and();
			}
			switch (anExprMap[prop].operatorSelect.val()) {
			case '>':
				query = query.greater(anExprMap[prop].attrSelect.val(), anExprMap[prop].inputElem.getValue());
				break;
			case '>=':
				query = query.greaterOrEqual(anExprMap[prop].attrSelect.val(), anExprMap[prop].inputElem.getValue());
				break;
			case '<':
				query = query.less(anExprMap[prop].attrSelect.val(), anExprMap[prop].inputElem.getValue());
				break;
			case '<=':
				query = query.lessOrEual(anExprMap[prop].attrSelect.val(), anExprMap[prop].inputElem.getValue());
				break;
			case '=':
				query = query.equal(anExprMap[prop].attrSelect.val(), anExprMap[prop].inputElem.getValue());
				break;
			case '<>':
				query = query.notEqual(anExprMap[prop].attrSelect.val(), anExprMap[prop].inputElem.getValue());
				break;
			}
		}
	}
	if (hasExpr) {
		query = query.end();
	}

	return query.query();
};

JDY.view.setFilterSelectAttrForClass = function (attrSelect, aClassInfo) {
	"use strict";

	attrSelect.find('option').remove();
	aClassInfo.forEachAttr(function (curAttrInfo) {

		if (curAttrInfo.isPrimitive() &&
				curAttrInfo.type.$type !== 'VarCharType' &&
				curAttrInfo.type.$type !== 'BlobType') {

			attrSelect.append(new Option(curAttrInfo.getInternalName(), curAttrInfo.getInternalName()));
		}
	});
};



JDY.view.setOperatorsForAttribute = function (operatorSelect, anAttr) {
	"use strict";

    var operators = anAttr.getType().handlePrimitiveKey(JDY.view.createOperatorVisitor()),
        i;
    operatorSelect.find('option').remove();
    if (operators) {
        for (i = 0; i < operators.length; i++) {
            operatorSelect.append(new Option(operators[i].toString(), operators[i].toString()));
        }

    }
};

JDY.view.createOperatorVisitor = function () {
	"use strict";

	var DEFAULT_OPERATORS = [new JDY.base.EqualOperator(),
			new JDY.base.EqualOperator(true),
			new JDY.base.LessOperator(true),
			new JDY.base.LessOperator(false),
			new JDY.base.GreatorOperator(true),
			new JDY.base.GreatorOperator()],
		BOOLEAN_OPERATORS = [new JDY.base.EqualOperator(),
			new JDY.base.EqualOperator(true)];

	return {
		handleBoolean: function (aType) {
			return BOOLEAN_OPERATORS;
		},
		handleDecimal: function (aType) {
			return DEFAULT_OPERATORS;
		},
		handleTimeStamp: function (aType) {
			return DEFAULT_OPERATORS;
		},
		handleFloat: function (aType) {
			return DEFAULT_OPERATORS;
		},
		handleLong: function (aType) {
			return DEFAULT_OPERATORS;
		},
		handleText: function (aType) {
			return DEFAULT_OPERATORS;
		},
		handleVarChar: function (aType) {
			return DEFAULT_OPERATORS;
		},
		handleBlob: function (aType) {
			return null;
		}
	};
};



JDY.view.addFilterInputFieldForAttribute = function (operatorSelect, anAttr, aParentName, oldHandler) {
	"use strict";

	var typeHandler;

	typeHandler = anAttr.getType().handlePrimitiveKey(JDY.view.createInputComponentVisitor(anAttr));
	typeHandler.field.attr("name", anAttr.getInternalName());
	typeHandler.field.attr("id", aParentName + anAttr.getInternalName());
	typeHandler.field.insertAfter(operatorSelect);
	if (oldHandler) {
		oldHandler.field.datepicker("destroy");
		oldHandler.field.remove();
	}
	if (typeHandler.initElem) {
		typeHandler.initElem();
	}

	if (typeHandler.validation) {
		typeHandler.field.rules("add", typeHandler.validation);
	} 

	return typeHandler;
};
// jdyview.html5, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true, strict: false, plusplus: true */
/*global $, moment */

var JDY = JDY || {};

JDY.view = JDY.view || {};

JDY.view.createHtml5FormForClassInfo = function (aClassInfo, service) {
	"use strict";

	var contentElem = $("<div>"),
		assocDiv = $("<div>"),
		ulElem = $("<ul>").appendTo(assocDiv),
		form = JDY.view.createHtml5DivFormForClassInfo( aClassInfo, service);

	aClassInfo.forEachAssoc(function (anAssoc) {

		// create tab for current Assciation
		$("<span>").text(anAssoc.getAssocName()).appendTo($("<a>").attr("href", anAssoc.getAssocName()).appendTo($("<li>").appendTo(ulElem)));

	});



	contentElem.append(form.getFormElem());
	contentElem.append(assocDiv);
//	assocDiv.tabs({
//		beforeLoad: function( event, ui ) {
//			
//			var detailClassAssoc = aClassInfo.getAssoc(ui.ajaxSettings.url);
//			if ( ui.tab.data( "loaded" ) ) {
//				console.log("beforeLoad:loaded ");
//				event.preventDefault();
//				return;
//			} else {
//				console.log("beforeLoad: "  + ui.ajaxSettings.url);
//				JDY.view.createTableForClassInfo(ui.panel, detailClassAssoc.detailClass, null, null);
//				
//			}
//
//		}
//		
//	});
	
	return {
		getForm: function () {
			return contentElem;
		},
		getFormElem: function () {
			return contentElem;
		},
		setValueObject: function (aValueObj) {
			form.setValueObject(aValueObj);
		},
		writeToValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].getValFromField(aValueObj);
			}
		}

	};
};

 /**
 * Create Formular seperated by HTML DIV Elements 
 * for the given ClassInfo object
 *
 * @param {JDY.base.ClassInfo} aClassInfo - class info to create panel
 * @param {type} service to get data
 */
JDY.view.createHtml5DivFormForClassInfo = function (aClassInfo, service) {
	"use strict";

	var formElem = $("<form>"),
		allFieldComps;

	allFieldComps = JDY.view.addHtml5AttributesToDom(formElem, aClassInfo, null, service);

	return {
		getForm: function () {
			return formElem;
		},
		getFormElem: function () {
			return formElem[0];
		},
		setValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].setValInField(aValueObj);
			}
		},
		writeToValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].getValFromField(aValueObj);
			}
		}

	};
};

JDY.view.addHtml5AttributesToDom = function (aParentElem, aClassInfo, aParentRef, service) {
	"use strict";

	var allFieldComponents = [];

	function createAttributeComponent(anAttrInfo, typeHandler) {
		return {
			setValInField: function (anObj) {
				typeHandler.setValue((anObj) ? anObj.val(anAttrInfo) : null);
			},
			getValFromField: function (anObj) {
				if (anObj) {
					anObj.setVal(anAttrInfo, typeHandler.getValue());
				}
			}
		};
	}

	aClassInfo.forEachAttr(function (curAttrInfo) {

		var rowElem,
			typeHandler,
			parentName = "";

		rowElem = $("<div>").addClass("jdyDivRow");
		rowElem.appendTo(aParentElem);
		if (curAttrInfo.isPrimitive()) {

			$("<label>")
				.addClass("jdyLbl")
				.attr("for", "name")
				.attr("width", "250")
				.text(curAttrInfo.getInternalName())
				.appendTo(rowElem);

			typeHandler = curAttrInfo.getType().handlePrimitiveKey(JDY.view.createHtml5InputComponentVisitor(curAttrInfo));
			typeHandler.field.attr("name", curAttrInfo.getInternalName());
			typeHandler.field.attr("id", parentName + curAttrInfo.getInternalName());
			typeHandler.field.addClass("jdyField");
			rowElem.append(typeHandler.field);
			if (typeHandler.initElem) {
				typeHandler.initElem();
			}

			if (curAttrInfo.isGenerated) {
				typeHandler.field.attr('readonly', true);
			} 

			allFieldComponents.push(createAttributeComponent(curAttrInfo, typeHandler));

		} else {

			allFieldComponents.push(JDY.view.addObjectReference(curAttrInfo, rowElem, aParentRef, service));
		}

	});
	return allFieldComponents;

};


JDY.view.createHtml5InputComponentVisitor = function (anAttr) {
	"use strict";

	return {

		handleBoolean: function (aType) {

			var fieldElem = $('<input type="checkbox">');
			return {
				field: fieldElem,
				validation : null,
				getValue: function () {
					var result = fieldElem.attr('checked');
					result = (result && result.trim().length > 0) ? Boolean(result) : false;
					return result;
				},
				setValue: function (aValue) {
					if (aValue) {
						fieldElem.attr("checked", true);
					} else {
						fieldElem.removeAttr("checked");
					}
				}
			};
		},

		handleDecimal: function (aType) {

			var fieldElem = $("<input>");
			fieldElem.attr("type", "number");
			fieldElem.attr("min", aType.minValue);
			fieldElem.attr("max", aType.maxValue);
			fieldElem.attr("required", true);
//			if (anAttr.isNotNull) {
				
//			}
			return {
				field: fieldElem,
				getValue: function () {
					var result = fieldElem.val();
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleTimeStamp: function (aType) {

			var fieldElem = $("<input>"),
				result,
				formatString;
			if (aType.datePartUsed) {
				if (aType.datePartUsed && aType.timePartUsed) {
					fieldElem.attr("type", "datetime");
				} else {
					fieldElem.attr("type", "date");
				}
			} else {
				fieldElem.attr("type", "time");
			}

			result =  {
				field: fieldElem,
				getValue: function () {
					var result = fieldElem.val();
					result =  (result && result.trim().length > 0) ? moment(result, formatString) : null;
					return (result) ? result.toDate() : null;
				},
				setValue: function (aValue) {
					var dateString = null;
					if (aValue) {
						dateString = moment(aValue).format(formatString);
					}
					fieldElem.val(dateString);
				},
				initElem: function () {
				}
			};

			if (aType.datePartUsed && !aType.timePartUsed) {

				result.validation = {
					date: true,
					required: (anAttr.isNotNull ? true : false)
				};
			} else {
				result.validation = {
					required: (anAttr.isNotNull ? true : false)
				};
			}

			return result;
		},

		handleFloat: function (aType) {

			var fieldElem = $("<input>");
			return {
				field: fieldElem,
				getValue: function () {
					var result = fieldElem.val();
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleLong: function (aType) {

			var fieldElem = $("<input>"),
				i,
				isSelect = false;
		
			if (anAttr.isNotNull) {
				fieldElem.attr("required");
			}
		    
			if ( aType.domainValues && aType.domainValues.length > 0){
				
				isSelect = true;
				fieldElem = $("<select>");

				// empty object 
				fieldElem.append(new Option("-", null));

				for (i = 0; i < aType.domainValues.length; i++) {

					fieldElem.append(new Option(aType.domainValues[i].representation, aType.domainValues[i].dbValue));
				}
				
			}

			return {
				field: fieldElem,
				validation : {
					min: aType.minValue,
					max: aType.maxValue,
					number: true,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					if (isSelect) {
						result = (!result || result === "null") ? null : result;
					}
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleText: function (aType) {

			var fieldElem = $("<input>"),
				i,
				isSelect = false;
			
			if ( aType.domainValues && aType.domainValues.length > 0){
				
				isSelect = true;
				fieldElem = $("<select>");

				// empty object 
				fieldElem.append(new Option("-", null));

				for (i = 0; i < aType.domainValues.length; i++) {

					fieldElem.append(new Option(aType.domainValues[i].representation, aType.domainValues[i].dbValue));
				}
				
			} else {
				fieldElem.attr("placeholder", anAttr.getInternalName());
			}
			// $("<select>")
			// attrSelect.append(new Option(curAttrInfo.getInternalName(), curAttrInfo.getInternalName()));
			
			return {
				field: fieldElem,
				validation : {
					minlength: 0,
					maxlength: aType.length,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					if (isSelect) {
						result = (!result || result === "null") ? null : result;
					}
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleVarChar: function (aType) {

			var fieldElem = $("<textarea>");
			return {
				field: fieldElem,
				validation : {
					minlength: 0,
					maxlength: aType.length,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};

// jdyview, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true, strict: false, plusplus: true */
/*global $, moment */


var JDY = JDY || {};

JDY.view = JDY.view || {};

JDY.view.openDynamicFormDialog = function (aForm) {
	"use strict";

	return aForm.getForm().dialog({
		autoOpen: false,
		height: 600,
		width: 500,
		modal: true,
		buttons: {
			Save: function () {
				var bValid = aForm.getForm().valid();

				if (bValid) {
					$(this).dialog("close");

					if (aForm.formSaveCallback) {
						aForm.formSaveCallback();
					}
				}
			},
			Cancel: function () {
				$(this).dialog("close");
			}
		},
		close: function () {
		}
	});
};

//@Deprected
JDY.view.createTableFormForClassInfo = function (tbody, aClassInfo) {
	"use strict";

	tbody.empty();
	aClassInfo.forEachAttr(function (curAttrInfo) {

		var rowElem,
			colElem,
			inputElem,
			accordionElem,
			accorGroupElem,
			inputComp;

		rowElem = $("<tr>");
		$("<td>")
			.addClass("jdyLbl")
			.text(curAttrInfo.getInternalName())
			.data("col", 0)
			.appendTo(rowElem);

		if (curAttrInfo.isPrimitive()) {

			colElem = $("<td>")
				.addClass("jdyField");
			inputComp = curAttrInfo.getType().handlePrimitiveKey(JDY.view.createInputComponentVisitor(curAttrInfo));
			inputComp.field.attr("name", curAttrInfo.getInternalName());
			inputComp.field.attr("id", curAttrInfo.getInternalName());
			//inputComp.databind(inputComp.field, curAttrInfo);

			colElem.append(inputComp.field);
			rowElem.append(colElem);

		} else {

			colElem = $("<td>")
				.addClass("jdyObject");

			accordionElem = $("<div>");
			accorGroupElem = $("<div>").addClass("group");
			accorGroupElem.appendTo(accordionElem);
			$("<h3>").text(curAttrInfo.getInternalName()).appendTo(accorGroupElem);
			$("<div>").text("value").appendTo(accorGroupElem);

			accordionElem.accordion({
				header: "h3",
				collapsible: true,
				heightStyle: "content"
			});

			colElem.append(accordionElem);
			rowElem.append(colElem);

		}
		rowElem.appendTo(tbody);

	});
};

JDY.view.createFormForClassInfo = function (aClassInfo, service) {
	"use strict";

	var contentElem = $("<div>"),
		assocDiv = $("<div>"),
		ulElem = $("<ul>").appendTo(assocDiv),
		form = JDY.view.createDivFormForClassInfo( aClassInfo, service);

	aClassInfo.forEachAssoc(function (anAssoc) {

		// create tab for current Assciation
		$("<span>").text(anAssoc.getAssocName()).appendTo($("<a>").attr("href", anAssoc.getAssocName()).appendTo($("<li>").appendTo(ulElem)));

	});



	contentElem.append(form.getFormElem());
	contentElem.append(assocDiv);
	assocDiv.tabs({
		beforeLoad: function( event, ui ) {
			
			var detailClassAssoc = aClassInfo.getAssoc(ui.ajaxSettings.url);
			if ( ui.tab.data( "loaded" ) ) {
				console.log("beforeLoad:loaded ");
				event.preventDefault();
				return;
			} else {
				console.log("beforeLoad: "  + ui.ajaxSettings.url);
				JDY.view.createTableForClassInfo(ui.panel, detailClassAssoc.detailClass, null, null);
				
			}

		}
		
	});
	
	return {
		getForm: function () {
			return contentElem;
		},
		getFormElem: function () {
			return contentElem;
		},
		setValueObject: function (aValueObj) {
			form.setValueObject(aValueObj);
		},
		writeToValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].getValFromField(aValueObj);
			}
		}

	};;
};

 /**
 * Create Formular seperated by HTML DIV Elements 
 * for the given ClassInfo object
 *
 * @param {JDY.base.ClassInfo} aClassInfo - class info to create panel
 * @param {type} service to get data
 */
JDY.view.createDivFormForClassInfo = function (aClassInfo, service) {
	"use strict";

	var formElem = $("<form>"),
		allFieldComps;

	formElem.validate();
	allFieldComps = JDY.view.addAttributesToDom(formElem, aClassInfo, null, service);

	return {
		getForm: function () {
			return formElem;
		},
		getFormElem: function () {
			return formElem[0];
		},
		setValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].setValInField(aValueObj);
			}
		},
		writeToValueObject: function (aValueObj) {
			var i;
			for (i = 0; i < allFieldComps.length; i++) {
				allFieldComps[i].getValFromField(aValueObj);
			}
		}

	};
};

JDY.view.addAttributesToDom = function (aParentElem, aClassInfo, aParentRef, service) {
	"use strict";

	var allFieldComponents = [];

	function createAttributeComponent(anAttrInfo, typeHandler) {
		return {
			setValInField: function (anObj) {
				typeHandler.setValue((anObj) ? anObj.val(anAttrInfo) : null);
			},
			getValFromField: function (anObj) {
				if (anObj) {
					anObj.setVal(anAttrInfo, typeHandler.getValue());
				}
			}
		};
	}

	aClassInfo.forEachAttr(function (curAttrInfo) {

		var rowElem,
			typeHandler,
			parentName = "";

		rowElem = $("<div>").addClass("jdyDivRow");
		rowElem.appendTo(aParentElem);
		if (curAttrInfo.isPrimitive()) {

			$("<label>")
				.addClass("jdyLbl")
				.attr("for", "name")
				.attr("width", "250")
				.text(curAttrInfo.getInternalName())
				.appendTo(rowElem);

			typeHandler = curAttrInfo.getType().handlePrimitiveKey(JDY.view.createInputComponentVisitor(curAttrInfo));
			typeHandler.field.attr("name", curAttrInfo.getInternalName());
			typeHandler.field.attr("id", parentName + curAttrInfo.getInternalName());
			typeHandler.field.addClass("jdyField")
			rowElem.append(typeHandler.field);
			if (typeHandler.initElem) {
				typeHandler.initElem();
			}

			if (curAttrInfo.isGenerated) {
				typeHandler.field.attr('readonly', true);
			} else if (typeHandler.validation) {
				typeHandler.field.rules("add", typeHandler.validation);
			} 

			allFieldComponents.push(createAttributeComponent(curAttrInfo, typeHandler));

		} else {

			allFieldComponents.push(JDY.view.addObjectReference(curAttrInfo, rowElem, aParentRef, service));
		}

	});
	return allFieldComponents;

};


JDY.view.addObjectReference = function (aObjReference, aRowElem, aParentRef, service) {
	"use strict";

	function createAttributeObjectComponent(aObjReference, allFieldComps) {
		return {
			setValInField: function (anObj) {

				var refObj = (anObj) ? anObj.val(aObjReference) : null,
					i;
				for (i = 0; i < allFieldComps.length; i++) {
					allFieldComps[i].setValInField(refObj);
				}
			},
			getValFromField: function (anObj) {

				var refObj,
					i;
				if (anObj) {

					refObj = anObj.val(aObjReference);
					for (i = 0; i < allFieldComps.length; i++) {
						allFieldComps[i].getValFromField(refObj);
					}
				}
			}
		};
	}

	function createObjectSelect() {

		var selectSpan = $("<span>"),
			referenceSelect = $("<input>"),
			selectBtn = $("<a>"),
			lazyObjectList,
			aspectPaths = JDY.view.getDisplayAttributesFor(aObjReference.referencedClass),
			selectedObject;


		selectBtn.attr( "tabIndex", -1 )
			.attr( "title", "Show All Items" )
			.button({
				icons: {
					primary: "ui-icon-triangle-1-s"
				},
				text: false
			}).addClass( "jdy-select-btn" );

		// some asynchronous click handler
		selectBtn.click( function(e){
			var filter = new JDY.base.QueryCreator(aObjReference.referencedClass).query();

			function openSelectMenu() {
				var _offset = selectBtn.offset(),
				position = {
					x: _offset.left + 10, 
					y: _offset.top + 10
				};
				selectBtn.contextMenu(position);
			}

			
			function errorHandler() {
				window.alert("Error loading options");
			}

			function setOptions(filterdObjects) {
				lazyObjectList = filterdObjects;
				openSelectMenu();
			}

			if(lazyObjectList) {
				openSelectMenu();
			} else {
				service.loadValuesFromDb(filter, setOptions, errorHandler);
			}
		});


		referenceSelect.attr('readonly', true);
		selectSpan.addClass("jdyField");
		selectSpan.append(referenceSelect);
		selectSpan.append(selectBtn);
		// setup context menu
		selectSpan.contextMenu({
			 selector: '.jdy-select-btn',
			 trigger: 'none',
			 build: function($trigger, e) {
				
				var i,
					itemsList= {};
				for( i= 0; i< lazyObjectList.length; i++) {
					itemsList[lazyObjectList[i].Name+i] = {name: JDY.view.getTextForAspectPaths(aspectPaths, lazyObjectList[i]),
															value: lazyObjectList[i]};
				}
				
				 return {
					callback: function(key, options) {
						var m = "clicked: " + key,
								text;
						selectedObject = itemsList[key].value;
						text = (selectedObject) ? JDY.view.getTextForAspectPaths(aspectPaths, selectedObject) : "";
						referenceSelect.val(text);
					},
					items: itemsList
				};
			 }
		 });
	
		return {
			field: selectSpan,
			setValInField: function (anObj) {
				var val = ((anObj) ? anObj.val(aObjReference) : null),
					text = (val) ? JDY.view.getTextForAspectPaths(aspectPaths, val) : "";
				referenceSelect.val(text),
				selectedObject = val;
			},
			getValFromField: function (anObj) {
				if (anObj) {
					anObj.setVal(aObjReference, selectedObject);
				}
			}
		};
	}


	var accordionElem,
		referenceElem,
		allFieldComponents,
		selectElem;

	// add accordion only at the first level and a depent reference
	if (!aParentRef && aObjReference.dependent) {

		accordionElem = $("<div>");
		$("<h3>").text(aObjReference.getInternalName()).appendTo(accordionElem);

		referenceElem = $("<div>");
		referenceElem.attr("data-bind", "with: " + aObjReference.getInternalName());
		referenceElem.appendTo(accordionElem);
		aRowElem.append(accordionElem);

		allFieldComponents = JDY.view.addAttributesToDom(referenceElem, aObjReference.referencedClass, aObjReference, service);

		accordionElem.accordion({
			collapsible: true,
			heightStyle: "content",
			active: false
		});

		return createAttributeObjectComponent(aObjReference, allFieldComponents);
	} else {

		$("<label>")
			.addClass("jdyLbl")
			.attr("for", "name")
			.attr("width", "250")
			.text(aObjReference.getInternalName())
			.appendTo(aRowElem);

		selectElem = createObjectSelect();
		aRowElem.append(selectElem.field);
		return selectElem;

	}
};


JDY.view.createInputComponentVisitor = function (anAttr) {
	"use strict";

	return {

		handleBoolean: function (aType) {

			var fieldElem = $('<input type="checkbox">');
			return {
				field: fieldElem,
				validation : null,
				getValue: function () {
					var result = fieldElem.attr('checked');
					result = (result && result.trim().length > 0) ? Boolean(result) : false;
					return result;
				},
				setValue: function (aValue) {
					if (aValue) {
						fieldElem.attr("checked", true);
					} else {
						fieldElem.removeAttr("checked");
					}
				}
			};
		},

		handleDecimal: function (aType) {

			var fieldElem = $("<input>");
			return {
				field: fieldElem,
				validation : {
					min: aType.minValue,
					max: aType.maxValue,
					number: true,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleTimeStamp: function (aType) {

			var fieldElem = $("<input>"),
				result,
				formatString;
			if (aType.datePartUsed) {
				if (aType.datePartUsed && aType.timePartUsed) {
					formatString = "MM/DD/YYYY HH:mm";
				} else {
					formatString = "MM/DD/YYYY";
				}
			} else {
				formatString = "HH:mm";
			}

			result =  {
				field: fieldElem,
				getValue: function () {
					var result = fieldElem.val();
					result =  (result && result.trim().length > 0) ? moment(result, formatString) : null;
					return (result) ? result.toDate() : null;
				},
				setValue: function (aValue) {
					var dateString = null;
					if (aValue) {
						dateString = moment(aValue).format(formatString);
					}
					fieldElem.val(dateString);
				},
				initElem: function () {
					if (aType.datePartUsed && !aType.timePartUsed) {
						fieldElem.datepicker({
							showOn: "button",
							buttonImage: "js/icons/calendar.gif",
							buttonImageOnly: true,
							dateFormat: "mm/dd/yy"
						});
					}

				}
			};

			if (aType.datePartUsed && !aType.timePartUsed) {

				result.validation = {
					date: true,
					required: (anAttr.isNotNull ? true : false)
				};
			} else {
				result.validation = {
					required: (anAttr.isNotNull ? true : false)
				};
			}

			return result;
		},

		handleFloat: function (aType) {

			var fieldElem = $("<input>");
			return {
				field: fieldElem,
				validation : {
					number: true,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleLong: function (aType) {

			var fieldElem = $("<input>"),
				i,
				isSelect = false;
			
			if ( aType.domainValues && aType.domainValues.length > 0){
				
				isSelect = true;
				fieldElem = $("<select>");

				// empty object 
				fieldElem.append(new Option("-", null));

				for (i = 0; i < aType.domainValues.length; i++) {

					fieldElem.append(new Option(aType.domainValues[i].representation, aType.domainValues[i].dbValue));
				}
				
			}

			return {
				field: fieldElem,
				validation : {
					min: aType.minValue,
					max: aType.maxValue,
					number: true,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					if (isSelect) {
						result = (!result || result === "null") ? null : result;
					}
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleText: function (aType) {

			var fieldElem = $("<input>"),
				i,
				isSelect = false;
			
			if ( aType.domainValues && aType.domainValues.length > 0){
				
				isSelect = true;
				fieldElem = $("<select>");

				// empty object 
				fieldElem.append(new Option("-", null));

				for (i = 0; i < aType.domainValues.length; i++) {

					fieldElem.append(new Option(aType.domainValues[i].representation, aType.domainValues[i].dbValue));
				}
				
			}
			// $("<select>")
			// attrSelect.append(new Option(curAttrInfo.getInternalName(), curAttrInfo.getInternalName()));
			
			return {
				field: fieldElem,
				validation : {
					minlength: 0,
					maxlength: aType.length,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					if (isSelect) {
						result = (!result || result === "null") ? null : result;
					}
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleVarChar: function (aType) {

			var fieldElem = $("<textarea>");
			return {
				field: fieldElem,
				validation : {
					minlength: 0,
					maxlength: aType.length,
					required: (anAttr.isNotNull ? true : false)
				},
				getValue: function () {
					var result = fieldElem.val();
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				}
			};
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};

/**
 * 
 * @param {type} aDivElem element in wich the table is inserted
 * @param {type} aClassInfo
 * * @param {type} filterCreator
 * @param {type} service - service that reads from db and write changes to db
 * @returns {JDY.view.createTableForClassInfo.Anonym$23}
 */
JDY.view.createTableForClassInfo = function (aDivElem, aClassInfo, filterCreator, service) {
	"use strict";

	var tableElem = $("<table>"),
		headElem = $("<thead>"),
		bodyElem = $("<tbody>"),
		actionElem = $("<p>"),
		columns = [],
		columnActions,
		editForm,
		dialog,
		rows = []; //@todo: use hash

	editForm = JDY.view.createDivFormForClassInfo(aClassInfo, service);
	editForm.getForm().appendTo("body");
	editForm.getForm().hide();

	function createActionElem(anAct, aValueObj) {
		var newActElem = $("<a>").text(anAct.getLabel());
		newActElem.click(function () {
			anAct.handler(aValueObj);
		});
		return newActElem;
	}

	function addRowColumns(aBodyRowElem, aValueObj) {

		var actionsColElem,
			curActElem,
			i;

		// add column actions
		actionsColElem = $("<td>").addClass("jdyActField");
		for (i = 0; i < columnActions.length; i++) {

			curActElem = createActionElem(columnActions[i], aValueObj);
			actionsColElem.append(curActElem);
		}
		aBodyRowElem.append(actionsColElem);

		for (i = 0; i < columns.length; i++) {
			$("<td>")
				.addClass("jdyTblField")
				.text(columns[i].getValueAsText((aValueObj) ? aValueObj.val(columns[i].attrInfo) : null))
				.data("col", 0)
				.appendTo(aBodyRowElem);
		}

	}

	function addRow(aValueObj) {

		var bodyRowElem = $("<tr>");

		addRowColumns(bodyRowElem, aValueObj);

		bodyElem.append(bodyRowElem);
		bodyRowElem.valueObj = aValueObj;
		rows.push(bodyRowElem);
	}

	function reloadObjects() {
		
		var filter = new JDY.base.QueryCreator(aClassInfo).query();
                
                if (filterCreator) {
                    filter = filterCreator.createFilter();
                }

		service.loadValuesFromDb(filter, function (allReadedObjs) {

			var i;
			bodyElem.empty();
			for (i = 0; i < allReadedObjs.length; i++) {
				bodyElem.append(addRow(allReadedObjs[i]));
			}
		});
	}
	
	function removeObject(anObject) {

		var successFunc,
			failFunc;

		successFunc = function () {
			var idxToRemove,
				elemToRemove,
				i;
			for (i = 0; i < rows.length; i++) {
				if (rows[i].valueObj === anObject) {
					idxToRemove = i;
				}
			}

			if (idxToRemove !== undefined) {
				elemToRemove = rows.splice(idxToRemove, 1);
				elemToRemove[0].remove();
			}
		};
		failFunc = function () {
			window.alert("Error deleting node on server");
		};

		service.deleteObjectInDb(anObject, anObject.$typeInfo, successFunc, failFunc);

	}

	function changeObject(anObject) {

		var saveCallback = function () {

			var successFunc,
				failFunc;
			successFunc = function () {
				var i,
					idxToUpdate,
					elemToUpdate;
				for (i = 0; i < rows.length; i++) {
					if (rows[i].valueObj === anObject) {
						idxToUpdate = i;
					}
				}

				if (idxToUpdate !== undefined) {
					elemToUpdate = rows[idxToUpdate];
					elemToUpdate.empty();
					addRowColumns(elemToUpdate, anObject);
				}

			};
			failFunc = function () {
				window.alert("Error insering node on server");
			};


			editForm.writeToValueObject(anObject);
			service.updateObjectInDb(anObject,  successFunc, failFunc);

		};

		editForm.setValueObject(anObject);
		editForm.formSaveCallback = saveCallback;
		if (!dialog) {
			dialog = JDY.view.openDynamicFormDialog(editForm);
		}
		dialog.dialog("open");
	}

	function insertObject() {

		var valObj = new JDY.base.TypedValueObject(aClassInfo),
			saveCallback;

		saveCallback = function () {

			var successFunc,
				failFunc;
			failFunc = function () {
				window.alert("Error insering node on server");
			};
			successFunc = function (insertedObj) {
				addRow(insertedObj);
			};

			editForm.writeToValueObject(valObj);
			service.insertObjectInDb(valObj, successFunc, failFunc);

		};
		editForm.setValueObject(valObj);
		editForm.formSaveCallback = saveCallback;
		if (!dialog) {
			dialog = JDY.view.openDynamicFormDialog(editForm);
		}
		dialog.dialog("open");
	}

	function createColumns() {

		var columns = [],
			curColumn;
		aClassInfo.forEachAttr(function (curAttrInfo) {

			if (curAttrInfo.isPrimitive()) {

				curColumn = curAttrInfo.getType().handlePrimitiveKey(JDY.view.createColumnVisitor());
				curColumn.getLabel = function () {
					return curAttrInfo.getInternalName();
				};
				curColumn.attrInfo = curAttrInfo;
				columns.push(curColumn);
			} else {
				// don't add columns fot object references
			}
		});
		return columns;
	}

	function createColumnActions() {

		var columnActions = [];

		columnActions.push({
			getLabel : function () {
				return "Delete";
			},
			handler: removeObject
		});
		columnActions.push({
			getLabel : function () {
				return "Change";
			},
			handler: changeObject
		});
		return columnActions;
	}

	function createHeader() {

		var headerRowElem = $("<tr>"),
			i;

		// add columns actions heading
		$("<th>")
			.addClass("jdyTblLbl")
			.text("Actions")
			.data("col", 0)
			.appendTo(headerRowElem);

		for (i = 0; i < columns.length; i++) {
			$("<th>")
				.addClass("jdyTblLbl")
				.text(columns[i].getLabel())
				.data("col", 0)
				.appendTo(headerRowElem);
		}

		return headerRowElem;
	}

	function createActions() {

		var insertActElem = $("<button>").addClass("jdyInsert").text("Insert"),
			reloadActElem = $("<button>").addClass("jdyReload").text("Reload");
		actionElem.append(insertActElem);
		actionElem.append(reloadActElem);
		insertActElem.click(function () {
			insertObject();
		});
		reloadActElem.click(function () {
			reloadObjects();
		});
	}

	function destroyAllFunc() {
		editForm.getForm().remove();
		aDivElem.empty();
	}


	aDivElem.empty();


	columns = createColumns();
	columnActions = createColumnActions();
	createActions();

	headElem.append(createHeader());
	tableElem.append(headElem);
	tableElem.append(bodyElem);
	aDivElem.append(tableElem);

	aDivElem.append(actionElem);

	return {
		setValues: function (allValueObjs) {
			var i;
			for (i = 0; i < allValueObjs.length; i++) {
				bodyElem.append(addRow(allValueObjs[i]));
			}
		},
		destroy: destroyAllFunc
	};
};


JDY.view.createColumnVisitor = function () {
	"use strict";

	return {

		handleBoolean: function (aType) {

			return {
				getValueAsText: function (val) {
					return (val) ? 'x' : '-';
				}
			};
		},

		handleDecimal: function (aType) {

			return {
				getValueAsText: function (val) {
					return (val) ? ('' + val) : "";
				}
			};
		},

		handleTimeStamp: function (aType) {
			return {
				getValueAsText: function (val) {
					var text = '';
					if (val) {
						if (aType.datePartUsed) {
							if (aType.datePartUsed && aType.timePartUsed) {
								text = val.toLocaleDateString() + ' ' + val.toLocaleTimeString();
							} else {
								text = val.toLocaleDateString();
							}
						} else {
							text = val.toLocaleTimeString();
						}
					}
					return text;
				}
			};
		},

		handleFloat: function (aType) {
			return {
				getValueAsText: function (val) {
					return (val) ? String(val) : '';
				}
			};
		},

		handleLong: function (aType) {

			return {
				getValueAsText: function (val) {
					return (val) ? String(val) : '';
				}
			};
		},

		handleText: function (aType) {

			return {
				getValueAsText: function (val) {
					return (val) ? String(val) : '';
				}
			};
		},

		handleVarChar: function (aType) {

			return {
				getValueAsText: function (val) {
					return (val) ? String(val) : '';
				}
			};
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};




JDY.view.parseJsonDateString = function (value) {
	"use strict";

	var jsonDateRE = /^\/Date\((-?\d+)(\+|-)?(\d+)?\)\/$/,
		arr = value && jsonDateRE.exec(value);
	if (arr) {
		return new Date(parseInt(arr[1], 10));
	}
    return value;

};

JDY.view.createUlMenu = function (allObjects, aMenuConfig) {
	"use strict";

	var menuUl = $("<ul>");

	$.each(allObjects, function (index, value) {
		var curMenuLi = $("<li>").text(value[aMenuConfig.attrName]).appendTo(menuUl);
		curMenuLi.click(function () {
			if (aMenuConfig.callBack) {
				aMenuConfig.callBack({lvl1: value, lvl2: null});
			}
		});
		if (aMenuConfig.lvl2Assoc) {

			value.assocVals("Classes").done( function (allSubValues) {

				var subMenuUl = $("<ul>");
				subMenuUl.appendTo(curMenuLi);
				$.each(allSubValues, function (index, subValue) {
					var curSubMenuLi = $("<li>").text(subValue[aMenuConfig.lvl2AttrName]).appendTo(subMenuUl);
					curSubMenuLi.click(function (e) {
						if (aMenuConfig.callBack) {
							aMenuConfig.callBack({lvl1: value, lvl2: subValue});
						}
						e.stopPropagation();
					});
				});
			});
		}
	});

	return menuUl;
};
// jdyview, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true, strict: false, plusplus: true */
/*global $, moment */


var JDY = JDY || {};

JDY.view = JDY.view || {};

JDY.view.treeNodeConfig = {
	AppRepository: {
		icon: "jdyapp/tree-diagramm.png",
		nameAspects: [["Name"]]
	},

	AppClassInfo: {
		icon: "jdyapp/typeClass.png",
		nameAspects: [["Name"]]
	},
	AppBooleanType: {
		icon: "jdyapp/typeBoolean.png",
		nameAspects: [["Name"]]

	},
	AppLongType: {
		icon: "jdyapp/typeText.png",
		nameAspects: [["Name"]]

	},
	AppTextType: {
		icon: "jdyapp/typeText.png",
		nameAspects: [["Name"]]
	},
	AppBlobType: {
		nameAspects: [["Name"]]

	},
	AppDecimalType: {
		icon: "jdyapp/typeText.png",
		nameAspects: [["Name"]]

	},
	AppFloatType: {
		icon: "jdyapp/typeText.png",
		nameAspects: [["Name"]]
	},
	AppTimestampType: {
		icon: "jdyapp/typeTimestamp.png",
		nameAspects: [["Name"]]
	},
	AppVarCharType: {
		icon: "jdyapp/typeVarchar.png",
		nameAspects: [["Name"]]
	},
	AppObjectReference: {
		icon: "jdyapp/typeReference.png",
		nameAspects: [["Name"]]
	}
};



JDY.view.createAppTree = function (anAppRepoList, service) {

	"use strict";
	var rootNode,
		treeContainer = $("<div>").addClass("jdyAppTree"),
		treeDivElem = $("<div>").addClass("jdyAppTreePnl"),
		actionPnl,
		rootTypeName = "AppRepository",
		worflowMngr = JDY.meta.getAppWorkflowManager(),
		rep = JDY.meta.createAppRepository();

	function createActionPanel() {

		var actionDivElem = $("<div>").addClass("jdyAppTreeActPnl"),
			createRepoActElem = $("<button>").text("Create New Repo");
		actionDivElem.append(createRepoActElem);

		createRepoActElem.click(function () {
			JDY.view.createNewNode(null, rep.getClassInfo(rootTypeName), rootNode, null, service);
		});

		return {
			elem: actionDivElem,
			selectCallback: function (node) {
			}
		};
	}

	actionPnl = createActionPanel();

	// Attach the dynatree widget to an existing <div id="tree"> element
		// and pass the tree options as an argument to the dynatree() function:

	treeContainer.append(treeDivElem);
	treeContainer.append(actionPnl.elem);
	treeDivElem.dynatree({
		imagePath : "js/icons/",
		persist: true,
		selectMode: 2,
		onActivate: function (node) {
			actionPnl.selectCallback(node);
		},
		idPrefix: "jdy1"
	});

	// Now get the root node object
	rootNode = treeDivElem.dynatree("getRoot");

	JDY.view.addAppNodes(rootNode, anAppRepoList);

	JDY.view.bindContextMenu(treeDivElem, service, worflowMngr);

	return treeContainer;
};


JDY.view.addAppNodes = function (aRootNode, anAppRepoList) {

	"use strict";
	var i,
		childNode;

	for (i = 0; i < anAppRepoList.length; i++) {

		childNode = aRootNode.addChild(JDY.view.createNodeObForValObj(anAppRepoList[i], JDY.view.treeNodeConfig));
	}

};

JDY.view.createNodeObForValObj = function (aValObj, nodeConfig) {

	"use strict";
	var assocNodeObjs = [],
		result,
		config,
		aspectPaths;

	aValObj.$typeInfo.forEachAssoc(function (anAssoc) {

		var assocVals = aValObj.assocVals(anAssoc),
			assocChildNodes = [],
			i;


		if (assocVals) {
			
			for (i = 0; i < assocVals.length; i++) {
				assocChildNodes.push(JDY.view.createNodeObForValObj(assocVals[i], nodeConfig));
			}
		}

		assocNodeObjs.push({
			title: anAssoc.getAssocName(),
			isFolder: true,
			addClass: "assocNode",
			children: assocChildNodes,
			$assoc: anAssoc,
			$parent: aValObj
		});
	});

	result =  {
		title: aValObj.Name,
		tooltip: aValObj.applicationName,
		isFolder: true,
		addClass: "appNode",
		children: assocNodeObjs,
		$valObj: aValObj
	};

	config = nodeConfig[aValObj.$typeInfo.getInternalName()];
	aspectPaths = JDY.view.getDisplayAttributesFor(aValObj.$typeInfo);
	if (config) {
		if (config.icon) {
			result.icon = config.icon;
		}

		if (config.nameAspects) {
			aspectPaths = config.nameAspects;
		}
	}
	result.title = JDY.view.getTextForAspectPaths(aspectPaths, aValObj);

	return result;
};

JDY.view.createNewNodeMenuItem = function (anAssoc, detailClass, parentNode, parentObj, service) {
	"use strict";

	return {
		name: "Add " + detailClass.internalName,
		icon: "edit",
		callback: function () {
			JDY.view.createNewNode(anAssoc, detailClass, parentNode, parentObj, service);
		}

	};

};

JDY.view.createNewNode = function (anAssoc, detailClass, parentNode, parentObj, service) {
	"use strict";
	var valObj,
		saveCallback,
		editForm,
		dialog;

	editForm = JDY.view.createDivFormForClassInfo(detailClass, service);
	editForm.getForm().appendTo("body");
	dialog = JDY.view.openDynamicFormDialog(editForm);

	valObj = service.createNewObject(detailClass);
	// set master object 
	if (anAssoc) {
		valObj.setVal(anAssoc.masterClassReference, parentObj);
	}

	saveCallback = function () {

		var newNode = {},
			successFunc,
			failFunc;
		successFunc = function (createdValObj) {


			newNode = JDY.view.createNodeObForValObj(createdValObj, JDY.view.treeNodeConfig);
			parentNode.addChild(newNode);
		};
		failFunc = function () {
			window.alert("Error insering node on server");
		};

		editForm.writeToValueObject(valObj);
		service.insertObjectInDb(valObj, successFunc, failFunc);

	};
	editForm.setValueObject(valObj);
	editForm.formSaveCallback = saveCallback;

	dialog.dialog("open");

};


JDY.view.bindContextMenu = function (aTreeId, service, aWorkflowMngr) {
	"use strict";

	function createEditObjectAction(valObj) {

		return function (key, options) {
			var editForm,
				dialog,
				saveCallback;
			if (valObj) {

				editForm = JDY.view.createDivFormForClassInfo(valObj.$typeInfo, service);
				editForm.getForm().appendTo("body");
				editForm.setValueObject(valObj);

				saveCallback = function () {

					var successFunc,
						failFunc;

					successFunc = function () {
						window.alert("Object saved");
					};
					failFunc = function () {
						window.alert("Error updating node on server");
					};

					editForm.writeToValueObject(valObj);
					service.updateObjectInDb(valObj, successFunc, failFunc);

				};

				editForm.formSaveCallback = saveCallback;
				//if (!dialog) {
				dialog = JDY.view.openDynamicFormDialog(editForm);
				//}
				dialog.dialog("open");
			}
		};
	}

	function createDeleteObjectAction(valObj, node) {

		return function (key, options) {
			var successFunc,
				failFunc;

			successFunc = function () {
				node.remove();
			};
			failFunc = function () {
				window.alert("Error deleting node on server");
			};

			service.deleteObjectInDb(valObj, valObj.$typeInfo, successFunc, failFunc);
		};
	}

	function createExecuteWorkflowStepAction(valObj, aStep) {

		return function (key, options) {
			var successFunc,
				failFunc;

			successFunc = function () {
				window.alert("action executed " + aStep.action);
			};
			failFunc = function () {
				window.alert("Error executing workflow step");
			};
			service.executeWorkflowAction(aStep.action, valObj, successFunc, failFunc);
		};
	}

	// ap node context mmemu
	aTreeId.contextMenu({
		selector: '.appNode',
		build: function ($trigger, e) {
			// this callback is executed every time the menu is to be shown
			// its results are destroyed every time the menu is hidden
			// e is the original contextmenu event, containing e.pageX and e.pageY (amongst other data)
			var node  = $.ui.dynatree.getNode($trigger),
				valObj = (node) ? node.data.$valObj : null,
				workflowSteps,
				curStep,
				i,
				allItems = {};

			allItems.edit = {name: "edit", icon: "edit",
				callback: createEditObjectAction(valObj)
				};
			allItems.cut = {name: "Delete", icon: "cut",
				callback: createDeleteObjectAction(valObj, node)
				};
			workflowSteps = aWorkflowMngr.getWorkflowStepsFor(valObj.$typeInfo);
			if (workflowSteps) {
				for (i = 0; i < workflowSteps.length; i++) {
					curStep = workflowSteps[i];
					allItems[curStep.action] = { name: curStep.name, icon: "execute",
						callback: createExecuteWorkflowStepAction(valObj, curStep)
						};
				}
			}
			return {items: allItems};
		}
	});
	// add assoc node menu
	aTreeId.contextMenu({
		selector: '.assocNode',
		build: function ($trigger, e) {
			// this callback is executed every time the menu is to be shown
			// its results are destroyed every time the menu is hidden
			// e is the original contextmenu event, containing e.pageX and e.pageY (amongst other data)
			var parentNode  = $.ui.dynatree.getNode($trigger),
				assoc = (parentNode) ? parentNode.data.$assoc : null,
				parentObj = (parentNode) ? parentNode.data.$parent : null,
				detailClass = assoc.detailClass,
				i,
				subClassInfo,
				allItems = {};

			if (!detailClass.isAbstract) {
				allItems[detailClass.internalName] = JDY.view.createNewNodeMenuItem(assoc, detailClass, parentNode, parentObj, service);
			}

			// add menu entry for every concrete subclass
			for (i = 0; i < detailClass.getSubclassesRec().length; i++) {
				subClassInfo = detailClass.getSubclassesRec()[i];
				if (!subClassInfo.isAbstract) {
					allItems[subClassInfo.internalName] = JDY.view.createNewNodeMenuItem(assoc, subClassInfo, parentNode, parentObj, service);
				}
			}

			return {
				items: allItems
			};
		}
	});

};

/*
 * Get key Attributes Aspect paths as default attributes
 */
JDY.view.getDisplayAttributesFor = function getDisplayAttributesFor(aTypeInfo) {
	"use strict";

	var aspectPathList = [],
		i;
	aTypeInfo.forEachAttr(function (curAttrInfo) {

		var newPath,
			refAspectPath;

		if (curAttrInfo.isKey()) {

			if (curAttrInfo.isPrimitive()) {
				if (curAttrInfo.type.$type !== 'VarCharType' &&
						curAttrInfo.type.$type !== 'BlobType') {

					aspectPathList.push([curAttrInfo]);
				}
			} else {
				refAspectPath = getDisplayAttributesFor(curAttrInfo.getReferencedClass());
				for (i = 0; i < refAspectPath.length; i++) {
					newPath = [curAttrInfo];
					newPath = newPath.concat(refAspectPath[i]);
					aspectPathList.push(newPath);
				}
			}
		}
	});

    return aspectPathList;
};

JDY.view.getTextForAspectPaths = function getDisplayAttributesFor(aspectPaths, valueObject) {
	"use strict";

	var i,
		j,
		tmpObj,
		resultText = "";

	for (i = 0; i < aspectPaths.length; i++) {
		tmpObj = valueObject;
		for (j = 0; j < aspectPaths[i].length; j++) {
			if (j === aspectPaths[i].length - 1) {
				if(i > 0) {
					resultText = resultText + " - ";
				}
				resultText = resultText + tmpObj.val(aspectPaths[i][j]);
			} else {
				tmpObj = tmpObj.val(aspectPaths[i][j]);
			}
		}
	}

	return resultText;
};
/*
    json2.js
    2012-10-08

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html


    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.


    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array of strings.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the value

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array of strings, then it will be
            used to select the members to be serialized. It filters the results
            such that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.
*/

/*jslint evil: true, regexp: true */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
    call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
    getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
    lastIndex, length, parse, prototype, push, replace, slice, stringify,
    test, toJSON, toString, valueOf
*/


// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (typeof JSON !== 'object') {
    JSON = {};
}

(function () {
    'use strict';

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf())
                ? this.getUTCFullYear()     + '-' +
                    f(this.getUTCMonth() + 1) + '-' +
                    f(this.getUTCDate())      + 'T' +
                    f(this.getUTCHours())     + ':' +
                    f(this.getUTCMinutes())   + ':' +
                    f(this.getUTCSeconds())   + 'Z'
                : null;
        };

        String.prototype.toJSON      =
            Number.prototype.toJSON  =
            Boolean.prototype.toJSON = function (key) {
                return this.valueOf();
            };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string'
                ? c
                : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0
                    ? '[]'
                    : gap
                    ? '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']'
                    : '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    if (typeof rep[i] === 'string') {
                        k = rep[i];
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.prototype.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0
                ? '{}'
                : gap
                ? '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}'
                : '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                    typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.prototype.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/
                    .test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
                        .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
                        .replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function'
                    ? walk({'': j}, '')
                    : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());
