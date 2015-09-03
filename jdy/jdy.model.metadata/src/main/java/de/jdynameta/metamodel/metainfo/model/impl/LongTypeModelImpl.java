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
import de.jdynameta.base.metainfo.impl.JdyLongType;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

/**
 * LongTypeModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public  class LongTypeModelImpl extends de.jdynameta.metamodel.metainfo.model.impl.PrimitiveAttributeInfoModelImpl
	implements de.jdynameta.metamodel.metainfo.model.LongTypeModel

{
	private java.lang.Long maxValue;
	private java.lang.Long minValue;

	/**
	 *Constructor 
	 */
	public LongTypeModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getLongTypeModelInfo());
	}

	/**
	 * Get the maxValue
	 * @generated
	 * @return get the maxValue
	 */
	public Long getMaxValueValue() 
	{
		return maxValue;
	}

	/**
	 * set the maxValue
	 * @generated
	 * @param maxValue
	 */
	public void setMaxValue( Long aMaxValue) 
	{
		maxValue = aMaxValue;
	}

	public Long getMaxValue() 
	{
		return maxValue == null ? null : maxValue;
	}

	/**
	 * Get the maxValue
	 * @generated
	 * @return get the maxValue
	 */
	public Long getMinValueValue() 
	{
		return minValue;
	}

	/**
	 * set the maxValue
	 * @generated
	 * @param maxValue
	 */
	public void setMinValue( Long aMinValue) 
	{
		minValue = aMinValue;
	}

	public Long getMinValue() 
	{
		return minValue == null ? null : minValue;
	}
	
	
	public PrimitiveType getType()
	{
		long minValue = getMinValueValue() == null ? Long.MIN_VALUE : getMinValue();
		long maxValue = getMaxValueValue() == null ? Long.MAX_VALUE : getMaxValue();

		return new JdyLongType(minValue , maxValue);
	}

}