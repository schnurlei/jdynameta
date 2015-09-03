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

import java.math.BigDecimal;
import java.util.Locale;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.CurrencyType;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeBigDecimalTextfield;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ModelCurrencyTextField extends AttributeBigDecimalTextfield
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo longAttributeInfo;
	
	public ModelCurrencyTextField(PrimitiveAttributeInfo aCurrencyAttributeInfo, Locale aLocale) 
	{
		super(aLocale, ((CurrencyType)aCurrencyAttributeInfo.getType()).getMaxValue(), ((CurrencyType)aCurrencyAttributeInfo.getType()).getScale());
		
		assert(aCurrencyAttributeInfo.getType() instanceof CurrencyType);
		
		this.longAttributeInfo = aCurrencyAttributeInfo;
	}

	@Override
	protected BigDecimal getBigDecimalFromObject(Object anObject)
	{
		return (BigDecimal) ((ValueObject) anObject).getValue(longAttributeInfo);
	}
	
	@Override
	protected void setBigDecimalInObject(Object anObject, BigDecimal newValue)
	{
		((ChangeableValueObject) anObject).setValue(longAttributeInfo, newValue);
	}

	public AttributeInfo getAttributeInfo()
	{
		return this.longAttributeInfo;
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
}
