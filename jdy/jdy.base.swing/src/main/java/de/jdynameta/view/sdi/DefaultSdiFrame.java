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
package de.jdynameta.view.sdi;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.base.ManagedPanel;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;


/**
 * Frame which handles a <@link de.comafra.view.base.ManagedPanel />
 * With Menubar, Toolbar and Statusline
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
@SuppressWarnings("serial")
public class DefaultSdiFrame extends AbstractSdiFrame
{
	private Action closeAction;
	private ManagedPanel managedPnl;
	private final  ActionDecorator closeActionDecorator;
	
	/**
	 * @param aCloseAtionDecorator 
	 * @throws java.awt.HeadlessException
	 */
	public DefaultSdiFrame(PanelManager aPanelManager, final ActionDecorator aCloseActionDecorator) throws HeadlessException
	{
		super(aPanelManager);
		this.closeActionDecorator = aCloseActionDecorator;
		
		this.closeAction = createCloseAction(aCloseActionDecorator);
	}

	public void addCloseAction()
	{
		if( this.closeActionDecorator != null) {
			if( this.closeActionDecorator.isShowInActionBar()) {
				this.addActionsToActionBarRight(closeAction, true);
			}
			
			if(  this.closeActionDecorator.isShowInMenubar() ) {
				this.addActionToMenuBar(closeAction, this.closeActionDecorator.getMenuBarPath(), this.closeActionDecorator.isSeperator());
			}

			if(  this.closeActionDecorator.isShowInToolbar() ) {
				this.addActionToToolbar(closeAction, this.closeActionDecorator.isSeperator());
			}
			
		} else {
			this.addActionsToActionBarRight(closeAction, true);
		}
		
	}
	
	protected Action createCloseAction(final ActionDecorator closeActionDecorator) 
	{
		String actionName = getResourceLoader().getString("common.action.close");
		
		closeAction = new AbstractAction(actionName) 
		{
			public void actionPerformed(ActionEvent event)
			{
				try {
					closeAllFrames();
				} catch (CloseVetoException ex) {
					// ignore, because the thrower of the exception is responsible for the user nofitication
				}
			}
		};	

		if( closeActionDecorator != null) {
			if( closeActionDecorator.getWrappedAction() != null ) {
				Object value = closeActionDecorator.getWrappedAction().getValue(Action.NAME);
				if( value != null) {
					closeAction.putValue(Action.NAME, value);
				}
			}
		}
		
		
		return closeAction;
	}
	
	/**
	 *
	 */
	protected void closeFrame() throws CloseVetoException
	{
		this.managedPnl.closed();
		if( closeActionDecorator != null && closeActionDecorator.getWrappedAction() != null) {
			closeActionDecorator.getWrappedAction().actionPerformed(null);
		}
		super.closeFrame();
	}

	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(ManagedPanel aPanel) 
	{
		this.managedPnl = aPanel;
		super.displayPanel(aPanel);
	}
	
	
}
