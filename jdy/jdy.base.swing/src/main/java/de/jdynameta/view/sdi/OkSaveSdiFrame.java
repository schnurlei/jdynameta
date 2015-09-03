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
import javax.swing.JOptionPane;

import de.jdynameta.view.action.ActionDecorator;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.panel.CloseVetoException;
import de.jdynameta.view.panel.DefaultSavePanel;
import de.jdynameta.view.panel.DeselectVetoException;
import de.jdynameta.view.panel.SaveException;
import de.jdynameta.view.panel.SavePanel;


/**
 * Frame which handles a <@link de.comafra.view.base.ManagedPanel />
 * With Menubar, Toolbar and Statusline
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
@SuppressWarnings("serial")
public class OkSaveSdiFrame extends AbstractSdiFrame
{
	protected final SavePanel.SavePanelListener savePanelListener;
	private boolean askForSave = false;
	private Action okAction; // lazy initilized
	private Action cancelAction; // lazy initilized
	private SavePanel savePanel;
	private final ActionDecorator closeActionDecorator;	
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public OkSaveSdiFrame(PanelManager aPanelManager, final ActionDecorator aCloseActionDecorator) throws HeadlessException
	{
		super(aPanelManager);
		this.savePanelListener =  createSavePanelListener();
		this.closeActionDecorator = aCloseActionDecorator;
	}

	public void addCloseAction()
	{
		// add actions not in the constructor, because the actionBar could be changed
		this.addActionsToActionBarRight(getOkAction(), false);
		if( closeActionDecorator != null) {
			if( closeActionDecorator.isShowInActionBar()) {
				this.addActionsToActionBarRight(getCancelAction() , true);
			}
			
			if(  closeActionDecorator.isShowInMenubar() ) {
				this.addActionToMenuBar(getCancelAction() , closeActionDecorator.getMenuBarPath(), false);
			}

			if(  closeActionDecorator.isShowInToolbar() ) {
				this.addActionToToolbar(getCancelAction() , false);
			}
			
		} else {
			this.addActionsToActionBarRight(getCancelAction() , true);
		}
	}
	
	/**
	 * Display the Base Panel in a Frame
	 * @param aPanale the to show
	 */
	public void displayPanel(SavePanel aPanel) 
	{
		
		if ( savePanel != null) {
			savePanel.setOkPanelListener(null);
		}
		
		if( aPanel != null) {
			savePanel = aPanel;
		} else {
			savePanel = new DefaultSavePanel();
		}	

		savePanel.setOkPanelListener(savePanelListener);
		okAction.setEnabled(true); //savePanel.canClose());

		super.displayPanel(aPanel);
	}

	
	protected  SavePanel.SavePanelListener createSavePanelListener()
	{
		return new SavePanel.SavePanelListener()
		{
			public void canCloseChanged() 
			{
//				getOkAction().setEnabled(savePanel.canClose());
			}

			/* (non-Javadoc)
			 */
			public void isDirtyChanged()
			{
			}

		};				
	}
	
	
	private Action getOkAction() 
	{
		
		if ( okAction == null) {
		
			okAction = new AbstractAction(getResourceLoader().getString("common.action.ok")) 
			{
				public void actionPerformed(ActionEvent event)
				{
					try {
						closeRequest();
					} catch (DeselectVetoException ex) {
						// do nothing
					} catch (CloseVetoException e) {
						// do nothing
					}
				}
			};	
		}
		
		return okAction;
	}

	private Action getCancelAction() 
	{
		if ( cancelAction == null) {
			cancelAction = new AbstractAction(getResourceLoader().getString("common.action.cancel")) 
			{
				public void actionPerformed(ActionEvent event)
				{
					try {
						closeAllFrames();
					} catch (CloseVetoException e) {
						// do nothing
					}
					
				}
			};
			if( closeActionDecorator != null && closeActionDecorator.getWrappedAction() != null ) {
				Object value = closeActionDecorator.getWrappedAction().getValue(Action.NAME);
				if( value != null) {
					cancelAction.putValue(Action.NAME, value);
				}
			}

		}	
		
		return cancelAction;
	}
	
	private void closeRequest() throws CloseVetoException
	{
		// Save edited fields
		if ( savePanel.isDirty()) {

			int option =  JOptionPane.YES_OPTION; 
			if ( askForSave) {
				option = JOptionPane.showConfirmDialog(this,  getResourceLoader().getString("modul.saveQuestion"));
			}
		
			if ( option == JOptionPane.YES_OPTION) {
				try {
					savePanel.save();
				} catch (SaveException ex) {
					getPanelManager().displayErrorDialog( savePanel, ex.getLocalizedMessage(), ex);
					throw new CloseVetoException(ex);
				}
			} else if (  option == JOptionPane.NO_OPTION) {
				savePanel.discardChanges();	
			} else {
				
				throw new CloseVetoException();
			}
		}
		
		
		try
		{
			closeAllFrames();

		} catch (CloseVetoException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage()); //TODO
		}
		
	}

	/**
	 *
	 */
	public void closeFrame() throws CloseVetoException
	{
		if ( savePanel.isDirty()) {

			int option = JOptionPane.showConfirmDialog(this, getResourceLoader().getString("modul.cancelledSaveQuestion"), 
															getResourceLoader().getString("common.action.cancel"), 
															 JOptionPane.YES_NO_OPTION);
	
			if ( option != JOptionPane.YES_OPTION) {
				throw new CloseVetoException();
			}
		} 		
		savePanel.closed();
		if( closeActionDecorator != null && closeActionDecorator.getWrappedAction() != null ) {
			closeActionDecorator.getWrappedAction().actionPerformed(null);
		}
		super.closeFrame();
	}

	
}
