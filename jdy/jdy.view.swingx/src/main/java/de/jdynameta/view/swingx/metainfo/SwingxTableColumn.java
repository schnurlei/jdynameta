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
package de.jdynameta.view.swingx.metainfo;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.table.TableColumnExt;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.metainfo.AttributeInfo;

public class SwingxTableColumn extends TableColumnExt
{
	private AssociationInfo assocInfo;
	private AttributeInfo attrInfo;
	private boolean isMarker = false;
	
	public SwingxTableColumn() 
	{
		super();
	}

	public SwingxTableColumn(int modelIndex, int width,
			TableCellRenderer cellRenderer, TableCellEditor cellEditor) 
	{
		super(modelIndex, width, cellRenderer, cellEditor);
	}

	public SwingxTableColumn(int modelIndex, int width) 
	{
		super(modelIndex, width);
	}

	public SwingxTableColumn(int modelIndex) 
	{
		super(modelIndex);
	}

	public AssociationInfo getAssocInfo() 
	{
		return assocInfo;
	}

	public void setAssocInfo(AssociationInfo assocInfo) 
	{
		this.assocInfo = assocInfo;
	}

	public AttributeInfo getAttrInfo() 
	{
		return attrInfo;
	}

	public void setAttrInfo(AttributeInfo attrInfo) 
	{
		this.attrInfo = attrInfo;
	}

	public void setMarker(boolean isMarker) 
	{
		this.isMarker = isMarker;
	}

	public boolean isMarker() 
	{
		return isMarker;
	}
	
}