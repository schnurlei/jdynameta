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
import java.util.LinkedHashMap;
import java.util.List;

import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectPath;
import de.jdynameta.base.metainfo.AttributeHandler;
import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.base.value.ValueObject;

public class JdyMappingUtil
  {

    public static LinkedHashMap<String, List<AspectMapping>> createTableMapping(ClassInfo aClassInfo,
            JDyClassInfoToTableMapping aClassMapping, boolean onlyOwnTables) throws JdyPersistentException
    {
        LinkedHashMap<String, List<AspectMapping>> tableName2ColumnMap = new LinkedHashMap<>();

        for (AttributeInfo curInfo : aClassInfo.getAttributeInfoIterator())
        {
            // build all aspect path for attributes
            ArrayList<AspectPath> resultAspectList = new ArrayList<>();
            AttributePathsObjectReferenceHandler handler = new AttributePathsObjectReferenceHandler(resultAspectList,
                    null);
            curInfo.handleAttribute(handler, null);

            for (AspectPath aspectPath : resultAspectList)
            {
                // build colunm mappings
                List<AspectMapping> mappings = aClassMapping.getColumnMappingsFor(aspectPath);
                for (AspectMapping aspectMapping : mappings)
                {
                    if (aspectMapping.isOwnTable() || !onlyOwnTables)
                    {

                        List<AspectMapping> mappingList = tableName2ColumnMap
                                .get(aspectMapping.getQualifiedTableName());
                        if (mappingList == null)
                        {
                            mappingList = new ArrayList<>();
                            tableName2ColumnMap.put(aspectMapping.getQualifiedTableName(), mappingList);
                        }
                        mappingList.add(aspectMapping);
                    }
                }
            }
        }
        return tableName2ColumnMap;
    }

    /**
     * Generate the AttributePaths for all key attributes and add them to a Set
     * Every AttributePaths starts with the given path
     *
     * @author <a href="mailto:schnurlei@web.de">Rainer Schneider</a>
     *
     */
    public static class AttributePathsObjectReferenceHandler implements AttributeHandler
      {

        private final List<AspectPath> resultAttributPathColl;
        private final AspectPath startPath;

        public AttributePathsObjectReferenceHandler(List<AspectPath> aResultAttributPathColl, AspectPath aStartPath)
        {
            this.startPath = aStartPath;
            this.resultAttributPathColl = aResultAttributPathColl;
        }

        @Override
        public void handleObjectReference(ObjectReferenceAttributeInfo aInfo, ValueObject aObjToHandle)
                throws JdyPersistentException
        {
            for (AttributeInfo curInfo : aInfo.getReferencedClass().getAttributeInfoIterator())
            {
                if (curInfo.isKey())
                {
                    AspectPath pathToAdd = new AspectPath();
                    if (startPath != null && startPath.referenceIterator() != null)
                    {
                        for (Iterator<ObjectReferenceAttributeInfo> refIter = startPath.referenceIterator(); refIter
                                .hasNext();)
                        {
                            pathToAdd.addReference(refIter.next());
                        }
                    }
                    pathToAdd.addReference(aInfo);

                    AttributePathsObjectReferenceHandler handler = new AttributePathsObjectReferenceHandler(
                            resultAttributPathColl, pathToAdd);
                    curInfo.handleAttribute(handler, null);
                }
            }
        }

        @Override
        public void handlePrimitiveAttribute(PrimitiveAttributeInfo aInfo, Object aObjToHandle)
                throws JdyPersistentException
        {
            if (startPath == null)
            {
                AspectPath newPath = new AspectPath();
                newPath.setLastInfo(aInfo);
                resultAttributPathColl.add(newPath);
            } else
            {
                startPath.setLastInfo(aInfo);
                resultAttributPathColl.add(startPath);
            }
        }
      }
}
