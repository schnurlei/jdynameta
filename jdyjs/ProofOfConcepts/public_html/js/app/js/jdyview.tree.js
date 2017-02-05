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