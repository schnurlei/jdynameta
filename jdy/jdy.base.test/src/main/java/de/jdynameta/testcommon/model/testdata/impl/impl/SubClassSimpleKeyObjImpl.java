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
package de.jdynameta.testcommon.model.testdata.impl.impl;


/**
 * SubClassSimpleKeyObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class SubClassSimpleKeyObjImpl extends de.jdynameta.testcommon.model.testdata.impl.impl.SimpleKeyWithSubObjImpl

{
	private java.lang.String simpleDataSub1;

	/**
	 *Constructor 
	 */
	public SubClassSimpleKeyObjImpl ()
	{
	}

	/**
	 * Get the simpleDataSub1
	 * @generated
	 * @return get the simpleDataSub1
	 */
	public String getSimpleDataSub1() 
	{
		return simpleDataSub1;
	}

	/**
	 * set the simpleDataSub1
	 * @generated
	 * @param simpleDataSub1
	 */
	public void setSimpleDataSub1( String aSimpleDataSub1) 
	{
		simpleDataSub1 = (aSimpleDataSub1!= null) ? aSimpleDataSub1.trim() : null;
	}


}