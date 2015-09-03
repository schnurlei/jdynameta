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
 * Exeption throw by a Panel when it is being closed 
 * and the state of the panel prevents a close 
 * @author Rainer Schneider
 */
public class CloseVetoException extends Exception
{

	/**
	 * Constructor for SaveVetoException.
	 */
	public CloseVetoException() {
		super();
	}

	/**
	 * Constructor for SaveVetoException.
	 * @param message
	 */
	public CloseVetoException(String message) {
		super(message);
	}

	/**
	 * Constructor for SaveVetoException.
	 * @param message
	 * @param cause
	 */
	public CloseVetoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for SaveVetoException.
	 * @param cause
	 */
	public CloseVetoException( Throwable cause) {
		super( cause);
	}

}