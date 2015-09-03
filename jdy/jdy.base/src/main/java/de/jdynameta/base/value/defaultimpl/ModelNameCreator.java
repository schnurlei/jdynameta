/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.base.value.defaultimpl;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.BooleanType;
import de.jdynameta.base.value.DefaultClassNameCreator;

@SuppressWarnings("serial")
public class ModelNameCreator extends DefaultClassNameCreator
{

    @Override
    public String getReferenceClassNameFor(ClassInfo aInfo)
    {
        return "de.jdynameta.model.metainfo." + aInfo.getInternalName().substring(0,
                aInfo.getInternalName().length() - "Model".length());
    }

    /**
     * Add Value to getter to distinguish from the primitive type Method
     * @param aInfo
     * @return 
     */
    @Override
    public String getGetterNameFor(AttributeInfo aInfo)
    {
        String result = super.getGetterNameFor(aInfo);
        if (isBooleanAttribute(aInfo))
        {
            result += "Value";
        }
        return result;
    }

    private static boolean isBooleanAttribute(AttributeInfo aInfo)
    {
        return aInfo instanceof PrimitiveAttributeInfo
                && ((PrimitiveAttributeInfo) aInfo).getType() instanceof BooleanType;
    }

}
