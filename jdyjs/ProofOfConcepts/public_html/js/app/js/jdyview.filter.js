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

		exprObj.attrSelect.puidropdown();
		exprObj.operatorSelect.puidropdown();

		exprObj.deleteExprActElem.addClass("jdyDelete");
		//exprObj.deleteExprActElem.text("x");
		exprObj.deleteExprActElem.puibutton({  
						icon: 'ui-icon-trash'  
						,iconPos: 'right'  
					});
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