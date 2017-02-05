/* 
 * JdynaMeta Model - Mapping to Taffy Db 
 * jdynameta, 
 * Copyright (c)2013 Rainer Schneider, Roggenburg.
 * Distributed under Apache 2.0 license
 * http://jdynameta.de
 */


var JDY = JDY || {};
JDY.taffy = JDY.taffy || {};

// http://www.taffydb.com/
JDY.taffy.TaffyObjectReaderWriter = function () {
	"use strict";

	this.repoName2DbMap = {};
	this.defaultFailFunct = function(error) {
		console.error(error);
	};
};

JDY.taffy.TaffyObjectReaderWriter.prototype.loadValuesFromDb = function (aFilter, successFunct, failFunc) {
	"use strict";

	var typeDb = this.getDbForType(this.getBaseType(aFilter.resultType));

	var resultList = [],
		i, dbResult, taffyExpr;


	if (aFilter.getFilterExpression()) {
		taffyExpr = aFilter.getFilterExpression().visit(JDY.taffy.createExprVisitor());
		console.log(taffyExpr);
		dbResult = typeDb(taffyExpr).get();
	} else {
		dbResult = typeDb().get();
	}

	if (Array.isArray(dbResult)) {

		for (i = 0; i < dbResult.length; i++) {

			if (typeof dbResult[i] === "object") {
				resultList.push(this.createModelForJsonObj(dbResult[i], aFilter.resultType));
			} else {
				throw new JDY.base.JdyPersistentException("Error parsing JSON. No JSONObject: " + dbResult[i].toString());
			}
		}
	}

	successFunct(resultList);

};

JDY.taffy.TaffyObjectReaderWriter.prototype.resolveAssoc = function (anAssocInfo, aMasterObj, aCallback){

	var typeDb = this.getDbForType(this.getBaseType(anAssocInfo.getDetailClass())),
		searchObject = this.createTaffyDbKey(aMasterObj,true),
		dbResult,
		resultList = [],
		i;
	dbResult = typeDb(function () { return true }).get();
	
	dbResult = typeDb(function () {
		return JDY.taffy.objectCompare(searchObject, this[anAssocInfo.getMasterClassReference().internalName], false);
	}).get();

	if (Array.isArray(dbResult)) {

		for (i = 0; i < dbResult.length; i++) {

			if (typeof dbResult[i] === "object") {
				resultList.push(this.createModelForJsonObj(dbResult[i], anAssocInfo.getDetailClass()));
			} else {
				throw new JDY.base.JdyPersistentException("Error parsing JSON. No JSONObject: " + dbResult[i].toString());
			}
		}
	}

	aCallback(resultList);

};

JDY.taffy.createExprVisitor = function createExprVisitor(parentExpr) {
	
	return {
		
		visitOperatorExpression: function(aOpExpr) {

			var operatorExpr = {};

			if(!parentExpr) {
				parentExpr = {};
			}
			
			parentExpr[aOpExpr.attributeInfo.getInternalName()] =  operatorExpr;
			operatorExpr[aOpExpr.getOperator().visitOperatorHandler(JDY.taffy.createOperatorVisitor())] = aOpExpr.compareValue;

			return parentExpr;
		},
		
		visitOrExpression: function(anOrExpr) {
		    
		    var i, taffyExpr, subExpr;
			
			taffyExpr = [];
		    for ( i=0; i < anOrExpr.expressionVect.length ; i++) {
				subExpr = anOrExpr.expressionVect[i].visit(JDY.taffy.createExprVisitor());
				taffyExpr.push( subExpr);
		    }
			
			return taffyExpr;
		},
		visitAndExpression: function(anAndExpr) {
		    var i, taffyExpr;
			taffyExpr = {};
		    for ( i=0; i < anAndExpr.expressionVect.length; i++) {
				anAndExpr.expressionVect[i].visit(JDY.taffy.createExprVisitor(taffyExpr));
		    }
			return taffyExpr;
		}
	};
};

JDY.taffy.createOperatorVisitor = function createOperatorVisitor() {
	
	return {
		
		visitEqualOperator : function(expr) {
		    return "is";
		},
			
		visitGreatorOperator : function(expr) {
		    return "gt";
		},
			
		visitLessOperator : function(expr) {
		    return "lt";
		}
	};
};


JDY.taffy.TaffyObjectReaderWriter.prototype.insertObjectInDb = function (aObjToInsert, successFunct, failFunc) {
	"use strict";
	var typeDb = this.getDbForType(this.getBaseType(aObjToInsert.$typeInfo)),
		result,
		taffyObj;

	if (this.searchTaffyDbFor(aObjToInsert).count() > 0) {
		if (failFunc) {
			failFunc("Object already in DB : ");
		} else {
			this.defaultFailFunct("Object already in DB : ");
		}
	} else {
		try {	

			taffyObj = this.createTaffyDbObject(aObjToInsert);
			typeDb.insert(taffyObj);
			result =this.getObjectFromDb(aObjToInsert);
			if(successFunct) {
				successFunct(this.getObjectFromDb(aObjToInsert));
			}
		} catch (excp) {
			if (failFunc) {
				failFunc(excp);
			} else {
				this.defaultFailFunct(excp);
			}
		}
	}
};

JDY.taffy.TaffyObjectReaderWriter.prototype.deleteObjectInDb  = function (aObjToDelete, successFunct, failFunc) {

	try {
		this.searchTaffyDbFor(aObjToDelete).remove();
		if(successFunct) {
			successFunct();
		}
	} catch (excp) {
		if (failFunc) {
			failFunc(excp);
		} else {
			this.defaultFailFunct(excp);
		}
	}
};

JDY.taffy.TaffyObjectReaderWriter.prototype.updateObjectInDb = function (aObjToUpdate, successFunct, failFunc) {
	"use strict";

	var updateDbObj = this.createTaffyDbObject(aObjToUpdate,false),
		typeDb = this.getDbForType(this.getBaseType(aObjToUpdate.$typeInfo));

	try {
		this.searchTaffyDbFor(aObjToUpdate).update(updateDbObj);

		if(successFunct) {
			successFunct(this.getObjectFromDb(aObjToUpdate));
		}
	} catch (excp) {
		if (failFunc) {
			failFunc(excp);
		} else {
			this.defaultFailFunct(excp);
		}
	}
};



JDY.taffy.TaffyObjectReaderWriter.prototype.searchTaffyDbFor = function (aJdySearchObj) {
	"use strict";

	var typeDb = this.getDbForType(this.getBaseType(aJdySearchObj.$typeInfo)),
		searchObject = this.createTaffyDbKey(aJdySearchObj,true);

	return typeDb(function () {
		return JDY.taffy.objectCompare(searchObject, this, false);
	});
};

JDY.taffy.TaffyObjectReaderWriter.prototype.getTaffyDbFor = function (aJdyObjSearch) {
	"use strict";

	var result;

	result = this.searchTaffyDbFor(aJdyObjSearch);

	if (result.count() === 0 ) {
		throw new JDY.base.JdyPersistentException("Object not in  DB : " + searchObject);
	}

	if (result.count() > 1 ) {
		throw new JDY.base.JdyPersistentException("More than one Object in DB : " + searchObject);
	}

	return result;
};

JDY.taffy.TaffyObjectReaderWriter.prototype.getObjectFromDb = function (aJdyObjSearch) {
	"use strict";

	var result;

	result = this.getTaffyDbFor(aJdyObjSearch);

	return this.createModelForJsonObj(result.first(),aJdyObjSearch.$typeInfo);
};

JDY.taffy.TaffyObjectReaderWriter.prototype.createNewObject = function (aTypeInfo) {
	"use strict";
	return new JDY.base.TypedValueObject(aTypeInfo, this, false);
};



JDY.taffy.TaffyObjectReaderWriter.prototype.getDbForType = function (aType) {
	"use strict";
	
	var dbName = aType.getRepoName() +"." + aType.getInternalName(),
		taffyDb = this.repoName2DbMap[dbName];
	
	
	
	if(!taffyDb) {
		taffyDb = TAFFY();
		taffyDb.store(dbName);
		this.repoName2DbMap[dbName] = taffyDb;
	}
	
	return taffyDb;
};


JDY.taffy.TaffyObjectReaderWriter.prototype.addMetaDataFields = function (jsonObject, aClassInfo, asProxy) {
	"use strict";

	jsonObject[JDY.json.NAMESPACE_TAG] =  aClassInfo.getRepoName();
	jsonObject[JDY.json.CLASS_INTERNAL_NAME_TAG] = aClassInfo.getInternalName();
	if (asProxy) {
		jsonObject[JDY.json.PERSISTENCE_TAG] = JDY.json.Operation.PROXY;
	}
};

JDY.taffy.TaffyObjectReaderWriter.prototype.jsonValueGetVisitor = function (aAttrValue) {
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
			return aAttrValue;
		}
	};
};

JDY.taffy.TaffyObjectReaderWriter.prototype.createTaffyDbKey = function createTaffyDbKey(objToWrite) {
	"use strict";

	var jsonObject = {},
		attrValue,
		that = this,
		refJsonNode;


	this.addMetaDataFields(jsonObject, objToWrite.$typeInfo, false);

	objToWrite.$typeInfo.forEachAttr(function (curAttrInfo) {

		if (curAttrInfo.isKey()) {
			attrValue = objToWrite[curAttrInfo.getInternalName()];

			if (attrValue === undefined) {
				throw new JDY.base.JdyPersistentException("Missing value for type in attr value: " + curAttrInfo.getInternalName());
			} else {
				if (attrValue !== null) {

					if (curAttrInfo.isPrimitive()) {

						jsonObject[curAttrInfo.getInternalName()] = curAttrInfo.getType().handlePrimitiveKey(that.jsonValueGetVisitor(attrValue));
					} else {
						refJsonNode = that.createTaffyDbKey(attrValue);
						jsonObject[curAttrInfo.getInternalName()] =  refJsonNode;
					}
				} else {
					jsonObject[curAttrInfo.getInternalName()] =  null;
				}
			}
		}
	});

	return jsonObject;
};

JDY.taffy.TaffyObjectReaderWriter.prototype.createTaffyDbObject = function createTaffyDbObject(objToWrite, asProxy) {
	"use strict";

	var jsonObject = {},
		attrValue,
		that = this,
		refJsonNode;


	this.addMetaDataFields(jsonObject, objToWrite.$typeInfo, asProxy);

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
						refJsonNode = that.createTaffyDbObject(attrValue, true);
						jsonObject[curAttrInfo.getInternalName()] =  refJsonNode;
					}
				} else {
					jsonObject[curAttrInfo.getInternalName()] =  null;
				}
			}
		}
	});


//	jsonObject.$assocs = {};
//	objToWrite.$typeInfo.forEachAssoc(function (curAssoc) {
//		jsonObject.$assocs[curAssoc.getAssocName()] = that.createAssociationList(objToWrite.assocVals(curAssoc));
//	});

	return jsonObject;
};

JDY.taffy.TaffyObjectReaderWriter.prototype.createAssociationList = function (curAssoc) {
	"use strict";

	var objList = [],
		i;

	if (curAssoc === null) {
		objList = [];
	} else {
		objList = [];
		if (Array.isArray(curAssoc)) {
			for (i = 0; i < curAssoc.length; i++) {
				if (typeof curAssoc[i] === "object") {
					objList.push(this.createTaffyDbObject(curAssoc[i], true));
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

JDY.taffy.TaffyObjectReaderWriter.prototype.readObjectList = function (aJsonNode, aClassInfo) {
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

JDY.taffy.TaffyObjectReaderWriter.prototype.createModelForJsonObj = function createModelForJsonObj(aJsonNode, aClassInfo, asProxy) {
	"use strict";

	var concreteClass = this.createClassInfoFromMeta(aJsonNode, aClassInfo),
		persistenceType = aJsonNode[JDY.json.PERSISTENCE_TAG],
		result = null,
		attrValue,newValue,
		that = this;

	asProxy = asProxy || persistenceType === JDY.json.Operation.PROXY;
	result = new JDY.base.TypedValueObject(concreteClass, this, asProxy);
	concreteClass.forEachAttr(function (curAttrInfo) {

		if (persistenceType !== JDY.json.Operation.PROXY || curAttrInfo.isKey()) {
			attrValue = aJsonNode[curAttrInfo.getInternalName()];

			if (attrValue === undefined) {
				throw new JDY.base.JdyPersistentException("Missing value for type in attr value: " + curAttrInfo.getInternalName());
			} else {
				if (attrValue !== null) {

					if (curAttrInfo.isPrimitive()) {

						newValue = curAttrInfo.getType().handlePrimitiveKey(that.jsonValueGetVisitor(attrValue));
					} else {

						if (typeof attrValue === "object") {
							newValue = that.createModelForJsonObj(attrValue, curAttrInfo.getReferencedClass());
						} else {
							throw new JDY.base.JdyPersistentException("Wrong type for attr value (no object): " + curAttrInfo.getInternalName());
						}
					}
				} else {
					newValue = null;
				}
			}
			
			if( asProxy ) {
				result.setProxyVal(curAttrInfo, newValue);
			} 
			result.setVal(curAttrInfo, newValue);
		}
	});
//	if (persistenceType !== JDY.json.Operation.PROXY) {
//
//		concreteClass.forEachAssoc(function (curAssoc) {
//
//			result.$assocs[curAssoc.getAssocName()] = that.createAssociationList(aJsonNode, result, curAssoc);
//		});
//	}

	return result;
};

JDY.taffy.TaffyObjectReaderWriter.prototype.createClassInfoFromMeta = function getConcreteClass(jsonObj, aClassInfo) {
	"use strict";

	var repoName = jsonObj[JDY.json.NAMESPACE_TAG],
		classInternalName = jsonObj[JDY.json.CLASS_INTERNAL_NAME_TAG];
	return this.getConcreteClass(aClassInfo, repoName, classInternalName);
};

JDY.taffy.TaffyObjectReaderWriter.prototype.getBaseType = function (aType) {

	var result = aType;

	while (result.getSuperclass()) {
		result = result.getSuperclass();
	}
	
	return result;
};


JDY.taffy.TaffyObjectReaderWriter.prototype.getConcreteClass = function getConcreteClass(aClassInfo, aRepoName, classInternalName) {
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

// http://acatalept.com/blog/2009/10/09/compare-objects-recursively-in-javascript/
JDY.taffy.objectCompare = function objectCompare(searchObj, compareObj, loose) {
 
  // if loose is NOT TRUE, first do a quick comparison of lengths
  // quick & dirty attempt to avoid comparing all property values on large or complex objects
  var i;
    for (i in searchObj) {
      if (typeof searchObj[i] === 'object' && typeof compareObj[i] === 'object') { // both items are objects
        if (!objectCompare(searchObj[i], compareObj[i], loose)){ // see if objects match
          return false;
        }
      }
      else if (
        loose // loose comparison, 42 == "42", null == '' == undefined
        &&
        !(
          (searchObj[i] || '') == (compareObj[i] || '') // this will work in all cases EXCEPT ("0" ?= 0|null|undefined), which will evaluate to ("0" ?= ""), which is of course FALSE
          ||
          ( // if either value is a number, try to convert both to numbers and compare - this will work around above exception
            (typeof searchObj[i] === 'number' || typeof compareObj[i] === 'number')
            &&
            Number(searchObj[i]) === Number(compareObj[i])
          )
        )
      ){ // only one item is an object, or objects don't match
        return false;
      }
      else if (
        !loose // strict comparison, 42 != "42", null != '' != undefined
        &&
        !(searchObj[i] === compareObj[i])
      ){
        return false;
      }
    }
 
  return true; // all tests passed, objects are equivalent
};