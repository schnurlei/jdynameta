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
package de.jdynameta.view.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class AdminActionBar 
{
	public static final String BTN_PROPERTY_HIGHLIGHT = "Highlight";
	public static final String BTN_PROPERTY_BTN_GROUP = "ActionButtonGroup";

	private final JPanel actionBarPnl;
	private final JPanel actionBarRightPnl;
	private final JPanel mainPnl;

	public AdminActionBar() 
	{
		this.actionBarPnl = new JPanel();
		this.actionBarRightPnl = new JPanel();
		this.mainPnl = createActionBarPanel();
	}

	private JPanel createActionBarPanel()
	{
		actionBarRightPnl.setLayout((new GridLayout(1,4, 10, 10))); // gridLayout to ensure same size of buttons
		actionBarPnl.setLayout((new GridLayout(1,4, 10, 10))); // gridLayout to ensure same size of buttons
        JPanel pnlButtonsDecorator = new JPanel(new GridBagLayout()); // Gridbag to place it in the middle, ensure size is not wider the necessary
        pnlButtonsDecorator.add(actionBarPnl, new GridBagConstraints(0, 0, 1, 1
    			,0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE
    			, new Insets(10,0,10,0), 0,0 ));
        JPanel pnlButtonsDecoratorRight = new JPanel(new GridBagLayout()); // Gridbag to place it in the middle, ensure size is not wider the necessary
        pnlButtonsDecoratorRight.add(actionBarRightPnl, new GridBagConstraints(0, 0, 1, 1
    			,1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL
    			, new Insets(10,0,10,0), 0,0 ));

        JPanel buttonPnl = new JPanel(new BorderLayout());
		buttonPnl.add(pnlButtonsDecorator, BorderLayout.LINE_START);
		buttonPnl.add(pnlButtonsDecoratorRight, BorderLayout.LINE_END);
		
		buttonPnl.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		return buttonPnl;
		
	}
	
	protected JPanel getActionBarPnl() 
	{
		return actionBarPnl;
	}
	
	public AbstractButton addActionsToActionBar(Action aNewAction, boolean isSeperator)
	{
		final AbstractButton newButton;
		if( !isSeperator) 
		{
			boolean isToggleBtn = aNewAction.getValue(Action.SELECTED_KEY) != null;
			if( isToggleBtn ) {
		        newButton = new JToggleButton(aNewAction);
 			} else {
		        newButton = new JButton(aNewAction);
			}
	        newButton.setName("actionbar."+(String) aNewAction.getValue(Action.NAME));
	        newButton.getAccessibleContext().setAccessibleName("actionbar." + (String)aNewAction.getValue(Action.NAME));
			this.actionBarPnl.add(newButton);
			
			
	        aNewAction.addPropertyChangeListener( new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(BTN_PROPERTY_HIGHLIGHT)) {
						if ( evt.getNewValue().equals("true") ) {
							newButton.setForeground(Color.RED);
	
						} else {
							newButton.setForeground(Color.BLACK);
						}
					}
				}
			});
	        
			getActionBarPnl().add(newButton);
		} else {
			newButton = null;
//			getActionBarPnl().add(new JToolBar.Separator( null ));
		}
		
		
		return newButton;
	}

	public JButton addActionsToActionBarRight(Action aNewAction)
	{
        final JButton newButton = new JButton(aNewAction);
        newButton.setName("actionbar."+(String) aNewAction.getValue(Action.NAME));
        this.actionBarRightPnl.add(newButton);
		return newButton;
	}

	public void removeFromPanel(JPanel aPnl) 
	{
		aPnl.remove(this.mainPnl);
	}

	public void addToPanel(JPanel aPnl, String aConstraint) 
	{
		aPnl.add(this.mainPnl, aConstraint);
	}
	
}
