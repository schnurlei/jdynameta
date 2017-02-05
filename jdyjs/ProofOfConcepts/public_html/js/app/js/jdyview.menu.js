// jdyview.html5, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


/*jslint browser: true, strict: false, plusplus: true */
/*global $, moment */

var JDY = JDY || {};

JDY.view = JDY.view || {};

JDY.view.createPrimeFacesMenu = function (allObjects, aMenuConfig) {
	"use strict";

	var menuUl = $("<ul>");
	menuUl.attr("id", "primeID");
	menuUl.append($("<li>").append($("<h3>").text("Repositories")));

	$.each(allObjects, function (index, value) {
		
		var curMenuLi = $("<li>").append($("<a>").text(value[aMenuConfig.attrName]));
		menuUl.append(curMenuLi);
		curMenuLi.click(function () {
			if (aMenuConfig.callBack) {
				aMenuConfig.callBack({lvl1: value, lvl2: null});
			}
		});
		if (aMenuConfig.lvl2Assoc) {
			value.assocVals("Classes").done(function (allSubValues) {
				var subMenuUl = $("<ul>");
				subMenuUl.appendTo(curMenuLi);
				subMenuUl.append($("<li>").append($("<h4>").text("Classes")));
				$.each(allSubValues, function (index, subValue) {
					var curSubMenuLi = $("<li>").append($("<a>").text(subValue[aMenuConfig.lvl2AttrName]));
					subMenuUl.append(curSubMenuLi);
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
			fieldElem.puiinputtext();  
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
			fieldElem.puiinputtext();  
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
			fieldElem.puiinputtext();  
			
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
			fieldElem.puiinputtext();  
			
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
			fieldElem.puiinputtext();  
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
