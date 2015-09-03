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
/*
 * Created on 13.03.2004
 *
 */
package de.jdynameta.base.creation.db;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.LongType;
import de.jdynameta.base.metainfo.primitive.TextType;

/**
 * Default implementation of JdyRepositoryTableMapping
 *
 * @author Rainer
 *
 */
@SuppressWarnings("serial")
public class JDyDefaultRepositoryTableMapping implements JdyRepositoryTableMapping
  {

    public JDyDefaultRepositoryTableMapping()
    {
    }

    /* (non-Javadoc)
     * @see de.comafra.model.dbaccess.sql.connection.ClassInfoToTableMapping#getClassMappingFor(de.comafra.model.metainfo.ClassInfo)
     */
    @Override
    public JDyClassInfoToTableMapping getTableMappingFor(ClassInfo aClassInfo)
    {
        return new JDyDefaultClassInfoToTableMapping(aClassInfo);
    }

    /**
     * Return an attribute if: The class has exact one PrimitiveAttribute as key
     * and the attibute has the following properties - it is generateed - is is
     * from the type Long - its internal name ends with Id
     */
    @Override
    public PrimitiveAttributeInfo getGeneratedIdAttribute(ClassInfo aClassInfo)
    {
        assert (aClassInfo != null);
        int countKeys = 0;
        PrimitiveAttributeInfo idAttribute = null;
        for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
        {
            if (curAttr.isKey())
            {
                countKeys++;
                if (curAttr.isGenerated() && curAttr.getInternalName().endsWith("Id")
                        && curAttr instanceof PrimitiveAttributeInfo
                        && ((PrimitiveAttributeInfo) curAttr).getType() instanceof LongType)
                {
                    idAttribute = (PrimitiveAttributeInfo) curAttr;
                }
            }

        }
        return (countKeys == 1) ? idAttribute : null;
    }

    @Override
    public PrimitiveAttributeInfo getDiscriminatorAttribute(ClassInfo aClassInfo)
    {
        assert (aClassInfo != null);

        PrimitiveAttributeInfo discriminatorAttribute = null;
        if (aClassInfo.hasSubClasses() && aClassInfo.getSuperclass() == null)
        {

            for (AttributeInfo curAttr : aClassInfo.getAttributeInfoIterator())
            {
                if (curAttr.isGenerated() && curAttr.getInternalName().endsWith("Discriminator")
                        && curAttr instanceof PrimitiveAttributeInfo
                        && ((PrimitiveAttributeInfo) curAttr).getType() instanceof TextType
                        && ((TextType) ((PrimitiveAttributeInfo) curAttr).getType()).getLength() == 31)
                {
                    discriminatorAttribute = (PrimitiveAttributeInfo) curAttr;
                }
            }
        }
        return discriminatorAttribute;

    }

  }
