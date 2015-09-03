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
package de.jdynameta.metamodel.metainfo.model;


/**
 * TimestampTypeModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public interface TimestampTypeModel	extends de.jdynameta.metamodel.metainfo.model.PrimitiveAttributeInfoModel

{


	/**
	 * Get the isDatePartUsed
	 * @generated
	 * @return get the isDatePartUsed
	 */
	public Boolean getIsDatePartUsedValue(); 
	/**
	 * set the isDatePartUsed
	 * @generated
	 * @param isDatePartUsed
	 */
	public void setIsDatePartUsed( Boolean aIsDatePartUsed); 
	/**
	 * Get the isTimePartUsed
	 * @generated
	 * @return get the isTimePartUsed
	 */
	public Boolean getIsTimePartUsedValue(); 
	/**
	 * set the isTimePartUsed
	 * @generated
	 * @param isTimePartUsed
	 */
	public void setIsTimePartUsed( Boolean aIsTimePartUsed); 

}