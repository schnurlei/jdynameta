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
package de.jdynameta.base.generation;

import java.util.HashSet;
import java.util.Iterator;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.ClassNameCreator;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;




/**
 * @author Rainer Schneider
 * @version 13.06.2003
 */
public class ClassInfoCodeGenerator extends AbstractClassCodeGenerator 
{
	protected static final String GENERATED_TAG = "@generated";
	protected static final String OVERRIDE_ANNOTATION_TAG = "@Override";
	
	protected ClassInfo info;
	private final ClassNameCreator classNameCreator;
	private final ClassNameCreator referencedClassNameCreator;

	
	private String[] implementedInterfaces;
	private String extendsClassName;

	private boolean	generateAbstractClassFlag;

	public ClassInfoCodeGenerator(  ClassNameCreator aClassNameCreator
			, ClassNameCreator aReferencedClassNameCreator) 
	{
	super();
	this.classNameCreator = aClassNameCreator;
	this.referencedClassNameCreator = aReferencedClassNameCreator;
	}

	
	public ClassInfoCodeGenerator(  ClassInfo aInfo, ClassNameCreator aClassNameCreator
									, ClassNameCreator aReferencedClassNameCreator) 
	{
		super();
		this.info = aInfo;
		this.classNameCreator = aClassNameCreator;
		this.referencedClassNameCreator = aReferencedClassNameCreator;
	}
	
	public ClassInfoCodeGenerator(  ClassInfo aInfo
									, ClassNameCreator aClassNameCreator, ClassNameCreator aReferencedClassNameCreator 
									, String [] allImplementedInterfaces, String aExtendedClassName, boolean aGenerateAbstactClassFlag) 
	{
		super();
		this.info = aInfo;
		this.classNameCreator = aClassNameCreator;
		this.referencedClassNameCreator = aReferencedClassNameCreator;
		this.extendsClassName = aExtendedClassName;
		this.implementedInterfaces = allImplementedInterfaces;
		this.generateAbstractClassFlag = aGenerateAbstactClassFlag;
	}
	
	public ClassNameCreator getClassNameCreator() 
	{
		return classNameCreator;
	}
	
	public void setInfo(ClassInfo aInfo)
	{
		info = aInfo;
	}
	
	@Override
	public void appendImportDeclarations() 
	{
		super.appendImportDeclarations();

		final HashSet<Class<? extends Object>> referencedTypeSet = new HashSet<>(10);
		final HashSet<ClassInfo> referencedClassInfoSet = new HashSet<>(10);

		try
		{
			// add Variables for attributes
			info.handleAttributes(new AttributeHandler()
			{
                                @Override
				public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
													ValueObject objToHandle)
				{
					if(! referencedClassNameCreator.getPackageNameFor( aInfo.getReferencedClass())
							.equals(classNameCreator.getPackageNameFor(info)) ){
						referencedClassInfoSet.add(aInfo.getReferencedClass());	
					}
				}
				
                                @Override
				public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
				{
					if( !aInfo.getJavaTyp().isPrimitive()) {
						referencedTypeSet.add( aInfo.getJavaTyp());
					}
					
				}
			}, null);
		} catch (JdyPersistentException e) {
			e.printStackTrace();
		}

		for (AssociationInfo curType : info.getAssociationInfoIterator())
		{
			referencedClassInfoSet.add(curType.getDetailClass());
		}


		for (Iterator<Class<? extends Object>> typeIter = referencedTypeSet.iterator(); typeIter.hasNext();)
		{
			Class<? extends Object> curType = typeIter.next();
			appendLineWithTabs(0, "import " + curType.getName() + ";");
		}

		for (Iterator<ClassInfo> typeIter = referencedClassInfoSet.iterator(); typeIter.hasNext();)
		{
			ClassInfo curType = (ClassInfo) typeIter.next();
			appendLineWithTabs(0, "import " + this.referencedClassNameCreator.getAbsolutClassNameFor( curType) + ";");
		}

		// add imports if an class has an assoc		
		if( info.getAssociationInfoIterator().iterator().hasNext()) {
			appendLineWithTabs(0, "import de.jdynameta.base.objectlist.ObjectList;");
			appendLineWithTabs(0, "import de.jdynameta.base.objectlist.DefaultObjectList;");

			
		}
	}
	
	@Override
	public String getClassName() 
	{
		return this.classNameCreator.getClassNameFor(info);
	}

	/* (non-Javadoc)
	 * @see de.comafra.metamodel.generation.GeneratorForClassCode#getPackageName()
	 */
	@Override
	public String getPackageName()
	{
		return this.classNameCreator.getPackageNameFor(info);
	}

	@Override
	public void appendClassDeclarations() 
	{
		appendClassDeclarationForAuthor( "Rainer Schneider", this.implementedInterfaces
										,this.extendsClassName, this.generateAbstractClassFlag); 
	}

	public void setExtendsClassName(String aExtendsClassName)
	{
		extendsClassName = aExtendsClassName;
	}
	
	public void setImplementedInterfaces(String[] aImplementedInterfaces)
	{
		implementedInterfaces = aImplementedInterfaces;
	}
	
	@Override
	public void appendInstanceVariableDeclaration() 
	{
		try
		{
			// add Variables for associations
			if( info.getAssociationInfoIterator().iterator().hasNext()) {

				for (AssociationInfo curAssoc : info.getAssociationInfoIterator()) {
		
					appendWithTabs(1,"private ");
					append("ObjectList");
					append(' ');
					appendWithFirstLetterLowercase(curAssoc.getNameResource());
					append("Coll");
					append(" = new DefaultObjectList();\n");
				}
			}

			// add Variables for attributes
			info.handleAttributes(new AttributeHandler()
			{
                                @Override
				public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
													ValueObject objToHandle)
				{
					appendReferenceInstanceVariable(aInfo);
				}

			
                                @Override
				public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
				{
					appendPrimitiveAttributeInstanceVariable(aInfo);
				}

			}, null);
		} catch (JdyPersistentException ex) {
			ex.printStackTrace();
		}
	}

	protected void appendPrimitiveAttributeInstanceVariable(PrimitiveAttributeInfo aInfo)
	{
		if ( !(info.getSuperclass()!= null) || info.isSubclassAttribute(aInfo) ) { 
			appendWithTabs(1,"private ");
			append(aInfo.getJavaTyp().getName());
			append(' ');
			append(classNameCreator.getInstanceVariableName(aInfo));
			append(";\n");
		}
	}

	protected void appendReferenceInstanceVariable(
			ObjectReferenceAttributeInfo aInfo)
	{
		if ( !(info.getSuperclass()!= null) || info.isSubclassAttribute(aInfo) ) { 
			appendWithTabs(1,"private ");
			append(referencedClassNameCreator.getClassNameFor(aInfo.getReferencedClass()));
			append(' ');
			append(classNameCreator.getInstanceVariableName(aInfo));
			append(";\n");
		}
	}
	
	
	@Override
	public void appendMethodDeclarations() 
	{
		appendConstructorDeclaration();
		this.appendEmptyLine();
		appendAllInstanceGetterSetterMethods();
		appendAssociationMethods();
		
		if( !(this.info.getSuperclass() != null ) ) {
			appendEqualsMethod();
			appendHashMethod(); 
		}
	}


	/**
	 * 
	 */
	private void appendAssociationMethods() 
	{
		for (AssociationInfo curAssoc : info.getAssociationInfoIterator()) {
		
			appendAssocGetterDeclaration(curAssoc.getDetailClass(), curAssoc.getNameResource(), "de.jdynameta.base.objectlist.ObjectList");
			appendAssocSetDetailMethod(curAssoc.getDetailClass(), curAssoc.getNameResource(), "ObjectList");
			//appendAssocAddDetailMethod(curAssoc, curAssoc.getDetailClass().getInternalName());
		}		
	}


	protected void appendConstructorDeclaration() 
	{
		// appen default Constructor
		this.appendLineWithTabs(1,  "/**");
		this.appendLineWithTabs(1,  " *Constructor ");
		this.appendLineWithTabs(1,  " */");
		this.appendLineWithTabs(1,  "public ", getClassName(), " ()" );
		this.appendLineWithTabs(1,  "{");
		this.appendLineWithTabs(1,  "}");
	}

	private void appendAllInstanceGetterSetterMethods() 
	{
		try
		{
			info.handleAttributes(new AttributeHandler()
			{
                                @Override
				public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
													ValueObject objToHandle)
				{
					if ( !(info.getSuperclass()!= null) ||  info.isSubclassAttribute(aInfo) ) { 
						appendInstanceGetterDeclaration(aInfo, referencedClassNameCreator.getReferenceClassNameFor(aInfo.getReferencedClass()));
						appendInstanceSetterDeclaration(aInfo, referencedClassNameCreator.getClassNameFor(aInfo.getReferencedClass()));
					}
				}
			
                                @Override
				public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
				{
					if ( !(info.getSuperclass()!= null) || info.isSubclassAttribute(aInfo) ) { 
						createInstanceGetterSetterFor(aInfo);
					}
				}
			}, null);
		} catch (JdyPersistentException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void createInstanceGetterSetterFor(PrimitiveAttributeInfo aInfo)
	{
		appendInstanceGetterDeclaration(aInfo, aInfo.getJavaTypName());
		appendInstanceSetterDeclaration(aInfo, aInfo.getJavaTypName());
	}

	protected void appendInstanceGetterDeclaration( AttributeInfo aInfo, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Get the ", classNameCreator.getInstanceVariableName(aInfo));
		this.appendLineWithTabs(1," * ", GENERATED_TAG);
		this.appendLineWithTabs(1," * ", "@return get the ", classNameCreator.getInstanceVariableName(aInfo));
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public ", aTypeName, " ");
		this.append(classNameCreator.getGetterNameFor(aInfo));
		this.append("() \n");
		this.appendLineWithTabs(1,"{");		
		this.appendWithTabs(1,"\treturn ", classNameCreator.getInstanceVariableName(aInfo));
		this.append(";\n");
		this.appendLineWithTabs(1,"}");
		this.appendEmptyLine();
	}

	protected String getParameterName(String variableName) 
	{
		return "a" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
	}

	protected void appendInstanceSetterDeclaration( AttributeInfo aInfo, String aTypeName) 
	{
		String parameterName = classNameCreator.getParameterName(aInfo);
		
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * set the ", classNameCreator.getInstanceVariableName(aInfo));
		this.appendLineWithTabs(1," * ", GENERATED_TAG);
		this.appendLineWithTabs(1," * ", "@param ", classNameCreator.getInstanceVariableName(aInfo));
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public void ", classNameCreator.getSetterNameFor(aInfo));
		this.append("( " + aTypeName + " ");
		this.append(parameterName );
		this.append(") \n");
		this.appendLineWithTabs(1,"{");		
		this.appendWithTabs(2, classNameCreator.getInstanceVariableName(aInfo), " = ");

		if( aTypeName.equals("String")) { //TODO
			// trim 
			this.append("(" +parameterName + "!= null) ? ");
			this.appendLine(parameterName +".trim() : null;");
			
		} else {
			this.appendLine(parameterName + ";");
		}

		this.appendLineWithTabs(1,"}");
		this.appendEmptyLine();
	}


	protected void appendAssocGetterDeclaration( ClassInfo aDetailClass, String assocName, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Get all  ", this.referencedClassNameCreator.getClassNameFor(aDetailClass));
		this.appendLineWithTabs(1," *");
		this.appendLineWithTabs(1," * ", "@return get the Collection of", this.referencedClassNameCreator.getClassNameFor(aDetailClass));
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public ");
		this.append(aTypeName);
		this.append(" get");
		this.appendWithFirstLetterUppercase(assocName + "Coll");
		this.append("() \n");
		this.appendLineWithTabs(1,"{");		
		this.appendWithTabs(1,"\treturn ");
		this.appendWithFirstLetterLowercase(assocName);
		this.append("Coll");
		this.append(";\n");
		this.appendLineWithTabs(1,"}");
		this.appendEmptyLine();
	}

	private void appendAssocAddDetailMethod( AssociationInfo aAssoc, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Add a ", this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.appendLineWithTabs(1," *");
		this.appendLineWithTabs(1," * ", "@param ", this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public void add");
		this.appendWithFirstLetterUppercase(this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.append("( " + aTypeName);
		this.append(" a" );
		this.appendWithFirstLetterUppercase(this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.append(") \n");
		this.appendLineWithTabs(1,"{");		
		this.appendWithTabs(2,"");
		this.appendWithFirstLetterLowercase(this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.append("Coll.addObject(");
		this.append(" a" );
		this.appendWithFirstLetterUppercase(this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.appendLine(" );");
		// add code to set masterclass		
		this.appendWithTabs(2," a" );
		this.appendWithFirstLetterUppercase(this.referencedClassNameCreator.getClassNameFor(aAssoc.getDetailClass()));
		this.append("." );
		this.append(this.referencedClassNameCreator.getSetterNameFor(aAssoc.getMasterClassReference()));
		this.appendLine("(this);" );
		
		this.appendLineWithTabs(1,"}");
		this.appendEmptyLine();
	}

	protected void appendAssocSetDetailMethod( ClassInfo aDetailClass, String assocName, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Set aCollection of all ", this.referencedClassNameCreator.getClassNameFor(aDetailClass));
		this.appendLineWithTabs(1," *");
		this.appendLineWithTabs(1," * ", "@param ", this.referencedClassNameCreator.getClassNameFor(aDetailClass));
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public void set");
		this.appendWithFirstLetterUppercase(assocName + "Coll");
		this.append("( " + aTypeName);
		this.append(" a" );
		this.appendWithFirstLetterUppercase(this.referencedClassNameCreator.getClassNameFor(aDetailClass));
		this.append("Coll) \n");
		this.appendLineWithTabs(1,"{");		
		this.appendWithTabs(2,"");
		this.appendWithFirstLetterLowercase(assocName);
		this.append("Coll = ");
		this.append(" a" );
		this.appendWithFirstLetterUppercase(this.referencedClassNameCreator.getClassNameFor(aDetailClass));
		this.append("Coll ;\n");
		this.appendLineWithTabs(1,"}");
		this.appendEmptyLine();
	}

	protected void appendEqualsMethod() 
	{
		this.appendLineWithTabs(1,"/* (non-Javadoc)");
		this.appendLineWithTabs(1," * @see java.lang.Object#equals(java.lang.Object");
		this.appendLineWithTabs(1," */");
		this.appendLineWithTabs(1,OVERRIDE_ANNOTATION_TAG);
		this.appendLineWithTabs(1,"public boolean equals(Object compareObj) ");
		this.appendLineWithTabs(1,"{");
		// add cast 
		this.appendLineWithTabs(2, this.classNameCreator.getClassNameFor(info)
								, " typeObj = ("
								, this.classNameCreator.getClassNameFor(info)+ ") compareObj;");

		this.appendLineWithTabs(2, "return typeObj != null ");
		appendLineWithTabs(4,"&& ( ");

		// add Compare for all Key attributes
		appendWithTabs(5,"( ");
		try {
			info.handleAttributes(new AttributeHandler()
			{
				boolean isFirst = true;
                                @Override
				public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
													ValueObject objToHandle)
				{
					if( aInfo.isKey()) {

						appendEmptyLine();
						if(isFirst) {
							appendWithTabs(5, "(");
							isFirst = false; 
						} else {
							appendWithTabs(5, "&& (");
						}
						// is variable in this object not null
						append("get");
						appendWithFirstLetterUppercase(aInfo.getInternalName());
						append("() != null");
						appendEmptyLine();
						// is variable in compare object not null
						appendWithTabs(5,"&& typeObj.get");
						appendWithFirstLetterUppercase(aInfo.getInternalName());
						append("() != null");
						appendEmptyLine();
						// compare variables
						appendWithTabs(5,"&& typeObj.get");
						appendWithFirstLetterUppercase(aInfo.getInternalName());
						append("().equals( typeObj.get");
						appendWithFirstLetterUppercase(aInfo.getInternalName());
						append("()) )");
						appendEmptyLine();
						
					}
				}
				
                                @Override
				public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
				{
					if( aInfo.isKey()) {
						appendEmptyLine();
							if(isFirst) {
								appendWithTabs(5, "(");
								isFirst = false; 
							} else {
								appendWithTabs(5, "&& (");
							}
						// is variable in this object not null
						append(classNameCreator.getGetterNameFor(aInfo));
						append("() != null");
						appendEmptyLine();
						// is variable in compare object not null
						appendWithTabs(5,"&& typeObj."+ classNameCreator.getGetterNameFor(aInfo));
						append("() != null");
						appendEmptyLine();
						// compare variables
						appendWithTabs(5,"&& this." + classNameCreator.getGetterNameFor(aInfo));
						append("().equals( typeObj."+ classNameCreator.getGetterNameFor(aInfo));
						append("()) )");

						appendEmptyLine();
					}
				}
			}, null);
		} catch (JdyPersistentException e) {
		}
		this.appendLineWithTabs(5, ")");

		// if all Key attributes are null compare idenity with 
		appendWithTabs(5,"|| ( ");
		try {
			info.handleAttributes(new AttributeHandler()
			{
				private boolean isFirst = true;
				
                                @Override
				public void handleObjectReference(	ObjectReferenceAttributeInfo aInfo,
													ValueObject objToHandle)
				{
					if( aInfo.isKey()) {
						if(isFirst) {
							isFirst = false; 
						} else {
							appendWithTabs(5, "&& ");
						}
					// is variable in this object not null
						append(classNameCreator.getGetterNameFor(aInfo));
						append("() == null");
						appendEmptyLine();
						// is variable in compare object not null
						appendWithTabs(5,"&& typeObj." + classNameCreator.getGetterNameFor(aInfo));
						append("() == null");
						appendEmptyLine();
					}
				}
				
                                @Override
				public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object objToHandle)
				{
					if( aInfo.isKey()) {
						if(isFirst) {
						isFirst = false; 
						} else {
							appendWithTabs(5, "&& ");
						}
						// is key attribute in this object null
						append( classNameCreator.getGetterNameFor(aInfo));
						append("() == null");
						appendEmptyLine();
						// is key attribute in compare object null
						appendWithTabs(5,"&& typeObj." + classNameCreator.getGetterNameFor(aInfo));
						append("() == null");
						appendEmptyLine();
					}
				}
			}, null);
		} catch (JdyPersistentException e) {
		}
		this.appendLineWithTabs(5, "&& this == typeObj )");

	
		this.appendLineWithTabs(4, ");");

		this.appendLineWithTabs(1,"}");
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
                                @Override
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

						appendWithTabs(4," (( ", classNameCreator.getInstanceVariableName(aInfo));
						append(" != null) ? ");
						append( classNameCreator.getInstanceVariableName(aInfo));
						// is variable in compare object not null
						append(".hashCode() : super.hashCode())");
					}
				}
				
                                @Override
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

						appendWithTabs(4," (( ", classNameCreator.getInstanceVariableName(aInfo));
						append(" != null) ? ");
						// is variable in compare object not null
						append(classNameCreator.getInstanceVariableName(aInfo));
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
