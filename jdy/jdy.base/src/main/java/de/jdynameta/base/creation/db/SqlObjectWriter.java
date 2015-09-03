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

import de.jdynameta.base.creation.ObjectWriter;
import de.jdynameta.base.creation.db.JDyClassInfoToTableMapping.AspectMapping;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;

@SuppressWarnings("serial")
public abstract class SqlObjectWriter implements ObjectWriter
{
	public enum KeysOrValues 
	{
		ALL
		, ONLY_KEYS
		,ONLY_VALUES
	}
	
	protected String createDeleteStatement(String aTableName, List<AspectMapping> mappings) throws NoColumnSetException
	{
		StringBuffer stmtBuffer = new StringBuffer(300);
		stmtBuffer.append("DELETE FROM ");
		stmtBuffer.append(aTableName);
		stmtBuffer.append(" WHERE ");

		addColumnNamesAndValuePlaceholders(mappings, stmtBuffer, "AND", KeysOrValues.ONLY_KEYS);
		return stmtBuffer.toString();
	}
	
	
	protected String createInsertStatment(String aTableName, List<AspectMapping> mappings, PrimitiveAttributeInfo aGeneratedKeyAttr)
	{
		StringBuffer stmtBuffer = new StringBuffer(300);

		stmtBuffer.append("INSERT INTO ");
		stmtBuffer.append(aTableName);
		stmtBuffer.append(" ( ");

		addColumnNames(mappings, stmtBuffer, aGeneratedKeyAttr);
		
		stmtBuffer.append(" ) ");
		stmtBuffer.append(" VALUES (");

		addValuePlaceholders(mappings, stmtBuffer, aGeneratedKeyAttr);

		stmtBuffer.append(" ) ");
		return stmtBuffer.toString();
	}
	

	protected String creatUpdateStatement(String aTableName, List<AspectMapping> mappings) throws NoColumnSetException
	{
		StringBuffer stmtBuffer = new StringBuffer(300);

		stmtBuffer.append("UPDATE ");
		stmtBuffer.append(aTableName);
		stmtBuffer.append(" SET ");

		addColumnNamesAndValuePlaceholders(mappings, stmtBuffer, ", ", KeysOrValues.ONLY_VALUES);			

		stmtBuffer.append(" WHERE ");

		addColumnNamesAndValuePlaceholders(mappings, stmtBuffer, "AND", KeysOrValues.ONLY_KEYS);			

		return stmtBuffer.toString();
	}
	
	
	/**
	 * add the comma separated placeholders to the statement string
	 * @param mappings
	 * @param stmtBuffer
	 * @param aGeneratedKeyAttr 
	 */
	protected void addValuePlaceholders(List<AspectMapping> mappings, StringBuffer stmtBuffer, PrimitiveAttributeInfo aGeneratedKeyAttr)
	{
		boolean isFirstAttribute;
		isFirstAttribute = true;
		for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
		{
			AspectMapping curMapping = attrPathIter.next();
			if( (aGeneratedKeyAttr == null ) || (( aGeneratedKeyAttr != null) && curMapping.getMappedPath().getLastInfo() != aGeneratedKeyAttr)) {
				if( !isFirstAttribute) {
					stmtBuffer.append(',');
				}
				stmtBuffer.append('?');
				isFirstAttribute = false;
			}
		}
	}

	/**
	 * add the comma separated column names to the statement string
	 * @param mappings
	 * @param stmtBuffer
	 * @param aGeneratedKeyAttr 
	 */
	protected void addColumnNames(List<AspectMapping> mappings, StringBuffer stmtBuffer, PrimitiveAttributeInfo aGeneratedKeyAttr)
	{
			boolean isFirstAttribute = true;			
			for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
			{
				AspectMapping curMapping = attrPathIter.next();
				if( (aGeneratedKeyAttr == null ) || (( aGeneratedKeyAttr != null) && curMapping.getMappedPath().getLastInfo() != aGeneratedKeyAttr)) {
				
					if( !isFirstAttribute) {
						stmtBuffer.append(',');
					}
		//			stmtBuffer.append(curMapping.getTableName()+"."+curMapping.getColumnName());
					stmtBuffer.append(curMapping.getColumnName());
					isFirstAttribute = false;
				}
			}
		}

	/**
	 * add the comma separated column names to the statement string
	 * @param mappings
	 * @param stmtBuffer
	 * @param keysOrValues 
	 * @return false - at least one columns was set, true - no columns was set
	 * @throws NoColumnSetException 
	 */
	protected void addColumnNamesAndValuePlaceholders(List<AspectMapping> mappings, StringBuffer stmtBuffer, String aSeparator, KeysOrValues keysOrValues) throws NoColumnSetException
	{
		boolean isFirstAttribute = true;			
		for (Iterator<AspectMapping> attrPathIter = mappings.iterator(); attrPathIter.hasNext();)
		{
			AspectMapping curMapping = attrPathIter.next();
			
			if ( KeysOrValues.ALL == keysOrValues 
					|| (KeysOrValues.ONLY_KEYS == keysOrValues && curMapping.getMappedPath().firstAttribute().isKey() )
					|| (KeysOrValues.ONLY_VALUES == keysOrValues && !curMapping.getMappedPath().firstAttribute().isKey()) ) {
//				String columnName = curMapping.getTableName()+"."+curMapping.getColumnName();
				String columnName = curMapping.getColumnName();				
				if (!isFirstAttribute) {
					stmtBuffer.append(' ');
					stmtBuffer.append(aSeparator);
					stmtBuffer.append(' ');
				} else {
					isFirstAttribute = false;
				}
							
				stmtBuffer.append(columnName);
				stmtBuffer.append(" = ");
				stmtBuffer.append(" ?");
			}
		}
		
		if( isFirstAttribute ) {
			throw new NoColumnSetException();
		}
	}

	protected class NoColumnSetException extends Exception
	{
		
	}
}
