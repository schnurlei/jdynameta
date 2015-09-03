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
package de.jdynameta.base.generation;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;

/**
 * default implementation of the PropertyNameCreator
 * @author * <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 */
public class DefaultPropertyNameCreator implements PropertyNameCreator
{
        @Override
	public String getPropertyNameFor( ClassInfo aClassInfo, AttributeInfo aInfo)
	{
		return aClassInfo.getRepoName() 
				+ "." + aClassInfo.getInternalName()
				+ "." + aInfo.getInternalName();	
	}

        @Override
	public String getPropertyNameFor(ClassInfo aClassInfo)
	{
		return aClassInfo.getRepoName() 
				+ "." + aClassInfo.getInternalName();
	}

        @Override
	public String getPropertyNameFor(ClassInfo aClassInfo, AssociationInfo aInfo)
	{
		return aClassInfo.getRepoName() 
				+ "." + aClassInfo.getInternalName()
				+ "." + aInfo.getNameResource();
	}


}
