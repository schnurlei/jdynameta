// jdynameta, 
// Copyright (c)2012 Rainer Schneider, Roggenburg.
// Distributed under Apache 2.0 license
// http://jdynameta.de


var JDY = JDY || {};

JDY.i18n = {};

JDY.i18n.key = function (aClassInfo, aAttrInfo) {
	
	return aClassInfo.getRepoName() + "." + aClassInfo.getInternalName()+'.'+aAttrInfo.getInternalName();
};

JDY.i18n.METADATA_RESOURCES =  {
	dev: { 
		  translation: { 
			  ApplicationRepository : 
				{ AppBooleanType: { 
					isGenerated : 'generiert?'
					, isKey: 'Schlüssel' 
					, temp : 'temp'
					, Name: 'name'
					, InternalName : 'InternalName'
					, isNotNull: 'not Null?'
					, AttrGroup: 'AttrGroup'
					, pos: 'pos'
				   },
			  AppDecimalType: {
				  Scale: 'Scale',
				  MinValue: 'Min. Value',
				  MaxValue: 'Max. Value',
				  isGenerated : 'generiert?',
				  isKey: 'Schlüssel', 
				  Name: 'name',
				  InternalName : 'InternalName',
				  isNotNull: 'not Null?',
				  AttrGroup: 'AttrGroup',
				  pos: 'pos'
			  },
			  AppFloatType: {
				  Scale: 'Scale',
				  MinValue: 'Min. Value',
				  MaxValue: 'Max. Value',
				  isGenerated : 'generiert?',
				  isKey: 'Schlüssel', 
				  Name: 'name',
				  InternalName : 'InternalName',
				  isNotNull: 'not Null?',
				  AttrGroup: 'AttrGroup',
				  pos: 'pos'
			  },
			  AppLongType: {
				  Scale: 'Scale',
				  MinValue: 'Min. Value',
				  MaxValue: 'Max. Value',
				  isGenerated : 'generiert?',
				  isKey: 'Schlüssel', 
				  Name: 'name',
				  InternalName : 'InternalName',
				  isNotNull: 'not Null?',
				  AttrGroup: 'AttrGroup',
				  pos: 'pos'
			  },
			  AppTextType: {
				  length: 'length:',
				  typeHint: 'typeHint:',
				  isGenerated : 'generiert?',
				  isKey: 'Schlüssel', 
				  Name: 'name',
				  InternalName : 'InternalName',
				  isNotNull: 'not Null?',
				  AttrGroup: 'AttrGroup',
				  pos: 'pos'
			  },
			  AppTimestampType: {
				  isDatePartUsed: 'isDatePartUsed:',
				  isTimePartUsed: 'isTimePartUsed:',
				  isGenerated : 'generiert?',
				  isKey: 'Schlüssel', 
				  Name: 'name',
				  InternalName : 'InternalName',
				  isNotNull: 'not Null?',
				  AttrGroup: 'AttrGroup',
				  pos: 'pos'
			  },
			  AppVarCharType: {
				  mimeType: 'mimeType',
				  length: 'length',
				  isClob: 'isClob',
				  isGenerated : 'generiert?',
				  isKey: 'Schlüssel', 
				  Name: 'name',
				  InternalName : 'InternalName',
				  isNotNull: 'not Null?',
				  AttrGroup: 'AttrGroup',
				  pos: 'pos'
			  }	
		  }

	  }
	},
	de: { translation: 
		   { ApplicationRepository : 
				{ AppBooleanType : 
					{ isGenerated : 'generiert?'
					, isKey: 'Schlüssel' 
					, temp : 'temp'
					, Name: 'name'
					, InternalName : 'InternalName'
					, isNotNull: 'not Null?'
					, AttrGroup: 'AttrGroup'
					, pos: 'pos'
					} 
				}
		   }
		},
	en: { translation: 
		   {ApplicationRepository : 
				{ AppBooleanType : 
					{ isGenerated : 'generated?'
					, isKey: 'Key' 
					, temp : 'temp'
					, Name: 'name'
					, InternalName : 'InternalName'
					, isNotNull: 'not Null?'
					, AttrGroup: 'AttrGroup'
					, pos: 'pos'
				  } 
			  }
		  }
	}
  };
