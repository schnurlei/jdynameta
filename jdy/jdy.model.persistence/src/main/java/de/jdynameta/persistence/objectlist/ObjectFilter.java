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
package de.jdynameta.persistence.objectlist;

import java.io.Serializable;



/**	A filter for objects 
 *
 * @author Rainer Schneider
 */
public interface ObjectFilter<TFilteredObject> extends Serializable
{
	/** Tests whether specified object matches the filter
	 *
	 * @param object		The object to be tested.
	 *
	 * @return			<tt>true</tt> if the object shoud be included,
	 * 					otherwise <tt>false</tt>.
	 */
	public boolean accept(TFilteredObject object);
}
