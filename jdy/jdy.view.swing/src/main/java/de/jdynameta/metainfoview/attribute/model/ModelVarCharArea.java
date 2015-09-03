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

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeStringTextArea;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ModelVarCharArea extends AttributeStringTextArea
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo varCharAttributeInfo;
	
	public ModelVarCharArea(PrimitiveAttributeInfo aTextAttributeInfo) 
	{
		super(((VarCharType) aTextAttributeInfo.getType()).getLength());
		
		this.varCharAttributeInfo = aTextAttributeInfo;
	}

	@Override
	protected String getStringFromObject(Object anObject)
	{
		return (String) ((ValueObject) anObject).getValue(varCharAttributeInfo);
	}
	
	@Override
	protected void setStringInObject(Object anObject, String newValue)
	{
		((ChangeableValueObject) anObject).setValue(varCharAttributeInfo, newValue);
	}

	public AttributeInfo getAttributeInfo()
	{
		return this.varCharAttributeInfo;
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
}
