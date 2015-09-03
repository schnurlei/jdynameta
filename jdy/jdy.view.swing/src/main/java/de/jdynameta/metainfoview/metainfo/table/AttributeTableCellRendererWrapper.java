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
package de.jdynameta.metainfoview.metainfo.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.value.ValueObject;

/**
 * @author Rainer Schneider
 *
 */
public class AttributeTableCellRendererWrapper implements TableCellRenderer 
{
	private AttributeInfo attributeInfo;
	
	private TableCellRenderer rendererComponent;
	private JLabel errorComponent;
	
	/**
	 * Constructor for AttributeTableCellRenderer.
	 */
	public AttributeTableCellRendererWrapper(AttributeInfo aInfo, TableCellRenderer aRendererComponent) 
	{
		super();
		this.attributeInfo = aInfo;
		
		errorComponent = new JLabel("ERROR");
		rendererComponent = aRendererComponent;
	}


	public Component getTableCellRendererComponent(JTable table, Object value,	boolean isSelected
						,boolean hasFocus, int row,int column) 
	{
		Component result = null;

		if ( value instanceof ValueObject) {
			Object attributeValue = ((ValueObject) value).getValue(this.attributeInfo);
			result = rendererComponent.getTableCellRendererComponent(table, attributeValue, isSelected
						,hasFocus, row, column);
		} else 
		{
			result = errorComponent;
		}

		return result;
	}
	
}
