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
import de.jdynameta.base.metainfo.impl.JdyTimeStampType;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

/**
 * TimestampTypeModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public  class TimestampTypeModelImpl extends de.jdynameta.metamodel.metainfo.model.impl.PrimitiveAttributeInfoModelImpl
	implements de.jdynameta.metamodel.metainfo.model.TimestampTypeModel

{
	private java.lang.Boolean isDatePartUsed;
	private java.lang.Boolean isTimePartUsed;

	/**
	 *Constructor 
	 */
	public TimestampTypeModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getTimeStampTypeModelInfo());
	}

	/**
	 * Get the isDatePartUsed
	 * @generated
	 * @return get the isDatePartUsed
	 */
	public Boolean getIsDatePartUsedValue() 
	{
		return isDatePartUsed;
	}

	/**
	 * set the isDatePartUsed
	 * @generated
	 * @param isDatePartUsed
	 */
	public void setIsDatePartUsed( Boolean aIsDatePartUsed) 
	{
		isDatePartUsed = aIsDatePartUsed;
	}

	public boolean isDatePartUsed() 
	{
		return isDatePartUsed.booleanValue();
	}

	/**
	 * Get the isTimePartUsed
	 * @generated
	 * @return get the isTimePartUsed
	 */
	public Boolean getIsTimePartUsedValue() 
	{
		return isTimePartUsed;
	}

	/**
	 * set the isTimePartUsed
	 * @generated
	 * @param isTimePartUsed
	 */
	public void setIsTimePartUsed( Boolean aIsTimePartUsed) 
	{
		isTimePartUsed = aIsTimePartUsed;
	}

	public boolean isTimePartUsed() 
	{
		return isTimePartUsed.booleanValue();
	}

	public PrimitiveType getType()
	{
		return new JdyTimeStampType(isDatePartUsed(), isTimePartUsed());
	}

}