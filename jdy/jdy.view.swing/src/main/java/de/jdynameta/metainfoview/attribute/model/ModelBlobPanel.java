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
import de.jdynameta.base.metainfo.primitive.BlobByteArrayHolder;
import de.jdynameta.base.metainfo.primitive.BlobType;
import de.jdynameta.base.value.ChangeableValueObject;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.attribute.AttributeBlobPanel;

/**
 *
 * @author Rainer Schneider
 *
 */
public class ModelBlobPanel extends AttributeBlobPanel
	implements AttrInfoComponent
{
	private PrimitiveAttributeInfo blobAttributeInfo;
	
	public ModelBlobPanel(PrimitiveAttributeInfo aBlobAttributeInfo) 
	{
		super( (BlobType) aBlobAttributeInfo.getType() );
		
		assert(aBlobAttributeInfo.getType() instanceof BlobType);
		
		this.blobAttributeInfo = aBlobAttributeInfo;
	}

	@Override
	protected BlobByteArrayHolder getBlobFromObject(Object anObject)
	{
		return (BlobByteArrayHolder) ((ValueObject) anObject).getValue(blobAttributeInfo);
	}
	
	@Override
	protected void setBlobInObject(Object anObject, BlobByteArrayHolder newValue)
	{
		((ChangeableValueObject) anObject).setValue(blobAttributeInfo, newValue);
	}

	public AttributeInfo getAttributeInfo()
	{
		return this.blobAttributeInfo;
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
}
