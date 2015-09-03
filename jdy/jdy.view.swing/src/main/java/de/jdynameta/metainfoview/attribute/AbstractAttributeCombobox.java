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
package de.jdynameta.metainfoview.attribute;

import java.awt.Component;
import java.text.Collator;
import java.util.Comparator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.jdynameta.base.metainfo.AssociationInfo;
import de.jdynameta.base.objectlist.ObjectListModel;
import de.jdynameta.base.objectlist.ProxyResolveException;


/**
 *
 * @author Rainer Schneider
 *
 */
public abstract class AbstractAttributeCombobox<TDisplayedObject> extends AttributeCombobox<TDisplayedObject> 
{
	/**
	 * @param aObjectColl
	 */
	public AbstractAttributeCombobox(ObjectListModel<TDisplayedObject>  aObjectColl) throws ProxyResolveException 
	{
		super(aObjectColl);
		
		setAttributeComparator(createAttributeTextComparator() );
		setAttributeRenderer(createAttributeTextRenderer());
		//setKeySelectionManager(createKeySelectionFinder());		
	}

//	protected Display createKeySelectionFinder() 
//	{
//		return new IncrementalTypeFinder.Display() 
//		{
//			public String getDisplay(Object object) 
//			{
//				return getTextForAttribute(object);
//			}
//		};
//	}

	protected Comparator<TDisplayedObject> createAttributeTextComparator() 
	{
		final Collator textCollator = Collator.getInstance();
		textCollator.setStrength(Collator.TERTIARY);

		return new Comparator<TDisplayedObject>()
		{
			public int compare(Object o1, Object o2)
			{
				return  textCollator.compare( getTextForAttribute(o1), getTextForAttribute(o2));
			}
		};
	}

	@SuppressWarnings("serial")
	protected ListCellRenderer createAttributeTextRenderer() 
	{
		return new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(
				JList list,	Object value, int index,
				boolean isSelected, boolean cellHasFocus) 
			{
				Component resultComp = super.getListCellRendererComponent( list, value, index
																			,isSelected, cellHasFocus);
				if( resultComp instanceof JLabel ) {
					if( value != null) {
						((JLabel)resultComp).setText(getTextForAttribute(value));															
					} else {
						((JLabel)resultComp).setText("_  ");															
					}
				}
				return resultComp;																			
			}

		};
	}

	protected String getTextForAttribute(Object aValue)
	{
		return aValue.toString();
	}

	public AssociationInfo getAssociationInfo()
	{
		return null;
	}
	
}
