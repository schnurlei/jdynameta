/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var JDY = JDY || {};

JDY.view = JDY.view || {};

JDY.view.nodeConfig = {
	AppRepository: {
		icon: "jdyapp/tree-diagramm.png",
		nameAspects: [["Name"]]
	},

	AppClassInfo: {
		icon: "jdyapp/typeClass.png",
		nameAspects: [["Name"]]
	},
	AppBooleanType: {
		icon: "typeBoolean.png",
		nameAspects: [["Name"]]

	},
	AppLongType: {
		icon: "input_integer.png",
		nameAspects: [["Name"]]

	},
	AppTextType: {
		icon: "input_text.png",
		nameAspects: [["Name"]]
	},
	AppBlobType: {
		nameAspects: [["Name"]]

	},
	AppDecimalType: {
		icon: "input_decimal.png",
		nameAspects: [["Name"]]

	},
	AppFloatType: {
		icon: "input_float.png",
		nameAspects: [["Name"]]
	},
	AppTimestampType: {
		icon: "calendar.png",
		nameAspects: [["Name"]]
	},
	AppVarCharType: {
		icon: "typeVarchar.png",
		nameAspects: [["Name"]]
	},
	AppObjectReference: {
		icon: "jdyapp/typeReference.png",
		nameAspects: [["Name"]]
	}
};

JDY.view.createNewAttr = function (anAssoc, detailClass, parentNode, parentObj, service) {
	"use strict";
	var valObj,
		saveCallback,
		editForm,
		dialog;

	editForm = JDY.view.form.createDivFormForClassInfo(detailClass, service);
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


			newNode = JDY.view.createNodeObForValObj(createdValObj, JDY.view.nodeConfig);
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


function useToolForShapes(aPosX, aPosY, allShapes, aTool) {
	var i;
	for (var i=0; i < allShapes.length; i++) {
		if (allShapes[i].isPointInside(aPosX, aPosY)) {
			allShapes[i].useTool(aTool);
		}
	}
}


function drawWithRaphael() {

	var paper = Raphael(0, 0, 600, 400),
		metaRepo = JDY.meta.createAppRepository(),
		i, subClassInfo, 
		detailClass = metaRepo.getClassInfo("AppPrimitiveAttribute"),
		config,
		curShape,
		allShapes =[];
	
	function useToolCallBack(aPosX, aPosY, aTool) {
		useToolForShapes(aPosX, aPosY, allShapes, aTool);
	}
	
	for ( i=0; i < 3; i++) {
		curShape = createEntityShape();
		curShape.draw(paper,10+i*110,50);

		allShapes.push(curShape);
	}
	
	for (i = 0; i < detailClass.getSubclassesRec().length; i++) {
		subClassInfo = detailClass.getSubclassesRec()[i];
		if (!subClassInfo.isAbstract) {
			createTool(subClassInfo, useToolCallBack).draw(paper, 10+(i*20),10);
		}
	}

}

function createEntityShape()
{
	var shape = {},
		shapeWidth =100,
		shapeHeigth =150;

	function moveTo(aPosX, aPosY) {
		shape.seperator.attr({path: "M" + aPosX + " " + (aPosY+15) +"H"+  (aPosX+shapeWidth)});
		shape.allElements.attr({x: aPosX, y: aPosY}); 
	};

	
	function dragMove(dx, dy) {
		moveTo ( (this.ox +dx), (this.oy+dy));
	};
	
	function dragStart() {
		this.ox = this.attr("x");
        this.oy = this.attr("y");
	}
	function dragEnd() {
	}	

	function markShape() {
		shape.border.attr ("stroke", 'black');
	}	
	
	function clearShapeMark() {
		shape.border.attr ("stroke", 'grey');
	}	
	
	
	function init(aPaper, aPosX, aPosY) {
		
		shape.border = aPaper.rect(aPosX, aPosY, shapeWidth, shapeHeigth, 5).attr({
					fill: 'white',
					stroke: 'grey',
					'stroke-width': 2
				});
		shape.seperator = aPaper.path("M" + aPosX + " " + (aPosY+15) +"H"+  (aPosX+shapeWidth));	
		shape.title = aPaper.text(aPosX, aPosY, "Start");
		shape.title.matrix.translate(20,5);
		shape.title.attr({transform: shape.title.matrix.toTransformString()});
		
		shape.allElements = aPaper.set();
		shape.allElements.push(shape.border);
		shape.allElements.push(shape.seperator);
		shape.allElements.push(shape.title);
	}
	
	shape.draw = function (aPaper, aPosX, aPosY) {
		
		init(aPaper, aPosX, aPosY);
		
		shape.border.drag(dragMove, dragStart, dragEnd);
		shape.border.mouseover(function() {
			shape.allElements.toFront();
			markShape();
		});
		shape.border.mouseout(function() {
			clearShapeMark();
		});
	};
	
	shape.isPointInside = function (aPosX, aPosY) {
		return shape.border.isPointInside(aPosX, aPosY);
	};
	
	shape.useTool = function (tool) {
		
		var service =  new JDY.db.TaffyObjectReaderWriter();
		JDY.view.createNewAttr(null, tool.subClassInfo, null, null, service);
		shape.border.attr ("stroke", 'red');
	};

	return shape;
}

function createTool(aSubClassInfo, useToolCallBack)
{
	var shape = {},
		config;

	function moveTo(aPosX, aPosY) {
		shape.image.attr({x: aPosX, y: aPosY});
	};
	
	
	function dragMove(dx, dy) {
		this.toFront();
		moveTo ( (this.ox +dx), (this.oy+dy));
	};
	
	function dragStart() {

		this.toFront();
		this.ox = this.attr("x");
        this.oy = this.attr("y");
	}
	function dragEnd() {
		var posX = this.attr("x"),
			posY = this.attr("y");
		moveTo ( (this.ox ), (this.oy));
		useToolCallBack(posX, posY, shape);
	}	
	
	config = JDY.view.nodeConfig[aSubClassInfo.getInternalName()];
	
	shape.draw = function (aPaper, aPosX, aPosY) {
		
		shape.image = aPaper.image("raphael/images/"+config.icon, aPosX,aPosY,16,16);
		shape.image.drag(dragMove, dragStart, dragEnd);	
	};
	
	shape.subClassInfo = aSubClassInfo;

	return shape;
}