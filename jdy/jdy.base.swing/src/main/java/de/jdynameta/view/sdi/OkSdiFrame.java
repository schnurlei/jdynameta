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
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;
import de.jdynameta.view.panel.DefaultOkPanel;
import de.jdynameta.view.panel.OkPanel;


/**
 * Frame which handles a <@link de.comafra.view.base.OkPanel />
 * 
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
@SuppressWarnings("serial")
public class OkSdiFrame extends AbstractSdiFrame
{
	private Action closeAction;
	private OkPanel okPanel;
	protected OkPanel.OkPanelListener okPanelListener;
	private final ActionDecorator closeActionDecorator;	
	
	/**
	 * @param aCloseAtionDecorator 
	 * @throws java.awt.HeadlessException
	 */
	public OkSdiFrame(PanelManager aPanelManager, final ActionDecorator aCloseActionDecorator) throws HeadlessException
	{
		super(aPanelManager);

		this.closeActionDecorator = aCloseActionDecorator;
		this.okPanelListener = createOkPanelListener();
		this.closeAction = createCloseAction(aCloseActionDecorator);
	}
	
	public void addCloseAction()
	{
		if( closeActionDecorator != null) {
			if( closeActionDecorator.isShowInActionBar()) {
				this.addActionsToActionBarRight(closeAction, true);
			}
			
			if(  closeActionDecorator.isShowInMenubar() ) {
				this.addActionToMenuBar(closeAction, closeActionDecorator.getMenuBarPath(), false);
			}

			if(  closeActionDecorator.isShowInToolbar() ) {
				this.addActionToToolbar(closeAction, false);
			}
			
		} else {
			this.addActionsToActionBarRight(closeAction, true);
			
		}
	}
	

	protected  OkPanel.OkPanelListener createOkPanelListener()
	{
		return new OkPanel.OkPanelListener()
		{
			public void canCloseChanged() 
			{
				closeAction.setEnabled(okPanel.canClose());
			}
		};		
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
				} catch (CloseVetoException e) {
					// ignore, because the thrower of the exception is responsible for the user nofitication
				}
			}
		};	

		if( closeActionDecorator != null && closeActionDecorator.getWrappedAction() != null ) {
			Object value = closeActionDecorator.getWrappedAction().getValue(Action.NAME);
			if( value != null) {
				closeAction.putValue(Action.NAME, value);
			}
		}
		
		
		return closeAction;
	}
	
	/**
	 * @throws CloseVetoException 
	 *
	 */
	protected void closeFrame() throws CloseVetoException 
	{
		this.okPanel.close();
		if( closeActionDecorator != null && closeActionDecorator.getWrappedAction() != null ) {
			closeActionDecorator.getWrappedAction().actionPerformed(null);
		}
		this.okPanel.closed();
		super.closeFrame();
	}

	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(OkPanel aPanel) 
	{

		if ( okPanel != null) {
			okPanel.setOkPanelListener(null);
		}
		
		if( aPanel != null) {
			okPanel = aPanel;
		} else {
			okPanel = new DefaultOkPanel();
		}	

		okPanel.setOkPanelListener(okPanelListener);
		closeAction.setEnabled(okPanel.canClose());
			
		this.okPanel = aPanel;
		super.displayPanel(aPanel);
	}
	
	
}
