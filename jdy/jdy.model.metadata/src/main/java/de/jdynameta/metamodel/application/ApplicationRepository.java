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
package de.jdynameta.metamodel.application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.metainfo.primitive.TextType;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.GenericValueObjectImpl;
import de.jdynameta.base.value.ObjectCreationException;
import de.jdynameta.base.value.ProxyResolver;

/**
 * @author rsc
 */
public class ApplicationRepository extends JdyRepositoryModel
{
	
	public static final String ATTR_OBJ_REF_IS_IN_ASSOCIATION = "isInAssociation";

	public static final String META_REPO_NAME = "ApplicationRepository";

	private static final ApplicationRepository singleton = new ApplicationRepository(); 
	
	private final JdyClassInfoModel repositoryModel;

	private final JdyClassInfoModel classInfoModel;
	private final JdyClassInfoModel attributeInfoModel;
	private final JdyClassInfoModel objectReferenceInfoModel;
	private final JdyClassInfoModel associationInfoModel;
	private final JdyClassInfoModel primitiveAttribute;

	private final JdyClassInfoModel booleanTypeModel;
	private final JdyClassInfoModel blobTypeModel;
	private final JdyClassInfoModel decimalTypeModel;
	private final JdyClassInfoModel floatTypeModel;
	private final JdyClassInfoModel longTypeModel;
	private final JdyClassInfoModel textTypeModel;
	private final JdyClassInfoModel timestampTypeModel; 
	private final JdyClassInfoModel varcharTypeModel;
	
	private final JdyClassInfoModel decimalDomainValuesModel;
	private final JdyClassInfoModel longDomainValuesModel;
	private final JdyClassInfoModel stringDomainValuesModel;

	
	
	public ApplicationRepository() throws InvalidClassInfoException
	{
		super("ApplicationRepository");
		this.addListener(new DefaultClassRepositoryValidator());
		
		this.repositoryModel = addClassInfo("AppRepository").setShortName("REP");
		this.repositoryModel.addTextAttr("Name" ,100).setNotNull(true);
		this.repositoryModel.addTextAttr("applicationName" ,100).setIsKey(true).setGenerated(true);
		this.repositoryModel.addLongAttr("appVersion" ,0, 9999999).setGenerated(true).setNotNull(true);
		this.repositoryModel.addBooleanAttr("closed" ).setGenerated(true);
		
		this.classInfoModel = addClassInfo("AppClassInfo").setShortName("CLM");
		this.classInfoModel.addTextAttr("Name" , 100).setNotNull(true);
		this.classInfoModel.addTextAttr("InternalName" , 100).setIsKey(true).setExternalName("Internal").setGenerated(true);
		this.classInfoModel.addBooleanAttr("isAbstract").setNotNull(true);
		this.classInfoModel.addTextAttr("NameSpace",100);
		this.classInfoModel.addVarCharAttr("beforeSaveScript", 4000);

		this.attributeInfoModel = addClassInfo("AppAttribute").setShortName("ATM").setAbstract(true);
		this.attributeInfoModel.addTextAttr( "Name" , 100).setNotNull(true);
		this.attributeInfoModel.addTextAttr("InternalName" , 100).setIsKey(true).setGenerated(true);
		this.attributeInfoModel.addBooleanAttr("isKey").setNotNull(true);
		this.attributeInfoModel.addBooleanAttr("isNotNull").setNotNull(true);
		this.attributeInfoModel.addBooleanAttr("isGenerated").setNotNull(true);
		this.attributeInfoModel.addTextAttr( "AttrGroup" , 100);
		this.attributeInfoModel.addLongAttr( "pos" , 0, Integer.MAX_VALUE).setNotNull(true);
				
		this.primitiveAttribute = addClassInfo("AppPrimitiveAttribute", this.attributeInfoModel).setShortName("PAM").setAbstract(true);
			
		this.objectReferenceInfoModel = addClassInfo("AppObjectReference", attributeInfoModel).setShortName("ORM");
		this.objectReferenceInfoModel.addReference("referencedClass", classInfoModel).setNotNull(true);
		this.objectReferenceInfoModel.addBooleanAttr(ATTR_OBJ_REF_IS_IN_ASSOCIATION).setNotNull(true).setGenerated(true);
		this.objectReferenceInfoModel.addBooleanAttr("isDependent").setNotNull(true).setGenerated(false);
	
		
		
		this.associationInfoModel = addClassInfo("AppAssociation" ).setShortName("AIM");
		this.associationInfoModel.addTextAttr( "Name" , 100).setNotNull(true);
		this.associationInfoModel.addTextAttr("nameResource" , 100).setIsKey(true).setGenerated(true);
		this.associationInfoModel.addReference( "masterClassReference" , objectReferenceInfoModel).setDependent(true).setNotNull(true);
			
		
		this.booleanTypeModel = addClassInfo("AppBooleanType",getPrimitiveAttributeInfo()).setShortName("BTM");
		this.booleanTypeModel.addLongAttr("temp",0,Integer.MAX_VALUE);
	
		this.blobTypeModel = addClassInfo("AppBlobType",getPrimitiveAttributeInfo()).setShortName("BLTM");
		this.blobTypeModel.addLongAttr("TypeHintId",0,Integer.MAX_VALUE);
			
		this.decimalTypeModel = addClassInfo("AppDecimalType",getPrimitiveAttributeInfo()).setShortName("CUM");
		this.decimalTypeModel.addLongAttr("Scale",0, 10);
		this.decimalTypeModel.addDecimalAttr("MinValue", CurrencyType.MIN_VALUE, CurrencyType.MAX_VALUE, 6);
		this.decimalTypeModel.addDecimalAttr("MaxValue", CurrencyType.MIN_VALUE, CurrencyType.MAX_VALUE, 6);

		this.decimalDomainValuesModel = addClassInfo("AppDecimalDomainModel").setShortName("DDM");
		this.decimalDomainValuesModel.addTextAttr("representation", 100);
		this.decimalDomainValuesModel.addDecimalAttr("dbValue", CurrencyType.MIN_VALUE,CurrencyType.MAX_VALUE, 3).setIsKey(true);
		
		this.longDomainValuesModel = addClassInfo("AppLongDomainModel").setShortName("LDM");
		this.longDomainValuesModel.addTextAttr("representation", 100);
		this.longDomainValuesModel.addLongAttr("dbValue", -999999999, 99999999).setIsKey(true);

		this.stringDomainValuesModel= addClassInfo("AppStringDomainModel").setShortName("SDM");
		this.stringDomainValuesModel.addTextAttr("representation", 100);
		this.stringDomainValuesModel.addTextAttr("dbValue", 100).setIsKey(true);
		
		this.floatTypeModel = addClassInfo("AppFloatType",getPrimitiveAttributeInfo()).setShortName("FTM");
		this.floatTypeModel.addLongAttr("Scale",0, 20); 
		this.floatTypeModel.addLongAttr("MaxValue",Integer.MIN_VALUE, Integer.MAX_VALUE); 
			
		this.longTypeModel = addClassInfo("AppLongType",getPrimitiveAttributeInfo()).setShortName("LTM");
		this.longTypeModel.addLongAttr("MinValue",Long.MIN_VALUE, Long.MAX_VALUE);
		this.longTypeModel.addLongAttr("MaxValue",Long.MIN_VALUE, Long.MAX_VALUE);
			
		this.textTypeModel = addClassInfo("AppTextType",getPrimitiveAttributeInfo()).setShortName("TXM");
		this.textTypeModel.addLongAttr("length",1, 1000).setNotNull(true);
		this.textTypeModel.addTextAttr("typeHint", 20, TextType.TypeHint.values()).setNotNull(false);
			
		this.timestampTypeModel = addClassInfo("AppTimestampType",getPrimitiveAttributeInfo()).setShortName("TSM");
		this.timestampTypeModel.addBooleanAttr( "isDatePartUsed").setNotNull(true);
		this.timestampTypeModel.addBooleanAttr( "isTimePartUsed").setNotNull(true); 
			
		this.varcharTypeModel = addClassInfo("AppVarCharType",getPrimitiveAttributeInfo()).setShortName("VCM");
		this.varcharTypeModel.addLongAttr("length",1,Integer.MAX_VALUE);
		this.varcharTypeModel.addBooleanAttr("isClob").setNotNull(true);
		this.varcharTypeModel.addTextAttr("mimeType", 20, VarCharType.TextMimeType.values()).setNotNull(false);
			
		addAssociation("Attributes", this.classInfoModel, this.attributeInfoModel, "Masterclass", "Masterclass", true, true, true);		
		addAssociation("Associations", this.classInfoModel, this.associationInfoModel, "Masterclass", "Masterclass", true, true, true);
		addAssociation("Subclasses", this.classInfoModel, this.classInfoModel, "Superclass", "Superclass", false, false, true);

		addAssociation("Classes", this.repositoryModel, this.classInfoModel, "Repository", "Repository", true, true, true);

		addAssociation("DomainValues", this.decimalTypeModel, this.decimalDomainValuesModel, "Type", "Type", true, true, true);
		addAssociation("DomainValues", this.textTypeModel, this.stringDomainValuesModel, "Type", "Type", true, true, true);
		addAssociation("DomainValues", this.longTypeModel, this.longDomainValuesModel, "Type", "Type", true, true, true);

	}	
	
	
	public JdyClassInfoModel getRepositoryModel() 
	{
		return repositoryModel;
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
		return this.primitiveAttribute;
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
		return this.decimalTypeModel;
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

    public static GenericValueObjectImpl createValueObject(	ProxyResolver aProxyResolver, ClassInfo aClassInfo,	boolean aIsNewFlag, ClassNameCreator nameCreator) throws ObjectCreationException 
	{
		try {
			final Class<? extends Object> metaClass = Class.forName(nameCreator.getAbsolutClassNameFor(aClassInfo));
			
			Constructor<? extends Object> classConstructor = metaClass.getConstructor(new Class[] {});
			GenericValueObjectImpl newObject = (GenericValueObjectImpl)  classConstructor.newInstance(new Object[]{});
			newObject.setNew(aIsNewFlag);
			newObject.setProxyResolver(aProxyResolver);
			
			
			return newObject;
		} catch (SecurityException excp) {
			throw new ObjectCreationException(excp);
		} catch (IllegalArgumentException excp) {
			throw new ObjectCreationException(excp);
		} catch (ClassNotFoundException excp) {
			throw new ObjectCreationException(excp);
		} catch (NoSuchMethodException excp) {
			throw new ObjectCreationException(excp);
		} catch (InstantiationException excp) {
			throw new ObjectCreationException(excp);
		} catch (IllegalAccessException excp) {
			throw new ObjectCreationException(excp);
		} catch (InvocationTargetException excp) {
			throw new ObjectCreationException(excp);
		}
	}


	public static ApplicationRepository getSingleton()
	{
		return singleton;
	}
	
}
