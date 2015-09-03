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
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import de.jdynameta.persistence.state.PersistentValueObject;

public class TableRowMarkerRenderer extends DefaultTableCellRenderer
{

	public TableRowMarkerRenderer() 
	{
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		setForeground(UIManager.getColor("TableHeader.foreground"));
		setBackground(UIManager.getColor("TableHeader.background"));
	}

    /**
     *  This method is sent to the renderer by the drawing table to
     *  configure the renderer appropriately before drawing.  Return
     *  the Component used for drawing.
     *
     * @param	table		the JTable that is asking the renderer to draw.
     *				This parameter can be null.
     * @param	value		the value of the cell to be rendered.  It is
     *				up to the specific renderer to interpret
     *				and draw the value.  eg. if value is the
     *				String "true", it could be rendered as a
	  *				string or it could be rendered as a check
     *				box that is checked.  null is a valid value.
     * @param	isSelected	true is the cell is to be renderer with
     *				selection highlighting
     * @param	row	        the row index of the cell being drawn.  When
     *				drawing the header the rowIndex is -1.
     * @param	column	        the column index of the cell being drawn
     */
	 @Override
	public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus,
						int row, int column) {
		JLabel renderer =  (JLabel) super. getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
		
		String result = ""+row;
		if( value != null && value instanceof PersistentValueObject) {
			PersistentValueObject model = (PersistentValueObject) value;
			result = ""+row;
			if ( model.getPersistentState().isNew()) {
				result += "*";
			} else if ( model.getPersistentState().isMarkedAsDeleted()) {
				result += "-";
			} else if ( model.getPersistentState().isDirty()) {
				result += "!";
			}

		}
		
		setText(result);
		if( isSelected) {
			setBackground(UIManager.getColor("TableHeader.foreground"));
			setForeground(UIManager.getColor("TableHeader.background"));
		} else {
			setForeground(UIManager.getColor("TableHeader.foreground"));
			setBackground(UIManager.getColor("TableHeader.background"));
		}
		
		return renderer;
	 }

}
