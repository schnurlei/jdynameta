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
 * PrimitiveAttributeInfoModel
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
public interface PrimitiveAttributeInfoModel	extends de.jdynameta.metamodel.metainfo.model.AttributeInfoModel, de.jdynameta.base.metainfo.PrimitiveAttributeInfo

{


	/**
	 * Get the id
	 * @generated
	 * @return get the id
	 */
	public Long getIdValue(); 
	/**
	 * set the id
	 * @generated
	 * @param id
	 */
	public void setId( Long aId); 
	/**
	 * Get the name
	 * @generated
	 * @return get the name
	 */
	public String getName(); 
	/**
	 * set the name
	 * @generated
	 * @param name
	 */
	public void setName( String aName); 

}