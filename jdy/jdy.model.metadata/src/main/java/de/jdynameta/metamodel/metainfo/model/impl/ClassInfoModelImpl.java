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
package de.jdynameta.metamodel.metainfo.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.impl.AbstractAttributeInfo;
import de.jdynameta.base.objectlist.DefaultObjectList;
import de.jdynameta.base.objectlist.ObjectList;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;
import de.jdynameta.metamodel.metainfo.model.ClassInfoModel;

/**
 * ClassInfoModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public  class ClassInfoModelImpl extends de.jdynameta.base.value.defaultimpl.TypedReflectionValueObject
	implements de.jdynameta.metamodel.metainfo.model.ClassInfoModel

{
	private ObjectList attributesColl = new DefaultObjectList();
	private ObjectList associationsColl = new DefaultObjectList();
	private ObjectList subclassesColl = new DefaultObjectList();
	private java.lang.String internalName;
	private java.lang.String externalName;
	private java.lang.String shortName;
	private java.lang.Boolean isAbstract;
	private ClassInfoModel superclass;
	private String repoName;
	
	/**
	 *Constructor 
	 */
	public ClassInfoModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getClassInfoModelInfo());
	}


	/**
	 * Get the internalName
	 * @generated
	 * @return get the internalName
	 */
	public String getInternalName() 
	{
		return internalName;
	}

	/**
	 * set the internalName
	 * @generated
	 * @param internalName
	 */
	public void setInternalName( String aInternalName) 
	{
		internalName = (aInternalName!= null) ? aInternalName.trim() : null;
	}

	@Override
	public String getNameSpace()
	{
		return getRepoName();
	}
	
	@Override
	public String getRepoName()
	{
		return this.repoName;
	}
	
	public void setRepoName(String aRepoName)
	{
		repoName = (aRepoName!= null) ? aRepoName.trim() : null;
	}
	
	/**
	 * Get the externalName
	 * @generated
	 * @return get the externalName
	 */
	public String getExternalName() 
	{
		return externalName;
	}

	/**
	 * set the externalName
	 * @generated
	 * @param externalName
	 */
	public void setExternalName( String aExternalName) 
	{
		externalName = (aExternalName!= null) ? aExternalName.trim() : null;
	}

	/**
	 * Get the shortName
	 * @generated
	 * @return get the shortName
	 */
	public String getShortName() 
	{
		return shortName;
	}

	/**
	 * set the shortName
	 * @generated
	 * @param shortName
	 */
	public void setShortName( String aShortName) 
	{
		shortName = (aShortName!= null) ? aShortName.trim() : null;
	}

	/**
	 * Get the isAbstract
	 * @generated
	 * @return get the isAbstract
	 */
	public Boolean getIsAbstractValue() 
	{
		return isAbstract;
	}

	/**
	 * set the isAbstract
	 * @generated
	 * @param isAbstract
	 */
	public void setIsAbstract( Boolean aIsAbstract) 
	{
		isAbstract = aIsAbstract;
	}

	public boolean isAbstract() 
	{
		return isAbstract.booleanValue();
	}

	/**
	 * Get the superclass
	 * @generated
	 * @return get the superclass
	 */
	public de.jdynameta.base.metainfo.ClassInfo getSuperclass() 
	{
		return superclass;
	}

	/**
	 * set the superclass
	 * @generated
	 * @param superclass
	 */
	public void setSuperclass( ClassInfoModel aSuperclass) 
	{
		superclass = aSuperclass;
	}

	/**
	 * Get all  AttributeInfoModel
	 *
	 * @return get the Collection ofAttributeInfoModel
	 */
	public de.jdynameta.base.objectlist.ObjectList getAttributesColl() 
	{
		return attributesColl;
	}

	/**
	 * Set aCollection of all AttributeInfoModel
	 *
	 * @param AttributeInfoModel
	 */
	public void setAttributesColl( ObjectList aAttributeInfoModelColl) 
	{
		attributesColl =  aAttributeInfoModelColl ;
	}

	/**
	 * Get all  AssociationInfoModel
	 *
	 * @return get the Collection ofAssociationInfoModel
	 */
	public de.jdynameta.base.objectlist.ObjectList getAssociationsColl() 
	{
		return associationsColl;
	}

	/**
	 * Set aCollection of all AssociationInfoModel
	 *
	 * @param AssociationInfoModel
	 */
	public void setAssociationsColl( ObjectList aAssociationInfoModelColl) 
	{
		associationsColl =  aAssociationInfoModelColl ;
	}

	/**
	 * Get all  ClassInfoModel
	 *
	 * @return get the Collection ofClassInfoModel
	 */
	public de.jdynameta.base.objectlist.ObjectList getSubclassesColl() 
	{
		return subclassesColl;
	}

	/**
	 * Set aCollection of all ClassInfoModel
	 *
	 * @param ClassInfoModel
	 */
	public void setSubclassesColl( ObjectList aClassInfoModelColl) 
	{
		subclassesColl =  aClassInfoModelColl ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object
	 */
	@Override
	public boolean equals(Object compareObj) 
	{
		ClassInfoModel typeObj = (ClassInfoModel) compareObj;
		return typeObj != null 
				&& ( 
					( 
					(getNameSpace() != null
					&& typeObj.getNameSpace() != null
					&& typeObj.getNameSpace().equals( typeObj.getNameSpace()) )

					&& (getInternalName() != null
					&& typeObj.getInternalName() != null
					&& this.getInternalName().equals( typeObj.getInternalName()) )
					)
					|| ( getNameSpace() == null
					&& typeObj.getNameSpace() == null
					&& getInternalName() == null
					&& typeObj.getInternalName() == null
					&& this == typeObj )
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return 				 (( repoName != null) ? repoName.hashCode() : super.hashCode())
				^
				 (( internalName != null) ? internalName.hashCode() : super.hashCode())		;
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.ClassInfo#getAttributeInfoForExternalName(java.lang.String)
	 */
	public AbstractAttributeInfo getAttributeInfoForExternalName(
			String aExternalName) throws ProxyResolveException
	{
		AbstractAttributeInfo result = null;
		for (AttributeInfo curAttr : getAttributeInfoIterator()) {
			if( curAttr.getExternalName().equals(aExternalName)) {
				result = (AbstractAttributeInfo) curAttr;
				break;
			}
		}
		return result;	
	}

	@Override
	public AssociationInfo getAssoc(String aInternalName) 
	{
		AssociationInfo result = null;
		for (AssociationInfo curAssoc : getAssociationInfoIterator()) {
			if( curAssoc.getNameResource().equals(aInternalName)) {
				result = curAssoc;
				break;
			}
		}
		return result;	
	}
	
	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.ClassInfo#getAssociationInfoIterator()
	 */
	public Iterable<AssociationInfo> getAssociationInfoIterator() throws ProxyResolveException
	{
		return getAssociationsColl();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.ClassInfo#associationInfoSize()
	 */
	public int getAssociationInfoSize() throws ProxyResolveException
	{
		return getAssociationsColl().size();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.ClassInfo#getAllSubclasses()
	 */
	public ObjectList getAllSubclasses()
	{
		return getSubclassesColl();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.ClassInfo#hasSubClasses()
	 */
	public boolean hasSubClasses() throws ProxyResolveException
	{
		return this.getAllSubclasses() != null && this.getAllSubclasses().size() > 0;
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.FlatClassInfo#handleAttributes(de.comafra.model.metainfo.AttributeHandler, de.comafra.model.value.ValueObject)
	 */
	public void handleAttributes(AttributeHandler aHandler,
			ValueObject aObjToHandle) throws ProxyResolveException,JdyPersistentException
	{
		for (AttributeInfo curAttr : getAttributeInfoIterator()) {

			if ( aObjToHandle != null) {
				curAttr.handleAttribute(aHandler, aObjToHandle.getValue(curAttr));
			} else {
				curAttr.handleAttribute(aHandler, null);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.FlatClassInfo#getAttributeInfoIterator()
	 */
	public Iterable<? extends AttributeInfo> getAttributeInfoIterator() throws ProxyResolveException
	{
		return getAllAttributeList();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.FlatClassInfo#attributeInfoSize()
	 */
	public int attributeInfoSize() throws ProxyResolveException
	{
		return getAllAttributeList().size();
	}

	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.impl.DefaultClassInfo#getAllAttributeList()
	 */
	protected List<AttributeInfo> getAllAttributeList() 
	{
		ArrayList<AttributeInfo> tmpAttrList = new ArrayList<AttributeInfo>(getAttributesColl().size()); 
		
		if( getSuperclass() != null) {
			for (AttributeInfo curAttr : getSuperclass().getAttributeInfoIterator()) {

				tmpAttrList.add(curAttr);
			}
		}
			
		for(Iterator<AttributeInfo> attrIter= getAttributesColl().iterator(); attrIter.hasNext();) {
			tmpAttrList.add(attrIter.next());
		}
		
		return 	tmpAttrList;
	}	
	
	/* (non-Javadoc)
	 * @see de.comafra.model.metainfo.SubclassInfo#isSubclassAttribute(de.comafra.model.metainfo.AttributeInfo)
	 */
	public boolean isSubclassAttribute(AttributeInfo aAttrInfo)
	{
		return getAttributesColl().indexOf(aAttrInfo) >=0;	
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " : " + internalName + " @ RepoName " + repoName;
	}
}