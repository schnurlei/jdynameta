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
/**
 * 
 */
package de.jdynameta.metainfoview.filter;

import java.util.Collections;
import java.util.List;

import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.metainfoview.metainfo.table.ColumnVisibilityDef;
import de.jdynameta.view.JdyResourceLoader;

/**
 * Path to an Primitive attribute over one or more Associations
 * @author Rainer Schneider
 *
 */
public class AssociationAttributePath
{
	private List<AssociationInfo> assocInfoPath;
	private AttributeInfo attribute;
	private ClassInfo classInfo;
	
	
	public AssociationAttributePath(ClassInfo aClassInfo, AttributeInfo attribute, AssociationInfo anAssocInfo) 
	{
		super();
		this.classInfo = aClassInfo;
		this.attribute = attribute;
		if( anAssocInfo != null) {
			this.assocInfoPath = Collections.singletonList(anAssocInfo);
		} else {
			this.assocInfoPath = null;
		}
	}

	public ClassInfo getClassInfo()
	{
		return classInfo;
	}
	
	public AttributeInfo getAttribute() 
	{
		return attribute;
	}
	
	public List<AssociationInfo> getAssocInfoPath() 
	{
		return assocInfoPath;
	}
	
	public AssociationInfo getLastAssoc()
	{
		return (assocInfoPath == null) ? null : assocInfoPath.get(assocInfoPath.size()-1);
	}
	
	
	public String getDisplayName(JdyResourceLoader resourceLoader, PropertyNameCreator propertyGenerator, ClassInfo classInfo )
	{
		String result = "";
		
		if ( assocInfoPath != null ) {
			
			for (AssociationInfo curAssoc : assocInfoPath) {
				String assocNameProperty = propertyGenerator.getPropertyNameFor(classInfo,  curAssoc);
				result += resourceLoader.getString(assocNameProperty) + ">";
			} 
			AssociationInfo lastAssoc = assocInfoPath.get(assocInfoPath.size()-1);
			String attrNameProperty = propertyGenerator.getPropertyNameFor(lastAssoc.getDetailClass(),  attribute);
			result += resourceLoader.getString(attrNameProperty);
			
		} else {
			String attrNameProperty = propertyGenerator.getPropertyNameFor(classInfo,  attribute);
			result += resourceLoader.getString(attrNameProperty);
		}
		
		return result;
	}
	
	@Override
	public int hashCode() 
	{
		int hash = attribute.hashCode();
		if (assocInfoPath != null) {
			for (AssociationInfo curAssoc : assocInfoPath) {
				hash ^= curAssoc.hashCode();
			} 
		}
		
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(  obj instanceof AssociationAttributePath ) {
			
			AssociationAttributePath compareAttrPath = (AssociationAttributePath) obj;
			boolean result = true; 

			if( assocInfoPath != null) {
				// compare attr path
				if( compareAttrPath.assocInfoPath != null) {
					for( int i =0; i < assocInfoPath.size(); i++ ) {
						if( compareAttrPath.assocInfoPath.size() > i) {
							result = assocInfoPath.get(i).equals(compareAttrPath.assocInfoPath.get(i));
						} else {
							result = false;
						}
					}
				} else {
					result = false;
				}
			} else {
				// assure both assoc coll are null 
				result &= (compareAttrPath.assocInfoPath == null);
			}
			result &= attribute.equals(compareAttrPath.attribute);
			return result;
		} else {
			return false;
		}

	}
	
	/**
	 * Defines which Attributes are visible
	 * @author * <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
	 *
	 */
	public interface VisibilityDef
	{
		public boolean isAttributeVisible(AssociationAttributePath aAttrInfo);
	}
	

	/**
	 * Creates a VisibilityDef from a ColumnVisibility
	 * @author Rainer Schneider
	 *
	 */
	public static class DefaultVisibilityDef implements VisibilityDef
	{
		private ColumnVisibilityDef columnVisbility;
		
		public DefaultVisibilityDef(ColumnVisibilityDef columnVisbility) 
		{
			super();
			this.columnVisbility = columnVisbility;
		}



		public boolean isAttributeVisible(AssociationAttributePath assocPath) 
		{
			return assocPath.getAssocInfoPath() == null && columnVisbility.isAttributeVisible(assocPath.attribute);
		}
	}
	
}