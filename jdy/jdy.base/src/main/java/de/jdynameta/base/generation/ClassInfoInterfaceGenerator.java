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

import java.util.Collection;
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
public class ClassInfoInterfaceGenerator extends AbstractClassCodeGenerator 
{
	protected static final String GENERATED_TAG = "@generated";
	
	protected ClassInfo info;
	private ClassNameCreator classNameCreator;
	private Collection allExtendedInterfaces;

	public ClassInfoInterfaceGenerator(  ClassNameCreator aClassNameCreator) 
	{
		this.classNameCreator = aClassNameCreator;
	}
	
	public ClassInfoInterfaceGenerator(  ClassInfo aInfo, ClassNameCreator aClassNameCreator
										, Collection someExtededInterfaces ) 
	{
		super();
		this.info = aInfo;
		this.classNameCreator = aClassNameCreator;
		this.allExtendedInterfaces = someExtededInterfaces;
	}
	
	public void setInfo(ClassInfo aInfo)
	{
		info = aInfo;
	}
	
	public void setAllExtendedInterfaces(Collection aAllExtendedInterfaces)
	{
		allExtendedInterfaces = aAllExtendedInterfaces;
	}
	
	public void setClassNameCreator(ClassNameCreator aClassNameCreator)
	{
		classNameCreator = aClassNameCreator;
	}
	
	@Override
	public void appendImportDeclarations() 
	{
		super.appendImportDeclarations();

		final HashSet<Class> referencedTypeSet = new HashSet<>(10);
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
					if(!aInfo.getReferencedClass().getNameSpace().equals(info.getNameSpace())){
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

		for (Iterator typeIter = referencedTypeSet.iterator(); typeIter.hasNext();)
		{
			Class curType = (Class) typeIter.next();
			appendLineWithTabs(0, "import " + curType.getName() + ";");
		}

		for (Iterator typeIter = referencedClassInfoSet.iterator(); typeIter.hasNext();)
		{
			ClassInfo curType = (ClassInfo) typeIter.next();
			appendLineWithTabs(0, "import " + this.classNameCreator.getAbsolutClassNameFor( curType) + ";");
		}

		// add imports if an class has an assoc		
		if( info.getAssociationInfoIterator().iterator().hasNext()) {
			appendLineWithTabs(0, "import de.jdynameta.base.objectlist.ObjectList;");
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
		appendInterfaceDeclarationForAuthor( "Rainer Schneider", this.allExtendedInterfaces ); 
	}

	@Override
	public void appendMethodDeclarations() 
	{
		this.appendEmptyLine();
		appendAllInstanceGetterSetterMethods();
		appendAssociationMethods();
	}


	/**
	 * 
	 */
	private void appendAssociationMethods() 
	{
		for (AssociationInfo curAssoc : info.getAssociationInfoIterator()) {
		
			appendAssocGetterDeclaration(curAssoc.getNameResource() + "Coll", "ObjectList");
			appendAssocSetDetailMethod(curAssoc.getNameResource() +"Coll", "ObjectList");
			//appendAssocAddDetailMethod(curAssoc.getDetailClass().getInternalName(), curAssoc.getDetailClass().getInternalName());
		}		
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
					// the getters of the supercalls are done in the superclass
					if ( !(info.getSuperclass()!= null) ||  info.isSubclassAttribute(aInfo) ) { 
						appendInstanceGetterDeclaration(aInfo, classNameCreator.getReferenceClassNameFor(aInfo.getReferencedClass()));
						appendInstanceSetterDeclaration(aInfo, aInfo.getReferencedClass().getInternalName());
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
		this.append("(); \n");
	}

	protected String getParameterName(String variableName) 
	{
		return "a" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
	}

	private void appendInstanceSetterDeclaration( AttributeInfo aInfo, String aTypeName) 
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
		this.append("); \n");
	}


	private void appendAssocGetterDeclaration( String aInternalName, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Get all  ", aInternalName);
		this.appendLineWithTabs(1," * ", GENERATED_TAG);
		this.appendLineWithTabs(1," * ", "@return get the Collection of", aInternalName);
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public ");
		this.append(aTypeName);
		this.append(" get");
		this.appendWithFirstLetterUppercase(aInternalName);
		this.append("(); \n");
	}

	private void appendAssocAddDetailMethod( String aInternalName, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Add a ", aInternalName);
		this.appendLineWithTabs(1," *", GENERATED_TAG);
		this.appendLineWithTabs(1," * ", "@param ", aInternalName);
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public void add");
		this.appendWithFirstLetterUppercase(aInternalName);
		this.append("( " + aTypeName);
		this.append(" a" );
		this.appendWithFirstLetterUppercase(aInternalName);
		this.append("); \n");
		this.appendEmptyLine();
	}

	private void appendAssocSetDetailMethod( String aInternalName, String aTypeName) 
	{
		// add Comment
		this.appendLineWithTabs(1,"/**");
		this.appendLineWithTabs(1," * Set aCollection of all ", aInternalName);
		this.appendLineWithTabs(1," *", GENERATED_TAG);
		this.appendLineWithTabs(1," * ", "@param ", aInternalName);
		this.appendLineWithTabs(1," */");
	
		// add Method
		this.appendWithTabs(1,"public void set");
		this.appendWithFirstLetterUppercase(aInternalName);
		this.append("( " + aTypeName);
		this.append(" a" );
		this.appendWithFirstLetterUppercase(aInternalName);
		this.append("Coll); \n");
	}


	/* (non-Javadoc)
	 * @see de.comafra.metamodel.generation.AbstractClassCodeGenerator#appendInstanceVariableDeclaration()
	 */
	@Override
	public void appendInstanceVariableDeclaration()
	{
	}


}
