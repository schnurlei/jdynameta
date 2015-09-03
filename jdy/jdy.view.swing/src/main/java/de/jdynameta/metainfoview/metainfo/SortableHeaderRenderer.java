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
package de.jdynameta.metainfoview.metainfo;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.metainfo.table.ClassInfoColumnModel;
import de.jdynameta.metainfoview.metainfo.table.ClassInfoTableModel;
import de.jdynameta.view.icons.Arrow;

public class SortableHeaderRenderer<TEditObj extends ValueObject> implements TableCellRenderer 
{
    private TableCellRenderer tableCellRenderer;
    private ClassInfoTableModel<TEditObj> tableModel;

    public SortableHeaderRenderer(TableCellRenderer tableCellRenderer, ClassInfoTableModel<TEditObj> aTableModel) 
    {
    	this.tableModel = aTableModel;
    	this.tableCellRenderer = tableCellRenderer;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);
        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            l.setHorizontalTextPosition(JLabel.LEFT);
            int modelColumn = table.convertColumnIndexToModel(column);
            l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
            l.setToolTipText(l.getText());
        }
        return c;
    }
    
    private Icon getHeaderRendererIcon(int column, int size) 
    {
    	ClassInfoTableModel.Directive directive = tableModel.getDirective(column);
        if (directive == null) {
            return null;
        } else {
        	return new Arrow(directive.getDirection() == ClassInfoTableModel.Directive.DESCENDING, size, directive.getSortPos());
        }
    }
    
    public void addSortListenerToHeader(JTableHeader aHeader)
    {
    	MouseAdapter adapter =  new MouseAdapter()
    	{
	        public void mouseClicked(MouseEvent mouseEvent) {
	        	
	            JTableHeader header = (JTableHeader) mouseEvent.getSource();
	            ClassInfoColumnModel columnModel = (ClassInfoColumnModel) header.getColumnModel();
	            // use columnAtPoint because it checks the ComponentOrientation
	            int viewColumn = header.getTable().columnAtPoint(mouseEvent.getPoint());          
	            if( viewColumn > -1) {
		            int modelIdx = columnModel.getColumn(viewColumn).getModelIndex();
		            
		            if (modelIdx != -1) {
		            	tableModel.sortingByColumn(modelIdx, columnModel.getAttributeInfoForModelIdx(modelIdx), mouseEvent.isShiftDown(), mouseEvent.isControlDown());
		               if (header != null) {
		            	   header.repaint();
		               }
		            }
		        }
	        }
    	};
    	aHeader.addMouseListener(adapter);
    }
    
     
}