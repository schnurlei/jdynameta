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
/*
 *	AttributeDateTextfield.java
 * Created on 06.08.2003
 *
 */
package de.jdynameta.metainfoview.attribute.model;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.metainfoview.attribute.AttributeCombobox;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ModelComboboxTextField extends AttributeCombobox<ModelComboboxTextField.StringComboboxElement>
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo textAttributeInfo;
	
	public ModelComboboxTextField(ObjectListModel<StringComboboxElement>  aObjectColl, PrimitiveAttributeInfo aTextAttributeInfo) 
	{
		super( aObjectColl);
		
		this.textAttributeInfo = aTextAttributeInfo;
	}

	protected ModelComboboxTextField.StringComboboxElement getAttributeFromObject(Object anObject)
	{
		String textFromObj = (String) ((ValueObject) anObject).getValue(textAttributeInfo);	
		return getElementForAttrValue(textFromObj);
	}
	
	private StringComboboxElement getElementForAttrValue(String textFromObj)
	{
		StringComboboxElement result = null;
		if ( textFromObj != null ) {
			for ( int i = 0; i < getObjectCombo().getModel().getSize(); i++) {
				StringComboboxElement element = (StringComboboxElement)getObjectCombo().getModel().getElementAt(i);
				if ( element != null && element.getAttributeValue().equals(textFromObj) ) {
					result = (StringComboboxElement)getObjectCombo().getModel().getElementAt(i);
				}
				
			}
		}
		
		return result;
	}
	
	
	protected void setAttributeInObject(Object anObject, ModelComboboxTextField.StringComboboxElement selectedAttribute)
	{
		((ChangeableValueObject) anObject).setValue(textAttributeInfo, selectedAttribute != null ? selectedAttribute.getAttributeValue() : null);
	}
	

	public AttributeInfo getAttributeInfo()
	{
		return this.textAttributeInfo;
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
	
	public static interface StringComboboxElement
	{
		public String getAttributeValue(); 
	}
	
	public static class DomainValueStringComboElem implements StringComboboxElement
	{
		private DbDomainValue domainValue;
		
		
		public DomainValueStringComboElem(DbDomainValue aDomValue) {
			super();
			this.domainValue = aDomValue;
		}

		public String getAttributeValue()
		{
			return (String) this.domainValue.getDbValue();
		}
		
		@Override
		public String toString() 
		{
			return this.domainValue.getRepresentation();
		}
	}

}
