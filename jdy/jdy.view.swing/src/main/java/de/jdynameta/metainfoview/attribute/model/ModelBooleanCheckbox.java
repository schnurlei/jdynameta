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
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeBooleanCheckbox;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ModelBooleanCheckbox extends AttributeBooleanCheckbox
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo booleanAttributeInfo;
	
	public ModelBooleanCheckbox(PrimitiveAttributeInfo aBoleanAttributeInfo) 
	{
		super();
		
		assert(aBoleanAttributeInfo.getType() instanceof BooleanType);
		
		this.booleanAttributeInfo = aBoleanAttributeInfo;

		// test tools
		getAttributeCheckBox().setName(aBoleanAttributeInfo.getInternalName());
		getAttributeCheckBox().getAccessibleContext().setAccessibleName(aBoleanAttributeInfo.getInternalName() +"_Access");
		
	}

	@Override
	protected Boolean getBooleanFromObject(Object anObject)
	{
		return (Boolean) ((ValueObject) anObject).getValue(booleanAttributeInfo);
	}
	
	@Override
	protected void setBooleanInObject(Object anObject, Boolean newValue)
	{
		((ChangeableValueObject) anObject).setValue(booleanAttributeInfo, newValue);
	}

	public AttributeInfo getAttributeInfo()
	{
		return this.booleanAttributeInfo;
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
}
