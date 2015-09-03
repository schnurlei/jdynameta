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
package de.jdynameta.base.creation.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;

/**
 *
 * @author * <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
 *
 */
public class JDyDefaultClassInfoToTableMapping implements JDyClassInfoToTableMapping
{

    private final ClassInfo classInfo;

    public JDyDefaultClassInfoToTableMapping(ClassInfo aClassInfo)
    {
        this.classInfo = aClassInfo;
    }

    public List<AspectMapping> getColumnMappingsFor(AspectPath anAttributePath)
    {
        assert (anAttributePath.getLastInfo() != null);

        List<AspectMapping> allMappings = new ArrayList<>();
        String columnName = "";

        boolean isFirstRef = true;
        if (anAttributePath.referenceIterator() != null)
        {
            for (Iterator<ObjectReferenceAttributeInfo> attrIter = anAttributePath.referenceIterator(); attrIter.hasNext();)
            {
                ObjectReferenceAttributeInfo curAttrInfo = attrIter.next();

                if (columnName.length() != 0)
                {
                    columnName += "_";
                }
                if (isFirstRef)
                {
                    // use name to distinguish several references on the same class
                    columnName += curAttrInfo.getExternalName();
                    isFirstRef = false;
                } else
                {
                    columnName += curAttrInfo.getReferencedClass().getShortName();
                }
            }
        }

        if (columnName.length() != 0)
        {
            columnName += "_";
        }
        columnName += anAttributePath.getLastInfo().getExternalName();

        ClassInfo curInfo = classInfo;

        while (curInfo != null)
        {

            if (anAttributePath.firstAttribute().isKey())
            { // for key add an table for the start class and every superlcass  
                allMappings.add(new JDyClassInfoToTableMapping.AspectMapping(anAttributePath, curInfo == classInfo, getQualifiedTableName(curInfo), getTableName(curInfo), curInfo.getShortName(), columnName));
            } else
            {	// add non key attributes only in the class they occur
                if (curInfo.isSubclassAttribute(anAttributePath.firstAttribute()))
                {
                    allMappings.add(new JDyClassInfoToTableMapping.AspectMapping(anAttributePath, curInfo == classInfo, getQualifiedTableName(curInfo), getTableName(curInfo), curInfo.getShortName(), columnName));
                }
            }
            curInfo = curInfo.getSuperclass();
        }

        return allMappings;
    }

    protected String getQualifiedTableName(ClassInfo curInfo)
    {
        return curInfo.getExternalName();
    }

    protected String getTableName(ClassInfo curInfo)
    {
        return curInfo.getExternalName();
    }
  }
