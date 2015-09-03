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

import javax.swing.table.TableColumn;

import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.metainfoview.metainfo.table.SwingTableModelMapping;

/**
 * @author Rainer Schneider
 *
 */
@SuppressWarnings("serial")
public class SwingxClassInfoTableModel<TListObject extends ValueObject> extends SwingTableModelMapping<TListObject> 
{
	private SwingxClassInfoColumnModel colModel;

	public SwingxClassInfoTableModel ( SwingxClassInfoColumnModel aColModel)
	{
		super(null);
		this.colModel = aColModel;
	}


	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() 
	{
		return colModel.getColumnCount(true);
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */

	public TListObject getObjectAt(int aIndex) throws ProxyResolveException
	{
		return this.getRowAt(aIndex);	
	}

	@Override
	public Class<?> getColumnClass(int aColumnIndex) 
	{
		Class<?> result = super.getColumnClass(aColumnIndex);

		if ( aColumnIndex < colModel.getColumnCount()) {
			TableColumn col = colModel.getColumnAtModelIdx(aColumnIndex);
			if( col instanceof SwingxTableColumn ) {
				SwingxTableColumn swingxCol = (SwingxTableColumn) col;
	
				if( swingxCol.getAttrInfo() != null) {
					if( swingxCol.getAttrInfo() instanceof PrimitiveAttributeInfo) {
						result = ((PrimitiveAttributeInfo) swingxCol.getAttrInfo()).getJavaTyp();
					} else {
						result = ValueObject.class;
					}
				} else if( swingxCol.getAssocInfo() != null) {
					result = ValueObject.class;
				}
			}
		}
		
		return result;
	}
	/* (non-Javadoc)
	 * @see de.comafra.view.metainfo.table.SwingTableModelMapping#getColumnValue(java.lang.Object, int)
	 */
	@Override
	public Object getColumnValue(Object aRowObject, int aColumnIndex)
	{
		Object result = aRowObject;
		
		TableColumn col = colModel.getColumnAtModelIdx(aColumnIndex);
		if( col instanceof SwingxTableColumn && aRowObject instanceof ValueObject) {
			SwingxTableColumn swingxCol = (SwingxTableColumn) col;

			if( swingxCol.getAttrInfo() != null) {
				result = ((ValueObject)aRowObject).getValue(swingxCol.getAttrInfo());
			} else if( swingxCol.getAssocInfo() != null) {
				result = ((ValueObject)aRowObject).getValue(swingxCol.getAssocInfo());
			} else if ( swingxCol.isMarker()) {
				result = null;
			}
		}
		
		return result;
	}
	
	

	


}
