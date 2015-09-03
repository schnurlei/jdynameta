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
 * {@link ClassInfo} without subclasses and associations 
 * At the Moment only {@link ClassInfo} is used 
 * @author rsc
 *
 */
public interface FlatClassInfo
{
	/**
	 * Returns the externalName.
	 * @return String
	 */
	public String getExternalName();
	public String getInternalName();
	public String getShortName();
	
	
	public String getRepoName();

	public String getNameSpace();
	
	public void handleAttributes(
		AttributeHandler aHandler,
		ValueObject objToHandle)
		throws JdyPersistentException;
		
	/**
	 * Iteraor over all AttributeInfo of this Class
	 * @return 
	 */
	public Iterable<? extends AttributeInfo> getAttributeInfoIterator();
	
	/**
	 * @TODO rename
	 * @return
	 */
	public int attributeInfoSize();
	
}