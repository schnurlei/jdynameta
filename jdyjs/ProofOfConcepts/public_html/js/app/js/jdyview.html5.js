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
		formDiv = $("<div>"),
		allFieldComps;
	formElem.append(formDiv);
	formDiv.addClass("shapeshiftDiv");
	allFieldComps = JDY.view.addHtml5AttributesToDom(formDiv, aClassInfo, null, service);

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
		rowElem.attr("data-ss-colspan","1");
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
