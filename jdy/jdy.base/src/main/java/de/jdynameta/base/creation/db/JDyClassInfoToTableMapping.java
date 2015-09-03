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

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ObjectReferenceAttributeInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;

public interface JDyClassInfoToTableMapping
  {

    /**
     * Get the Mappings for the given AttributePath
     *
     * @param anAttributePath list of Attributes, which define a Path to a
     * Primitive Attribute starting at aClassInfo
     * @return
     */
    public List<AspectMapping> getColumnMappingsFor(AspectPath anAttributePath);

    /**
     * Describes the path from a class to the primitive attribute of one of its
     * object references One Aspect Path could be mapped to a one or more tables
     *
     * @author rainer
     *
     */
    public static class AspectPath
      {

        private List<ObjectReferenceAttributeInfo> referenceList;
        private PrimitiveAttributeInfo lastInfo;

        public AspectPath()
        {
        }

        public AspectPath(PrimitiveAttributeInfo aLastInfo)
        {
            this.lastInfo = aLastInfo;
        }

        public void addReference(ObjectReferenceAttributeInfo aNextReference)
        {
            // lazy initialize
            if (referenceList == null)
            {
                referenceList = new ArrayList<>();
            }
            referenceList.add(aNextReference);
        }

        public PrimitiveAttributeInfo getLastInfo()
        {
            return lastInfo;
        }

        public void setLastInfo(PrimitiveAttributeInfo aLastInfo)
        {
            lastInfo = aLastInfo;
        }

        public Iterator<ObjectReferenceAttributeInfo> referenceIterator()
        {
            return (referenceList == null) ? null : referenceList.iterator();
        }

        public int getReferenceListSize()
        {
            return referenceList == null ? 0 : referenceList.size();
        }

        public ObjectReferenceAttributeInfo getReferenceAt(int index)
        {
            return referenceList == null ? null : referenceList.get(index);
        }

        public AttributeInfo firstAttribute()
        {
            if (referenceList == null)
            {
                return lastInfo;
            } else
            {
                return referenceList.get(0);
            }
        }

        @Override
        public String toString()
        {
            String result = "";
            if (referenceList != null)
            {
                for (ObjectReferenceAttributeInfo curRef : referenceList)
                {
                    result += curRef.getInternalName() + "->";
                }
            }
            result += lastInfo.getInternalName();

            return result;
        }

        public void removeLastReference(ObjectReferenceAttributeInfo aAttrInfo)
        {
            if (referenceList != null)
            {
                referenceList.remove(referenceList.size() - 1);
            }
        }
      }

    /**
     * Mappping for a Aspect Path One Aspect Path could have one or more
     * mappings
     *
     * @author rainer
     *
     */
    public static class AspectMapping
      {

        private final AspectPath mappedPath;
        private final boolean isOwnTable;
        private final String qualifiedTableName;
        private final String tableName;
        private final String tableShortName;
        private final String columnName;

        public AspectMapping(AspectPath aMappedPath, boolean aIsOwnTable, String aQualifiedTableName, String aTableName, String aTableShortName, String aColumnName)
        {
            super();
            this.mappedPath = aMappedPath;
            this.isOwnTable = aIsOwnTable;
            this.qualifiedTableName = aQualifiedTableName;
            this.tableName = aTableName;
            this.tableShortName = aTableShortName;
            this.columnName = aColumnName;
        }

        public String getQualifiedTableName()
        {
            return qualifiedTableName;
        }

        public String getTableName()
        {
            return tableName;
        }

        public String getTableShortName()
        {
            return tableShortName;
        }

        public String getColumnName()
        {
            return columnName;
        }

        public boolean isOwnTable()
        {
            return isOwnTable;
        }

        public AspectPath getMappedPath()
        {
            return mappedPath;
        }

        @Override
        public String toString()
        {
            // TODO Auto-generated method stub
            return qualifiedTableName + "." + columnName + " # " + mappedPath.toString();
        }
      }
  }
