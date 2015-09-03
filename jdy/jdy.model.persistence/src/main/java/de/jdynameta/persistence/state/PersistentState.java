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
package de.jdynameta.persistence.state;

/**
 * @author Rainer
 *
 */
public interface PersistentState 
{
	/**
	 * Has the values of the object changed since last save?
	 *
	 * @author	Rainer Schneider
	 * @return boolean
	 */
	public boolean isDirty();
	
	/**
	 * test if it is a new object
	 */
	public boolean  isNew();
	
	 /**
	  *	Test if the Model is Marked as Deleted
	  * @author	Rainer Schneider
	  * @return true if it is marked as deleted
	  */
	 public boolean isMarkedAsDeleted();
	 
}