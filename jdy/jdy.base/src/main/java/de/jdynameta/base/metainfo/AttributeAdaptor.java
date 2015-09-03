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

import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

/**
 * An abstract adapter class for an AttributeHandler.
 * The methods in this class are empty. This class exists as
 * convenience for creating handler objects.
 * @author rs
 *
 */
public class AttributeAdaptor implements AttributeHandler 
{

	@Override
	public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo,	Object objToHandle) throws JdyPersistentException 
	{
	}

	@Override
	public void handleObjectReference(ObjectReferenceAttributeInfo aInfo,
			ValueObject objToHandle) throws JdyPersistentException 
	{
	}

}
