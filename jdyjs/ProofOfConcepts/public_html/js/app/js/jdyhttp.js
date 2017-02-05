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

