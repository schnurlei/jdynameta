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

JDY.meta.convertAppRepositoryToRepository = function (appRepository, callback) {

	appRepository.assocVals("Classes").done( function(appClassInfos) {

	    var newRepository = new JDY.base.Repository(appRepository.applicationName),
			appClassInfo,
			allPromises = [],
			i;

	    for (i = 0; i < appClassInfos.length; i++) {
		    appClassInfo = appClassInfos[i];		
		    newRepository.addClassInfo(appClassInfo.InternalName).setAbstract(appClassInfo.isAbstract);
	    }

	    for (i = 0; i < appClassInfos.length; i++) {
		    appClassInfo = appClassInfos[i];		
		    allPromises.push(JDY.meta.buildAttrForMetaRepo(newRepository, appClassInfo));
	    }

	    for (i = 0; i < appClassInfos.length; i++) {
		    appClassInfo = appClassInfos[i];		
		    allPromises.push(JDY.meta.buildAssocsForMetaRepo(newRepository, appClassInfo));
	    }

	    for (i = 0; i < appClassInfos.length; i++) {
		    appClassInfo = appClassInfos[i];		
		    JDY.meta.buildSubclassesForMetaRepo(newRepository, appClassInfo);
	    }
	    
	    $.when.apply(allPromises).then(callback(newRepository));
	});
};

JDY.meta.addClassToMetaRepo = function(metaRepo, anAppClassInfo) {

	var newMetaClass = metaRepo.addClassInfo(anAppClassInfo.InternalName).setAbstract(anAppClassInfo.isAbstract);
	newMetaClass.setAbstract(anAppClassInfo.isAbstract);
	newMetaClass.setExternalName(anAppClassInfo.InternalName);
	newMetaClass.setShortName(anAppClassInfo.InternalName);
};

JDY.meta.buildAssocsForMetaRepo= function(metaRepo, anAppClassInfo)	{

	var dfrd = $.Deferred();

	anAppClassInfo.assocVals("Associations").done( function(appAssocs) {

		var i,
			appAssoc,
			metaMasterClass,
			metaClass = metaRepo.getClassInfo(anAppClassInfo.InternalName),
			metaMasterClassRef,
			appAssocName,
			metaAssoc;

		for (i = 0; i < appAssocs.length; i++) {

			appAssoc = appAssocs[i];

			metaMasterClass =  metaRepo.getClassInfo(appAssoc.masterClassReference.Masterclass.InternalName); 
			metaMasterClassRef = metaMasterClass.getAttr(appAssoc.masterClassReference.InternalName);
			appAssocName = appAssoc.NameResource;			
			metaAssoc = new JDY.base.AssociationModel(metaMasterClassRef, metaMasterClass, appAssocName);
			metaClass.addAssociation(metaAssoc);
		}
		dfrd.resolve();
	});
	
	return dfrd.promise();
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
		appAttr,
		metaAttr,
		metaClass = metaRepo.getClassInfo(anAppClassInfo.InternalName),
		refClass;

    var dfrd = $.Deferred();

	anAppClassInfo.assocVals("Attributes").done( function(appAttributes) {

		var appDomainVals,
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
						appDomainVals = appAttr.assocVals("DomainValues");
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
						appDomainVals = appAttr.assocVals("DomainValues");
						domainVals = [];
						if (appDomainVals) {
							for (j = 0; j < appDomainVals.length; j++) {
								domainVals.push({dbValue:appDomainVals[j].dbValue, representation:appDomainVals[j].representation});
							}
						}
						metaAttr = metaClass.addLongAttr(appAttr.InternalName, appAttr.MinValue , appAttr.MaxValue, domainVals);

					break;
				case 'AppTextType':
						appDomainVals = appAttr.assocVals("DomainValues");
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
		
		dfrd.resolve();
	});

	return dfrd.promise();
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

	