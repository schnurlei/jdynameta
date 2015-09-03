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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXTitledSeparator;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.metainfoview.metainfo.ComponentLayoutStrategy;
import de.jdynameta.view.JdyResourceLoader;

public class GroupedLayoutStrategy extends ComponentLayoutStrategy
{

	public GroupedLayoutStrategy(JdyResourceLoader resourceLoader)
	{
		super(resourceLoader);
	}

	public JPanel createMainPanel(Collection<AttrInfoComponent> modelComponentColl, ClassInfo aClassInfo) throws JdyPersistentException
	{

		
		final GridBagConstraints 	col1GroupConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,2,1
				,1.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL
				, new Insets(5,5,5,15),0,0 );
		final GridBagConstraints 	col1LblConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,1,1
									,0.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.NONE
									, new Insets(5,5,5,15),0,0 );
		final GridBagConstraints col1TxfConstr = new GridBagConstraints(1, GridBagConstraints.RELATIVE,1,1
									,1.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL
									, new Insets(5,5,5,15),0,0 );


		JPanel fieldPnl = new JPanel(new GridLayout(1,2));
		JPanel gridPanl1 = new JPanel(new GridBagLayout());
		JPanel gridPanl2 = new JPanel(new GridBagLayout());
		fieldPnl.add(gridPanl1);
		fieldPnl.add(gridPanl2);
		
		// create association tab panes
		JTabbedPane tabpAssoc = new JTabbedPane();
		tabpAssoc.setBorder( 	BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED)
				, BorderFactory.createEmptyBorder(4, 2, 4, 2)));
		JPanel textAreaPnl = new JPanel(new GridBagLayout());

		gridPanl1.add( new JXTitledSeparator("Common", SwingConstants.LEADING),col1GroupConstr ); 
		gridPanl2.add( new JXTitledSeparator(" ", SwingConstants.LEADING),col1GroupConstr ); 
		
		int fieldCnt = 0; 
		for (AttrInfoComponent modelComp : modelComponentColl)
		{
			if( modelComp.getAttributeInfo() != null) {
				if (modelComp.getAttributeInfo() instanceof PrimitiveAttributeInfo 
						&& ( ((PrimitiveAttributeInfo)modelComp.getAttributeInfo()).getType() instanceof VarCharType ) ){
					String attrNameProperty = getPropertyGenerator().getPropertyNameFor(aClassInfo, modelComp.getAttributeInfo());
					modelComp.addLabelToContainer(getResourceLoader().getString(attrNameProperty), textAreaPnl, col1LblConstr);
					col1TxfConstr.weighty = 1.0; col1TxfConstr.fill = GridBagConstraints.BOTH;
					modelComp.addToContainer(textAreaPnl, col1TxfConstr);
					col1TxfConstr.weighty = 0.0; col1TxfConstr.fill = GridBagConstraints.HORIZONTAL;
				} else {
					JPanel gridPnl = (  (modelComponentColl.size() > 5) && fieldCnt > (modelComponentColl.size()/2) ) ? gridPanl2 : gridPanl1;
					
					String attrNameProperty = getPropertyGenerator().getPropertyNameFor(aClassInfo, modelComp.getAttributeInfo());
					modelComp.addLabelToContainer(getResourceLoader().getString(attrNameProperty), gridPnl, col1LblConstr);
					modelComp.addToContainer(gridPnl, col1TxfConstr);
					fieldCnt++;
				}
			} else if (  modelComp.getAssociationInfo() != null) {
				String assocNameProperty = getPropertyGenerator().getPropertyNameFor(aClassInfo,modelComp.getAssociationInfo() );
				modelComp.addToContainer(tabpAssoc, getResourceLoader().getString(assocNameProperty));
			}
		}
	
		
		final JPanel mainPnl = new JPanel(new GridBagLayout());
		final GridBagConstraints 	pnlConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,1,1
				,1.0, 0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL
				, new Insets(5,0,0,5),0,0 );
		mainPnl.add(fieldPnl, pnlConstr);
		pnlConstr.weighty = 1.0; pnlConstr.fill =GridBagConstraints.BOTH;
		if( textAreaPnl.getComponentCount() >0 ) {
			mainPnl.add(textAreaPnl, pnlConstr);
		}

		pnlConstr.weighty = 1.0; pnlConstr.fill =GridBagConstraints.BOTH;
		if( aClassInfo.getAssociationInfoSize() > 0) {
			mainPnl.add(tabpAssoc, pnlConstr);
		} else {
			col1LblConstr.weighty = 1.0; col1LblConstr.fill = GridBagConstraints.VERTICAL;
			mainPnl.add(Box.createVerticalGlue(), pnlConstr);
		}
			
		col1LblConstr.weighty = 1.0; col1LblConstr.fill = GridBagConstraints.VERTICAL;
		gridPanl1.add(Box.createVerticalGlue(), col1LblConstr);
		gridPanl2.add(Box.createVerticalGlue(), col1LblConstr);
		
		String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(aClassInfo);
		mainPnl.setBorder(	BorderFactory.createEmptyBorder(4, 4, 4, 4));

		
		return mainPnl;
	}
	
	
}
