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
package de.jdynameta.view.swingx.metainfo.attribute;

import java.util.Date;
import java.util.Locale;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.TimeStampType;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;

/**
 *
 * @author Rainer Schneider
 *
 */
public class SwingxModelDateTextField extends SwingxDateTextfield
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo dateAttributeInfo;
	
	public SwingxModelDateTextField(PrimitiveAttributeInfo aDateAttributeInfo, Locale aLocale) 
	{
		super(aLocale);
		
		assert(aDateAttributeInfo.getType() instanceof TimeStampType);
		
		this.dateAttributeInfo = aDateAttributeInfo;
	}

	@Override
	protected Date getDateFromObject(Object anObject)
	{
		return (Date) ((ValueObject) anObject).getValue(dateAttributeInfo);
	}
	
	@Override
	protected void setDateInObject(Object anObject, Date newValue)
	{
		((ChangeableValueObject) anObject).setValue(dateAttributeInfo, newValue);
	}

	public AttributeInfo getAttributeInfo()
	{
		return this.dateAttributeInfo;
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
}
