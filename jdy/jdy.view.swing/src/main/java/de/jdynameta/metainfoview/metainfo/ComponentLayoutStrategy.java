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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.generation.PropertyNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.view.JdyResourceLoader;

/**
 * Strategy now the Attributes Components are arranged on the main panel
 * @author Rainer Schneider
 *
 */
public class ComponentLayoutStrategy
{
	private final PropertyNameCreator propertyGenerator;
	private final JdyResourceLoader resourceLoader;

	public ComponentLayoutStrategy(JdyResourceLoader aResourceLoader)
	{
		this.propertyGenerator = new DefaultPropertyNameCreator();
		this.resourceLoader = aResourceLoader;
	}	

	public JPanel createMainPanel(Collection<AttrInfoComponent> modelComponentColl, ClassInfo aClassInfo) throws JdyPersistentException
	{

		final GridBagConstraints 	col1LblConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,1,1
															,0.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.NONE
															, new Insets(5,5,0,5),0,0 );
	
		final GridBagConstraints col1TxfConstr = new GridBagConstraints(1, GridBagConstraints.RELATIVE,1,1
															,1.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL
															, new Insets(5,0,0,5),0,0 );

		final JPanel mainPnl = new JPanel(new GridBagLayout());
		
		// create association tab panes
		JTabbedPane tabpAssoc = null;

		for (AttrInfoComponent modelComp : modelComponentColl)
		{
			if( modelComp.getAttributeInfo() != null) {
				String attrNameProperty = propertyGenerator.getPropertyNameFor(aClassInfo, modelComp.getAttributeInfo());
				modelComp.addLabelToContainer(resourceLoader.getString(attrNameProperty), mainPnl, col1LblConstr);
				modelComp.addToContainer(mainPnl, col1TxfConstr);
			} else if (  modelComp.getAssociationInfo() != null) {
				
				if( tabpAssoc == null ) {
					tabpAssoc = new JTabbedPane();
				}
				String assocNameProperty = propertyGenerator.getPropertyNameFor(aClassInfo,modelComp.getAssociationInfo() );
				modelComp.addToContainer(tabpAssoc, resourceLoader.getString(assocNameProperty));
			}
		}
	
		final GridBagConstraints tblConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,2,1
				,1.0, 1.0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH
				, new Insets(15,0,5,5),0,0 );
		
		if( tabpAssoc != null) {
			mainPnl.add(tabpAssoc, tblConstr);
		} else {
			tblConstr.weighty = 1.0; tblConstr.fill = GridBagConstraints.VERTICAL;
			mainPnl.add(Box.createVerticalGlue(), col1LblConstr);
			tblConstr.weightx = 1.0; tblConstr.fill = GridBagConstraints.HORIZONTAL;
			mainPnl.add(Box.createHorizontalGlue(), tblConstr);
		}
		
	
		String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(aClassInfo);
		mainPnl.setBorder(		BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder(4, 4, 4, 4)
				, BorderFactory.createTitledBorder( getResourceLoader().getString(classNameProperty))));
		return mainPnl;
	}
	
	public JdyResourceLoader getResourceLoader()
	{
		return resourceLoader;
	}
	
	public PropertyNameCreator getPropertyGenerator()
	{
		return propertyGenerator;
	}
	
	/**
	 * @param o1
	 * @return
	 */
	private String getTextForClassInfo(ClassInfo aClassInfo) 
	{
		String attrNameProperty = propertyGenerator.getPropertyNameFor(aClassInfo);
		return resourceLoader.getString(attrNameProperty);
	}
	
	private Comparator<ClassInfo> createAttributeTextComparator() 
	{
		final Collator textCollator = Collator.getInstance();
		textCollator.setStrength(Collator.TERTIARY);

		return new Comparator<ClassInfo>()
		{
			public int compare(ClassInfo info1, ClassInfo info2)
			{
				return  textCollator.compare( getTextForClassInfo((ClassInfo)info1)
												, getTextForClassInfo((ClassInfo)info1));
			}
		};
	}

	private ListCellRenderer createAttributeTextRenderer() 
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
						((JLabel)resultComp).setText(getTextForClassInfo((ClassInfo)value));															
					} else {
						((JLabel)resultComp).setText("");															
					}
				}
				return resultComp;																			
			}

		};
	}
	
}
