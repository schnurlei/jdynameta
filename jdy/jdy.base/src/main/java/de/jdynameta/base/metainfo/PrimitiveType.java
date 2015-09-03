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

import de.jdynameta.base.metainfo.primitive.PrimitiveTypeGetVisitor;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer Schneider
 */
public interface PrimitiveType {

	/**
	 * Gets the javaType.
	 * @return Returns a Class
	 */
	public Class<? extends Object> getJavaType();
	
	public int compareObjects(Object value1, Object value2);

	/**
     * @param aHandler
     * @param aValue
     * @throws de.jdynameta.base.value.JdyPersistentException
	 * @see de.comafra.model.metainfo.AbstractAttributeInfo#handlePrimitiveKeys(PrimitiveAttributeHandler)
	 */
	public void handlePrimitiveKey( PrimitiveTypeVisitor aHandler, Object aValue)
		throws JdyPersistentException;
		
	public Object handlePrimitiveKey(PrimitiveTypeGetVisitor aHandler)
		throws JdyPersistentException;
}