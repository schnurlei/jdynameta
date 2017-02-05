// jdyview slickgrid - Mapping to http://github.com/mleibman/slickgrid 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de
/*jslint browser: true, plusplus: true */
/*global $, Slick */

var JDY = JDY || {};
JDY.view = JDY.view || {};
JDY.view.slick = JDY.view.slick || {};


JDY.view.slick.createTableForClassInfo = function (aContainer, aClassInfo, valueList) {
    "use strict";

    var columns,
        options,
        dataView = new Slick.Data.DataView(),
        grid;

    function createColumns (aClassInfo) {

        var columns = [],
            curColumn;
        aClassInfo.forEachAttr(function (curAttrInfo) {

            if (curAttrInfo.isPrimitive()) {

                curColumn = curAttrInfo.getType().handlePrimitiveKey(JDY.view.slick.createColumnVisitor());
                curColumn.id = curAttrInfo.getInternalName();
                curColumn.name = curAttrInfo.getInternalName();
                curColumn.field = curAttrInfo.getInternalName();
                curColumn.attrInfo = curAttrInfo;
                columns.push(curColumn);
            } else {
                // don't add columns fot object references
            }
        });
        return columns;
    }

    function createRowData (aValueObj, columns) {

        var rowData = {},
            i;
        for (i = 0; i < columns.length; i++) {
            rowData[columns[i].id] = aValueObj.val(columns[i].attrInfo);
        }
        return rowData;
    }

    function reloadObjects () {

        var filter = new JDY.base.QueryCreator(aClassInfo).query();

        if (filterCreator) {
            filter = filterCreator.createFilter();
        }

        service.loadValuesFromDb(filter, function (allReadedObjs) {

            dataView.setItems(allReadedObjs);

//            var i,
//                data = [];
//            for (i = 0; i < allReadedObjs.length; i++) {
//                data.append(createRowData(allReadedObjs[i]));
//            }
//            aGrid.setData(data);
        });
    }

    options = {
        editable: true,
        enableAddRow: true,
        enableCellNavigation: true,
        asyncEditorLoading: false,
        autoEdit: false
    };

    columns = createColumns(aClassInfo);
    grid = new Slick.Grid(aContainer, dataView, columns, options);
    dataView.onRowCountChanged.subscribe(function (e, args) {
        grid.updateRowCount();
        grid.render();
    });

    dataView.onRowsChanged.subscribe(function (e, args) {
        grid.invalidateRows(args.rows);
        grid.render();
    });
    dataView.setItems(valueList);
    //reloadObjects(grid);
    grid.setActiveCell(0, 0);
    return grid;
};

JDY.view.slick.createColumnVisitor = function () {
    "use strict";

    return {
        handleBoolean: function (aType) {

            return {
                width: 40,
                minWidth: 20,
                maxWidth: 80,
                cssClass: "cell-title",
                formatter: Slick.Formatters.Checkmark,
                editor: Slick.Editors.Checkbox
            };
        },
        handleDecimal: function (aType) {

            return {
                width: 80,
                cssClass: "cell-title",
                editor: Slick.Editors.Text
            };
        },
        handleTimeStamp: function (aType) {
            return {
                width: 80,
                cssClass: "cell-title",
                editor: Slick.Editors.Date
            };
        },
        handleFloat: function (aType) {
            return {
                width: 80,
                cssClass: "cell-title",
                editor: Slick.Editors.Text
            };
        },
        handleLong: function (aType) {

            return {
                width: 80,
                cssClass: "cell-title",
                editor: Slick.Editors.Text
            };
        },
        handleText: function (aType) {

            return {
                width: 120,
                cssClass: "cell-title",
                editor: Slick.Editors.Text
            };
        },
        handleVarChar: function (aType) {

            return {
                width: 120,
                cssClass: "cell-title",
                editor: Slick.Editors.Text
            };
        },
        handleBlob: function (aType) {
            throw new JDY.base.JdyPersistentException("Blob Values not supported");
            //return aAttrValue;
        }
    };
};
