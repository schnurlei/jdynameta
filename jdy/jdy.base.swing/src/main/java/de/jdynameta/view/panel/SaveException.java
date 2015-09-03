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
package de.jdynameta.view.panel;


/**
 * Exception throw when the Save-Operation goes wrong
 * @author rs
 * @version 08/2003
 */
public class SaveException extends Exception 
{
	/**
	 * Constructor for SaveVetoException.
	 * @param message
	 */
	public SaveException(String message) 
	{
		super(message);
	}


	public SaveException(Throwable cause) 
	{
		super(cause);
	}

	/**
	 * Constructor for SaveVetoException.
	 * @param message
	 * @param cause
	 */
	public SaveException(String message, Throwable cause) {
		super(message, cause);
	}
}