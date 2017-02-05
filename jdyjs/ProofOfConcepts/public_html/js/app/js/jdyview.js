// jdyview, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.createInputComponentVisitor
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
 * 
 * @param {type} aDivElem element in wich the table is inserted
 * @param {type} aClassInfo
 * * @param {type} filterCreator
 * @param {type} service - service that reads from db and write changes to db
 * @returns {JDY.view.createTableForClassInfo.Anonym$23}
 */
JDY.view.createTableForClassInfo = function (aDivElem, aClassInfo, service, filterCreator) {
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

	editForm = JDY.view.form.createDivFormForClassInfo(aClassInfo, service);
	editForm.getForm().appendTo("body");
	editForm.getForm().hide();

	function createActionElem(anAct, aValueObj) {
		var newActElem = $("<button>").text(anAct.getLabel());
		newActElem.puibutton({  
						icon: anAct.icon  
						,iconPos: 'right'  
					});
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

		service.deleteObjectInDb(anObject, successFunc, failFunc);

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
				return "";
			},
			handler: removeObject,
			icon :'ui-icon-trash'
		});
		columnActions.push({
			getLabel : function () {
				return "";
			},
			handler: changeObject,
			icon :'ui-icon-pencil'
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

			value.assocVals("Classes").done(function (allSubValues) {

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