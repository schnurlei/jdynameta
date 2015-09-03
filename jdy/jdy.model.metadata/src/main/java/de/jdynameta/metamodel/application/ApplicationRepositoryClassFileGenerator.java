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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.jdynameta.base.generation.AbstractClassCodeGenerator;
import de.jdynameta.base.generation.ClassInfoCodeGenerator;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ClassRepository;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.impl.InvalidClassInfoException;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.DefaultAttributeNameCreator;
import de.jdynameta.base.value.DefaultClassNameCreator;
import de.jdynameta.base.value.DefaultInterfaceClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * @author Rainer
 *
 */
public class ApplicationRepositoryClassFileGenerator
{
	private static final String ROOT_DIR = "src/main/java/";


	/**
	 * 
	 */
	public ApplicationRepositoryClassFileGenerator()
	{
		super();
	}
	
	
	/**
	 * Create for all ClassInfo in the Repository the Files for the Model Classes
	 * @param aRepository
	 * @throws IOException
	 */
	protected void generateModelImpl(ClassRepository aRepository) throws IOException
	{
		for(ClassInfo curInfo : aRepository.getAllClassInfosIter())
		{
			generateModelClass( curInfo);	
		}
	}

	private void generateModelClass( ClassInfo curInfo) throws IOException
	{
		final ClassNameCreator nameCreator = new ModelNameCreator();
		ClassNameCreator interfaceNameCreator = new InterfaceNameCreator();
		String superClassName = (curInfo.getSuperclass() != null)
								?  nameCreator.getAbsolutClassNameFor(curInfo.getSuperclass())
								:"de.jdynameta.base.value.GenericValueObjectImpl";
		
		ClassInfoCodeGenerator generator = new ClassInfoCodeGeneratorExtension(curInfo, nameCreator, interfaceNameCreator,
				new String[]{}, superClassName, false, nameCreator);
  
		
		writeToFile(generator);
	}
	
	protected void writeToFile(AbstractClassCodeGenerator generator) throws IOException
	{
		String path = generator.getPackageName().replace('.', '/');
		path = ROOT_DIR  +  path;
		File targetPath = new File(path);
		targetPath.mkdirs();
		
		String code = generator.getModelCode();

		File targetFile = new File(targetPath, generator.getClassName()+".java") ;
		System.out.println(targetFile.getAbsolutePath());

		FileWriter writer = new FileWriter(targetFile);
		writer.write(code);
		writer.close();
		
	}
	
	private final class ClassInfoCodeGeneratorExtension extends	ClassInfoCodeGenerator 
	{
		private final ClassNameCreator nameCreator;

		private ClassInfoCodeGeneratorExtension(ClassInfo aInfo,ClassNameCreator aClassNameCreator,
				ClassNameCreator aReferencedClassNameCreator,	String[] allImplementedInterfaces, String aExtendedClassName,
				boolean aGenerateAbstactClassFlag, ClassNameCreator nameCreator) 
		{
			super(aInfo, aClassNameCreator, aReferencedClassNameCreator,
					allImplementedInterfaces, aExtendedClassName,
					aGenerateAbstactClassFlag);
			this.nameCreator = nameCreator;
		}

		@Override
		public void appendInstanceVariableDeclaration() 
		{
		}
		
		@Override
		protected void createInstanceGetterSetterFor(PrimitiveAttributeInfo aInfo)
		{
			super.createInstanceGetterSetterFor(aInfo);
			if( isBooleanAttribute(aInfo)) {
				appendBooleanGetterDeclaration(aInfo);
			}
			if( isIntegerAttribute(aInfo)) {
				appendLongGetterDeclaration(aInfo);
			}
		}

		protected void appendBooleanGetterDeclaration( AttributeInfo aInfo) 
		{
			// add Method
			this.appendWithTabs(1,"public ", "boolean", " ");
			this.append(aInfo.getInternalName());
			this.append("() \n");
			this.appendLineWithTabs(1,"{");		
			this.appendWithTabs(1,"\treturn ", "get"+ DefaultAttributeNameCreator.stringWithFirstLetterUppercase(nameCreator.getInstanceVariableName(aInfo))+"Value()" + ".booleanValue()");
			this.append(";\n");
			this.appendLineWithTabs(1,"}");
			this.appendEmptyLine();
		}

		protected void appendLongGetterDeclaration( AttributeInfo aInfo) 
		{
			// add Method
			this.appendWithTabs(1,"public ", "long", " ");
			this.append("get" + DefaultAttributeNameCreator.stringWithFirstLetterUppercase(aInfo.getInternalName()));
			this.append("() \n");
			this.appendLineWithTabs(1,"{");		
			this.appendWithTabs(1,"\treturn ", "get"+ DefaultAttributeNameCreator.stringWithFirstLetterUppercase(nameCreator.getInstanceVariableName(aInfo)) +"Value()" + ".intValue()");
			this.append(";\n");
			this.appendLineWithTabs(1,"}");
			this.appendEmptyLine();
		}

		@Override
		protected void appendConstructorDeclaration() 
		{
			// append default Constructor
			this.appendLineWithTabs(1,  "/**");
			this.appendLineWithTabs(1,  " *Constructor ");
			this.appendLineWithTabs(1,  " */");
			this.appendLineWithTabs(1,  "public ", getClassName(), " ()" );
			this.appendLineWithTabs(1,  "{");
			this.appendLineWithTabs(2,  "super(ApplicationRepository.getSingleton().getClassForName(\""+info.getInternalName()+ "\"));");
			this.appendLineWithTabs(1,  "}");

			this.appendLineWithTabs(1,  "/**");
			this.appendLineWithTabs(1,  " *Constructor for subclasses");
			this.appendLineWithTabs(1,  " */");
			this.appendLineWithTabs(1,  "public ", getClassName(), " (de.jdynameta.base.metainfo.ClassInfo infoForType)" );
			this.appendLineWithTabs(1,  "{");
			this.appendLineWithTabs(2,  "super(infoForType);");
			this.appendLineWithTabs(1,  "}");

		}
	
		
		protected void appendInstanceGetterDeclaration( AttributeInfo aInfo, String aTypeName) 
		{
			// add Comment
			this.appendLineWithTabs(1,"/**");
			this.appendLineWithTabs(1," * Get the ", getClassNameCreator().getInstanceVariableName(aInfo));
			this.appendLineWithTabs(1," * ", GENERATED_TAG);
			this.appendLineWithTabs(1," * ", "@return get the ",  getClassNameCreator().getInstanceVariableName(aInfo));
			this.appendLineWithTabs(1," */");
		
			// add Method
			this.appendWithTabs(1,"public ", aTypeName, " ");
			this.append( getClassNameCreator().getGetterNameFor(aInfo));
			this.append("() \n");
			this.appendLineWithTabs(1,"{");		
			this.appendWithTabs(1,"\treturn " + "(" + aTypeName +")" + " getValue(\"" + aInfo.getExternalName() +"\")");
			this.append(";\n");
			this.appendLineWithTabs(1,"}");
			this.appendEmptyLine();
		}
		
		protected  void appendInstanceSetterDeclaration( AttributeInfo aInfo, String aTypeName) 
		{
			String parameterName = getClassNameCreator().getParameterName(aInfo);
			
			// add Comment
			this.appendLineWithTabs(1,"/**");
			this.appendLineWithTabs(1," * set the ", getClassNameCreator().getInstanceVariableName(aInfo));
			this.appendLineWithTabs(1," * ", GENERATED_TAG);
			this.appendLineWithTabs(1," * ", "@param ", getClassNameCreator().getInstanceVariableName(aInfo));
			this.appendLineWithTabs(1," */");
		
			// add Method
			this.appendWithTabs(1,"public void ", getClassNameCreator().getSetterNameFor(aInfo));
			this.append("( " + aTypeName + " ");
			this.append(parameterName );
			this.append(") \n");
			this.appendLineWithTabs(1,"{");		
			this.appendWithTabs(2, " setValue( \""+ aInfo.getExternalName()+"\",");

			if( aTypeName.equals("String")) { //TODO
				// trim 
				this.append("(" +parameterName + "!= null) ? ");
				this.append(parameterName +".trim() : null");
				
			} else {
				this.append(parameterName);
			}
			this.appendLine(");");

			this.appendLineWithTabs(1,"}");
			this.appendEmptyLine();
		}
		
		protected void appendAssocGetterDeclaration( ClassInfo aDetailClass, String assocName, String aTypeName) 
		{
			// add Comment
			this.appendLineWithTabs(1,"/**");
			this.appendLineWithTabs(1," * Get all  ", this.getClassNameCreator().getClassNameFor(aDetailClass));
			this.appendLineWithTabs(1," *");
			this.appendLineWithTabs(1," * ", "@return get the Collection of", this.getClassNameCreator().getClassNameFor(aDetailClass));
			this.appendLineWithTabs(1," */");
		
			// add Method
			this.appendWithTabs(1,"public ");
			this.append(aTypeName);
			this.append(" get");
			this.appendWithFirstLetterUppercase(assocName + "Coll");
			this.append("() \n");
			this.appendLineWithTabs(1,"{");		
			this.appendWithTabs(1,"\treturn " + " getValue(this.getClassInfo().getAssoc(\"" + assocName +"\"))");
			this.append(";\n");
			this.appendLineWithTabs(1,"}");
			this.appendEmptyLine();
		}
		
		protected void appendAssocSetDetailMethod( ClassInfo aDetailClass, String assocName, String aTypeName) 
		{
			// add Comment
			this.appendLineWithTabs(1,"/**");
			this.appendLineWithTabs(1," * Set aCollection of all ", this.getClassNameCreator().getClassNameFor(aDetailClass));
			this.appendLineWithTabs(1," *");
			this.appendLineWithTabs(1," * ", "@param ", this.getClassNameCreator().getClassNameFor(aDetailClass));
			this.appendLineWithTabs(1," */");
		
			// add Method
			this.appendWithTabs(1,"public void set");
			this.appendWithFirstLetterUppercase(assocName + "Coll");
			this.append("( " + aTypeName);
			this.append(" a" );
			this.appendWithFirstLetterUppercase(this.getClassNameCreator().getClassNameFor(aDetailClass));
			this.append("Coll) \n");
			this.appendLineWithTabs(1,"{");		
			this.appendWithTabs(2,"setValue(this.getClassInfo().getAssoc(\"" + assocName +"\")," );
			this.append(" a" );
			this.appendWithFirstLetterUppercase(this.getClassNameCreator().getClassNameFor(aDetailClass));
			this.append("Coll");
			this.append(" );\n");
			this.appendLineWithTabs(1,"}");
			this.appendEmptyLine();
		}
		
		
		@Override
		public void appendImportDeclarations() 
		{
			super.appendImportDeclarations();
		}
		
		protected void appendHashMethod() 
		{
			this.appendLineWithTabs(1,"/* (non-Javadoc)");
			this.appendLineWithTabs(1," * @see java.lang.Object#hashCode()");
			this.appendLineWithTabs(1," */");
			this.appendLineWithTabs(1,OVERRIDE_ANNOTATION_TAG);
			this.appendLineWithTabs(1,"public int hashCode() ");
			this.appendLineWithTabs(1,"{");


			this.appendWithTabs(2, "return ");

			// add Variables for attributes
			try {
				info.handleAttributes(new AttributeHandler()
				{
					private boolean isFirst =true;
					public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
														ValueObject objToHandle)
					{
						if( aInfo.isKey()) {
							if (isFirst ){
								isFirst = false; 
							} else {
								// XOR Hashcodes of the variables
								appendEmptyLine();
								appendLineWithTabs(3, "^");
							} 

							appendWithTabs(4," (( ", getClassNameCreator().getGetterNameFor(aInfo)+"()");
							append(" != null) ? ");
							append( getClassNameCreator().getGetterNameFor(aInfo)+"()");
							// is variable in compare object not null
							append(".hashCode() : super.hashCode())");
						}
					}
					
					public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
					{
						if( aInfo.isKey()) {

							if (isFirst ){
								isFirst = false; 
							} else {
								// XOR Hashcodes of the variables
								appendEmptyLine();
								appendLineWithTabs(4, "^");
							}

							appendWithTabs(4," (( ", getClassNameCreator().getGetterNameFor(aInfo)+"()");
							append(" != null) ? ");
							// is variable in compare object not null
							append(getClassNameCreator().getGetterNameFor(aInfo)+"()");
							append(".hashCode() : super.hashCode())");
						}
					}
				}, null);
			} catch (JdyPersistentException e) {
			}
		
			this.appendLineWithTabs(2, ";");

			this.appendLineWithTabs(1,"}");
		}
		
	}


	@SuppressWarnings("serial")
	public static class ModelNameCreator extends DefaultClassNameCreator
	{
		public String getClassNameFor(ClassInfo aInfo)
		{
			return aInfo.getInternalName();
		}
		
		@Override
		public String getReferenceClassNameFor(ClassInfo aInfo)
		{
			return  "de.jdynameta.metamodel.application." + aInfo.getInternalName();
		}
		/* (non-Javadoc)
		 * @see de.comafra.model.value.ClassNameCreator#getPackageNameFor(de.comafra.model.metainfo.ClassInfo)
		 */
		public String getPackageNameFor(ClassInfo aInfo)
		{
			return "de.jdynameta.metamodel.application";
		}
		
		/**
		 * Add Value to getter to distinguish from the primitive type Method
		 */
		@Override
		public String getGetterNameFor(AttributeInfo aInfo)
		{
			String result = super.getGetterNameFor( aInfo);
			if( isBooleanAttribute(aInfo) || isIntegerAttribute(aInfo)) {
				result += "Value";
			}
			return result;
		}
		
	}
	
	
	@SuppressWarnings("serial")
	public static class InterfaceNameCreator extends DefaultInterfaceClassNameCreator
	{
		
		@Override
		public String getPackageNameFor(ClassInfo aInfo)
		{
			return "de.jdynameta.metamodel.application";
		}
		
		@Override
		public String getReferenceClassNameFor(ClassInfo aInfo)
		{
			return  "de.jdynameta.metamodel.application." + aInfo.getInternalName();
		}
		
		@Override
		public String getGetterNameFor(AttributeInfo aInfo)
		{
			String result = super.getGetterNameFor( aInfo);
			if( isBooleanAttribute(aInfo) || isIntegerAttribute(aInfo)) {
				result += "Value";
			}
			return result;
		}
				
	}
	
	private static boolean isBooleanAttribute(AttributeInfo aInfo)
	{
		return aInfo instanceof PrimitiveAttributeInfo
				&& ((PrimitiveAttributeInfo) aInfo).getType() instanceof BooleanType;
	}
	
	private static boolean isIntegerAttribute(AttributeInfo aInfo)
	{
		return aInfo instanceof PrimitiveAttributeInfo
				&& ((PrimitiveAttributeInfo) aInfo).getType() instanceof LongType;
	}
	
	public static void main(String[] args)
	{
		System.out.println("Test start");
		
		try
		{	
			ApplicationRepositoryClassFileGenerator generator = new ApplicationRepositoryClassFileGenerator();
			
//			generator.generateModelClass(new ApplicationRepository().getClassInfoModelInfo());
			generator.generateModelImpl(new ApplicationRepository());
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InvalidClassInfoException excp)
		{
			excp.printStackTrace();
		}
		
	}

}
