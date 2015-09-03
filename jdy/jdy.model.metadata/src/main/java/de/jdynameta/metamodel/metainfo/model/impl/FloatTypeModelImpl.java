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
import de.jdynameta.base.metainfo.impl.JdyFloatType;
import de.jdynameta.metamodel.metainfo.MetaModelRepository;

/**
 * FloatTypeModelImpl
 *
 * @author Copyright &copy;  
 * @author Rainer Schneider
 * @version 
 */
@SuppressWarnings("serial")
public  class FloatTypeModelImpl extends de.jdynameta.metamodel.metainfo.model.impl.PrimitiveAttributeInfoModelImpl
	implements de.jdynameta.metamodel.metainfo.model.FloatTypeModel

{
	private java.lang.Long scale;
	private java.lang.Long maxValue;

	/**
	 *Constructor 
	 */
	public FloatTypeModelImpl ()
	{
		super(MetaModelRepository.getSingleton().getFloatTypeModelInfo());
	}

	/**
	 * Get the scale
	 * @generated
	 * @return get the scale
	 */
	public Long getScaleValue() 
	{
		return scale;
	}

	/**
	 * set the scale
	 * @generated
	 * @param scale
	 */
	public void setScale( Long aScale) 
	{
		scale = aScale;
	}

	public Long getScale() 
	{
		return scale;
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
		return maxValue;
	}

	public PrimitiveType getType()
	{
		return new JdyFloatType();
	}
	

}