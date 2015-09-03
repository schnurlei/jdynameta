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
 * SubClassReferenceInKeyObjImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public  class SubClassReferenceInKeyObjImpl extends de.jdynameta.testcommon.model.testdata.impl.impl.ReferendeInPrimaryKeyWithSubImpl

{
	private CompoundKeyObjImpl compundRefSub1;

	/**
	 *Constructor 
	 */
	public SubClassReferenceInKeyObjImpl ()
	{
	}

	/**
	 * Get the compundRefSub1
	 * @generated
	 * @return get the compundRefSub1
	 */
	public CompoundKeyObjImpl getCompundRefSub1() 
	{
		return compundRefSub1;
	}

	/**
	 * set the compundRefSub1
	 * @generated
	 * @param compundRefSub1
	 */
	public void setCompundRefSub1( CompoundKeyObjImpl aCompundRefSub1) 
	{
		compundRefSub1 = aCompundRefSub1;
	}


}