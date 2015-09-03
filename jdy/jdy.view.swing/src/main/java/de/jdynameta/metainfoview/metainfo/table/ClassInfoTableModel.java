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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.jdynameta.base.metainfo.AttributeInfo;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;
import de.jdynameta.base.value.ValueObject;
import de.jdynameta.persistence.objectlist.ObjectListFilterAndSortWrapper;

/**
 * @author Rainer Schneider
 *
 */
public class ClassInfoTableModel<TListObject extends ValueObject> extends SwingTableModelMapping<TListObject> 
{
	private ClassInfo classInfo;
	private ObjectListFilterAndSortWrapper<TListObject> sortWrapper;
    private Map<Integer,Directive> modelIdx2SortDirectiveMap = new HashMap<Integer,Directive>();
	private final Collator textCollator;

	public ClassInfoTableModel (ClassInfo aClassInfo)
	{
		super(null);
		this.classInfo = aClassInfo;
		this.sortWrapper = new ObjectListFilterAndSortWrapper<TListObject>();
		this.textCollator = Collator.getInstance();
		this.textCollator.setStrength(Collator.TERTIARY);
		super.setListModel(this.sortWrapper);
	}


	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() 
	{
		return classInfo.attributeInfoSize();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */

	public TListObject getObjectAt(int aIndex) throws ProxyResolveException
	{
		return this.getRowAt(aIndex);	
	}


	/* (non-Javadoc)
	 * @see de.comafra.view.metainfo.table.SwingTableModelMapping#getColumnValue(java.lang.Object, int)
	 */
	@Override
	public Object getColumnValue(Object aRowObject, int aColumnIndex)
	{
		return aRowObject;
	}
	
	@Override
	public void setListModel(ObjectListModel<TListObject> listModel)
	{
		if(sortWrapper != null ) {
			this.sortWrapper.setWrappedObjectList(listModel);
		}
	}
	
    public ClassInfoTableModel.Directive getDirective(int modelColIdx)
	{
		return modelIdx2SortDirectiveMap.get(modelColIdx);
	}

	private int getSortingStatus(int column)
	{
		return (getDirective(column) == null) ? ClassInfoTableModel.Directive.NOT_SORTED : getDirective(column).direction;
	}

	public void sortingByColumn(int modelColIdx, AttributeInfo aInfo, boolean down, boolean addStatus)
	{
		if( aInfo != null)
		{
			int status = getSortingStatus(modelColIdx);
	
			if (!addStatus)
			{
				modelIdx2SortDirectiveMap.clear();
			}
	
			// Cycle the sorting states through {NOT_SORTED, ASCENDING,DESCENDING}
			status = status + ((down) ? -1 : 1);
			status = (status + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
	
			Directive directive = getDirective(modelColIdx);
			if (directive != null)
			{
				modelIdx2SortDirectiveMap.remove(directive);
			}
			if (status != Directive.NOT_SORTED)
			{
				modelIdx2SortDirectiveMap.put(modelColIdx, new Directive(modelColIdx, status, modelIdx2SortDirectiveMap.size(), aInfo) );
			}
			sortingStatusChanged();
		}
	}

	private void sortingStatusChanged()
	{
		this.sortWrapper.setSortComparator(getComparatorFor());
	}

	
	private Comparator<TListObject> getComparatorFor()
	{
		final ArrayList<Directive> directiveColl = new ArrayList<Directive>();
		directiveColl.addAll(modelIdx2SortDirectiveMap.values());
		Collections.sort(directiveColl);

		return new Comparator<TListObject>()
		{
			public int compare(TListObject object1, TListObject object2) 
			{

               int comparison = 0;
	           for (Iterator<Directive> it = directiveColl.iterator(); comparison == 0 && it.hasNext();) {
	        	   
	        	   	Directive directive = it.next();

	                Object o1 = object1.getValue(directive.getAttrbuteInfo());
	                Object o2 = object2.getValue(directive.getAttrbuteInfo());

	                // Define null less than everything, except null.
	                if (o1 == null && o2 == null) {
	                    comparison = 0;
	                } else if (o1 == null) {
	                    comparison = 1;
	                } else if (o2 == null) {
	                    comparison = -1;
	                } else {
	                	if( o1 instanceof String || !(o1 instanceof Comparable) ) {
	                		comparison = textCollator.compare(o1.toString(), o2.toString()); 
	                	} else {
	                		comparison = ( (Comparable)o1).compareTo(o2);
	                	}
	                    if (comparison != 0) {
	                    	comparison =  directive.getDirection() == Directive.DESCENDING ? -comparison : comparison;
	                    }
	                }
	            }			
				return comparison;
	        }
		};
	}
	

	public static class Directive implements Comparable<Directive>
	{
		public static final int DESCENDING = -1;
		public static final int NOT_SORTED = 0;
		public static final int ASCENDING = 1;

		private int modelColIdx;
		private int direction;
		private int sortPos;
		private AttributeInfo attrbuteInfo;

		public Directive(int aModelColIdx, int direction, int aSortPos, AttributeInfo anAttrInfo)
		{
			this.modelColIdx = aModelColIdx;
			this.direction = direction;
			this.sortPos = aSortPos;
			this.attrbuteInfo = anAttrInfo;
		}

		public AttributeInfo getAttrbuteInfo()
		{
			return attrbuteInfo;
		}
		
		/**
		 * @return the column
		 */
		public int getColumn()
		{
			return modelColIdx;
		}

		/**
		 * @return the direction
		 */
		public int getDirection()
		{
			return direction;
		}

		public int getSortPos()
		{
			return sortPos;
		}
		
		public int compareTo(Directive anotherDirective)
		{
			int thisSortPos = this.getSortPos();
			int anotherSortPos = anotherDirective.getSortPos();
			return thisSortPos<anotherSortPos ? -1 : (thisSortPos==anotherSortPos ? 0 : 1) ;
		}
	}

}
