/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.metamodel.metainfo;

import java.math.BigDecimal;

import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;

/**
 * @author rsc
 */
public class MetaModelRepository extends JdyRepositoryModel
{
	
	public static final String META_REPO_NAME = "de.jdynameta.metamodel.metainfo.model";
	
	private static final MetaModelRepository singleton = new MetaModelRepository(); 
	
	private final JdyClassInfoModel classInfoModel;
	private final JdyClassInfoModel attributeInfoModel;
	private final JdyClassInfoModel objectReferenceInfoModel;
	private final JdyClassInfoModel associationInfoModel;
	private final JdyClassInfoModel primitiveAttributeInfoModel;

	private final JdyClassInfoModel booleanTypeModel;
	private final JdyClassInfoModel blobTypeModel;
	private final JdyClassInfoModel currencyTypeModel;
	private final JdyClassInfoModel floatTypeModel;
	private final JdyClassInfoModel longTypeModel;
	private final JdyClassInfoModel textTypeModel;
	private final JdyClassInfoModel timestampTypeModel;
	private final JdyClassInfoModel varcharTypeModel;


	public MetaModelRepository() throws InvalidClassInfoException
	{
		super("MetaModel");
		this.addListener(new DefaultClassRepositoryValidator());
		this.classInfoModel = addClassInfo("ClassInfoModel").setShortName("CLM");

		this.classInfoModel.addTextAttr("InternalName" , 60).setIsKey(true).setExternalName("Internal");
		this.classInfoModel.addTextAttr("ExternalName", 60).setNotNull(true);
		this.classInfoModel.addTextAttr("ShortName", 60).setNotNull(true);
		this.classInfoModel.addBooleanAttr("isAbstract").setNotNull(true);

		
		this.attributeInfoModel = addClassInfo("AttributeInfoModel").setShortName("ATM").setAbstract(true);
		this.attributeInfoModel.addTextAttr("InternalName" , 60).setIsKey(true);
		this.attributeInfoModel.addTextAttr("ExternalName", 60 ).setNotNull(true);
		this.attributeInfoModel.addBooleanAttr("isKey").setNotNull(true);
		this.attributeInfoModel.addBooleanAttr( "isNotNull").setNotNull(true);
		this.attributeInfoModel.addBooleanAttr("isChangeable").setNotNull(true);
		this.attributeInfoModel.addBooleanAttr("isGenerated").setNotNull(true);
		this.attributeInfoModel.addTextAttr( "AttrGroup" , 100);
				
		this.primitiveAttributeInfoModel = addClassInfo("PrimitiveAttributeInfoModel", this.attributeInfoModel).setShortName("PAM").setAbstract(true);
		this.primitiveAttributeInfoModel.addLongAttr( "id" , 0, Integer.MAX_VALUE).setNotNull(true);
		this.primitiveAttributeInfoModel.addTextAttr( "name" , 100).setNotNull(true);
			
			
			
		this.objectReferenceInfoModel = addClassInfo("ObjectReferenceAttributeInfoModel", attributeInfoModel);
		this.objectReferenceInfoModel.setInternalName("ObjectReferenceAttributeInfoModel");
		this.objectReferenceInfoModel.setExternalName("ObjectReferenceAttributeInfoModel");
		this.objectReferenceInfoModel.setShortName("ORM");
		this.objectReferenceInfoModel.addReference("referencedClass", classInfoModel).setNotNull(true);
		this.objectReferenceInfoModel.addBooleanAttr( "isDependent" );
		this.objectReferenceInfoModel.addBooleanAttr("isMultipleAssociation");
				
		this.associationInfoModel = addClassInfo("AssociationInfoModel").setShortName("AIM");
		this.associationInfoModel.addTextAttr("nameResource" , 60).setIsKey(true);
		this.associationInfoModel.addReference( "masterClassReference" , objectReferenceInfoModel).setNotNull(true);
			
		
		this.booleanTypeModel = addClassInfo("BooleanTypeModel",getPrimitiveAttributeInfo()).setShortName("BTM");
		this.booleanTypeModel.addLongAttr("temp",Integer.MIN_VALUE,Integer.MAX_VALUE);
	
		this.blobTypeModel = addClassInfo("BlobTypeModel",getPrimitiveAttributeInfo()).setShortName("BLTM");
		this.blobTypeModel.addLongAttr("TypeHintId",0,Integer.MAX_VALUE);
			
		this.currencyTypeModel = addClassInfo("CurrencyTypeModel",getPrimitiveAttributeInfo()).setShortName("CUM");
		this.currencyTypeModel.addLongAttr("Scale",0,Integer.MAX_VALUE);
		this.currencyTypeModel.addDecimalAttr("MinValue", new BigDecimal(Double.MIN_VALUE),new BigDecimal(Double.MAX_VALUE), 3);
		this.currencyTypeModel.addDecimalAttr("MaxValue", new BigDecimal(Double.MIN_VALUE),new BigDecimal(Double.MAX_VALUE), 3);
			
		this.floatTypeModel = addClassInfo("FloatTypeModel",getPrimitiveAttributeInfo()).setShortName("FTM");
		this.floatTypeModel.addLongAttr("Scale",0,Integer.MAX_VALUE); 
		this.floatTypeModel.addLongAttr("MaxValue",Long.MIN_VALUE,Long.MAX_VALUE); 
			
		this.longTypeModel = addClassInfo("LongTypeModel",getPrimitiveAttributeInfo()).setShortName("LTM");
		this.longTypeModel.addLongAttr("MinValue",Long.MIN_VALUE,Long.MAX_VALUE);
		this.longTypeModel.addLongAttr("MaxValue",Long.MIN_VALUE,Long.MAX_VALUE);
			
		this.textTypeModel = addClassInfo("TextTypeModel",getPrimitiveAttributeInfo()).setShortName("TXM");
		this.textTypeModel.addLongAttr("length",1,Integer.MAX_VALUE);
			
		this.timestampTypeModel = addClassInfo("TimestampTypeModel",getPrimitiveAttributeInfo()).setShortName("TSM");
		this.timestampTypeModel.addBooleanAttr( "isDatePartUsed").setNotNull(true);
		this.timestampTypeModel.addBooleanAttr( "isTimePartUsed").setNotNull(true); 
			
		this.varcharTypeModel = addClassInfo("VarCharTypeModel",getPrimitiveAttributeInfo()).setShortName("VCM");
		this.varcharTypeModel.addLongAttr("length",1, Integer.MAX_VALUE);
		this.varcharTypeModel.addBooleanAttr("isClob").setNotNull(true);
			
		addAssociation("Attributes", this.classInfoModel, this.attributeInfoModel, "Masterclass", "Masterclass", true, true, true);		
		addAssociation("Associations", this.classInfoModel, this.associationInfoModel, "Masterclass", "Masterclass", false, true, true);
		addAssociation("Subclasses", this.classInfoModel, this.classInfoModel, "Superclass", "Superclass", false, false, true);
		
	}	
	
			
	public JdyClassInfoModel getClassInfoModelInfo()
	{
			
		return this.classInfoModel;
	}

	public JdyClassInfoModel getAttributeInfoModelInfo()
	{
		return this.attributeInfoModel;
	}

	public JdyClassInfoModel getObjectReferenceInfo()
	{
		return this.objectReferenceInfoModel;
	}

	public JdyClassInfoModel getAssociationInfo()
	{
		return this.associationInfoModel;
	}

	public JdyClassInfoModel getPrimitiveAttributeInfo()
	{
		return this.primitiveAttributeInfoModel;
	}

	public JdyClassInfoModel getBooleanTypeModelInfo()
	{
		return this.booleanTypeModel;
	}

	public JdyClassInfoModel getBlobTypeModelInfo()
	{
		return this.blobTypeModel;
	}

	public JdyClassInfoModel getCurrencyTypeModelInfo()
	{
		return this.currencyTypeModel;
	}


	public JdyClassInfoModel getFloatTypeModelInfo()
	{
		return this.floatTypeModel;
	}

	public JdyClassInfoModel getLongTypeModelInfo()
	{
		return this.longTypeModel;
	}

	public JdyClassInfoModel getTextTypeModelInfo()
	{
		return this.textTypeModel;
	}

	public JdyClassInfoModel getTimeStampTypeModelInfo()
	{
		return this.timestampTypeModel;
	}

	public JdyClassInfoModel getVarCharTypeModelInfo()
	{
		return this.varcharTypeModel;
	}

    public static MetaModelRepository getSingleton()
	{
		return singleton;
	}
	
}
