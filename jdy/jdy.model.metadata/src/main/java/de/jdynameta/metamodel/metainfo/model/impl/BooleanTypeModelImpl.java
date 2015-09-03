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
package de.jdynameta.metamodel.metainfo.model.impl;

import de.jdynameta.base.metainfo.PrimitiveType;
import de.jdynameta.base.metainfo.impl.JdyBooleanType;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

/**
 * BooleanTypeModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */

@SuppressWarnings("serial")
public  class BooleanTypeModelImpl extends de.jdynameta.metamodel.metainfo.model.impl.PrimitiveAttributeInfoModelImpl
	implements de.jdynameta.metamodel.metainfo.model.BooleanTypeModel

{
	private java.lang.Long temp;

	/**
	 *Constructor 
	 */
	public BooleanTypeModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getBooleanTypeModelInfo());
	}
	

	/**
	 * Get the temp
	 * @generated
	 * @return get the temp
	 */
	public Long getTempValue() 
	{
		return temp;
	}

	/**
	 * set the temp
	 * @generated
	 * @param temp
	 */
	public void setTemp( Long aTemp) 
	{
		temp = aTemp;
	}

	public Long getTemp() 
	{
		return temp;
	}


	public PrimitiveType getType()
	{
		return new JdyBooleanType();
	}

	

}