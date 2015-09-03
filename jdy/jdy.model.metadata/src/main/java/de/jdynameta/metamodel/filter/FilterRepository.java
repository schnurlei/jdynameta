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
package de.jdynameta.metamodel.filter;

import java.io.IOException;
import java.math.BigDecimal;

import de.jdynameta.base.generation.AbstractClassCodeGenerator;
import de.jdynameta.base.generation.ClassInfoCodeGenerator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.impl.DefaultClassRepositoryValidator;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.impl.JdyClassInfoModel;
import de.jdynameta.base.metainfo.impl.JdyRepositoryModel;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.defaultimpl.ReflectionChangeableValueObject;

/**
 * @author rsc
 */
public class FilterRepository extends JdyRepositoryModel
{
	private static final FilterRepository singleton = new FilterRepository(); 
	
	public static final DefaultClassNameCreator NAME_CREATOR = createNameCreator(); 
	
	public static enum TypeName 
	{
		AppFilterExpr, AppQuery, AppAndExpr, AppOrExpr, AppOperatorEqual, AppOperatorGreater, AppOperatorLess, AppOperatorExpr
	};
	
	private final JdyClassInfoModel classInfoQueryModel ;
	private final JdyClassInfoModel filterExprModel;

	private JdyClassInfoModel andExprModel;

	private JdyClassInfoModel orExprModel;

	private JdyClassInfoModel operatorExprModel;

	private JdyClassInfoModel primitveOperatorModel;

	private JdyClassInfoModel operatorEqualModel;

	private JdyClassInfoModel operatorGreaterModel;

	private JdyClassInfoModel operatorLessModel;

	// private JdyClassInfoModel compareValueModel;
	
	public FilterRepository() throws InvalidClassInfoException
	{
		super("FilterRepository");
		this.addListener(new DefaultClassRepositoryValidator());

		this.filterExprModel = addClassInfo(TypeName.AppFilterExpr.name()).setShortName("FEX");
		this.filterExprModel.addLongAttr("ExprId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true);
		
		this.andExprModel = addClassInfo(TypeName.AppAndExpr.name(),filterExprModel).setShortName("FEA");
		this.andExprModel.addTextAttr("ExprName" , 100);
		
		this.orExprModel = addClassInfo(TypeName.AppOrExpr.name(),filterExprModel).setShortName("FEO");
		this.orExprModel.addTextAttr("ExprName" , 100);

		this.primitveOperatorModel = addClassInfo("AppPrimitiveOperator").setShortName("FPO");
		this.primitveOperatorModel.addLongAttr("OperatorId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true);

		this.operatorEqualModel = addClassInfo(TypeName.AppOperatorEqual.name(),primitveOperatorModel).setShortName("FPE");
		this.operatorEqualModel.addBooleanAttr("isNotEqual").setNotNull(true);
		
		this.operatorGreaterModel = addClassInfo(TypeName.AppOperatorGreater.name(),primitveOperatorModel).setShortName("FPG");
		this.operatorGreaterModel.addBooleanAttr("isAlsoEqual").setNotNull(true);

		this.operatorLessModel = addClassInfo(TypeName.AppOperatorLess.name(),primitveOperatorModel).setShortName("FPL");
		this.operatorLessModel.addBooleanAttr("isAlsoEqual").setNotNull(true);
		
		this.operatorExprModel = addClassInfo(TypeName.AppOperatorExpr.name(),filterExprModel).setShortName("OEX");
		this.operatorExprModel.addTextAttr("attrName" , 100).setNotNull(true);
		this.operatorExprModel.addReference("operator", primitveOperatorModel).setDependent(true).setNotNull(true);
		this.operatorExprModel.addBooleanAttr("booleanVal"); 
		this.operatorExprModel.addDecimalAttr("decimalVal", new BigDecimal("999999999.9999999"),new BigDecimal("999999999.9999999"),9); 
		this.operatorExprModel.addFloatAttr("floatVal"); 
		this.operatorExprModel.addLongAttr("longVal", -999999999, 999999999); 
		this.operatorExprModel.addTextAttr("textVal",1000); 
		this.operatorExprModel.addTimestampAttr("timestampVal",true,true); 

		this.classInfoQueryModel = addClassInfo(TypeName.AppQuery.name()).setShortName("FQM");
		this.classInfoQueryModel.addLongAttr("FilterId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true);
		this.classInfoQueryModel.addTextAttr("repoName" ,100).setNotNull(true);
		this.classInfoQueryModel.addTextAttr("className" , 35).setNotNull(true);
		this.classInfoQueryModel.addReference("expr", filterExprModel);
		
		addAssociation("andSubExpr", andExprModel, filterExprModel, false, false);
		addAssociation("orSubExpr", orExprModel, filterExprModel, false, false);
		
//		this.compareValueModel = addClassInfo("AppCompareValue").setShortName("CVL");
//		this.compareValueModel.addLongAttr("ValueId" ,0, 999999999).setIsKey(true).setNotNull(true).setGenerated(true); 
//		this.compareValueModel.addBooleanAttr("booleanVal"); 
//		this.compareValueModel.addDecimalAttr("decimalVal", new BigDecimal("999999999.9999999"),new BigDecimal("999999999.9999999"),9); 
//		this.compareValueModel.addFloatAttr("floatVal"); 
//		this.compareValueModel.addLongAttr("longVal", -999999999, 999999999); 
//		this.compareValueModel.addTextAttr("textVal",1000); 

	}	
	
	public ClassInfo getInfoForType(String aTypeName)
	{
		return getInfoForType(TypeName.valueOf(aTypeName));
	}
	
	public ClassInfo getInfoForType(TypeName aTypeName)
	{
		ClassInfo result = null;
		for (ClassInfo curType :  getAllClassInfosIter())
		{
			if( curType.getInternalName().equals(aTypeName.name())) {
				result = curType;
				break;
			}
		}
		
		return result;
	}
	

	public static FilterRepository getSingleton()
	{
		return singleton;
	}
	
	private static ClassInfoCodeGenerator createConcreteClassGenerator(final ClassNameCreator nameCreator, ClassNameCreator interfaceNameCreator) throws IOException
	{
		
		ClassInfoCodeGenerator generator = new ClassInfoCodeGenerator( nameCreator, interfaceNameCreator)
		{
			@Override
			protected void appendConstructorDeclaration() 
			{
				// append default Constructor
				this.appendLineWithTabs(1,  "/**");
				this.appendLineWithTabs(1,  " *Constructor ");
				this.appendLineWithTabs(1,  " */");
				this.appendLineWithTabs(1,  "public ", getClassName(), " ()" );
				this.appendLineWithTabs(1,  "{");
				this.appendLineWithTabs(2,  "super(FilterRepository.getSingleton().getInfoForType(\""+info.getInternalName()+ "\"), NAME_CREATOR);");
				this.appendLineWithTabs(1,  "}");

				this.appendLineWithTabs(1,  "/**");
				this.appendLineWithTabs(1,  " *Constructor for subclasses");
				this.appendLineWithTabs(1,  " */");
				this.appendLineWithTabs(1,  "public ", getClassName(), " (ClassInfo infoForType, ClassNameCreator aNameCreator)" );
				this.appendLineWithTabs(1,  "{");
				this.appendLineWithTabs(2,  "super(infoForType, aNameCreator);");
				this.appendLineWithTabs(1,  "}");

			}
			
			
			@Override
			public void appendInstanceVariableDeclaration()
			{
				this.appendLineWithTabs(1, "private static final long serialVersionUID = 1L;");

				super.appendInstanceVariableDeclaration();
			} 
			
			@Override
			public void appendImportDeclarations()
			{
				super.appendImportDeclarations();
				appendLineWithTabs(0, "import static de.jdynameta.metamodel.filter.FilterRepository.NAME_CREATOR;");
				appendLineWithTabs(0, "import de.jdynameta.base.value.ClassNameCreator;");
				appendLineWithTabs(0, "import de.jdynameta.base.metainfo.ClassInfo;");
			}			
		};										
		
		return generator;
	}	
	
	private static DefaultClassNameCreator createNameCreator()
	{
		return new DefaultClassNameCreator()
		{
			@Override
			public String getPackageNameFor(ClassInfo aInfo)
			{
				return "de.jdynameta.metamodel.filter";
			}
	
			public String getClassNameFor(ClassInfo aInfo)
			{
				return aInfo.getInternalName();
			}
			
			@Override
			public String getReferenceClassNameFor(ClassInfo aInfo)
			{
				return super.getAbsolutClassNameFor(aInfo);
			}
			
		};
		
	}

	
	
	public static void main(String[] args)
	{
		final ClassNameCreator nameCreator = createNameCreator();
		try
		{
			ClassInfoCodeGenerator generator = createConcreteClassGenerator(nameCreator, nameCreator);
			AbstractClassCodeGenerator.generateSourceFiles(getSingleton(), generator,ReflectionChangeableValueObject.class,"src/main/java/");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
