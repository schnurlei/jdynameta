// jdyview -form, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true, strict: false, plusplus: true */
/*global $, moment */


var JDY = JDY || {};
JDY.view = JDY.view || {};
JDY.view.form = JDY.view.form || {};

JDY.view.openDynamicFormDialog = function (aForm, aFormTitel) {
	"use strict";

	return aForm.getForm().dialog({
		autoOpen: false,
		height: 600,
		width: 500,
		modal: true,
		show: 'clip',
		title: aFormTitel,
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

/**
 * Create Formular seperated by HTML DIV Elements 
 * for the given ClassInfo object
 *
 * @param {JDY.base.ClassInfo} aClassInfo - class info to create panel
 * @param {type} service to get data
 */
JDY.view.form.createDivFormForClassInfo = function (aClassInfo, service) {
	"use strict";

	var formElem = $("<form>"),
		allFieldComps;

	formElem.validate();
	allFieldComps = JDY.view.form.addAttributesToDom(formElem, aClassInfo, null, service);

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


JDY.view.form.addAttributesToDom = function (aParentElem, aClassInfo, aParentRef, service) {
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

	function createLabel(anAttrInfo) {
		return $("<label>")
				.addClass("jdyLbl")
				.attr("for", "name")
				.attr("width", "250")
				.text($.t(JDY.i18n.key(aClassInfo,anAttrInfo)));
	}

	function addFieldAttributes(anAttrInfo, aField, aParentName) {
		
		aField.attr("name", anAttrInfo.getInternalName());
		aField.attr("id", aParentName + anAttrInfo.getInternalName());
		aField.addClass("jdyField");
		if (anAttrInfo.isGenerated) {
			aField.attr('readonly', true);
		} 
	}

	
	aClassInfo.forEachAttr(function (curAttrInfo) {

		var rowElem,
			typeHandler,
			parentName = "",
			primitiveTypeVisitor = JDY.view.form.createJqueryInputComponentVisitor(curAttrInfo);

		rowElem = $("<div>").addClass("jdyDivRow");
		rowElem.appendTo(aParentElem);
		if (curAttrInfo.isPrimitive()) {

			createLabel(curAttrInfo).appendTo(rowElem);

			typeHandler = curAttrInfo.getType().handlePrimitiveKey(primitiveTypeVisitor);
			addFieldAttributes(curAttrInfo, typeHandler.field, parentName);

			rowElem.append(typeHandler.field);
			if (typeHandler.initElem) {
				typeHandler.initElem();
			}
			allFieldComponents.push(createAttributeComponent(curAttrInfo, typeHandler));

		} else {

			allFieldComponents.push(JDY.view.form.addObjectReference(curAttrInfo, rowElem, aParentRef, service));
		}
	});
	return allFieldComponents;

};

JDY.view.form.createJqueryInputComponentVisitor = function (anAttr) {
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
				getValue: function () {
					var result = fieldElem.val();
					result = (result && result.trim().length > 0) ? Number(result) : null;
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				},
				initElem: function () {
					fieldElem.rules("add", {
						min: aType.minValue,
						max: aType.maxValue,
						number: true,
						required: (anAttr.isNotNull ? true : false)
					});
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
					if (aType.datePartUsed && !aType.timePartUsed) {

						fieldElem.rules("add", {
							date: true,
							required: (anAttr.isNotNull ? true : false)
						});
					} else {
						fieldElem.rules("add", {
							required: (anAttr.isNotNull ? true : false)
						});
					}
				}
			};


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
				},
				initElem: function () {
					fieldElem.rules("add", {
						number: true,
						required: (anAttr.isNotNull ? true : false)
					});
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
				},
				initElem: function () {
					fieldElem.rules("add", {
						min: aType.minValue,
						max: aType.maxValue,
						number: true,
						required: (anAttr.isNotNull ? true : false)
					});
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
				getValue: function () {
					var result = fieldElem.val();
					if (isSelect) {
						result = (!result || result === "null") ? null : result;
					}
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				},
				initElem: function () {
					fieldElem.rules("add", {
					minlength: 0,
					maxlength: aType.length,
					required: (anAttr.isNotNull ? true : false)
					});
				}
			};
		},

		handleVarChar: function (aType) {

			var fieldElem = $("<textarea>");
			return {
				field: fieldElem,
				getValue: function () {
					var result = fieldElem.val();
					return result;
				},
				setValue: function (aValue) {
					fieldElem.val(aValue);
				},
				initElem: function () {
					fieldElem.rules("add", {
					minlength: 0,
					maxlength: aType.length,
					required: (anAttr.isNotNull ? true : false)
					});
				}
			};
		},

		handleBlob: function (aType) {
			throw new JDY.base.JdyPersistentException("Blob Values not supported");
			//return aAttrValue;
		}
	};
};

JDY.view.form.addObjectReference = function (aObjReference, aRowElem, aParentRef, service) {
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

		allFieldComponents = JDY.view.form.addAttributesToDom(referenceElem, aObjReference.referencedClass, aObjReference, service);

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
