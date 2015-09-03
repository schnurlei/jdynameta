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
package de.jdynameta.view.panel;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.jdynameta.view.JdyResourceLoader;
import de.jdynameta.view.base.PanelManager;
import de.jdynameta.view.field.FixedSizeButton;


/**
 * A dialog with standard ok and cancel button. If the cancel button is pressed the underlying
 * SavePanel (see setPanel) will be asked if any component is dirty, means it must be saved.
 * If you call setAskForSave(true) then the user will be promptet if he really wants
 * to save his changed data. 
 * @author rs
 * @version 24.10.2002
 */
public class OkCancelSaveDialog extends JDialog 
{
	private JPanel mainPanel;
	private SavePanel savePanel;
	protected int result;
	protected final PanelManager panelManger;
	protected final SavePanel.SavePanelListener savePanelListener;
	
	private boolean askForSave = true;
	
	private Action okAction; // lazy initilized

	private final WindowListener windowListener = new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				try {
					cancelRequest();
					result = JOptionPane.CLOSED_OPTION;
					OkCancelSaveDialog.this.dispose();
				} catch (DeselectVetoException e1) {
					// do nothing
				}
			}
			@Override
			public void windowClosed(WindowEvent e)
			{
				OkCancelSaveDialog.this.removeWindowListener(this);
			}
		};

    public OkCancelSaveDialog(PanelManager aPanelManager, Dialog owner, String title, boolean modal) throws HeadlessException 
    {
        super(owner, title, modal);
        this.panelManger = aPanelManager;
		savePanelListener =  createSavePanelListener();
		askForSave = true;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowListener);
        createMainPanel();
        setPanel(new DefaultSavePanel());
    }

    public OkCancelSaveDialog(PanelManager aPanelManager, Frame owner, String title, boolean modal) throws HeadlessException 
    {
        super(owner, title, modal);     
        this.panelManger = aPanelManager;
		savePanelListener =  createSavePanelListener();
		askForSave = true;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowListener);
        createMainPanel();
        setPanel(new DefaultSavePanel());
    }

	protected  SavePanel.SavePanelListener createSavePanelListener()
	{
		return new SavePanel.SavePanelListener()
		{
			public void canCloseChanged() 
			{
				getOkAction().setEnabled(savePanel.canClose());
			}

			/* (non-Javadoc)
			 */
			public void isDirtyChanged()
			{
			}

		};				
	}

	/**
	 * Sets the panel for Dialog 
	 * @param aPanel see SavePanel 
	 */
	public void setPanel(SavePanel aPanel) 
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
		okAction.setEnabled(savePanel.canClose());

		mainPanel.add(savePanel, BorderLayout.CENTER);	
	}

	public int getResult() 
	{
		return result;
	}


	private void createMainPanel()
	{
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,0));
		btnPanel.add(new FixedSizeButton(getOkAction(), 100));
		btnPanel.add(new FixedSizeButton(createCancelAction(), 100));
		btnPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(btnPanel, BorderLayout.SOUTH);
		this.setContentPane(mainPanel);
	}

	private JdyResourceLoader getResources()
	{
		return this.panelManger.getResourceLoader();
	}
	
	private void closeRequest() throws DeselectVetoException
	{
		if ( savePanel.isDirty()) {

			int option =  JOptionPane.YES_OPTION; 
			if ( askForSave) {
				option = JOptionPane.showConfirmDialog(this,  getResources().getString("modul.saveQuestion"));
			}  
		
			if ( option == JOptionPane.YES_OPTION) {
				try {
					savePanel.save();
					savePanel.close();
				} catch (SaveException ex) {
					this.panelManger.displayErrorDialog( savePanel, ex.getLocalizedMessage(), ex);
					throw new DeselectVetoException(ex);
				} catch (CloseVetoException ex)
				{
					this.panelManger.displayErrorDialog( savePanel, ex.getLocalizedMessage(), ex);
					throw new DeselectVetoException(ex);
				}
				savePanel.closed();
			} else if (  option == JOptionPane.NO_OPTION) {
				savePanel.discardChanges();
				savePanel.closed();
			} else {
				throw new DeselectVetoException();
			}
		} else {
			savePanel.closed();
		}
	}


	private void cancelRequest() throws DeselectVetoException
	{
		if ( savePanel.isDirty()) {

			int option = JOptionPane.showConfirmDialog(this, getResources().getString("common.msg.cancelledSaveQuestion"), 
															 getResources().getString("common.action.cancel"), 
															 JOptionPane.YES_NO_OPTION);
	
			if ( option == JOptionPane.YES_OPTION) {
				savePanel.discardChanges();
				savePanel.closed();
			} else {
				throw new DeselectVetoException();
			}
		} else {
			savePanel.closed();
		}
	}

	private Action getOkAction() 
	{
		
		if ( okAction == null) {
		
			okAction = new AbstractAction(getResources().getString("common.action.ok")) 
			{
				public void actionPerformed(ActionEvent event)
				{
					try {
						closeRequest();
						result = JOptionPane.OK_OPTION;
						OkCancelSaveDialog.this.dispose();
					} catch (DeselectVetoException ex) {
						// do nothing
					}
				}
			};	
		}
		
		return okAction;
	}

	private Action createCancelAction() 
	{
		return new AbstractAction(getResources().getString("common.action.cancel")) 
		{
			public void actionPerformed(ActionEvent event)
			{
				try {
					cancelRequest();
					result = JOptionPane.CANCEL_OPTION;
					OkCancelSaveDialog.this.dispose();
				} catch (DeselectVetoException e) {
					// do nothing
				}
				
			}
		};	
	}


	public static OkCancelSaveDialog createDialog(PanelManager aPanelManager, Component parentComponent, String title, boolean modal) 
	{
		OkCancelSaveDialog dialog; 
    	Window window = OkCancelDialog.getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new OkCancelSaveDialog(aPanelManager, (Frame)window, title, modal);	
        } else {
            dialog = new OkCancelSaveDialog(aPanelManager, (Dialog)window, title, modal);
        }

		dialog.setResizable(true);
		dialog.setSize(600,300);
		dialog.setLocationRelativeTo(parentComponent);

		return dialog;	
	}
	

	/**
	 * Sets the askForSave on pressing ok button.
	 * @param aAskForSaveFlag The askForSave to set
	 */
	public void setAskForSave(boolean aAskForSaveFlag) 
	{
		askForSave = aAskForSaveFlag;
	}	
}
