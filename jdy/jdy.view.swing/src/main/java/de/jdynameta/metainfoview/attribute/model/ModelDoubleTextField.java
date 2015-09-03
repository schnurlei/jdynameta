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
package de.jdynameta.metainfoview.attribute.model;

import java.util.Locale;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.FloatType;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeDoubleTextfield;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ModelDoubleTextField extends AttributeDoubleTextfield
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo longAttributeInfo;
	
	public ModelDoubleTextField(PrimitiveAttributeInfo aDoubleAttributeInfo, Locale aLocale) 
	{
		super(aLocale, new Double(Double.MIN_NORMAL), new Double(Double.MAX_VALUE), 10);
		
		assert(aDoubleAttributeInfo.getType() instanceof FloatType);
		
		this.longAttributeInfo = aDoubleAttributeInfo;
	}

	@Override
	protected Double getDoubleFromObject(Object anObject)
	{
		return (Double) ((ValueObject) anObject).getValue(longAttributeInfo);
	}
	
	@Override
	protected void setDoubleInObject(Object anObject, Double newValue)
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
