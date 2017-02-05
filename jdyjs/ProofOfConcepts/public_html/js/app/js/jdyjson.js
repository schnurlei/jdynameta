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
		objList = new JDY.base.ObjectList(curAssoc);
		if (Array.isArray(assocNode)) {
			for (i = 0; i < assocNode.length; i++) {
				if (typeof assocNode[i] === "object") {
					objList.add(this.createModelForJsonObj(assocNode[i], curAssoc.getDetailClass()));
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
			jsonObject.setAssocVals(that.createAssociationList(objToWrite.getValue(curAssoc), aPersistenceType));
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