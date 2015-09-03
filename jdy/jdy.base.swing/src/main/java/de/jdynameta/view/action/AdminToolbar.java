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


import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;


/**
 * Wrapper for JToolbar to add actions and add it to a Frame 
 * @author rs
 */
public class AdminToolbar 
{
	private JToolBar toolBar; 

	public AdminToolbar() 
	{
		this.toolBar = createToolbar();
	}
	
	public void addAction(final Action actionToAdd, boolean isSeperator) 
	{
		final AbstractButton button;
		
		if( !isSeperator ) {
			boolean isToggleBtn = actionToAdd.getValue(Action.SELECTED_KEY) != null;
			if( isToggleBtn) {
				button = this.toolBar.add(actionToAdd);
			} else {
				button = this.toolBar.add(actionToAdd);
			}
			
			final Color oldBackground = button.getBackground();
			
			actionToAdd.addPropertyChangeListener( new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(AdminActionBar.BTN_PROPERTY_HIGHLIGHT)) {
						if ( evt.getNewValue().equals("true") ) {
							button.setBackground(Color.RED);
	
						} else {
							button.setBackground(oldBackground);
						}
					}
				}
			});
 		
			
		} else {
			this.toolBar.addSeparator();
		}
	}

	public void addSeparator() 
	{
		
		this.toolBar.addSeparator();
	}


	protected JToolBar createToolbar()
	{
		JToolBar tmpToolBar = new JToolBar();
		tmpToolBar.setOrientation(SwingConstants.HORIZONTAL);
		tmpToolBar.setFloatable(false);
		return tmpToolBar;
	}
	
	public void addToFrame(JPanel aPanel, Object aConstraint) 
	{
		aPanel.add(toolBar, aConstraint);
		
	}

	public void removeFromFrame(JPanel aPanel) 
	{
		aPanel.remove(toolBar);
		
	}
	
}
