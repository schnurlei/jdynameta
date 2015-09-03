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
package de.jdynameta.metainfoview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import de.jdynameta.base.objectlist.ChangeableObjectListModel;
import de.jdynameta.metainfoview.metainfo.SwingSelectedListModelMapping;
import de.jdynameta.view.panel.OkPanel;

/**
 * Select from some values from a list of values 
 * @author Rainer Schneider
 *
 * @param <TSelectablObj>
 */
@SuppressWarnings("serial")
public class SelectionPanel<TSelectablObj> extends OkPanel 
{
	private SwingSelectedListModelMapping leftCol;
	private SwingSelectedListModelMapping rightCol;
	private ChangeableObjectListModel<TSelectablObj> leftObjects;
	private ChangeableObjectListModel<TSelectablObj> rightObjects;
	
	public SelectionPanel(List<TSelectablObj> sourceList, List<TSelectablObj> targetList) 
	{
		leftObjects = new ChangeableObjectListModel<TSelectablObj>(sourceList);
		rightObjects = new ChangeableObjectListModel<TSelectablObj>(targetList);
		
		leftCol = new SwingSelectedListModelMapping(leftObjects);
		rightCol = new SwingSelectedListModelMapping(rightObjects);
		initUi();
	}
	
	public List<TSelectablObj> getSelectedObjects()
	{
		return rightObjects.getAllObjectsAsArray();
	}
	
	
	private void initUi()
	{
		JList  leftLst = new JList(leftCol);
		leftLst.setSelectionModel(leftCol.getSelectionModel());
		leftLst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JList rightLst = new JList(rightCol);
		rightLst.setSelectionModel(rightCol.getSelectionModel());
		rightLst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JPanel buttonPnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints btnConstr = new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1,1
															, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE
															, new Insets(10,10,10,10), 0, 0);
		buttonPnl.add(new JButton(createMoveAllRightAction()), btnConstr);
		buttonPnl.add(new JButton( createMoveAllLeftAction()), btnConstr);
		buttonPnl.add(new JButton(createMoveRightAction()), btnConstr);
		buttonPnl.add(new JButton(createMoveLeftAction()), btnConstr);
		
		GridBagConstraints pnlConstr = new GridBagConstraints(GridBagConstraints.RELATIVE,0,  1,1
				, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH
				, new Insets(10,10,10,10), 0, 0);
		
		this.setLayout(new GridBagLayout());
		this.add(new JScrollPane(leftLst), pnlConstr);
		pnlConstr.fill = GridBagConstraints.VERTICAL;
		pnlConstr.weightx = 0.0;
		this.add(buttonPnl, pnlConstr);
		pnlConstr.fill = GridBagConstraints.BOTH;
		pnlConstr.weightx = 1.0;
		this.add(new JScrollPane(rightLst), pnlConstr);
		
	}
	
	private void moveObjectsRight(List<TSelectablObj> list) 
	{
		for (TSelectablObj object : list) {
			leftObjects.removeFromListContent( object);
			rightObjects.addToListContent( object);
		}
	}

	private void moveObjectsLeft(List<TSelectablObj> list) 
	{
		for (TSelectablObj object : list) {
			rightObjects.removeFromListContent( object);
			leftObjects.addToListContent( object);
		}
	}
	
	private Action createMoveRightAction()
	{
		return new AbstractAction(">") 
		{
			public void actionPerformed(ActionEvent e) 
			{
				List<TSelectablObj> allObjects = new ArrayList<TSelectablObj>();
				for (Object anObject : leftCol.getAllSelectedObjects()) {
					allObjects.add((TSelectablObj) anObject);
				}
				moveObjectsRight(allObjects);
			}

		};
	}
	
	
	private Action createMoveLeftAction()
	{
		return new AbstractAction("<") 
		{
			public void actionPerformed(ActionEvent e) 
			{
				List<TSelectablObj> allObjects = new ArrayList<TSelectablObj>();
				for (Object anObject : rightCol.getAllSelectedObjects()) {
					allObjects.add((TSelectablObj) anObject);
				}
				moveObjectsLeft(allObjects);
			}
		};
	}

	private Action createMoveAllRightAction()
	{
		return new AbstractAction(">>") 
		{
			public void actionPerformed(ActionEvent e) 
			{
				moveObjectsRight(leftObjects.getAllObjectsAsArray());
				
			}
		};
	}

	private Action createMoveAllLeftAction()
	{
		return new AbstractAction("<<") 
		{
			public void actionPerformed(ActionEvent e) 
			{
				moveObjectsLeft(rightObjects.getAllObjectsAsArray());
			}
		};
	}
	
}
