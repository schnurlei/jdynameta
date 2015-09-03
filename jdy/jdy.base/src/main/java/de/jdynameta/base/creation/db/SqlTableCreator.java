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
package de.jdynameta.base.creation.db;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.primitive.PrimitiveTypeVisitor;
import de.jdynameta.base.value.JdyPersistentException;

public abstract class SqlTableCreator
{
	
	public void  buildTableForClassInfo( ClassInfo aClassInfo) throws JdyPersistentException 
	{
		JDyClassInfoToTableMapping aClassMapping = getTableMapping().getTableMappingFor(aClassInfo);

	   	Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aClassInfo, aClassMapping, true);
	   	
	   	for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet()) {
	   		try
			{
				buildTableForClassInfo(aClassInfo, entry.getKey(), entry.getValue());
				createPrimaryKeyForClass(aClassInfo, entry.getKey(), entry.getValue());
			} catch (Exception ex)
			{
				throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
			}
	   	}
	}
	
	public void  buildForeignKeysForClassInfo( ClassInfo aClassInfo) throws JdyPersistentException 
	{
		JDyClassInfoToTableMapping aClassMapping = getTableMapping().getTableMappingFor(aClassInfo);

	   	Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aClassInfo, aClassMapping, true);
	   	
	   	for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet()) {
	   		try
			{
				createForeignKeysForClass(aClassInfo, entry.getKey(), entry.getValue());
			} catch (Exception ex)
			{
				throw new JdyPersistentException(ex.getLocalizedMessage(), ex);
			}
	   	}
	}
	
	
	protected String createCreateTableStmt(ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings)
	{
		StringBuffer sqlBuffer = new StringBuffer(300);
		
		sqlBuffer.append("CREATE TABLE ");
		sqlBuffer.append(aTableName);
		sqlBuffer.append("(");
		
		addClassAttributes(aClassInfo, mappings, sqlBuffer);
		sqlBuffer.append(")");
		return sqlBuffer.toString();
	}
	
	
	public void deleteTableForClassInfo(ClassInfo aClassInfo) throws JdyPersistentException
	{
		
		JDyClassInfoToTableMapping aClassMapping = getTableMapping().getTableMappingFor(aClassInfo);
	   	Map<String, List<AspectMapping>> tableName2ColumnMap = JdyMappingUtil.createTableMapping(aClassInfo, aClassMapping, true);
	   	
	   	for (Map.Entry<String, List<AspectMapping>> entry : tableName2ColumnMap.entrySet()) {
			deleteTableForClassInfo(entry.getKey());
	   	}
	}
	
	protected void addClassAttributes( ClassInfo aClassInfo, List<AspectMapping> mappings, StringBuffer aSqlBuffer) 
	{

		boolean isFirstAttribute = true;			

		for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
		{
			AspectMapping curMapping = attrPathIter.next();
			addColumn(aClassInfo, aSqlBuffer, curMapping, isFirstAttribute);
			isFirstAttribute = false;			
		}

	}
	
	protected void addColumn( ClassInfo aClassInfo, StringBuffer aSqlBuffer, AspectMapping columnMapping, boolean isFirstAttribute)
	{
		if( !isFirstAttribute) {
			aSqlBuffer.append(',');
		}
		addColumnName(aClassInfo, aSqlBuffer, columnMapping);
		addColumnType(aClassInfo, aSqlBuffer, columnMapping);
		
		createNotNullDirective(aClassInfo, aSqlBuffer, columnMapping);
		
	}

	protected void addColumnName(ClassInfo aClassInfo, StringBuffer aSqlBuffer, AspectMapping columnMapping) 
	{
		aSqlBuffer.append(columnMapping.getColumnName());
		aSqlBuffer.append(' ');
	}

	protected void addColumnType(ClassInfo aClassInfo, StringBuffer aSqlBuffer, AspectMapping columnMapping) 
	{
		try {
			this.getTypeHandler().setBuffer(aSqlBuffer);
			columnMapping.getMappedPath().getLastInfo().getType().handlePrimitiveKey(this.getTypeHandler(), null);
		} catch (JdyPersistentException ex) {
			ex.printStackTrace();
		}
		aSqlBuffer.append(' ');
	}

	
	protected void createNotNullDirective(ClassInfo aClassInfo, StringBuffer aSqlBuffer, AspectMapping columnMapping) 
	{
		if( columnMapping.getMappedPath().firstAttribute().isNotNull()) {
			aSqlBuffer.append(" NOT NULL ");
		}
	}
	
	
	public abstract BufferedPrimitiveTypeVisitor getTypeHandler();
	
	protected abstract void deleteTableForClassInfo(String aTableName) throws JdyPersistentException; 
	 	
	public abstract JdyRepositoryTableMapping getTableMapping();
	
	protected abstract void  buildTableForClassInfo(  ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings ) throws JdyPersistentException; 
	
	protected abstract void createPrimaryKeyForClass( ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings ) throws JdyPersistentException; 

	protected abstract void createForeignKeysForClass( ClassInfo aClassInfo, String aTableName, List<AspectMapping> mappings ) throws JdyPersistentException; 
	

	/** Get the name of the fields in the index 
	*/
	protected void addIndexAttributesForClassInfo(List<AspectMapping> mappings, StringBuffer aSqlBuffer)
	{
		boolean isFirstAttribute = true;			
		for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
		{
			AspectMapping curMapping = attrPathIter.next();
			if( curMapping.getMappedPath().firstAttribute().isKey() ) {
				if( !isFirstAttribute) {
					aSqlBuffer.append(',');
				} else {
					isFirstAttribute = false;
				}
				aSqlBuffer.append(curMapping.getColumnName());
				isFirstAttribute = false;
			}
		}
		
	}


	public static interface BufferedPrimitiveTypeVisitor extends PrimitiveTypeVisitor
	{
		void setBuffer(StringBuffer sqlBuffer);
	}

}
