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
package de.jdynameta.view.swingx.metainfo.attribute;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.base.view.DbDomainValue;
import de.jdynameta.metainfoview.attribute.AttributeCombobox;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;

/**
 *
 * @author Rainer Schneider
 *
 */
public class SwingxModelComboboxField extends AttributeCombobox<SwingxModelComboboxField.DomainValueComboElem>
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo textAttributeInfo;
	
	public SwingxModelComboboxField(ObjectListModel<DomainValueComboElem>  aObjectColl, PrimitiveAttributeInfo aTextAttributeInfo) 
	{
		super( aObjectColl);
		// assert the object coll is of the right type by testing the first element
		assert( aObjectColl.size() > 0 && aObjectColl.get(0).domainValue.getDbValue().getClass().equals(aTextAttributeInfo.getJavaTyp()));
		
		this.textAttributeInfo = aTextAttributeInfo;
		
		AutoCompleteDecorator.decorate(getObjectCombo());
		
	}

	protected DomainValueComboElem getAttributeFromObject(Object anObject)
	{
		Object textFromObj = ((ValueObject) anObject).getValue(textAttributeInfo);	
		return getElementForAttrValue(textFromObj);
	}
	
	private DomainValueComboElem getElementForAttrValue(Object textFromObj)
	{
		DomainValueComboElem result = null;
		if ( textFromObj != null ) {
			for ( int i = 0; i < getObjectCombo().getModel().getSize(); i++) {
				DomainValueComboElem element = (DomainValueComboElem)getObjectCombo().getModel().getElementAt(i);
				if ( element != null && element.getAttributeValue().equals(textFromObj) ) {
					result = (DomainValueComboElem)getObjectCombo().getModel().getElementAt(i);
				}
				
			}
		}
		
		return result;
	}
	
	
	protected void setAttributeInObject(Object anObject, DomainValueComboElem selectedAttribute)
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
	
		
	public static class DomainValueComboElem 
	{
		private DbDomainValue<? extends Object> domainValue;
		
		
		public DomainValueComboElem(DbDomainValue<? extends Object> aDomValue) {
			super();
			this.domainValue = aDomValue;
		}

		public Object getAttributeValue()
		{
			return this.domainValue.getDbValue();
		}
		
		@Override
		public String toString() 
		{
			return this.domainValue.getRepresentation();
		}
	}

}
