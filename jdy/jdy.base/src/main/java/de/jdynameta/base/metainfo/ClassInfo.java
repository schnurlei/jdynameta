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
package de.jdynameta.base.metainfo;

import de.jdynameta.base.objectlist.ObjectList;

/**
 * Describes the meta information of a class.
 * It contains the necessary information to map the class to different
 * data sources, to create class files, ...
 * It hold a list of Attribute Informations (primitive and object reference),
 * about relations to other classes and to a superclass 
 * @author Rainer Schneider
 */
public interface ClassInfo extends FlatClassInfo 
{
	public AttributeInfo getAttributeInfoForExternalName(String aExternalName);

	public AssociationInfo getAssoc(String aInternalName);
	
	public Iterable<AssociationInfo> getAssociationInfoIterator();

	public int getAssociationInfoSize();

	public ObjectList<ClassInfo> getAllSubclasses();
	
	public boolean hasSubClasses();
	
	/**
	 * 
	 * @return true if the class is abstract and has no instances
	 */
	public boolean isAbstract();
	
	/**
	 * @return
	 */
	public abstract ClassInfo getSuperclass();

	/**
	 * 
	 * @param aAttrInfo
	 * @return
	 */
	public abstract boolean isSubclassAttribute(AttributeInfo aAttrInfo);

	
}