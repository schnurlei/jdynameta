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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;

import de.jdynameta.base.generation.DefaultPropertyNameCreator;
import de.jdynameta.base.metainfo.ClassInfo;
import de.jdynameta.base.metainfo.PrimitiveAttributeInfo;
import de.jdynameta.base.metainfo.primitive.VarCharType;
import de.jdynameta.base.value.JdyPersistentException;
import de.jdynameta.metainfoview.attribute.model.AttrInfoComponent;
import de.jdynameta.view.JdyResourceLoader;

public class MultiColumnLayoutStrategy extends ComponentLayoutStrategy
{

	public MultiColumnLayoutStrategy(JdyResourceLoader resourceLoader)
	{
		super(resourceLoader);
	}

	public JPanel createMainPanel(Collection<AttrInfoComponent> modelComponentColl, ClassInfo aClassInfo) throws JdyPersistentException
	{

		final GridBagConstraints 	col1LblConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE,1,1
									,0.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.NONE
									, new Insets(5,5,0,15),0,0 );
		final GridBagConstraints col1TxfConstr = new GridBagConstraints(1, GridBagConstraints.RELATIVE,1,1
									,1.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL
									, new Insets(5,5,0,15),0,0 );

		final GridBagConstraints 	col2LblConstr = new GridBagConstraints(2, GridBagConstraints.RELATIVE,1,1
									,0.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.NONE
									, new Insets(5,10,0,5),0,0 );
		final GridBagConstraints col2TxfConstr = new GridBagConstraints(3, GridBagConstraints.RELATIVE,1,1
									,1.0, 0.0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL
									, new Insets(5,0,0,5),0,0 );
		
		
		
		// create association tab panes
		JTabbedPane tabpAssoc = new JTabbedPane();
		tabpAssoc.setBorder( 	BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED)
				, BorderFactory.createEmptyBorder(4, 2, 4, 2)));
		JPanel fieldPnl = new JPanel(new GridBagLayout());
		JPanel textAreaPnl = new JPanel(new GridBagLayout());

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
					col1TxfConstr.weightx = 0.0; col1TxfConstr.fill = GridBagConstraints.HORIZONTAL;
				} else {
					GridBagConstraints lblContr = (fieldCnt % 2 == 0 )? col1LblConstr: col2LblConstr;
					GridBagConstraints fieldContr = (fieldCnt % 2 == 0 )? col1TxfConstr: col2TxfConstr;;
					String attrNameProperty = getPropertyGenerator().getPropertyNameFor(aClassInfo, modelComp.getAttributeInfo());
					modelComp.addLabelToContainer(getResourceLoader().getString(attrNameProperty), fieldPnl, lblContr);
					modelComp.addToContainer(fieldPnl, fieldContr);
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
			
		String classNameProperty = new DefaultPropertyNameCreator().getPropertyNameFor(aClassInfo);
		mainPnl.setBorder(	BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder(4, 4, 4, 4)
								, BorderFactory.createTitledBorder( getResourceLoader().getString(classNameProperty))));

		
		return mainPnl;
	}
	
	
}
